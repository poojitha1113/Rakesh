package api.policies;

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
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.TaskType;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import dataPreparation.policyPreparation;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

/**
 * API to test policy creation with task type SQL Server backup
 * 
 * Prerequisites: 
 * CSR token to enroll organizations
 * Direct, MSP, SUB organizations and users under it 
 * Sources, Cloud Direct destinations in all organizations
 * 
 * The payload:
 * "cloud_direct_sql_server_backup": {
        "sql_backup_type": "string",     <= this value must be "bak", you should get an error if it is anything else
        "sql_backup_default": "string",  <= this value must be boolean (true/false) or if set as string value: "true", "false" (case insensitive)
        "sql_backup_local_instance": [        <= this is the local sql instance(s), it can be empty, null, or any number of strings 
          "string"
        ],
        "sql_backup_path": "string",   <= this should be a valid path in your windows system, otherwise the backup will fail
        "sql_verify_enabled": "string", <= this value must be boolean (true/false) or if set as string value: "true", "false" (case insensitive)  
        "local_backup": {                        <= this is the same as local backup setting for other tasks
          "path": "string",
          "enabled": "string"
        }
      }

 * 
 * @author Rakesh.Chalamala
 * Sprint 25
 */

public class CreateSQLServerBackupPolicyTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private Policy4SPOGServer policy4SPOGServer;
	private SPOGHypervisorsServer spogHypervisorsServer;
	private UserSpogServer userSpogServer;
	private policyPreparation pp;          
	//public int Nooftest;
	private ExtentTest test;
	//used for test case count like passed,failed,remaining cases
	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;
	String buildnumber=null;

    /*private ExtentReports rep;
    private SQLServerDb bqdb1;
    public int Nooftest;
    private long creationTime;
    private String BQName=null;
    private String runningMachine;
    private testcasescount count1;
    private String buildVersion;*/
	String direct_cloud_id;
	String msp_cloud_id;
	Response response;
	String cloudAccountSecret;
	String direct_destination_ID;
	String msp_destination_ID;
	String sub_orga_destination_ID;
	String submsp_suborg_destination_id;

	String direct_source_id;
	String msp_source_id;
	String sub_orga_source_id;
	String submsp_suborg_source_id;
	String prefix = RandomStringUtils.randomAlphanumeric(8);

	private String  org_model_prefix=this.getClass().getSimpleName();
	private TestOrgInfo ti;
	
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port,String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		policy4SPOGServer= new Policy4SPOGServer(baseURI, port);
		spogHypervisorsServer = new SPOGHypervisorsServer(baseURI,port);
		userSpogServer = new UserSpogServer(baseURI, port);
		pp = new policyPreparation();
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Kanamarlapudi, Chandra Kanth";

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
		ti = new TestOrgInfo(spogServer, test);
		
		response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		direct_cloud_id = response.then().extract().path("data[0].cloud_account_id");
		
		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		msp_cloud_id = response.then().extract().path("data[0].cloud_account_id");

		spogDestinationServer.setToken(ti.csr_token);
		String[] datacenters = spogDestinationServer.getDestionationDatacenterID();
		
		response = spogDestinationServer.getDestinations(ti.direct_org1_user1_token, "destination_type="+DestinationType.cloud_direct_volume, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);;
		direct_destination_ID = response.then().extract().path("data[0].destination_id");

		response = spogDestinationServer.getDestinations(ti.root_msp1_suborg1_user1_token, "destination_type="+DestinationType.cloud_direct_volume, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);;
		sub_orga_destination_ID = response.then().extract().path("data[0].destination_id");

		response = spogDestinationServer.getDestinations(ti.msp1_submsp1_suborg1_user1_token, "destination_type="+DestinationType.cloud_direct_volume, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);;
		submsp_suborg_destination_id = response.then().extract().path("data[0].destination_id");

		//Create source in all organization
		spogServer.setToken(ti.direct_org1_user1_token);
		direct_source_id = spogServer.createSourceWithCheck(spogServer.ReturnRandom("src"), SourceType.machine, SourceProduct.cloud_direct, ti.direct_org1_id, direct_cloud_id,
				ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange", "Rak_vm", null, "Rak_agent1",
				"windows 2012",  "64", "1.0.0", "2.0", "http://upgrade", test);

		spogServer.setToken(ti.root_msp1_suborg1_user1_token);
		response = spogServer.createSource(spogServer.ReturnRandom("src"), SourceType.machine, SourceProduct.cloud_direct, ti.root_msp1_suborg1_id, msp_cloud_id,
				ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange", "Rak_vm", null, "Rak_agent1",
				"windows 2012",  "64", "1.0.0", "2.0", "http://upgrade", test);
		sub_orga_source_id = response.then().extract().path("data.source_id");

		spogServer.setToken(ti.msp1_submsp1_suborg1_user1_token);
		response = spogServer.createSource(spogServer.ReturnRandom("src"), SourceType.machine, SourceProduct.cloud_direct, ti.msp1_submsp1_sub_org1_id, msp_cloud_id,
				ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange", "Rak_vm", null, "Rak_agent1",
				"windows 2012",  "64", "1.0.0", "2.0", "http://upgrade", test);
		submsp_suborg_source_id = response.then().extract().path("data.source_id");
	}

	@DataProvider(name = "testCases")
	public final Object[][] testCases() {
		return new Object[][] {
			//200
			{"Create SQL Server backup policy in direct org with valid request payload",
				ti.direct_org1_id,ti.direct_org1_user1_token,direct_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
				direct_source_id,direct_destination_ID,"bak","true",new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
				SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with msp token valid request payload",
					ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
					sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
					SpogConstants.SUCCESS_POST, null},			
			{"Create SQL Server backup policy in sub org with valid request payload",
					ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
					sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),"C:\\sql_backup_path","false",
					SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with msp account admin token and valid request payload",
					ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
					sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),"C:\\sql_backup_path","false",
					SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with sub msp token valid request payload",
					ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
					submsp_suborg_source_id,submsp_suborg_destination_id,"bak","true",new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
					SpogConstants.SUCCESS_POST, null},			
			{"Create SQL Server backup policy in sub msp sub org with valid request payload",
					ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
					submsp_suborg_source_id,submsp_suborg_destination_id,"bak","true",new ArrayList<String>(),"C:\\sql_backup_path","false",
					SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with sub msp account admin token and valid request payload",
					ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp1_account_admin_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
					submsp_suborg_source_id,submsp_suborg_destination_id,"bak","true",new ArrayList<String>(),"C:\\sql_backup_path","false",
					SpogConstants.SUCCESS_POST, null},
			
			//sql_backup_local_instance value can be null when Default backup value is true
			{"Create SQL Server backup policy in direct org with sql_backup_local_instance value as null",
								ti.direct_org1_id,ti.direct_org1_user1_token,direct_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								direct_source_id,direct_destination_ID,"bak","true",new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
								SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with msp token sql_backup_local_instance value as null",
								ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
								SpogConstants.SUCCESS_POST, null},			
			{"Create SQL Server backup policy in sub org with sql_backup_local_instance value as null",
								ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),"C:\\sql_backup_path","false",
								SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with msp account admin token and sql_backup_local_instance value as null",
								ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),"C:\\sql_backup_path","false",
								SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with sub msp token sql_backup_local_instance value as null",
									ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
									submsp_suborg_source_id,submsp_suborg_destination_id,"bak","true",new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
									SpogConstants.SUCCESS_POST, null},			
			{"Create SQL Server backup policy in sub msp sub org with sql_backup_local_instance value as null",
									ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
									submsp_suborg_source_id,submsp_suborg_destination_id,"bak","true",new ArrayList<String>(),"C:\\sql_backup_path","false",
									SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with sub msp account admin token and sql_backup_local_instance value as null",
									ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp1_account_admin_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
									submsp_suborg_source_id,submsp_suborg_destination_id,"bak","true",new ArrayList<String>(),"C:\\sql_backup_path","false",
									SpogConstants.SUCCESS_POST, null},
				
			//400 
			// SQLServer Default backup value as false
			{"Create SQL Server backup policy in direct org with SQLServer Default backup value as false",
						ti.direct_org1_id,ti.direct_org1_user1_token,direct_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
						direct_source_id,direct_destination_ID,"bak","false",new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.SQLSERVER_DEFAULT_BACKUP_MUST_BE_TRUE_LOCAL_INSTANCE_DOESNOT_HAVE_VALUE},
			{"Create SQL Server backup policy in sub org with MSP token and SQLServer Default backup value as false",
						ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
						sub_orga_source_id,sub_orga_destination_ID,"bak","false",new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.SQLSERVER_DEFAULT_BACKUP_MUST_BE_TRUE_LOCAL_INSTANCE_DOESNOT_HAVE_VALUE},			
			{"Create SQL Server backup policy in sub org with SQLServer Default backup value as false",
						ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
						sub_orga_source_id,sub_orga_destination_ID,"bak","false",new ArrayList<String>(),"C:\\sql_backup_path","false",
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.SQLSERVER_DEFAULT_BACKUP_MUST_BE_TRUE_LOCAL_INSTANCE_DOESNOT_HAVE_VALUE},
			{"Create SQL Server backup policy in sub org with msp account admin token and SQLServer Default backup value as false",
						ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
						sub_orga_source_id,sub_orga_destination_ID,"bak","false",new ArrayList<String>(),"C:\\sql_backup_path","false",
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.SQLSERVER_DEFAULT_BACKUP_MUST_BE_TRUE_LOCAL_INSTANCE_DOESNOT_HAVE_VALUE},
			
			 // SQLServer Default backup value as null
			{"Create SQL Server backup policy in direct org with SQLServer Default backup value as null",
							ti.direct_org1_id,ti.direct_org1_user1_token,direct_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							direct_source_id,direct_destination_ID,"bak",null,new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
							SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with MSP token and SQLServer Default backup value as null",
							ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID,"bak",null,new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
							SpogConstants.SUCCESS_POST, null},			
			{"Create SQL Server backup policy in sub org with SQLServer Default backup value as null",
							ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID,"bak",null,new ArrayList<String>(),"C:\\sql_backup_path","false",
							SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with msp account admin token and SQLServer Default backup value as null",
							ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID,"bak",null,new ArrayList<String>(),"C:\\sql_backup_path","false",
							SpogConstants.SUCCESS_POST, null},

			// SQLServer Default backup value as empty
			{"Create SQL Server backup policy in direct org with SQLServer Default backup value as empty string",
							ti.direct_org1_id,ti.direct_org1_user1_token,direct_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							direct_source_id,direct_destination_ID,"bak","",new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
							SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with MSP token and SQLServer Default backup value as empty string",
							ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID,"bak","",new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
							SpogConstants.SUCCESS_POST, null},			
			{"Create SQL Server backup policy in sub org with SQLServer Default backup value as empty string",
							ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID,"bak","",new ArrayList<String>(),"C:\\sql_backup_path","false",
							SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with msp account admin token and SQLServer Default backup value as empty string",
							ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID,"bak","",new ArrayList<String>(),"C:\\sql_backup_path","false",
							SpogConstants.SUCCESS_POST, null},
			
			// SQLServer Default backup value as invalid
			{"Create SQL Server backup policy in direct org with SQLServer Default backup value as invalid",
							ti.direct_org1_id,ti.direct_org1_user1_token,direct_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							direct_source_id,direct_destination_ID,"bak","invalid",new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
							SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_NOT_A_BOOLEAN_VALUE},
			{"Create SQL Server backup policy in sub org with msp token and  SQLServer Default backup value as invalid",
							ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID,"bak","invalid",new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
							SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_NOT_A_BOOLEAN_VALUE},			
			{"Create SQL Server backup policy in sub org with SQLServer Default backup value as invalid",
							ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID,"bak","invalid",new ArrayList<String>(),"C:\\sql_backup_path","false",
							SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_NOT_A_BOOLEAN_VALUE},
			{"Create SQL Server backup policy in sub org with msp account admin token and SQLServer Default backup value as invalid",
							ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID,"bak","invalid",new ArrayList<String>(),"C:\\sql_backup_path","false",
							SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_NOT_A_BOOLEAN_VALUE},
			
			
			// SQLServer backup type as empty string
			{"Create SQL Server backup policy in direct org with SQLServer backup type as empty string",
								ti.direct_org1_id,ti.direct_org1_user1_token,direct_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								direct_source_id,direct_destination_ID,"","true",new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
								SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.SQLSERVER_DEFAULT_BACKUP_MUST_BE_TRUE_LOCAL_INSTANCE_DOESNOT_HAVE_VALUE},
			{"Create SQL Server backup policy in sub org with msp token and  SQLServer backup type as empty string",
								ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								sub_orga_source_id,sub_orga_destination_ID,"","true",new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
								SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.SQLSERVER_DEFAULT_BACKUP_MUST_BE_TRUE_LOCAL_INSTANCE_DOESNOT_HAVE_VALUE},			
			{"Create SQL Server backup policy in sub org with SQLServer backup type as empty string",
								ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								sub_orga_source_id,sub_orga_destination_ID,"","true",new ArrayList<String>(),"C:\\sql_backup_path","false",
								SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.SQLSERVER_DEFAULT_BACKUP_MUST_BE_TRUE_LOCAL_INSTANCE_DOESNOT_HAVE_VALUE},
			{"Create SQL Server backup policy in sub org with msp account admin token and SQLServer backup type as empty string",
								ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								sub_orga_source_id,sub_orga_destination_ID,"","true",new ArrayList<String>(),"C:\\sql_backup_path","false",
								SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.SQLSERVER_DEFAULT_BACKUP_MUST_BE_TRUE_LOCAL_INSTANCE_DOESNOT_HAVE_VALUE},
				
			// SQLServer backup type as null
			{"Create SQL Server backup policy in direct org with SQLServer backup type as null",
								ti.direct_org1_id,ti.direct_org1_user1_token,direct_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								direct_source_id,direct_destination_ID,null,"true",new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
								SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.SQLSERVER_DEFAULT_BACKUP_MUST_BE_TRUE_LOCAL_INSTANCE_DOESNOT_HAVE_VALUE},
			{"Create SQL Server backup policy in sub org with msp token and  SQLServer backup type as null",
								ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								sub_orga_source_id,sub_orga_destination_ID,null,"true",new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
								SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.SQLSERVER_DEFAULT_BACKUP_MUST_BE_TRUE_LOCAL_INSTANCE_DOESNOT_HAVE_VALUE},			
			{"Create SQL Server backup policy in sub org with SQLServer backup type as null",
								ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								sub_orga_source_id,sub_orga_destination_ID,null,"true",new ArrayList<String>(),"C:\\sql_backup_path","false",
								SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.SQLSERVER_DEFAULT_BACKUP_MUST_BE_TRUE_LOCAL_INSTANCE_DOESNOT_HAVE_VALUE},
			{"Create SQL Server backup policy in sub org with msp account admin token and SQLServer backup type as null",
								ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								sub_orga_source_id,sub_orga_destination_ID,null,"true",new ArrayList<String>(),"C:\\sql_backup_path","false",
								SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.SQLSERVER_DEFAULT_BACKUP_MUST_BE_TRUE_LOCAL_INSTANCE_DOESNOT_HAVE_VALUE},
						
			// SQLServer backup type as empty invalid	
			{"Create SQL Server backup policy in direct org with SQLServer backup type as invalid",
							ti.direct_org1_id,ti.direct_org1_user1_token,direct_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							direct_source_id,direct_destination_ID,"invalidbak","true",new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
							SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.SQL_BACKUP_TYPE_IS_NOT_A_VALID_VALUE},
			{"Create SQL Server backup policy in sub org with msp token and  SQLServer backup type as invalid",
							ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID,"invalidbak","true",new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
							SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.SQL_BACKUP_TYPE_IS_NOT_A_VALID_VALUE},			
			{"Create SQL Server backup policy in sub org with SQLServer backup type as invalid",
							ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID,"invalidbak","true",new ArrayList<String>(),"C:\\sql_backup_path","false",
							SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.SQL_BACKUP_TYPE_IS_NOT_A_VALID_VALUE},
			{"Create SQL Server backup policy in sub org with msp account admin token and SQLServer backup type as invalid",
							ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID,"invalidbak","true",new ArrayList<String>(),"C:\\sql_backup_path","false",
							SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.SQL_BACKUP_TYPE_IS_NOT_A_VALID_VALUE},
			
			
			// sql_backup_path value as null
			{"Create SQL Server backup policy in direct org with sql_backup_path value as null",
							ti.direct_org1_id,ti.direct_org1_user1_token,direct_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							direct_source_id,direct_destination_ID,"bak","true",new ArrayList<String>(),null,"false",
							SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with msp token and  sql_backup_path value as null",
							ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),null,"false",
							SpogConstants.SUCCESS_POST, null},			
			{"Create SQL Server backup policy in sub org with sql_backup_path value as null",
							ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),null,"false",
							SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with msp account admin token and sql_backup_path value as null",
							ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),null,"false",
							SpogConstants.SUCCESS_POST, null},

			// sql_backup_path value as empty string
			{"Create SQL Server backup policy in direct org with sql_backup_path value as empty string",
							ti.direct_org1_id,ti.direct_org1_user1_token,direct_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							direct_source_id,direct_destination_ID,"bak","true",new ArrayList<String>(),"","false",
							SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with msp token and  sql_backup_path value as empty string",
							ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),"","false",
							SpogConstants.SUCCESS_POST, null},			
			{"Create SQL Server backup policy in sub org with sql_backup_path value as empty string",
							ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),"","false",
							SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with msp account admin token and sql_backup_path value as empty string",
							ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),"","false",
							SpogConstants.SUCCESS_POST, null},
						
			// sql_backup_path value as invalid
			{"Create SQL Server backup policy in direct org with sql_backup_path value as invalid",
							ti.direct_org1_id,ti.direct_org1_user1_token,direct_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							direct_source_id,direct_destination_ID,"bak","true",new ArrayList<String>(),"invalid","false",
							SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with msp token and  sql_backup_path value as invalid",
							ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),"invalid","false",
							SpogConstants.SUCCESS_POST, null},			
			{"Create SQL Server backup policy in sub org with sql_backup_path value as invalid",
							ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),"invalid","false",
							SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with msp account admin token and sql_backup_path value as invalid",
							ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),"invalid","false",
							SpogConstants.SUCCESS_POST, null},
			
			//sql_verify_enabled value as null
			{"Create SQL Server backup policy in direct org with sql_verify_enabled value as null",
								ti.direct_org1_id,ti.direct_org1_user1_token,direct_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								direct_source_id,direct_destination_ID,"bak","true",new ArrayList<String>(),"C:\\sql_backup_path",null,
								SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with msp token and sql_verify_enabled value as null",
								ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),"C:\\sql_backup_path",null,
								SpogConstants.SUCCESS_POST, null},			
			{"Create SQL Server backup policy in sub org with sql_verify_enabled value as null",
								ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),"C:\\sql_backup_path",null,
								SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with msp account admin token and sql_verify_enabled value as null",
								ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),"C:\\sql_backup_path",null,
								SpogConstants.SUCCESS_POST, null},

			// sql_verify_enabled value as empty string
			{"Create SQL Server backup policy in direct org with sql_verify_enabled value as empty string",
								ti.direct_org1_id,ti.direct_org1_user1_token,direct_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								direct_source_id,direct_destination_ID,"bak","true",new ArrayList<String>(),"C:\\sql_backup_path","",
								SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with msp token and  sql_verify_enabled value as empty string",
								ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),"C:\\sql_backup_path","",
								SpogConstants.SUCCESS_POST, null},			
			{"Create SQL Server backup policy in sub org with sql_verify_enabled value as empty string",
								ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),"C:\\sql_backup_path","",
								SpogConstants.SUCCESS_POST, null},
			{"Create SQL Server backup policy in sub org with msp account admin token and sql_verify_enabled value as empty string",
								ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),"C:\\sql_backup_path","",
								SpogConstants.SUCCESS_POST, null},
									
			// sql_verify_enabled value as invalid
			{"Create SQL Server backup policy in direct org with sql_verify_enabled value as invalid",
								ti.direct_org1_id,ti.direct_org1_user1_token,direct_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								direct_source_id,direct_destination_ID,"bak","true",new ArrayList<String>(),"C:\\sql_backup_path","invalid",
								SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_NOT_A_BOOLEAN_VALUE},
			{"Create SQL Server backup policy in sub org with msp token and  sql_verify_enabled value as invalid",
								ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),"C:\\sql_backup_path","invalid",
								SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_NOT_A_BOOLEAN_VALUE},			
			{"Create SQL Server backup policy in sub org with sql_verify_enabled value as invalid",
								ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),"C:\\sql_backup_path","invalid",
								SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_NOT_A_BOOLEAN_VALUE},
			{"Create SQL Server backup policy in sub org with msp account admin token and sql_verify_enabled value as invalid",
								ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,msp_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
								sub_orga_source_id,sub_orga_destination_ID,"bak","true",new ArrayList<String>(),"C:\\sql_backup_path","invalid",
								SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_NOT_A_BOOLEAN_VALUE},
												
			//401
			{"Create SQL Server backup policy with invalid token",
										ti.direct_org1_id,"invalid",direct_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
										direct_source_id,direct_destination_ID,"bak","true",new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
										SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			{"Create SQL Server backup policy with null as token",
										ti.direct_org1_id,null,direct_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
										direct_source_id,direct_destination_ID,"bak","true",new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
										SpogConstants.NOT_LOGGED_IN,  SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			{"Create SQL Server backup policy with missing token",
										ti.direct_org1_id,"",direct_cloud_id,"cloud_direct_sql_server_backup","cloud_direct_baas",
										direct_source_id,direct_destination_ID,"bak","true",new ArrayList<String>(),"C:\\"+spogServer.ReturnRandom("sql_backup_path"),"false",
										SpogConstants.NOT_LOGGED_IN,  SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
		};
	}

	@Test(dataProvider = "testCases",enabled=true)
	public void createPolicyTaskTypeSQLServerBackup(String caseType,
													String organization_id,
													String validToken,
													String cloud_account_id,
													String task_type,
													String policy_type,
													String source_id,
													String destination_id,
													String sql_backup_type,
													String sql_backup_default,
													ArrayList<String> sql_backup_local_instance,
													String sql_backup_path,
													String sql_verify_enabled,
													int expectedStatusCode,
													SpogMessageCode expectedErrorMessage
													) {

		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.log(LogStatus.INFO, caseType);

		String schedule_id=spogServer.returnRandomUUID();
		String task_id=spogServer.returnRandomUUID();
		String throttle_id=spogServer.returnRandomUUID();
		String throttle_type="network";
		String policy_name=spogServer.ReturnRandom("test");
		String policy_description=spogServer.ReturnRandom("description");
		String policy_id = spogServer.returnRandomUUID();
		String cron_string = "0 6 * * *";
		

		ArrayList<HashMap<String,Object>>  destinations = new ArrayList<>();
		HashMap<String, Object> expectedResponse = null;

		spogDestinationServer.setToken(validToken);
		spogServer.setToken(validToken);
		policy4SPOGServer.setToken(validToken);

		test.log(LogStatus.INFO, "Create cloud direct schedule");
		HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO(cron_string, test);

		test.log(LogStatus.INFO, "Create schedule settings");
		HashMap<String, Object> scheduleSettingDTO = policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);

		test.log(LogStatus.INFO, "Create schedules");
		ArrayList<HashMap<String,Object>> schedules = policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
				"1d", task_id, destination_id, scheduleSettingDTO, "06:00", "12:00", test);

		test.log(LogStatus.INFO, "Exclude the folders from cloud direct file backup");  
		ArrayList<HashMap<String,Object>> excludes = policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\tmp", test);
		HashMap<String, Object> cloudDirectLocalBackupDTO = policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true", excludes, test);

		ArrayList<String> drivers = new ArrayList<>();
		drivers.add("C");
		
		if (caseType.contains("sql_backup_local_instance")) {
			sql_backup_local_instance = null;
		}
		
		HashMap<String, Object> cloudDirectSQLServerBackupTaskInfoDTO = policy4SPOGServer.createCloudDirectSQLServerBackupTaskInfoDTO(sql_backup_type, sql_backup_default, sql_backup_local_instance, sql_backup_path, sql_verify_enabled, cloudDirectLocalBackupDTO, test);

		test.log(LogStatus.INFO, "Create task type and link it to the destination ");
		destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, destination_id, "none", null, null, null,null,cloudDirectSQLServerBackupTaskInfoDTO, test);		

		test.log(LogStatus.INFO, "Create network throttle ");
		ArrayList<HashMap<String,Object>> throttles = policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id, task_id, throttle_type, "1200", "1", "06:00", "18:00", test);

		policy4SPOGServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create a policy of type backup_recovery and task of type "+task_type);
		Response response = policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true", source_id, destinations, schedules, throttles, policy_id, organization_id, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);
		
		if (expectedStatusCode == SpogConstants.SUCCESS_POST) { //Compose only for valid cases
			
			ArrayList<HashMap<String, Object>> allSourcesInfo = new ArrayList<>();
	         HashMap<String, Object> sourceInfo = null;
	         String[] src = source_id.replace(" ", "").split(",");
	         
	         for (int i = 0; i < src.length; i++) {
	   		sourceInfo = new HashMap<>();
	   		sourceInfo.put("source_id", src[i]);
	   		allSourcesInfo.add(sourceInfo);
	         }
			
			//Composing expected response for validation
			expectedResponse = (HashMap<String, Object>) pp.composePolicyCreateRequestDTO(policy_name, policy_description,
					policy_type, null, "true",  throttles, allSourcesInfo, destinations, schedules,
					policy_id, organization_id);
		}
		
		//Actual validation of response
		policy4SPOGServer.checkCDSQLServerBackupTask(response, expectedResponse, expectedStatusCode, expectedErrorMessage, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {
			//Check for policy status
			for (int i = 0; i < 15; i++) {
				response = policy4SPOGServer.getPolicyById(validToken, policy_id, test);	
				String policy_status = response.then().extract().path("data.policy_status");
				if (policy_status.equalsIgnoreCase("deploying")) {
					//Policy_status takes time to get update from state deploying
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					i++;
				}else {
					break;
				}
				if (i==15) {
					System.out.println("Policy is in deploying status only for the past 5 minuites");
					test.log(LogStatus.FAIL, "Policy is in deploying status only for the past 5 minuites");
				}
			}		
			
			test.log(LogStatus.INFO, "Delete the policy by policy id");
			policy4SPOGServer.deletePolicybyPolicyId(validToken, policy_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);	
		}
		
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
