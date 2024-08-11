package api.organizations;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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

import Constants.ErrorCode;
import Constants.SpogConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import ui.others.helper.EmailHelper;
import ui.spog.server.SPOGUIServer;

public class EnrollOrganizationTest extends base.prepare.Is4Org {

	  private SPOGServer spogServer;
	  private Org4SPOGServer org4SpogServer;
	  private UserSpogServer user4SpogServer;
	  private SPOGDestinationServer spogDestinationServer;
	  private String csrAdminUserName;
	  private String csrAdminPassword;
	
	  private ExtentTest test;
	  
	/*  private ExtentReports rep;
	  private SQLServerDb bqdb1;
	  public int Nooftest;
	  private long creationTime;
	  private String BQName=null;
	  private String runningMachine;
	  private testcasescount count1;
	  private String buildVersion;
	  */
	  private EmailHelper emailHelper;
	  private SPOGUIServer spogUIServer;
	  private String browserType;
	  private String maxWaitTimeSec;
	  private String  org_model_prefix=this.getClass().getSimpleName();

	  @BeforeClass
	  @Parameters({ "baseURI", "port", "browserType", "maxWaitTimeSec", "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion"})
	  public void beforeClass(String baseURI, String port,String browserType, String maxWaitTimeSec,
			  String logFolder, String adminUserName, String adminPassword ,  String buildVersion) throws UnknownHostException {
		
		
	 	  spogServer = new SPOGServer(baseURI, port);
	 	  org4SpogServer = new Org4SPOGServer(baseURI, port);
	 	  user4SpogServer = new UserSpogServer(baseURI, port);
	 	 spogDestinationServer = new SPOGDestinationServer(baseURI, port);
	 	  this.browserType = browserType;
	 	  this.maxWaitTimeSec = maxWaitTimeSec;
	 	
	 	 
		  rep = ExtentManager.getInstance(this.getClass().getSimpleName(),logFolder);
		  this.csrAdminUserName = adminUserName;
		  this.csrAdminPassword = adminPassword;
		  test = rep.startTest("beforeClass");
		  this.BQName = this.getClass().getSimpleName();
		      String author = "Ramya.Nagepalli";
		      this.runningMachine =  InetAddress.getLocalHost().getHostName();
		      SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		      java.util.Date date=new java.util.Date();
		      this.buildVersion=buildVersion+"_"+dateFormater.format(date);
		      Nooftest=0;
		      bqdb1 = new SQLServerDb();
		      count1 = new testcasescount();
		      if(count1.isstarttimehit()==0) {
		          System.out.println("Into get loggedInUserById");
		          creationTime=System.currentTimeMillis();
		          count1.setcreationtime(creationTime);
		          //creationTime = System.currentTimeMillis();
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
	  

	   @DataProvider(name = "organizationAndUserInfo1")
		public final Object[][] getOrganizationAndUserInfo1() {
			return new Object[][] { {"auto_first","auto_last", "arcserveautotest1@hotmail.com","", "kate"+org_model_prefix, SpogConstants.DIRECT_ORG,"", "Storage!07", SpogConstants.SUCCESS_POST, null},
				{ RandomStringUtils.randomAlphanumeric(128),"auto_last", "ARCSERVEAUTOTEST1@hotmail.com","+8612398408", "hello international"+org_model_prefix, SpogConstants.MSP_ORG, "id", "Storage!07", SpogConstants.SUCCESS_POST, null},
						{null,"auto_last", "arcserveautotest1@hotmail.com","", "kate"+org_model_prefix, SpogConstants.DIRECT_ORG,"", "Storage!07", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK},
				{"","auto_last", "arcserveautotest1@hotmail.com","", "kate"+org_model_prefix, SpogConstants.DIRECT_ORG,"", "Storage!07", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK},
				{RandomStringUtils.randomAlphanumeric(129),"auto_last", "arcserveautotest1@hotmail.com","", "kate"+org_model_prefix, SpogConstants.DIRECT_ORG,"", "Storage!07", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.NOT_FIT_LENGTH},
				{"auto_first",null, "arcserveautotest1@hotmail.com","", "kate"+org_model_prefix, SpogConstants.DIRECT_ORG,"", "Storage!07", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK},
				{"auto_first","", "arcserveautotest1@hotmail.com","", "kate"+org_model_prefix, SpogConstants.DIRECT_ORG,"", "Storage!07", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK},
				{"auto_first",RandomStringUtils.randomAlphanumeric(129), "arcserveautotest1@hotmail.com","", "kate"+org_model_prefix, SpogConstants.DIRECT_ORG,"", "Storage!07", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.NOT_FIT_LENGTH},
				{"auto_first","auto_last", null,"", "kate"+org_model_prefix, SpogConstants.DIRECT_ORG,"", "Storage!07", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK},
				{"auto_first","auto_last", "","", "kate"+org_model_prefix, SpogConstants.DIRECT_ORG,"", "Storage!07", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK},
				{"auto_first","auto_last", RandomStringUtils.randomAlphanumeric(65)+ "@arcserve.com","", "kate"+org_model_prefix, SpogConstants.DIRECT_ORG,"", "Storage!07", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.EMAIL_FORMAT},
				{"auto_first","auto_last", "auto@"+ RandomStringUtils.randomAlphanumeric(253)+ ".com","", "kate"+org_model_prefix, SpogConstants.DIRECT_ORG,"", "Storage!07", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.EMAIL_FORMAT},
				{"auto_first","auto_last",  RandomStringUtils.randomAlphanumeric(23)+ ".com","", "kate"+org_model_prefix, SpogConstants.DIRECT_ORG,"", "Storage!07", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.EMAIL_FORMAT},
				{"auto_first","auto_last", "arcserveautotest1@hotmail.com","", null, SpogConstants.DIRECT_ORG,"", "Storage!07", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK},
				{"auto_first","auto_last", "arcserveautotest1@hotmail.com","", "", SpogConstants.DIRECT_ORG,"", "Storage!07", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK},
				{"auto_first","auto_last", "arcserveautotest1@hotmail.com","", RandomStringUtils.randomAlphanumeric(257), SpogConstants.DIRECT_ORG,"", "Storage!07", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.NOT_FIT_LENGTH},
				{"auto_first","auto_last", "arcserveautotest1@hotmail.com","", "spade"+org_model_prefix, SpogConstants.MSP_SUB_ORG,"", "Storage!07", SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.ENROLLMENT_IS_NOT_ALLOWED_ORG_TYPE},
				{"auto_first","auto_last", "arcserveautotest1@hotmail.com","", "spade"+org_model_prefix, SpogConstants.CSR_ORG,"", "Storage!07", SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.ENROLLMENT_IS_NOT_ALLOWED_ORG_TYPE},
				{"auto_first","auto_last", "arcserveautotest1@hotmail.com","", "kate"+org_model_prefix, SpogConstants.DIRECT_ORG, UUID.randomUUID().toString(), "Storage!07", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.DATACENTER_IS_NOT_FOUND},
				 {null, null, null,null, "kate"+org_model_prefix, SpogConstants.DIRECT_ORG,"", "Storage!07", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK},
				 {"auto_first","auto_last", "arcserveautotest1@hotmail.com","", null, null,null, "Storage!07", SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK},
				 
			};
		}
	  @Test(dataProvider = "organizationAndUserInfo1")
	  /**
	   * 1. user could create organization and user successfully when input only required information
	   * 24. set org type as direct org, should be successful
	   * 17. set phone as null, api should be called successfully
	   * 18. set phone as "", api should be called successfully
	   * 2. user could create organization and user successfully when input all information
	   * 14.Email is case insensitive - it will use lower case for email no matter user providing lower/upper case.
	   * 25. set org type as msp org, should be successful
	   * 4. set first name as null, should report error
	   * 5. set first name as "", should report error
	   * 6. set first name 's length bigger than 128, should report error
	   * 7. set last name as null, should report error
	   * 8. set last name as "", should report error
	   * 9. set last name 's length bigger than 128, should report error
	   * 10. set email as null, should report error
	   * 11. set email as "" should report error
	   * 12. set email's local part's length is bigger than 64  should report error
	   * 13. set email's domain part's length is bigger than 256,  should report error
	   * 15. Email must include '@' (ie,local-part@domain), or it will fail to create.
	   * 
	   * 21. set org name as null, should report error
	   * 22. set org name as "" should report error
	   * 23. set org name' lengh is bigger than 256  should report error
	   * 26. set org type as msp_child, should report error
	   * 27. set org type as csr, should report error
	   * 
	   * 28. set datacenter as null, should be successful
	   * 29. set datacenter as "",  should be successful
	   * 30. set datacenter as random id,  should report error
	   * 
	   * 16. Email is insensitive - it can't create two same user (ie. spog@ci.com/SPOG@ci.com).
	   * 
	   * 1. check first name and last name in mail is correct
	   * 2. check organization in mail is correct
	   * 3. check account in mail is correct
	   * 4. check account created by in mail is correct
	   * 5. check activate account button is correct
	   * @param first_name
	   * @param last_name
	   * @param email
	   * @param phone_number
	   * @param organization_name
	   * @param organization_type
	   * @param datacenter_id
	   * @param password
	   * @param status_code
	   * @param error_code
	   */
	  public void enrollOrganization(String first_name, String last_name,  String email, String phone_number, 
			  String organization_name, String organization_type, String datacenter_id, String password, int status_code, String error_code ){
		  
		  test = rep.startTest("enrollOrganization");
	 	  test.assignAuthor("Ramya.Nagepalli");
	 	  spogServer.errorHandle.printInfoMessageInDebugFile("/****************enrollOrganization**************/");
	 	  spogServer.userLogin(csrAdminUserName, csrAdminPassword);
	 	  org4SpogServer.setToken(spogServer.getJWTToken());
	 	  
	 	  if((email!=null) && !email.equals("")){
	 		  Response response = user4SpogServer.postUsersSearchResponse(email, null, null, spogServer.getJWTToken());
	 		  if(response.statusCode()==SpogConstants.SUCCESS_GET_PUT_DELETE){
	 			 int total_size = response.then().extract().path("pagination.total_size");
			 	  if(total_size==1){
			 		 ArrayList<HashMap<String, Object>> dataList = response.then().extract().path("data");
			 		 HashMap<String ,Object> orgInfo = 	(HashMap<String, Object>) dataList.get(0).get("organization");
			 		 org4SpogServer.destroyOrganization(orgInfo.get("organization_id").toString() , test);
			 	  }
	 		  }
		 	 
	 	  }
	 	
	 	  
	 	 test.log(LogStatus.INFO,"create organization and user");
	 	 if((datacenter_id!=null) && datacenter_id.equals("id")){	 		
	 		String[] datacenterIDs=spogDestinationServer.getDestionationDatacenterID(); 		  
	 		datacenter_id=datacenterIDs[0];
	 	 }
	 	 String org_id= org4SpogServer.enrollOrganizationsWithCheck(first_name, last_name, email, phone_number, organization_name, organization_type, datacenter_id, status_code, error_code, test);
	 	
	 	 if(status_code== SpogConstants.SUCCESS_POST){
	 	 	 test.log(LogStatus.INFO,"activate user");
		 	 emailHelper = new EmailHelper(browserType, Integer.valueOf(maxWaitTimeSec));
		 	 emailHelper.loginMailboxAndOpenActivateAccountPage(email, password, first_name, last_name, "spog_udp_qa_" +organization_name, email);
		 	 spogUIServer = new SPOGUIServer(browserType, Integer.valueOf(maxWaitTimeSec));
		 	 spogUIServer.createAccount(password);
		 	 emailHelper.destroy();
		 	 
		 	 org4SpogServer.enrollOrganizationsWithCheck(first_name, last_name, email.toUpperCase(), phone_number, organization_name, organization_type, datacenter_id, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.EMAIL_EXISTS, test);
		 	
		 	 test.log(LogStatus.INFO,"destroy org");
		 	 org4SpogServer.destroyOrganization(org_id , test);
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
	      rep.endTest(test);    
	  }
	 
}
