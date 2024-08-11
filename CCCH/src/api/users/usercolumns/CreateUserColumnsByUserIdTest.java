
	package api.users.usercolumns;

	import java.io.IOException;
	import java.text.SimpleDateFormat;
	import java.time.LocalDate;
	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.HashMap;
	import java.util.Random;
	import java.util.UUID;

	import org.apache.commons.lang.RandomStringUtils;
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
	import InvokerServer.SPOGServer;
	import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
	import genericutil.SQLServerDb;
	import genericutil.testcasescount;
	import io.restassured.response.Response;

	public class CreateUserColumnsByUserIdTest extends base.prepare.Is4Org{
		private SPOGServer spogServer;
		private TestOrgInfo ti;
		private GatewayServer gatewayServer;
		private UserSpogServer userSpogServer;
		private SPOGDestinationServer spogDestinationServer;
		//public int Nooftest;
		private ExtentTest test;
				
		private String site_version="1.0.0";
		private String gateway_hostname="rakesh";
		
		/*private ExtentReports rep;
		private SQLServerDb bqdb1;
		public int Nooftest;
		private long creationTime;
		private String BQName=null;
		private String runningMachine;
		private testcasescount count1;
		private String buildVersion;*/
		
		//used for test case count like passed,failed,remaining cases
		int passedcases=0;
		int failedcases=0;
		int skippedcases=0;
		String buildnumber=null;
		ArrayList<String> columnIdList = new ArrayList<String>();
		ArrayList<HashMap<String,Object>> columnsHeadContent = new ArrayList<HashMap<String,Object>>();
		
		private String  org_model_prefix=this.getClass().getSimpleName();
		
		@BeforeClass
		@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
		public void beforeClass(String baseURI, String port, String logFolder,String runningMachine,String buildVersion) {
			spogServer = new SPOGServer(baseURI, port);
			gatewayServer = new GatewayServer(baseURI, port);
			userSpogServer = new UserSpogServer(baseURI, port);
			spogDestinationServer = new SPOGDestinationServer(baseURI, port);
			rep = ExtentManager.getInstance("CreateUserColumnsByUserIdTest", logFolder);
			test = rep.startTest("Setup");
			bqdb1 = new SQLServerDb();
			count1 = new testcasescount();
			String author = "RAKESH.CHALAMALA";
			
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
				    try{
					    bqdb1.updateTable(BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
				    } 
				    catch (ClientProtocolException e) {
					       e.printStackTrace();
				    } 
				    catch (IOException e)
				    {
					        e.printStackTrace();
				    }
			}

			ti = new TestOrgInfo(spogServer, test);
			
			userSpogServer.setToken(ti.direct_org1_user1_token);
			Response response = userSpogServer.getUserColumnsWithCheck(SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

			columnsHeadContent = response.then().extract().path("data");
			int length = columnsHeadContent.size();
			for (int i = 0; i < length; i++) {
				HashMap<String,Object> HeadContent = columnsHeadContent.get(i);
				columnIdList.add((String) HeadContent.get("column_id"));
			}

		}
		@DataProvider
		public Object[][] createUsersColumnsByUserId_201(){
			return new Object[][] {
				
				{ "csr", ti.csr_token, ti.csr_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
				
				{ "csrReadOnlyT", ti.csr_readonly_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
				
				{ "direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
								new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},		
				{ "direct-csrT", ti.csr_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
									new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
							
				{ "direct-visibleTrue", ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
										new String[] { "true"},new String[]{"2","3","1","4","7","5","6"},4},
				{ "direct-visibleFalse", ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
											new String[] {"false"},new String[]{"2","3","1","4","7","5","6"},4},
							
				{ "root msp", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
										new String[] { "true", "false"},new String[]{"3","6","2","1","4","5","7"},7},
				{ "root msp-csrT", ti.csr_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
											new String[] { "true", "false"},new String[]{"3","6","2","1","4","5","7"},7},
				{ "normal msp", ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
												new String[] { "true", "false"},new String[]{"3","6","2","1","4","5","7"},7},
				{ "normal msp-csrT", ti.csr_token,ti.normal_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
													new String[] { "true", "false"},new String[]{"3","6","2","1","4","5","7"},7},
				{ "sub msp", ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
														new String[] { "true", "false"},new String[]{"3","6","2","1","4","5","7"},7},
				{ "sub msp-csrT", ti.csr_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
															new String[] { "true", "false"},new String[]{"3","6","2","1","4","5","7"},7},
								
							
				{ "suborg", ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
								new String[] { "true", "false"},new String[]{"3","2","1","4","5","7","6"},1},
				{ "suborg-mspT", ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
									new String[] { "true", "false"},new String[]{"3","2","1","4","5","7","6"},1},
				{ "suborg-csrT", ti.csr_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
										new String[] { "true", "false"},new String[]{"3","2","1","4","5","7","6"},1},
				{ "suborg-mspAccAdminT", ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
											new String[] { "true", "false"},new String[]{"3","2","1","4","5","7","6"},1},

				{ "suborg", ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
												new String[] { "true", "false"},new String[]{"3","2","1","4","5","7","6"},1},
				{ "suborg-mspT", ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
													new String[] { "true", "false"},new String[]{"3","2","1","4","5","7","6"},1},
				{ "suborg-csrT", ti.csr_token,ti.normal_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
														new String[] { "true", "false"},new String[]{"3","2","1","4","5","7","6"},1},
				{ "suborg-mspAccAdminT", ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
															new String[] { "true", "false"},new String[]{"3","2","1","4","5","7","6"},1},

				{ "suborg", ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
																new String[] { "true", "false"},new String[]{"3","2","1","4","5","7","6"},1},
				{ "suborg-mspT", ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
																	new String[] { "true", "false"},new String[]{"3","2","1","4","5","7","6"},1},
				{ "suborg-csrT", ti.csr_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
																		new String[] { "true", "false"},new String[]{"3","2","1","4","5","7","6"},1},
				{ "suborg-mspAccAdminT", ti.root_msp1_submsp1_account_admin_token,ti.msp1_submsp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
																			new String[] { "true", "false"},new String[]{"3","2","1","4","5","7","6"},1},
							
//				Delete the existing user user columns and create the same columns 
				{ "direct-deleteCreateSame", ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
											new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
								
				{ "direct-sameColId", ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
												new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},2},
																
				{ "direct-sameColIdOrderId", ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
													new String[] { "true", "false","none"},new String[]{"2","2","2","2","3","3","2"},2},
												
				{ "direct-sameorderid", ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
										new String[] { "true", "false","none"},new String[]{"2","3","2","2","3","3","2"},4},
				};
		}
		
		@Test(dataProvider="createUsersColumnsByUserId_201",enabled=true)
		public void createUsersColumnsByUserIdTest_201(String organizationType,
														String validToken,
														String user_id,
														String[] sort,
														String[] filter,
														String[] visible,
														String[] order_id,
														int noofcolumnstocreate
														) {
			test = ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread().getStackTrace()[1].getMethodName()+"_"+organizationType);
			
			test.assignAuthor("Rakesh.Chalamala");
			userSpogServer.setToken(validToken);
			
			ArrayList<HashMap<String, Object>> expectedColumns = new ArrayList<>();
			ArrayList<HashMap<String, Object>> expected_columns1 = new ArrayList<>();
			HashMap<String, Object> temp = new HashMap<>();
			
			for (int i = 0; i < noofcolumnstocreate; i++) {
			
				int index1 = gen_random_index(sort);
				int index2 = gen_random_index(filter);
				int index3 = gen_random_index(visible);
				
				if(organizationType.contains("sameColId"))
				{
					temp = userSpogServer.composeUser_Column(columnIdList.get(0), sort[index1], filter[index2], visible[index3], order_id[i]);
					if(i!=1) {
					expected_columns1.add(temp);
					}
				}
				else {
					temp = userSpogServer.composeUser_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], order_id[i]);
				}
//				temp = userSpogServer.composeUser_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], order_id[i]);
				expectedColumns.add(temp);
			}
			
			test.log(LogStatus.INFO, "Delete user columns by user id before create for organization : "+ organizationType);
			userSpogServer.deleteUserColumnsforSpecifiedUserwithCheck(user_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
						
			test.log(LogStatus.INFO, "Create user columns by user id for organization : " + organizationType);
			Response response = userSpogServer.createUserColumnsByUserId(validToken, user_id, expectedColumns, test);
			
			test.log(LogStatus.INFO, "Compare user columns by user id for organization : " + organizationType);
			if(organizationType.contains("direct-sameColId"))
			{
				userSpogServer.compareUserColumnsContent(response, expected_columns1, columnsHeadContent, SpogConstants.SUCCESS_POST, null, test);
			}
			else {
				userSpogServer.compareUserColumnsContent(response, expectedColumns, columnsHeadContent, SpogConstants.SUCCESS_POST, null, test);
			}
			
			test.log(LogStatus.INFO, "Delete user columns by user id for organization : "+ organizationType);
			userSpogServer.deleteUserColumnsforSpecifiedUserwithCheck(user_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			if(organizationType=="direct-deleteCreateSame")
			{
				test.log(LogStatus.INFO, "Create user columns by user id for organization : " + organizationType);
				response = userSpogServer.createUserColumnsByUserId(validToken, user_id, expectedColumns, test);
				
				test.log(LogStatus.INFO, "Compare user columns by user id for organization : " + organizationType);
				userSpogServer.compareUserColumnsContent(response, expectedColumns, columnsHeadContent, SpogConstants.SUCCESS_POST, null, test);
				
				test.log(LogStatus.INFO, "Delete user columns by user id for organization : "+ organizationType);
				userSpogServer.deleteUserColumnsforSpecifiedUserwithCheck(user_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				
			}
		}
		
		
		@DataProvider(name="createUsersColumnsByUserId_400")
		public final Object[][] createUsersColumnsByUserId_400() {
			
			return new Object[][] {
					// different users
					{ "direct-noColId", ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
					{ "direct-invalidUid", ti.direct_org1_user1_token,"invalidUid", new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
								new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
					{ "direct-invalidOrderId", ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
									new String[] { "true", "false","none"},new String[]{"-1","1","0"},2},
					{ "direct-moreOrderIds", ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
										new String[] { "true", "false","none"},new String[]{"2","1","9","7","5","6","4","3"},7},
					
					{ "msp-noColId", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
					{ "msp-invalidUid", ti.root_msp_org1_user1_token,"invalidUid", new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
								new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
					{ "msp-invalidOrderId", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
								new String[] { "true", "false","none"},new String[]{"-1","1","0"},2},
					{ "msp-moreOrderIds", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
									new String[] { "true", "false","none"},new String[]{"2","1","9","7","5","6","4","3"},7},	
					
					{ "submsp-noColId", ti.msp1_submsp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
										new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
					{ "submsp-invalidUid", ti.msp1_submsp1_suborg1_user1_token,"invalidUid", new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
											new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
					{ "submsp-invalidOrderId", ti.msp1_submsp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
											new String[] { "true", "false","none"},new String[]{"-1","1","0"},2},
					{ "submsp-moreOrderIds", ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
												new String[] { "true", "false","none"},new String[]{"2","1","9","7","5","6","4","3"},7},	
								
					{ "sub-noColId", ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
									new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
					{ "sub-invalidUid", ti.root_msp1_suborg1_user1_token,"invalidUid", new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
					{ "sub-invalidOrderId", ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true", "false","none"},new String[]{"-1","1","0"},2},
					{ "sub-moreOrderIds", ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
								new String[] { "true", "false","none"},new String[]{"2","1","9","7","5","6","4","3"},7},	
													
					{ "csr-noColId", ti.csr_token,ti.csr_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
					{ "csr-invalidUid", ti.csr_token,"invalidUid", new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
					{ "csr-invalidOrderId", ti.csr_token,ti.csr_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
								new String[] { "true", "false","none"},new String[]{"-1","1","0"},2},
					{ "csr-moreOrderIds", ti.csr_token,ti.csr_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
									new String[] { "true", "false","none"},new String[]{"2","1","9","7","5","6","4","3"},7},
					
					{ "csrReadOnly-noColId", ti.csr_readonly_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
										new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
					{ "csrReadOnly-invalidUid", ti.csr_readonly_token,"invalidUid", new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
								new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
					{ "csrReadOnly-invalidOrderId", ti.csr_readonly_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
								new String[] { "true", "false","none"},new String[]{"-1","1","0"},2},
					{ "csrReadOnly-moreOrderIds", ti.csr_readonly_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
								new String[] { "true", "false","none"},new String[]{"2","1","9","7","5","6","4","3"},7},
			};
		}
		@Test(dataProvider="createUsersColumnsByUserId_400",enabled=true)
		public void createUsersColumnsByUserIdTest_400(String organizationType,
				  												String validToken,
				  												String user_Id,
				  												String[] sort,
				  												String[] filter,
				  												String[] visible,
				  												String[] orderId, 
				  												int noofcolstocreate
				  												) {
			  	SpogMessageCode expectedErrorMessage;
				test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
				
				test.assignAuthor("Rakesh.Chalamala");
				userSpogServer.setToken(validToken);
				
				
				ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
				HashMap<String,Object> temp = new HashMap<>() ;

				for(int i=0;i<noofcolstocreate ;i++)
				{
					int index1 = gen_random_index(sort);
					int index2 = gen_random_index(filter);
					int index3 = gen_random_index(visible);
//					int index4 = gen_random_index(orderId);
					if(organizationType.contains("-noColId"))
					{
						temp = userSpogServer.composeUser_Column("", sort[index1], filter[index2], visible[index3], orderId[i]);
					}
					else {
						temp = userSpogServer.composeUser_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], orderId[i]);				
					}
					expected_columns.add(temp);
				}
				
				test.log(LogStatus.INFO, "Create user columns for specified user in the org: "+organizationType);
				Response response= userSpogServer.createUserColumnsByUserId(validToken,user_Id, expected_columns, test);
				
				if(organizationType.contains("-invalidUid")) {
					expectedErrorMessage = SpogMessageCode.ELEMENT_USERID_IS_NOT_UUID;
				}
				else if (organizationType.contains("-invalidOrderId")){  
					expectedErrorMessage = SpogMessageCode.ORDER_ID_ATLEAST_1;
				}
				else if(organizationType.contains("-moreOrderIds"))
				{
					expectedErrorMessage = SpogMessageCode.ORDER_ID_SHOULD_BE_LESS_THAN_MAX_COUNT;
				}
				else {
					expectedErrorMessage = SpogMessageCode.COLUMN_CANNOT_BLANK;
				}
				
				test.log(LogStatus.INFO, "Compare the user columns created for specified user in org: "+organizationType);
				userSpogServer.compareUserColumnsContent(response, expected_columns, columnsHeadContent, SpogConstants.REQUIRED_INFO_NOT_EXIST, expectedErrorMessage, test);
				
			}
			
		@DataProvider(name="createUsersColumnsByUserId_401")
		public final Object[][] createUsersColumnsByUserId_401() {
			
			return new Object[][] {
				// different users
					{ "direct-invalidT", "invalid",ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
					{ "direct-missingT", "",ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
			};
		}
		@Test(dataProvider="createUsersColumnsByUserId_401",enabled=true)
		public void createUsersColumnsByUserIdTest_401(String organizationType,
				  												String validToken,
				  												String user_Id,
				  												String[] sort,
				  												String[] filter,
				  												String[] visible,
				  												String[] orderId, 
				  												int noofcolstocreate
				  												) {
				SpogMessageCode expectedErrorMessage;
				test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
				
				test.assignAuthor("Rakesh.Chalamala");
				userSpogServer.setToken(validToken);
				
				
				ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
				HashMap<String,Object> temp = new HashMap<>() ;

				for(int i=0;i<noofcolstocreate ;i++)
				{
					int index1 = gen_random_index(sort);
					int index2 = gen_random_index(filter);
					int index3 = gen_random_index(visible);
//					int index4 = gen_random_index(orderId);
					temp = userSpogServer.composeUser_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], orderId[i]);
					expected_columns.add(temp);
				}

				test.log(LogStatus.INFO, "Create user columns for specified user in the org: "+organizationType);
				Response response= userSpogServer.createUserColumnsByUserId(validToken,user_Id, expected_columns, test);
				
				if(validToken=="invalid") {
					expectedErrorMessage=SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT;
				}
				else {
					expectedErrorMessage=SpogMessageCode.COMMON_AUTHENTICATION_FAILED;
				}
				
				test.log(LogStatus.INFO, "Compare the user columns created for specified user in org: "+organizationType);
				userSpogServer.compareUserColumnsContent(response, expected_columns, columnsHeadContent, SpogConstants.NOT_LOGGED_IN, expectedErrorMessage, test);
								
		}
		
		
		@DataProvider(name="createUsersColumnsByUserId_403")
		public final Object[][] createUsersColumnsByUserId_403() {
			
			return new Object[][] {
				// different users
					{ "direct-mspT", ti.direct_org1_user1_token,ti.root_msp_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},	
					{ "direct-suborgT", ti.direct_org1_user1_token,ti.root_msp1_suborg1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
					{ "direct-submspmspT", ti.direct_org1_user1_token,ti.root_msp1_submsp1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
								new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},	
					{ "direct-submspsuborgT", ti.direct_org1_user1_token,ti.msp1_submsp1_suborg1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
									new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},4},
					
					{ "msp-directT", ti.root_msp_org1_user1_token,ti.direct_org1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
								new String[] { "true", "false"},new String[]{"3","6","2","1","4","5","7"},7},
					{ "msp-suborgT", ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
									new String[] { "true", "false"},new String[]{"3","6","2","1","4","5","7"},7},
					{ "msp-mspbT", ti.root_msp_org1_user1_token,ti.normal_msp_org2_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
										new String[] { "true", "false"},new String[]{"3","6","2","1","4","5","7"},7},
					{ "msp-submspsuborgT", ti.root_msp_org1_user1_token,ti.msp1_submsp1_suborg1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
											new String[] { "true", "false"},new String[]{"3","6","2","1","4","5","7"},7},
					{ "msp-submspT", ti.root_msp_org1_user1_token,ti.root_msp1_submsp1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
												new String[] { "true", "false"},new String[]{"3","6","2","1","4","5","7"},7},
					
					{ "submsp-directT", ti.root_msp1_submsp1_user1_token,ti.direct_org1_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
											new String[] { "true", "false"},new String[]{"3","6","2","1","4","5","7"},7},
					{ "submsp-suborgT", ti.root_msp1_submsp1_user1_token,ti.root_msp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
												new String[] { "true", "false"},new String[]{"3","6","2","1","4","5","7"},7},
					{ "submsp-mspbT", ti.root_msp1_submsp1_user1_token,ti.normal_msp_org2_user1_token,ti.root_msp1_submsp1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
													new String[] { "true", "false"},new String[]{"3","6","2","1","4","5","7"},7},
					
					{ "suborg-directT", ti.root_msp1_suborg1_user1_token,ti.direct_org1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","7"},1},
					{ "suborg-suborgbT", ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg2_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
							new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","7"},1},
					{ "suborgb-mspAccAdminT", ti.root_msp1_suborg2_user1_token,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg2_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
								new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","7"},1},
					{ "suborg-submspT", ti.root_msp1_suborg2_user1_token,ti.root_msp1_submsp1_user1_token,ti.root_msp1_suborg2_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
									new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","7"},1},
					{ "suborg-submspsuborgT", ti.root_msp1_suborg2_user1_token,ti.msp1_submsp1_suborg1_user1_token,ti.root_msp1_suborg2_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
										new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","7"},1},

					{ "submspsuborg-directT", ti.msp1_submsp1_suborg1_user1_token,ti.direct_org1_user1_token,ti.msp1_submsp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
									new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","7"},1},
					{ "submspsuborg-suborgbT", ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg2_user1_token,ti.msp1_submsp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
										new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","7"},1},
					{ "submspsuborgb-mspAccAdminT", ti.msp1_submsp1_suborg2_user1_token,ti.root_msp1_submsp2_account_admin_token,ti.msp1_submsp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
											new String[] { "true", "false"},new String[]{"1","2","3","4","5","6","7"},1},

					
					{ "csr-dirctT",ti.csr_token, ti.direct_org1_user1_token,ti.csr_admin_user_id, new String[] { "true", "false"}, new String[] { "true", "false"},
								new String[] { "true", "false"},new String[]{"4","2","3","1","5","6","7"},1},
					
					{ "csr-mspT", ti.csr_token,ti.root_msp_org1_user1_token,ti.csr_admin_user_id, new String[] { "true", "false"}, new String[] { "true", "false"},
									new String[] { "true", "false"},new String[]{"1","2","3","5","4","7","6"},1},
					
					{ "csr-suborgT", ti.csr_token,ti.root_msp1_suborg1_user1_token,ti.csr_admin_user_id, new String[] { "true", "false"}, new String[] { "true", "false"},
						new String[] { "true", "false"},new String[]{"1","7","5","4","3","6","2"},1},
					
					{ "csrReadOnly-dirctT",ti.csr_readonly_token, ti.direct_org1_user1_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false"}, new String[] { "true", "false"},
							new String[] { "true", "false"},new String[]{"4","2","3","1","5","6","7"},1},
				
					{ "csrReadOnly-mspT", ti.csr_readonly_token,ti.root_msp_org1_user1_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false"}, new String[] { "true", "false"},
								new String[] { "true", "false"},new String[]{"1","2","3","5","4","7","6"},1},
				
					{ "csrReadOnly-suborgT", ti.csr_readonly_token,ti.root_msp1_suborg1_user1_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false"}, new String[] { "true", "false"},
									new String[] { "true", "false"},new String[]{"1","7","5","4","3","6","2"},1},
					
					{ "direct-csrReadOnlyT", ti.direct_org1_user1_token,ti.csr_readonly_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
										new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6"},5},
					{ "msp-csrReadOnlyT", ti.root_msp_org1_user1_token,ti.csr_readonly_token,ti.root_msp_org1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
											new String[] { "true", "false"},new String[]{"3","6","2","1","4","5","7"},7},
					{ "suborg-csrReadOnlyT", ti.root_msp1_suborg1_user1_token,ti.csr_readonly_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false"}, new String[] { "true", "false"},
												new String[] { "true", "false"},new String[]{"3","2","1","4","5","7","6"},1},
				
			};
		}
		@Test(dataProvider="createUsersColumnsByUserId_403",enabled=true)
		public void createUsersColumnsByUserIdTest_403(String organizationType,
																String validToken,
																String otherUserValidToken,
																String user_Id,
																String[] sort,
																String[] filter,
																String[] visible,
																String[] orderId, 
																int noofcolumnstobecreated
																) {
			test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
			
			test.assignAuthor("Rakesh.Chalamala");
			userSpogServer.setToken(validToken);
			SpogMessageCode expected_errormessage;
			
			ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
			HashMap<String,Object> temp = new HashMap<>() ;

			test.log(LogStatus.INFO, "Compose the user columns as per the user request");
			for(int i=0;i<noofcolumnstobecreated ;i++)
			{
				int index1 = gen_random_index(sort);
				int index2 = gen_random_index(filter);
				int index3 = gen_random_index(visible);
				//int index4 = gen_random_index(orderId);
				
				temp = userSpogServer.composeUser_Column(columnIdList.get(i),sort[index1],filter[index2],visible[index3],orderId[i]);
				expected_columns.add(temp);
			}
			
			test.log(LogStatus.INFO, "Create the user columns for the specified user in the org: "+organizationType);
			Response response= userSpogServer.createUserColumnsByUserId(otherUserValidToken,user_Id,expected_columns,test);
			
			if (organizationType=="msp-directT" || organizationType=="msp-suborgT") {
				expected_errormessage = SpogMessageCode.RESOURCE_PERMISSION_DENY;
			}
			else if (user_Id==ti.csr_admin_user_id) {
				expected_errormessage = SpogMessageCode.RESOURCE_PERMISSION_DENY;
				if (organizationType=="csr-mspT") {
					expected_errormessage = SpogMessageCode.RESOURCE_PERMISSION_DENY;
					
				}
				
			}
			else{
				expected_errormessage = SpogMessageCode.RESOURCE_PERMISSION_DENY;}
				
			test.log(LogStatus.INFO, "Get the user columns for the specified user in the org: "+organizationType);
			userSpogServer.compareUserColumnsContent(response, expected_columns, columnsHeadContent, SpogConstants.INSUFFICIENT_PERMISSIONS, expected_errormessage, test);

		}
		

		@DataProvider(name="createUsersColumnsByUserId_404")
		public final Object[][] createUsersColumnsByUserId_404() {
			
			return new Object[][] {
				// different users
					{ "csr-uidNotExist", ti.csr_token,UUID.randomUUID().toString(), new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
					new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6","9"},1},
					{ "csr-colIdNotExist", ti.csr_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6","9"},1},
				
					{ "csrReadOnly-uidNotExist", ti.csr_readonly_token,UUID.randomUUID().toString(), new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6","9"},1},
					{ "csrReadOnly-colIdNotExist", ti.csr_readonly_token,ti.csr_readonly_admin_user_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
								new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6","9"},1},
				
					{ "direct-uidNotExist", ti.direct_org1_user1_token,UUID.randomUUID().toString(), new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
						new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6","9"},1},
					{ "direct-colIdNotExist", ti.direct_org1_user1_token,ti.direct_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6","9"},1},
					
					{ "msp-uidNotExist", ti.root_msp_org1_user1_token,UUID.randomUUID().toString(), new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6","9"},1},
					{ "msp-colIdNotExist", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6","9"},1},
							
					{ "suborg-uidNotExist", ti.root_msp1_suborg1_user1_token,UUID.randomUUID().toString(), new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6","9"},1},
					{ "suborg-colIdNotExist", ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
							new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6","9"},1},
					
					{ "suborg-uidNotExist-mspAccAdminT", ti.root_msp_org1_msp_accountadmin1_token,UUID.randomUUID().toString(), new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
								new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6","9"},1},
					{ "suborg-colIdNotExist-mspAccAdminT", ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_user1_id, new String[] { "true", "false","none"}, new String[] { "true", "false","none"},
								new String[] { "true", "false","none"},new String[]{"2","3","1","4","7","5","6","9"},1},
									
			};
		}
		@Test(dataProvider="createUsersColumnsByUserId_404",enabled=true)
		public void createUsersColumnsByUserIdTest_404(String organizationType,
				  												String validToken,
				  												String user_Id,
				  												String[] sort,
				  												String[] filter,
				  												String[] visible,
				  												String[] orderId, 
				  												int noofcolstocreate
				  												) {
			  
				test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
				
				SpogMessageCode expected_errormessage;
				
				test.assignAuthor("Rakesh.Chalamala");
				userSpogServer.setToken(validToken);
				
				
				ArrayList<HashMap<String,Object>> expected_columns = new ArrayList<>();
				HashMap<String,Object> temp = new HashMap<>() ;
				String columnid = UUID.randomUUID().toString();

				for(int i=0;i<noofcolstocreate ;i++)
				{
					int index1 = gen_random_index(sort);
					int index2 = gen_random_index(filter);
					int index3 = gen_random_index(visible);
//					int index4 = gen_random_index(orderId);
					
					if(organizationType.contains("colIdNotExist"))
					{
						temp = userSpogServer.composeUser_Column(columnid, sort[index1], filter[index2], visible[index3], orderId[i]);
					}
					else {
						temp = userSpogServer.composeUser_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3], orderId[i]);	
					}
					expected_columns.add(temp);
				}

				test.log(LogStatus.INFO, "Create user columns for specified user in the org: "+organizationType);
				Response response= userSpogServer.createUserColumnsByUserId( validToken,user_Id, expected_columns, test);
				if(organizationType.contains("uidNotExist")) {
					test.log(LogStatus.INFO, "Compare the user columns created for specified user in org: "+organizationType);
					userSpogServer.compareUserColumnsContent(response, expected_columns, columnsHeadContent, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_ID_DOESNOT_EXIST, test);	
				}
				else {
					test.log(LogStatus.INFO, "Compare the user columns created for specified user in org: "+organizationType);
					userSpogServer.compareUserColumnsContent(response, expected_columns, columnsHeadContent, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.COLUMN_ID_DOESNOT_EXIST, test);
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
					
			/******************************************************************RandomFunction******************************************************************************/
			public int gen_random_index(String[] job_status) {
				Random generator = new Random();
				int randomindx = generator.nextInt(job_status.length);
				
				return randomindx;
			}

	}


