package api.users.jobs.columns;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

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
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class CreateJobColumnsForspecifiedUserTest extends base.prepare.Is4Org{
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGJobServer spogJobServer;
	private ExtentTest test;
	//used for test case count like passed,failed,remaining cases
	private String  org_model_prefix=this.getClass().getSimpleName();
	ArrayList<String> columnIdList = new ArrayList<String>();
	ArrayList<HashMap<String,Object>> columnsHeadContent = new ArrayList<HashMap<String,Object>>();
	private TestOrgInfo ti;
	
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port,String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogJobServer = new SPOGJobServer(baseURI, port);
		rep = ExtentManager.getInstance("CreateJobColumnsForspecifiedUserTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Ramesh.Pen";
		
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
		
		spogServer.setToken(ti.direct_org1_user1_token);
		Response response = spogServer.getJobsColumns(test);	

		columnsHeadContent = response.then().extract().path("data");
		int length = columnsHeadContent.size();
		for (int i = 0; i < length; i++) {
			HashMap<String,Object> HeadContent = columnsHeadContent.get(i);
			columnIdList.add((String) HeadContent.get("column_id"));
		}
	}

	@DataProvider(name = "create_job_columns_valid")
	public final Object[][] createJobColumnsValidParams() {
		
		return new Object[][] {
			// different users
				{ "csr", ti.csr_org_id,ti.csr_token,ti.csr_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"4","7","3","2","9","6","8","5","1"},9},
				{ "csr_readonly", ti.csr_org_id,ti.csr_readonly_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"4","7","3","2","9","6","8","5","1"},9},
			
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"4","7","3","2","9","6","8","5","1"},9},
				{ "msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"9","7","8","3","4","5","2","6",},4},
				{ "suborg", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"3","9","6","2","5","8","4","7","1"},6},
				{ "suborgmsptoken", ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","7","8","9"},4},
				{ "submsp", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"9","7","8","3","4","5","2","6",},4},
				{ "submsp_suborg", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"3","9","6","2","5","8","4","7","1"},6},
				{ "suborg_submsptoken", ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","7","8","9"},4}
				
		};
	}
	
	@DataProvider(name = "create_job_columns_valid_visible_true")
	public final Object[][] createJobColumnsValidParams_Visible_True() {
		
		return new Object[][] {
			// different users
				{ "csr", ti.csr_org_id,ti.csr_token,ti.csr_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true" },
					new String[] { "true", "false","none"},new String[]{"4","7","3","2","9","6","8","5","1"},9},
				{ "csr_readonly", ti.csr_org_id,ti.csr_readonly_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true" },
					new String[] { "true", "false","none"},new String[]{"4","7","3","2","9","6","8","5","1"},9},
			
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true" },
					new String[] { "true", "false","none"},new String[]{"4","7","3","2","9","6","8","5","1"},9},
				{ "msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"9","7","8","3","4","5","2","6",},4},
				{ "suborg", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"3","9","6","2","5","8","4","7","1"},6},
				{ "suborgmsptoken", ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","7","8","9"},4},
				{ "submsp", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"9","7","8","3","4","5","2","6",},4},
				{ "submsp_suborg", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"3","9","6","2","5","8","4","7","1"},6},
				{ "suborg_submsptoken", ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","7","8","9"},4}
				
		};
	}
	
	@DataProvider(name = "create_job_columns_valid_visible_false")
	public final Object[][] createJobColumnsValidParams_Visible_False() {
		
		return new Object[][] {
			// different users
				{ "csr", ti.csr_org_id,ti.csr_token,ti.csr_admin_user_id, new String[] { "true", "false","none"}, new String[] { "false"},
					new String[] { "true", "false","none"},new String[]{"4","7","3","2","9","6","8","5","1"},9},
				{ "csr_readonly", ti.csr_org_id,ti.csr_readonly_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false","none"}, new String[] { "false"},
					new String[] { "true", "false","none"},new String[]{"4","7","3","2","9","6","8","5","1"},9},
			
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "false"},
					new String[] { "true", "false","none"},new String[]{"4","7","3","2","9","6","8","5","1"},9},
				{ "msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"9","7","8","3","4","5","2","6",},4},
				{ "suborg", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"3","9","6","2","5","8","4","7","1"},6},
				{ "suborgmsptoken", ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","7","8","9"},4},
				{ "submsp", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"9","7","8","3","4","5","2","6",},4},
				{ "submsp_suborg", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"3","9","6","2","5","8","4","7","1"},6},
				{ "suborg_submsptoken", ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","7","8","9"},4}

		};
	}
	
	
	@DataProvider(name = "create_job_columns_same_order_id")
	public final Object[][] createJobColumnsforsameorderID() {
		
		return new Object[][] {
			// different users
				{ "csr", ti.csr_org_id,ti.csr_token,ti.csr_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","2","2","2","2"},5},
				{ "csr_readonly", ti.csr_org_id,ti.csr_readonly_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","2","2","2","2"},5},
			
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","2","2","2","2"},5},
				{ "msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"5","5","5","5"},4},
				{ "suborg", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"8","8","8","8"},4},
				{ "suborgmsptoken", ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"6","6","6","6"},4},
				{ "submsp", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"5","5","5","5"},4},
				{ "submsp_suborg", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"8","8","8","8"},4},
				{ "suborg_submsptoken", ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"6","6","6","6"},4}
			
		};
	}
	
	@DataProvider(name = "create_job_columns_otheruser_id")
	public final Object[][] createJobColumnsforotherUserId() {
		
		return new Object[][] {
			// different users
				{ "direct_msp", ti.direct_org1_id,ti.direct_org1_user1_token,ti.root_msp_org1_user1_id,new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"4","7","3","2","9","6","8","5","1"},9},
				{ "msp_direct", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"9","7","8","3","4","5","2","6",},4},
				{ "suborg_direct", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"3","9","6","2","5","8","4","7","1"},6},
				{ "suborg_mspb", ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,ti.root_msp_org2_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","7","8","9"},4},
				{ "suborg_suborgb", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg2_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"3","9","6","2","5","8","4","7","1"},6}
				
		};
	}
	
	@DataProvider(name = "create_job_columns_403")
	public final Object[][] createJobColumnsfor403Scenarios() {
		
		return new Object[][] {
			// different users
				{ "direct_msp", ti.direct_org1_id,ti.root_msp_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"4","7","3","2","9","6","8","5","1"},9},
				{ "msp_suborg", ti.root_msp_org1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"9","7","8","3","4","5","2","6",},4},
				{ "msp_mspb", ti.root_msp_org1_id,ti.root_msp_org2_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"9","7","8","3","4","5","2","6",},4},
				{ "suborg_direct", ti.root_msp1_suborg1_id,ti.direct_org1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"3","9","6","2","5","8","4","7","1"},6},
				{ "suborg_suborgb", ti.root_msp1_suborg1_id,ti.root_msp1_suborg2_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","7","8","9"},4}
				
		};
	}
	
	@DataProvider(name = "create_job_columns_csr_403")
	public final Object[][] createJobColumnsforcsr403Scenarios() {
		
		return new Object[][] {
			// different users
				{ "csr_direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.csr_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"4","7","3","2","9","6","8","5","1"},9},
				{ "csr_msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.csr_admin_user_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"9","7","8","3","4","5","2","6",},4},
				{ "csr_suborg", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.csr_admin_user_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"3","9","6","2","5","8","4","7","1"},6},
				
				{ "csr_readonly_direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"4","7","3","2","9","6","8","5","1"},9},
				{ "csr_readonly_msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"9","7","8","3","4","5","2","6",},4},
				{ "csr_readonly_suborg", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"3","9","6","2","5","8","4","7","1"},6},
				
		};
	}
	
	@DataProvider(name = "create_job_columns_negative_order_id")
	public final Object[][] createJobColumnsfornegativeorderID() {
		
		return new Object[][] {
			// different users
				{ "csr", ti.csr_org_id,ti.csr_token,ti.csr_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"-1","6","3","5"},4},
				{ "csr_readonly", ti.csr_org_id,ti.csr_readonly_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"-1","6","3","5"},4},
			
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"-1","6","3","5"},4},
				{ "msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"5","1","6","-9"},4},
				{ "suborg", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"7","5","-3","2"},4},
				{ "suborgmsptoken", ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"-6","1","-2","3"},4}
				
		};
	}
	
	
	@DataProvider(name = "create_job_columns_order_id_zero")
	public final Object[][] createJobColumnsforzeroorderID() {
		
		return new Object[][] {
			// different users
				{ "csr", ti.csr_org_id,ti.csr_token,ti.csr_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"1","0","3","5"},4},
				{ "csr_readonly", ti.csr_org_id,ti.csr_readonly_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"1","0","3","5"},4},
			
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"1","0","3","5"},4},
				{ "msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"5","0","6","9"},4},
				{ "suborg", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"7","0","3","2"},4},
				{ "suborgmsptoken", ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
					new String[] { "true", "false"},new String[]{"6","1","2","0"},4}
				
		};
	}
	

	// Create Job Columns using respective Organization's Token
	
	@Test(dataProvider = "create_job_columns_valid")
	public void CreateJobColumnsForSpecifiedUser(String organizationType,
												 String org_Id,
												 String validToken,
												 String user_Id,
												 String[] sort,
												 String[] filter,
												 String[] visible,
												 String[] orderId, 
												 int noofjobcolumns 
												 ){	 
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		test.assignAuthor("");
		spogJobServer.setToken(validToken);
		
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;

		for(int i=0;i<noofjobcolumns ;i++)
		{
			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);
			
			temp = spogJobServer.composejob_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
			expected_columns.add(temp);
		}

		test.log(LogStatus.INFO, "Create job columns in the org: "+organizationType + "using validToken");
		Response response= spogJobServer.createJobColumnsForSpecifiedUser(user_Id,ti.csr_token,expected_columns,test);
		
		test.log(LogStatus.INFO, "Compare job columns");
		spogJobServer.compareJobColumnsContent(response, expected_columns,columnsHeadContent,SpogConstants.SUCCESS_POST,null, test);
		
		test.log(LogStatus.INFO, "Delete job columns by userid in the org: "+organizationType);
		spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
	
	}
	
	// Create Job Columns using CSR Token
	
	@Test(dataProvider = "create_job_columns_valid")
	public void CreateJobColumnsForSpecifiedUser_csrtoken(String organizationType,
												 String org_Id,
												 String validToken,
												 String user_Id,
												 String[] sort,
												 String[] filter,
												 String[] visible,
												 String[] orderId, 
												 int noofjobcolumns 
												 ){	 
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		test.assignAuthor("");
		spogJobServer.setToken(validToken);
		
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;

		for(int i=0;i<noofjobcolumns ;i++)
		{
			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);
			
			temp = spogJobServer.composejob_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
			expected_columns.add(temp);
		}

		test.log(LogStatus.INFO, "Create job columns in the org: "+organizationType + "using validToken");
		Response response= spogJobServer.createJobColumnsForSpecifiedUser(user_Id,validToken,expected_columns,test);
		
		test.log(LogStatus.INFO, "Compare job columns");
		spogJobServer.compareJobColumnsContent(response, expected_columns,columnsHeadContent,SpogConstants.SUCCESS_POST,null, test);
		
		test.log(LogStatus.INFO, "Delete job columns by userid in the org: "+organizationType);
		spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
	
	}
	
	// Create Job Columns using same Order ID
	@Test(dataProvider = "create_job_columns_same_order_id")
	public void CreateJobColumnsForSpecifiedUser_sameorderID(String organizationType,
												 String org_Id,
												 String validToken,
												 String user_Id,
												 String[] sort,
												 String[] filter,
												 String[] visible,
												 String[] orderId, 
												 int noofjobcolumns 
												 ){	 
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		test.assignAuthor("");
		spogJobServer.setToken(validToken);
		
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;

		for(int i=0;i<noofjobcolumns ;i++)
		{
			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);
			
			temp = spogJobServer.composejob_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
			expected_columns.add(temp);
		}

		test.log(LogStatus.INFO, "Create job columns in the org: "+organizationType + "using validToken");
		Response response= spogJobServer.createJobColumnsForSpecifiedUser(user_Id,validToken,expected_columns,test);
		
		test.log(LogStatus.INFO, "Compare job columns");
		spogJobServer.compareJobColumnsContent(response, expected_columns,columnsHeadContent,SpogConstants.SUCCESS_POST,null, test);
		
		test.log(LogStatus.INFO, "Delete job columns by userid in the org: "+organizationType);
		spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
	}
		
	// Create Job Columns using other organization's User ID
	@Test(dataProvider = "create_job_columns_otheruser_id")
	public void CreateJobColumnsForSpecifiedUser_otheruserid(String organizationType,
												 String org_Id,
												 String validToken,
												 String user_Id,
												 String[] sort,
												 String[] filter,
												 String[] visible,
												 String[] orderId, 
												 int noofjobcolumns 
												 ){	 
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		test.assignAuthor("");
		SpogMessageCode expected = null;
		spogJobServer.setToken(validToken);
		
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;

		for(int i=0;i<noofjobcolumns ;i++)
		{
			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);
			
			temp = spogJobServer.composejob_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
			expected_columns.add(temp);
			
		}
			
			/*if(organizationType=="msp_direct"||organizationType=="suborg_direct"||organizationType=="suborg_mspb"||organizationType=="suborg_suborgb") {
				expected = SpogMessageCode.LOGIN_USER_ORG_NOT_SAME_AS_GET_USER;
			}else if(organizationType=="direct_msp") {
				expected = SpogMessageCode.DIRECT_ADMIN_CANNOT_VIEW_MSP_CSR;
						
					
		}*/

		test.log(LogStatus.INFO, "Create job columns in the org: "+organizationType + "using validToken");
		Response response= spogJobServer.createJobColumnsForSpecifiedUser(user_Id,validToken,expected_columns,test);
		
		test.log(LogStatus.INFO, "Compare job columns");
		spogJobServer.compareJobColumnsContent(response, expected_columns,columnsHeadContent,SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
		
		//test.log(LogStatus.INFO, "Delete job columns by userid in the org: "+organizationType);
		//spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
}


	
	// Create Job Columns without User ID
	
		@Test(dataProvider = "create_job_columns_valid")
		public void CreateJobColumnsForSpecifiedUser_without_userid(String organizationType,
													 String org_Id,
													 String validToken,
													 String user_Id,
													 String[] sort,
													 String[] filter,
													 String[] visible,
													 String[] orderId, 
													 int noofjobcolumns 
													 ){	 
			test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
			
			test.assignAuthor("");
			spogJobServer.setToken(validToken);
			
			ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
			HashMap<String,Object> temp = new HashMap<>() ;

			for(int i=0;i<noofjobcolumns ;i++)
			{
				int index1 = gen_random_index(sort);
				int index2 = gen_random_index(filter);
				int index3 = gen_random_index(visible);
				
				temp = spogJobServer.composejob_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
				expected_columns.add(temp);
				
			}
				
			test.log(LogStatus.INFO, "Create job columns in the org: "+organizationType + "using validToken");
			Response response= spogJobServer.createJobColumnsForSpecifiedUser(user_Id+"123",validToken,expected_columns,test);
			
			test.log(LogStatus.INFO, "Compare job columns");
			spogJobServer.compareJobColumnsContent(response, expected_columns,columnsHeadContent,SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID, test);
			
			//test.log(LogStatus.INFO, "Delete job columns by userid in the org: "+organizationType);
			//spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
	}
		

	
	// Create Job Columns using other organization's token
	
	@Test(dataProvider = "create_job_columns_403")
	public void CreateJobColumnsForSpecifiedUser_403(String organizationType,
												 String org_Id,
												 String validToken,
												 String user_Id,
												 String[] sort,
												 String[] filter,
												 String[] visible,
												 String[] orderId, 
												 int noofjobcolumns 
												 ){	 
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		test.assignAuthor("");
		SpogMessageCode expected = null;
		spogJobServer.setToken(validToken);
		
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;

		for(int i=0;i<noofjobcolumns ;i++)
		{
			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);
			
			temp = spogJobServer.composejob_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
			expected_columns.add(temp);
		}
		
				test.log(LogStatus.INFO, "Create job columns in the org: "+organizationType + "using validToken");
		Response response= spogJobServer.createJobColumnsForSpecifiedUser(user_Id,validToken,expected_columns,test);
		
		test.log(LogStatus.INFO, "Compare job columns");
		spogJobServer.compareJobColumnsContent(response, expected_columns,columnsHeadContent,SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
		
	
	}
	


	// Create Job Columns in CSR Org using it's Direct, MSP and Suborg token
	
	@Test(dataProvider = "create_job_columns_csr_403")
	public void CreateJobColumnsForSpecifiedUser_403_CSR(String organizationType,
												 String org_Id,
												 String validToken,
												 String user_Id,
												 String[] sort,
												 String[] filter,
												 String[] visible,
												 String[] orderId, 
												 int noofjobcolumns 
												 ){	 
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		test.assignAuthor("");
		
		SpogMessageCode expected = null;
		spogJobServer.setToken(validToken);
		
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;

		for(int i=0;i<noofjobcolumns ;i++)
		{
			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);
			
			temp = spogJobServer.composejob_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
			expected_columns.add(temp);
		}
		
		
		/*if(organizationType=="csr_direct"||organizationType=="csr_suborg") {
			expected = SpogMessageCode.DIRECT_ADMIN_CANNOT_VIEW_MSP_CSR;
		}else if(organizationType=="csr_msp") {
			expected = SpogMessageCode.MSP_ADMIN_CANNOT_VIEW_CSR;
		}		*/
	

		test.log(LogStatus.INFO, "Create job columns in the org: "+organizationType + "using validToken");
		Response response= spogJobServer.createJobColumnsForSpecifiedUser(user_Id,validToken,expected_columns,test);
						
		test.log(LogStatus.INFO, "Compare job columns");
		spogJobServer.compareJobColumnsContent(response, expected_columns,columnsHeadContent,SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
		
	
	}
	
	
	
	// Create Job columns without Column ID
	
	@Test(dataProvider = "create_job_columns_valid")
	public void CreateJobColumnsForSpecifiedUser_without_ColumnID(String organizationType,
												 String org_Id,
												 String validToken,
												 String user_Id,
												 String[] sort,
												 String[] filter,
												 String[] visible,
												 String[] orderId, 
												 int noofjobcolumns 
												 ){	 
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		test.assignAuthor("");
		spogJobServer.setToken(validToken);
		
		
		
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;
		
						
		
	

		for(int i=0;i<noofjobcolumns ;i++)
		{
			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);
			
		temp = spogJobServer.composejob_Column("",sort[index1],filter[index2],visible[index3],orderId[i]);
		     expected_columns.add(temp);
		}

		test.log(LogStatus.INFO, "Create job columns in the org: "+organizationType + "using validToken");
		Response response= spogJobServer.createJobColumnsForSpecifiedUser(user_Id,validToken,expected_columns,test);
		
		test.log(LogStatus.INFO, "Compare job columns");
		spogJobServer.compareJobColumnsContent(response, expected_columns,columnsHeadContent,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.COLUMN_CANNOT_BLANK, test);
		
		//test.log(LogStatus.INFO, "Delete job columns by userid in the org: "+organizationType);
		//spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	//
	
	}
	

	
	// Create Job columns where Visible is set as True
	
		@Test(dataProvider = "create_job_columns_valid_visible_true")
		public void CreateJobColumnsForSpecifiedUser_Visibile_True(String organizationType,
														 String org_Id,
														 String validToken,
														 String user_Id,
														 String[] sort,
														 String[] filter,
														 String[] visible,
														 String[] orderId, 
														 int noofjobcolumns 
														 ){	 
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
				
		test.assignAuthor("");
		spogJobServer.setToken(validToken);
				
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>();

				for(int i=0;i<noofjobcolumns ;i++)
				{
					int index1 = gen_random_index(sort);
					int index2 = gen_random_index(filter);
					int index3 = gen_random_index(visible);
					
					temp = spogJobServer.composejob_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
					expected_columns.add(temp);
		}

		test.log(LogStatus.INFO, "Create job columns in the org: "+organizationType + "using validToken");
		Response response= spogJobServer.createJobColumnsForSpecifiedUser(user_Id,validToken,expected_columns,test);
				
		test.log(LogStatus.INFO, "Compare job columns");
		spogJobServer.compareJobColumnsContent(response, expected_columns,columnsHeadContent,SpogConstants.SUCCESS_POST,null, test);
				
		test.log(LogStatus.INFO, "Delete job columns by userid in the org: "+organizationType);
		spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
	}
	
	// Create Job columns where Visible is set as False
	
	@Test(dataProvider = "create_job_columns_valid_visible_false")
	public void CreateJobColumnsForSpecifiedUser_Visibile_False(String organizationType,
												 String org_Id,
												 String validToken,
												 String user_Id,
												 String[] sort,
												 String[] filter,
												 String[] visible,
												 String[] orderId, 
												 int noofjobcolumns 
												 ){	 
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		test.assignAuthor("");
		spogJobServer.setToken(validToken);
		
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;

		for(int i=0;i<noofjobcolumns ;i++)
		{
			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);
			
			temp = spogJobServer.composejob_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
			 expected_columns.add(temp);
		}

		test.log(LogStatus.INFO, "Create job columns in the org: "+organizationType + "using validToken");
		Response response= spogJobServer.createJobColumnsForSpecifiedUser(user_Id,validToken,expected_columns,test);
		
		test.log(LogStatus.INFO, "Compare job columns");
		spogJobServer.compareJobColumnsContent(response, expected_columns,columnsHeadContent,SpogConstants.SUCCESS_POST,null, test);
		
		test.log(LogStatus.INFO, "Delete job columns by userid in the org: "+organizationType);
		spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	
	}	
	
	// Create Job columns using negative Order ID
	
	@Test(dataProvider = "create_job_columns_negative_order_id")
	public void CreateJobColumnsForSpecifiedUser_Negative_Order_Id(String organizationType,
													 String org_Id,
													 String validToken,
													 String user_Id,
													 String[] sort,
													 String[] filter,
													 String[] visible,
													 String[] orderId, 
													 int noofjobcolumns 
												 ){	 
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
			
		test.assignAuthor("");
		spogJobServer.setToken(validToken);
			
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;

			for(int i=0;i<noofjobcolumns ;i++)
			{
				int index1 = gen_random_index(sort);
				int index2 = gen_random_index(filter);
				int index3 = gen_random_index(visible);
				
		temp = spogJobServer.composejob_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
				expected_columns.add(temp);
		}

		test.log(LogStatus.INFO, "Create job columns in the org: "+organizationType + "using validToken");
		Response response= spogJobServer.createJobColumnsForSpecifiedUser(user_Id,validToken,expected_columns,test);
			
		test.log(LogStatus.INFO, "Compare job columns");
		spogJobServer.compareJobColumnsContent(response, expected_columns,columnsHeadContent,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ORDER_ID_ATLEAST_1, test);
			
		//test.log(LogStatus.INFO, "Delete job columns by userid in the org: "+organizationType);
		//spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
}	
	

