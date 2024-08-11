package ui.spog.configure;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import ui.spog.server.BrandingHelperpage;
import ui.spog.server.CustomerAccountsPageHelper;
import ui.spog.server.SPOGUIServer;

public class createBrandingForOrganizationTest extends base.prepare.Is4Org{
	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private SPOGUIServer spogUIServer;
	private CustomerAccountsPageHelper customerAccountsPageHelper;
	private BrandingHelperpage brandingpageHelper;
	private String csrAdminUserName;
	private String csrAdminPassword;
	private ExtentTest test;

	/*private ExtentReports rep;
  private SQLServerDb bqdb1;
  public int Nooftest;
  private long creationTime;
  private String BQName=null;
  private String runningMachine;
  private testcasescount count1;
  private String buildVersion;*/
	private String url;

	private String postfix_email = "@arcserve.com";
	private String common_password = "Mclaren@2016";

	private String prefix_direct = "SPOG_QA_MALLESWARI_BQ_DIRECT_ORG";
	private String direct_org_name = prefix_direct + "_org";
	private String direct_org_email = direct_org_name + postfix_email;
	private String direct_org_first_name = direct_org_name + "_first_name";
	private String direct_org_last_name = direct_org_name + "_last_name";
	private String direct_user_name = prefix_direct + "_admin";
	private String direct_user_name_email = prefix_direct + "_admin" + postfix_email;
	private String direct_user_first_name = direct_user_name + "_first_name";
	private String direct_user_last_name = direct_user_name + "_last_name";
	private String direct_user_validToken,direct_org_id;

	private String prefix_msp = "spog_qa_malleswari_msp";
	private String msp_org_name = prefix_msp + "_org";
	private String msp_user_name = prefix_msp + "_admin";
	private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	private String msp_user_first_name = msp_user_name + "_first_name";
	private String msp_user_last_name = msp_user_name + "_last_name";
	private String msp_org_id=null;
	private String final_msp_user_name_email=null;	  

	private String account_id;
	private String account_user_email;
	private String direct_user_id;
	private String msp_user_id;
	private String account_user_id;
	private Org4SPOGServer org4SpogServer;
	private String  org_model_prefix = "UI_"+this.getClass().getSimpleName();


