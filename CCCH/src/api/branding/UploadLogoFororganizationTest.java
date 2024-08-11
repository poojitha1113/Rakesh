package api.branding;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.text.SimpleDateFormat;
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

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.BrandingSpogSever;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import dataPreparation.JsonPreparation;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class UploadLogoFororganizationTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	private BrandingSpogSever brandingSpogSever;
	private static JsonPreparation jp = new JsonPreparation();
	private TestOrgInfo ti;
	//public int Nooftest;
	//private ExtentReports rep;
	private ExtentTest test;

	//used for test case count like passed,failed,remaining cases
	//private testcasescount count1;
	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;
	private String  org_model_prefix=this.getClass().getSimpleName();

	/*private String runningMachine;
	private String buildVersion;*/
	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		brandingSpogSever = new BrandingSpogSever(baseURI, port);
		rep = ExtentManager.getInstance("UploadLogoForloggeninuserTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Malleswari.sykam";
		ti = new TestOrgInfo(spogServer, test);	
		Nooftest=0;
		//Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);

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

	@DataProvider(name = "upload_logo_valid")
	public final Object[][] createUserFilterValidParams() {
		return new Object[][] {
			{ "direct",ti.direct_org1_id,ti.direct_org1_user1_token,"./testdata/images/1.png"},
			{ "Normalmsp",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,"./testdata/images/1.png"},
			{ "Root Msp",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,"./testdata/images/1.png"},
			{ "SUB MSP",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,"./testdata/images/1.png"},


			{ "direct",ti.direct_org1_id,ti.direct_org1_user1_token,"./testdata/images/1.PNG"},
			{ "Normalmsp",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,"./testdata/images/1.PNG"},
			{ "Root Msp",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,"./testdata/images/1.PNG"},
			{ "SUB MSP",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,"./testdata/images/1.PNG"},

			{ "direct",ti.direct_org1_id,ti.direct_org1_user1_token,"./testdata/images/2.JPG"},
			{ "Normalmsp",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,"./testdata/images/2.JPG"},
			{ "Root Msp",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,"./testdata/images/2.JPG"},
			{ "SUB MSP",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,"./testdata/images/2.JPG"},

			{ "direct",ti.direct_org1_id,ti.direct_org1_user1_token,"./testdata/images/3.GIF"},
			{ "Normalmsp",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,"./testdata/images/3.GIF"},
			{ "Root Msp",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,"./testdata/images/3.GIF"},
			{ "SUB MSP",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,"./testdata/images/3.GIF"},


			{ "direct",ti.direct_org1_id,ti.direct_org1_user1_token,"./testdata/images/5.jpg"},
			{ "Normalmsp",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,"./testdata/images/5.jpg"},
			{ "Root Msp",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,"./testdata/images/5.jpg"},
			{ "SUB MSP",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,"./testdata/images/5.jpg"},

			{ "direct",ti.direct_org1_id,ti.direct_org1_user1_token,"./testdata/images/6.gif"},
			{ "Normalmsp",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,"./testdata/images/6.gif"},
			{ "Root Msp",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,"./testdata/images/6.gif"},
			{ "SUB MSP",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,"./testdata/images/6.gif"},


		};
	}


	@Test(dataProvider = "upload_logo_valid")
	public void uploadLogo_200(String organizationType,

			String organziation_id,
			String validToken,
			String logoPath
			) 
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);

		brandingSpogSever.setToken(validToken);
		Response logoResponse = brandingSpogSever.uploadlogoFororganization(organziation_id,logoPath, test);
		Response userReponse = brandingSpogSever.getLoggedInUser(validToken,200,null, test);
	}

	@DataProvider(name = "upload_logo_invalid")
	public final Object[][] upload_user_filter_invalid1() {
		return new Object[][] {
			{ "direct",ti.direct_org1_id,ti.direct_org1_user1_token,"./testdata/images/1.png"},
			{ "Normalmsp",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,"./testdata/images/1.png"},
			{ "Root Msp",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,"./testdata/images/1.png"},
			{ "SUB MSP",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,"./testdata/images/1.png"},

		};
	}

	@Test(dataProvider = "upload_logo_invalid")
	public void uploadlogo_401(String organizationType,
			String organziation_id,
			String validToken,
			String logoPath
			) 
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);

		//invalid token 
		brandingSpogSever.setToken(validToken+"junk");
		brandingSpogSever.uploadlogoFororganiztaionwithcheck(organziation_id,logoPath,401,"00900006",test);

		//missed token 
		brandingSpogSever.setToken("");
		brandingSpogSever.uploadlogoFororganiztaionwithcheck(organziation_id,logoPath,401,"00900006",test);

		//random generated organization_id
		brandingSpogSever.setToken(validToken);
		String random_organization_id=UUID.randomUUID().toString();
		brandingSpogSever.uploadlogoFororganiztaionwithcheck(random_organization_id,logoPath,404,"0030000A",test);

		//orgnaiztion id is not uuid

		brandingSpogSever.setToken(validToken);
		brandingSpogSever.uploadlogoFororganiztaionwithcheck("uuid",logoPath,400,"40000005",test);


	}


	@DataProvider(name = "upload_logo_invalid_400")
	public final Object[][] upload_logo_invalid_400() {
		return new Object[][] {
			// size is bigger than 200KB
			{ "direct",ti.direct_org1_id,ti.direct_org1_user1_token,"./testdata/images/exceed_size.gif",400,"00100005"},
			{ "Normalmsp",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,"./testdata/images/exceed_size.gif",400,"00100005"},
			{ "Root Msp",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,"./testdata/images/exceed_size.gif",400,"00100005"},
			{ "SUB MSP",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,"./testdata/images/exceed_size.gif",400,"00100005"},

			// size is bigger than 200KB

			{ "direct",ti.direct_org1_id,ti.direct_org1_user1_token,"./testdata/images/bitmap.bmp",400,"00100006"},
			{ "Normalmsp",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,"./testdata/images/bitmap.bmp",400,"00100006"},
			{ "Root Msp",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,"./testdata/images/bitmap.bmp",400,"00100006"},
			{ "SUB MSP",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,"./testdata/images/bitmap.bmp",400,"00100006"},

			{ "direct",ti.direct_org1_id,ti.direct_org1_user1_token,"./testdata/images/py2exe.exe",400,"00100006"},
			{ "Normalmsp",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,"./testdata/images/py2exe.exe",400,"00100006"},
			{ "Root Msp",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,"./testdata/images/py2exe.exe",400,"00100006"},
			{ "SUB MSP",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,"./testdata/images/py2exe.exe",400,"00100006"},


			{ "direct",ti.direct_org1_id,ti.direct_org1_user1_token,"./testdata/images/fakeimg.jpg",400,"00100006"},
			{ "Normalmsp",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,"./testdata/images/fakeimg.jpg",400,"00100006"},
			{ "Root Msp",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,"./testdata/images/fakeimg.jpg",400,"00100006"},
			{ "SUB MSP",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,"./testdata/images/fakeimg.jpg",400,"00100006"},
		};
	}

	@Test(dataProvider = "upload_logo_invalid_400")
	public void uploadlogo_400(String organizationType,
			String organziation_id,
			String validToken,
			String logoPath,
			int statusCode, String errorCode)

	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);

		//orgnaiztion id is not uuid

		brandingSpogSever.setToken(validToken);
		brandingSpogSever.uploadlogoFororganiztaionwithcheck(organziation_id,logoPath,statusCode, errorCode, test);


	}


	@DataProvider(name = "upload_logo_invalid_nofile400")
	public final Object[][] upload_logo_invalid_nofile400() {
		return new Object[][] {
			{ "direct",ti.direct_org1_id,ti.direct_org1_user1_token,"./testdata/images/fakeimg.jpg",400,"00100006"},
			{ "Normalmsp",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,"./testdata/images/fakeimg.jpg",400,"00100006"},
			{ "Root Msp",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,"./testdata/images/fakeimg.jpg",400,"00100006"},
			{ "SUB MSP",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,"./testdata/images/fakeimg.jpg",400,"00100006"},


		};
	}

	@Test(dataProvider = "upload_logo_invalid_nofile400")
	public void uploadLogo_400_invalid(String organizationType,
			String organziation_id,
			String validToken,
			String logoPath,
			int statusCode, String errorCode)

	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);

		//orgnaiztion id is not uuid

		brandingSpogSever.setToken(validToken);
		brandingSpogSever.uploadlogoFororganiztaionwithcheck(organziation_id,logoPath,statusCode, errorCode, test);


	}



	@DataProvider(name = "upload_logo_invalid_403")
	public final Object[][] upload_user_filter_invalid2() {
		return new Object[][] {
			//Direct Organization
			{ "Upload logo for the Direct organization with the another direct user token",ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org2_user1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Direct organization with the normal msp user token ",ti.direct_org1_id,ti.direct_org1_user1_token,ti.normal_msp_org1_user1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Direct organization with the normal msp account admin user ",ti.direct_org1_id,ti.direct_org1_user1_token,ti.normal_msp_org2_msp_accountadmin1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Direct organization with the customer account of normal msp user",ti.direct_org1_id,ti.direct_org1_user1_token,ti.normal_msp1_suborg1_user1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Direct organization with the sub msp user token",ti.direct_org1_id,ti.direct_org1_user1_token,ti.root_msp1_submsp1_user1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Direct organization with the sub msp account admin user token",ti.direct_org1_id,ti.direct_org1_user1_token,ti.root_msp2_submsp1_account_admin_token,"./testdata/images/1.png"},
			{ "Upload logo for the Direct organization with the customer account user of root msp",ti.direct_org1_id,ti.direct_org1_user1_token,ti.msp1_submsp1_suborg1_user1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Direct organization with the root msp user",ti.direct_org1_id,ti.direct_org1_user1_token,ti.root_msp_org1_user1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Direct organization with the root msp account admin user",ti.direct_org1_id,ti.direct_org1_user1_token,ti.root_msp_org1_msp_accountadmin1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Direct organization with the customer account of root msp user",ti.direct_org1_id,ti.direct_org1_user1_token,ti.root_msp2_suborg1_user1_token,"./testdata/images/1.png"},


			//Normal msp Orgrnization
			{ "Upload logo for the Normal Msp organization with the another direct user token",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.direct_org1_user2_token,"./testdata/images/1.png"},
			{ "Upload logo for the Normal Msp organization with the normal msp user token ",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org2_user1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Normal Msp organization with the normal msp account admin user ",ti.normal_msp_org1_id,ti.normal_msp_org2_user1_token,ti.normal_msp_org2_msp_accountadmin1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Normal Msp organization with the customer account of normal msp user",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_user1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Normal Msp organization with the sub msp user token",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.root_msp1_submsp1_user1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Normal Msp organization with the sub msp account admin user token",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.root_msp1_submsp2_account_admin_token,"./testdata/images/1.png"},
			{ "Upload logo for the Normal Msp organization with the customer account user of root msp",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.msp1_submsp1_suborg1_user1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Normal Msp organization with the root msp user",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.root_msp_org1_user1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Normal Msp organization with the root msp account admin user",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.root_msp_org1_msp_accountadmin1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Normal Msp organization with the customer account of root msp user",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.root_msp2_suborg1_user1_token,"./testdata/images/1.png"},

			//Root MSP

			{ "Upload logo for the Root Msp organization with the another direct user token",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.direct_org1_user2_token,"./testdata/images/1.png"},
			{ "Upload logo for the Root Msp organization with the normal msp user token ",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.normal_msp_org2_user1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Root Msp organization with the normal msp account admin user ",ti.root_msp_org1_id,ti.root_msp_org2_user1_token,ti.normal_msp_org2_msp_accountadmin1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Root Msp organization with the customer account of normal msp user",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.normal_msp1_suborg1_user1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Root Msp organization with the sub msp user token",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp1_submsp1_user1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Root Msp organization with the sub msp account admin user token",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp1_submsp2_account_admin_token,"./testdata/images/1.png"},
			{ "Upload logo for the Root Msp organization with the customer account user of root msp",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.msp1_submsp1_suborg1_user1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Root Msp organization with the root msp user",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org2_user1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Root Msp organization with the root msp account admin user",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org2_msp_accountadmin1_token,"./testdata/images/1.png"},
			{ "Upload logo for the Root Msp organization with the customer account of root msp user",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp2_suborg1_user1_token,"./testdata/images/1.png"},

			//Sub Msp
			{ "Upload logo for the SUB Msp organization with the another direct user token",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.direct_org1_user2_token,"./testdata/images/1.png"},
			{ "Upload logo for the SUB Msp organization with the normal msp user token ",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.normal_msp_org2_user1_token,"./testdata/images/1.png"},
			{ "Upload logo for the SUB Msp organization with the normal msp account admin user ",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp2_user1_token,ti.normal_msp_org2_msp_accountadmin1_token,"./testdata/images/1.png"},
			{ "Upload logo for the SUB Msp organization with the customer account of normal msp user",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.normal_msp1_suborg1_user1_token,"./testdata/images/1.png"},
			{ "Upload logo for the SUB Msp organization with the sub msp user token",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp2_submsp1_user1_token,"./testdata/images/1.png"},
			{ "Upload logo for the SUB Msp organization with the sub msp account admin user token",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp2_user1_token,ti.root_msp1_submsp2_account_admin_token,"./testdata/images/1.png"},
			{ "Upload logo for the SUB Msp organization with the customer account user of root msp",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_suborg1_user1_token,"./testdata/images/1.png"},
			{ "Upload logo for the SUB Msp organization with the root msp user",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp_org2_user1_token,"./testdata/images/1.png"},
			{ "Upload logo for the SUB Msp organization with the root msp account admin user",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp_org2_msp_accountadmin1_token,"./testdata/images/1.png"},
			{ "Upload logo for the SUB Msp organization with the customer account of root msp user",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp2_user1_token,ti.root_msp1_suborg1_user1_token,"./testdata/images/1.png"},

		};
	}

	@Test(dataProvider = "upload_logo_invalid_403")
	public void uploadLogo_403(String organizationType,
			String organziation_id,
			String validToken,
			String anotherusertoken,
			String logoPath
			) 
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);

		//orgnaiztion id is not uuid

		brandingSpogSever.setToken(anotherusertoken);
		brandingSpogSever.uploadlogoFororganiztaionwithcheck(organziation_id,logoPath,403,"00100101",test);


	}



	@DataProvider(name = "upload_logo_invalid_404")
	public final Object[][] upload_logo_invalid_404() {
		return new Object[][] {
			{ "direct",UUID.randomUUID().toString(),ti.direct_org1_user1_token,"./testdata/images/1.png"},
			{ "Normalmsp",UUID.randomUUID().toString(),ti.normal_msp_org1_user1_token,"./testdata/images/1.png"},
			{ "Root Msp",UUID.randomUUID().toString(),ti.root_msp_org1_user1_token,"./testdata/images/1.png"},
			{ "SUB MSP",UUID.randomUUID().toString(),ti.root_msp1_submsp1_user1_token,"./testdata/images/1.png"},

		};
	}

	@Test(dataProvider = "upload_logo_invalid_404")
	public void Uploadlogoorganization_404(String organizationType,
			String organziation_id,
			String validToken,
			String logoPath
			) 
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);

		test.log(LogStatus.INFO, "upload logog for the deleted organization_id");
		brandingSpogSever.setToken(validToken);
		brandingSpogSever.uploadlogoFororganiztaionwithcheck(organziation_id,logoPath,404,"0030000A",test);


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
