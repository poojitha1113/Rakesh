package api.sources.groups;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

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

import Constants.ConnectionStatus;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Log4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.GatewayServer.siteType;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import invoker.SiteTestHelper;
import io.restassured.response.Response;

public class RemovesourcefromsourcegroupTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private GatewayServer gatewayServer;
	private Log4SPOGServer spogLogServer;
	private TestOrgInfo ti;

	//public int Nooftest;
	//private ExtentReports rep;
	private ExtentTest test;

	ArrayList<String>normalmspsites=new ArrayList<String>();
	ArrayList<String>rootmspsites=new ArrayList<String>();
	ArrayList<String>submspsites=new ArrayList<String>();
	//Information related to the sites
	ArrayList<String>sites=new ArrayList<String>();
	ArrayList<String>directsites=new ArrayList<String>();
	ArrayList<String>mspsites=new ArrayList<String>();

	LocalDate date = LocalDate.now();  
	LocalDate yesterday = date.minusDays(1);  
	LocalDate tomorrow = yesterday.plusDays(2);
	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;

	String direct_site_id;
	String direct_site_token;
	String sourceName;
	String direct_server_id;
	String normal_msp_site_id,normal_msp_site_token;

	String root_msp_site_id,root_msp_site_token,sub_msp_site_id,sub_msp_site_token;
	String root_subOrg_Server_id,submsp_subOrg_Server_id;
	String normal_subOrg_Server_id;

	private String  org_model_prefix=this.getClass().getSimpleName();
	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) throws UnknownHostException {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogLogServer = new Log4SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("RemovesourcefromsourcegroupTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Sykam.Malleswari";
		test = rep.startTest("beforeClass");
		Nooftest=0;
		//Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);
		ti = new TestOrgInfo(spogServer, test);	
		BQName=this.getClass().getSimpleName();
		this.runningMachine=runningMachine;
		System.out.println("The value of hit is "+count1.isstarttimehit());


		if( count1.isstarttimehit( ) == 0 ) 
		{
			System.out.println("into creation time");
			creationTime=System.currentTimeMillis();
			count1.setcreationtime(creationTime);

			// creationTime = System.currentTimeMillis();
			try
			{
				bqdb1.updateTable(BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
			} 
			catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@DataProvider(name = "addSourcestoSoucreGroup")
	public final Object[][] getOrganizationAndUserInfo_valid1() {
		return new Object[][] {
			//Direct Org
			{"Remove source from source group the source group of direct organziation with valid token",ti.direct_org1_user1_token,ti.direct_org1_id},

			//Customer account of Normal SUB Msp Organization
			{"Remove source from source group of direct organziation with valid token",ti.normal_msp1_suborg1_user1_token ,ti.normal_msp1_suborg1_id},
			{"Remove source from source group of direct organziation with valid token",ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_id },
			{"Remove source from source group of direct organziation with valid token",ti.normal_msp_org1_msp_accountadmin1_token ,ti.normal_msp1_suborg1_id},

			//Customer account of Root MSP Organization
			{"Remove source from source group of direct organziation with valid token",ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_id },
			{"Remove source from source group of direct organziation with valid token",ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_id },
			{"Remove source from source group of direct organziation with valid token",ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_id},

			//Customer account of sub msp Organization
			{"Remove source from source group of direct organziation with valid token",ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_sub_org1_id },
			{"Remove source from source group of direct organziation with valid token",ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_sub_org1_id},
			{"Remove source from source group of direct organziation with valid token",ti.root_msp1_submsp1_account_admin_token,ti.msp1_submsp1_sub_org1_id},


		};

	}

	@Test (dataProvider="addSourcestoSoucreGroup")	
	public void removeSoucreFromGroupWithValidToken(String testcase,
			String validtoken,
			String organization_id
			){
		test=ExtentManager.getNewTest(testcase);

		String source_name = UUID.randomUUID().toString();
		int noofsources=1;
		int i=0;
		String[] arrayofsourcenodes= new String[noofsources];
		/*//created site for the direct user 
		test.log(LogStatus.INFO, "Create a site/register/login to the site");
		site_id = gatewayServer.createsite_register_login(organization_id, Validtoken, user_id, "ts", "1.0.0", spogServer, test);

		 */

		spogServer.setToken(validtoken);

		//creating sources for the direct user
		test.log(LogStatus.INFO, "Adding sources to direct org");
		for(i=0;i<noofsources;i++) {
			spogServer.setToken(validtoken);
			arrayofsourcenodes[i] = spogServer.createSourceWithCheck(source_name+"_"+i+i, SourceType.machine, SourceProduct.cloud_direct, organization_id, null,ProtectionStatus.unprotect, ConnectionStatus.online, "windows", "SQLSERVER",test);
		}

		//creating source to the group
		test.log(LogStatus.INFO, "Create a source group");
		String group_id = spogServer.createGroupWithCheck(organization_id,source_name+i, testcase+i+i,test);

		test.log(LogStatus.INFO, "Add the sources to the sourcegroup "+group_id);
		spogServer.addSourcetoSourceGroupwithCheck(group_id, arrayofsourcenodes, validtoken, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test);

		//DELETE THE SOURCE FORM THE SOURCE
		test.log(LogStatus.INFO, "Delete the source from the source group");
		spogServer.deleteSourcefromSourceGroupwithCheck(group_id, arrayofsourcenodes[i-1], validtoken, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);


	}


	@DataProvider(name = "organizationAndUserInfo_2")
	public final Object[][] getOrganizationAndUserInfo_valid2() {
		return new Object[][] {

			//Direct Org
			{"Remove source from source group of direct organziation with another Direct Org User token",ti.direct_org1_user1_token ,ti.direct_org2_user1_token,ti.direct_org1_id},
			{"Remove source from source group of direct organziation with  Normal Msp Usertoken",ti.direct_org1_user1_token ,ti.normal_msp_org1_user1_token,ti.direct_org1_id},
			{"Remove source from source group of direct organziation with Normal msp account admin user token",ti.direct_org1_user1_token ,ti.normal_msp_org1_msp_accountadmin1_token,ti.direct_org1_id},
			{"Remove source from source group of direct organziation with Customer account of normal MSP ",ti.direct_org1_user1_token ,ti.normal_msp1_suborg1_user1_token,ti.direct_org1_id},
			{"Remove source from source group of direct organziation with Root MSP Organization user token",ti.direct_org1_user1_token ,ti.root_msp_org1_user1_token,ti.direct_org1_id},
			{"Remove source from source group of direct organziation with Root msp account admin user token",ti.direct_org1_user1_token ,ti.root_msp_org1_msp_accountadmin1_token,ti.direct_org1_id},
			{"Remove source from source group of direct organziation with Customer account of Root msp Organiaztion",ti.direct_org1_user1_token ,ti.root_msp1_suborg1_user1_token,ti.direct_org1_id},
			{"Remove source from source group of direct organziation with  Sub Msp user token",ti.direct_org1_user1_token ,ti.root_msp1_submsp1_user1_token,ti.direct_org1_id},
			{"Remove source from source group of direct organziation with Sub msp accoutn admin token",ti.direct_org1_user1_token ,ti.root_msp1_submsp1_account_admin_token,ti.direct_org1_id},
			{"Remove source from source group of direct organziation with Customer account of Sub Msp Organization",ti.direct_org1_user1_token , ti.msp1_submsp1_suborg1_user1_token,ti.direct_org1_id},


			//Customer account of Normal SUB msp Organization 
			{"Remove source from source group of Customer account of Normal msp with Direct user token",ti.normal_msp1_suborg1_user1_token ,ti.direct_org1_user1_token,ti.normal_msp1_suborg1_id},
			{"Remove source from source group of Customer account of Normal msp with another Normal MSP user token",ti.normal_msp1_suborg1_user1_token ,ti.normal_msp2_suborg1_user1_token,ti.normal_msp1_suborg1_id},
			{"Remove source from source group of Customer account of Normal msp with another  Normal MSP account admin user token",ti.normal_msp1_suborg1_user1_token ,ti.normal_msp_org2_msp_accountadmin1_token,ti.normal_msp1_suborg1_id},
			{"Remove source from source group of Customer account of Normal msp with another  Normal MSP  Customer account user token",ti.normal_msp1_suborg1_user1_token ,ti.normal_msp2_suborg1_user1_token,ti.normal_msp1_suborg1_id},
			{"Remove source from source group of Customer account of Normal msp with Root MSP user token",ti.normal_msp1_suborg1_user1_token ,ti.root_msp_org1_user1_token,ti.normal_msp1_suborg1_id},
			{"Remove source from source group of Customer account of Normal msp with another  Normal MSP account admin user token",ti.normal_msp1_suborg1_user1_token ,ti.root_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id},
			{"Remove source from source group of Customer account of Normal msp with another  Normal MSP  Customer account user token",ti.normal_msp1_suborg1_user1_token ,ti.root_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_id},
			{"Remove source from source group of Customer account of Normal msp with sub MSP user token",ti.normal_msp1_suborg1_user1_token ,ti.root_msp1_submsp1_user1_token,ti.normal_msp1_suborg1_id},
			{"Remove source from source group of Customer account of Normal msp with another  sub MSP account admin user token",ti.normal_msp1_suborg1_user1_token ,ti.root_msp1_submsp1_account_admin_token,ti.normal_msp1_suborg1_id},
			{"Remove source from source group of Customer account of Normal msp with another  sub MSP  Customer account user token",ti.normal_msp1_suborg1_user1_token ,ti.msp1_submsp1_suborg1_user1_token,ti.normal_msp1_suborg1_id},

			//Customer account of Root MSP Organization
			{"Remove source from source group of Customer account of Root MSP  organziation with Direct User  token",ti.root_msp1_suborg1_user1_token ,ti.direct_org1_user1_token,ti.root_msp1_suborg1_id},
			{"Remove source from source group of Customer account of  Root  MSP  organziation with Normal MSP  User  token",ti.root_msp1_suborg1_user1_token ,ti.normal_msp2_suborg1_user1_token,ti.root_msp1_suborg1_id},
			{"Remove source from source group of Customer account of  Root  MSP  organziation with Normal MSP account admin  User  token",ti.root_msp1_suborg1_user1_token ,ti.normal_msp_org2_msp_accountadmin1_token,ti.root_msp1_suborg1_id},
			{"Remove source from source group of Customer account of  Root  MSP  organziation with Customer account of Normal MSP User",ti.root_msp1_suborg1_user1_token ,ti.normal_msp1_suborg1_user1_token,ti.root_msp1_suborg1_id},
			{"Remove source from source group of Customer account of  Root  MSP  organziation with Root MSP User",ti.root_msp1_suborg1_user1_token ,ti.root_msp_org2_user1_token,ti.root_msp1_suborg1_id},
			{"Remove source from source group of Customer account of  Root  MSP  organziation with Root MSP account admin User",ti.root_msp1_suborg1_user1_token ,ti.root_msp_org2_msp_accountadmin1_token,ti.root_msp1_suborg1_id},
			{"Remove source from source group of Customer account of  Root  MSP  organziation with Customer account of  Root MSP account admin User",ti.root_msp1_suborg1_user1_token ,ti.root_msp2_suborg1_user1_token,ti.root_msp1_suborg1_id},
			{"Remove source from source group of Customer account of  Root  MSP  organziation with Root MSP User",ti.root_msp1_suborg1_user1_token ,ti.root_msp1_submsp1_user1_token,ti.root_msp1_suborg1_id},
			{"Remove source from source group of Customer account of  Root  MSP  organziation with Root MSP account admin User",ti.root_msp1_suborg1_user1_token ,ti.root_msp1_submsp1_account_admin_token,ti.root_msp1_suborg1_id},
			{"Remove source from source group of Customer account of  Root  MSP  organziation with Customer account of  Root MSP account admin User",ti.root_msp1_suborg1_user1_token ,ti.msp1_submsp1_suborg1_user1_token,ti.root_msp1_suborg1_id},

			//Customer account of SUB Msp Organization

			{"Remove source from source group of Customer account of Sub Msp organziation with Direct User token",ti.msp1_submsp1_suborg1_user1_token ,ti.direct_org1_user1_token,ti.msp1_submsp1_sub_org1_id},
			{"Remove source from source group of Customer account of Sub Msp organziation with Normal MSP  User token",ti.msp1_submsp1_suborg1_user1_token ,ti.normal_msp2_suborg1_user1_token,ti.msp1_submsp1_sub_org1_id},
			{"Remove source from source group of Customer account of Sub Msp organziation with Normal MSP account admin User token",ti.msp1_submsp1_suborg1_user1_token ,ti.normal_msp_org2_msp_accountadmin1_token,ti.msp1_submsp1_sub_org1_id},
			{"Remove source from source group of Customer account of Sub Msp organziation with Customer account of MSP account admin User token",ti.msp1_submsp1_suborg1_user1_token ,ti.normal_msp1_suborg1_user1_token,ti.msp1_submsp1_sub_org1_id},
			{"Remove source from source group of Customer account of Sub Msp organziation with Root MSP  User token",ti.msp1_submsp1_suborg1_user1_token ,ti.normal_msp2_suborg1_user1_token,ti.msp1_submsp1_sub_org1_id},
			{"Remove source from source group of Customer account of Sub Msp organziation with Root MSP account admin User token",ti.msp1_submsp1_suborg1_user1_token ,ti.root_msp_org2_msp_accountadmin1_token,ti.msp1_submsp1_sub_org1_id},
			{"Remove source from source group of Customer account of Sub Msp organziation with Customer account of Root MSP User token",ti.msp1_submsp1_suborg1_user1_token ,ti.root_msp2_suborg1_user1_token,ti.msp1_submsp1_sub_org1_id},
			{"Remove source from source group of Customer account of Sub Msp organziation with another SUB  MSP  User token",ti.msp1_submsp1_suborg1_user1_token ,ti.msp1_submsp2_suborg1_user1_token,ti.msp1_submsp1_sub_org1_id},
			{"Remove source from source group of Customer account of Sub Msp organziation with another SUB MSP account admin User token",ti.msp1_submsp1_suborg1_user1_token ,ti.root_msp2_submsp1_account_admin_token,ti.msp1_submsp1_sub_org1_id},
			{"Remove source from source group of Customer account of Sub Msp organziatiwith Customer account of another SUB MSP User token",ti.msp1_submsp1_suborg1_user1_token ,ti.msp2_submsp1_suborg1_user1_token,ti.msp1_submsp1_sub_org1_id},



		};

	}

	@Test (dataProvider="organizationAndUserInfo_2")	
	public void removeSoucrefromSoucreGroupWithanotherorgtoken(String testcase,
			String validtoken,
			String anothertoken,
			String organization_id
			){
		test=ExtentManager.getNewTest(testcase);

		String source_name = UUID.randomUUID().toString();
		int noofsources=1;
		int i=0;
		String[] arrayofsourcenodes= new String[noofsources];
		//creating sources for the direct user
		test.log(LogStatus.INFO, "Adding sources to direct org");
		for(i=0;i<noofsources;i++) {
			spogServer.setToken(validtoken);
			arrayofsourcenodes[i] = spogServer.createSourceWithCheck(source_name+"_"+i, SourceType.machine, SourceProduct.cloud_direct, organization_id, null,ProtectionStatus.unprotect, ConnectionStatus.online, "windows", "SQLSERVER",test);
		}

		//creating source to the group
		spogServer.setToken(validtoken);
		test.log(LogStatus.INFO, "Create a source group");
		String group_id = spogServer.createGroupWithCheck(organization_id,i+source_name+i, testcase+i+i,test);

		test.log(LogStatus.INFO, "Add the sources to the sourcegroup "+group_id);
		spogServer.addSourcetoSourceGroupwithCheck(group_id, arrayofsourcenodes, validtoken, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test);


		//DELETE THE SOURCE FORM THE SOURCE
		test.log(LogStatus.INFO, "Delete the source from the source group");
		spogServer.deleteSourcefromSourceGroupwithCheck(group_id, arrayofsourcenodes[i-1], anothertoken, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);


	}

	@Test (dataProvider="addSourcestoSoucreGroup")	
	public void RemovesoucresfromthesoucregroupWithValidToken_again_try_to_delet_the_deleted_sources(String testcase,
			String validtoken,
			String organization_id){
		test=ExtentManager.getNewTest(testcase);

		String source_name = UUID.randomUUID().toString();
		int noofsources=1;
		int i=0;
		String[] arrayofsourcenodes= new String[noofsources];
		/*//created site for the direct user 
		test.log(LogStatus.INFO, "Create a site/register/login to the site");
		site_id = gatewayServer.createsite_register_login(organization_id, Validtoken, user_id, "ts", "1.0.0", spogServer, test);

		 */

		spogServer.setToken(validtoken);

		//creating sources for the direct user
		test.log(LogStatus.INFO, "Adding sources to direct org");
		for(i=0;i<noofsources;i++) {
			arrayofsourcenodes[i] = spogServer.createSourceWithCheck(source_name+"_"+i, SourceType.machine, SourceProduct.cloud_direct, organization_id,null,ProtectionStatus.unprotect, ConnectionStatus.online, "windows", "SQLSERVER",test);
		}

		//creating source to the group
		test.log(LogStatus.INFO, "Create a source group");
		String group_id = spogServer.createGroupWithCheck(organization_id, source_name+i+i, testcase+i+i,test);

		test.log(LogStatus.INFO, "Add the sources to the sourcegroup "+group_id);
		spogServer.addSourcetoSourceGroupwithCheck(group_id, arrayofsourcenodes, validtoken, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test);


		//DELETE THE SOURCE FORM THE SOURCE
		test.log(LogStatus.INFO, "Delete the source from the source group");
		spogServer.deleteSourcefromSourceGroupwithCheck(group_id, arrayofsourcenodes[i-1], validtoken, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		//DELETE THE SOURCE FORM THE SOURCE
		test.log(LogStatus.INFO, "Delete the source from the source group");
		spogServer.deleteSourcefromSourceGroupwithCheck(group_id, arrayofsourcenodes[i-1], validtoken, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.SOURCE_GROUP_NO_MAP_SOURCE, test);

	}
	@Test (dataProvider="addSourcestoSoucreGroup")	
	public void RemovesoucresfromthesoucregroupWithvalidtoken_deletd_user(String testcase,
			String validtoken,
			String organization_id
			){
		test=ExtentManager.getNewTest(testcase);

		String source_name = UUID.randomUUID().toString();
		int noofsources=1;
		int i=0;
		String[] arrayofsourcenodes= new String[noofsources];

		spogServer.setToken(validtoken);

		//creating sources for the direct user
		test.log(LogStatus.INFO, "Adding sources to direct org");
		for(i=0;i<noofsources;i++) {
			arrayofsourcenodes[i] = spogServer.createSourceWithCheck(source_name+"_"+i, SourceType.machine, SourceProduct.cloud_direct, organization_id, null,ProtectionStatus.unprotect, ConnectionStatus.online, "windows", "SQLSERVER",test);
		}
		test.log(LogStatus.INFO, "Performing the operations to delete the left out filters, the user and orginzation by logging in as csr admin");	

		//creating source to the group
		test.log(LogStatus.INFO, "Create a source group");
		String group_id = spogServer.createGroupWithCheck(organization_id,source_name+i+i, testcase+i+i,test);

		spogServer.setToken(ti.csr_token);
		test.log(LogStatus.INFO, "Add the sources to the sourcegroup "+group_id);
		spogServer.addSourcetoSourceGroupwithCheck(group_id, arrayofsourcenodes, ti.csr_token, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test);


		spogServer.deleteGroup(group_id, test);

		//DELETE THE SOURCE FORM THE SOURCE
		test.log(LogStatus.INFO, "Delete the source from the source group");
		spogServer.deleteSourcefromSourceGroupwithCheck(group_id, arrayofsourcenodes[i-1], validtoken,SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.SOURCE_GROUP_NOT_EXIST, test);
	}

	@Test (dataProvider="addSourcestoSoucreGroup")	
	public void RemoveSourceformsoucregroupWithValidToken_radom_uuid_is_the_source_group(String testcase,
			String validtoken,
			String organization_id
			){
		test=ExtentManager.getNewTest(testcase);

		String source_name = UUID.randomUUID().toString();
		int noofsources=1;
		int i=0;
		String[] arrayofsourcenodes= new String[noofsources];
		//creating sources for the direct user
		test.log(LogStatus.INFO, "Adding sources to direct org");
		for(i=0;i<noofsources;i++) {
			spogServer.setToken(validtoken);
			arrayofsourcenodes[i] = spogServer.createSourceWithCheck(source_name+"_"+i, SourceType.machine, SourceProduct.cloud_direct, organization_id, null,ProtectionStatus.unprotect, ConnectionStatus.online, "windows", "SQLSERVER",test);
		}

		String group_id=UUID.randomUUID().toString();

		//DELETE THE SOURCE FORM THE SOURCE
		test.log(LogStatus.INFO, "Delete the source from the source group");
		spogServer.deleteSourcefromSourceGroupwithCheck(group_id, arrayofsourcenodes[i-1], validtoken, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.SOURCE_GROUP_NOT_EXIST, test);

	}


	@AfterMethod
	public void getResult(ITestResult result){
		if(result.getStatus() == ITestResult.FAILURE){
			count1.setfailedcount();
			//remaincases=Nooftest-passedcases-failedcases;
			test.log(LogStatus.FAIL, "Test Case Failed is "+result.getName()+" with parameters as "+Arrays.asList(result.getParameters()) );
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());

		}else if(result.getStatus() == ITestResult.SKIP){
			test.log(LogStatus.SKIP, "Test Case Skipped is "+result.getName());
			count1.setskippedcount();
		}else if(result.getStatus()==ITestResult.SUCCESS){
			count1.setpassedcount();
			//remaincases=Nooftest-passedcases-failedcases;

		}
		// ending test
		//endTest(logger) : It ends the current test and prepares to create HTML report
		rep.endTest(test);
		//rep.flush();
	}

}

