package base.prepare;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.LogStatus;

import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import genericutil.FolderOrFileUtils;
import genericutil.SQLServerDb;
import genericutil.battsendmail;
import genericutil.testcasescount;


public class Is4Org {
	private SPOGServer spogServer;
	private Org4SPOGServer org4SPOGServer;
	private SPOGDestinationServer spogDestinationServer;
	protected ExtentReports rep;
	private String csrAdminUserName="shaji02_csr_0816@arcserve.com";
	private String csrAdminPassword="Welcome*021";	
	private String baseURI="http://tccapi.arcserve.com";
	private String port="8080";
	protected SQLServerDb bqdb1;
    public int Nooftest;
    protected long creationTime;
    protected String BQName=null;
    protected String runningMachine;
    protected testcasescount count1;
    protected String buildVersion;
       
    public void setEnv(String baseURI, String port, String csrAdminUserName, String csrAdminPassword) {
    	this.baseURI=baseURI;
    	this.port=port;
    	this.csrAdminUserName=csrAdminUserName;
    	this.csrAdminPassword=csrAdminPassword;
    }
    
	public void destroyOrg(String orgString){
		spogServer=new SPOGServer(baseURI, port);
		org4SPOGServer = new Org4SPOGServer(baseURI, port);
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		org4SPOGServer.setToken(spogServer.getJWTToken());		
		int total_page=org4SPOGServer.getOrgPagesBySearchStringWithCsrLogin(spogServer.getJWTToken(),orgString);
		if(total_page>0){
			for(int i=1;i<=total_page;i++ ){
				ArrayList<String> ret=org4SPOGServer.getOrgIdsBySearchStringWithCsrLogin(spogServer.getJWTToken(),orgString,i);
				if(ret!=null){
					if(ret.size()>0){
						for(int del=0;del<ret.size();del++ ){
							org4SPOGServer.destroyOrganizationWithoutCheck(ret.get(del).toString());
						}
					}
				}						
			}
	    }		
	}
	
	public static void main(String[] args) {
		new Is4Org().recycleVolumeInCDandDestroyOrg("yin");
	}
	
	/**
	 * This method will search for organizations with given string 
	 * Gets the destination_ids from those organizations
	 * Deletes the policies associated with those destinations
	 * Recycles the volumes
	 * Adds the organization_ids to the CD organization deletion Queue
	 * Deletes the organization from CD 
	 * Destroys the organization  
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 * @param orgString
	 */
	public void recycleVolumeInCDandDestroyOrg(String orgString) {
		
		String token=null;		
		spogServer=new SPOGServer(baseURI, port);
		org4SPOGServer = new Org4SPOGServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		token = spogServer.getJWTToken();
	
		if (!orgString.contains("submsp")) {
			String submspString = orgString+"*submsp";
			recycleVolumeInCDandDestroyOrg(submspString);	
		}		
		org4SPOGServer.setToken(spogServer.getJWTToken());		
		int total_page=org4SPOGServer.getOrgPagesBySearchStringWithCsrLogin(spogServer.getJWTToken(),orgString);
		if(total_page>0){
			for(int i=1;i<=total_page;i++ ){
				ArrayList<String> ret=org4SPOGServer.getOrgIdsBySearchStringWithCsrLogin(spogServer.getJWTToken(),orgString,1);
				if(ret!=null){
					if(ret.size()>0){
						for(int del=0;del<ret.size();del++ ){
								spogDestinationServer.recycleCloudVolumesAndDestroyOrg(token, ret.get(del));
						}
					}
				}						
			}
	    }
	}
	
	@AfterClass
	public void updatebd() {
		  try {
			if(count1.getfailedcount()>0) {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Failed");
			}else {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Passed");
			}
		  }catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
		  }
		  String orgHasString=this.getClass().getSimpleName();
		  System.out.println(orgHasString);
		  System.out.println("in father afterclass");
		  System.out.println("class in father is:"+orgHasString);
		  System.out.println("in father after class");
		  recycleVolumeInCDandDestroyOrg(orgHasString);
	  
	  }
	
	@AfterTest	  
    public void aftertest() {
	  rep.flush();
	  System.out.println("in father aftertest");
    }

}

