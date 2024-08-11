package Report;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class OpenReport {

	/** Opens the report after suite
	 * 
	 * @author Rakesh.Chalamala
	 * @param logFolder
	 * @param filename
	 */
	@AfterSuite()
	@Parameters({"logFolder","testName"})
	public void excCommand(String logFolder, String filename){
		
		
		String file = logFolder+"\\"+getDate()+"\\"+filename+".html";
		
		System.out.println(file);
		
	    Runtime rt = Runtime.getRuntime();
	    try {
	        rt.exec(new String[]{"cmd.exe","/c","start "+file});

	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	}
	
	public String getDate() {
		
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
		String date = dateFormater.format(new Date());
		
		return date;
	}
	
}