	@BeforeClass
	@Parameters({ "baseURI", "port",   "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion", "uiBaseURI", "browserType", "maxWaitTimeSec"})
	public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword,  String buildVersion,
			String uiBaseURI, String browserType, int maxWaitTimeSec) throws UnknownHostException {

		org4SpogServer = new Org4SPOGServer(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		this.url = uiBaseURI;
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(),logFolder);
		test = rep.startTest("beforeClass");

		prepareEnv();

		brandingpageHelper = new BrandingHelperpage(browserType, maxWaitTimeSec);
		brandingpageHelper.openUrl(uiBaseURI);

		this.BQName = "UI_"+this.getClass().getSimpleName();
		String author = "mallleswari.sykam";
		this.runningMachine =  InetAddress.getLocalHost().getHostName();
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);
		Nooftest=0;
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		if(count1.isstarttimehit()==0) {

			creationTime=System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			//creationTime = System.currentTimeMillis();
			/*try {
              bqdb1.updateTable(this.BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
          } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
          } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
          }*/
		}

		brandingpageHelper.login_Spog(direct_org_email, common_password);
		//brandingpageHelper.login_Spog(final_msp_user_name_email, common_password);
	}
	//	@BeforeMethod
	@Parameters({"uiBaseURI", "browserType", "maxWaitTimeSec"})
	public void login(String uiBaseURI, String browserType, int maxWaitTimeSec) {
		//brandingpageHelper.login_Spog(final_msp_user_name_email, common_password);


	}

	@DataProvider(name = "Branding-info")
	public final Object[][] BrandingInfo() {
		return new Object[][] { 	

			//Valid cases

			/*//For MSP organization
			{"Success","Send valid organziation for the Msp organziation user to create the branding",final_msp_user_name_email,common_password,"TEST_ARCSERVE",null,null,"Valid"},
			{"Success","Send valid primarycolor  for the Msp organziation user to create the branding",final_msp_user_name_email,common_password,null,"#4cbfa8",null,"Valid"},
			//{"Success","Send valid seconadry color for the Msp organziation user to create the branding" ,final_msp_user_name_email,common_password,null,null,"#4cbfa8","Valid"},
			 */
			//Invalid cases
			/*
			//For MSP organization 

			//Given Invalid parameters for the organization name
			{"Error","Given lessthan 3 characters as organziation name to create the bradning for the msp user",final_msp_user_name_email,common_password,"es",null,null,"Must be 3 characters or more"},
			{"Error","Send more than 128 characters as organziation name to create branding for the msp user ",final_msp_user_name_email,common_password,"Nagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykam",null,null,"Must be 128 characters or less"},


			//Given invalid parameters to the primary color
			{"Error","Send lessthan 3 characters as primarycolor to create the bradning for the msp user",final_msp_user_name_email,common_password,null,"#1",null,"Invalid Hexcode"},
			{"Error","Given 3 charactes as Primary colorto create bradning for the msp organziation",final_msp_user_name_email,common_password,null,"#56",null,"Invalid Hexcode"},
			{"Error","Given Invalid Details as a primary color to create the bradning for the msp organization",final_msp_user_name_email,common_password,null,"4cbfa81d",null,"Invalid Hexcode"},
			{"Error","Given Invalid Details as a primary color to create the bradning for the msp organization",final_msp_user_name_email,common_password,null,"4cbfa74cbfa7",null,"Invalid Hexcode"},

			//Given invalid parameters to the primary color
			{"Error","Given lessthan 3 characters as organziation name to create the bradning for the msp user",final_msp_user_name_email,common_password,null,null,"#1","Invalid Hexcode"},
			{"Error","Given less than 3 charactors to the create brandinf for the msp user",final_msp_user_name_email,common_password,null,null,"#56","Invalid Hexcode"},
			{"Error","Given Invalid details of Seconady color to create branding for the msp org user",final_msp_user_name_email,common_password,null,null,"#4cbfa81","Invalid Hexcode"},
			{"Error","Given More than 7 characters of secondary color  to create branding for the msp organization user",final_msp_user_name_email,common_password,null,null,"#4cbfa7#4cbfa7","Invalid Hexcode"},

			 */
			//For Direct Organization
			{"Success","Send valid organziation for the direct organziation user to create the branding",direct_org_email,common_password,"ARCSERVE",null,null,"Valid"},
			{"Success","Send valid primarycolor  for the direct organziation user to create the branding",direct_org_email,common_password,null,"#4cbfa8",null,"Valid"},
			{"Success","Send valid seconadry color for the direct organziation user to create the branding",direct_org_email,common_password,null,null,"#4cbfa8","Valid"},

			
			//Direct Organization

			//Given Invalid parameters for the organization name
			{"Error","Given less than 3 charactters of organziation name to create bradning for the direct organization user",direct_org_email,common_password,"es",null,null,"Must be 3 characters or more"},
			{"Error","Gievn more than 128 charactres as a organization name to creat branding for the direct org user",direct_org_email,common_password,"Nagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykam",null,null,"Must be 128 characters or less"},


			//Given invalid parameters to the primary color
			{"Error","Given 2 characters as primary color to create the branding for the direct organization user",direct_org_email,common_password,null,"11",null,"Invalid Hexcode"},
			{"Error","Given 3 characters as primary color to create the branding for the direct organization user",direct_org_email,common_password,null,"56",null,"Invalid Hexcode"},
			{"Error","Given Invalid primary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,"#4cbfa81",null,"Invalid Hexcode"},
			{"Error","Given Invalid primary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,"#4cbfa7#4cbfa7",null,"Invalid Hexcode"},

			//Given invalid parameters to the primary color
			{"Error","Given 2 characters as secondary color to create the branding for the direct organization user",direct_org_email,common_password,null,null,"44","Invalid Hexcode"},
			{"Error","Given 3 characters as secondary color to create the branding for the direct organization user",direct_org_email,common_password,null,null,"5","Invalid Hexcode"},
			{"Error","Given Invalid secondary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,null,"#4cbfa81","Invalid Hexcode"},
			{"Error","Given Invalid secondary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,null,"#4cbfa7#4cbfa7","Invalid Hexcode"},

			//For Direct Organization
			{"Success","Send valid organziation for the direct organziation user to create the branding",direct_org_email,common_password,"ARCSERVE",null,null,"Valid"},
			{"Success","Send valid primarycolor  for the direct organziation user to create the branding",direct_org_email,common_password,null,"#4cbfa8",null,"Valid"},
			{"Success","Send valid seconadry color for the direct organziation user to create the branding",direct_org_email,common_password,null,null,"#4cbfa8","Valid"},

			
			
			//Create Branding when Navigate form Backup job page
			
			//Direct Organization

			//Valid Secnario
			{"Navigation_From_Backup_Job_Reports","Send valid organziation for the direct organziation user to create the branding",direct_org_email,common_password,"ARCSERVE",null,null,"Valid"},
			{"Navigation_From_Backup_Job_Reports","Send valid primarycolor  for the direct organziation user to create the branding",direct_org_email,common_password,null,"#4cbfa8",null,"Valid"},
			{"Navigation_From_Backup_Job_Reports","Send valid seconadry color for the direct organziation user to create the branding",direct_org_email,common_password,null,null,"#4cbfa8","Valid"},

			
			//Given Invalid parameters for the organization name
			{"Nagative_Secnario - From_backup_Job_Reports","Given less than 3 charactters of organziation name to create bradning for the direct organization user",direct_org_email,common_password,"es",null,null,"Must be 3 characters or more"},
			{"Nagative_Secnario - From_backup_Job_Reports","Gievn more than 128 charactres as a organization name to creat branding for the direct org user",direct_org_email,common_password,"Nagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykam",null,null,"Must be 128 characters or less"},


			//Given invalid parameters to the primary color
			{"Nagative_Secnario - From_backup_Job_Reports","Given 2 characters as primary color to create the branding for the direct organization user",direct_org_email,common_password,null,"11",null,"Invalid Hexcode"},
			{"Nagative_Secnario - From_backup_Job_Reports","Given 3 characters as primary color to create the branding for the direct organization user",direct_org_email,common_password,null,"56",null,"Invalid Hexcode"},
			{"Nagative_Secnario - From_backup_Job_Reports","Given Invalid primary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,"#4cbfa81",null,"Invalid Hexcode"},
			{"Nagative_Secnario - From_backup_Job_Reports","Given Invalid primary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,"#4cbfa7#4cbfa7",null,"Invalid Hexcode"},

			//Given invalid parameters to the primary color
			{"Nagative_Secnario - From_backup_Job_Reports","Given 2 characters as secondary color to create the branding for the direct organization user",direct_org_email,common_password,null,null,"44","Invalid Hexcode"},
			{"Nagative_Secnario - From_backup_Job_Reports","Given 3 characters as secondary color to create the branding for the direct organization user",direct_org_email,common_password,null,null,"5","Invalid Hexcode"},
			{"Nagative_Secnario - From_backup_Job_Reports","Given Invalid secondary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,null,"#4cbfa81","Invalid Hexcode"},
			{"Nagative_Secnario - From_backup_Job_Reports","Given Invalid secondary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,null,"#4cbfa7#4cbfa7","Invalid Hexcode"},


			//Create Branding when navigate form the customer page
			//Direct Organization

			//Valid Secnario
			{"Navigation_From_Customer_Page_Page","Send valid organziation for the direct organziation user to create the branding",direct_org_email,common_password,"ARCSERVE",null,null,"Valid"},
			{"Navigation_From_Customer_Page","Send valid primarycolor  for the direct organziation user to create the branding",direct_org_email,common_password,null,"#4cbfa8",null,"Valid"},
			{"Navigation_From_Customer_Page","Send valid seconadry color for the direct organziation user to create the branding",direct_org_email,common_password,null,null,"#4cbfa8","Valid"},

			
			//Given Invalid parameters for the organization name
			{"Nagative_Secnario - From_Customer_Page","Given less than 3 charactters of organziation name to create bradning for the direct organization user",direct_org_email,common_password,"es",null,null,"Must be 3 characters or more"},
			{"Nagative_Secnario - From_Customer_Page","Gievn more than 128 charactres as a organization name to creat branding for the direct org user",direct_org_email,common_password,"Nagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykam",null,null,"Must be 128 characters or less"},


			//Given invalid parameters to the primary color
			{"Nagative_Secnario - From_Customer_Page","Given 2 characters as primary color to create the branding for the direct organization user",direct_org_email,common_password,null,"11",null,"Invalid Hexcode"},
			{"Nagative_Secnario - From_Customer_Page","Given 3 characters as primary color to create the branding for the direct organization user",direct_org_email,common_password,null,"56",null,"Invalid Hexcode"},
			{"Nagative_Secnario - From_Customer_Page","Given Invalid primary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,"#4cbfa81",null,"Invalid Hexcode"},
			{"Nagative_Secnario - From_Customer_Page","Given Invalid primary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,"#4cbfa7#4cbfa7",null,"Invalid Hexcode"},

			//Given invalid parameters to the primary color
			{"Nagative_Secnario - From_Customer_Page","Given 2 characters as secondary color to create the branding for the direct organization user",direct_org_email,common_password,null,null,"44","Invalid Hexcode"},
			{"Nagative_Secnario - From_Customer_Page","Given 3 characters as secondary color to create the branding for the direct organization user",direct_org_email,common_password,null,null,"5","Invalid Hexcode"},
			{"Nagative_Secnario - From_Customer_Page","Given Invalid secondary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,null,"#4cbfa81","Invalid Hexcode"},
			{"Nagative_Secnario - From_Customer_Page","Given Invalid secondary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,null,"#4cbfa7#4cbfa7","Invalid Hexcode"},


			
			//Create branding when navigate form User account page
			
			//Direct Organization

			//Valid Secnario
			{"Navigation_From_UserAccountsPage","Send valid organziation for the direct organziation user to create the branding",direct_org_email,common_password,"ARCSERVE",null,null,"Valid"},
			{"Navigation_From_UserAccountsPage","Send valid primarycolor  for the direct organziation user to create the branding",direct_org_email,common_password,null,"#4cbfa8",null,"Valid"},
			{"Navigation_From_UserAccountsPage","Send valid seconadry color for the direct organziation user to create the branding",direct_org_email,common_password,null,null,"#4cbfa8","Valid"},

			
			//Given Invalid parameters for the organization name
			{"Nagative_Secnario - From_UserAccountsPage","Given less than 3 charactters of organziation name to create bradning for the direct organization user",direct_org_email,common_password,"es",null,null,"Must be 3 characters or more"},
			{"Nagative_Secnario - From_UserAccountsPage","Gievn more than 128 charactres as a organization name to creat branding for the direct org user",direct_org_email,common_password,"Nagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykam",null,null,"Must be 128 characters or less"},


			//Given invalid parameters to the primary color
			{"Nagative_Secnario - From_UserAccountsPage","Given 2 characters as primary color to create the branding for the direct organization user",direct_org_email,common_password,null,"11",null,"Invalid Hexcode"},
			{"Nagative_Secnario - From_UserAccountsPage","Given 3 characters as primary color to create the branding for the direct organization user",direct_org_email,common_password,null,"56",null,"Invalid Hexcode"},
			{"Nagative_Secnario - From_UserAccountsPage","Given Invalid primary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,"#4cbfa81",null,"Invalid Hexcode"},
			{"Nagative_Secnario - From_UserAccountsPage","Given Invalid primary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,"#4cbfa7#4cbfa7",null,"Invalid Hexcode"},

			//Given invalid parameters to the primary color
			{"Nagative_Secnario - From_UserAccountsPage","Given 2 characters as secondary color to create the branding for the direct organization user",direct_org_email,common_password,null,null,"44","Invalid Hexcode"},
			{"Nagative_Secnario - From_UserAccountsPage","Given 3 characters as secondary color to create the branding for the direct organization user",direct_org_email,common_password,null,null,"5","Invalid Hexcode"},
			{"Nagative_Secnario - From_UserAccountsPage","Given Invalid secondary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,null,"#4cbfa81","Invalid Hexcode"},
			{"Nagative_Secnario - From_UserAccountsPage","Given Invalid secondary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,null,"#4cbfa7#4cbfa7","Invalid Hexcode"},

			
			//Create branding when navigate form th managed reports page 
			//Direct Organization

			//Valid Secnario
			{"Navigation_From_ManageReportsPage","Send valid organziation for the direct organziation user to create the branding",direct_org_email,common_password,"ARCSERVE",null,null,"Valid"},
			{"Navigation_From_ManageReportsPage","Send valid primarycolor  for the direct organziation user to create the branding",direct_org_email,common_password,null,"#4cbfa8",null,"Valid"},
			{"Navigation_From_ManageReportsPage","Send valid seconadry color for the direct organziation user to create the branding",direct_org_email,common_password,null,null,"#4cbfa8","Valid"},

			
			//Given Invalid parameters for the organization name
			{"Nagative_Secnario - From_ManageReportsPage","Given less than 3 charactters of organziation name to create bradning for the direct organization user",direct_org_email,common_password,"es",null,null,"Must be 3 characters or more"},
			{"Nagative_Secnario - From_ManageReportsPage","Gievn more than 128 charactres as a organization name to creat branding for the direct org user",direct_org_email,common_password,"Nagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykam",null,null,"Must be 128 characters or less"},


			//Given invalid parameters to the primary color
			{"Nagative_Secnario - From_ManageReportsPage","Given 2 characters as primary color to create the branding for the direct organization user",direct_org_email,common_password,null,"11",null,"Invalid Hexcode"},
			{"Nagative_Secnario - From_ManageReportsPage","Given 3 characters as primary color to create the branding for the direct organization user",direct_org_email,common_password,null,"56",null,"Invalid Hexcode"},
			{"Nagative_Secnario - From_ManageReportsPage","Given Invalid primary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,"#4cbfa81",null,"Invalid Hexcode"},
			{"Nagative_Secnario - From_ManageReportsPage","Given Invalid primary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,"#4cbfa7#4cbfa7",null,"Invalid Hexcode"},

			//Given invalid parameters to the primary color
			{"Nagative_Secnario - From_ManageReportsPage","Given 2 characters as secondary color to create the branding for the direct organization user",direct_org_email,common_password,null,null,"44","Invalid Hexcode"},
			{"Nagative_Secnario - From_ManageReportsPage","Given 3 characters as secondary color to create the branding for the direct organization user",direct_org_email,common_password,null,null,"5","Invalid Hexcode"},
			{"Nagative_Secnario - From_ManageReportsPage","Given Invalid secondary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,null,"#4cbfa81","Invalid Hexcode"},
			{"Nagative_Secnario - From_ManageReportsPage","Given Invalid secondary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,null,"#4cbfa7#4cbfa7","Invalid Hexcode"},

			
			//Create Branding when navigate form EntitleMnetpage
			//Direct Organization

			//Valid Secnario
			{"Navigation_From_EntitlementPage","Send valid organziation for the direct organziation user to create the branding",direct_org_email,common_password,"ARCSERVE",null,null,"Valid"},
			{"Navigation_From_EntitlementPage","Send valid primarycolor  for the direct organziation user to create the branding",direct_org_email,common_password,null,"#4cbfa8",null,"Valid"},
			{"Navigation_From_EntitlementPage","Send valid seconadry color for the direct organziation user to create the branding",direct_org_email,common_password,null,null,"#4cbfa8","Valid"},

			
			//Given Invalid parameters for the organization name
			{"Nagative_Secnario - From_EntitlementPage","Given less than 3 charactters of organziation name to create bradning for the direct organization user",direct_org_email,common_password,"es",null,null,"Must be 3 characters or more"},
			{"Nagative_Secnario - From_EntitlementPage","Gievn more than 128 charactres as a organization name to creat branding for the direct org user",direct_org_email,common_password,"Nagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykam",null,null,"Must be 128 characters or less"},


			//Given invalid parameters to the primary color
			{"Nagative_Secnario - From_EntitlementPage","Given 2 characters as primary color to create the branding for the direct organization user",direct_org_email,common_password,null,"11",null,"Invalid Hexcode"},
			{"Nagative_Secnario - From_EntitlementPage","Given 3 characters as primary color to create the branding for the direct organization user",direct_org_email,common_password,null,"56",null,"Invalid Hexcode"},
			{"Nagative_Secnario - From_EntitlementPage","Given Invalid primary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,"#4cbfa81",null,"Invalid Hexcode"},
			{"Nagative_Secnario - From_EntitlementPage","Given Invalid primary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,"#4cbfa7#4cbfa7",null,"Invalid Hexcode"},

			//Given invalid parameters to the primary color
			{"Nagative_Secnario - From_EntitlementPage","Given 2 characters as secondary color to create the branding for the direct organization user",direct_org_email,common_password,null,null,"44","Invalid Hexcode"},
			{"Nagative_Secnario - From_EntitlementPage","Given 3 characters as secondary color to create the branding for the direct organization user",direct_org_email,common_password,null,null,"5","Invalid Hexcode"},
			{"Nagative_Secnario - From_EntitlementPage","Given Invalid secondary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,null,"#4cbfa81","Invalid Hexcode"},
			{"Nagative_Secnario - From_EntitlementPage","Given Invalid secondary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,null,"#4cbfa7#4cbfa7","Invalid Hexcode"},

			
			//Create branding when navigate form ReportPage
			//Direct Organization

			//Valid Secnario
			{"Navigation_From_ReportsPage","Send valid organziation for the direct organziation user to create the branding",direct_org_email,common_password,"ARCSERVE",null,null,"Valid"},
			{"Navigation_From_ReportsPage","Send valid primarycolor  for the direct organziation user to create the branding",direct_org_email,common_password,null,"#4cbfa8",null,"Valid"},
			{"Navigation_From_ReportsPage","Send valid seconadry color for the direct organziation user to create the branding",direct_org_email,common_password,null,null,"#4cbfa8","Valid"},

			
			//Given Invalid parameters for the organization name
			{"Nagative_Secnario - From_ReportsPage","Given less than 3 charactters of organziation name to create bradning for the direct organization user",direct_org_email,common_password,"es",null,null,"Must be 3 characters or more"},
			{"Nagative_Secnario - From_ReportsPage","Gievn more than 128 charactres as a organization name to creat branding for the direct org user",direct_org_email,common_password,"Nagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykamNagamalleswari.sykam",null,null,"Must be 128 characters or less"},


			//Given invalid parameters to the primary color
			{"Nagative_Secnario - From_ReportsPage","Given 2 characters as primary color to create the branding for the direct organization user",direct_org_email,common_password,null,"11",null,"Invalid Hexcode"},
			{"Nagative_Secnario - From_ReportsPage","Given 3 characters as primary color to create the branding for the direct organization user",direct_org_email,common_password,null,"56",null,"Invalid Hexcode"},
			{"Nagative_Secnario - From_ReportsPage","Given Invalid primary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,"#4cbfa81",null,"Invalid Hexcode"},
			{"Nagative_Secnario - From_ReportsPage","Given Invalid primary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,"#4cbfa7#4cbfa7",null,"Invalid Hexcode"},

			//Given invalid parameters to the primary color
			{"Nagative_Secnario - From_ReportsPage","Given 2 characters as secondary color to create the branding for the direct organization user",direct_org_email,common_password,null,null,"44","Invalid Hexcode"},
			{"Nagative_Secnario - From_ReportsPage","Given 3 characters as secondary color to create the branding for the direct organization user",direct_org_email,common_password,null,null,"5","Invalid Hexcode"},
			{"Nagative_Secnario - From_ReportsPage","Given Invalid secondary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,null,"#4cbfa81","Invalid Hexcode"},
			{"Nagative_Secnario - From_ReportsPage","Given Invalid secondary color to create the bradning for the direct organziation user",direct_org_email,common_password,null,null,"#4cbfa7#4cbfa7","Invalid Hexcode"},

		};
	}
	@Test(dataProvider = "Branding-info", enabled=true)
	public void CreateBradning(String caseType, String Testcase,String Username,
			String password,String orgName, 
			String	primaryColor,
			String secondaryColor,
			String expectedErrorMsg) {


		test=ExtentManager.getNewTest(Testcase);
		//Login to the could console 
		//brandingpageHelper.login_Spog(Username, password);

		if (caseType.contains("Success")) 
		{
			//Checking the branding if the case type contains SUCCESS
			brandingpageHelper.createBradningwithCheck(orgName,primaryColor,secondaryColor);
		}else if (caseType.contains("Error")) {
			//Checking the branding if the case type contains Error
			brandingpageHelper.getBrandingFailwithcheck(orgName,primaryColor,secondaryColor, expectedErrorMsg);
		}
		else if(caseType.contains("Navigation")) {
			brandingpageHelper.createBrandingWhenNvaigateFromOtherPages(caseType, orgName, primaryColor, secondaryColor);
		}else if (caseType.contains("Nagative_Secnario")) {
			brandingpageHelper.createBrandingwithcheckWhenNvaigateFromOtherPages(caseType, orgName, primaryColor, secondaryColor);	
		}
	}



	@DataProvider(name = "BrandingEmailer-info")
	public final Object[][] BrandingEmailerInfo() {
		return new Object[][] { 	



			//Create Branding emialer for the direct organization 

		
			/*//For MSP organization
			{"MSP-Success -Given all available filed to create Branding emailer for the msp user",final_msp_user_name_email,common_password,"Arcserve","no@arcserve.com","https://www.arcserve.com/saleschat/123",
				"http://www.arcserve.com/supportchat/123","http://www.facebook.com/ArcserveLLC/123","http://twitter.com/Arcserve/123","http://www.linkedin.com/company/arcserve/123"
				,"http://www.arcserve.com/no/social-community.aspx/123","http://www.arcserve.com/no/contact-arcserve.aspx/123","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html/123",
			"https://www.arcserve.com/about/privacy/123"},


			//msp organziation user
			{"MSP-Given the Formemailadress to create the bradningemialer and remaing fileds empty strings ",final_msp_user_name_email,common_password,"","123@gmail.com","","","","","","","","",""},
			{"MSP-Given the Formemailname to create the bradningemialer and remaing fileds empty strings ",final_msp_user_name_email,common_password,"Arcserve","","","","","","","","","",""},
			{"MSP-Given the SalesChat to create the brandingemialer for the msp user and remaning fileds are empty ",final_msp_user_name_email,common_password,"","","http://www.arcserve.com/saleschat","","","","","","","",""},
			{"MSP-Given the SupportChat to create the brandingemialer for the msp user and remaning fileds are empty",final_msp_user_name_email,common_password,"","","","http://www.arcserve.com/supportchat","","","","","","",""},
			{"MSP-Given the Facebookfield to create the bradningemialer and remaing fileds empty strings ",final_msp_user_name_email,common_password,"","","","","http://www.facebook.com/ArcserveLLC","","","","","",""},
			{"MSP-Given the TwitterFiled to create the bradningemialer and remaing fileds empty strings ",final_msp_user_name_email,common_password,"","","","","","http://twitter.com/Arcserve","","","","",""},
			{"MSP-Given the LinkedInfiled to create the bradningemialer and remaing fileds empty strings ",final_msp_user_name_email,common_password,"","","","","","","http://www.linkedin.com/company/arcserve","","","",""},
			{"MSP-Given the Socialmediplatform to create the bradningemialer and remaing fileds empty strings ",final_msp_user_name_email,common_password,"","","","","","","","http://www.arcserve.com/no/social-community.aspx","","",""},
			{"MSP-Given the ContactUs to create the bradningemialer and remaing fileds empty strings ",final_msp_user_name_email,common_password,"","","","","","","","","http://www.arcserve.com/no/contact-arcserve.aspx","",""},
			{"MSP-Given the LegalNotice to create the bradningemialer and remaing fileds empty strings ",final_msp_user_name_email,common_password,"","","","","","","","","","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html",""},
			{"MSP-Given the Privacy to create the bradningemialer and remaing fileds empty strings ",final_msp_user_name_email,common_password,"","","","","","","","","","","http://www.arcserve.com/about/privacy"},


			//MSP Org user
			{"MSP-Given the Formemailadress to create the bradningemialer and remaing fileds empty strings ",final_msp_user_name_email,common_password,null,"1234@gmail.com",null,null,null,null,null,null,null,null,null},
			{"MSP-Given the Formemailname to create the bradningemialer and remaing fileds empty strings ",final_msp_user_name_email,common_password,"Arcserve123",null,null,null,null,null,null,null,null,null,null},
			{"MSP-Given the SalesChat to create the brandingemialer for the msp user and remaning fileds are empty ",final_msp_user_name_email,common_password,null,null,"http://www.arcserve.com/saleschat12",null,null,null,null,null,null,null,null},
			{"MSP-Given the SupportChat to create the brandingemialer for the msp user and remaning fileds are empty",final_msp_user_name_email,common_password,null,null,null,"http://www.arcserve.com/supportchat12",null,null,null,null,null,null,null},
			{"MSP-Given the Facebookfield to create the bradningemialer and remaing fileds empty strings ",final_msp_user_name_email,common_password,null,null,null,null,"http://www.facebook.com/ArcserveLLC12",null,null,null,null,null,null},
			{"MSP-Given the TwitterFiled to create the bradningemialer and remaing fileds empty strings ",final_msp_user_name_email,common_password,null,null,null,null,null,"http://twitter.com/Arcserve12",null,null,null,null,null},
			{"MSP-Given the LinkedInfiled to create the bradningemialer and remaing fileds empty strings ",final_msp_user_name_email,common_password,null,null,null,null,null,null,"http://www.linkedin.com/company/arcserve12",null,null,null,null},
			{"MSP-Given the Socialmediplatform to create the bradningemialer and remaing fileds empty strings ",final_msp_user_name_email,common_password,null,null,null,null,null,null,null,"http://www.arcserve.com/no/social-community.aspx12",null,null,null},
			{"MSP-Given the ContactUs to create the bradningemialer and remaing fileds empty strings ",final_msp_user_name_email,common_password,null,null,null,null,null,null,null,null,"http://www.arcserve.com/no/contact-arcserve.aspx1234",null,null},
			{"MSP-Given the LegalNotice to create the bradningemialer and remaing fileds empty strings ",final_msp_user_name_email,common_password,null,null,null,null,null,null,null,null,null,"http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html12",null},
			{"MSP-Given the Privacy to create the bradningemialer and remaing fileds empty strings ",final_msp_user_name_email,common_password,null,null,null,null,null,null,null,null,null,null,"http://www.arcserve.com/about/privacy12"},

			 
			//Msp
			  {"MSP-Given combinations of fileds to create Branding for the Msp user",final_msp_user_name_email,common_password,"ArcserveSupport","12345@gmail.com",null,null,null,null,null,null,null,null,null},
			{"MSP-Given combinations of fileds to create Branding for the Msp user ",final_msp_user_name_email,common_password,"ArcserveSupport123","12347@gmail.com","https://www.arcserve.com/saleschat",null,null,null,null,null,null,null,null},
			{"MSP-Given combinations of fileds to create Branding for the Msp user ",final_msp_user_name_email,common_password,"ArcserveSupport12","12349@gmail.com","https://www.arcserve.com/saleschat",null,null,null,null,null,null,null,null},
			{"MSP-Given combinations of fileds to create Branding for the Msp user ",final_msp_user_name_email,common_password,"ArcserveSupport1","12340@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat",null,null,null,null,null,null,null},
			{"MSP-Given combinations of fileds to create Branding for the Msp user ",final_msp_user_name_email,common_password,"Arcserve_Support","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC",null,null,null,null,null,null},
			{"MSP-Given combinations of fileds to create Branding for the Msp user ",final_msp_user_name_email,common_password,"Arcserve_Support1","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve123",null,null,null,null,null},
			{"MSP-Given combinations of fileds to create Branding for the Msp user ",final_msp_user_name_email,common_password,"Arcserve_Support2","1235@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","http://www.linkedin.com/company/arcserve12",null,null,null,null},
			{"MSP-Given combinations of fileds to create Branding for the Msp user ",final_msp_user_name_email,common_password,"Arcserve_Support3","1236@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","http://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx",null,null,null},
			{"MSP-Given combinations of fileds to create Branding for the Msp user ",final_msp_user_name_email,common_password,"Arcserve_Support4","1237@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","http://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx","https://www.arcserve.com/no/contact-arcserve.aspx",null,null},
			{"MSP-Given combinations of fileds to create Branding for the Msp user ",final_msp_user_name_email,common_password,"Arcserve_Support4","1238@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","http://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx","https://www.arcserve.com/no/contact-arcserve.aspx","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html",null},

*/
			//Given the one filed to create the bradningemialer and remaing fileds empty strings 
			//DirectOrg
			{"Direct -Given all available filed to create Branding emailer for the Direct user",direct_org_email,common_password,"Arcserve","no@arcserve.com","https://www.arcserve.com/saleschat/123",
				"http://www.arcserve.com/supportchat/123","http://www.facebook.com/ArcserveLLC/123","http://twitter.com/Arcserve/123","http://www.linkedin.com/company/arcserve/123"
				,"http://www.arcserve.com/no/social-community.aspx/123","http://www.arcserve.com/no/contact-arcserve.aspx/123","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html/123",
			"https://www.arcserve.com/about/privacy/123"},
			{"Direct-Given the Formemailadress to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","123@gmail.com","","","","","","","","",""},
			{"Direct-Given the Formemailname to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"Arcserve","","","","","","","","","",""},
			{"Direct-Given the SalesChat to create the brandingemialer for the Direct user and remaning fileds are emptyto direct user  ",direct_org_email,common_password,"","","http://www.arcserve.com/saleschat","","","","","","","",""},
			{"Direct-Given the SupportChat to create the brandingemialer for the Direct user and remaning fileds are empty to direct user ",direct_org_email,common_password,"","","","http://www.arcserve.com/supportchat","","","","","","",""},
			{"Direct-Given the Facebookfield to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,"","","","","http://www.facebook.com/ArcserveLLC","","","","","",""},
			{"Direct-Given the TwitterFiled to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","","","","","http://twitter.com/Arcserve","","","","",""},
			{"Direct-Given the LinkedInfiled to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","http://www.linkedin.com/company/arcserve","","","",""},
			{"Direct-Given the Socialmediplatform to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","","http://www.arcserve.com/no/social-community.aspx","","",""},
			{"Direct-Given the ContactUs to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","","","http://www.arcserve.com/no/contact-arcserve.aspx","",""},
			{"Direct-Given the LegalNotice to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","","","","","","","","","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html",""},
			{"Direct-Given the Privacy to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,"","","","","","","","","","","http://www.arcserve.com/about/privacy"},
			
		

			//Only passing one field at a time 
			//Direc org
			{"Direct-Given the Formemailadress to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,"1234@gmail.com",null,null,null,null,null,null,null,null,null},
			{"Direct-Given the Formemailname to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"Arcserve123",null,null,null,null,null,null,null,null,null,null},
			{"Direct-Given the SalesChat to create the brandingemialer for the Direct user and remaning fileds are emptyto direct user  ",direct_org_email,common_password,null,null,"http://www.arcserve.com/saleschat12",null,null,null,null,null,null,null,null},
			{"Direct-Given the SupportChat to create the brandingemialer for the Direct user and remaning fileds are empty to direct user ",direct_org_email,common_password,null,null,null,"http://www.arcserve.com/supportchat12",null,null,null,null,null,null,null},
			{"Direct-Given the Facebookfield to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,null,null,null,null,"http://www.facebook.com/ArcserveLLC12",null,null,null,null,null,null},
			{"Direct-Given the TwitterFiled to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,"http://twitter.com/Arcserve12",null,null,null,null,null},
			{"Direct-Given the LinkedInfiled to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,null,null,null,null,null,null,"http://www.linkedin.com/company/arcserve12",null,null,null,null},
			{"Direct-Given the Socialmediplatform to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,"http://www.arcserve.com/no/social-community.aspx12",null,null,null},
			{"Direct-Given the ContactUs to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,null,"http://www.arcserve.com/no/contact-arcserve.aspx12",null,null},
			{"Direct-Given the LegalNotice to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,null,null,"http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html12",null},
			{"Direct-Given the Privacy to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,null,null,null,null,null,null,null,null,null,null,"http://www.arcserve.com/about/privacy12"},





			//Combination of all fileds 
			//Direct

			{"Direct-combinations of fileds to create Branding for the Direct user",direct_org_email,common_password,"ArcserveSupport","123456@gmail.com",null,null,null,null,null,null,null,null,null},
			{"Direct-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport123","12347@gmail.com","https://www.arcserve.com/saleschat",null,null,null,null,null,null,null,null},
			{"Direct-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport12","12349@gmail.com","https://www.arcserve.com/saleschat",null,null,null,null,null,null,null,null},
			{"Direct-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport1","12340@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat",null,null,null,null,null,null,null},
			{"Direct-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC",null,null,null,null,null,null},
			{"Direct-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support1","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve",null,null,null,null,null},
			{"Direct-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support2","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12",null,null,null,null},
			{"Direct-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support3","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx",null,null,null},
			{"Direct-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support4","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx","https://www.arcserve.com/no/contact-arcserve.aspx",null,null},
			{"Direct-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support4","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx","https://www.arcserve.com/no/contact-arcserve.aspx","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html",null},
			 

		};
	}

	@Test(dataProvider = "BrandingEmailer-info", enabled=true)
	public void CreateBradningEmailerTest(String caseType, 
			String username,
			String password,
			String fromEmailName,
			String fromEmailAddress,
			String salesChart,
			String supportChart,
			String faceBook,
			String twitter,
			String linkedin,
			String socialMediaPlatform,
			String contactUs,
			String legalNotice,
			String privacy

			) {


		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		//brandingpageHelper.login_Spog(Username, password);

		/*brandingpageHelper.createBradningEmailerwithCheck(fromEmailName,fromEmailAddress,salesChart,supportChart,faceBook,twitter,linkedin,socialMediaPlatform,contactUs,
				legalNotice,privacy);

		 */		if(caseType.contains("Backup")) {
			 brandingpageHelper.createBradningEmailerWhenNavigateFromOtherPages(caseType, fromEmailName, fromEmailAddress, salesChart, supportChart, faceBook, twitter, linkedin, socialMediaPlatform, contactUs, legalNotice, privacy);

		 }

	}


	@DataProvider(name = "BrandingEmailerNavigation-info")
	public final Object[][] BrandingEmailerNavigationInfo() {
		return new Object[][] { 	
			
			//When Navigate from BACKUPJOBS Reports to Branding emialer page and create/Update the emailer fileds
			//Create Branding emialer for the direct organization 

			{"Navigate_from_Backup page -Given all available filed to create Branding emailer for the Direct user",direct_org_email,common_password,"Arcserve","no@arcserve.com","https://www.arcserve.com/saleschat/123",
				"http://www.arcserve.com/supportchat/123","http://www.facebook.com/ArcserveLLC/123","http://twitter.com/Arcserve/123","http://www.linkedin.com/company/arcserve/123"
				,"http://www.arcserve.com/no/social-community.aspx/123","http://www.arcserve.com/no/contact-arcserve.aspx/123","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html/123",
			"https://www.arcserve.com/about/privacy/123"},

			//Given the one filed to create the bradningemialer and remaing fileds empty strings 
			//DirectOrg
			{"Navigate_from_Backup page-Given the Formemailadress to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","123@gmail.com","","","","","","","","",""},
			{"Navigate_from_Backup page-Given the Formemailname to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"Arcserve","","","","","","","","","",""},
			{"Navigate_from_Backup page-Given the SalesChat to create the brandingemialer for the Direct user and remaning fileds are emptyto direct user  ",direct_org_email,common_password,"","","http://www.arcserve.com/saleschat","","","","","","","",""},
			{"Navigate_from_Backup page-Given the SupportChat to create the brandingemialer for the Direct user and remaning fileds are empty to direct user ",direct_org_email,common_password,"","","","http://www.arcserve.com/supportchat","","","","","","",""},
			{"Navigate_from_Backup page-Given the Facebookfield to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,"","","","","http://www.facebook.com/ArcserveLLC","","","","","",""},
			{"Navigate_from_Backup page-Given the TwitterFiled to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","","","","","http://twitter.com/Arcserve","","","","",""},
			{"Navigate_from_Backup page-Given the LinkedInfiled to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","http://www.linkedin.com/company/arcserve","","","",""},
			{"Navigate_from_Backup page-Given the Socialmediplatform to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","","http://www.arcserve.com/no/social-community.aspx","","",""},
			{"Navigate_from_Backup page-Given the ContactUs to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","","","http://www.arcserve.com/no/contact-arcserve.aspx","",""},
			{"Navigate_from_Backup page-Given the LegalNotice to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","","","","","","","","","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html",""},
			{"Navigate_from_Backup page-Given the Privacy to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,"","","","","","","","","","","http://www.arcserve.com/about/privacy"},


			//Only passing one field at a time 
			//Direc org
			{"Navigate_from_Backup page-Given the Formemailadress to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,"1234@gmail.com",null,null,null,null,null,null,null,null,null},
			{"Navigate_from_Backup page-Given the Formemailname to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"Arcserve123",null,null,null,null,null,null,null,null,null,null},
			{"Navigate_from_Backup page-Given the SalesChat to create the brandingemialer for the Direct user and remaning fileds are emptyto direct user  ",direct_org_email,common_password,null,null,"http://www.arcserve.com/saleschat12",null,null,null,null,null,null,null,null},
			{"Navigate_from_Backup page-Given the SupportChat to create the brandingemialer for the Direct user and remaning fileds are empty to direct user ",direct_org_email,common_password,null,null,null,"http://www.arcserve.com/supportchat12",null,null,null,null,null,null,null},
			{"Navigate_from_Backup page-Given the Facebookfield to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,null,null,null,null,"http://www.facebook.com/ArcserveLLC12",null,null,null,null,null,null},
			{"Navigate_from_Backup page-Given the TwitterFiled to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,"http://twitter.com/Arcserve12",null,null,null,null,null},
			{"Navigate_from_Backup page-Given the LinkedInfiled to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,null,null,null,null,null,null,"http://www.linkedin.com/company/arcserve12",null,null,null,null},
			{"Navigate_from_Backup page-Given the Socialmediplatform to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,"http://www.arcserve.com/no/social-community.aspx12",null,null,null},
			{"Navigate_from_Backup page-Given the ContactUs to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,null,"http://www.arcserve.com/no/contact-arcserve.aspx12",null,null},
			{"Navigate_from_Backup page-Given the LegalNotice to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,null,null,"http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html12",null},
			{"Navigate_from_Backup page-Given the Privacy to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,null,null,null,null,null,null,null,null,null,null,"http://www.arcserve.com/about/privacy12"},

			//Combination of all fileds 
			//Direct
			{"Navigate_from_Backup page-combinations of fileds to create Branding for the Direct user",direct_org_email,common_password,"ArcserveSupport","123456@gmail.com",null,null,null,null,null,null,null,null,null},
			{"Navigate_from_Backup page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport123","12347@gmail.com","https://www.arcserve.com/saleschat",null,null,null,null,null,null,null,null},
			{"Navigate_from_Backup page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport12","12349@gmail.com","https://www.arcserve.com/saleschat",null,null,null,null,null,null,null,null},
			{"Navigate_from_Backup page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport1","12340@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat",null,null,null,null,null,null,null},
			{"Navigate_from_Backup page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC",null,null,null,null,null,null},
			{"Navigate_from_Backup page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support1","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve",null,null,null,null,null},
			{"Navigate_from_Backup page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support2","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12",null,null,null,null},
			{"Navigate_from_Backup page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support3","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx",null,null,null},
			{"Navigate_from_Backup page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support4","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx","https://www.arcserve.com/no/contact-arcserve.aspx",null,null},
			{"Navigate_from_Backup page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support4","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx","https://www.arcserve.com/no/contact-arcserve.aspx","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html",null},


			//When Navigate from Customer Page to Branding emialer page and create/Update the emailer fields
			//Create Branding emialer for the direct organization 

			{"Navigate_from_Backup page -Given all available filed to create Branding emailer for the Direct user",direct_org_email,common_password,"Arcserve","no@arcserve.com","https://www.arcserve.com/saleschat/123",
				"http://www.arcserve.com/supportchat/123","http://www.facebook.com/ArcserveLLC/123","http://twitter.com/Arcserve/123","http://www.linkedin.com/company/arcserve/123"
				,"http://www.arcserve.com/no/social-community.aspx/123","http://www.arcserve.com/no/contact-arcserve.aspx/123","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html/123",
			"https://www.arcserve.com/about/privacy/123"},

			//Given the one filed to create the bradning emialer and remaing fileds empty strings 
			//DirectOrg
			{"Navigate_from_Customer page-Given the Formemailadress to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","123@gmail.com","","","","","","","","",""},
			{"Navigate_from_Customer page-Given the Formemailname to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"Arcserve","","","","","","","","","",""},
			{"Navigate_from_Customer page-Given the SalesChat to create the brandingemialer for the Direct user and remaning fileds are emptyto direct user  ",direct_org_email,common_password,"","","http://www.arcserve.com/saleschat","","","","","","","",""},
			{"Navigate_from_Customer page-Given the SupportChat to create the brandingemialer for the Direct user and remaning fileds are empty to direct user ",direct_org_email,common_password,"","","","http://www.arcserve.com/supportchat","","","","","","",""},
			{"Navigate_from_Customer page-Given the Facebookfield to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,"","","","","http://www.facebook.com/ArcserveLLC","","","","","",""},
			{"Navigate_from_Customer page-Given the TwitterFiled to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","","","","","http://twitter.com/Arcserve","","","","",""},
			{"Navigate_from_Customer page-Given the LinkedInfiled to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","http://www.linkedin.com/company/arcserve","","","",""},
			{"Navigate_from_Customer page-Given the Socialmediplatform to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","","http://www.arcserve.com/no/social-community.aspx","","",""},
			{"Navigate_from_Customer page-Given the ContactUs to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","","","http://www.arcserve.com/no/contact-arcserve.aspx","",""},
			{"Navigate_from_Customer page-Given the LegalNotice to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","","","","","","","","","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html",""},
			{"Navigate_from_Customer page-Given the Privacy to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,"","","","","","","","","","","http://www.arcserve.com/about/privacy"},


			//Only passing one field at a time 
			//Direct Organization
			{"Navigate_from_Customer page-Given the Formemailadress to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,"1234@gmail.com",null,null,null,null,null,null,null,null,null},
			{"Navigate_from_Customer page-Given the Formemailname to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"Arcserve123",null,null,null,null,null,null,null,null,null,null},
			{"Navigate_from_Customer page-Given the SalesChat to create the brandingemialer for the Direct user and remaning fileds are emptyto direct user  ",direct_org_email,common_password,null,null,"http://www.arcserve.com/saleschat12",null,null,null,null,null,null,null,null},
			{"Navigate_from_Customer page-Given the SupportChat to create the brandingemialer for the Direct user and remaning fileds are empty to direct user ",direct_org_email,common_password,null,null,null,"http://www.arcserve.com/supportchat12",null,null,null,null,null,null,null},
			{"Navigate_from_Customer page-Given the Facebookfield to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,null,null,null,null,"http://www.facebook.com/ArcserveLLC12",null,null,null,null,null,null},
			{"Navigate_from_Customer page-Given the TwitterFiled to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,"http://twitter.com/Arcserve12",null,null,null,null,null},
			{"Navigate_from_Customer page-Given the LinkedInfiled to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,null,null,null,null,null,null,"http://www.linkedin.com/company/arcserve12",null,null,null,null},
			{"Navigate_from_Customer page-Given the Socialmediplatform to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,"http://www.arcserve.com/no/social-community.aspx12",null,null,null},
			{"Navigate_from_Customer page-Given the ContactUs to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,null,"http://www.arcserve.com/no/contact-arcserve.aspx12",null,null},
			{"Navigate_from_Customer page-Given the LegalNotice to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,null,null,"http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html12",null},
			{"Navigate_from_Customer page-Given the Privacy to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,null,null,null,null,null,null,null,null,null,null,"http://www.arcserve.com/about/privacy12"},

			//Combination of all fields
			//Direct
			{"Navigate_from_Customer page-combinations of fileds to create Branding for the Direct user",direct_org_email,common_password,"ArcserveSupport","123456@gmail.com",null,null,null,null,null,null,null,null,null},
			{"Navigate_from_Customer page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport123","12347@gmail.com","https://www.arcserve.com/saleschat",null,null,null,null,null,null,null,null},
			{"Navigate_from_Customer page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport12","12349@gmail.com","https://www.arcserve.com/saleschat",null,null,null,null,null,null,null,null},
			{"Navigate_from_Customer page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport1","12340@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat",null,null,null,null,null,null,null},
			{"Navigate_from_Customer page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC",null,null,null,null,null,null},
			{"Navigate_from_Customer page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support1","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve",null,null,null,null,null},
			{"Navigate_from_Customer page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support2","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12",null,null,null,null},
			{"Navigate_from_Customer page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support3","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx",null,null,null},
			{"Navigate_from_Customer page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support4","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx","https://www.arcserve.com/no/contact-arcserve.aspx",null,null},
			{"Navigate_from_Customer page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support4","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx","https://www.arcserve.com/no/contact-arcserve.aspx","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html",null},


			
			//When Navigate from Policy Page to Branding emialer page and create/Update the emailer fields
			//Create Branding emialer for the direct organization 

			{"Navigate_from_Policy page -Given all available filed to create Branding emailer for the Direct user",direct_org_email,common_password,"Arcserve","no@arcserve.com","https://www.arcserve.com/saleschat/123",
				"http://www.arcserve.com/supportchat/123","http://www.facebook.com/ArcserveLLC/123","http://twitter.com/Arcserve/123","http://www.linkedin.com/company/arcserve/123"
				,"http://www.arcserve.com/no/social-community.aspx/123","http://www.arcserve.com/no/contact-arcserve.aspx/123","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html/123",
			"https://www.arcserve.com/about/privacy/123"},

			//Given the one filed to create the bradning emialer and remaing fileds empty strings 
			//DirectOrg
			{"Navigate_from_Policy page-Given the Formemailadress to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","123@gmail.com","","","","","","","","",""},
			{"Navigate_from_Policy page-Given the Formemailname to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"Arcserve","","","","","","","","","",""},
			{"Navigate_from_Policy page-Given the SalesChat to create the brandingemialer for the Direct user and remaning fileds are emptyto direct user  ",direct_org_email,common_password,"","","http://www.arcserve.com/saleschat","","","","","","","",""},
			{"Navigate_from_Policy page-Given the SupportChat to create the brandingemialer for the Direct user and remaning fileds are empty to direct user ",direct_org_email,common_password,"","","","http://www.arcserve.com/supportchat","","","","","","",""},
			{"Navigate_from_Policy page-Given the Facebookfield to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,"","","","","http://www.facebook.com/ArcserveLLC","","","","","",""},
			{"Navigate_from_Policy page-Given the TwitterFiled to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","","","","","http://twitter.com/Arcserve","","","","",""},
			{"Navigate_from_Policy page-Given the LinkedInfiled to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","http://www.linkedin.com/company/arcserve","","","",""},
			{"Navigate_from_Policy page-Given the Socialmediplatform to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","","http://www.arcserve.com/no/social-community.aspx","","",""},
			{"Navigate_from_Policy page-Given the ContactUs to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","","","http://www.arcserve.com/no/contact-arcserve.aspx","",""},
			{"Navigate_from_Policy page-Given the LegalNotice to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","","","","","","","","","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html",""},
			{"Navigate_from_Policy page-Given the Privacy to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,"","","","","","","","","","","http://www.arcserve.com/about/privacy"},


			//Only passing one field at a time 
			//Direct Organization
			{"Navigate_from_Policy page-Given the Formemailadress to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,"1234@gmail.com",null,null,null,null,null,null,null,null,null},
			{"Navigate_from_Policy page-Given the Formemailname to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"Arcserve123",null,null,null,null,null,null,null,null,null,null},
			{"Navigate_from_Policy page-Given the SalesChat to create the brandingemialer for the Direct user and remaning fileds are emptyto direct user  ",direct_org_email,common_password,null,null,"http://www.arcserve.com/saleschat12",null,null,null,null,null,null,null,null},
			{"Navigate_from_Policy page-Given the SupportChat to create the brandingemialer for the Direct user and remaning fileds are empty to direct user ",direct_org_email,common_password,null,null,null,"http://www.arcserve.com/supportchat12",null,null,null,null,null,null,null},
			{"Navigate_from_Policy page-Given the Facebookfield to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,null,null,null,null,"http://www.facebook.com/ArcserveLLC12",null,null,null,null,null,null},
			{"Navigate_from_Policy page-Given the TwitterFiled to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,"http://twitter.com/Arcserve12",null,null,null,null,null},
			{"Navigate_from_Policy page-Given the LinkedInfiled to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,null,null,null,null,null,null,"http://www.linkedin.com/company/arcserve12",null,null,null,null},
			{"Navigate_from_Policy page-Given the Socialmediplatform to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,"http://www.arcserve.com/no/social-community.aspx12",null,null,null},
			{"Navigate_from_Policy page-Given the ContactUs to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,null,"http://www.arcserve.com/no/contact-arcserve.aspx12",null,null},
			{"Navigate_from_Policy page-Given the LegalNotice to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,null,null,"http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html12",null},
			{"Navigate_from_Policy page-Given the Privacy to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,null,null,null,null,null,null,null,null,null,null,"http://www.arcserve.com/about/privacy12"},

			//Combination of all fields
			//Direct
			{"Navigate_from_Policy page-combinations of fileds to create Branding for the Direct user",direct_org_email,common_password,"ArcserveSupport","123456@gmail.com",null,null,null,null,null,null,null,null,null},
			{"Navigate_from_Policy page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport123","12347@gmail.com","https://www.arcserve.com/saleschat",null,null,null,null,null,null,null,null},
			{"Navigate_from_Policy page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport12","12349@gmail.com","https://www.arcserve.com/saleschat",null,null,null,null,null,null,null,null},
			{"Navigate_from_Policy page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport1","12340@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat",null,null,null,null,null,null,null},
			{"Navigate_from_Policy page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC",null,null,null,null,null,null},
			{"Navigate_from_Policy page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support1","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve",null,null,null,null,null},
			{"Navigate_from_Policy page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support2","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12",null,null,null,null},
			{"Navigate_from_Policy page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support3","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx",null,null,null},
			{"Navigate_from_Policy page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support4","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx","https://www.arcserve.com/no/contact-arcserve.aspx",null,null},
			{"Navigate_from_Policy page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support4","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx","https://www.arcserve.com/no/contact-arcserve.aspx","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html",null},

			
			//When Navigate from UserAccount Page to Branding emialer page and create/Update the emailer fields
			//Create Branding emialer for the direct organization 

			{"Navigate_from_UserAccount page -Given all available filed to create Branding emailer for the Direct user",direct_org_email,common_password,"Arcserve","no@arcserve.com","https://www.arcserve.com/saleschat/123",
				"http://www.arcserve.com/supportchat/123","http://www.facebook.com/ArcserveLLC/123","http://twitter.com/Arcserve/123","http://www.linkedin.com/company/arcserve/123"
				,"http://www.arcserve.com/no/social-community.aspx/123","http://www.arcserve.com/no/contact-arcserve.aspx/123","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html/123",
			"https://www.arcserve.com/about/privacy/123"},

			//Given the one filed to create the bradning emialer and remaing fileds empty strings 
			//DirectOrg
			{"Navigate_from_UserAccount page-Given the Formemailadress to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","123@gmail.com","","","","","","","","",""},
			{"Navigate_from_UserAccount page-Given the Formemailname to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"Arcserve","","","","","","","","","",""},
			{"Navigate_from_UserAccount page-Given the SalesChat to create the brandingemialer for the Direct user and remaning fileds are emptyto direct user  ",direct_org_email,common_password,"","","http://www.arcserve.com/saleschat","","","","","","","",""},
			{"Navigate_from_UserAccount page-Given the SupportChat to create the brandingemialer for the Direct user and remaning fileds are empty to direct user ",direct_org_email,common_password,"","","","http://www.arcserve.com/supportchat","","","","","","",""},
			{"Navigate_from_UserAccount page-Given the Facebookfield to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,"","","","","http://www.facebook.com/ArcserveLLC","","","","","",""},
			{"Navigate_from_UserAccount page-Given the TwitterFiled to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","","","","","http://twitter.com/Arcserve","","","","",""},
			{"Navigate_from_UserAccount page-Given the LinkedInfiled to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","http://www.linkedin.com/company/arcserve","","","",""},
			{"Navigate_from_UserAccount page-Given the Socialmediplatform to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","","http://www.arcserve.com/no/social-community.aspx","","",""},
			{"Navigate_from_UserAccount page-Given the ContactUs to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","","","http://www.arcserve.com/no/contact-arcserve.aspx","",""},
			{"Navigate_from_UserAccount page-Given the LegalNotice to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","","","","","","","","","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html",""},
			{"Navigate_from_UserAccount page-Given the Privacy to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,"","","","","","","","","","","http://www.arcserve.com/about/privacy"},


			//Only passing one field at a time 
			//Direct Organization
			{"Navigate_from_UserAccount page-Given the Formemailadress to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,"1234@gmail.com",null,null,null,null,null,null,null,null,null},
			{"Navigate_from_UserAccount page-Given the Formemailname to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"Arcserve123",null,null,null,null,null,null,null,null,null,null},
			{"Navigate_from_UserAccount page-Given the SalesChat to create the brandingemialer for the Direct user and remaning fileds are emptyto direct user  ",direct_org_email,common_password,null,null,"http://www.arcserve.com/saleschat12",null,null,null,null,null,null,null,null},
			{"Navigate_from_UserAccount page-Given the SupportChat to create the brandingemialer for the Direct user and remaning fileds are empty to direct user ",direct_org_email,common_password,null,null,null,"http://www.arcserve.com/supportchat12",null,null,null,null,null,null,null},
			{"Navigate_from_UserAccount page-Given the Facebookfield to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,null,null,null,null,"http://www.facebook.com/ArcserveLLC12",null,null,null,null,null,null},
			{"Navigate_from_UserAccount page-Given the TwitterFiled to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,"http://twitter.com/Arcserve12",null,null,null,null,null},
			{"Navigate_from_UserAccount page-Given the LinkedInfiled to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,null,null,null,null,null,null,"http://www.linkedin.com/company/arcserve12",null,null,null,null},
			{"Navigate_from_UserAccount page-Given the Socialmediplatform to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,"http://www.arcserve.com/no/social-community.aspx12",null,null,null},
			{"Navigate_from_UserAccount page-Given the ContactUs to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,null,"http://www.arcserve.com/no/contact-arcserve.aspx12",null,null},
			{"Navigate_from_UserAccount page-Given the LegalNotice to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,null,null,"http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html12",null},
			{"Navigate_from_UserAccount page-Given the Privacy to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,null,null,null,null,null,null,null,null,null,null,"http://www.arcserve.com/about/privacy12"},

			//Combination of all fields
			//Direct
			{"Navigate_from_UserAccount page-combinations of fileds to create Branding for the Direct user",direct_org_email,common_password,"ArcserveSupport","123456@gmail.com",null,null,null,null,null,null,null,null,null},
			{"Navigate_from_UserAccount page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport123","12347@gmail.com","https://www.arcserve.com/saleschat",null,null,null,null,null,null,null,null},
			{"Navigate_from_UserAccount page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport12","12349@gmail.com","https://www.arcserve.com/saleschat",null,null,null,null,null,null,null,null},
			{"Navigate_from_UserAccount page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport1","12340@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat",null,null,null,null,null,null,null},
			{"Navigate_from_UserAccount page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC",null,null,null,null,null,null},
			{"Navigate_from_UserAccount page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support1","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve",null,null,null,null,null},
			{"Navigate_from_UserAccount page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support2","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12",null,null,null,null},
			{"Navigate_from_UserAccount page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support3","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx",null,null,null},
			{"Navigate_from_UserAccount page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support4","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx","https://www.arcserve.com/no/contact-arcserve.aspx",null,null},
			{"Navigate_from_UserAccount page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support4","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx","https://www.arcserve.com/no/contact-arcserve.aspx","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html",null},


			//When Navigate from ManageReports Page to Branding emialer page and create/Update the emailer fields
			//Create Branding emialer for the direct organization 

			{"Navigate_from_ManageReports page -Given all available filed to create Branding emailer for the Direct user",direct_org_email,common_password,"Arcserve","no@arcserve.com","https://www.arcserve.com/saleschat/123",
				"http://www.arcserve.com/supportchat/123","http://www.facebook.com/ArcserveLLC/123","http://twitter.com/Arcserve/123","http://www.linkedin.com/company/arcserve/123"
				,"http://www.arcserve.com/no/social-community.aspx/123","http://www.arcserve.com/no/contact-arcserve.aspx/123","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html/123",
			"https://www.arcserve.com/about/privacy/123"},

			//Given the one filed to create the bradning emialer and remaing fileds empty strings 
			//DirectOrg
			{"Navigate_from_ManageReports page-Given the Formemailadress to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","123@gmail.com","","","","","","","","",""},
			{"Navigate_from_ManageReports page-Given the Formemailname to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"Arcserve","","","","","","","","","",""},
			{"Navigate_from_ManageReports page-Given the SalesChat to create the brandingemialer for the Direct user and remaning fileds are emptyto direct user  ",direct_org_email,common_password,"","","http://www.arcserve.com/saleschat","","","","","","","",""},
			{"Navigate_from_ManageReports page-Given the SupportChat to create the brandingemialer for the Direct user and remaning fileds are empty to direct user ",direct_org_email,common_password,"","","","http://www.arcserve.com/supportchat","","","","","","",""},
			{"Navigate_from_ManageReports page-Given the Facebookfield to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,"","","","","http://www.facebook.com/ArcserveLLC","","","","","",""},
			{"Navigate_from_ManageReports page-Given the TwitterFiled to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","","","","","http://twitter.com/Arcserve","","","","",""},
			{"Navigate_from_ManageReports page-Given the LinkedInfiled to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","http://www.linkedin.com/company/arcserve","","","",""},
			{"Navigate_from_ManageReports page-Given the Socialmediplatform to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","","http://www.arcserve.com/no/social-community.aspx","","",""},
			{"Navigate_from_ManageReports page-Given the ContactUs to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","","","http://www.arcserve.com/no/contact-arcserve.aspx","",""},
			{"Navigate_from_ManageReports page-Given the LegalNotice to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","","","","","","","","","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html",""},
			{"Navigate_from_ManageReports page-Given the Privacy to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,"","","","","","","","","","","http://www.arcserve.com/about/privacy"},


			//Only passing one field at a time 
			//Direct Organization
			{"Navigate_from_ManageReports page-Given the Formemailadress to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,"1234@gmail.com",null,null,null,null,null,null,null,null,null},
			{"Navigate_from_ManageReports page-Given the Formemailname to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"Arcserve123",null,null,null,null,null,null,null,null,null,null},
			{"Navigate_from_ManageReports page-Given the SalesChat to create the brandingemialer for the Direct user and remaning fileds are emptyto direct user  ",direct_org_email,common_password,null,null,"http://www.arcserve.com/saleschat12",null,null,null,null,null,null,null,null},
			{"Navigate_from_ManageReports page-Given the SupportChat to create the brandingemialer for the Direct user and remaning fileds are empty to direct user ",direct_org_email,common_password,null,null,null,"http://www.arcserve.com/supportchat12",null,null,null,null,null,null,null},
			{"Navigate_from_ManageReports page-Given the Facebookfield to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,null,null,null,null,"http://www.facebook.com/ArcserveLLC12",null,null,null,null,null,null},
			{"Navigate_from_ManageReports page-Given the TwitterFiled to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,"http://twitter.com/Arcserve12",null,null,null,null,null},
			{"Navigate_from_ManageReports page-Given the LinkedInfiled to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,null,null,null,null,null,null,"http://www.linkedin.com/company/arcserve12",null,null,null,null},
			{"Navigate_from_ManageReports page-Given the Socialmediplatform to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,"http://www.arcserve.com/no/social-community.aspx12",null,null,null},
			{"Navigate_from_ManageReports page-Given the ContactUs to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,null,"http://www.arcserve.com/no/contact-arcserve.aspx12",null,null},
			{"Navigate_from_ManageReports page-Given the LegalNotice to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,null,null,"http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html12",null},
			{"Navigate_from_ManageReports page-Given the Privacy to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,null,null,null,null,null,null,null,null,null,null,"http://www.arcserve.com/about/privacy12"},

			//Combination of all fields
			//Direct
			{"Navigate_from_ManageReports page-combinations of fileds to create Branding for the Direct user",direct_org_email,common_password,"ArcserveSupport","123456@gmail.com",null,null,null,null,null,null,null,null,null},
			{"Navigate_from_ManageReports page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport123","12347@gmail.com","https://www.arcserve.com/saleschat",null,null,null,null,null,null,null,null},
			{"Navigate_from_ManageReports page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport12","12349@gmail.com","https://www.arcserve.com/saleschat",null,null,null,null,null,null,null,null},
			{"Navigate_from_ManageReports page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport1","12340@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat",null,null,null,null,null,null,null},
			{"Navigate_from_ManageReports page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC",null,null,null,null,null,null},
			{"Navigate_from_ManageReports page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support1","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve",null,null,null,null,null},
			{"Navigate_from_ManageReports page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support2","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12",null,null,null,null},
			{"Navigate_from_ManageReports page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support3","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx",null,null,null},
			{"Navigate_from_ManageReports page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support4","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx","https://www.arcserve.com/no/contact-arcserve.aspx",null,null},
			{"Navigate_from_ManageReports page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support4","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx","https://www.arcserve.com/no/contact-arcserve.aspx","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html",null},


			//When Navigate from Entitlement Page to Branding emialer page and create/Update the emailer fields
			//Create Branding emialer for the direct organization 

			{"Navigate_from_Entitlement page -Given all available filed to create Branding emailer for the Direct user",direct_org_email,common_password,"Arcserve","no@arcserve.com","https://www.arcserve.com/saleschat/123",
				"http://www.arcserve.com/supportchat/123","http://www.facebook.com/ArcserveLLC/123","http://twitter.com/Arcserve/123","http://www.linkedin.com/company/arcserve/123"
				,"http://www.arcserve.com/no/social-community.aspx/123","http://www.arcserve.com/no/contact-arcserve.aspx/123","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html/123",
			"https://www.arcserve.com/about/privacy/123"},

			//Given the one filed to create the bradning emialer and remaing fileds empty strings 
			//DirectOrg
			{"Navigate_from_Entitlement page-Given the Formemailadress to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","123@gmail.com","","","","","","","","",""},
			{"Navigate_from_Entitlement page-Given the Formemailname to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"Arcserve","","","","","","","","","",""},
			{"Navigate_from_Entitlement page-Given the SalesChat to create the brandingemialer for the Direct user and remaning fileds are emptyto direct user  ",direct_org_email,common_password,"","","http://www.arcserve.com/saleschat","","","","","","","",""},
			{"Navigate_from_Entitlement page-Given the SupportChat to create the brandingemialer for the Direct user and remaning fileds are empty to direct user ",direct_org_email,common_password,"","","","http://www.arcserve.com/supportchat","","","","","","",""},
			{"Navigate_from_Entitlement page-Given the Facebookfield to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,"","","","","http://www.facebook.com/ArcserveLLC","","","","","",""},
			{"Navigate_from_Entitlement page-Given the TwitterFiled to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","","","","","http://twitter.com/Arcserve","","","","",""},
			{"Navigate_from_Entitlement page-Given the LinkedInfiled to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","http://www.linkedin.com/company/arcserve","","","",""},
			{"Navigate_from_Entitlement page-Given the Socialmediplatform to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","","http://www.arcserve.com/no/social-community.aspx","","",""},
			{"Navigate_from_Entitlement page-Given the ContactUs to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","","","http://www.arcserve.com/no/contact-arcserve.aspx","",""},
			{"Navigate_from_Entitlement page-Given the LegalNotice to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","","","","","","","","","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html",""},
			{"Navigate_from_Entitlement page-Given the Privacy to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,"","","","","","","","","","","http://www.arcserve.com/about/privacy"},


			//Only passing one field at a time 
			//Direct Organization
			{"Navigate_from_Entitlement page-Given the Formemailadress to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,"1234@gmail.com",null,null,null,null,null,null,null,null,null},
			{"Navigate_from_Entitlement page-Given the Formemailname to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"Arcserve123",null,null,null,null,null,null,null,null,null,null},
			{"Navigate_from_Entitlement page-Given the SalesChat to create the brandingemialer for the Direct user and remaning fileds are emptyto direct user  ",direct_org_email,common_password,null,null,"http://www.arcserve.com/saleschat12",null,null,null,null,null,null,null,null},
			{"Navigate_from_Entitlement page-Given the SupportChat to create the brandingemialer for the Direct user and remaning fileds are empty to direct user ",direct_org_email,common_password,null,null,null,"http://www.arcserve.com/supportchat12",null,null,null,null,null,null,null},
			{"Navigate_from_Entitlement page-Given the Facebookfield to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,null,null,null,null,"http://www.facebook.com/ArcserveLLC12",null,null,null,null,null,null},
			{"Navigate_from_Entitlement page-Given the TwitterFiled to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,"http://twitter.com/Arcserve12",null,null,null,null,null},
			{"Navigate_from_Entitlement page-Given the LinkedInfiled to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,null,null,null,null,null,null,"http://www.linkedin.com/company/arcserve12",null,null,null,null},
			{"Navigate_from_Entitlement page-Given the Socialmediplatform to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,"http://www.arcserve.com/no/social-community.aspx12",null,null,null},
			{"Navigate_from_Entitlement page-Given the ContactUs to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,null,"http://www.arcserve.com/no/contact-arcserve.aspx12",null,null},
			{"Navigate_from_Entitlement page-Given the LegalNotice to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,null,null,"http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html12",null},
			{"Navigate_from_Entitlement page-Given the Privacy to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,null,null,null,null,null,null,null,null,null,null,"http://www.arcserve.com/about/privacy12"},

			//Combination of all fields
			//Direct
			{"Navigate_from_Entitlement page-combinations of fileds to create Branding for the Direct user",direct_org_email,common_password,"ArcserveSupport","123456@gmail.com",null,null,null,null,null,null,null,null,null},
			{"Navigate_from_Entitlement page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport123","12347@gmail.com","https://www.arcserve.com/saleschat",null,null,null,null,null,null,null,null},
			{"Navigate_from_Entitlement page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport12","12349@gmail.com","https://www.arcserve.com/saleschat",null,null,null,null,null,null,null,null},
			{"Navigate_from_Entitlement page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport1","12340@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat",null,null,null,null,null,null,null},
			{"Navigate_from_Entitlement page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC",null,null,null,null,null,null},
			{"Navigate_from_Entitlement page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support1","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve",null,null,null,null,null},
			{"Navigate_from_Entitlement page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support2","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12",null,null,null,null},
			{"Navigate_from_Entitlement page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support3","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx",null,null,null},
			{"Navigate_from_Entitlement page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support4","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx","https://www.arcserve.com/no/contact-arcserve.aspx",null,null},
			{"Navigate_from_Entitlement page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support4","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx","https://www.arcserve.com/no/contact-arcserve.aspx","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html",null},

			//When Navigate from Reports Page to Branding emialer page and create/Update the emailer fields
			//Create Branding emialer for the direct organization 

			{"Navigate_from_Reports page -Given all available filed to create Branding emailer for the Direct user",direct_org_email,common_password,"Arcserve","no@arcserve.com","https://www.arcserve.com/saleschat/123",
				"http://www.arcserve.com/supportchat/123","http://www.facebook.com/ArcserveLLC/123","http://twitter.com/Arcserve/123","http://www.linkedin.com/company/arcserve/123"
				,"http://www.arcserve.com/no/social-community.aspx/123","http://www.arcserve.com/no/contact-arcserve.aspx/123","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html/123",
			"https://www.arcserve.com/about/privacy/123"},

			//Given the one filed to create the bradning emialer and remaing fileds empty strings 
			//DirectOrg
			{"Navigate_from_Reports page-Given the Formemailadress to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","123@gmail.com","","","","","","","","",""},
			{"Navigate_from_Reports page-Given the Formemailname to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"Arcserve","","","","","","","","","",""},
			{"Navigate_from_Reports page-Given the SalesChat to create the brandingemialer for the Direct user and remaning fileds are emptyto direct user  ",direct_org_email,common_password,"","","http://www.arcserve.com/saleschat","","","","","","","",""},
			{"Navigate_from_Reports page-Given the SupportChat to create the brandingemialer for the Direct user and remaning fileds are empty to direct user ",direct_org_email,common_password,"","","","http://www.arcserve.com/supportchat","","","","","","",""},
			{"Navigate_from_Reports page-Given the Facebookfield to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,"","","","","http://www.facebook.com/ArcserveLLC","","","","","",""},
			{"Navigate_from_Reports page-Given the TwitterFiled to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","","","","","http://twitter.com/Arcserve","","","","",""},
			{"Navigate_from_Reports page-Given the LinkedInfiled to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","http://www.linkedin.com/company/arcserve","","","",""},
			{"Navigate_from_Reports page-Given the Socialmediplatform to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","","http://www.arcserve.com/no/social-community.aspx","","",""},
			{"Navigate_from_Reports page-Given the ContactUs to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,"","","","","","","","","http://www.arcserve.com/no/contact-arcserve.aspx","",""},
			{"Navigate_from_Reports page-Given the LegalNotice to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"","","","","","","","","","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html",""},
			{"Navigate_from_Reports page-Given the Privacy to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,"","","","","","","","","","","http://www.arcserve.com/about/privacy"},


			//Only passing one field at a time 
			//Direct Organization
			{"Navigate_from_Reports page-Given the Formemailadress to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,"1234@gmail.com",null,null,null,null,null,null,null,null,null},
			{"Navigate_from_Reports page-Given the Formemailname to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,"Arcserve123",null,null,null,null,null,null,null,null,null,null},
			{"Navigate_from_Reports page-Given the SalesChat to create the brandingemialer for the Direct user and remaning fileds are emptyto direct user  ",direct_org_email,common_password,null,null,"http://www.arcserve.com/saleschat12",null,null,null,null,null,null,null,null},
			{"Navigate_from_Reports page-Given the SupportChat to create the brandingemialer for the Direct user and remaning fileds are empty to direct user ",direct_org_email,common_password,null,null,null,"http://www.arcserve.com/supportchat12",null,null,null,null,null,null,null},
			{"Navigate_from_Reports page-Given the Facebookfield to create the bradningemialer and remaing fileds empty strings  to direct user ",direct_org_email,common_password,null,null,null,null,"http://www.facebook.com/ArcserveLLC12",null,null,null,null,null,null},
			{"Navigate_from_Reports page-Given the TwitterFiled to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,"http://twitter.com/Arcserve12",null,null,null,null,null},
			{"Navigate_from_Reports page-Given the LinkedInfiled to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,null,null,null,null,null,null,"http://www.linkedin.com/company/arcserve12",null,null,null,null},
			{"Navigate_from_Reports page-Given the Socialmediplatform to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,"http://www.arcserve.com/no/social-community.aspx12",null,null,null},
			{"Navigate_from_Reports page-Given the ContactUs to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,null,"http://www.arcserve.com/no/contact-arcserve.aspx12",null,null},
			{"Navigate_from_Reports page-Given the LegalNotice to create the bradningemialer and remaing fileds empty strings to direct user  ",direct_org_email,common_password,null,null,null,null,null,null,null,null,null,"http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html12",null},
			{"Navigate_from_Reports page-Given the Privacy to create the bradningemialer and remaing fileds empty strings to direct user ",direct_org_email,common_password,null,null,null,null,null,null,null,null,null,null,"http://www.arcserve.com/about/privacy12"},

			//Combination of all fields
			//Direct
			{"Navigate_from_Reports page-combinations of fileds to create Branding for the Direct user",direct_org_email,common_password,"ArcserveSupport","123456@gmail.com",null,null,null,null,null,null,null,null,null},
			{"Navigate_from_Reports page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport123","12347@gmail.com","https://www.arcserve.com/saleschat",null,null,null,null,null,null,null,null},
			{"Navigate_from_Reports page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport12","12349@gmail.com","https://www.arcserve.com/saleschat",null,null,null,null,null,null,null,null},
			{"Navigate_from_Reports page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"ArcserveSupport1","12340@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat",null,null,null,null,null,null,null},
			{"Navigate_from_Reports page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC",null,null,null,null,null,null},
			{"Navigate_from_Reports page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support1","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve",null,null,null,null,null},
			{"Navigate_from_Reports page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support2","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12",null,null,null,null},
			{"Navigate_from_Reports page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support3","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx",null,null,null},
			{"Navigate_from_Reports page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support4","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx","https://www.arcserve.com/no/contact-arcserve.aspx",null,null},
			{"Navigate_from_Reports page-Given combinations of fileds to create Branding for the Direct user ",direct_org_email,common_password,"Arcserve_Support4","1234@gmail.com","https://www.arcserve.com/saleschat","https://www.arcserve.com/supportchat","https://www.facebook.com/ArcserveLLC","https://twitter.com/Arcserve","https://www.linkedin.com/company/arcserve12","http://www.arcserve.com/no/social-community.aspx","https://www.arcserve.com/no/contact-arcserve.aspx","http://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html",null},


			
			
		};
	}

	
	
	@Test(dataProvider = "BrandingEmailerNavigation-info", enabled=true)
	public void CreateBradningEmailerTestWhenNavigateFormDifferentPage(String caseType, 
			String username,
			String password,
			String fromEmailName,
			String fromEmailAddress,
			String salesChart,
			String supportChart,
			String faceBook,
			String twitter,
			String linkedin,
			String socialMediaPlatform,
			String contactUs,
			String legalNotice,
			String privacy

			) {


		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		//brandingpageHelper.login_Spog(Username, password);

		brandingpageHelper.createBradningEmailerWhenNavigateFromOtherPages(caseType, fromEmailName, fromEmailAddress, salesChart, supportChart, faceBook, twitter, linkedin, socialMediaPlatform, contactUs, legalNotice, privacy);
	}


	//	@AfterMethod
	public void afterMethod(){
		//brandingpageHelper.logout();
		//brandingpageHelper.destroy();
	}
	private void prepareEnv(){

		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);

		//************************create msp org,user *************************************
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO,"create a msp org");
		msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name+org_model_prefix , SpogConstants.MSP_ORG, null, null, null, null, test);
		final_msp_user_name_email = prefix + msp_user_name_email;

		test.log(LogStatus.INFO,"create a admin under msp org");
		msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
		spogServer.userLogin(final_msp_user_name_email, common_password);

		//*********************Create Direct Org user****************************
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);


		this.direct_org_email = prefix + this.direct_org_email;
		direct_org_id = spogServer.CreateOrganizationWithCheck(prefix + direct_org_name + org_model_prefix,
				SpogConstants.DIRECT_ORG, this.direct_org_email, common_password, prefix + direct_org_first_name,
				prefix + direct_org_last_name, test);
		spogServer.userLogin(this.direct_org_email, common_password);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		direct_user_validToken = spogServer.getJWTToken();
		// direct_user_validToken=validToken;
		test.log(LogStatus.INFO, "The token is :" + direct_user_validToken);

		test.log(LogStatus.INFO, "Get the logged in user id ");
		direct_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The direct org user id is " + direct_user_id);


	}

	@AfterMethod
	public void getResult(ITestResult result){
		//brandingpageHelper.logout();

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

	@AfterClass
	public void afterClass() {
		brandingpageHelper.destroy();
		recycleVolumeInCDandDestroyOrg(org_model_prefix);
	}


}
