package api.organizations;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
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
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetSubOrganizationByIdTest {

	
	private SPOGServer spogServer;
	//public int Nooftest;
	private ExtentReports rep;
	private ExtentTest test;
	private String common_password = "Mclaren@2013";
	private String csrAdminUserName;
	private String csrAdminPassword;
	private String prefix_direct = "spog_bharadwajReddy_direct";
	private String postfix_email = "@arcserve.com";
	private String direct_org_name = prefix_direct + "_org";
	private String direct_org_email = direct_org_name + postfix_email;
	private String direct_org_first_name = direct_org_name + "_first_name";
	private String direct_org_last_name = direct_org_name + "_last_name";
	private String direct_user_name = prefix_direct + "_admin";
	private String direct_user_name_email = prefix_direct + "_admin" + postfix_email;
	private String direct_user_first_name = direct_user_name + "_first_name";
	private String direct_user_last_name = direct_user_name + "_last_name";

	private String prefix_msp = "spog_bharadwajReddy_msp";
	private String msp_org_name = prefix_msp + "_org";
	private String msp_org_email = msp_org_name + postfix_email;
	private String msp_org_first_name = msp_org_name + "_first_name";
	private String msp_org_last_name = msp_org_name + "_last_name";
	private String msp_user_name = prefix_msp + "_admin";
	private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	private String msp_user_first_name = msp_user_name + "_first_name";
	private String msp_user_last_name = msp_user_name + "_last_name";
	
	//sub org related
	private String prefix_suborg="spog_kiran_suborg";
	private String sub_org_name = prefix_suborg+"_org";
	//used for test case count like passed,failed,remaining cases
	private SQLServerDb bqdb1;
	public int Nooftest;
	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;
	long creationTime;
	String buildnumber=null;
	String BQame=null;
	private testcasescount count1;
	private String running_Machine;
	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","logFolder","runningMachine"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword, String logFolder,String runningMachine) {
		spogServer = new SPOGServer(baseURI, port);
		rep = ExtentManager.getInstance("GetSubOrganizationByIdTest", logFolder);
		Nooftest=0;
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		buildnumber="SPOGv1";
		running_Machine=runningMachine;
		BQame=this.getClass().getSimpleName();
		if(count1.isstarttimehit()==0) {
			System.out.println("Into get GetSubOrganizationByIdTest");
			creationTime=System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			//creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(BQame, running_Machine, buildnumber, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress");
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	
	@DataProvider(name = "suborganizationAndUserInfo")
	public final Object[][] getMSP_subOrganizationAndUserInfo() {
		return new Object[][] {{msp_org_name, SpogConstants.MSP_ORG, msp_org_email, common_password,msp_org_first_name, msp_org_last_name, 
			msp_user_name_email, common_password,msp_user_first_name, msp_user_last_name, SpogConstants.MSP_ADMIN,sub_org_name }};
	}
	
	@Test(dataProvider="suborganizationAndUserInfo")
	public void getsuborginfoById_mspuservalidtoken(String organizationName,
			String organizationType,
			String organizationEmail,
			String organizationPwd ,
			String organizationFirstName,
			String organizationLastName,
			String userEmail,
			String userPassword,
			String FirstName,
			String LastName,
			String Role_Id,
			String suborgname){
		test = rep.startTest("getsuborginfoById_mspuservalidtoken");
		test.assignAuthor("Kiran Sripada");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_"+organizationName;
		organizationEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+organizationEmail;
		organizationFirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationFirstName;
		organizationLastName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationLastName;
		FirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+FirstName;
		LastName=RandomStringUtils.randomAlphanumeric(8) +"_"+LastName;
		userEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+userEmail;
		suborgname = spogServer.ReturnRandom(suborgname);
		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword,test);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,test);
		test.log(LogStatus.INFO, "The MSP organization id is "+organization_id);
		test.log(LogStatus.INFO, "Create a sub org under organization "+organizationName +" with org name as "+suborgname);
		String sub_org_Id = spogServer.createAccountWithCheck(organization_id, suborgname, organization_id, test);
		test.log(LogStatus.INFO,"logging in with the created user "+organizationEmail);
		spogServer.userLogin(organizationEmail,organizationPwd);
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Getting the sub organization Info by using the msp admin user JWT"+validToken);
		//spogServer.getsuborgaccountinfobyIdwithcheck(validToken, organization_id, sub_org_Id, SpogConstants.SUCCESS_GET_PUT_DELETE, suborgname, "", test);
		test.log(LogStatus.INFO, "Performing the operations to delete the user and orginzation by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);
	}

	@AfterMethod
	public void getResult(ITestResult result){
		if(result.getStatus() == ITestResult.FAILURE){
			count1.setfailedcount();

			test.log(LogStatus.FAIL, "Test Case Failed is "+result.getName()+" with parameters as "+Arrays.asList(result.getParameters()) );
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());
		}else if(result.getStatus() == ITestResult.SKIP){
			count1.setskippedcount();
			test.log(LogStatus.SKIP, "Test Case Skipped is "+result.getName());

		}else if(result.getStatus()==ITestResult.SUCCESS){
			count1.setpassedcount();


		}
		// ending test
		//endTest(logger) : It ends the current test and prepares to create HTML report
		rep.endTest(test);

	}
	@AfterTest
	public void aftertest() {
		test.log(LogStatus.INFO, "The total test cases passed are "+count1.getpassedcount());
		test.log(LogStatus.INFO, "the total test cases failed are "+count1.getfailedcount());
		/*try {
				if(count1.getfailedcount()>0) {
					remaincases=Nooftest-(count1.getpassedcount()+count1.getfailedcount());
					bqdb1.updateTable(BQame, "KIRSRI-LAPW10", buildnumber, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(remaincases), count1.getcreationtime(), "Failed");
				}else {
					remaincases=Nooftest-(count1.getpassedcount()+count1.getfailedcount());
					bqdb1.updateTable(BQame, "KIRSRI-LAPW10", buildnumber, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(remaincases), count1.getcreationtime(), "Passed");
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//rep.endTest(test);
		 */		rep.flush();
	}

	@AfterSuite
	public void updatebd() {
		try {
			if(count1.getfailedcount()>0) {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(BQame, running_Machine, buildnumber, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Failed");
			}else {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(BQame, running_Machine, buildnumber, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Passed");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}	

}
