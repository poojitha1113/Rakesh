package base.prepare;

import org.apache.commons.lang3.RandomStringUtils;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.GatewayServer.siteType;
import genericutil.ExtentManager;
import io.restassured.response.Response;

public class PrepareOrgInfo extends Is4Org {
	
	public String pmfKey;
	public String postfix_email = "@arcserve.com";
	private String prefix_direct ;
	private String direct_org_name ;
	private String direct_org_email ;
	private String direct_org_first_name ;
	private String direct_org_last_name ;
	private String direct_user_name;
	private String direct_user_name_email ;
	private String direct_user_first_name ;
	private String direct_user_last_name;
	private String prefix_direct1 ;
	private String direct_org1_name ;
	private String direct_org1_email ;
	private String direct_org1_first_name ;
	private String direct_org1_last_name ;
	private String direct_user1_name;
	private String direct_user1_name_email ;
	private String direct_user1_first_name ;
	private String direct_user1_last_name;
	
	
	
	public String prefix_root_msp ;
	public String root_msp_org_name;
	public String root_msp_org_id;
	public String root_msp_user_id;
	public String root_msp_user_name_email;
	public String final_root_msp_user_name_email;
	public String root_msp_user_name ;
	public String root_msp_user_first_name ;
	public String root_msp_user_last_name ;
	
	public String prefix_sub_msp  ;
	public String sub_msp_org_name ;
	public String sub_msp1_org_id;
	public String sub_msp2_org_id;
	public String sub_msp1_user_id;
	public String sub_msp2_user_id;
	public String sub_msp_user_name_email ;
	public String final_sub_msp1_user_name_email;
	public String sub_msp_user_name;
	public String sub_msp_user_first_name ;
	public String sub_msp_user_last_name;
	public String sub_msp1_account1_id;
	public String final_sub_msp1_account1_user_email;
	public String sub_msp1_account1_user_id;
	public String sub_msp1_account2_id;
	public String final_sub_msp1_account2_user_email;
	public String sub_msp1_account2_user_id;
	public String sub_msp1_msp_account_admin_id;
	public String final_sub_msp1_msp_account_user_name_email;
	public String final_sub_msp2_user_name_email;
	public String sub_msp2_account1_id;
	public String sub_msp1_account1_name,sub_msp1_account2_name,sub_msp2_account1_name;
	public String final_sub_msp2_account1_user_email;
	public String sub_msp2_account1_user_id;

	public String root_msp_direct_org_id;
	public String root_msp_direct_org_name;
	public String root_msp_direct_org1_id;
	public String root_msp_direct_org1_name;
	
	public String final_root_msp_direct_org_user_email,final_root_msp_direct_org1_user_email;
	public String root_msp_direct_org_user_id,root_msp_direct_org1_user_id;
	public String final_root_msp_account_admin_user_name_email;
	public String root_msp_account_admin_user_id;
	
	private ExtentTest test;
	private SPOGServer spogServer;
	  private UserSpogServer userSpogServer;
	  private Org4SPOGServer org4SpogServer;
	  private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	  private String csrAdminUserName;
	  private String csrAdminPassword;

	  
	  public String common_password =  "Welcome*02";
	  private String org_model_prefix;

	  public String root_msp_direct_org_site1_siteName;
	  public String root_msp_direct_org_site1_siteId;
	  public String root_msp_direct_org_site1_token;

	  public String sub_msp1_account1_site1_siteId;
	  public String sub_msp1_account1_site1_token;
	  public String sub_msp1_account1_site2_siteId;
	  public String sub_msp1_account1_site2_token;
	  public String sub_msp1_account2_site1_siteId;
	  public String sub_msp1_account2_site1_token;
	  public String sub_msp2_account1_site1_siteId;
	  public String sub_msp2_account1_site1_token;

	public String root_msp_direct_org_site2_siteId;
	public String root_msp_direct_org_site2_token;
	
	
	public PrepareOrgInfo(String pmfKey){
		 this.pmfKey = pmfKey;
		 prefix_direct = "spogqa_"+pmfKey+ "_direct";
		 direct_org_name = prefix_direct + "_org";		 
		 direct_org_email = direct_org_name + postfix_email;
		 direct_org_first_name = direct_org_name + "_first_name";
		 direct_org_last_name = direct_org_name + "_last_name";
		 direct_user_name = prefix_direct + "_admin";
		 direct_user_name_email = prefix_direct + "_admin" + postfix_email;
		 direct_user_first_name = direct_user_name + "_first_name";
		 direct_user_last_name = direct_user_name + "_last_name";
		 
		 prefix_direct1 = "spogqa_"+pmfKey+ "_direct1";
		 direct_org1_name = prefix_direct1 + "_org1";
		 direct_org1_email = direct_org1_name + postfix_email;
		 direct_org1_first_name = direct_org1_name + "_first_name";
		 direct_org1_last_name = direct_org1_name + "_last_name";
		 direct_user1_name = prefix_direct1 + "_admin";
		 direct_user1_name_email = prefix_direct1 + "_admin" + postfix_email;
		 direct_user1_first_name = direct_user1_name + "_first_name";
		 direct_user1_last_name = direct_user1_name + "_last_name";
		 
		 prefix_root_msp = "spogqa_"+ pmfKey+"_root_msp";
		 root_msp_org_name = prefix_root_msp + "_org";	
		 root_msp_user_name_email = prefix_root_msp + "_admin" + postfix_email;
		 root_msp_user_name = prefix_root_msp + "_admin";
		 root_msp_user_first_name = root_msp_user_name + "_first_name";
		 root_msp_user_last_name = root_msp_user_name + "_last_name";

		prefix_sub_msp = "spogqa_"+pmfKey+"_sub_msp";
		sub_msp_org_name = prefix_sub_msp + "_org";
		sub_msp_user_name_email = prefix_sub_msp + "_admin" + postfix_email;
		sub_msp_user_name = prefix_sub_msp + "_admin";
		sub_msp_user_first_name = sub_msp_user_name + "_first_name";
		sub_msp_user_last_name = sub_msp_user_name + "_last_name";
	}
	
