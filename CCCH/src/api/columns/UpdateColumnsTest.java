package api.columns;

import java.io.IOException;
/**
 * @author Nagamalleswari.Sykam
 * We are Get columns for the all type of column in  this class
 * We are covered many scenarios for all roles 
 * Update columns for all user roles
 * Update columns for the user if the user have already columns
 * Insufficient scenarios 
 * Invalid and Missed token 
 * update columns for with invalid user columns id
 * update columns for the random uuid as user id
 * update columns for user with the invalid user column id
 * update columns for user with the random user column id 
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

public class UpdateColumnsTest  extends base.prepare.Is4Org{

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
	String[] visible = new String[] { "true", "false" };

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

		Nooftest = 0;
		// Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);
		ti = new TestOrgInfo(spogServer, test);
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

	@DataProvider(name = "update_columns")
	public final Object[][] updatecoloumnsValidParams() {

		return new Object[][] {

			//alert email recipients
			{ "update alert_email_recipient columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST ,new String[] { "3", "1", "2", "4", "5" }, 4,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update alert_email_recipient columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "2", "1", "5", "3", "4" }, 4,SpogConstants.SUCCESS_POST, new String[] { "5", "1", "5", "3", "4" }, 2,SpogConstants.SUCCESS_GET_PUT_DELETE  },
			{ "update alert_email_recipient columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST , new String[] { "3", "2", "5" }, 3,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update alert_email_recipient columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.ALERT_EMAIL_RECIPIENT.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST,new String[] { "3", "1", "3", "4", "5" }, 1, SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update alert_email_recipient columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "2", "1", "5", "4", "2" }, 5,SpogConstants.SUCCESS_POST ,new String[] { "4", "6", "5", "4", "2" }, 3,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update alert_email_recipient columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "2", "1", "3", "4", "5" }, 2,SpogConstants.SUCCESS_POST, new String[] { "5", "4", "3", "1", "2" }, 5,SpogConstants.SUCCESS_GET_PUT_DELETE},
			{ "update alert_email_recipient columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST ,new String[] { "2", "5", "3", "4", "5" }, 3,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update alert_email_recipient columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST, new String[] { "5", "3", "1", "4" }, 1,SpogConstants.SUCCESS_GET_PUT_DELETE},
			{ "update alert_email_recipient columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "2", "1", "3", "4", "5" }, 5,SpogConstants.SUCCESS_POST, new String[] { "5", "2", "1", "4", "5" }, 3,SpogConstants.SUCCESS_GET_PUT_DELETE },

			// CSR user Scenario
			{ "update  alert_email_recipient columns for the direct organization with the csr user token ",ti.csr_token, ti.msp1_submsp1_suborg1_user1_id, ColumnType.ALERT_EMAIL_RECIPIENT.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST,new String[] { "3", "2", "5", "4", "6" }, 2,SpogConstants.SUCCESS_GET_PUT_DELETE},

			// Hypervisor columns
			{ "update hyperviosr columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.HYPERVISOR.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST,new String[] { "3", "1", "5", "2", "6" }, 4,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update hyperviosr columns for the  normal msp  organization", ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ColumnType.HYPERVISOR.toString(),new String[] { "2", "1", "3", "4", "5" }, 3, SpogConstants.SUCCESS_POST,new String[] { "6", "1", "3", "4", "5" }, 1,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update  hyperviosr columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.HYPERVISOR.toString(), new String[] { "2", "1", "5", "3", "4" }, 4,SpogConstants.SUCCESS_POST,new String[] { "2", "3", "3", "4", "5" }, 2,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update  hyperviosr columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.HYPERVISOR.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update  hyperviosr columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.HYPERVISOR.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST,new String[] { "5", "3", "3", "4", "5" }, 3,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update  hyperviosr columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.HYPERVISOR.toString(), new String[] { "2", "1", "5", "4", "2" }, 5,SpogConstants.SUCCESS_POST,new String[] { "6", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update  hyperviosr columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.HYPERVISOR.toString(), new String[] { "2", "1", "3", "4", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "5", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update  hyperviosr columns for the sub msp organization", ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, ColumnType.HYPERVISOR.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST,new String[] { "3", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update hyperviosr columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.HYPERVISOR.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST,new String[] { "5", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update  hyperviosr columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.HYPERVISOR.toString(), new String[] { "2", "1", "3", "4", "5" }, 5,SpogConstants.SUCCESS_POST,new String[] { "6", "2", "3", "4", "5" }, 3,SpogConstants.SUCCESS_GET_PUT_DELETE },

			// CSR user Scenario
			{ "update  hyperviosr columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.HYPERVISOR.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "5", "1", "4", "6" }, 4,SpogConstants.SUCCESS_GET_PUT_DELETE },

			//Recovered resources columns
			{ "update recovered_resources columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.RECOVEREDRESOURCE.toString(),new String[] { "2", "1", "5", "4", "3","6","7","8","9","10","11"}, 11, SpogConstants.SUCCESS_POST,new String[] { "11", "10", "5", "4", "3","6","7","8","9","10","11"}, 3,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update recovered_resources columns for the  normal msp  organization", ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ColumnType.RECOVEREDRESOURCE.toString(),new String[] { "4", "3","6","7","8","9"}, 5, SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","6","7","8","9","10","11"}, 11,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update  recovered_resources columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] { "3","6","7","8","9","10","11" }, 4,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","6","7","8","9","10","11"}, 11,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update  recovered_resources columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] { "9","10","11"}, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","6","7","8","9","10","11"}, 11,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update  recovered_resources columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.RECOVEREDRESOURCE.toString(),new String[] { "3","10","11","6","7","8","9"}, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","6","7","8","9","10","11"}, 11,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update  recovered_resources columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] {  "2", "1", "5", "3","7","8","10","11"}, 5,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","6","7","8","9","10","11"}, 11,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update  recovered_resources columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] { "2", "1", "5", "4", "3","6","7","8","9","10","11" },7,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","6","7","8","9","10","11"}, 11,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update  recovered_resources columns for the sub msp organization", ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, ColumnType.RECOVEREDRESOURCE.toString(),new String[] {  "2", "1", "5", "4", "3","6","7","9","8","11","10",}, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","6","7","8","9","10","11"}, 11,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update recovered_resources columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] {  "3","6","7","8", }, 3,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","6","7","8","9","10","11"}, 11,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update  recovered_resources columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] {  "3", "7", "9","8","11" }, 5,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","6","7","8","9","10","11"}, 11,SpogConstants.SUCCESS_GET_PUT_DELETE },

			// CSR user Scenario
			{ "update  recovered_resources columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.RECOVEREDRESOURCE.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "4", "1", "4", "6" }, 4, SpogConstants.SUCCESS_GET_PUT_DELETE },

			//Policy columns
			{ "update policy columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.POLICY.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update policy columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.POLICY.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update policy columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.POLICY.toString(), new String[] { "2", "1", "5", "3", "4" }, 4,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update policy columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.POLICY.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update policy columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.POLICY.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update policy columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.POLICY.toString(), new String[] { "2", "1", "5", "4", "2" }, 5,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update policy columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.POLICY.toString(), new String[] { "2", "1", "3", "4", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update policy columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.POLICY.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update policy columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.POLICY.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update policy columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.POLICY.toString(), new String[] { "2", "1","7","6", "3", "4", "5" },7,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },


			//CSR user 
			{ "update  policy columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.POLICY.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },


			//Log columns
			{ "update log columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.LOG.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update log columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.LOG.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update log columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.LOG.toString(), new String[] { "2", "1", "5", "3", "4" }, 4,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update log columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.LOG.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update log columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.LOG.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update log columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.LOG.toString(), new String[] { "2", "1", "5", "4", "2" }, 5,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update log columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.LOG.toString(), new String[] { "2", "1", "3", "4", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update log columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.LOG.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update log columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.LOG.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update log columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.LOG.toString(), new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_POST,new String[] { "5", "1","6","7", "3", "4", "2" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },


			//Csr 
			{ "update  log columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.LOG.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },

			//User Columns
			{ "update user columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.USER.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update user columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.USER.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update user columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.USER.toString(), new String[] { "2", "1", "5", "3", "4" }, 4,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update user columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.USER.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update user columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.USER.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update user columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.USER.toString(), new String[] { "2", "1", "5", "4", "2" }, 5,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update user columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.USER.toString(), new String[] { "2", "1", "3", "4", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update user columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.USER.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update user columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.USER.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update user columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.USER.toString(), new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_POST,new String[] { "6", "1","2","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },


			//Csr 
			{ "update  user columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.USER.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5" },7,SpogConstants.SUCCESS_GET_PUT_DELETE },


			//Source columns 
			{ "update source columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.SOURCE.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update source columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.SOURCE.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update source columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.SOURCE.toString(), new String[] { "2", "1", "5", "3", "4","8","12","10","7" }, 9,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update source columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.SOURCE.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update source columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.SOURCE.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update source columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.SOURCE.toString(), new String[] { "2", "1", "5", "4", "2" }, 5,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update source columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.SOURCE.toString(), new String[] { "2", "1", "3", "4", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update source columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.SOURCE.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update source columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.SOURCE.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update source columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.SOURCE.toString(), new String[] { "2", "1","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },


			//Csr 
			{ "update  source columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.SOURCE.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },

			//Recovery job report columns 
			{ "update recovery_job_report columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.RESTORE_JOB_REPORT.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update recovery_job_report columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update recovery_job_report columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "2", "1", "5", "3", "4","8","12","10","7" }, 9,SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update recovery_job_report columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update recovery_job_report columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.RESTORE_JOB_REPORT.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update recovery_job_report columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "2", "1", "5", "4", "2" }, 5,SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update recovery_job_report columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "2", "1", "3", "4", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update recovery_job_report columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update recovery_job_report columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update recovery_job_report columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "2", "1","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },


			//Csr 
			{ "update  recovery_job_report columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.RESTORE_JOB_REPORT.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },

			//Backup job report columns 
			{ "update backup_job_report columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.BACKUP_JOB_REPORT.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update backup_job_report columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update backup_job_report columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "2", "1", "5", "3", "4","8","12","10","7","13" }, 9,SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update backup_job_report columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update backup_job_report columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.BACKUP_JOB_REPORT.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update backup_job_report columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "2", "1", "12", "4", "2" }, 5,SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update backup_job_report columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "2", "13", "12", "4", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update backup_job_report columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update backup_job_report columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update backup_job_report columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "2", "1","6","13", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },


			//Csr 
			{ "update  backup_job_report columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.BACKUP_JOB_REPORT.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST,new String[] { "1", "2","6","7", "3", "4", "5","9","11","8","10","12" },12,SpogConstants.SUCCESS_GET_PUT_DELETE },

			//Job columns 
			{ "update job columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.JOB.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update job columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.JOB.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update job columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.JOB.toString(), new String[] { "2", "1", "5", "3", "4","8","9","7"}, 6,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update job columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.JOB.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update job columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.JOB.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update job columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.JOB.toString(), new String[] { "2", "1", "7", "4", "2" }, 5,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update job columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.JOB.toString(), new String[] { "2", "8", "9", "4", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update job columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.JOB.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update job columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.JOB.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update job columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.JOB.toString(), new String[] { "2", "1","6","3", "4", "5","9","8",},5,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },

			//Csr 
			{ "update  job columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.JOB.toString(),new String[] { "2", "1", "5", "4", "6" }, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },

			//Job columns 
			{ "update job columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.JOB.toString(),new String[] { "2", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update job columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.JOB.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update job columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.JOB.toString(), new String[] { "2", "1", "5", "3", "4","8","9","7"}, 6,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update job columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.JOB.toString(), new String[] { "3", "2", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update job columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.JOB.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update job columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.JOB.toString(), new String[] { "2", "1", "7", "4", "2" }, 5,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update job columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.JOB.toString(), new String[] { "2", "8", "9", "4", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update job columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.JOB.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update job columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.JOB.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update job columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.JOB.toString(), new String[] { "2", "1","6","3", "4", "5","9","8",},5,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },

			//POLICY_TASK_REPORT columns 
			{ "update policy_task_report  columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.POLICY_TASK_REPORT.toString(),new String[] { "10", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","3", "4", "5","9","8",},5,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update policy_task_report  columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "11", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","3", "4", "5","9","8",},5,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update policy_task_report  columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "2", "1", "5", "3", "4","8","9","7"}, 6,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","3", "4", "5","9","8",},5,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update policy_task_report  columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "3", "10", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","3", "4", "5","9","8",},5,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update policy_task_report  columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.POLICY_TASK_REPORT.toString(),new String[] { "2", "1", "11", "4", "5" }, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","3", "4", "5","9","8",},5,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update policy_task_report  columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "2", "1", "7", "4", "2" }, 5,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","3", "4", "5","9","8",},5,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update policy_task_report  columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "2", "10", "9", "4", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","3", "4", "5","9","8",},5,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update policy_task_report  columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "2", "1", "3", "11", "5" }, 4,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","3", "4", "5","9","8",},5,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update policy_task_report  columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","3", "4", "5","9","8",},5,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update policy_task_report  columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "2", "1","6","3", "4", "5","9","8",},5,SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","3", "5", "4","9","8",},5,SpogConstants.SUCCESS_GET_PUT_DELETE },


			//Csr 
			{ "update  policy_task_report  columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.POLICY_TASK_REPORT.toString(),new String[] { "2", "1", "5", "10", "6" }, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "1","6","3", "4", "5","9","8",},5,SpogConstants.SUCCESS_GET_PUT_DELETE },


			//Destiantion column
			{ "update destination columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.DESTINATION.toString(),new String[] { "10", "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update destination columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.DESTINATION.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update destination columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.DESTINATION.toString(), new String[] { "2", "1", "5", "3", "4","8","9","7"}, 6,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update destination columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.DESTINATION.toString(), new String[] { "3", "10", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update destination columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.DESTINATION.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update destination columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.DESTINATION.toString(), new String[] { "2", "1", "7", "4", "2" }, 5,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update destination columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.DESTINATION.toString(), new String[] { "2", "10", "9", "4", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update destination columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.DESTINATION.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update destination columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.DESTINATION.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update destination columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.DESTINATION.toString(), new String[] { "2", "1","6","3", "4", "5","9","8",},5,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },


			//Csr 
			{ "update  destination columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.DESTINATION.toString(),new String[] { "2", "1", "5", "10", "6" }, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },

			
			//report column
			{ "update report columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.REPORT.toString(),new String[] { "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6",}, 2,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update report columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.REPORT.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "4", "3","7", },3,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update report columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.REPORT.toString(), new String[] { "2", "1", "5", "3", "4","7"}, 6,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 2,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update report columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.REPORT.toString(), new String[] { "3","5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4",  }, 2,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update report columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.REPORT.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3"}, 2,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update report columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.REPORT.toString(), new String[] { "2", "1", "7", "4", "2" }, 5,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", }, 2,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update report columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.REPORT.toString(), new String[] { "2","9", "4", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4",  }, 2,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update report columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.REPORT.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" },2,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update report columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.REPORT.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5",  }, 2,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update report columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.REPORT.toString(), new String[] { "2", "1","6","3", "4", "5",},5,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "4", "3","7", }, 2,SpogConstants.SUCCESS_GET_PUT_DELETE },


			//Csr 
			{ "update  report columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.REPORT.toString(),new String[] { "2", "1", "5", "10", "6" }, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3","7","6","9","8" }, 8,SpogConstants.SUCCESS_GET_PUT_DELETE },

			
			//ManagedReportSchedules column
			{ "update ManagedReportSchedules columns for the direct organization", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.REPORT_SCHEDULE.toString(),new String[] {  "1", "5", "4", "3" }, 2, SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3",}, 2,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update ManagedReportSchedules columns for the  normal msp  organization",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "2", "1", "3", "4", "5" }, 3,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", },2,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update ManagedReportSchedules columns for the normal msp account admin ",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "2", "1", "5", "3", "4","8","9","7"}, 6,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3"}, 2,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update ManagedReportSchedules columns for the customer account of normal msp ",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "3", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4",}, 2,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update ManagedReportSchedules columns for the root msp organization", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, ColumnType.REPORT_SCHEDULE.toString(),new String[] { "2", "1", "3", "4", "5" }, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3", }, 2,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update ManagedReportSchedules columns  for the msp account admin of root  msp organziation",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "2", "1", "7", "4", "2" }, 5,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4" }, 2,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update ManagedReportSchedules columns for the customer account of root  msp organziation",ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "2",  "4", "5" }, 2,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4" },2,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update ManagedReportSchedules columns for the sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "2", "1", "3", "4", "5" }, 4,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3" }, 2,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update ManagedReportSchedules columns for the msp account admin of sub  msp organziation",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "2", "3", "1", "4" }, 3,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3"}, 2,SpogConstants.SUCCESS_GET_PUT_DELETE },
			{ "update ManagedReportSchedules columns for the customer account of sub  msp organziation",ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "2", "1","6","3", "4", "5"},5,SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5", "4", "3"}, 2,SpogConstants.SUCCESS_GET_PUT_DELETE },


			//Csr 
			{ "update  ManagedReportSchedules columns for the direct organization with the csr user token ", ti.csr_token,ti.msp1_submsp1_suborg1_user1_id, ColumnType.REPORT_SCHEDULE.toString(),new String[] { "2", "1", "5", "6" }, 4, SpogConstants.SUCCESS_POST,new String[] { "2", "1", "5"}, 2,SpogConstants.SUCCESS_GET_PUT_DELETE },

			
			
			
		};
	}

	@Test(dataProvider = "update_columns",enabled=false)
	public void updateColumns_200_400_cases(String testCase,
			String token,
			String user_id,
			String columnType,
			String[] orderId,
			int noofcolumstobecreated,
			int expectedStatusCode,
			String[] updateorderId,
			int updatenoofcolumstobecreated,
			int updateexpectedStatusCode
			) {
		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("sykam.nagamalleswari");		

		test.log(LogStatus.INFO, "Get and delete log columns");
		spogServer.getAndDeleteColumns(columnType, user_id, token);

		test.log(LogStatus.INFO, "Getting the default columsn info");
		ArrayList<String> columnIdList = getDefaultColumns(token, user_id, columnType);

		test.log(LogStatus.INFO, "Compose the log columns as per the user request");
		ArrayList<HashMap<String, Object>>expected_columns=jp.composeColumnsInfo(noofcolumstobecreated, columnIdList, orderId, sort, filter, visible);


		test.log(LogStatus.INFO, "Create the log columns for the specified user");
		Response response = spogServer.postColumnsWithCheck(token, user_id, columnType, expected_columns, expectedStatusCode, null,test);


		test.log(LogStatus.INFO, "Compose the log columns as per the user request");
		ArrayList<HashMap<String, Object>>updatedexpected_columns=jp.composeColumnsInfo(updatenoofcolumstobecreated, columnIdList, orderId, sort, filter, visible);



		test.log(LogStatus.INFO, "Compose data to delet the column");
		String getfilterInfo = jp.composeColumnFilter(columnType, user_id, "none", "none");


		test.log(LogStatus.INFO, "Get the columns to know the user_column_id");
		response = spogServer.getColumns(token, getfilterInfo);

		test.log(LogStatus.INFO, "the User column id Info");
		String user_Column_id= response.then().extract().path("data.user_column_id");

		test.log(LogStatus.INFO, "Update the log columns for the specified user");
		spogServer.updateColumnWithCheck(token,user_Column_id, user_id, columnType, updatedexpected_columns, SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);

		test.log(LogStatus.INFO, "Get and delete columns");
		spogServer.getAndDeleteColumns(columnType, user_id, token);

	}


	@DataProvider(name = "updatecolumns_insufficientPermissions")
	public final Object[][] updatecoloumnsinValidParams() {

		return new Object[][] {

			// Insufficient permissions

			//Policy task reports 
			{ "update policy task columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policy task columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policy task columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policy task columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policy task columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policy task columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policy task columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policy task columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policy task columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policy task columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},


			//Recovered resources 
			{ "update recovered resources columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "update recovered resources columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "update recovered resources columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "update recovered resources columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "update recovered resources columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "update recovered resources columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "update recovered resources columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "update recovered resources columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "update recovered resources columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "update recovered resources columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},


			//Backup job reports 
			{ "update backup job report columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "update backup job report columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "update backup job report columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "update backup job report columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "update backup job report columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "update backup job report columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "update backup job report columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "update backup job report columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "update backup job report columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "update backup job report columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},

			//log columns
			{ "update log columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.LOG.toString()},
			{ "update log columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.LOG.toString()},
			{ "update log columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.LOG.toString()},
			{ "update log columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.LOG.toString()},
			{ "update log columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.LOG.toString()},
			{ "update log columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.LOG.toString()},
			{ "update log columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.LOG.toString()},
			{ "update log columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.LOG.toString()},
			{ "update log columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.LOG.toString()},
			{ "update log columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.LOG.toString()},


			//Recovery job reports 
			{ "update recovery_job_report columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "update recovery_job_report columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "update recovery_job_report columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "update recovery_job_report columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "update recovery_job_report columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "update recovery_job_report columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "update recovery_job_report columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "update recovery_job_report columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "update recovery_job_report columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "update lrecovery_job_report  columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},

			//Source columns 
			{ "update source columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.SOURCE.toString()},
			{ "update source columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.SOURCE.toString()},
			{ "update source columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.SOURCE.toString()},
			{ "update source columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.SOURCE.toString()},
			{ "update source columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.SOURCE.toString()},
			{ "update source columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.SOURCE.toString()},
			{ "update source columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.SOURCE.toString()},
			{ "update source columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.SOURCE.toString()},
			{ "update source columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.SOURCE.toString()},
			{ "update source  columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.SOURCE.toString()},


			//Job columns 
			{ "update job columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.JOB.toString()},
			{ "update job columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.JOB.toString()},
			{ "update job columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.JOB.toString()},
			{ "update job columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.JOB.toString()},
			{ "update job columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.JOB.toString()},
			{ "update job columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.JOB.toString()},
			{ "update job columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.JOB.toString()},
			{ "update job columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.JOB.toString()},
			{ "update job columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.JOB.toString()},
			{ "update job columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.JOB.toString()},

			//Destination columns 
			{ "update destiantion columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.DESTINATION.toString()},
			{ "update destiantion columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.DESTINATION.toString()},
			{ "update destiantion columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.DESTINATION.toString()},
			{ "update destiantion columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.DESTINATION.toString()},
			{ "update destiantion columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.DESTINATION.toString()},
			{ "update destiantion columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.DESTINATION.toString()},
			{ "update destiantion columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.DESTINATION.toString()},
			{ "update destiantion columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.DESTINATION.toString()},
			{ "update destiantion columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.DESTINATION.toString()},
			{ "update destiantion columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.DESTINATION.toString()},

			//PolicyTask columns 
			{ "update policytaskReport columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policytaskReport columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policytaskReport columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policytaskReport columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policytaskReport columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policytaskReport columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policytaskReport columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policytaskReport columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policytaskReport columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policytaskReport columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},


			//user columns 
			{ "update user columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.USER.toString()},
			{ "update user columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.USER.toString()},
			{ "update user columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.USER.toString()},
			{ "update user columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.USER.toString()},
			{ "update user columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.USER.toString()},
			{ "update user columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.USER.toString()},
			{ "update user columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.USER.toString()},
			{ "update user columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.USER.toString()},
			{ "update user columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.USER.toString()},
			{ "update user columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.USER.toString()},


			{ "update user columns for the direct organization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.USER.toString()},

			
			//report columns 
			{ "update report columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.REPORT.toString()},
			{ "update report columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.REPORT.toString()},
			{ "update report columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.REPORT.toString()},
			{ "update report columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.REPORT.toString()},
			{ "update report columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.REPORT.toString()},
			{ "update report columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.REPORT.toString()},
			{ "update report columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.REPORT.toString()},
			{ "update report columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.REPORT.toString()},
			{ "update report columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.REPORT.toString()},
			{ "update report columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.REPORT.toString()},

			//ManagedreportSchedules columns 
			{ "update ManagedreportSchedules columns for the direct organization with the another direct user  ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.direct_org2_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "update ManagedreportSchedules columns for the direct organization with the normal msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "update ManagedreportSchedules columns for the direct organization with the normal msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "update ManagedreportSchedules columns for the direct organization with the customer account of normsl msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "update ManagedreportSchedules columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "update ManagedreportSchedules columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "update ManagedreportSchedules columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "update ManagedreportSchedules columns for the direct organization with the root msp user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "update ManagedreportSchedules columns for the direct organization with the root msp account admin user token ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "update ManagedreportSchedules columns for the direct organization with the customer account of root msp ",ti.direct_org1_user1_token,ti.direct_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},

			
//Normal msp 

			// Insufficient permissions

			//Policy task reports 
			{ "update policy task columns for the normal msp organization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policy task columns for the normal msp organization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policy task columns for the normal msp organization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policy task columns for the normal msp organization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policy task columns for the normal msp organization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policy task columns for the normal msp organization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policy task columns for the normal msp organization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policy task columns for the normal msp organization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policy task columns for the normal msp organization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policy task columns for the normal msp organization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},


			//Recovered resources 
			{ "update recovered resources columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "update recovered resources columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "update recovered resources columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "update recovered resources columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "update recovered resources columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "update recovered resources columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "update recovered resources columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "update recovered resources columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "update recovered resources columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.RECOVEREDRESOURCE.toString()},
			{ "update recovered resources columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.RECOVEREDRESOURCE.toString()},


			//Backup job reports 
			{ "update backup job report columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "update backup job report columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "update backup job report columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "update backup job report columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "update backup job report columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "update backup job report columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "update backup job report columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "update backup job report columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "update backup job report columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.BACKUP_JOB_REPORT.toString()},
			{ "update backup job report columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.BACKUP_JOB_REPORT.toString()},

			//log columns
			{ "update log columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.LOG.toString()},
			{ "update log columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.LOG.toString()},
			{ "update log columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.LOG.toString()},
			{ "update log columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.LOG.toString()},
			{ "update log columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.LOG.toString()},
			{ "update log columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.LOG.toString()},
			{ "update log columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.LOG.toString()},
			{ "update log columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.LOG.toString()},
			{ "update log columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.LOG.toString()},
			{ "update log columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.LOG.toString()},


			//Recovery job reports 
			{ "update recovery_job_report columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "update recovery_job_report columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "update recovery_job_report columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "update recovery_job_report columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "update recovery_job_report columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "update recovery_job_report columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "update recovery_job_report columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "update recovery_job_report columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "update recovery_job_report columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.RESTORE_JOB_REPORT.toString()},
			{ "update lrecovery_job_report  columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.RESTORE_JOB_REPORT.toString()},

			//Source columns 
			{ "update source columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.SOURCE.toString()},
			{ "update source columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.SOURCE.toString()},
			{ "update source columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.SOURCE.toString()},
			{ "update source columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.SOURCE.toString()},
			{ "update source columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.SOURCE.toString()},
			{ "update source columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.SOURCE.toString()},
			{ "update source columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.SOURCE.toString()},
			{ "update source columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.SOURCE.toString()},
			{ "update source columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.SOURCE.toString()},
			{ "update source  columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.SOURCE.toString()},


			//Job columns 
			{ "update job columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.JOB.toString()},
			{ "update job columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.JOB.toString()},
			{ "update job columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.JOB.toString()},
			{ "update job columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.JOB.toString()},
			{ "update job columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.JOB.toString()},
			{ "update job columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.JOB.toString()},
			{ "update job columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.JOB.toString()},
			{ "update job columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.JOB.toString()},
			{ "update job columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.JOB.toString()},
			{ "update job columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.JOB.toString()},

			//Destination columns 
			{ "update destiantion columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.DESTINATION.toString()},
			{ "update destiantion columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.DESTINATION.toString()},
			{ "update destiantion columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.DESTINATION.toString()},
			{ "update destiantion columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.DESTINATION.toString()},
			{ "update destiantion columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.DESTINATION.toString()},
			{ "update destiantion columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.DESTINATION.toString()},
			{ "update destiantion columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.DESTINATION.toString()},
			{ "update destiantion columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.DESTINATION.toString()},
			{ "update destiantion columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.DESTINATION.toString()},
			{ "update destiantion columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.DESTINATION.toString()},

			//PolicyTask columns 
			{ "update policytaskReport columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policytaskReport columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policytaskReport columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policytaskReport columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policytaskReport columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policytaskReport columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policytaskReport columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policytaskReport columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policytaskReport columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.POLICY_TASK_REPORT.toString()},
			{ "update policytaskReport columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.POLICY_TASK_REPORT.toString()},


			//user columns 
			{ "update user columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.USER.toString()},
			{ "update user columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.USER.toString()},
			{ "update user columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.USER.toString()},
			{ "update user columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.USER.toString()},
			{ "update user columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.USER.toString()},
			{ "update user columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.USER.toString()},
			{ "update user columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.USER.toString()},
			{ "update user columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.USER.toString()},
			{ "update user columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.USER.toString()},
			{ "update user columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.USER.toString()},

			
			//report columns 
			{ "update report columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.REPORT.toString()},
			{ "update report columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.REPORT.toString()},
			{ "update report columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.REPORT.toString()},
			{ "update report columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.REPORT.toString()},
			{ "update report columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.REPORT.toString()},
			{ "update report columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.REPORT.toString()},
			{ "update report columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.REPORT.toString()},
			{ "update report columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.REPORT.toString()},
			{ "update report columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.REPORT.toString()},
			{ "update report columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.REPORT.toString()},

			//managedreportschedules columns 
			{ "update managedreportschedules columns for the normal msporganization with the another direct user  ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.direct_org2_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "update managedreportschedules columns for the normal msporganization with the normal msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "update managedreportschedules columns for the normal msporganization with the normal msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp_org2_msp_accountadmin1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "update managedreportschedules columns for the normal msporganization with the customer account of normsl msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "update managedreportschedules columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "update managedreportschedules columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "update managedreportschedules columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "update managedreportschedules columns for the normal msporganization with the root msp user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "update managedreportschedules columns for the normal msporganization with the root msp account admin user token ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.root_msp1_submsp1_account_admin_token,ColumnType.REPORT_SCHEDULE.toString()},
			{ "update managedreportschedules columns for the normal msporganization with the customer account of root msp ",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,ColumnType.REPORT_SCHEDULE.toString()},


		};
	}

	@Test(dataProvider = "updatecolumns_insufficientPermissions", enabled = true)
	public void updatecolumns_insufficientPermissions(String testCase,
			String validtoken,
			String user_id,
			String ortherOrgToken,
			String columnType 
			)
	{

		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("sykam.nagamalleswari");

		spogServer.setToken(validtoken);
		spogServer.getAndDeleteColumns(columnType, user_id, validtoken);

		String [] orderId = new String [] {"4","2","1","3"};

		ArrayList<String> columnIdList = getDefaultColumns(validtoken, user_id, columnType);

		test.log(LogStatus.INFO, "Compose the log columns as per the user request");
		ArrayList<HashMap<String, Object>>expected_columns=jp.composeColumnsInfo(1, columnIdList, orderId, sort, filter, visible);


		test.log(LogStatus.INFO, "Create the log columns for the specified user");
		spogServer.setToken(validtoken);
		spogServer.postColumnsWithCheck(validtoken, user_id, columnType, expected_columns, SpogConstants.SUCCESS_POST, null, test);


		test.log(LogStatus.INFO, "Compose data to delet the column");
		String getfilterInfo = jp.composeColumnFilter(columnType, user_id, "none", "none");


		test.log(LogStatus.INFO, "Get the columns to know the user_column_id");
		Response response = spogServer.getColumns(validtoken, getfilterInfo);

		test.log(LogStatus.INFO, "the User column id Info");
		String user_Column_id= response.then().extract().path("data.user_column_id");

		test.log(LogStatus.INFO, "Update the log columns for the specified user");
		spogServer.updateColumnWithCheck(ortherOrgToken,user_Column_id, user_id,columnType, expected_columns, SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY, test);


		//Update log columns for random user id 
		test.log(LogStatus.INFO, "update log columns for random user");
		String 	random_User_id = UUID.randomUUID().toString();
		spogServer.updateColumnWithCheck(validtoken,user_Column_id, random_User_id,columnType, expected_columns, SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.USER_ID_DOESNOT_EXIST, test);

		//update columns for invalid user id 
		String invalid_user_id = "1234erdfdyd";
		test.log(LogStatus.INFO, "update log columns for invalid user");
		spogServer.updateColumnWithCheck(validtoken,user_Column_id, invalid_user_id,columnType, expected_columns, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.USER_ID_DOESNOT_EXIST, test); 


		//Update columns for the invalid column_id
		test.log(LogStatus.INFO, "Update the log columns for the invalid user column id");
		spogServer.updateColumnWithCheck(validtoken,"Invalid_Column_user_id", user_id,columnType, expected_columns, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.COLUMN_ID_DOESNOT_EXIST, test);

		test.log(LogStatus.INFO, "Get and delete columns");
		spogServer.setToken(validtoken);
		spogServer.getAndDeleteColumns(columnType, user_id, validtoken);
	}


	@DataProvider(name = "update_Columns_invalid_order_id_secnarioes")
	public final Object[][] updateColumnsforLoggedInUser4() {

		return new Object[][] {
			{ "update backupjobreport columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.BACKUP_JOB_REPORT.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "update backupjobreport columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.BACKUP_JOB_REPORT.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "update backupjobreport columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"update backupjobreport columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "update backupjobreport columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "update backupjobreport columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.BACKUP_JOB_REPORT.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },

			{ "update email_alert_recepient columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.ALERT_EMAIL_RECIPIENT.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "update email_alert_recepient columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.ALERT_EMAIL_RECIPIENT.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "update email_alert_recepient columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"update email_alert_recepient columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "update email_alert_recepient columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "update email_alert_recepient columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.ALERT_EMAIL_RECIPIENT.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },

			{ "update destination columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.DESTINATION.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "update destination columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.DESTINATION.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "update destination columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.DESTINATION.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"update destination columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.DESTINATION.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "update destination columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.DESTINATION.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "update destination columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.DESTINATION.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },

			{ "update hypervisor columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.HYPERVISOR.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "update hypervisor columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.HYPERVISOR.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "update hypervisor columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.HYPERVISOR.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"update hypervisor columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.HYPERVISOR.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "update hypervisor columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.HYPERVISOR.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "update hypervisor columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.HYPERVISOR.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },

			{ "update job columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.JOB.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "update job columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.JOB.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "update job columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.JOB.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"update job columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.JOB.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "update job columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.JOB.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "update job columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.JOB.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },


			{ "update log columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.LOG.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "update log columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.LOG.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "update log columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.LOG.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"update log columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.LOG.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "update log columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.LOG.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "update log columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.LOG.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },

			{ "update policy columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.POLICY.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "update policy columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.POLICY.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "update policy columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.POLICY.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"update policy columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.POLICY.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "update policy columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.POLICY.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "update policy columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.POLICY.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },


			{ "update policytaskReport columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.POLICY_TASK_REPORT.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "update policytaskReport columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.POLICY_TASK_REPORT.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "update policytaskReport columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"update policytaskReport columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "update policytaskReport columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "update policytaskReport columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.POLICY_TASK_REPORT.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },


			{ "update recoveredresource columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.RECOVEREDRESOURCE.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "update recoveredresource columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.RECOVEREDRESOURCE.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "update recoveredresource columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"update recoveredresource columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "update recoveredresource columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "update recoveredresource columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.RECOVEREDRESOURCE.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },

			{ "update restorejobReport columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.RESTORE_JOB_REPORT.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "update restorejobReport columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.RESTORE_JOB_REPORT.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "update restorejobReport columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"update restorejobReport columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "update restorejobReport columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "update restorejobReport columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.RESTORE_JOB_REPORT.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },

			{ "update source columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.SOURCE.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "update source columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.SOURCE.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "update source columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.SOURCE.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"update source columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.SOURCE.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "update source columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.SOURCE.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "update source columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.SOURCE.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },

			{ "update user columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.USER.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "update user columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.USER.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "update user columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.USER.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"update user columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.USER.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "update user columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.USER.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "update user columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.USER.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },

			{ "update report columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.REPORT.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "update report columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.REPORT.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "update report columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.REPORT.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"update report columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.REPORT.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "update report columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.REPORT.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "update report columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.REPORT.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },

			
			{ "update managedreportschedule columns for the direct organization","order_id_0", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.REPORT_SCHEDULE.toString(),new String[] { "0", "0", "0", "0", "0" }, 2, SpogConstants.SUCCESS_POST },
			{ "update managedreportschedule columns for the direct organization","order_id_negative_values", ti.direct_org1_user1_token,ti.direct_org1_user1_id, ColumnType.REPORT_SCHEDULE.toString(),new String[] { "-1", "-1", "-1", "-1", "-1" }, 2, SpogConstants.SUCCESS_POST },
			{ "update managedreportschedule columns for the normal msp account admin ","order_id_0",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "0", "0", "0", "0", "0" }, 1,SpogConstants.SUCCESS_POST },
			{"update managedreportschedule columns for the normal msp account admin ", "order_id_negative_values",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "-1", "-1", "-1", "-1", "-1" }, 4,SpogConstants.SUCCESS_POST },
			{ "update managedreportschedule columns for the customer account of normal msp ","order_id_0",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "0", "0", "0" },1,SpogConstants.SUCCESS_POST },
			{ "update managedreportschedule columns for the customer account of normal msp ","order_id_negative_values",ti.normal_msp2_suborg1_user1_token, ti.normal_msp2_suborg1_user1_id,ColumnType.REPORT_SCHEDULE.toString(), new String[] { "-1", "-1", "-1" }, 2,SpogConstants.SUCCESS_POST },


		};
	}
	@Test(dataProvider = "update_Columns_invalid_order_id_secnarioes",enabled=false)
	public void updateCloumswithInvalid_Order_Ids(String testCase,
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

		String [] orderId1 = new String [] {"4","2","1","3"};

		test.log(LogStatus.INFO, "Compose the log columns as per the user request");
		ArrayList<HashMap<String, Object>>expected_columns=jp.composeColumnsInfo(2, columnIdList, orderId1, sort, filter, visible);


		test.log(LogStatus.INFO, "Create the log columns for the specified user");
		spogServer.setToken(token);
		Response response = spogServer.postColumnsWithCheck(token, user_id, columnType, expected_columns, SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "Validate the get columns with the given columns");
		response =spogServer.getColumnsWithCheck(token, user_id, columnType, expected_columns, SpogConstants.SUCCESS_GET_PUT_DELETE,null, test);

		test.log(LogStatus.INFO, "the User column id Info");
		String delete_Column_id= response.then().extract().path("data.user_column_id");

		test.log(LogStatus.INFO, "Compose the log columns as per the user request");
		ArrayList<HashMap<String, Object>>updated_expected_columns=jp.composeColumnsInfo(1, columnIdList, orderId, sort, filter, visible);


		test.log(LogStatus.INFO, "update Columsn with the invalid orderid");
		spogServer.updateColumnWithCheck(token,delete_Column_id, user_id, columnType, updated_expected_columns,SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ORDER_ID_ATLEAST_1, test);
 
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

}
