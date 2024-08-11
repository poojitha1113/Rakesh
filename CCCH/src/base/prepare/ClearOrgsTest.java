package base.prepare;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.LogStatus;

import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;


public class ClearOrgsTest {
	private SPOGServer spogServer;
	private Org4SPOGServer org4SPOGServer;
	private UserSpogServer userSpogServer;
	private SPOGDestinationServer spogDestinationServer;
	protected ExtentReports rep;
	private String csrAdminUserName="ramya_csr@arcserve.com";
	private String csrAdminPassword="Mclaren@2020";
	private String baseURI="http://tccapi.arcserve.com";
	private String port="8080";
	protected SQLServerDb bqdb1;
    public int Nooftest;
    protected long creationTime;
    protected String BQName=null;
    protected String runningMachine;
    protected testcasescount count1;
    protected String buildVersion;
    protected static boolean destroy=true;
	
	
	public static void main(String[] args) throws IOException {
		ClearOrgsTest test = new ClearOrgsTest();
		
		destroy = true;
		test.printOrgIdsAndDestroy();;
//		test.destroyOrgByUserName();
	}
		
	@Test
	public void printOrgIdsAndDestroy() throws IOException {

//		names = "spogqa_shuo,liyin,zhaoguo,spog_udp_qa_d_jing,yuefen,eric,spog_qa_malleswari,rakesh,spog_ramya_,spog_Prasad_, spog_qa_ramesh";
//		String names = "spog_qa_, spog_udp_qa_";
//		String names="rakesh*suite";
		String names = "auto";
		
		String orgString=null;
		String token=null;
		
		spogServer=new SPOGServer(baseURI, port);
		org4SPOGServer = new Org4SPOGServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		token = spogServer.getJWTToken();

		//date formatter
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
		String date=dateFormater.format(new Date());
		String prefix=spogServer.ReturnRandom(names);

		String file = "E:\\Orgdata\\"+date+".txt";
		File fil = new File(file);
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(fil, true));
		writer.newLine();
		writer.write(new SimpleDateFormat("yyyy-MM-dd HH:MM:SS").format(new Date()));

		String[] allNames = names.split(",");
		for (int t = 0; t < allNames.length; t++) {
			orgString=(allNames[t]);
			writer.write("search_string used: "+orgString);
			writer.newLine();

			if (!orgString.contains("submsp")) {
				String submspString=orgString+"*submsp";
				printDestroy(token, writer, submspString);	
			}
			printDestroy(token, writer, orgString);			
		}
		writer.flush();
		writer.close();
	}
	
	public void printDestroy(String token,BufferedWriter writer, String orgString ) throws IOException {
		org4SPOGServer.setToken(spogServer.getJWTToken());
		int total_page=org4SPOGServer.getOrgPagesBySearchStringWithCsrLogin(spogServer.getJWTToken(),orgString);
		if(total_page>0){
			for(int i=1;i<=total_page;i++ ){

				/*********** Write org details to output file ***********/
				ArrayList<String> ret=org4SPOGServer.getOrgIdNameBySearchStringWithCsrLogin(spogServer.getJWTToken(),orgString,1);
				for (int j = 0; j < ret.size(); j++) { 
					writer.write("Organization ID and Name: "+ret.get(j).toString());
					writer.newLine();
				}
				/************* Recycle and delete ***************/
				if (destroy) {
					ret=org4SPOGServer.getOrgIdsBySearchStringWithCsrLogin(spogServer.getJWTToken(),orgString,1);
					if(ret!=null){
						if(ret.size()>0){
							for(int del=0;del<ret.size();del++ ){
								if (ret.get(del).equalsIgnoreCase("11111111-2222-3333-1111-222222222222")) {

								} else {
									spogDestinationServer.recycleCloudVolumesAndDestroyOrg(token, ret.get(del));
								}							
							}
						}					
					}	
				}					
			}
		}
	}
	
	public void destroyOrgByUserName() {
		
		String name="";
		spogServer=new SPOGServer(baseURI, port);
		org4SPOGServer = new Org4SPOGServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		String token = spogServer.getJWTToken();

		Response response = userSpogServer.postUsersSearchResponse(name, "1", "100", token);
		ArrayList<String> orgIds = response.then().extract().path("data.organization.organization_id");
		for (String orgId : orgIds) {
			spogDestinationServer.recycleCloudVolumesAndDestroyOrg(token, orgId);
		}
	}
	
}
