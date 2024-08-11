package genericutil;

import genericutil.ErrorHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;


public class SQLServerDb {

	private String dbURL = "http://mabji01-hm02.arcserve.com:3000/bqinfos/updatedb";
	private String aaaSURL = "http://mabji01-hm02.arcserve.com:3000/bqs/jobmonitor";
	private String aaaSStatusURL = "http://mabji01-hm02:3000/bqs/bq_update";

	ErrorHandler errorhandle = null;
    public static void main(String[] args) throws ClientProtocolException, IOException {
    	SQLServerDb db1=new SQLServerDb();
    	
    	db1.updateAaaSTable("10", "2", "10", "3", "2", "1", "3765");
    	
    	long currentTime1 = System.currentTimeMillis()/60000;
    	long currentTime=(currentTime1-29)*60000;
    	db1.updateTable("Lite Integration agent based backups - UDP Dedupe Datastore 2-step restore", "barre02-bqs3", "17.0.7514", "26", "26", "0", "0", currentTime, "Passed");
    }
    
    public boolean updateAaaSTable(
    		String bqID, 
    		String status, 
    		String totalCases, 
    		String passedCases, 
    		String notRunCases, 
    		String failedCases, 
    		String buildNumber )throws ClientProtocolException, IOException
    {
    	errorhandle = new ErrorHandler();
    	HttpClient client = new DefaultHttpClient();
    	HttpPost post = new HttpPost(aaaSURL);
    	
    	//{"bqjob":{"bq_id":10,"status":3, "total":10, "passed":3, "notrun":7, "failed":0, "build":"4449"}}
    	String content = "{\"bqjob\":{\"bq_id\":" + bqID + 
    			", \"status\":" + status + ", \"total\":" + totalCases + 
    			", \"passed\":" + passedCases + ", \"notrun\":" + notRunCases + 
    			", \"failed\":" + failedCases + ", \"build\":\"" + buildNumber + "\"}}";
    	errorhandle.printInfoMessageInDebugFile("The rest post content is:" + content);
    	StringEntity input = new StringEntity(content);
    	input.setContentType("application/json");
    	post.setEntity(input);
        HttpResponse response = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = "";
        while ((line = rd.readLine()) != null) {
        	errorhandle.printDebugMessageInDebugFile(line);
        }
        
        updateBQStatusToActive(bqID);
        
        return true;
    }
    
    private boolean updateBQStatusToActive(String bqID) throws ClientProtocolException, IOException
    {
    	errorhandle = new ErrorHandler();
    	HttpClient client = new DefaultHttpClient();
    	HttpPost post = new HttpPost(aaaSStatusURL);
    	
    	//{"bq":{"id":10,"status":1}} 1 means Active.
    	String content = "{\"bq\":{\"id\":" + bqID + ",\"status\":1}}";
    	
    	errorhandle.printInfoMessageInDebugFile("The rest post content is:" + content);
    	StringEntity input = new StringEntity(content);
    	input.setContentType("application/json");
    	post.setEntity(input);
        HttpResponse response = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = "";
        while ((line = rd.readLine()) != null) {
        	errorhandle.printDebugMessageInDebugFile(line);
        }
    	
    	return true;
    }
    
    public boolean updateTable(	String bqName, 
			String machineName, 
			String buildNumber,
			String totalCases, 
			String passedNumber, 
			String faileNumber, 
			String remainCases,
			long creationTime,
			String sts) throws ClientProtocolException, IOException{
    	errorhandle = new ErrorHandler();
		long currentTime = System.currentTimeMillis();
		errorhandle.printInfoMessageInDebugFile("The current time is "+currentTime);
		errorhandle.printInfoMessageInDebugFile("The creation time is "+creationTime);
    	long timetaken = ((currentTime - creationTime)/1000);
    	//String timeTaken = creationTime + " Mins";
    	String timeTaken = timetaken + " Sec";
    	errorhandle.printInfoMessageInDebugFile("The run time in sec "+timeTaken);
    	String os = System.getProperty("os.name");
    	if(os.contains("unknow")&&machineName.contains("shaji02")) {
    		os="Windows Server 2012 R2";
    	}
    	HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(dbURL);
        String aa="{\"bqname\":\""+bqName+"\", \"casenumber\":"+totalCases+", \"passedcasenumber\":"+
		passedNumber+", \"failedcasenumber\":"+faileNumber+", \"remaincasenumber\":"+
		remainCases+", \"runmachine\":\""+machineName+"\", \"buildnumber\":\""+
		buildNumber+"\", \"status\":\""+sts+"\", \"timetaken\":\""+timeTaken+"\", \"platform\":\""+
		os+"\"}";
        errorhandle.printInfoMessageInDebugFile("The rest post content is:"+aa);
    	StringEntity input = new StringEntity(aa);
        input.setContentType("application/json");
        post.setEntity(input);
        HttpResponse response = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = "";
        while ((line = rd.readLine()) != null) {
            System.out.println(line);
        }
        return true;
    }
    
    public boolean updateTable(	String bqName, 
			String machineName, 
			String buildNumber,
			String totalCases, 
			String passedNumber, 
			String faileNumber, 
			String remainCases,
			long creationTime,
			String sts,
			String author) throws ClientProtocolException, IOException{
    	errorhandle = new ErrorHandler();
		long currentTime = System.currentTimeMillis();
		errorhandle.printInfoMessageInDebugFile("The current time is "+currentTime);
		errorhandle.printInfoMessageInDebugFile("The creation time is "+creationTime);
    	long timetaken = ((currentTime - creationTime)/1000);
    	//String timeTaken = creationTime + " Mins";
    	String timeTaken = timetaken + " Sec";
    	errorhandle.printInfoMessageInDebugFile("The run time in sec "+timeTaken);
    	String os = System.getProperty("os.name");
    	if(os.contains("unknow")&&machineName.contains("shaji02")) {
    		os="Windows Server 2012 R2";
    	}
    	HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(dbURL);
        String aa="{\"bqname\":\""+bqName+"\", \"casenumber\":"+totalCases+", \"passedcasenumber\":"+
		passedNumber+", \"failedcasenumber\":"+faileNumber+", \"remaincasenumber\":"+
		remainCases+", \"runmachine\":\""+machineName+"\", \"buildnumber\":\""+
		buildNumber+"\", \"status\":\""+sts+"\", \"comment\":\""+author+"\", \"timetaken\":\""+timeTaken+"\", \"platform\":\""+
		os+"\"}";
        errorhandle.printInfoMessageInDebugFile("The rest post content is:"+aa);
    	StringEntity input = new StringEntity(aa);
        input.setContentType("application/json");
        post.setEntity(input);
        HttpResponse response = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = "";
        while ((line = rd.readLine()) != null) {
            System.out.println(line);
        }
        return true;
    }
}