	public void prepare(String baseURI, String port, String logFolder, String adminUserName, String adminPassword, String className){
		
		createRootOrgAndSubOrgs(baseURI, port, logFolder, adminUserName, adminPassword, className, false);
		  
		
		//*******************create root msp***********************//*
		/*test.log(LogStatus.INFO,"create a root msp org");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		String csrToken = spogServer.getJWTToken();
		spogServer.setToken(csrToken);
		String prefix = RandomStringUtils.randomAlphanumeric(8)+org_model_prefix;;
		root_msp_org_id=spogServer.CreateOrganizationWithCheck(prefix+root_msp_org_name,"msp",null, null, null, null, test);
		test.log(LogStatus.INFO,"create a admin under root msp org");
		prefix = RandomStringUtils.randomAlphanumeric(8);
		final_root_msp_user_name_email = prefix + root_msp_user_name_email;
		root_msp_user_id = spogServer.createUserAndCheck(final_root_msp_user_name_email, common_password, prefix + root_msp_user_first_name, prefix + root_msp_user_last_name, SpogConstants.MSP_ADMIN, root_msp_org_id, test);  		
		test.log(LogStatus.INFO,"create a msp account admin under root msp org");
		final_root_msp_account_admin_user_name_email = "msp_account"+prefix + root_msp_user_name_email;
		root_msp_account_admin_user_id = spogServer.createUserAndCheck(final_root_msp_account_admin_user_name_email, common_password, prefix + root_msp_user_first_name, prefix + root_msp_user_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, root_msp_org_id, test);  		
		org4SpogServer.setToken(csrToken);
		org4SpogServer.convertToRootMSP(root_msp_org_id);
			
		test.log(LogStatus.INFO,"create a first sub msp org");
		prefix = RandomStringUtils.randomAlphanumeric(8);
		final_sub_msp1_user_name_email= prefix + sub_msp_user_name_email;
		sub_msp1_org_id=org4SpogServer.createSubMSPAccountincc(prefix+ sub_msp_org_name, root_msp_org_id, prefix + sub_msp_user_first_name, prefix + sub_msp_user_last_name,spogServer.ReturnRandom("spogqa_update_submsp1_rootmsp1@spogqa.com"), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		sub_msp1_user_id = spogServer.createUserAndCheck(final_sub_msp1_user_name_email, common_password, prefix + sub_msp_user_first_name, prefix + sub_msp_user_last_name, SpogConstants.MSP_ADMIN, sub_msp1_org_id, test);
		test.log(LogStatus.INFO,"create a msp account admin under sub msp org");
		final_sub_msp1_msp_account_user_name_email= "msp_account_admin_"+ prefix + sub_msp_user_name_email;
		sub_msp1_msp_account_admin_id = spogServer.createUserAndCheck(final_sub_msp1_msp_account_user_name_email, common_password, prefix + sub_msp_user_first_name, prefix + sub_msp_user_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, sub_msp1_org_id, test);
		
		
		
		
		test.log(LogStatus.INFO,"Creating first account For first sub msp org");		
		prefix = RandomStringUtils.randomAlphanumeric(8);
		sub_msp1_account1_name="sub_" + prefix + sub_msp_org_name;
		sub_msp1_account1_id = spogServer.createAccountWithCheck(sub_msp1_org_id,sub_msp1_account1_name , "", test);
		
		test.log(LogStatus.INFO,"Creating a account user For the first account org for sub msp org");
		final_sub_msp1_account1_user_email = prefix + sub_msp_user_name_email;
		sub_msp1_account1_user_id = spogServer.createUserAndCheck(final_sub_msp1_account1_user_email, common_password, prefix + sub_msp_user_name_email, prefix + sub_msp_user_name_email, SpogConstants.DIRECT_ADMIN, sub_msp1_account1_id, test);
		
		test.log(LogStatus.INFO,"assign account1 to sub msp account admin");
		String[] mspAccountAdmins = new String []{sub_msp1_msp_account_admin_id};
		userSpogServer.assignMspAccountAdmins(sub_msp1_org_id, sub_msp1_account1_id, mspAccountAdmins , csrToken); 
		
		
		
		test.log(LogStatus.INFO,"Creating second account For first sub msp org");
		prefix = RandomStringUtils.randomAlphanumeric(8);
		sub_msp1_account2_name="sub_" + prefix + sub_msp_org_name;
		sub_msp1_account2_id = spogServer.createAccountWithCheck(sub_msp1_org_id, sub_msp1_account2_name, "", test);
		
		test.log(LogStatus.INFO,"Creating a account user For the second account org for sub msp org");
		final_sub_msp1_account2_user_email = prefix + sub_msp_user_name_email;
		sub_msp1_account2_user_id = spogServer.createUserAndCheck(final_sub_msp1_account2_user_email, common_password, prefix + sub_msp_user_name_email, prefix + sub_msp_user_name_email, SpogConstants.DIRECT_ADMIN, sub_msp1_account2_id, test);
		
		
		test.log(LogStatus.INFO,"create a second sub msp org");
		prefix = RandomStringUtils.randomAlphanumeric(8);
		final_sub_msp2_user_name_email= prefix + sub_msp_user_name_email;
		sub_msp2_org_id=org4SpogServer.createSubMSPAccountincc(prefix+ sub_msp_org_name, root_msp_org_id, prefix + sub_msp_user_first_name, prefix + sub_msp_user_last_name,spogServer.ReturnRandom("spogqa_update_submsp2_rootmsp1@spogqa.com"), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		sub_msp2_user_id = spogServer.createUserAndCheck(final_sub_msp2_user_name_email, common_password, prefix + sub_msp_user_first_name, prefix + sub_msp_user_last_name, SpogConstants.MSP_ADMIN, sub_msp2_org_id, test);
		
		test.log(LogStatus.INFO,"Creating first account For second sub msp org");
		prefix = RandomStringUtils.randomAlphanumeric(8);
		sub_msp2_account1_name="sub_" + prefix + sub_msp_org_name;
		sub_msp2_account1_id = spogServer.createAccountWithCheck(sub_msp2_org_id, sub_msp2_account1_name, "", test);
		
		test.log(LogStatus.INFO,"Creating a account user For the first account org for second sub msp org");
		final_sub_msp2_account1_user_email = prefix + sub_msp_user_name_email;
		sub_msp2_account1_user_id = spogServer.createUserAndCheck(final_sub_msp2_account1_user_email, common_password, prefix + sub_msp_user_name_email, prefix + sub_msp_user_name_email, SpogConstants.DIRECT_ADMIN, sub_msp2_account1_id, test);
		
		test.log(LogStatus.INFO,"Creating an account For root msp org");
		prefix = RandomStringUtils.randomAlphanumeric(8);

		root_msp_direct_org_name=prefix + direct_org_name;		
		root_msp_direct_org_id = spogServer.createAccountWithCheck(root_msp_org_id, root_msp_direct_org_name, "", test);
		root_msp_direct_org1_name=prefix + direct_org1_name;
		root_msp_direct_org1_id = spogServer.createAccountWithCheck(root_msp_org_id, root_msp_direct_org1_name, "", test);

		test.log(LogStatus.INFO,"Creating a account user for the account under root msp org");
		final_root_msp_direct_org_user_email = prefix + direct_user_name_email;
		root_msp_direct_org_user_id = spogServer.createUserAndCheck(final_root_msp_direct_org_user_email, common_password, prefix + direct_user_name_email, prefix + direct_user_name_email, SpogConstants.DIRECT_ADMIN, root_msp_direct_org_id, test);
		
		final_root_msp_direct_org1_user_email = prefix + direct_user1_name_email;
		root_msp_direct_org1_user_id = spogServer.createUserAndCheck(final_root_msp_direct_org1_user_email, common_password, prefix + direct_user1_name_email, prefix + direct_user1_name_email, SpogConstants.DIRECT_ADMIN, root_msp_direct_org1_id, test);
		
		
		test.log(LogStatus.INFO,"assign account to root msp account admin");
		mspAccountAdmins = new String []{root_msp_account_admin_user_id};
		userSpogServer.assignMspAccountAdmins(root_msp_org_id, root_msp_direct_org_id, mspAccountAdmins , csrToken); 
		
		
		root_msp_direct_org_site1_siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+root_msp_direct_org_site1_siteName);
		String sitetype = siteType.gateway.toString();
	
		
		test.log(LogStatus.INFO,"Creating a site For root msp's direct org");
		root_msp_direct_org_site1_siteId = gatewayServer.createsite_register_login(root_msp_direct_org_id, csrToken , spogServer.GetLoggedinUser_UserID(), pmfKey, "1.0.0", spogServer, test);
		root_msp_direct_org_site1_token=gatewayServer.getJWTToken();
		
		test.log(LogStatus.INFO,"Creating another site For root msp's direct org");
		root_msp_direct_org_site2_siteId = gatewayServer.createsite_register_login(root_msp_direct_org_id, csrToken , spogServer.GetLoggedinUser_UserID(), pmfKey, "1.0.0", spogServer, test);
		root_msp_direct_org_site2_token=gatewayServer.getJWTToken();
		
		test.log(LogStatus.INFO,"Creating a site For sub msp1's account1");
		sub_msp1_account1_site1_siteId = gatewayServer.createsite_register_login(sub_msp1_account1_id, csrToken , spogServer.GetLoggedinUser_UserID(), pmfKey, "1.0.0", spogServer, test);
		sub_msp1_account1_site1_token=gatewayServer.getJWTToken();
		
		
		test.log(LogStatus.INFO,"Creating another site For sub msp1's account1");
		sub_msp1_account1_site2_siteId = gatewayServer.createsite_register_login(sub_msp1_account1_id, csrToken , spogServer.GetLoggedinUser_UserID(), pmfKey, "1.0.0", spogServer, test);
		sub_msp1_account1_site2_token=gatewayServer.getJWTToken();
		
		test.log(LogStatus.INFO,"Creating a site For sub msp1's account2");
		sub_msp1_account2_site1_siteId = gatewayServer.createsite_register_login(sub_msp1_account2_id, csrToken , spogServer.GetLoggedinUser_UserID(), pmfKey, "1.0.0", spogServer, test);
		sub_msp1_account2_site1_token=gatewayServer.getJWTToken();
		
		test.log(LogStatus.INFO,"Creating a site For sub msp2's account1");
		sub_msp2_account1_site1_siteId = gatewayServer.createsite_register_login(sub_msp2_account1_id, csrToken , spogServer.GetLoggedinUser_UserID(), pmfKey, "1.0.0", spogServer, test);
		sub_msp2_account1_site1_token=gatewayServer.getJWTToken();
		
		spogServer.postCloudhybridInFreeTrial(csrToken, root_msp_org_id, SpogConstants.MSP_ORG, false, false);*/

		
	}
	
	
	public void prepareByEnroll(String baseURI, String port, String logFolder, String adminUserName, String adminPassword, String className){
		
		
		createRootOrgAndSubOrgs(baseURI, port, logFolder, adminUserName, adminPassword, className, true);
		  		
	/*	*//*******************create root msp***********************//*
		test.log(LogStatus.INFO,"create a root msp org");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		String csrToken = spogServer.getJWTToken();
		String prefix = RandomStringUtils.randomAlphanumeric(8)+org_model_prefix;;
		
			
	 	String namePrefix = RandomStringUtils.randomAlphanumeric(8);
	 	final_root_msp_user_name_email = namePrefix + root_msp_user_name_email;
		root_msp_org_id=spogServer.CreateOrganizationByEnrollWithCheck(prefix+root_msp_org_name,"msp",final_root_msp_user_name_email, common_password, namePrefix + root_msp_user_first_name, namePrefix + root_msp_user_last_name);
		spogServer.userLogin(final_root_msp_user_name_email, common_password);
		root_msp_user_id=spogServer.GetLoggedinUser_UserID();
		
	
	    spogServer.setToken(csrToken);
	    prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO,"create a msp account admin under root msp org");
		final_root_msp_account_admin_user_name_email = "msp_account"+prefix + root_msp_user_name_email;
		root_msp_account_admin_user_id = spogServer.createUserAndCheck(final_root_msp_account_admin_user_name_email, common_password, prefix + root_msp_user_first_name, prefix + root_msp_user_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, root_msp_org_id, test);  		
		org4SpogServer.setToken(csrToken);
		//org4SpogServer.convertToRootMSP(root_msp_org_id);
		spogServer.convertTo3Tier(root_msp_org_id);
		
		test.log(LogStatus.INFO,"create a first sub msp org");
		prefix = RandomStringUtils.randomAlphanumeric(8);
		final_sub_msp1_user_name_email= prefix + sub_msp_user_name_email;
		sub_msp1_org_id=org4SpogServer.createSubMSPAccount(prefix+ sub_msp_org_name, root_msp_org_id, prefix + sub_msp_user_first_name, prefix + sub_msp_user_last_name,spogServer.ReturnRandom("spogqa_update_submsp1_rootmsp1@spogqa.com"), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		sub_msp1_user_id = spogServer.createUserAndCheck(final_sub_msp1_user_name_email, common_password, prefix + sub_msp_user_first_name, prefix + sub_msp_user_last_name, SpogConstants.MSP_ADMIN, sub_msp1_org_id, test);
		test.log(LogStatus.INFO,"create a msp account admin under sub msp org");
		final_sub_msp1_msp_account_user_name_email= "msp_account_admin_"+ prefix + sub_msp_user_name_email;
		sub_msp1_msp_account_admin_id = spogServer.createUserAndCheck(final_sub_msp1_msp_account_user_name_email, common_password, prefix + sub_msp_user_first_name, prefix + sub_msp_user_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, sub_msp1_org_id, test);
		
		
		
		
		test.log(LogStatus.INFO,"Creating first account For first sub msp org");		
		prefix = RandomStringUtils.randomAlphanumeric(8);
		sub_msp1_account1_name="sub_" + prefix + sub_msp_org_name;
		sub_msp1_account1_id = spogServer.createAccountWithCheck(sub_msp1_org_id,sub_msp1_account1_name , "", test);
		
		test.log(LogStatus.INFO,"Creating a account user For the first account org for sub msp org");
		final_sub_msp1_account1_user_email = prefix + sub_msp_user_name_email;
		sub_msp1_account1_user_id = spogServer.createUserAndCheck(final_sub_msp1_account1_user_email, common_password, prefix + sub_msp_user_name_email, prefix + sub_msp_user_name_email, SpogConstants.DIRECT_ADMIN, sub_msp1_account1_id, test);
		
		test.log(LogStatus.INFO,"assign account1 to sub msp account admin");
		String[] mspAccountAdmins = new String []{sub_msp1_msp_account_admin_id};
		userSpogServer.assignMspAccountAdmins(sub_msp1_org_id, sub_msp1_account1_id, mspAccountAdmins , csrToken); 
		
		
		
		test.log(LogStatus.INFO,"Creating second account For first sub msp org");
		prefix = RandomStringUtils.randomAlphanumeric(8);
		sub_msp1_account2_name="sub_" + prefix + sub_msp_org_name;
		sub_msp1_account2_id = spogServer.createAccountWithCheck(sub_msp1_org_id, sub_msp1_account2_name, "", test);
		
		test.log(LogStatus.INFO,"Creating a account user For the second account org for sub msp org");
		final_sub_msp1_account2_user_email = prefix + sub_msp_user_name_email;
		sub_msp1_account2_user_id = spogServer.createUserAndCheck(final_sub_msp1_account2_user_email, common_password, prefix + sub_msp_user_name_email, prefix + sub_msp_user_name_email, SpogConstants.DIRECT_ADMIN, sub_msp1_account2_id, test);
		
		
		test.log(LogStatus.INFO,"create a second sub msp org");
		prefix = RandomStringUtils.randomAlphanumeric(8);
		final_sub_msp2_user_name_email= prefix + sub_msp_user_name_email;
		sub_msp2_org_id=org4SpogServer.createSubMSPAccount(prefix+ sub_msp_org_name, root_msp_org_id, prefix + sub_msp_user_first_name, prefix + sub_msp_user_last_name,spogServer.ReturnRandom("spogqa_update_submsp2_rootmsp1@spogqa.com"), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		sub_msp2_user_id = spogServer.createUserAndCheck(final_sub_msp2_user_name_email, common_password, prefix + sub_msp_user_first_name, prefix + sub_msp_user_last_name, SpogConstants.MSP_ADMIN, sub_msp2_org_id, test);
		
		test.log(LogStatus.INFO,"Creating first account For second sub msp org");
		prefix = RandomStringUtils.randomAlphanumeric(8);
		sub_msp2_account1_name="sub_" + prefix + sub_msp_org_name;
		sub_msp2_account1_id = spogServer.createAccountWithCheck(sub_msp2_org_id, sub_msp2_account1_name, "", test);
		
		test.log(LogStatus.INFO,"Creating a account user For the first account org for second sub msp org");
		final_sub_msp2_account1_user_email = prefix + sub_msp_user_name_email;
		sub_msp2_account1_user_id = spogServer.createUserAndCheck(final_sub_msp2_account1_user_email, common_password, prefix + sub_msp_user_name_email, prefix + sub_msp_user_name_email, SpogConstants.DIRECT_ADMIN, sub_msp2_account1_id, test);
		
		test.log(LogStatus.INFO,"Creating an account For root msp org");
		prefix = RandomStringUtils.randomAlphanumeric(8);

		root_msp_direct_org_name=prefix + direct_org_name;		
		root_msp_direct_org_id = spogServer.createAccountWithCheck(root_msp_org_id, root_msp_direct_org_name, "", test);
		root_msp_direct_org1_name=prefix + direct_org1_name;
		root_msp_direct_org1_id = spogServer.createAccountWithCheck(root_msp_org_id, root_msp_direct_org1_name, "", test);

		test.log(LogStatus.INFO,"Creating a account user for the account under root msp org");
		final_root_msp_direct_org_user_email = prefix + direct_user_name_email;
		root_msp_direct_org_user_id = spogServer.createUserAndCheck(final_root_msp_direct_org_user_email, common_password, prefix + direct_user_name_email, prefix + direct_user_name_email, SpogConstants.DIRECT_ADMIN, root_msp_direct_org_id, test);
		
		final_root_msp_direct_org1_user_email = prefix + direct_user1_name_email;
		root_msp_direct_org1_user_id = spogServer.createUserAndCheck(final_root_msp_direct_org1_user_email, common_password, prefix + direct_user1_name_email, prefix + direct_user1_name_email, SpogConstants.DIRECT_ADMIN, root_msp_direct_org1_id, test);
		
		
		test.log(LogStatus.INFO,"assign account to root msp account admin");
		mspAccountAdmins = new String []{root_msp_account_admin_user_id};
		userSpogServer.assignMspAccountAdmins(root_msp_org_id, root_msp_direct_org_id, mspAccountAdmins , csrToken); 
		
		
		root_msp_direct_org_site1_siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+root_msp_direct_org_site1_siteName);
		String sitetype = siteType.gateway.toString();
	
		
		test.log(LogStatus.INFO,"Creating a site For root msp's direct org");
		root_msp_direct_org_site1_siteId = gatewayServer.createsite_register_login(root_msp_direct_org_id, csrToken , spogServer.GetLoggedinUser_UserID(), pmfKey, "1.0.0", spogServer, test);
		root_msp_direct_org_site1_token=gatewayServer.getJWTToken();
		
		test.log(LogStatus.INFO,"Creating another site For root msp's direct org");
		root_msp_direct_org_site2_siteId = gatewayServer.createsite_register_login(root_msp_direct_org_id, csrToken , spogServer.GetLoggedinUser_UserID(), pmfKey, "1.0.0", spogServer, test);
		root_msp_direct_org_site2_token=gatewayServer.getJWTToken();
		
		test.log(LogStatus.INFO,"Creating a site For sub msp1's account1");
		sub_msp1_account1_site1_siteId = gatewayServer.createsite_register_login(sub_msp1_account1_id, csrToken , spogServer.GetLoggedinUser_UserID(), pmfKey, "1.0.0", spogServer, test);
		sub_msp1_account1_site1_token=gatewayServer.getJWTToken();
		
		
		test.log(LogStatus.INFO,"Creating another site For sub msp1's account1");
		sub_msp1_account1_site2_siteId = gatewayServer.createsite_register_login(sub_msp1_account1_id, csrToken , spogServer.GetLoggedinUser_UserID(), pmfKey, "1.0.0", spogServer, test);
		sub_msp1_account1_site2_token=gatewayServer.getJWTToken();
		
		test.log(LogStatus.INFO,"Creating a site For sub msp1's account2");
		sub_msp1_account2_site1_siteId = gatewayServer.createsite_register_login(sub_msp1_account2_id, csrToken , spogServer.GetLoggedinUser_UserID(), pmfKey, "1.0.0", spogServer, test);
		sub_msp1_account2_site1_token=gatewayServer.getJWTToken();
		
		test.log(LogStatus.INFO,"Creating a site For sub msp2's account1");
		sub_msp2_account1_site1_siteId = gatewayServer.createsite_register_login(sub_msp2_account1_id, csrToken , spogServer.GetLoggedinUser_UserID(), pmfKey, "1.0.0", spogServer, test);
		sub_msp2_account1_site1_token=gatewayServer.getJWTToken();
		
		spogServer.postCloudhybridInFreeTrial(csrToken, root_msp_org_id, SpogConstants.MSP_ORG, false, false);*/

		
	}
	
