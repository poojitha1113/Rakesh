package api.reports.datatransferreport;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.JobStatus;
import Constants.JobType4LatestJob;
import Constants.JobTypeConstants;
import Constants.Job_method;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class DataTransferReportDetailsTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private SPOGReportServer spogReportServer;
	private SPOGDestinationServer spogDestinationServer;
	private GatewayServer gatewayServer;
	private ExtentTest test;

	private TestOrgInfo ti;
	private String Direct_cloud_id;
	private String msp_cloud_account_id;
	private String root_cloud_id;
	private ArrayList<HashMap<String, Object>> direct_response = new ArrayList<>();
	private ArrayList<String> direct_sources = new ArrayList<>();
	private ArrayList<String> direct_sourceGroups = new ArrayList<>();
	private String direct_report_id;
	private ArrayList<HashMap<String, Object>> suborg_response = new ArrayList<>();
	private ArrayList<String> suborg_sources = new ArrayList<>();
	private ArrayList<String> suborg_sourceGroups = new ArrayList<>();
	private String sub_report_id;
	private String direct_destinationid;
	private String suborg_destinationid;
	private String rootsub_destinationid;
	private String submsp_sub_destinationid;

	private ArrayList<HashMap<String, Object>> rootsub_response = new ArrayList<>();
	private ArrayList<String> rootsub_sources = new ArrayList<>();
	private ArrayList<String> rootsub_sourceGroups = new ArrayList<>();
	private String rootsub_report_id;

	private ArrayList<HashMap<String, Object>> submsp_sub_response = new ArrayList<>();
	private ArrayList<String> submsp_sub_sources = new ArrayList<>();
	private ArrayList<String> submsp_sub_sourceGroups = new ArrayList<>();
	private String submsp_sub_report_id;
	private Policy4SPOGServer policy4SpogServer;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		spogReportServer = new SPOGReportServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		policy4SpogServer=new Policy4SPOGServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Ramya.Nagepalli";

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
		ti = new TestOrgInfo(spogServer, test);
		// get cloud account for the submsp_sub organization
		Response response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		Direct_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get cloud account for the msp organization
		response = spogServer.getCloudAccounts(ti.normal_msp_org1_user1_token, "", test);
		msp_cloud_account_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get cloud account for the root msp organization
		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		root_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		response = spogDestinationServer.getDestinations(ti.direct_org1_user1_token, "", test);
		direct_destinationid = response.then().extract().path("data[" + 0 + "].destination_id");

		response = spogDestinationServer.getDestinations(ti.normal_msp_org1_user1_token,
				"organization_id=" + ti.normal_msp1_suborg1_id, test);
		suborg_destinationid = response.then().extract().path("data[" + 0 + "].destination_id");

		response = spogDestinationServer.getDestinations(ti.root_msp_org1_user1_token,
				"organization_id=" + ti.root_msp1_suborg1_id, test);
		rootsub_destinationid = response.then().extract().path("data[" + 0 + "].destination_id");

		response = spogDestinationServer.getDestinations(ti.root_msp1_submsp1_user1_token,
				"organization_id=" + ti.root_msp1_submsp_org1_id, test);
		submsp_sub_destinationid = response.then().extract().path("data[" + 0 + "].destination_id");
	}

	@DataProvider(name = "postSource")
	public Object[][] postSource() {
		return new Object[][] {

				// dataprovider to create the sources in all organizations
				{ "direct", ti.direct_org1_user1_token, ti.direct_org1_id, Direct_cloud_id, direct_destinationid },
				{ "suborg", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_id, msp_cloud_account_id,
						suborg_destinationid },
				{ "rootsub", ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_id, root_cloud_id,
						rootsub_destinationid },
				{ "submsp_sub", ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_sub_org1_id, root_cloud_id,
						submsp_sub_destinationid },

		};
	}

	@Test(dataProvider = "postSource",dependsOnMethods="deleteResources")
	public void postData(String orgType, String validToken, String organization_id, String site_id,
			String destination_id) {

		for (int i = 0; i < 6; i++) {

			String source_id, source_group_id;
			String source_name = spogServer.ReturnRandom("source");
			String source_group_name = spogServer.ReturnRandom("group_name");
			String destination_name = spogServer.ReturnRandom("dest_rak");
			Response response;
			long start_time_ts = System.currentTimeMillis();
			long end_time_ts = System.currentTimeMillis() + 10000;
			String rps_id = UUID.randomUUID().toString();
			String policy_id = UUID.randomUUID().toString();

			if (i == 0) {
				source_name = "ConstantSourceName";
			}

			spogServer.setToken(validToken);
			test.log(LogStatus.INFO, "Creating source in the organization of type: " + orgType);
			source_id = spogServer.createSourceWithCheck(source_name, SourceType.machine, SourceProduct.cloud_direct,
					organization_id, site_id, ProtectionStatus.unprotect, ConnectionStatus.online,
					OSMajor.windows.name(), "", test);

			source_group_id = spogServer.createGroupWithCheck2(validToken, organization_id, source_group_name,
					spogServer.ReturnRandom("group_description"), test);
			spogServer.addSourcetoSourceGroupwithCheck(source_group_id, new String[] { source_id }, validToken,
					SpogConstants.SUCCESS_POST, null, test);

			response = gatewayServer.postJob(start_time_ts, end_time_ts, organization_id, source_id, source_id, rps_id,
					destination_id, policy_id, "backup", Job_method.full.toString(), JobStatus.finished.toString(),
					validToken, test);

			if (orgType.equalsIgnoreCase("direct")) {
				HashMap<String, Object> data = new HashMap<>();
				HashMap<String, Object> source = new HashMap<>();
				source.put("source_id", source_id);
				source.put("source_name", source_name);

				HashMap<String, Object> sourceGroup = new HashMap<>();
				sourceGroup.put("source_group_name", source_group_name);
				sourceGroup.put("source_group_id", source_group_id);

				ArrayList<HashMap<String, Object>> sourceGroupArray = new ArrayList<>();
				sourceGroupArray.add(sourceGroup);

				data.put("source", source);
				data.put("source_group", sourceGroupArray);
				direct_response.add(data);
				direct_sources.add(source_id);
				direct_sourceGroups.add(source_group_id);

				// create report under direct organization
				test.log(LogStatus.INFO, "create report schedule ");
				HashMap<String, Object> scheduleInfo = new HashMap<String, Object>();
				scheduleInfo = spogReportServer.composeReportScheduleInfo("report_name", "data_transfer", "last_7_days",
						System.currentTimeMillis(), System.currentTimeMillis(), "monthly", "", organization_id,
						"a@gmail.com,b@gmail.com", null, "all_sources");
				spogReportServer.createReportScheduleWithCheck(validToken, scheduleInfo,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

				ArrayList<HashMap<String, Object>> expectedReportsInfo = new ArrayList<HashMap<String, Object>>();
				expectedReportsInfo.add(scheduleInfo);

				response = spogReportServer.getReports(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
				direct_report_id = response.then().extract().path("data.get(0).report_id");

			} else if (orgType.equalsIgnoreCase("suborg")) {
				HashMap<String, Object> data = new HashMap<>();
				HashMap<String, Object> source = new HashMap<>();
				source.put("source_id", source_id);
				source.put("source_name", source_name);

				HashMap<String, Object> sourceGroup = new HashMap<>();
				sourceGroup.put("source_group_name", source_group_name);
				sourceGroup.put("source_group_id", source_group_id);

				ArrayList<HashMap<String, Object>> sourceGroupArray = new ArrayList<>();
				sourceGroupArray.add(sourceGroup);

				data.put("source", source);
				data.put("source_group", sourceGroupArray);
				suborg_response.add(data);
				suborg_sources.add(source_id);
				suborg_sourceGroups.add(source_group_id);

				// create report under suborg organization
				test.log(LogStatus.INFO, "create report schedule ");
				HashMap<String, Object> scheduleInfo = new HashMap<String, Object>();
				scheduleInfo = spogReportServer.composeReportScheduleInfo("report_name", "data_transfer", "last_7_days",
						System.currentTimeMillis(), System.currentTimeMillis(), "monthly", "", organization_id,
						"a@gmail.com,b@gmail.com", null, "all_sources");
				spogReportServer.createReportScheduleWithCheck(validToken, scheduleInfo,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

				ArrayList<HashMap<String, Object>> expectedReportsInfo = new ArrayList<HashMap<String, Object>>();
				expectedReportsInfo.add(scheduleInfo);

				response = spogReportServer.getReports(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
				sub_report_id = response.then().extract().path("data.get(0).report_id");

			} else if (orgType.equalsIgnoreCase("rootsub")) {
				HashMap<String, Object> data = new HashMap<>();
				HashMap<String, Object> source = new HashMap<>();
				source.put("source_id", source_id);
				source.put("source_name", source_name);

				HashMap<String, Object> sourceGroup = new HashMap<>();
				sourceGroup.put("source_group_name", source_group_name);
				sourceGroup.put("source_group_id", source_group_id);

				ArrayList<HashMap<String, Object>> sourceGroupArray = new ArrayList<>();
				sourceGroupArray.add(sourceGroup);

				data.put("source", source);
				data.put("source_group", sourceGroupArray);
				rootsub_response.add(data);
				rootsub_sources.add(source_id);
				rootsub_sourceGroups.add(source_group_id);

				// create report under suborg organization
				test.log(LogStatus.INFO, "create report schedule ");
				HashMap<String, Object> scheduleInfo = new HashMap<String, Object>();
				scheduleInfo = spogReportServer.composeReportScheduleInfo("report_name", "data_transfer", "last_7_days",
						System.currentTimeMillis(), System.currentTimeMillis(), "monthly", "", organization_id,
						"a@gmail.com,b@gmail.com", null, "all_sources");
				spogReportServer.createReportScheduleWithCheck(validToken, scheduleInfo,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

				ArrayList<HashMap<String, Object>> expectedReportsInfo = new ArrayList<HashMap<String, Object>>();
				expectedReportsInfo.add(scheduleInfo);

				response = spogReportServer.getReports(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
				rootsub_report_id = response.then().extract().path("data.get(0).report_id");

			} else if (orgType.equalsIgnoreCase("submsp_sub")) {
				HashMap<String, Object> data = new HashMap<>();
				HashMap<String, Object> source = new HashMap<>();
				source.put("source_id", source_id);
				source.put("source_name", source_name);

				HashMap<String, Object> sourceGroup = new HashMap<>();
				sourceGroup.put("source_group_name", source_group_name);
				sourceGroup.put("source_group_id", source_group_id);

				ArrayList<HashMap<String, Object>> sourceGroupArray = new ArrayList<>();
				sourceGroupArray.add(sourceGroup);

				data.put("source", source);
				data.put("source_group", sourceGroupArray);
				submsp_sub_response.add(data);
				submsp_sub_sources.add(source_id);
				submsp_sub_sourceGroups.add(source_group_id);

				// create report under suborg organization
				test.log(LogStatus.INFO, "create report schedule ");
				HashMap<String, Object> scheduleInfo = new HashMap<String, Object>();
				scheduleInfo = spogReportServer.composeReportScheduleInfo("report_name", "data_transfer", "last_7_days",
						System.currentTimeMillis(), System.currentTimeMillis(), "monthly", "", organization_id,
						"a@gmail.com,b@gmail.com", null, "all_sources");
				spogReportServer.createReportScheduleWithCheck(validToken, scheduleInfo,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

				ArrayList<HashMap<String, Object>> expectedReportsInfo = new ArrayList<HashMap<String, Object>>();
				expectedReportsInfo.add(scheduleInfo);

				response = spogReportServer.getReports(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
				submsp_sub_report_id = response.then().extract().path("data.get(0).report_id");

			}
		}
	}

	@DataProvider(name = "DTRvalid")
	public Object[][] DTRvalid() {
		return new Object[][] {
				// csr_readonly for direct org
				{ "Get Data transfer report details for direct organization no filter, sort & csr_readonly token",
						ti.csr_readonly_token, direct_response, "organization_id;=;" + ti.direct_org1_id, null, 1, 20,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for direct organization sort on source_name ascending & csr_readonly token",
						ti.csr_readonly_token, direct_response,
						"organization_id;=;" + ti.direct_org1_id + "," + "source_id;=;" + direct_sources.get(0) + "|"
								+ direct_sources.get(1),
						"source_name;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for direct organization sort on source_name descending & csr_readonly token",
						ti.csr_readonly_token, direct_response,
						"organization_id;=;" + ti.direct_org1_id + "," + "source_id;=;" + direct_sources.get(0) + "|"
								+ direct_sources.get(1),
						"source_name;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for direct organization sort on source_name ascending & csr_readonly token",
						ti.csr_readonly_token, direct_response,
						"organization_id;=;" + ti.direct_org1_id + "," + "group_id;=;" + direct_sourceGroups.get(0)
								+ "|" + direct_sourceGroups.get(1),
						"source_name;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for direct organization sort on source_name descending & csr_readonly token",
						ti.csr_readonly_token, direct_response,
						"organization_id;=;" + ti.direct_org1_id + "," + "group_id;=;" + direct_sourceGroups.get(2)
								+ "|" + direct_sourceGroups.get(1),
						"source_name;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for direct organization filter by group_id & csr_readonly token",
						ti.csr_readonly_token, direct_response,
						"organization_id;=;" + ti.direct_org1_id + "," + "group_id;=;" + direct_sourceGroups.get(0),
						null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for direct organization filter by source_id & csr_readonly token",
						ti.csr_readonly_token, direct_response,
						"organization_id;=;" + ti.direct_org1_id + "," + "source_id;=;" + direct_sources.get(0), null,
						1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for direct organization  filter by source_name & csr_readonly token",
						ti.csr_readonly_token, direct_response,
						"organization_id;=;" + ti.direct_org1_id + "," + "source_name;=;" + "ConstantSourceName", null,
						1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for direct organization  filter by group_id 1 | 2 & csr_readonly token",
						ti.csr_readonly_token, direct_response,
						"organization_id;=;" + ti.direct_org1_id + "," + "group_id;=;" + direct_sourceGroups.get(0)
								+ "|" + direct_sourceGroups.get(1),
						null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for direct organization filter by source_id 2 & csr_readonly token",
						ti.csr_readonly_token, direct_response,
						"organization_id;=;" + ti.direct_org1_id + "," + "source_id;=;" + direct_sources.get(1), null,
						1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for direct organization filter by report_id & csr_readonly token",
						ti.csr_readonly_token, direct_response,
						"organization_id;=;" + ti.direct_org1_id + "," + "report_id;=;" + direct_report_id, null, 1, 20,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				// csr_readonly for sub org
				{ "Get Data transfer report details for sub organization no filter, sort & csr_readonly token",
						ti.csr_readonly_token, suborg_response, "organization_id;=;" + ti.normal_msp1_suborg1_id, null,
						1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization sort on source_name ascending & csr_readonly token",
						ti.csr_readonly_token, suborg_response,
						"organization_id;=;" + ti.normal_msp1_suborg1_id + "," + "source_id;=;" + direct_sources.get(0)
								+ "|" + direct_sources.get(1),
						"source_name;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization sort on source_name descending & csr_readonly token",
						ti.csr_readonly_token, suborg_response,
						"organization_id;=;" + ti.normal_msp1_suborg1_id + "," + "source_id;=;" + direct_sources.get(0)
								+ "|" + direct_sources.get(1),
						"source_name;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization sort on source_name ascending & csr_readonly token",
						ti.csr_readonly_token, suborg_response,
						"organization_id;=;" + ti.normal_msp1_suborg1_id + "," + "group_id;=;"
								+ direct_sourceGroups.get(0) + "|" + direct_sourceGroups.get(1),
						"source_name;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization sort on source_name descending & csr_readonly token",
						ti.csr_readonly_token, suborg_response,
						"organization_id;=;" + ti.normal_msp1_suborg1_id + "," + "group_id;=;"
								+ direct_sourceGroups.get(2) + "|" + direct_sourceGroups.get(1),
						"source_name;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization filter by group_id & csr_readonly token",
						ti.csr_readonly_token, suborg_response,
						"organization_id;=;" + ti.normal_msp1_suborg1_id + "," + "group_id;=;"
								+ suborg_sourceGroups.get(0),
						null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization filter by source_id & csr_readonly token",
						ti.csr_readonly_token, suborg_response,
						"organization_id;=;" + ti.normal_msp1_suborg1_id + "," + "source_id;=;" + suborg_sources.get(0),
						null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization  filter by source_name & csr_readonly token",
						ti.csr_readonly_token, suborg_response,
						"organization_id;=;" + ti.normal_msp1_suborg1_id + "," + "source_name;=;"
								+ "ConstantSourceName",
						null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization  filter by group_id 1 | 2 & csr_readonly token",
						ti.csr_readonly_token, suborg_response,
						"organization_id;=;" + ti.normal_msp1_suborg1_id + "," + "group_id;=;"
								+ suborg_sourceGroups.get(0) + "|" + suborg_sourceGroups.get(1),
						null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization filter by source_id 2 & csr_readonly token",
						ti.csr_readonly_token, suborg_response,
						"organization_id;=;" + ti.normal_msp1_suborg1_id + "," + "source_id;=;" + suborg_sources.get(1),
						null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization filter by report_id & csr_readonly token",
						ti.csr_readonly_token, suborg_response,
						"organization_id;=;" + ti.normal_msp1_suborg1_id + "," + "report_id;=;" + sub_report_id, null,
						1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				// Direct organization
				{ "Get Data transfer report details for direct organization no filter, sort",
						ti.direct_org1_user1_token, direct_response, null, null, 1, 20,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for direct organization sort on source_name ascending",
						ti.direct_org1_user1_token, direct_response,
						"source_id;=;" + direct_sources.get(0) + "|" + direct_sources.get(1), "source_name;asc", 1, 20,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for direct organization sort on source_name descending",
						ti.direct_org1_user1_token, direct_response,
						"source_id;=;" + direct_sources.get(0) + "|" + direct_sources.get(1), "source_name;-asc", 1, 20,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for direct organization sort on source_name ascending",
						ti.direct_org1_user1_token, direct_response,
						"group_id;=;" + direct_sourceGroups.get(0) + "|" + direct_sourceGroups.get(1),
						"source_name;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for direct organization sort on source_name descending",
						ti.direct_org1_user1_token, direct_response,
						"group_id;=;" + direct_sourceGroups.get(2) + "|" + direct_sourceGroups.get(1),
						"source_name;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for direct organization filter by group_id",
						ti.direct_org1_user1_token, direct_response, "group_id;=;" + direct_sourceGroups.get(0), null,
						1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for direct organization filter by source_id",
						ti.direct_org1_user1_token, direct_response, "source_id;=;" + direct_sources.get(0), null, 1,
						20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for direct organization  filter by source_name",
						ti.direct_org1_user1_token, direct_response, "source_name;=;" + "ConstantSourceName", null, 1,
						20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for direct organization  filter by group_id 1 | 2",
						ti.direct_org1_user1_token, direct_response,
						"group_id;=;" + direct_sourceGroups.get(0) + "|" + direct_sourceGroups.get(1), null, 1, 20,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for direct organization filter by source_id 2",
						ti.direct_org1_user1_token, direct_response, "source_id;=;" + direct_sources.get(1), null, 1,
						20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for direct organization filter by report_id",
						ti.direct_org1_user1_token, direct_response, "report_id;=;" + direct_report_id, null, 1, 20,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				// Sub organization
				{ "Get Data transfer report details for sub organization no filter, sort",
						ti.normal_msp1_suborg1_user1_token, suborg_response, null, null, 1, 20,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization sort on source_name ascending",
						ti.normal_msp1_suborg1_user1_token, suborg_response,
						"source_id;=;" + suborg_sources.get(0) + "|" + suborg_sources.get(1), "source_name;asc", 1, 20,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization sort on source_name descending",
						ti.normal_msp1_suborg1_user1_token, suborg_response,
						"source_id;=;" + suborg_sources.get(0) + "|" + suborg_sources.get(1), "source_name;-asc", 1, 20,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization sort on source_name ascending",
						ti.normal_msp1_suborg1_user1_token, suborg_response,
						"group_id;=;" + suborg_sourceGroups.get(0) + "|" + suborg_sourceGroups.get(1),
						"source_name;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization sort on source_name descending",
						ti.normal_msp1_suborg1_user1_token, suborg_response,
						"group_id;=;" + suborg_sourceGroups.get(2) + "|" + suborg_sourceGroups.get(1),
						"source_name;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization filter by group_id",
						ti.normal_msp1_suborg1_user1_token, suborg_response, "group_id;=;" + suborg_sourceGroups.get(0),
						null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization filter by source_id",
						ti.normal_msp1_suborg1_user1_token, suborg_response, "source_id;=;" + suborg_sources.get(0),
						null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization  filter by source_name",
						ti.normal_msp1_suborg1_user1_token, suborg_response, "source_name;=;" + "ConstantSourceName",
						null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization  filter by group_id 1 | 2",
						ti.normal_msp1_suborg1_user1_token, suborg_response,
						"group_id;=;" + suborg_sourceGroups.get(0) + "|" + suborg_sourceGroups.get(1), null, 1, 20,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization filter by source_id 2",
						ti.normal_msp1_suborg1_user1_token, suborg_response, "source_id;=;" + suborg_sources.get(1),
						null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization filter by report_id",
						ti.normal_msp1_suborg1_user1_token, suborg_response, "report_id;=;" + sub_report_id, null, 1,
						20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				// Sub org with msp token
				{ "Get Data transfer report details for sub organization sort on source_name ascending",
						ti.normal_msp_org1_user1_token, suborg_response,
						"source_id;=;" + direct_sources.get(0) + "|" + direct_sources.get(1), "source_name;asc", 1, 20,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization sort on source_name descending",
						ti.normal_msp_org1_user1_token, suborg_response,
						"source_id;=;" + direct_sources.get(0) + "|" + direct_sources.get(1), "source_name;-asc", 1, 20,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization sort on source_name ascending",
						ti.normal_msp_org1_user1_token, suborg_response,
						"group_id;=;" + suborg_sourceGroups.get(0) + "|" + suborg_sourceGroups.get(1),
						"source_name;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization sort on source_name descending",
						ti.normal_msp_org1_user1_token, suborg_response,
						"group_id;=;" + suborg_sourceGroups.get(0) + "|" + suborg_sourceGroups.get(1),
						"source_name;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization filter by group_id",
						ti.normal_msp_org1_user1_token, suborg_response, "group_id;=;" + suborg_sourceGroups.get(0),
						null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization filter by source_id",
						ti.normal_msp_org1_user1_token, suborg_response, "source_id;=;" + suborg_sources.get(0), null,
						1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization  filter by source_name",
						ti.normal_msp_org1_user1_token, suborg_response, "source_name;=;" + "ConstantSourceName", null,
						1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization  filter by group_id 1 | 2",
						ti.normal_msp_org1_user1_token, suborg_response,
						"group_id;=;" + suborg_sourceGroups.get(0) + "|" + suborg_sourceGroups.get(1), null, 1, 20,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization filter by source_id 2",
						ti.normal_msp_org1_user1_token, suborg_response, "source_id;=;" + suborg_sources.get(1), null,
						1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization filter by report_id",
						ti.normal_msp_org1_user1_token, suborg_response, "report_id;=;" + sub_report_id, null, 1, 20,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				// suborg with msp_account_admin token
				{ "Get Data transfer report details for sub organization sort on source_name ascending",
						ti.normal_msp_org1_msp_accountadmin1_token, suborg_response,
						"source_id;=;" + suborg_sources.get(0) + "|" + suborg_sources.get(1), "source_name;asc", 1, 20,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization sort on source_name descending",
						ti.normal_msp_org1_msp_accountadmin1_token, suborg_response,
						"source_id;=;" + suborg_sources.get(0) + "|" + suborg_sources.get(1), "source_name;-asc", 1, 20,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization sort on source_name ascending",
						ti.normal_msp_org1_msp_accountadmin1_token, suborg_response,
						"group_id;=;" + direct_sourceGroups.get(0) + "|" + direct_sourceGroups.get(1),
						"source_name;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization sort on source_name descending",
						ti.normal_msp_org1_msp_accountadmin1_token, suborg_response,
						"group_id;=;" + direct_sourceGroups.get(0) + "|" + direct_sourceGroups.get(1),
						"source_name;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization filter by group_id",
						ti.normal_msp_org1_msp_accountadmin1_token, suborg_response,
						"group_id;=;" + suborg_sourceGroups.get(0), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE,
						null },
				{ "Get Data transfer report details for sub organization filter by source_id",
						ti.normal_msp_org1_msp_accountadmin1_token, suborg_response,
						"source_id;=;" + suborg_sources.get(0), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE,
						null },
				{ "Get Data transfer report details for sub organization  filter by source_name",
						ti.normal_msp_org1_msp_accountadmin1_token, suborg_response,
						"source_name;=;" + "ConstantSourceName", null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE,
						null },
				{ "Get Data transfer report details for sub organization  filter by group_id 1 | 2",
						ti.normal_msp_org1_msp_accountadmin1_token, suborg_response,
						"group_id;=;" + suborg_sourceGroups.get(0) + "|" + suborg_sourceGroups.get(1), null, 1, 20,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for sub organization filter by source_id 2",
						ti.normal_msp_org1_msp_accountadmin1_token, suborg_response,
						"source_id;=;" + suborg_sources.get(1), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE,
						null },
				{ "Get Data transfer report details for sub organization filter by report_id",
						ti.normal_msp_org1_msp_accountadmin1_token, suborg_response, "report_id;=;" + sub_report_id,
						null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				// 401
				{ "Get Data transfer report details with invalid token", ti.direct_org1_user1_token + 1,
						direct_response, null, null, 1, 20, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Get Data transfer report details with missing token", "", suborg_response, null, null, 1, 20,
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },

				// 3 tier cases

				{ "Get Data transfer report details for rootsub organization sort on source_name ascending",
						ti.root_msp_org1_user1_token, rootsub_response,
						"source_id;=;" + rootsub_sources.get(0) + "|" + rootsub_sources.get(1), "source_name;asc", 1,
						20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for rootsub organization sort on source_name descending",
						ti.root_msp_org1_user1_token, rootsub_response,
						"source_id;=;" + rootsub_sources.get(0) + "|" + rootsub_sources.get(1), "source_name;-asc", 1,
						20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for rootsub organization sort on source_name ascending",
						ti.root_msp_org1_user1_token, rootsub_response,
						"group_id;=;" + rootsub_sourceGroups.get(0) + "|" + rootsub_sourceGroups.get(1),
						"source_name;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for rootsub organization sort on source_name descending",
						ti.root_msp_org1_user1_token, rootsub_response,
						"group_id;=;" + rootsub_sourceGroups.get(0) + "|" + rootsub_sourceGroups.get(1),
						"source_name;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for rootsub organization filter by group_id",
						ti.root_msp_org1_user1_token, rootsub_response, "group_id;=;" + rootsub_sourceGroups.get(0),
						null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for rootsub organization filter by source_id",
						ti.root_msp_org1_user1_token, rootsub_response, "source_id;=;" + rootsub_sources.get(0), null,
						1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for rootsub organization  filter by source_name",
						ti.root_msp_org1_user1_token, rootsub_response, "source_name;=;" + "ConstantSourceName", null,
						1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for rootsub organization  filter by group_id 1 | 2",
						ti.root_msp_org1_user1_token, rootsub_response,
						"group_id;=;" + rootsub_sourceGroups.get(0) + "|" + rootsub_sourceGroups.get(1), null, 1, 20,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for rootsub organization filter by source_id 2",
						ti.root_msp_org1_user1_token, rootsub_response, "source_id;=;" + rootsub_sources.get(1), null,
						1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for rootsub organization filter by report_id",
						ti.root_msp_org1_user1_token, rootsub_response, "report_id;=;" + rootsub_report_id, null, 1, 20,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				// rootsub with msp_account_admin token
				{ "Get Data transfer report details for rootsub organization sort on source_name ascending",
						ti.root_msp_org1_msp_accountadmin1_token, rootsub_response,
						"source_id;=;" + rootsub_sources.get(0) + "|" + rootsub_sources.get(1), "source_name;asc", 1,
						20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				{ "Get Data transfer report details for submsp_sub organization sort on source_name ascending",
						ti.msp1_submsp1_suborg1_user1_token, submsp_sub_response,
						"source_id;=;" + submsp_sub_sources.get(0) + "|" + submsp_sub_sources.get(1), "source_name;asc",
						1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for submsp_sub organization sort on source_name descending",
						ti.msp1_submsp1_suborg1_user1_token, submsp_sub_response,
						"source_id;=;" + submsp_sub_sources.get(0) + "|" + submsp_sub_sources.get(1),
						"source_name;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for submsp_sub organization sort on source_name ascending",
						ti.msp1_submsp1_suborg1_user1_token, submsp_sub_response,
						"group_id;=;" + submsp_sub_sourceGroups.get(0) + "|" + submsp_sub_sourceGroups.get(1),
						"source_name;asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for submsp_sub organization sort on source_name descending",
						ti.msp1_submsp1_suborg1_user1_token, submsp_sub_response,
						"group_id;=;" + submsp_sub_sourceGroups.get(0) + "|" + submsp_sub_sourceGroups.get(1),
						"source_name;-asc", 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for submsp_sub organization filter by group_id",
						ti.msp1_submsp1_suborg1_user1_token, submsp_sub_response,
						"group_id;=;" + submsp_sub_sourceGroups.get(0), null, 1, 20,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for submsp_sub organization filter by source_id",
						ti.msp1_submsp1_suborg1_user1_token, submsp_sub_response,
						"source_id;=;" + submsp_sub_sources.get(0), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE,
						null },
				{ "Get Data transfer report details for submsp_sub organization  filter by source_name",
						ti.root_msp1_submsp1_user1_token, submsp_sub_response, "source_name;=;" + "ConstantSourceName",
						null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for submsp_sub organization  filter by group_id 1 | 2",
						ti.root_msp1_submsp1_user1_token, submsp_sub_response,
						"group_id;=;" + submsp_sub_sourceGroups.get(0) + "|" + submsp_sub_sourceGroups.get(1), null, 1,
						20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data transfer report details for submsp_sub organization filter by source_id 2",
						ti.root_msp1_submsp1_user1_token, submsp_sub_response,
						"source_id;=;" + submsp_sub_sources.get(1), null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE,
						null },
				{ "Get Data transfer report details for submsp_sub organization filter by report_id",
						ti.root_msp1_submsp1_user1_token, submsp_sub_response, "report_id;=;" + submsp_sub_report_id,
						null, 1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				// submsp_sub with msp_account_admin token
				{ "Get Data transfer report details for submsp_sub organization sort on source_name ascending",
						ti.root_msp1_submsp1_account_admin_token, submsp_sub_response,
						"source_id;=;" + submsp_sub_sources.get(0) + "|" + submsp_sub_sources.get(1), "source_name;asc",
						1, 20, SpogConstants.SUCCESS_GET_PUT_DELETE, null },

		};
	}

	@Test(dataProvider = "DTRvalid", dependsOnMethods = "postData")
	public void dataTransferReportDetailsValid(String caseType, String token,
			ArrayList<HashMap<String, Object>> expectedResponse, String filterStr, String sortStr, int page,
			int page_size, int expectedStatusCode, SpogMessageCode expectedErrorMessage) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

		test.log(LogStatus.INFO, caseType);
		spogReportServer.getDataTransferReportDetailsWithCheck(token, expectedResponse, filterStr, sortStr, page,
				page_size, expectedStatusCode, expectedErrorMessage, test);

		test.log(LogStatus.INFO, caseType + " Export CSV");
		spogReportServer.getDataTransferReportDetailsExportCSV(token, expectedStatusCode, expectedErrorMessage, test);
	}
	
	

	@DataProvider(name = "org_info")
	public final Object[][] org_info() {
		return new Object[][] {

				{ ti.direct_org1_id, ti.direct_org1_user1_token },
				{ ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token },
				{ ti.root_msp_org1_id, ti.root_msp_org1_user1_token },
				{ ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token },
				{ ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token },
				{ ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token },

		};
	}

	@Test(dataProvider = "org_info")
	public void deleteResources(String org_id, String validToken) {

		policy4SpogServer.setToken(validToken);
		Response response = policy4SpogServer.getPolicies(null);
		ArrayList<String> policies = new ArrayList<>();
		policies = response.then().extract().path("data.policy_id");

		if (!policies.isEmpty()) {
			policies.stream().forEach(policy -> {
				policy4SpogServer.deletePolicybyPolicyId(ti.csr_token, policy);
			});
		}

		spogServer.setToken(validToken);
		response = spogServer.getSources("", "", 1, 20, true, test);

		ArrayList<String> sources = new ArrayList<>();
		sources = response.then().extract().path("data.source_id");
		if (!sources.isEmpty()) {
			sources.stream().forEach(source -> {
				spogServer.deleteSourceByID(source, test);
			});
		}
		test.log(LogStatus.INFO, "get reports");
		response = spogReportServer.getReports(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		ArrayList<String> reports = new ArrayList<>();
		reports = response.then().extract().path("data.report_id");

		if (!reports.isEmpty())
			reports.stream().forEach(report -> {
				spogReportServer.deleteReportsByIdWithCheck(validToken, report, SpogConstants.SUCCESS_GET_PUT_DELETE,
						null, test);
			});
	}


	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();
			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());

		} else if (result.getStatus() == ITestResult.SKIP) {
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
			count1.setskippedcount();
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();

		}
		rep.endTest(test);
		rep.flush();
	}
}