// Create Job columns using negative Order ID
		
	@Test(dataProvider = "create_job_columns_order_id_zero")
	public void CreateJobColumnsForSpecifiedUser_Order_Id_Zero(String organizationType,
												 		 String org_Id,
														 String validToken,
														 String user_Id,
														 String[] sort,
														 String[] filter,
														 String[] visible,
														 String[] orderId, 
														 int noofjobcolumns 
														 ){	 
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
				
		test.assignAuthor("");
		spogJobServer.setToken(validToken);
				
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;

				for(int i=0;i<noofjobcolumns ;i++)
				{
					int index1 = gen_random_index(sort);
					int index2 = gen_random_index(filter);
					int index3 = gen_random_index(visible);
					
		temp = spogJobServer.composejob_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
		 expected_columns.add(temp);
		}

		test.log(LogStatus.INFO, "Create job columns in the org: "+organizationType + "using validToken");
		Response response= spogJobServer.createJobColumnsForSpecifiedUser(user_Id,validToken,expected_columns,test);
				
		test.log(LogStatus.INFO, "Compare job columns");
		spogJobServer.compareJobColumnsContent(response, expected_columns,columnsHeadContent,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ORDER_ID_ATLEAST_1, test);
			
	//	test.log(LogStatus.INFO, "Delete job columns by userid in the org: "+organizationType);
	//	spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	
}		
	

	
	// Create Job columns using invalid JWT
	
		@Test(dataProvider = "create_job_columns_valid")
		public void CreateJobColumnsForSpecifiedUser_invalid_JWT(String organizationType,
													 		 String org_Id,
															 String validToken,
															 String user_Id,
															 String[] sort,
															 String[] filter,
															 String[] visible,
															 String[] orderId, 
															 int noofjobcolumns 
															 ){	 
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
					
		test.assignAuthor("");
		spogJobServer.setToken(validToken);
				
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;

		for(int i=0;i<noofjobcolumns ;i++)
					{
						int index1 = gen_random_index(sort);
						int index2 = gen_random_index(filter);
						int index3 = gen_random_index(visible);
						
		temp = spogJobServer.composejob_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
		expected_columns.add(temp);
		}

		test.log(LogStatus.INFO, "Create job columns in the org: "+organizationType + "using validToken");
		Response response= spogJobServer.createJobColumnsForSpecifiedUser(user_Id,validToken+"1234",expected_columns,test);
					
		test.log(LogStatus.INFO, "Compare job columns");
		spogJobServer.compareJobColumnsContent(response, expected_columns,columnsHeadContent,SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
					
		//test.log(LogStatus.INFO, "Delete job columns by userid in the org: "+organizationType);
		//spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				
	}
		
	
		// Create Job columns using missing JWT
		
		@Test(dataProvider = "create_job_columns_valid")
		public void CreateJobColumnsForSpecifiedUser_missing_JWT(String organizationType,
													 		 String org_Id,
															 String validToken,
															 String user_Id,
															 String[] sort,
															 String[] filter,
															 String[] visible,
															 String[] orderId, 
															 int noofjobcolumns 
															 ){	 
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
					
		test.assignAuthor("");
		spogJobServer.setToken(validToken);
				
		ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;

		for(int i=0;i<noofjobcolumns ;i++)
					{
						int index1 = gen_random_index(sort);
						int index2 = gen_random_index(filter);
						int index3 = gen_random_index(visible);
						
		temp = spogJobServer.composejob_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
		expected_columns.add(temp);
		}

		test.log(LogStatus.INFO, "Create job columns in the org: "+organizationType + "using validToken");
		Response response= spogJobServer.createJobColumnsForSpecifiedUser(user_Id,"",expected_columns,test);
					
		test.log(LogStatus.INFO, "Compare job columns");
		spogJobServer.compareJobColumnsContent(response, expected_columns,columnsHeadContent,SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
					
		//test.log(LogStatus.INFO, "Delete job columns by userid in the org: "+organizationType);
		//spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				
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
	
	
	/******************************************************************RandomFunction******************************************************************************/
	public int gen_random_index(String[] job_status) {
		Random generator = new Random();
		int randomindx = generator.nextInt(job_status.length);
		
		return randomindx;
	}
}