	private void initialize(String baseURI, String port, String logFolder, String adminUserName, String adminPassword, String className){
		  
		 spogServer = new SPOGServer(baseURI, port);
	 	  userSpogServer = new UserSpogServer(baseURI, port);
		  org4SpogServer = new Org4SPOGServer(baseURI, port);
		  gatewayServer =new GatewayServer(baseURI,port);
		  spogDestinationServer=new SPOGDestinationServer(baseURI,port);
		  rep = ExtentManager.getInstance("PrepareOrgInfo",logFolder);
		  test = rep.startTest("prepare");
		  org_model_prefix = className;
		  this.csrAdminUserName=adminUserName;
		  this.csrAdminPassword=adminPassword;
	}
	
	private void createRootOrgAndSubOrgs(String baseURI, String port, String logFolder, String adminUserName, String adminPassword, String className, boolean enroll){
	
		initialize(baseURI,port,logFolder,adminUserName,adminPassword,className);
  		
		/*******************create root msp***********************/
		test.log(LogStatus.INFO,"create a root msp org");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		String csrToken = spogServer.getJWTToken();
		String prefix = RandomStringUtils.randomAlphanumeric(8)+org_model_prefix;;
		
			
	
	 	if(enroll){ 		
	 		String namePrefix = RandomStringUtils.randomAlphanumeric(8);
		 	final_root_msp_user_name_email = namePrefix + root_msp_user_name_email;
		 	root_msp_org_id=spogServer.CreateOrganizationByEnrollWithCheck(prefix+root_msp_org_name,"msp",final_root_msp_user_name_email, common_password, namePrefix + root_msp_user_first_name, namePrefix + root_msp_user_last_name);
		 	spogServer.userLogin(final_root_msp_user_name_email, common_password);
			root_msp_user_id=spogServer.GetLoggedinUser_UserID();
	 	}
	 		
	 	else{ 		
			root_msp_org_id=spogServer.CreateOrganizationWithCheck(prefix+root_msp_org_name,"msp",null, null, null, null, test);
			test.log(LogStatus.INFO,"create a admin under root msp org");
			prefix = RandomStringUtils.randomAlphanumeric(8);
			final_root_msp_user_name_email = prefix + root_msp_user_name_email;
			root_msp_user_id = spogServer.createUserAndCheck(final_root_msp_user_name_email, common_password, prefix + root_msp_user_first_name, prefix + root_msp_user_last_name, SpogConstants.MSP_ADMIN, root_msp_org_id, test);  		

	 	}
	
	    spogServer.setToken(csrToken);
	    prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO,"create a msp account admin under root msp org");
		final_root_msp_account_admin_user_name_email = "msp_account"+prefix + root_msp_user_name_email;
		root_msp_account_admin_user_id = spogServer.createUserAndCheck(final_root_msp_account_admin_user_name_email, common_password, prefix + root_msp_user_first_name, prefix + root_msp_user_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, root_msp_org_id, test);  		
		
