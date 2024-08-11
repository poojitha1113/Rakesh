
package api.audittrail;

import static org.testng.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.joda.time.LocalDate;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Base64.EncodingBase64;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Log4GatewayServer;
import InvokerServer.Log4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGRecoveredResourceServer;
import InvokerServer.SPOGServer;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.CreateOrgsInfo;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetAuditTrailCodesByCSRTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private TestOrgInfo ti;
	private ExtentTest test;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);

		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);

		test = rep.startTest("Setup");
		Nooftest = 0;
		bqdb1 = new SQLServerDb();
		String author = "Ramya.Nagepalli";
		count1 = new testcasescount();
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

	}

	@DataProvider(name = "getAuditTrailCodeswithcsrAdminToken")
	public final Object[][] getAuditTrailCodeswithcsrAdminToken() {

		return new Object[][] { { "page=4&page_size=5", ti.csr_readonly_token }, { "page=4&page_size=5", ti.csr_token },
				{ "page=3&page_size=2", ti.csr_token }, { "page=7&page_size=8", ti.csr_token },
				{ "page=1&page_size=6", ti.csr_token }, { "page=3&page_size=8", ti.csr_token },
				{ "page=1", ti.csr_token }, { "page=5", ti.csr_token }, { "", ti.csr_token }

		};

	}

	@Test(dataProvider = "getAuditTrailCodeswithcsrAdminToken")
	public void getAuditTrailCodeswithcsrAdminToken(String additionalurl, String Token) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");

		ArrayList<HashMap> auditdetails = new ArrayList<HashMap>();

		spogServer.userLogin(ti.csr_admin_email, ti.csr_admin_password);
		test.log(LogStatus.INFO, "Getting the jwtToken of  csr user ");

		auditdetails = getComposedAudits(additionalurl, test);

		test.log(LogStatus.INFO, "get audit code details of csr using token");
		Response response = spogServer.getAuditCodesForCSR(Token, auditdetails, additionalurl,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

	}

	@Test(dataProvider = "getAuditTrailCodeswithcsrAdminToken")
	public void getAuditTrailCodeswithcsrAdminToken_401(String additionalurl, String token) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		ArrayList<HashMap> auditdetails = new ArrayList<HashMap>();

		spogServer.userLogin(ti.csr_admin_email, ti.csr_admin_password);
		test.log(LogStatus.INFO, "Getting the jwtToken of  csr user ");

		auditdetails = getComposedAudits(additionalurl, test);

		test.log(LogStatus.INFO, "get audit code details of csr using  missing token");

		Response response = spogServer.getAuditCodesForCSR("", auditdetails, additionalurl, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		test.log(LogStatus.INFO, "get audit code details of csr using  invalid token");
		response = spogServer.getAuditCodesForCSR("abc", auditdetails, additionalurl, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

	}

	@DataProvider(name = "getAuditTrailCodeswithcsrAdminToken_403")
	public final Object[][] getAuditTrailCodeswithcsrAdminToken_403() {

		return new Object[][] {
				{ "get audit code details of csr using direct organization token", ti.direct_org1_user1_token },
				{ "get audit code details of csr using normal_msp organization token", ti.normal_msp_org1_user1_token },
				{ "get audit code details of csr using suborg organization token", ti.normal_msp1_suborg1_user1_token },
				{ "get audit code details of csr using root_msp organization token", ti.root_msp_org1_user1_token },
				{ "get audit code details of csr using root_sub organization token", ti.root_msp1_suborg1_user1_token },
				{ "get audit code details of csr submsp_sub organization token", ti.msp1_submsp1_suborg1_user1_token },
				{ "get audit code details of csr submsp organization token", ti.root_msp1_submsp1_user1_token },
				// monitor role cases
				{ "get audit code details of csr using direct monitor token", ti.direct_org1_monitor_user1_token },
				{ "get audit code details of csr using msp monitor token", ti.normal_msp_org1_monitor_user1_token },
				{ "get audit code details of csr using suborg monitor token",
						ti.normal_msp1_suborg1_monitor_user1_token },
				{ "get audit code details of csr using root msp monitor token", ti.root_msp_org1_monitor_user1_token },
				{ "get audit code details of csr using root suborg monitor token",
						ti.root_msp1_suborg1_monitor_user1_token },
				{ "get audit code details of csr submsp sub monitor token", ti.root_msp1_submsp1_monitor_user1_token },
				{ "get audit code details of csr using submsp suborg monitor token",
						ti.msp1_submsp1_suborg1_monitor_user1_token },

		};

	}

	@Test(dataProvider = "getAuditTrailCodeswithcsrAdminToken_403")
	public void getAuditTrailCodeswithcsrAdminToken_403(String testcase, String otherOrgToken) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "Audit code details by CSR token");
		test.assignAuthor("Ramya Nagepalli");
		ArrayList<HashMap> auditdetails = new ArrayList<HashMap>();

		String additionalurl = "page=1&page_size=4";

		auditdetails = getComposedAudits(additionalurl, test);

		test.log(LogStatus.INFO, testcase);
		Response response = spogServer.getAuditCodesForCSR(otherOrgToken, auditdetails, additionalurl,
				SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE, test);

	}

	private ArrayList<HashMap> getComposedAudits(String filter, ExtentTest test) {

		ArrayList<HashMap> expecteddetails = new ArrayList<HashMap>();

		HashMap<String, Object> code_1 = new HashMap<String, Object>();
		code_1.put("code_id", "1");
		code_1.put("code_name", "create user");
		expecteddetails.add(code_1);

		HashMap<String, Object> code_2 = new HashMap<String, Object>();
		code_2.put("code_id", "2");
		code_2.put("code_name", "modify user");
		expecteddetails.add(code_2);

		HashMap<String, Object> code_3 = new HashMap<String, Object>();
		code_3.put("code_id", "3");
		code_3.put("code_name", "delete user");
		expecteddetails.add(code_3);

		HashMap<String, Object> code_4 = new HashMap<String, Object>();
		code_4.put("code_id", "4");
		code_4.put("code_name", "login user");
		expecteddetails.add(code_4);

		HashMap<String, Object> code_5 = new HashMap<String, Object>();
		code_5.put("code_id", "5");
		code_5.put("code_name", "modify login user");
		expecteddetails.add(code_5);

		HashMap<String, Object> code_6 = new HashMap<String, Object>();
		code_6.put("code_id", "6");
		code_6.put("code_name", "modify user password");
		expecteddetails.add(code_6);

		HashMap<String, Object> code_7 = new HashMap<String, Object>();
		code_7.put("code_id", "7");
		code_7.put("code_name", "modify login user password");
		expecteddetails.add(code_7);

		HashMap<String, Object> code_7_1 = new HashMap<String, Object>();
		code_7_1.put("code_id", "101");
		code_7_1.put("code_name", "create organization");
		expecteddetails.add(code_7_1);

		HashMap<String, Object> code_8 = new HashMap<String, Object>();
		code_8.put("code_id", "102");
		code_8.put("code_name", "modify organization");
		expecteddetails.add(code_8);

		HashMap<String, Object> code_9 = new HashMap<String, Object>();
		code_9.put("code_id", "103");
		code_9.put("code_name", "delete organization");
		expecteddetails.add(code_9);

		HashMap<String, Object> code_10 = new HashMap<String, Object>();
		code_10.put("code_id", "105");
		code_10.put("code_name", "create MSP account");
		expecteddetails.add(code_10);

		HashMap<String, Object> code_11 = new HashMap<String, Object>();
		code_11.put("code_id", "106");
		code_11.put("code_name", "modify MSP account");
		expecteddetails.add(code_11);

		HashMap<String, Object> code_12 = new HashMap<String, Object>();
		code_12.put("code_id", "107");
		code_12.put("code_name", "delete MSP account");
		expecteddetails.add(code_12);

		HashMap<String, Object> code_13 = new HashMap<String, Object>();
		code_13.put("code_id", "108");
		code_13.put("code_name", "modify login user organization");
		expecteddetails.add(code_13);

		HashMap<String, Object> code_13_1 = new HashMap<String, Object>();
		code_13_1.put("code_id", "109");
		code_13_1.put("code_name", "assign threshold cloud capacity for msp account");
		expecteddetails.add(code_13_1);

		HashMap<String, Object> code_13_2 = new HashMap<String, Object>();
		code_13_2.put("code_id", "110");
		code_13_2.put("code_name", "create organization orders");
		expecteddetails.add(code_13_2);

		HashMap<String, Object> code_14 = new HashMap<String, Object>();
		code_14.put("code_id", "301");
		code_14.put("code_name", "create cloud account");
		expecteddetails.add(code_14);

		HashMap<String, Object> code_15 = new HashMap<String, Object>();
		code_15.put("code_id", "302");
		code_15.put("code_name", "update cloud account");
		expecteddetails.add(code_15);

		HashMap<String, Object> code_16 = new HashMap<String, Object>();
		code_16.put("code_id", "303");
		code_16.put("code_name", "delete cloud account");
		expecteddetails.add(code_16);

		HashMap<String, Object> code_17 = new HashMap<String, Object>();
		code_17.put("code_id", "304");
		code_17.put("code_name", "login cloud account");
		expecteddetails.add(code_17);

		HashMap<String, Object> code_18 = new HashMap<String, Object>();
		code_18.put("code_id", "401");
		code_18.put("code_name", "create site");
		expecteddetails.add(code_18);

		HashMap<String, Object> code_19 = new HashMap<String, Object>();
		code_19.put("code_id", "402");
		code_19.put("code_name", "update site");
		expecteddetails.add(code_19);

		HashMap<String, Object> code_20 = new HashMap<String, Object>();
		code_20.put("code_id", "403");
		code_20.put("code_name", "delete site");
		expecteddetails.add(code_20);

		HashMap<String, Object> code_21 = new HashMap<String, Object>();
		code_21.put("code_id", "404");
		code_21.put("code_name", "login site");
		expecteddetails.add(code_21);

		HashMap<String, Object> code_22 = new HashMap<String, Object>();
		code_22.put("code_id", "405");
		code_22.put("code_name", "register site");
		expecteddetails.add(code_22);

		HashMap<String, Object> code_23 = new HashMap<String, Object>();
		code_23.put("code_id", "501");
		code_23.put("code_name", "create source");
		expecteddetails.add(code_23);

		HashMap<String, Object> code_24 = new HashMap<String, Object>();
		code_24.put("code_id", "502");
		code_24.put("code_name", "update source");
		expecteddetails.add(code_24);

		HashMap<String, Object> code_25 = new HashMap<String, Object>();
		code_25.put("code_id", "503");
		code_25.put("code_name", "delete source");
		expecteddetails.add(code_25);

		HashMap<String, Object> code_25_1 = new HashMap<String, Object>();
		code_25_1.put("code_id", "504");
		code_25_1.put("code_name", "hide source");
		expecteddetails.add(code_25_1);

		HashMap<String, Object> code_25_2 = new HashMap<String, Object>();
		code_25_2.put("code_id", "505");
		code_25_2.put("code_name", "remove policy for source");
		expecteddetails.add(code_25_2);

		HashMap<String, Object> code_26 = new HashMap<String, Object>();
		code_26.put("code_id", "601");
		code_26.put("code_name", "create destination");
		expecteddetails.add(code_26);

		HashMap<String, Object> code_27 = new HashMap<String, Object>();
		code_27.put("code_id", "602");
		code_27.put("code_name", "update destination");
		expecteddetails.add(code_27);

		HashMap<String, Object> code_28 = new HashMap<String, Object>();
		code_28.put("code_id", "603");
		code_28.put("code_name", "delete destination");
		expecteddetails.add(code_28);

		HashMap<String, Object> code_29 = new HashMap<String, Object>();
		code_29.put("code_id", "604");
		code_29.put("code_name", "create cloud direct destination");
		expecteddetails.add(code_29);

		HashMap<String, Object> code_30 = new HashMap<String, Object>();
		code_30.put("code_id", "701");
		code_30.put("code_name", "create source group");
		expecteddetails.add(code_30);

		HashMap<String, Object> code_31 = new HashMap<String, Object>();
		code_31.put("code_id", "702");
		code_31.put("code_name", "update source group");
		expecteddetails.add(code_31);

		HashMap<String, Object> code_32 = new HashMap<String, Object>();
		code_32.put("code_id", "703");
		code_32.put("code_name", "delete source group");
		expecteddetails.add(code_32);

		HashMap<String, Object> code_33 = new HashMap<String, Object>();
		code_33.put("code_id", "801");
		code_33.put("code_name", "add source to group");
		expecteddetails.add(code_33);

		HashMap<String, Object> code_34 = new HashMap<String, Object>();
		code_34.put("code_id", "802");
		code_34.put("code_name", "delete source from group");
		expecteddetails.add(code_34);

		HashMap<String, Object> code_35 = new HashMap<String, Object>();
		code_35.put("code_id", "1001");
		code_35.put("code_name", "create source filter for user");
		expecteddetails.add(code_35);

		HashMap<String, Object> code_36 = new HashMap<String, Object>();
		code_36.put("code_id", "1002");
		code_36.put("code_name", "update source filter for user");
		expecteddetails.add(code_36);

		HashMap<String, Object> code_37 = new HashMap<String, Object>();
		code_37.put("code_id", "1003");
		code_37.put("code_name", "delete source filter for user");
		expecteddetails.add(code_37);

		HashMap<String, Object> code_38 = new HashMap<String, Object>();
		code_38.put("code_id", "1004");
		code_38.put("code_name", "create destination filter for user");
		expecteddetails.add(code_38);

		HashMap<String, Object> code_39 = new HashMap<String, Object>();
		code_39.put("code_id", "1005");
		code_39.put("code_name", "update destination filter for user");
		expecteddetails.add(code_39);

		HashMap<String, Object> code_40 = new HashMap<String, Object>();
		code_40.put("code_id", "1006");
		code_40.put("code_name", "delete destination filter for user");
		expecteddetails.add(code_40);

		HashMap<String, Object> code_41 = new HashMap<String, Object>();
		code_41.put("code_id", "1007");
		code_41.put("code_name", "create job filter for user");
		expecteddetails.add(code_41);

		HashMap<String, Object> code_42 = new HashMap<String, Object>();
		code_42.put("code_id", "1008");
		code_42.put("code_name", "update job filter for user");
		expecteddetails.add(code_42);

		HashMap<String, Object> code_43 = new HashMap<String, Object>();
		code_43.put("code_id", "1009");
		code_43.put("code_name", "delete job filter for user");
		expecteddetails.add(code_43);

		HashMap<String, Object> code_44 = new HashMap<String, Object>();
		code_44.put("code_id", "1010");
		code_44.put("code_name", "create log filter for user");
		expecteddetails.add(code_44);

		HashMap<String, Object> code_45 = new HashMap<String, Object>();
		code_45.put("code_id", "1011");
		code_45.put("code_name", "update log filter for user");
		expecteddetails.add(code_45);

		HashMap<String, Object> code_46 = new HashMap<String, Object>();
		code_46.put("code_id", "1012");
		code_46.put("code_name", "delete log filter for user");
		expecteddetails.add(code_46);

		HashMap<String, Object> code_47 = new HashMap<String, Object>();
		code_47.put("code_id", "1013");
		code_47.put("code_name", "create user filter");
		expecteddetails.add(code_47);

		HashMap<String, Object> code_48 = new HashMap<String, Object>();
		code_48.put("code_id", "1014");
		code_48.put("code_name", "update user filter");
		expecteddetails.add(code_48);

		HashMap<String, Object> code_49 = new HashMap<String, Object>();
		code_49.put("code_id", "1015");
		code_49.put("code_name", "delete user filter");
		expecteddetails.add(code_49);

		HashMap<String, Object> code_50 = new HashMap<String, Object>();
		code_50.put("code_id", "1016");
		code_50.put("code_name", "create hypervisor filter for user");
		expecteddetails.add(code_50);

		HashMap<String, Object> code_51 = new HashMap<String, Object>();
		code_51.put("code_id", "1017");
		code_51.put("code_name", "update hypervisor filter for user");
		expecteddetails.add(code_51);

		HashMap<String, Object> code_52 = new HashMap<String, Object>();
		code_52.put("code_id", "1018");
		code_52.put("code_name", "delete hypervisor filter for user");
		expecteddetails.add(code_52);

		HashMap<String, Object> code_52_1 = new HashMap<String, Object>();
		code_52_1.put("code_id", "1019");
		code_52_1.put("code_name", "create recovered resource filter for user");
		expecteddetails.add(code_52_1);

		HashMap<String, Object> code_52_2 = new HashMap<String, Object>();
		code_52_2.put("code_id", "1020");
		code_52_2.put("code_name", "update recovered resource filter for user");
		expecteddetails.add(code_52_2);

		HashMap<String, Object> code_52_3 = new HashMap<String, Object>();
		code_52_3.put("code_id", "1021");
		code_52_3.put("code_name", "delete recovered resource filter for user");
		expecteddetails.add(code_52_3);

		HashMap<String, Object> code_52_4 = new HashMap<String, Object>();
		code_52_4.put("code_id", "1022");
		code_52_4.put("code_name", "create policy filter for user");
		expecteddetails.add(code_52_4);

		HashMap<String, Object> code_52_5 = new HashMap<String, Object>();
		code_52_5.put("code_id", "1023");
		code_52_5.put("code_name", "update policy filter for user");
		expecteddetails.add(code_52_5);

		HashMap<String, Object> code_52_6 = new HashMap<String, Object>();
		code_52_6.put("code_id", "1024");
		code_52_6.put("code_name", "delete policy filter for user");
		expecteddetails.add(code_52_6);

		HashMap<String, Object> code_52_7 = new HashMap<String, Object>();
		code_52_7.put("code_id", "1025");
		code_52_7.put("code_name", "create report list filter for user");
		expecteddetails.add(code_52_7);

		HashMap<String, Object> code_52_8 = new HashMap<String, Object>();
		code_52_8.put("code_id", "1026");
		code_52_8.put("code_name", "update report list filter for user");
		expecteddetails.add(code_52_8);

		HashMap<String, Object> code_52_9 = new HashMap<String, Object>();
		code_52_9.put("code_id", "1027");
		code_52_9.put("code_name", "delete report list filter for user");
		expecteddetails.add(code_52_9);

		HashMap<String, Object> code_52_10 = new HashMap<String, Object>();
		code_52_10.put("code_id", "1028");
		code_52_10.put("code_name", "create report filter for user");
		expecteddetails.add(code_52_10);

		HashMap<String, Object> code_52_11 = new HashMap<String, Object>();
		code_52_11.put("code_id", "1029");
		code_52_11.put("code_name", "update report filter for user");
		expecteddetails.add(code_52_11);

		HashMap<String, Object> code_52_12 = new HashMap<String, Object>();
		code_52_12.put("code_id", "1030");
		code_52_12.put("code_name", "delete report filter for user");
		expecteddetails.add(code_52_12);

		HashMap<String, Object> code_53 = new HashMap<String, Object>();
		code_53.put("code_id", "2001");
		code_53.put("code_name", "create source column preferences for user");
		expecteddetails.add(code_53);

		HashMap<String, Object> code_54 = new HashMap<String, Object>();
		code_54.put("code_id", "2002");
		code_54.put("code_name", "update source column preferences for user");
		expecteddetails.add(code_54);

		HashMap<String, Object> code_55 = new HashMap<String, Object>();
		code_55.put("code_id", "2003");
		code_55.put("code_name", "delete source column preferences for user");
		expecteddetails.add(code_55);

		HashMap<String, Object> code_56 = new HashMap<String, Object>();
		code_56.put("code_id", "2004");
		code_56.put("code_name", "create destination column preferences for user");
		expecteddetails.add(code_56);

		HashMap<String, Object> code_57 = new HashMap<String, Object>();
		code_57.put("code_id", "2005");
		code_57.put("code_name", "update destination column preferences for user");
		expecteddetails.add(code_57);

		HashMap<String, Object> code_58 = new HashMap<String, Object>();
		code_58.put("code_id", "2006");
		code_58.put("code_name", "delete destination column preferences for user");
		expecteddetails.add(code_58);

		HashMap<String, Object> code_59 = new HashMap<String, Object>();
		code_59.put("code_id", "2007");
		code_59.put("code_name", "create hypervisor column preferences for user");
		expecteddetails.add(code_59);

		HashMap<String, Object> code_60 = new HashMap<String, Object>();
		code_60.put("code_id", "2008");
		code_60.put("code_name", "update hypervisor column preferences for user");
		expecteddetails.add(code_60);

		HashMap<String, Object> code_61 = new HashMap<String, Object>();
		code_61.put("code_id", "2009");
		code_61.put("code_name", "delete hypervisor column preferences for user");
		expecteddetails.add(code_61);

		HashMap<String, Object> code_62 = new HashMap<String, Object>();
		code_62.put("code_id", "2010");
		code_62.put("code_name", "create user column preferences for user");
		expecteddetails.add(code_62);

		HashMap<String, Object> code_63 = new HashMap<String, Object>();
		code_63.put("code_id", "2011");
		code_63.put("code_name", "update user column preferences for user");
		expecteddetails.add(code_63);

		HashMap<String, Object> code_64 = new HashMap<String, Object>();
		code_64.put("code_id", "2012");
		code_64.put("code_name", "delete user column preferences for user");
		expecteddetails.add(code_64);

		HashMap<String, Object> code_65 = new HashMap<String, Object>();
		code_65.put("code_id", "2013");
		code_65.put("code_name", "create job column preferences for user");
		expecteddetails.add(code_65);

		HashMap<String, Object> code_66 = new HashMap<String, Object>();
		code_66.put("code_id", "2014");
		code_66.put("code_name", "update job column preferences for user");
		expecteddetails.add(code_66);

		HashMap<String, Object> code_67 = new HashMap<String, Object>();
		code_67.put("code_id", "2015");
		code_67.put("code_name", "delete job column preferences for user");
		expecteddetails.add(code_67);

		HashMap<String, Object> code_68 = new HashMap<String, Object>();
		code_68.put("code_id", "2016");
		code_68.put("code_name", "create log column preferences for user");
		expecteddetails.add(code_68);

		HashMap<String, Object> code_69 = new HashMap<String, Object>();
		code_69.put("code_id", "2017");
		code_69.put("code_name", "update log column preferences for user");
		expecteddetails.add(code_69);

		HashMap<String, Object> code_70 = new HashMap<String, Object>();
		code_70.put("code_id", "2018");
		code_70.put("code_name", "delete log column preferences for user");
		expecteddetails.add(code_70);

		HashMap<String, Object> code_70_1 = new HashMap<String, Object>();
		code_70_1.put("code_id", "2019");
		code_70_1.put("code_name", "create recovered resource column preferences for user");
		expecteddetails.add(code_70_1);

		HashMap<String, Object> code_70_2 = new HashMap<String, Object>();
		code_70_2.put("code_id", "2020");
		code_70_2.put("code_name", "update recovered resource column preferences for user");
		expecteddetails.add(code_70_2);

		HashMap<String, Object> code_70_3 = new HashMap<String, Object>();
		code_70_3.put("code_id", "2021");
		code_70_3.put("code_name", "delete recovered resource column preferences for user");
		expecteddetails.add(code_70_3);

		HashMap<String, Object> code_70_4 = new HashMap<String, Object>();
		code_70_4.put("code_id", "2022");
		code_70_4.put("code_name", "create policy column preferences for user");
		expecteddetails.add(code_70_4);

		HashMap<String, Object> code_70_5 = new HashMap<String, Object>();
		code_70_5.put("code_id", "2023");
		code_70_5.put("code_name", "update policy column preferences for user");
		expecteddetails.add(code_70_5);

		HashMap<String, Object> code_70_6 = new HashMap<String, Object>();
		code_70_6.put("code_id", "2024");
		code_70_6.put("code_name", "delete policy column preferences for user");
		expecteddetails.add(code_70_6);

		HashMap<String, Object> code_70_7 = new HashMap<String, Object>();
		code_70_7.put("code_id", "2025");
		code_70_7.put("code_name", "create backup job report column preferences for user");
		expecteddetails.add(code_70_7);

		HashMap<String, Object> code_70_8 = new HashMap<String, Object>();
		code_70_8.put("code_id", "2026");
		code_70_8.put("code_name", "update backup job report column preferences for user");
		expecteddetails.add(code_70_8);

		HashMap<String, Object> code_70_9 = new HashMap<String, Object>();
		code_70_9.put("code_id", "2027");
		code_70_9.put("code_name", "delete backup job report column preferences for user");
		expecteddetails.add(code_70_9);

		HashMap<String, Object> code_70_10 = new HashMap<String, Object>();
		code_70_10.put("code_id", "2028");
		code_70_10.put("code_name", "create restore job report column preferences for user");
		expecteddetails.add(code_70_10);

		HashMap<String, Object> code_70_11 = new HashMap<String, Object>();
		code_70_11.put("code_id", "2029");
		code_70_11.put("code_name", "update restore job report column preferences for user");
		expecteddetails.add(code_70_11);

		HashMap<String, Object> code_70_12 = new HashMap<String, Object>();
		code_70_12.put("code_id", "2030");
		code_70_12.put("code_name", "delete restore job report column preferences for user");
		expecteddetails.add(code_70_12);

		HashMap<String, Object> code_71 = new HashMap<String, Object>();
		code_71.put("code_id", "3001");
		code_71.put("code_name", "create hypervisor");
		expecteddetails.add(code_71);

		HashMap<String, Object> code_72 = new HashMap<String, Object>();
		code_72.put("code_id", "3002");
		code_72.put("code_name", "update hypervisor");
		expecteddetails.add(code_72);

		HashMap<String, Object> code_73 = new HashMap<String, Object>();
		code_73.put("code_id", "3003");
		code_73.put("code_name", "delete hypervisor");
		expecteddetails.add(code_73);

		HashMap<String, Object> code_74 = new HashMap<String, Object>();
		code_74.put("code_id", "4001");
		code_74.put("code_name", "create log");
		expecteddetails.add(code_74);

		HashMap<String, Object> code_75 = new HashMap<String, Object>();
		code_75.put("code_id", "4002");
		code_75.put("code_name", "update log");
		expecteddetails.add(code_75);

		HashMap<String, Object> code_76 = new HashMap<String, Object>();
		code_76.put("code_id", "4003");
		code_76.put("code_name", "delete log");
		expecteddetails.add(code_76);

		HashMap<String, Object> code_77 = new HashMap<String, Object>();
		code_77.put("code_id", "4004");
		code_77.put("code_name", "create log data");
		expecteddetails.add(code_77);

		HashMap<String, Object> code_78 = new HashMap<String, Object>();
		code_78.put("code_id", "4005");
		code_78.put("code_name", "update log data");
		expecteddetails.add(code_78);

		HashMap<String, Object> code_79 = new HashMap<String, Object>();
		code_79.put("code_id", "4006");
		code_79.put("code_name", "create job");
		expecteddetails.add(code_79);

		HashMap<String, Object> code_80 = new HashMap<String, Object>();
		code_80.put("code_id", "4007");
		code_80.put("code_name", "update job");
		expecteddetails.add(code_80);

		HashMap<String, Object> code_81 = new HashMap<String, Object>();
		code_81.put("code_id", "4008");
		code_81.put("code_name", "create job data");
		expecteddetails.add(code_81);

		HashMap<String, Object> code_82 = new HashMap<String, Object>();
		code_82.put("code_id", "4009");
		code_82.put("code_name", "update job data");
		expecteddetails.add(code_82);

		HashMap<String, Object> code_83 = new HashMap<String, Object>();
		code_83.put("code_id", "4010");
		code_83.put("code_name", "create policy");
		expecteddetails.add(code_83);

		HashMap<String, Object> code_84 = new HashMap<String, Object>();
		code_84.put("code_id", "4011");
		code_84.put("code_name", "update policy");
		expecteddetails.add(code_84);

		HashMap<String, Object> code_85 = new HashMap<String, Object>();
		code_85.put("code_id", "4012");
		code_85.put("code_name", "delete policy");
		expecteddetails.add(code_85);

		HashMap<String, Object> code_86 = new HashMap<String, Object>();
		code_86.put("code_id", "4013");
		code_86.put("code_name", "assign policy");
		expecteddetails.add(code_86);

		HashMap<String, Object> code_87 = new HashMap<String, Object>();
		code_87.put("code_id", "4014");
		code_87.put("code_name", "unassign policy");
		expecteddetails.add(code_87);

		HashMap<String, Object> code_88 = new HashMap<String, Object>();
		code_88.put("code_id", "4015");
		code_88.put("code_name", "create report schedule");
		expecteddetails.add(code_88);

		HashMap<String, Object> code_89 = new HashMap<String, Object>();
		code_89.put("code_id", "4016");
		code_89.put("code_name", "update report schedule");
		expecteddetails.add(code_89);

		HashMap<String, Object> code_90 = new HashMap<String, Object>();
		code_90.put("code_id", "4017");
		code_90.put("code_name", "delete report schedule");
		expecteddetails.add(code_90);

		HashMap<String, Object> code_91 = new HashMap<String, Object>();
		code_91.put("code_id", "5001");
		code_91.put("code_name", "create recovery point server");
		expecteddetails.add(code_91);

		HashMap<String, Object> code_92 = new HashMap<String, Object>();
		code_92.put("code_id", "5002");
		code_92.put("code_name", "update recovery point server");
		expecteddetails.add(code_92);

		HashMap<String, Object> code_93 = new HashMap<String, Object>();
		code_93.put("code_id", "5003");
		code_93.put("code_name", "delete recovery point server");
		expecteddetails.add(code_93);

		HashMap<String, Object> code_94 = new HashMap<String, Object>();
		code_94.put("code_id", "5004");
		code_94.put("code_name", "cloud direct recover");
		expecteddetails.add(code_94);

		HashMap<String, Object> code_95 = new HashMap<String, Object>();
		code_95.put("code_id", "6001");
		code_95.put("code_name", "create linux backup server");
		expecteddetails.add(code_95);

		HashMap<String, Object> code_96 = new HashMap<String, Object>();
		code_96.put("code_id", "6002");
		code_96.put("code_name", "update linux backup server");
		expecteddetails.add(code_96);

		HashMap<String, Object> code_97 = new HashMap<String, Object>();
		code_97.put("code_id", "6003");
		code_97.put("code_name", "delete linux backup server");
		expecteddetails.add(code_97);

		HashMap<String, Object> code_98 = new HashMap<String, Object>();
		code_98.put("code_id", "7001");
		code_98.put("code_name", "create data store");
		expecteddetails.add(code_98);

		HashMap<String, Object> code_99 = new HashMap<String, Object>();
		code_99.put("code_id", "7002");
		code_99.put("code_name", "update data store");
		expecteddetails.add(code_99);

		HashMap<String, Object> code_100 = new HashMap<String, Object>();
		code_100.put("code_id", "7003");
		code_100.put("code_name", "delete data store");
		expecteddetails.add(code_100);

		ArrayList<HashMap> expected = new ArrayList<HashMap>();

		HashMap<String, Object> temp = new HashMap<String, Object>();

		if (!(filter.isEmpty())) {

			int page_size = 0;
			String[] filterstr = filter.split(Pattern.quote("&"));

			String[] filt = filterstr[0].split(Pattern.quote("="));

			int page = Integer.parseInt(filt[1]);
			if (!(filterstr.length == 1)) {
				String[] filt_1 = filterstr[1].split(Pattern.quote("="));
				page_size = Integer.parseInt(filt_1[1]);
			} else {
				page_size = 20;
			}
			int code_num = (page - 1) * page_size;

			if (code_num > 120 && (code_num + page_size) > 121 && page == 7) {
				test.log(LogStatus.INFO, "page size is more than expeced, no audit details are available to display");
			}

			else {
				for (int k = code_num; k < code_num + page_size; k++) {

					temp = (HashMap<String, Object>) expecteddetails.get(k);

					expected.add(temp);
				}
			}

			return expected;
		}

		return expecteddetails;
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
		// ending test
		// endTest(logger) : It ends the current test and prepares to create
		// HTML report
		rep.endTest(test);
		// rep.flush();
	}

}
