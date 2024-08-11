package base.prepare;

import static invoker.SiteTestHelper.configSpogServerConnection;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import InvokerServer.GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

public abstract class TestApiBase {
	public SPOGServer				spogServer;
	public Org4SPOGServer			org4SPOGServer;
	public SPOGDestinationServer	spogDestinationServer;
	public UserSpogServer			userSpogServer;
	public GatewayServer			gatewayServer;
	public Policy4SPOGServer		policy4SPOGServer;
	public ExtentReports			rep;
	public String					csrAdminUserName	= "xiang_csr@arcserve.com";
	public String					csrAdminPassword	= "Caworld_2017";
	public String					baseURI				= "http://tccapi.arcserve.com";
	public String					port				= "8080";
	public SQLServerDb				bqdb1;
	public int						Nooftest;
	public long						creationTime;
	public String					BQName				= null;
	public String					runningMachine;
	public testcasescount			count1;
	public String					buildVersion;
	public ExtentTest				test;
	public String					author;
	public String					TestPrefix;

	public abstract void setAuthor();

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "logFolder", "buildVersion" })
	public void setSpogServerConnection(String baseURI, String port, String username, String password, String logFolder,
			String buildVersion) throws UnknownHostException {

		// baseURI = this.baseURI;
		// port = this.port;
		//
		// this.csrAdminUserName = username;
		// this.csrAdminPassword = password;

		this.buildVersion = buildVersion;

		setAuthor();
		TestPrefix = getClass().getSimpleName();

		initServerConnection();

		initTestRresultCounter(logFolder);

		// initTestEnv();
	}

	public void initServerConnection() {

		configSpogServerConnection(baseURI, port);

		spogServer = new SPOGServer(baseURI, port);
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);

		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);

		gatewayServer = new GatewayServer(baseURI, port);
		policy4SPOGServer = new Policy4SPOGServer(baseURI, port);
	}

	public void initTestRresultCounter(String logFolder) throws UnknownHostException {

		// this is for update portal
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("initializing data...");
		this.BQName = this.getClass().getSimpleName();
		this.runningMachine = java.net.InetAddress.getLocalHost().getHostName();
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		buildVersion = buildVersion + "_" + dateFormater.format(date);

		Nooftest = 0;
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		if (count1.isstarttimehit() == 0) {
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			// creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
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

	public void destroyOrg(String orgString) {

		spogServer = new SPOGServer(baseURI, port);

		org4SPOGServer = new Org4SPOGServer(baseURI, port);

		spogServer.userLogin(csrAdminUserName, csrAdminPassword);

		org4SPOGServer.setToken(spogServer.getJWTToken());

		int total_page = org4SPOGServer.getOrgPagesBySearchStringWithCsrLogin(orgString, spogServer.getJWTToken());

		if (total_page > 0) {
			for (int i = 1; i <= total_page; i++) {
				ArrayList<String> ret = org4SPOGServer.getOrgIdsBySearchStringWithCsrLogin(spogServer.getJWTToken(),
						orgString, i);
				if (ret != null) {
					if (ret.size() > 0) {
						for (int del = 0; del < ret.size(); del++) {
							org4SPOGServer.destroyOrganizationWithoutCheck(ret.get(del).toString());
						}
					}
				}
			}
		}
	}

	public void recycleVolumeInCDandDestroyOrg(String orgString) {

		String token = null;
		spogServer = new SPOGServer(baseURI, port);
		org4SPOGServer = new Org4SPOGServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);

		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		token = spogServer.getJWTToken();

		org4SPOGServer.setToken(spogServer.getJWTToken());
		int total_page = org4SPOGServer.getOrgPagesBySearchStringWithCsrLogin(spogServer.getJWTToken(), orgString);
		if (total_page > 0) {
			for (int i = 1; i <= total_page; i++) {
				ArrayList<String> ret = org4SPOGServer.getOrgIdsBySearchStringWithCsrLogin(spogServer.getJWTToken(),
						orgString, i);
				if (ret != null) {
					if (ret.size() > 0) {
						for (int del = 0; del < ret.size(); del++) {
							spogDestinationServer.recycleCloudVolumesAndDestroyOrg(token, ret.get(del));
						}
					}
				}
			}
		}
	}

	@AfterClass(alwaysRun = true)
	public void updatebd() {

		String orgHasString = this.getClass().getSimpleName();
		System.out.println("in father afterclass");
		System.out.println("class in father is:" + orgHasString);
		// destroyOrg(orgHasString);
		recycleVolumeInCDandDestroyOrg(orgHasString);
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

		rep.endTest(test);
	}

	@AfterTest
	public void aftertest() {

		rep.flush();
		System.out.println("in father aftertest");
	}

}