		org4SpogServer.setToken(csrToken);
		if(enroll)
			spogServer.convertTo3Tier(root_msp_org_id);
		else
			org4SpogServer.convertToRootMSP(root_msp_org_id);
		
		
		test.log(LogStatus.INFO,"create a first sub msp org");
		prefix = RandomStringUtils.randomAlphanumeric(8);
		final_sub_msp1_user_name_email= prefix + sub_msp_user_name_email;
		if(enroll)
			sub_msp1_org_id=org4SpogServer.createSubMSPAccount(prefix+ sub_msp_org_name, root_msp_org_id, prefix + sub_msp_user_first_name, prefix + sub_msp_user_last_name,spogServer.ReturnRandom("spogqa_update_submsp1_rootmsp1@spogqa.com"), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		else
			sub_msp1_org_id=org4SpogServer.createSubMSPAccountincc(prefix+ sub_msp_org_name, root_msp_org_id, prefix + sub_msp_user_first_name, prefix + sub_msp_user_last_name,spogServer.ReturnRandom("spogqa_update_submsp1_rootmsp1@spogqa.com"), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		sub_msp1_user_id = spogServer.createUserAndCheck(final_sub_msp1_user_name_email, common_password, prefix + sub_msp_user_first_name, prefix + sub_msp_user_last_name, SpogConstants.MSP_ADMIN, sub_msp1_org_id, test);
		test.log(LogStatus.INFO,"create a msp account admin under sub msp org");
		final_sub_msp1_msp_account_user_name_email= "msp_account_admin_"+ prefix + sub_msp_user_name_email;
		sub_msp1_msp_account_admin_id = spogServer.createUserAndCheck(final_sub_msp1_msp_account_user_name_email, common_password, prefix + sub_msp_user_first_name, prefix + sub_msp_user_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, sub_msp1_org_id, test);
		
		
		test.log(LogStatus.INFO,"Creating first account For first sub msp org");		
		prefix = RandomStringUtils.randomAlphanumeric(8);
		sub_msp1_account1_name="sub_" + prefix + sub_msp_org_name;
		sub_msp1_account1_id = spogServer.createAccountWithCheck(sub_msp1_org_id,sub_msp1_account1_name , "", test);
		
		test.log(LogStatus.INFO,"Creating a account user For the first account org for sub msp org");
		final_sub_msp1_account1_user_email = prefix + sub_msp_user_name_email;
		sub_msp1_account1_user_id = spogServer.createUserAndCheck(final_sub_msp1_account1_user_email, common_password, prefix + sub_msp_user_name_email, prefix + sub_msp_user_name_email, SpogConstants.DIRECT_ADMIN, sub_msp1_account1_id, test);
		
		test.log(LogStatus.INFO,"assign account1 to sub msp account admin");
		String[] mspAccountAdmins = new String []{sub_msp1_msp_account_admin_id};
		userSpogServer.assignMspAccountAdmins(sub_msp1_org_id, sub_msp1_account1_id, mspAccountAdmins , csrToken); 
		
		
		test.log(LogStatus.INFO,"Creating second account For first sub msp org");
		prefix = RandomStringUtils.randomAlphanumeric(8);
		sub_msp1_account2_name="sub_" + prefix + sub_msp_org_name;
		sub_msp1_account2_id = spogServer.createAccountWithCheck(sub_msp1_org_id, sub_msp1_account2_name, "", test);
		
		test.log(LogStatus.INFO,"Creating a account user For the second account org for sub msp org");
		final_sub_msp1_account2_user_email = prefix + sub_msp_user_name_email;
		sub_msp1_account2_user_id = spogServer.createUserAndCheck(final_sub_msp1_account2_user_email, common_password, prefix + sub_msp_user_name_email, prefix + sub_msp_user_name_email, SpogConstants.DIRECT_ADMIN, sub_msp1_account2_id, test);
		
		
		test.log(LogStatus.INFO,"create a second sub msp org");
		prefix = RandomStringUtils.randomAlphanumeric(8);
		final_sub_msp2_user_name_email= prefix + sub_msp_user_name_email;
		if(enroll)
			sub_msp2_org_id=org4SpogServer.createSubMSPAccount(prefix+ sub_msp_org_name, root_msp_org_id, prefix + sub_msp_user_first_name, prefix + sub_msp_user_last_name,spogServer.ReturnRandom("spogqa_update_submsp2_rootmsp1@spogqa.com"), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		else
			sub_msp2_org_id=org4SpogServer.createSubMSPAccountincc(prefix+ sub_msp_org_name, root_msp_org_id, prefix + sub_msp_user_first_name, prefix + sub_msp_user_last_name,spogServer.ReturnRandom("spogqa_update_submsp2_rootmsp1@spogqa.com"), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		sub_msp2_user_id = spogServer.createUserAndCheck(final_sub_msp2_user_name_email, common_password, prefix + sub_msp_user_first_name, prefix + sub_msp_user_last_name, SpogConstants.MSP_ADMIN, sub_msp2_org_id, test);
		
		test.log(LogStatus.INFO,"Creating first account For second sub msp org");
		prefix = RandomStringUtils.randomAlphanumeric(8);
		sub_msp2_account1_name="sub_" + prefix + sub_msp_org_name;
		sub_msp2_account1_id = spogServer.createAccountWithCheck(sub_msp2_org_id, sub_msp2_account1_name, "", test);
		
		test.log(LogStatus.INFO,"Creating a account user For the first account org for second sub msp org");
		final_sub_msp2_account1_user_email = prefix + sub_msp_user_name_email;
		sub_msp2_account1_user_id = spogServer.createUserAndCheck(final_sub_msp2_account1_user_email, common_password, prefix + sub_msp_user_name_email, prefix + sub_msp_user_name_email, SpogConstants.DIRECT_ADMIN, sub_msp2_account1_id, test);
		
		test.log(LogStatus.INFO,"Creating an account For root msp org");
		prefix = RandomStringUtils.randomAlphanumeric(8);

		root_msp_direct_org_name=prefix + direct_org_name;		
		root_msp_direct_org_id = spogServer.createAccountWithCheck(root_msp_org_id, root_msp_direct_org_name, "", test);
		root_msp_direct_org1_name=prefix + direct_org1_name;
		root_msp_direct_org1_id = spogServer.createAccountWithCheck(root_msp_org_id, root_msp_direct_org1_name, "", test);

		test.log(LogStatus.INFO,"Creating a account user for the account under root msp org");
		final_root_msp_direct_org_user_email = prefix + direct_user_name_email;
		root_msp_direct_org_user_id = spogServer.createUserAndCheck(final_root_msp_direct_org_user_email, common_password, prefix + direct_user_name_email, prefix + direct_user_name_email, SpogConstants.DIRECT_ADMIN, root_msp_direct_org_id, test);
		
		final_root_msp_direct_org1_user_email = prefix + direct_user1_name_email;
		root_msp_direct_org1_user_id = spogServer.createUserAndCheck(final_root_msp_direct_org1_user_email, common_password, prefix + direct_user1_name_email, prefix + direct_user1_name_email, SpogConstants.DIRECT_ADMIN, root_msp_direct_org1_id, test);
		
		
		test.log(LogStatus.INFO,"assign account to root msp account admin");
		mspAccountAdmins = new String []{root_msp_account_admin_user_id};
		userSpogServer.assignMspAccountAdmins(root_msp_org_id, root_msp_direct_org_id, mspAccountAdmins , csrToken); 
		
		
		root_msp_direct_org_site1_siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+root_msp_direct_org_site1_siteName);
		String sitetype = siteType.gateway.toString();
	
		
		test.log(LogStatus.INFO,"Creating a site For root msp's direct org");
		root_msp_direct_org_site1_siteId = gatewayServer.createsite_register_login(root_msp_direct_org_id, csrToken , spogServer.GetLoggedinUser_UserID(), pmfKey, "1.0.0", spogServer, test);
		root_msp_direct_org_site1_token=gatewayServer.getJWTToken();
		
		test.log(LogStatus.INFO,"Creating another site For root msp's direct org");
		root_msp_direct_org_site2_siteId = gatewayServer.createsite_register_login(root_msp_direct_org_id, csrToken , spogServer.GetLoggedinUser_UserID(), pmfKey, "1.0.0", spogServer, test);
		root_msp_direct_org_site2_token=gatewayServer.getJWTToken();
		
		test.log(LogStatus.INFO,"Creating a site For sub msp1's account1");
		sub_msp1_account1_site1_siteId = gatewayServer.createsite_register_login(sub_msp1_account1_id, csrToken , spogServer.GetLoggedinUser_UserID(), pmfKey, "1.0.0", spogServer, test);
		sub_msp1_account1_site1_token=gatewayServer.getJWTToken();
		
		
		test.log(LogStatus.INFO,"Creating another site For sub msp1's account1");
		sub_msp1_account1_site2_siteId = gatewayServer.createsite_register_login(sub_msp1_account1_id, csrToken , spogServer.GetLoggedinUser_UserID(), pmfKey, "1.0.0", spogServer, test);
		sub_msp1_account1_site2_token=gatewayServer.getJWTToken();
		
		test.log(LogStatus.INFO,"Creating a site For sub msp1's account2");
		sub_msp1_account2_site1_siteId = gatewayServer.createsite_register_login(sub_msp1_account2_id, csrToken , spogServer.GetLoggedinUser_UserID(), pmfKey, "1.0.0", spogServer, test);
		sub_msp1_account2_site1_token=gatewayServer.getJWTToken();
		
		test.log(LogStatus.INFO,"Creating a site For sub msp2's account1");
		sub_msp2_account1_site1_siteId = gatewayServer.createsite_register_login(sub_msp2_account1_id, csrToken , spogServer.GetLoggedinUser_UserID(), pmfKey, "1.0.0", spogServer, test);
		sub_msp2_account1_site1_token=gatewayServer.getJWTToken();
		
		spogServer.postCloudhybridInFreeTrial(csrToken, root_msp_org_id, SpogConstants.MSP_ORG, false, false);

		
	}
	
}
