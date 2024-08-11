package api.sources;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
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
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.volume_type;
import InvokerServer.GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class PostRecoveryForSourceTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGJobServer spogJobServer;
	private SPOGDestinationServer spogDestinationServer;
	private SPOGReportServer spogreportServer;
	private Policy4SPOGServer policy4SpogServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	String[] type = new String[12];
	private String job_Type = "backup";
	private String job_Status = "finished";
	private String job_Method = "full,incremental,resync,full,incremental,resync,full,incremental,resync,full,incremental,resync";
	public String JobSeverity = "warning,information,critical,warning,information,critical,warning,information,critical,warning,information,critical";
	public String JobStatus = "canceled,failed,finished,active,canceled,idle,waiting,crash,license_failed,backupjob_proc_exit,skipped,stop,active,finished";

	private String Direct_cloud_id;
	private String msp_cloud_account_id;
	private String root_cloud_id;
	private String destination_id_direct;
	private String destination_id_suborg;
	private String destination_id_rootsub;
	private String destination_id_submsp_sub;

	private ArrayList<HashMap> source_group = new ArrayList<HashMap>();
	private ArrayList<String> direct_source_ids = new ArrayList<String>();
	private ArrayList<HashMap> direct_sources = new ArrayList<HashMap>();
	private ArrayList<String> sub_source_ids = new ArrayList<String>();
	private ArrayList<HashMap> sub_sources = new ArrayList<HashMap>();
	String hypervisor_id = "8419aacb-b1a6-4859-ab8a-fee8350804bc";
	private ArrayList<String> root_sub_source_ids = new ArrayList<String>();
	private ArrayList<HashMap> root_sub_sources = new ArrayList<HashMap>();
	private ArrayList<String> submsp_sub_source_ids = new ArrayList<String>();
	private ArrayList<HashMap> submsp_sub_sources = new ArrayList<HashMap>();

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		spogDestinationServer=new SPOGDestinationServer(baseURI, port);
		policy4SpogServer=new Policy4SPOGServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		Nooftest = 0;
		bqdb1 = new SQLServerDb();
		String author = "Ramya.Nagepalli";
		count1 = new testcasescount();
		test = rep.startTest("beforeClass");
		test.assignAuthor("Ramya");
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

		// get cloud account for the direct organization
		Response response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		Direct_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get cloud account for the msp organization
		response = spogServer.getCloudAccounts(ti.normal_msp_org1_user1_token, "", test);
		msp_cloud_account_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get cloud account for the root msp organization
		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		root_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get destinations
		response = spogDestinationServer.getDestinations(ti.direct_org1_user1_token, "", test);
		destination_id_direct = response.then().extract().path("data[" + 0 + "].destination_id");

		response = spogDestinationServer.getDestinations(ti.normal_msp1_suborg1_user1_token, "", test);
		destination_id_suborg = response.then().extract().path("data[" + 0 + "].destination_id");

		response = spogDestinationServer.getDestinations(ti.root_msp1_suborg1_user1_token, "", test);
		destination_id_rootsub = response.then().extract().path("data[" + 0 + "].destination_id");

		response = spogDestinationServer.getDestinations(ti.msp1_submsp1_suborg1_user1_token, "", test);
		destination_id_submsp_sub = response.then().extract().path("data[" + 0 + "].destination_id");

		test.log(LogStatus.INFO, "create sources of all types for Direct org");
		createSources("direct", ti.direct_org1_id, Direct_cloud_id, ti.direct_org1_user1_token);

		test.log(LogStatus.INFO, "create sources of all types for sub org");
		createSources("sub_org", ti.normal_msp1_suborg1_id, msp_cloud_account_id, ti.normal_msp1_suborg1_user1_token);

		test.log(LogStatus.INFO, "create sources of all types for root sub org");
		createSources("root_sub", ti.root_msp1_suborg1_id, root_cloud_id, ti.root_msp1_suborg1_user1_token);

		test.log(LogStatus.INFO, "create sources of all types for submsp sub org");
		createSources("submsp_sub", ti.msp1_submsp1_sub_org1_id, root_cloud_id, ti.msp1_submsp1_suborg1_user1_token);

	}

	@SuppressWarnings("unchecked")
	public void createSources(String org_type, String org_id, String site_id, String token) {

		HashMap<String, Object> source_data = new HashMap<String, Object>();
		SourceType[] sourceTypes = { SourceType.machine, /* SourceType.agentless_vm */ };
		SourceProduct[] sourceProducts = { /* SourceProduct.udp, */ SourceProduct.cloud_direct };
		ProtectionStatus[] protectionStatus = {
				/* ProtectionStatus.protect, */ ProtectionStatus.unprotect/* , ProtectionStatus.partial_protect */ };
		ConnectionStatus[] connectionStatus = { ConnectionStatus.online, ConnectionStatus.offline };
		String[] os = { OSMajor.windows.name(), /* OSMajor.linux.name(), OSMajor.mac.name() */ };

		for (int i = 0; i < sourceTypes.length; i++) {
			for (int j = 0; j < sourceProducts.length; j++) {
				for (int j2 = 0; j2 < protectionStatus.length; j2++) {
					for (int k = 0; k < connectionStatus.length; k++) {
						for (int k2 = 0; k2 < os.length; k2++) {

							String srcName = spogServer.ReturnRandom("src");

							spogServer.setToken(token);
							Response response = spogServer.createSource(srcName, sourceTypes[i], sourceProducts[j],
									org_id, site_id, protectionStatus[j2], connectionStatus[k], os[k2], "", test);
							spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
							source_data = response.then().extract().path("data");

							String source_id = response.then().extract().path("data.source_id");

							if (org_type.contains("direct")) {
								direct_source_ids.add(source_id);
								direct_sources.add(source_data);
							} else if (org_type.contains("sub_org")) {
								sub_source_ids.add(source_id);
								sub_sources.add(source_data);
							} else if (org_type.contains("root_sub")) {
								root_sub_source_ids.add(source_id);
								root_sub_sources.add(source_data);
							} else {
								submsp_sub_source_ids.add(source_id);
								submsp_sub_sources.add(source_data);
							}
						}
					}
				}
			}
		}
	}

	@DataProvider(name = "sources_invalid")
	public final Object[][] sources_invalid() {
		return new Object[][] { { "Invalid scenario - missing token", "direct", "", direct_source_ids.get(1),
				"/ZettaMirror/zsystem5", direct_source_ids.get(1), "img", "ztst-2029.zetta.net",
				".snapshot/sync-age_2019-03-07_111002_UTC/zetta", "cloud_direct_image_backup", "C:\\rest1\\10GB.img",
				direct_source_ids.get(0), SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "Invalid scenario - invalid token", "direct", "abc", direct_source_ids.get(1),
						"/ZettaMirror/zsystem5", direct_source_ids.get(1), "img", "ztst-2029.zetta.net",
						".snapshot/sync-age_2019-03-07_111002_UTC/zetta", "cloud_direct_image_backup",
						"C:\\rest1\\10GB.img", direct_source_ids.get(0), SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },

				{ "Invalid scenario -(direct- msp) other org token", "direct", ti.normal_msp_org1_user1_token,
						direct_source_ids.get(1), "/ZettaMirror/zsystem5", direct_source_ids.get(1), "img",
						"ztst-2029.zetta.net", ".snapshot/sync-age_2019-03-07_111002_UTC/zetta",
						"cloud_direct_image_backup", "C:\\rest1\\10GB.img", direct_source_ids.get(0),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Invalid scenario -(direct- sub) other org token", "direct", ti.normal_msp1_suborg1_user1_token,
						direct_source_ids.get(1), "/ZettaMirror/zsystem5", direct_source_ids.get(1), "img",
						"ztst-2029.zetta.net", ".snapshot/sync-age_2019-03-07_111002_UTC/zetta",
						"cloud_direct_image_backup", "C:\\rest1\\10GB.img", direct_source_ids.get(0),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Invalid scenario -(sub- direct) other org token", "sub", ti.direct_org1_user1_token,
						sub_source_ids.get(1), "/ZettaMirror/zsystem5", sub_source_ids.get(1), "img",
						"ztst-2029.zetta.net", ".snapshot/sync-age_2019-03-07_111002_UTC/zetta",
						"cloud_direct_image_backup", "C:\\rest1\\10GB.img", sub_source_ids.get(0),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Invalid scenario -(sub- msp) other org token", "sub", ti.normal_msp_org2_user1_token,
						sub_source_ids.get(1), "/ZettaMirror/zsystem5", sub_source_ids.get(1), "img",
						"ztst-2029.zetta.net", ".snapshot/sync-age_2019-03-07_111002_UTC/zetta",
						"cloud_direct_image_backup", "C:\\rest1\\10GB.img", sub_source_ids.get(0),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "Invalid scenario - token", "sub", ti.normal_msp_org1_user1_token, UUID.randomUUID().toString(),
						"/ZettaMirror/zsystem5", sub_source_ids.get(1), "img", "ztst-2029.zetta.net",
						".snapshot/sync-age_2019-03-07_111002_UTC/zetta", "cloud_direct_image_backup",
						"C:\\rest1\\10GB.img", sub_source_ids.get(0), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.UNABLE_TO_FIND_THE_RESOURCE_WITH_ID },
				};
	}

	@Test(dataProvider = "sources_invalid")
	public void PostRecoveryForSource_invalid(String Testcase, String org_type, String validToken, String source_id,
			String from_path, String from_source_id, String image_format, String snapshot_host, String snapshot_path,
			String task_type, String to_path, String to_source_id, int ExpectedStatusCode,
			SpogMessageCode ErrorMessage) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + org_type);
		test.log(LogStatus.INFO, "get Sources of policy types with " + Testcase);

		spogServer.postSourcesStartrecovery(validToken, source_id, from_path, from_source_id, image_format,
				snapshot_host, snapshot_path, task_type, to_path, to_source_id, ExpectedStatusCode, ErrorMessage, test);
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


	@Test(dataProvider="org_info",dependsOnMethods="PostRecoveryForSource_invalid")
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
