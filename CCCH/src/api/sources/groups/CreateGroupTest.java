package api.sources.groups;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.SPOGServer;

import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

import io.restassured.response.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import dataPreparation.JsonPreparation;
import InvokerServer.Org4SPOGServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
public class CreateGroupTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private JsonPreparation jp;
	private TestOrgInfo ti;
	private ExtentTest test;
	protected ExtentReports rep;

	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;


	String csr_token, csr_org_id;
	String prefix = RandomStringUtils.randomAlphabetic(4);
	String userEmail =prefix+"eswari.sykam100@gmail.com";
	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine,
			String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		jp = new JsonPreparation();
		rep = ExtentManager.getInstance(getClass().getName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Sykam, Naga Malleswari";
		ti = new TestOrgInfo(spogServer, test);
		Nooftest = 0;
		// Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);

		BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());

		ti = new TestOrgInfo(spogServer, test);


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

	}



	@DataProvider(name = "create_SoucreGroup_Info")
	public final Object[][] createSourcegroupParams() {

		return new Object[][] {
			//200 Scenarios
			{ "Create sourcegroup for the direct organization",ti.direct_org1_user1_token,ti.direct_org1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.SUCCESS_POST,null},
			{ "Create sourcegroup for the direct organization with csr user token",ti.csr_token,ti.direct_org1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.SUCCESS_POST,null},
			{ "Create sourcegroup for the customer account of normal msp",ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.SUCCESS_POST,null},
			{ "Create sourcegroup for the customer account of normal msp user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.SUCCESS_POST,null},
			{ "Create sourcegroup for the customer account of normal msp with normal msp account admin  token",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.SUCCESS_POST,null},
			{ "Create sourcegroup for the customer account of normal msp with csr token",ti.csr_token,ti.normal_msp1_suborg1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.SUCCESS_POST,null},
			{ "Create sourcegroup for the customer account of root msp",ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.SUCCESS_POST,null},
			{ "Create sourcegroup for the customer account of root msp user token",ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,RandomStringUtils.randomAlphabetic(4)+"SOUCRE","SourceGroup",SpogConstants.SUCCESS_POST,null},
			{ "Create sourcegroup for the customer account of root msp with csr token",ti.csr_token,ti.root_msp1_suborg1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.SUCCESS_POST,null},
			{ "Create sourcegroup for the customer account of sub msp",ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_sub_org1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.SUCCESS_POST,null},
			{ "Create sourcegroup for the customer account of sub msp user token",ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_sub_org1_id,RandomStringUtils.randomAlphabetic(4)+"SOUCRE","SourceGroup",SpogConstants.SUCCESS_POST,null},
			{ "Create sourcegroup for the customer account of sub msp with normal msp account admin  token",ti.root_msp1_submsp1_account_admin_token,ti.msp1_submsp1_sub_org1_id,RandomStringUtils.randomAlphabetic(5)+"SOUCRE","SourceGroup",SpogConstants.SUCCESS_POST,null},
			{ "Create sourcegroup for the customer account of sub msp with csr token",ti.csr_token,ti.msp1_submsp1_sub_org1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.SUCCESS_POST,null},


			//400 Scenarios ...... Null as source group name
			{ "Create sourcegroup for the direct organization with invalid sourcegroup name",ti.direct_org1_user1_token,ti.direct_org1_id,"","SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the direct organization with csr user token with invalid sourcegroup name",ti.csr_token,ti.direct_org1_id,"","SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of normal msp with invalid sourcegroup name",ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_id,"","SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of normal msp user token with invalid sourcegroup name",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,"","SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of normal msp with normal msp account admin  token with invalid sourcegroup name",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,"","SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of normal msp with csr token with invalid sourcegroup name",ti.csr_token,ti.normal_msp1_suborg1_id,"","SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of root msp with invalid sourcegroup name",ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_id,"","SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of root msp user token with invalid sourcegroup name",ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,"","SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of root msp with normal msp account admin  token with invalid sourcegroup name",ti.normal_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_id,"","SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of root msp with csr token with invalid sourcegroup name",ti.csr_token,ti.root_msp1_suborg1_id,"","SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of sub msp with invalid sourcegroup name",ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_sub_org1_id,"","SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of root msp user token with invalid sourcegroup name",ti.root_msp1_submsp1_user1_token,ti.root_msp1_suborg1_id,"","SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of root msp with csr token with invalid sourcegroup name",ti.csr_token,ti.msp1_submsp1_sub_org1_id,"","SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},

			//Invalid Source group name
			{ "Create sourcegroup for the direct organization with invalid sourcegroup name",ti.direct_org1_user1_token,ti.direct_org1_id,null,"SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the direct organization with csr user token with invalid sourcegroup name",ti.csr_token,ti.direct_org1_id,null,"SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of normal msp with invalid sourcegroup name",ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_id,null,"SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of normal msp user token with invalid sourcegroup name",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,null,"SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of normal msp with normal msp account admin  token with invalid sourcegroup name",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,null,"SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of normal msp with csr token with invalid sourcegroup name",ti.csr_token,ti.normal_msp1_suborg1_id,null,"SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of root msp with invalid sourcegroup name",ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_id,null,"SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of root msp user token with invalid sourcegroup name",ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id,null,"SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of root msp with normal msp account admin  token with invalid sourcegroup name",ti.normal_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_id,null,"SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of root msp with csr token with invalid sourcegroup name",ti.csr_token,ti.root_msp1_suborg1_id,null,"SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of sub msp with invalid sourcegroup name",ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_sub_org1_id,null,"SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of root msp user token with invalid sourcegroup name",ti.root_msp1_submsp1_user1_token,ti.root_msp1_suborg1_id,null,"SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},
			{ "Create sourcegroup for the customer account of root msp with csr token with invalid sourcegroup name",ti.csr_token,ti.msp1_submsp1_sub_org1_id,null,"SourceGroup",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SOUCRE_GROUP_NAME_CANNOT_BLANK},

			//403 test cases
			{ "Create sourcegroup for the direct organization with another direct org user tken ",ti.direct_org2_user1_token,ti.direct_org1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Create sourcegroup for the direct organization with nomral msp org user tken ",ti.normal_msp_org1_user1_token,ti.direct_org1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Create sourcegroup for the direct organization with normal msp account admin org user tken ",ti.normal_msp_org1_msp_accountadmin1_token,ti.direct_org1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Create sourcegroup for the direct organization with customer accout of normal msp org user tken ",ti.normal_msp1_suborg1_user1_token,ti.direct_org1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Create sourcegroup for the direct organization with csr read only  user tken ",ti.csr_readonly_token,ti.direct_org1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Create sourcegroup for the direct organization with normal msp monitor org user tken ",ti.normal_msp1_suborg1_monitor_user1_token,ti.direct_org1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Create sourcegroup for the customer account of normal msp with another direct org user tken ",ti.direct_org2_user1_token,ti.normal_msp1_suborg1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Create sourcegroup for the  customer account of normal msp organization with nomral msp org user tken ",ti.normal_msp_org2_user1_token,ti.normal_msp1_suborg1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Create sourcegroup for the  customer account of normal msp organization with normal msp account admin org user tken ",ti.normal_msp_org2_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Create sourcegroup for the  customer account of normal msp organization with customer accout of normal msp org user tken ",ti.normal_msp2_suborg1_user1_token,ti.normal_msp1_suborg1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Create sourcegroup for the  customer account of normal msp organization with csr read only  user tken ",ti.csr_readonly_token,ti.normal_msp1_suborg1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Create sourcegroup for the  customer account of normal msp organization with normal msp monitor org user tken ",ti.normal_msp2_suborg1_monitor_user1_token,ti.normal_msp1_suborg1_id,RandomStringUtils.randomAlphabetic(3)+"SOUCRE","SourceGroup",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},

		};
	}

	@Test(dataProvider = "create_SoucreGroup_Info", enabled = true)
	public void createSoucreGroupValidSecnarioes(String testCase,
			String token,
			String ogranization_id,
			String sourcegroupName,
			String sourcegroupDescription,
			int expectedstatuccode,
			SpogMessageCode errormessage
			) {

		test = ExtentManager.getNewTest(testCase);

		test.log(LogStatus.INFO, "Compose info for create soucre group");
		HashMap<String, Object> composeSoucreGroupInfo = jp.composeSourceGroupsInfo(sourcegroupName, sourcegroupDescription);
		composeSoucreGroupInfo.put("organization_id", ogranization_id);
		
		test.log(LogStatus.INFO, "Create soucre group");
		spogServer.createSoucregroupWithCheck(token, composeSoucreGroupInfo, expectedstatuccode, errormessage, test);

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
		
	}


}
