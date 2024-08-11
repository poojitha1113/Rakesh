package api.policies;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import genericutil.ExtentManager;

public class recycleAllVolumesTest {
	  private SPOGDestinationServer spogDestinationServer;
	  private SPOGServer spogServer;
	  private String csrAdmin;
	  private String csrPwd;
	  private ExtentReports rep;
	  private ExtentTest test;
	  
	  //end 
	  @BeforeClass
	  @Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","logFolder","runningMachine","buildVersion"})
	  public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword, String logFolder, String runningMachine, String buildVersion) {
		//this is for update portal

		  spogServer = new SPOGServer(baseURI, port);
		  spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		  rep = ExtentManager.getInstance("CreateAccountTest",logFolder);
		  test = rep.startTest("beforeClass");
		  this.csrAdmin = csrAdminUserName;
		  this.csrPwd = csrAdminPassword;
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  
	  }
	  @Test
	  public void recycleVolumes(){
		  String user_token=spogServer.getJWTToken();
		  spogDestinationServer.setToken(user_token);
		  spogDestinationServer.recycleAllUselessCloudVolumes( test);
	  }
}
