package api.destination;

import invoker.SiteTestHelper;
import io.restassured.response.Response;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;

import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.ErrorCode;
import Constants.SpogConstants;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.GatewayServer.siteType;
import InvokerServer.UserSpogServer;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class PostDestinationTest extends base.prepare.Is4Org{
	  private SPOGServer spogServer;
	  private SPOGDestinationServer spogDestinationServer;
	  private GatewayServer gatewayServer;
	  private UserSpogServer userSpogServer;
	  private String csrAdminUserName;
	  private String csrAdminPassword;
	 // private ExtentReports rep;
	  private ExtentTest test;
	  
	  private String csr_token=null;
	  private String postfix_email = "@arcserve.com";
	  private String common_password = "Welcome*02";
	  
	  private String csr_readonly_email = "csr_readonly_zhaoguo@arcserve.com";
	  private String csr_readonly_password = "Caworld_2017";
	  
	  private String prefix_direct = "SPOG_QA_Leiyu_BQ_direct";
	  private String direct_org_name = prefix_direct + "_org";
	  private String direct_org_id=null;
	  private String direct_user_name = prefix_direct + "_admin";
	  private String direct_user_name_email = direct_user_name + postfix_email;
	  private String direct_user_first_name = direct_user_name + "_first_name";
	  private String direct_user_last_name = direct_user_name + "_last_name";
	  private String direct_user_id =null;
	  private String final_direct_user_name_email = null;
	  private String direct_site_id;
	  private String direct_user_cloud_account_id;
	  
	  private String prefix_msp = "SPOG_QA_Leiyu_BQ_msp";
	  private String msp_org_name = prefix_msp + "_org";
	  private String msp_org_email = msp_org_name + postfix_email;
	  private String msp_org_first_name = msp_org_name + "_first_name";
	  private String msp_org_last_name = msp_org_name + "_last_name";
	  private String msp_user_name = prefix_msp + "_admin";
	  private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	  private String msp_user_first_name = msp_user_name + "_first_name";
	  private String msp_user_last_name = msp_user_name + "_last_name";
	  private String msp_user_id =null;
	  private String msp_org_id=null;
	  private String final_msp_user_name_email=null;	  
	  private String msp_site_id;
	  private String msp_user_cloud_account_id;
	  
	  private String msp_sub_org_name=prefix_msp + "_sub_org";
	  private String msp_sub_org_id=null;
	 
	  private String prefix_msp_account="SPOG_QA_Leiyu_BQ_msp_account_admin";
	  private String msp_account_admin_name = prefix_msp_account + "_admin";
	  private String msp_account_admin_email =msp_account_admin_name + postfix_email;
	  private String msp_account_admin_first_name = msp_account_admin_name + "_first_name";
	  private String msp_account_admin_last_name = msp_account_admin_name + "_last_name";
	  private String msp_account_admin_id;
	  private String final_msp_account_admin_email;	  
	  
	  private String account_id;
	  private String account_user_email;
	  private String account_user_id;
	  private String account_site_id;
	  private String another_direct_site_id;
	  private String another_msp_site_id;
	  private String another_account_site_id;
	  private String account_user_cloud_account_id;
			 	  
	  private String prefix_destination="dest_leiyu";
	  
	  private String cloud_account_id;
	  
	  private String  org_model_prefix=this.getClass().getSimpleName();
	  //this is for update portal, each testng class is taken as BQ set
	  /*private SQLServerDb bqdb1;
	  public int Nooftest;
	  private long creationTime;
	  private String BQName=null;
	  private String runningMachine;
	  private testcasescount count1;
	  private String buildVersion;
	  */

/**
 * 
 * @author leiyu.wang
 * 	Test Cases: 
direct admin could post destination in its organization
MSP admin could post destination in its organization
MSP admin could post destination in its child organization
csr admin could post destination in direct organization
csr admin could post destination in msp organization
csr admin could post destination in msp sub organization
account admin could post destination in its organization

post destination, destination_type as cloud_direct_volume
post destination, destination_type as cloud_direct_volume, set valid retention_id
post destination, destination_type as cloud_direct_volume, set custom retention_id
post destination, destination_type as cloud_hybrid_store
post destination, destination_status as running
post destination, destination_status as starting
post destination, destination_status as modifying
post destination, destination_status as deleting
post destination, destination_status as creating 
post destination without organization_id, it should use logged in user's organization id
post destination without site_id

MSP account admin could post destination of mastered organization
 * @param username
 * @param password
 * @param organization_id
 * @param site_id
 * @param destination_type
 * @param destination_status
 * @param primary_usage
 * @param snapshot_usage
 * @param total_usage
 * @param volume_type
 * @param hostname
 * @param retention_id
 * @param age_hours_max
 * @param age_four_hours_max
 * @param age_days_max
 * @param age_weeks_max
 * @param age_months_max
 * @param age_years_max
 * @param data_store_folder
 * @param data_destination
 * @param index_destination
 * @param hash_destination
 * @param concurrent_active_node
 * @param is_deduplicated
 * @param block_size
 * @param hash_memory
 * @param is_compressed
 * @param encryption_password
 * @param occupied_space
 * @param store_data
 * @param deduplication_rate
 * @param compression_rate
 */
  @Test(dataProvider = "destination201", enabled=true)
  public void createDestinations(String username, String password,String destination_id,String organization_id,String site_id, String destination_type, String destination_status,String dedupe_savings,
		  //cloud_direct_volume parameters
		  String cloud_account_id, String volume_type,String hostname, String  retention_id, String retention_name,
		  String age_hours_max,String age_four_hours_max,String age_days_max,String age_weeks_max,String age_months_max, String age_years_max,
		  //cloud_dedupe_volume parameters
		  String concurrent_active_node, String is_deduplicated, String block_size, String is_compressed) {
	  
	    test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  	test.assignAuthor("leiyu.wang");
	  	spogServer.errorHandle.printInfoMessageInDebugFile("/****************create destination**************/");
	  
		test.log(LogStatus.INFO,"admin login");
		spogServer.errorHandle.printInfoMessageInDebugFile("admin login");
		
		spogServer.userLogin(username, password);	  	
		
		spogDestinationServer.setToken(spogServer.getJWTToken());
		
		test.log(LogStatus.INFO,"create destination and check,expect succeed post");
		spogServer.errorHandle.printInfoMessageInDebugFile("create destination and check,expect succeed post");
				
		String[] datacenterIDs=spogDestinationServer.getDestionationDatacenterID();
	    int index=(int)Math.random()*datacenterIDs.length;
	    String datacenterID=datacenterIDs[index];
	    
	    test.log(LogStatus.INFO,"get datacenterID:" +datacenterID);
	    
	    String destination_name= prefix_destination+RandomStringUtils.randomAlphanumeric(3);
	    
		String destination_id_ret = spogDestinationServer.createDestinationWithCheck(destination_id,organization_id, site_id, datacenterID,destination_type, destination_status,dedupe_savings,
				cloud_account_id, volume_type, hostname, retention_id,retention_name,
				age_hours_max, age_four_hours_max, age_days_max, age_weeks_max, age_months_max, age_years_max,
				 concurrent_active_node, is_deduplicated,
				   block_size,  is_compressed,  destination_name,		
				test);
		
		test.log(LogStatus.INFO,"delete destination and check");
		if(destination_type.equals("cloud_direct_volume")){
			spogDestinationServer.RecycleDirectVolume(destination_id_ret, test);
		}else
			spogDestinationServer.deletedestinationbydestination_Id(destination_id_ret, spogServer.getJWTToken(), SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
			
		
  }

  /**
   * Test cases:
direct admin can't post destination in other organization
MSP admin could not post destination in other organization
account admin could not post destination in other organization
post destination without site_id
post destination with invalid organization_id will report error
post destination with invalid site_id will report error
post destination with invalid retention_id will report error
post destination with invalid destination_type will report error
post destination with invalid destination_folderPath will report error
   * @param username
   * @param password
   * @param organization_id
   * @param site_id
   * @param destination_type
   * @param destination_status
   * @param primary_usage
   * @param snapshot_usage
   * @param total_usage
   * @param volume_type
   * @param hostname
   * @param retention_id
   * @param age_hours_max
   * @param age_four_hours_max
   * @param age_days_max
   * @param age_weeks_max
   * @param age_months_max
   * @param age_years_max
   * @param data_store_folder
   * @param data_destination
   * @param index_destination
   * @param hash_destination
   * @param concurrent_active_node
   * @param is_deduplicated
   * @param block_size
   * @param hash_memory
   * @param is_compressed
   * @param encryption_password
   * @param occupied_space
   * @param store_data
   * @param deduplication_rate
   * @param compression_rate
   * @param statusCode
   * @param errorCode
   */
  @Test(dataProvider = "destinationPostFail",enabled=true)
  public void createDestinationErrorHandle(String username, String password,String destination_id,String organization_id,String site_id, String destination_type,
		  String destination_status,String dedupe_savings,
		  //cloud_direct_volume parameters
		  String cloud_account_id, String volume_type,String hostname, String  retention_id, String retention_name,
		  String age_hours_max,String age_four_hours_max,String age_days_max,String age_weeks_max,String age_months_max, String age_years_max,
		  //cloud_dedupe_volume parameters
		  String concurrent_active_node, String is_deduplicated, String block_size, String is_compressed,
		  int statusCode, String errorCode)  {
	  
	    test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  	test.assignAuthor("leiyu.wang");
	  	spogServer.errorHandle.printInfoMessageInDebugFile("/****************create destination**************/");
	  
		test.log(LogStatus.INFO,"admin login");
		spogServer.errorHandle.printInfoMessageInDebugFile("admin login");
	    spogServer.userLogin(username, password);
	  	
		test.log(LogStatus.INFO,"create destination and check, expect post fail");
		spogServer.errorHandle.printInfoMessageInDebugFile("create destination and check, expect post fail");
		
		spogDestinationServer.setToken(spogServer.getJWTToken());		
		String[] datacenterIDs=spogDestinationServer.getDestionationDatacenterID();
	    int index=(int)Math.random()*datacenterIDs.length;
	    String datacenterID=datacenterIDs[index];
	    
	    test.log(LogStatus.INFO,"get datacenterID:" +datacenterID);
	    
	    
	    String destination_name= prefix_destination+RandomStringUtils.randomAlphanumeric(3);
	    
		String destination_id_ret = spogDestinationServer.createDestinationWithCheckErrorCase(destination_id,organization_id, site_id, datacenterID,destination_type, destination_status,dedupe_savings,
				cloud_account_id, volume_type, hostname, retention_id,retention_name,
				age_hours_max, age_four_hours_max, age_days_max, age_weeks_max, age_months_max, age_years_max,
				  concurrent_active_node, is_deduplicated,
				   block_size,  is_compressed, destination_name,
				   statusCode, errorCode, 		
				test);

  }
  
  /**
   * post destination without token
   * post destination without datacenter_id
	 post destination with invalid datacenter_id will report error
   * @author leiyu.wang
   * @param username
   * @param password
   * @param organization_id
   * @param site_id
   * @param datacenter_id
   * @param destination_type
   * @param destination_status
   * @param primary_usage
   * @param snapshot_usage
   * @param total_usage
   * @param volume_type
   * @param hostname
   * @param retention_id
   * @param age_hours_max
   * @param age_four_hours_max
   * @param age_days_max
   * @param age_weeks_max
   * @param age_months_max
   * @param age_years_max
   * @param data_store_folder
   * @param data_destination
   * @param index_destination
   * @param hash_destination
   * @param concurrent_active_node
   * @param is_deduplicated
   * @param block_size
   * @param hash_memory
   * @param is_compressed
   * @param encryption_password
   * @param occupied_space
   * @param store_data
   * @param deduplication_rate
   * @param compression_rate
   */
  @Test(dataProvider = "WithDatacenterID", enabled=true)
  public void createDestinationErrorHandle(String username, String password, String destination_id,String organization_id,String site_id,String datacenter_id, String destination_type, String destination_status,String dedupe_savings,
		  //cloud_direct_volume parameters
		  String cloud_account_id, String volume_type,String hostname, String  retention_id, String retention_name, 
		  String age_hours_max,String age_four_hours_max,String age_days_max,String age_weeks_max,String age_months_max, String age_years_max,
		  //cloud_dedupe_volume parameters
		  String concurrent_active_node, String is_deduplicated, String block_size, String is_compressed,
		  int statusCode, String errorCode,boolean isLogin) {
	  	String destination_id_ret="";
	    test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  	test.assignAuthor("leiyu.wang");
	  	spogServer.errorHandle.printInfoMessageInDebugFile("/****************create destination**************/");
	  
		test.log(LogStatus.INFO,"admin login");
		spogServer.errorHandle.printInfoMessageInDebugFile("admin login");
		
		spogServer.userLogin(username, password);
		if (isLogin){		
			spogDestinationServer.setToken(spogServer.getJWTToken());
			//spogDestinationServer.setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1YzZkOTEzNS1mNDQ1LTQ1ODItYTE2Ni04N2Y2MDc1ZjhmMzciLCJ1c2VyX2lkIjoiNWM2ZDkxMzUtZjQ0NS00NTgyLWExNjYtODdmNjA3NWY4ZjM3IiwiZW1haWwiOiJ4aWFuZy5saUBhcmNzZXJ2ZS5jb20iLCJyb2xlX2lkIjoiY3NyX2FkbWluIiwib3JnYW5pemF0aW9uX2lkIjoiMzQ4ODRlODItMjIxMi00MzdlLThjZWQtOTIyNDAzMmQyYTEwIiwib3JnYW5pemF0aW9uX3R5cGUiOiJjc3IiLCJ1c2VyX3R5cGUiOiJVc2VyIiwiZXhwIjoxNTE2OTUwNDE0fQ.JgVdxAbxKE0pHvfF2gCWYIuJD3ee6p-HKujctiPPTQc");
		}else
			spogDestinationServer.setToken("");
		
		test.log(LogStatus.INFO,"create destination and check");
		spogServer.errorHandle.printInfoMessageInDebugFile("create destination and check");				
	    
	    String destination_name= prefix_destination+RandomStringUtils.randomAlphanumeric(3);
	    
		destination_id_ret = spogDestinationServer.createDestinationWithCheckErrorCase(destination_id,organization_id, site_id, datacenter_id,destination_type, destination_status,dedupe_savings,
				cloud_account_id, volume_type, hostname, retention_id,retention_name,
				age_hours_max, age_four_hours_max, age_days_max, age_weeks_max, age_months_max, age_years_max,
				concurrent_active_node, is_deduplicated,
				   block_size,  is_compressed, destination_name,statusCode, errorCode,	
				test);
		
		test.log(LogStatus.INFO,"delete destination and check");
		if(destination_id_ret!=null&&destination_id_ret!="null"){
			spogDestinationServer.deletedestinationbydestination_Id(destination_id_ret, spogServer.getJWTToken(), SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}
  }
  
  @DataProvider(name="destination201")
  public Object[][] destinationInfo() {
    return new Object[][]{
    			{ csrAdminUserName, csrAdminPassword, "",direct_org_id, direct_site_id,"cloud_direct_volume", "running", "1",
    				direct_user_cloud_account_id,"normal", "wanle05-win7", "7D","7D", "0", "0", "7", "0",
    				"0", "0",  "", "", "", "", },
				{ csrAdminUserName, csrAdminPassword,"", direct_org_id, direct_site_id,	"cloud_hybrid_store", "creating", "2",
    					"","normal", "wanle05-win7", "7D","7D", "0", "0", "7", "0",
    					"0", "0", "5", "true", "1",  "true" },
    			{ csrAdminUserName, csrAdminPassword, "",direct_org_id, account_site_id,	"cloud_direct_volume", "creating", "3",
    						direct_user_cloud_account_id,"normal", "wanle05-win7", "7D","7D", "0", "0", "7", "0",
        			"0", "0", "", "", "", "" },
				{ final_direct_user_name_email, common_password,"", direct_org_id,direct_site_id, "cloud_direct_volume", "creating","4",
        				direct_user_cloud_account_id, "normal", "wanle05-s2012", "7D", "7D","0",	"0", "7", "0", "0", "0", 
						"", "", "", ""},
				{ final_msp_user_name_email, common_password,"", account_id,msp_site_id, "cloud_direct_volume", "running", "5",
							msp_user_cloud_account_id, "normal", "wanle05-win7", "custom","custom", "0", "0","7", "0", "0", "0", 
						"", "", "", "" },
				{ final_msp_user_name_email, common_password,"", account_id,another_msp_site_id, "cloud_direct_volume","modifying", "6",
							msp_user_cloud_account_id, "normal", "wanle05-win7","custom","custom", "0", "0", "14", "0", "0", "0",
						"", "", "", ""},
				{ final_msp_user_name_email, common_password, "",account_id,another_msp_site_id, "cloud_direct_volume","modifying", "7",
							msp_user_cloud_account_id, "normal", "wanle05-s2012","14D","14D", "0", "0", "14", "0", "0", "0",
						"", "", "", "" },
				
				{ csrAdminUserName, csrAdminPassword,"", account_id, account_site_id, "cloud_hybrid_store", "deleting","8",
							"", "normal", "wanle05-win7", "2M", "2M", 
						"0","0", "31", "0", "2", "0", "5", "true", "1",  "true"},	
				{ csrAdminUserName, csrAdminPassword,"", account_id, account_site_id, "cloud_hybrid_store", "running","9",
							"", "normal", "wanle05-win7", "2M", "2M", 
						"0","0", "31", "0", "2", "0", "5", "true", "1", "true" },	
				{ csrAdminUserName, csrAdminPassword,"", account_id, account_site_id, "cloud_hybrid_store", "modifying","10",
							msp_user_cloud_account_id, "normal", "wanle05-win7", "2M", "2M", "0","0", "31", "0", "2", "0",
							 "5", "true", "1", "true" },			
				{ final_direct_user_name_email, common_password, "",direct_org_id,direct_site_id, "cloud_hybrid_store", "starting","11",
							"", "normal", "wanle05-s2012", "custom","custom",
						"0", "0", "0", "1", "0", "0", "5", "true", "1", "true"},
				{ final_direct_user_name_email, common_password, "",direct_org_id,direct_site_id, "cloud_hybrid_store", "starting", "12",
						"", "", "", "","", "0","0", "7", "0", "0", "0", 
						 "5", "true", "1", "true"},

				{ csrAdminUserName, csrAdminPassword,"", account_id, account_site_id,	"cloud_hybrid_store", "creating", "14",
							"","normal", "wanle05-win7", "7D","7D", "0", "0", "7", "0","0", "0", 
							 "5", "true", "1",  "true"},
				{ final_msp_account_admin_email, common_password,"", account_id, account_site_id,	"cloud_hybrid_store", "creating", "15",
						"","normal", "wanle05-win7", "7D","7D", "0", "0", "7", "0","0", "0", 
					"5", "true", "1", "true"},
				/*
				{ final_msp_user_name_email, common_password, "",account_id,account_site_id, "cloud_direct_volume","modifying", "7",
					msp_user_cloud_account_id, "normal", "wanle05-s2012","14D","14D", "0", "0", "14", "0", "0", "0",
					"", "", "", "" }*/

 
    };
  }
  
  
  /*
   * 
   *direct admin can't post destination in other organization
MSP admin could not post destination in other organization
account admin could not post destination in other organization

MSP account admin could not post destination of direct organization, report 403
MSP account admin could not post destination of msp organization, report 403
MSP account admin could not post destination of other sub organization, report 403


post destination with invalid organization_id will report error
post destination with invalid site_id will report error
post destination with invalid retention_id will report error
post destination with invalid destination_type will report error
post destination with invalid destination_folderPath will report error  not ready
   */
  @DataProvider(name="destinationPostFail")
  public Object[][] destinationPostFail(){
	  return new Object[][]{
			 { final_direct_user_name_email, common_password,"", msp_org_id,direct_site_id, "cloud_direct_volume", "creating","1",
				 direct_user_cloud_account_id, "normal", "wanle05-s2012", "7D","7D", "0",	"0", "7", "0", "0", "0", 
					"", "", "", "",SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION},
			{ final_msp_user_name_email, common_password,"", direct_org_id,msp_site_id, "cloud_direct_volume", "creating","2",
						msp_user_cloud_account_id, "normal", "wanle05-s2012", "7D","7D", "0",	"0", "7", "0", "0", "0", 
				"", "", "", "",SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION},
			{ account_user_email, common_password,"", direct_org_id,msp_site_id, "cloud_direct_volume", "creating","3",
					msp_user_cloud_account_id, "normal", "wanle05-s2012", "7D","7D", "0",	"0", "7", "0", "0", "0", 
				"", "", "", "",SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION},
			
			{ final_direct_user_name_email, common_password,"", msp_org_id,direct_site_id, "cloud_hybrid_store", "creating","4",
					 direct_user_cloud_account_id, "normal", "wanle05-s2012", "7D","7D", "0",	"0", "7", "0", "0", "0", 
						"", "", "", "",SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION},
			{ final_direct_user_name_email, common_password,"", account_id,direct_site_id, "cloud_hybrid_store", "creating","5",
					 direct_user_cloud_account_id, "normal", "wanle05-s2012", "7Dhf","7Dhf", "0",	"0", "7", "0", "0", "0", 
					 "", "", "", "",SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION},
			{ final_msp_user_name_email, common_password,"", direct_org_id,msp_site_id, "cloud_hybrid_store", "creating","6",
							msp_user_cloud_account_id, "normal", "wanle05-s2012", "7D","7D", "0",	"0", "7", "0", "0", "0", 
					"", "", "", "",SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION},
			{ account_user_email, common_password,"", direct_org_id,msp_site_id, "cloud_hybrid_store", "creating","7",
						msp_user_cloud_account_id, "normal", "wanle05-s2012", "7D","7D", "0",	"0", "7", "0", "0", "0", 
					"", "", "", "",SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION},
			{ account_user_email, common_password,"", msp_org_id,msp_site_id, "cloud_hybrid_store", "creating","8",
						msp_user_cloud_account_id, "normal", "wanle05-s2012", "7D","7D", "0",	"0", "7", "0", "0", "0", 
					"", "", "", "",SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION},
						 				
					
    		{ account_user_email, common_password,"", "2d6e1219-ed4a-4cd6-a9f1-ec7e039a76XX", account_site_id,"cloud_direct_volume", "running", "9",
						direct_user_cloud_account_id,"normal", "wanle05-win7", "7D","7D", "0", "0", "7", "0",
        		"0", "0", "2", "true", "1", "true",SpogConstants.REQUIRED_INFO_NOT_EXIST,ErrorCode.INVALID_PARAMETERS },    //00100001		invalid org id
        	/*{ csrAdminUserName, csrAdminPassword,"", direct_org_id, "bebc072a-7558-4b12-8d4c-ce0b60d3abXX","cloud_direct_volume", "running", "10",
        			direct_user_cloud_account_id,"normal", "wanle05-win7", "7Dhf","7Dhf", "0", "0", "7", "0",
            	"0", "0", "", "", "", "",SpogConstants.REQUIRED_INFO_NOT_EXIST,ErrorCode.INVALID_PARAMETERS},//"invalid site id" },   
*/            		//invalid retentionID
            { csrAdminUserName, csrAdminPassword,"", account_id, account_site_id,	"cloud_direct_volume", "creating", "11",
            		direct_user_cloud_account_id,"normal", "wanle05-win7", "00","00", "0", "0", "7", "0",
            	"0", "0", "", "", "", "",SpogConstants.REQUIRED_INFO_NOT_EXIST,ErrorCode.INVALID_RETENTION_ID},//"invalid retention ID" },           		
            			
            { csrAdminUserName, csrAdminPassword,"", account_id, account_site_id, "invalid_destination_type", "running","12",
            		msp_user_cloud_account_id, "normal", "wanle05-win7", "2M", "2M", 
    				"0","0", "31", "0", "2", "0","", "", "", "", SpogConstants.REQUIRED_INFO_NOT_EXIST,ErrorCode.NOT_VALID_ENUM},//"invalid invalid_destination_type" },     
    		//{ final_msp_user_name_email, common_password, msp_org_id,another_msp_site_id, "cloud_hybrid_store", "running", 
    		//			"","", "", "", "", "", "0","0", "0", "0", "0", "0", 
    		//			"invalid path", "invalid path", "invalid path", "invalid path", "5", "true", "1", "512", "true", "@@@!#", "1200", "3000", "10",	"20" ,400,"invalid folder path" },    //invalid folder path
    		{ csrAdminUserName, csrAdminPassword,"", direct_org_id, "","Null", "running", "13",
    					direct_user_cloud_account_id,"normal", "wanle05-win7", "7Dhf","7Dhf", "0", "0", "7", "0",
    		    	"0", "0", "", "", "", "",SpogConstants.REQUIRED_INFO_NOT_EXIST,ErrorCode.NOT_VALID_ENUM },     		
    		{ csrAdminUserName, csrAdminPassword,"", direct_org_id, direct_site_id,	"cloud_direct_volume", "creating", "14",
    		    		direct_user_cloud_account_id,"normal", "wanle05-win7", "Null","Null", "0", "0", "7", "0",
        			"0", "0", "", "", "", "",SpogConstants.REQUIRED_INFO_NOT_EXIST,ErrorCode.INVALID_RETENTION_ID },
        	
        	//MSP account admin user 		
        	{ final_msp_account_admin_email, common_password,"", direct_org_id, direct_site_id, "cloud_direct_volume", "creating","15",
       			direct_user_cloud_account_id, "normal", "wanle05-s2012", "7Dhf","7Dhf", "0",	"0", "7", "0", "0", "0", 
       			"", "", "", "" ,SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION},
           
           	//MSP account admin user 		
            { final_msp_account_admin_email, common_password,"", direct_org_id, direct_site_id, "cloud_hybrid_store", "creating","17",
           		direct_user_cloud_account_id, "normal", "wanle05-s2012", "7D","7D", "0",	"0", "7", "0", "0", "0", 
          		"", "", "", "" ,SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION},
            
            { csr_readonly_email, csr_readonly_password,"", direct_org_id, direct_site_id, "cloud_hybrid_store", "creating","17",
               		direct_user_cloud_account_id, "normal", "wanle05-s2012", "7D","7D", "0",	"0", "7", "0", "0", "0", 
              		"", "", "", "" , 403, "00100101"},

	  };
	  
  }
  
  
  @DataProvider(name="WithDatacenterID")
  public Object[][] WithDatacenterID(){
	  return new Object[][]{
			  { final_msp_user_name_email, common_password,"", msp_org_id,msp_site_id,"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", "cloud_direct_volume", "running", "19",
				  msp_user_cloud_account_id, "normal", "wanle05-win7", "7D","7D", "0", "0","7", "0", "0", "0", 
					"", "", "", "",SpogConstants.NOT_LOGGED_IN,"" ,false},//without token
			 { final_msp_user_name_email, common_password,"", account_id,account_site_id,"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", "cloud_hybrid_store", "running", "20",
						"", "normal", "wanle05-win7", "custom","custom", "0", "0","7", "0", "0", "0", 
						 "5", "true", "1",  "true", SpogConstants.SUCCESS_POST,"",true},
			{ final_msp_user_name_email, common_password,"", msp_org_id,another_msp_site_id,"d193e09c-efff-45f7-b929-ea138cd368XX", "cloud_direct_volume","modifying", "21",
					msp_user_cloud_account_id, "normal", "wanle05-win7","14D","14D", "0", "0", "14", "0", "0", "0",
					"", "", "", "",SpogConstants.REQUIRED_INFO_NOT_EXIST,ErrorCode.INVALID_PARAMETERS,true }, //invalid datacenterID
					

	  };
	  
  }
  
/*  @DataProvider(name="withDatacenterID")
  public Object[][] withDatacenterID(){
	  return new Object[][]{
			  { csrAdminUserName, csrAdminPassword, msp_org_id, msp_site_id,"1f2916f2-9271-49f6-9646-0eb4a6201111","cloud_direct_volume", "running", 
  				"10", "2", "5","archive", "wanle05-win7", "7D", "0", "0", "7", "0",
  				"0", "0", "", "", "", "", "", "", "","", "", "", "", "", "", "" }
	  }
  }*/
  
  @BeforeClass
  @Parameters({ "baseURI", "port", "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion"})
  public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword ,  String buildVersion) throws UnknownHostException {
	
	  	spogServer = new SPOGServer(baseURI, port);
	  	spogDestinationServer=new SPOGDestinationServer(baseURI,port);
	  	gatewayServer =new GatewayServer(baseURI,port);
	  	userSpogServer=new UserSpogServer(baseURI, port);
	  	rep = ExtentManager.getInstance("CreateDestinationTest",logFolder);
	  	this.csrAdminUserName = adminUserName;
	  	this.csrAdminPassword = adminPassword;

	    test = rep.startTest("beforeClass");
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		csr_token=spogServer.getJWTToken();
		//*******************create direct org,user,site**********************/
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO,"create a direct org");
		direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name+org_model_prefix , SpogConstants.DIRECT_ORG, null, common_password, null, null, test);
		final_direct_user_name_email = prefix + direct_user_name_email;
		
		test.log(LogStatus.INFO,"create a admin under direct org");
		direct_user_id = spogServer.createUserAndCheck(final_direct_user_name_email, common_password, prefix + direct_user_first_name, prefix + direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, test);
		spogServer.userLogin(final_direct_user_name_email, common_password);
	  	
		test.log(LogStatus.INFO,"Getting the JWTToken for the direct user");
		String direct_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ direct_user_validToken );
		
		String siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		String sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		
		test.log(LogStatus.INFO,"Creating a site For direct org");
		direct_site_id = gatewayServer.createsite_register_login(direct_org_id, direct_user_validToken, direct_user_id, "leiyu_directSite1", "1.0.0", spogServer, test);
		String direct_site_token=gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "The site token is "+direct_site_token);
		
		siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		
		test.log(LogStatus.INFO,"Creating another site For direct org");
		another_direct_site_id = gatewayServer.createsite_register_login(direct_org_id, direct_user_validToken, direct_user_id, "leiyu_directSite2", "1.0.0", spogServer, test);
		String another_direct_site_token=gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "The site token is "+another_direct_site_token);
		
		test.log(LogStatus.INFO, "creating cloudaccount for the user");
		//direct_user_cloud_account_id=spogServer.createCloudAccountWithCheck(prefix+"cloudAccountKey", prefix+"cloudAccountSecret", prefix+"cloudAccountName", "cloud_direct", direct_org_id, "SKUTESTDATA_1_4_0_0_"+prefix, "SKUTESTDATA_1_4_0_0_"+prefix,"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		
		//************************create msp org,user,site*************************************
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO,"create a msp org");
		msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name +org_model_prefix, SpogConstants.MSP_ORG, null, common_password, null, null, test);
		final_msp_user_name_email = prefix + msp_user_name_email;
		
		test.log(LogStatus.INFO,"create a admin under msp org");
		msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
		spogServer.userLogin(final_msp_user_name_email, common_password);
	  	
		test.log(LogStatus.INFO,"Getting the JWTToken for the msp user");
		String msp_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ msp_user_validToken );
		
		siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		
		/*test.log(LogStatus.INFO,"Creating a site For msp org");
		msp_site_id = gatewayServer.createsite_register_login(msp_org_id, msp_user_validToken, msp_user_id, "leiyu_MSPSite1", "1.0.0", spogServer, test);
		String msp_site_token=gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "The site token is "+ msp_site_token);*/
		
		siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		
		/*test.log(LogStatus.INFO,"Creating another site For msp org");
		another_msp_site_id = gatewayServer.createsite_register_login(msp_org_id, msp_user_validToken, msp_user_id, "leiyu_MSPSite2", "1.0.0", spogServer, test);
		String another_msp_site_token=gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "The site token is "+ another_msp_site_token);*/
		
		//msp_user_cloud_account_id=spogServer.createCloudAccountWithCheck(prefix+"cloudAccountKey", prefix+"cloudAccountSecret", prefix+"cloudAccountName", "cloud_direct", msp_org_id, "SKUTESTDATA_1_1_0_0_"+prefix, "SKUTESTDATA_1_1_0_0_"+prefix,"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		
		//create MSP account admin
		test.log(LogStatus.INFO,"create a msp account admin under msp org");
		final_msp_account_admin_email = prefix + this.msp_account_admin_email;
		msp_account_admin_id = spogServer.createUserAndCheck(final_msp_account_admin_email, common_password, prefix + msp_account_admin_first_name, prefix + msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, test);
		
		
		//create account, account user and site
		test.log(LogStatus.INFO,"Creating a account For msp org");
		account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
		
		//assign MSP account admin
		userSpogServer.assignMspAccountAdmins(msp_org_id,account_id, msp_account_admin_id, csr_token);
		
		prefix = RandomStringUtils.randomAlphanumeric(8);
	
		test.log(LogStatus.INFO,"Creating a account user For account org");
		account_user_email = prefix + msp_user_name_email;
		account_user_id = spogServer.createUserAndCheck(account_user_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, test);
		spogServer.userLogin(account_user_email, common_password);
	  	
		
		test.log(LogStatus.INFO,"Getting the JWTToken for the account user");
		String account_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ account_user_validToken );
		
		siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		
		test.log(LogStatus.INFO,"Creating a site For account org");
		account_site_id = gatewayServer.createsite_register_login(account_id, account_user_validToken, account_user_id, "leiyu_accountSite1", "1.0.0", spogServer, test);
		String account_site_token=gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "The site token is "+ account_site_token);
		
		//account_user_cloud_account_id=spogServer.createCloudAccountWithCheck(prefix+"cloudAccountKey", prefix+"cloudAccountSecret", prefix+"cloudAccountName", "cloud_direct", account_id,"SKUTESTDATA_1_0_0_0_123", "SKUTESTDATA_1_0_0_0_123", test);
		
		siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		
		test.log(LogStatus.INFO,"Creating another site For account org");
		another_account_site_id = gatewayServer.createsite_register_login(account_id, account_user_validToken, account_user_id, "leiyu_accountSite2", "1.0.0", spogServer, test);
		String another_account_site_token=gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "The site token is "+ another_account_site_token);
		
		
        spogServer.postCloudhybridInFreeTrial(csr_token, direct_org_id, SpogConstants.DIRECT_ORG, false, false);
        spogServer.postCloudhybridInFreeTrial(csr_token, msp_org_id, SpogConstants.MSP_ORG, false, false);
		
	  	//this is for update portal
	  	this.BQName = this.getClass().getSimpleName();
	    String author = "leiyu.wang";
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
	            try {
                    bqdb1.updateTable(this.BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
	            } catch (ClientProtocolException e) {
	                  // TODO Auto-generated catch block
	                  e.printStackTrace();
	            } catch (IOException e) {
	                  // TODO Auto-generated catch block
	                  e.printStackTrace();
	            }
	        }  
	    

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

}
