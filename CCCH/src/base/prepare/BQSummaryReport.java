package base.prepare;

import static org.testng.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.AfterSuite;
import org.testng.internal.Utils;
import org.testng.xml.XmlClass;

import com.google.common.base.Splitter;

import genericutil.FolderOrFileUtils;
import genericutil.battsendmail;

public class BQSummaryReport extends base.prepare.Is4Org implements ISuiteListener, ITestListener, IInvokedMethodListener {

	private PrintWriter writer = null;
	private String summaryFilePath = null;
	private String mailRecipients = null;
	private List<XmlClass> allClassesInSuite = null;
	private long startTime = 0;
	private String reportTitle = "SPOG BQ Report";
	private String suiteName="";
	private String lastClassName = null;
	private int casesCounter = 0;
	private int failedCasesCounter = 0;
	private int passedCasesCounter = 0;
	private int skippedCasesCounter = 0;
	private ArrayList<HashMap<String, Object>> classesInfo = new ArrayList<>();
	ArrayList<HashMap<String, Object>> failuresInfo = new ArrayList<>();
	ArrayList<HashMap<String, Object>> skipsInfo = new ArrayList<>();
	
	private CreateOrgsInfo orgsInfo;
	private CreateCCorgsInfo ccOrgsInfo;
	private static String filePath;
	
	public static String getFilePath() {
		return filePath;
	}

	public static void setFilePath(String filePath) {
		BQSummaryReport.filePath = filePath;
	}

	/** Opens the report after suite
	 * 
	 * @author Rakesh.Chalamala
	 * @param logFolder
	 * @param filename
	 * @throws InterruptedException 
	 */
	@AfterSuite
	public void excCommand() throws InterruptedException{
		
	    Runtime rt = Runtime.getRuntime();
	    try {
	        rt.exec(new String[]{"cmd.exe","/c","start "+summaryFilePath});
	    } catch (IOException e) {
	        e.printStackTrace();
	    }	    
	}
	

	public void setSuiteName(String suite) {
		suiteName=suite;
	}
	
	@Override
	public void onStart(ISuite suite) {
		setSuiteName(suite.getName());
		recycleVolumeInCDandDestroyOrg(suite.getName());
	
		try {
			if (suite.getParameter("enroll").toString().equalsIgnoreCase("true")) {
				orgsInfo = new CreateOrgsInfo(suite, createWriter());
				orgsInfo.create();
			}else {
				ccOrgsInfo = new CreateCCorgsInfo(suite, createWriter());
				ccOrgsInfo.create();
			}
		}catch (Exception e) {

			System.out.println("creating organization got failed due to error "+e);
			recycleVolumeInCDandDestroyOrg(suite.getName());
		}
	}
	
	public PrintWriter createWriter(String path) {
		try {
			return new PrintWriter(new BufferedWriter(new FileWriter(new File(path))));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public PrintWriter createWriter() {
		File tempFile;
		PrintWriter writer = null;
		try {
			tempFile = File.createTempFile("orgInfo", ".txt");
			String path = tempFile.getAbsolutePath();
			setFilePath(path);

			writer = new PrintWriter(new BufferedOutputStream(new FileOutputStream(tempFile)));
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return writer;
	}

	/** Starts HTML stream */
	protected void startHtml(PrintWriter out) {
		out.println("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.1//EN' 'http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd'>");
		out.println("<html xmlns='http://www.w3.org/1999/xhtml'>");
		out.println("<head>");
		out.println("<title>SPOG BQ Report</title>");
		out.println("<style type='text/css'>");
		
		out.println(".boldtable, .boldtable TD, .boldtable TH "
					+ "{font-size:10pt; border: 1px solid #CCCCCC; padding-left:2px; padding-right:2px }");
		out.println(".data {padding-left: 3px; padding-right:3px}");
		out.println("</style>");
		out.println("</head>");
		out.println("<body>");	
	}
	
	protected void writeReportTitle(String title) {
		writer.println("<center><h2>" + title + " - " + getDate() + "</h2></center>");
	}

	@Override
	public void onFinish(ISuite suite) {
		
		writeSummaryToFile(suite);
		sendReport();
	
		recycleVolumeInCDandDestroyOrg(suite.getName());
		assertTrue(FolderOrFileUtils.deleteFile(BQSummaryReport.filePath), "Organizations info file deleted successfully.");
	}
	
	public void writeSummaryToFile(ISuite suite) {
		
		this.summaryFilePath = suite.getParameter("summaryFilePath");
		this.mailRecipients = suite.getParameter("mailRecipients");
	
		this.writer = createWriter(summaryFilePath);
		startHtml(writer);
		
		if(classesInfo.size() < 1)	//If only one class exists in suite 
			composeIfOnlyOneClass();
		composeBQSummary();
		
		if (failuresInfo.size() > 0) {
			composeFaildSummary();
		}
		
		if (skipsInfo.size() > 0) {
			composeSkippedSummary();
		}	
		
		endHtml(writer);
		writer.flush();
		writer.close();	
	}
	
	public void composeIfOnlyOneClass() {
		outerTableStart();
		
		long executionTime = (System.currentTimeMillis()-startTime)/1000;
		int totalCases = passedCasesCounter+failedCasesCounter+skippedCasesCounter;
		
		HashMap<String, Object> classInfo = new HashMap<>();
		classInfo.put("classname", lastClassName);
		classInfo.put("allcases", totalCases);
		classInfo.put("passedcases", passedCasesCounter);
		classInfo.put("failedcases", failedCasesCounter);
		classInfo.put("skippedcases", skippedCasesCounter);
		classInfo.put("executiontime", executionTime);
		
		classesInfo.add(classInfo);
		lastClassName = null;
		casesCounter = 0;
		passedCasesCounter = 0;
		failedCasesCounter = 0;
		skippedCasesCounter = 0;
		startTime = 0;		
		
		outerTableEnd();
	}

	private void sendReport() {
	
		reportTitle += " "+getDate();
		
		for (int trytimes = 0; trytimes < 20; trytimes++){
    		boolean ret=battsendmail.send(summaryFilePath, "smtp.office365.com", "587", "Automation@arcservemail.onmicrosoft.com", "Boxa6033", mailRecipients, reportTitle,summaryFilePath);
	    	
	    	if(ret) {
	    		System.out.println("Mail sent successfully");
	    		break;
	    	}
    	}
	}

	@Override
	public void onTestStart(ITestResult result) {
		
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		passedCasesCounter++;		
	}

	@Override
	public void onTestFailure(ITestResult result) {
		failedCasesCounter++;
		
		String className = result.getInstance().getClass().getSimpleName();
		String methodName = result.getMethod().getMethodName();
		String errorMessage = null;
		String message = "";
		
		Throwable exception=result.getThrowable();
		boolean hasThrowable = exception != null;
		if(hasThrowable){
			String str = Utils.stackTrace(exception, true)[0];
			Scanner scanner = new Scanner(str);
			errorMessage = scanner.nextLine();
			if (errorMessage.length()/101 > 1) {
				Iterable<String> msgs = Splitter.fixedLength(100).split(errorMessage.trim());
				for (String msg : msgs) {
				 	message += msg +"<br/>";
				}
			}else {
				message = errorMessage;
			}
			errorMessage = message+"<br/>"+scanner.nextLine();
		}
		
		HashMap<String, Object> failureInfo = new HashMap<>();
		failureInfo.put("classname", className);
		failureInfo.put("methodname", methodName);
		failureInfo.put("errormessage", errorMessage);
		
		failuresInfo.add(failureInfo);
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		skippedCasesCounter++;
		
		if(skippedCasesCounter>3)
		{
			recycleVolumeInCDandDestroyOrg(suiteName);
		}
		String className = result.getMethod().getRealClass().getSimpleName();
		String methodName = result.getMethod().getMethodName();
		String errorMessage = null;
		String message = "";
		
		Throwable exception=result.getThrowable();
		boolean hasThrowable = exception != null;
		if(hasThrowable){
			String str = Utils.stackTrace(exception, true)[0];
			Scanner scanner = new Scanner(str);
			errorMessage = scanner.nextLine();
			if (errorMessage.length()/101 > 1) {
				Iterable<String> msgs = Splitter.fixedLength(100).split(errorMessage.trim());
				for (String msg : msgs) {
				 	message += msg +"<br/>";
				}
			}else {
				message = errorMessage;
			}
			errorMessage = message+"<br/>"+scanner.nextLine();
		}
		
		HashMap<String, Object> skipInfo = new HashMap<>();
		skipInfo.put("classname", className);
		skipInfo.put("methodname", methodName);
		skipInfo.put("errormessage", errorMessage);
		
		skipsInfo.add(skipInfo);
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		
	}

	@Override
	public void onStart(ITestContext context) {
		// TODO Auto-generated method stub
		allClassesInSuite = context.getCurrentXmlTest().getClasses();		
	}

	@Override
	public void onFinish(ITestContext context) {
		
	}

	@Override
	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {

		if (lastClassName == null) {
			startTime = System.currentTimeMillis();
		}
	}

	@Override
	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
			
		if (lastClassName == null) {
			lastClassName = method.getTestMethod().getInstance().getClass().getSimpleName();
			casesCounter++;
		}else {
			String className = method.getTestMethod().getInstance().getClass().getSimpleName();
			if (className.equalsIgnoreCase(lastClassName)) {
				casesCounter++;				
			}else {				
				long executionTime = (System.currentTimeMillis()-startTime)/1000;
				int totalCases = passedCasesCounter+failedCasesCounter+skippedCasesCounter;
				
				HashMap<String, Object> classInfo = new HashMap<>();
				classInfo.put("classname", lastClassName);
				classInfo.put("allcases", totalCases);
				classInfo.put("passedcases", passedCasesCounter);
				classInfo.put("failedcases", failedCasesCounter);
				classInfo.put("skippedcases", skippedCasesCounter);
				classInfo.put("executiontime", executionTime);
				
				classesInfo.add(classInfo);
				lastClassName = null;
				casesCounter = 0;
				passedCasesCounter = 0;
				failedCasesCounter = 0;
				skippedCasesCounter = 0;
				startTime = 0;
			}
		}	
		
	}

	public void composeBQSummary() {
		
		outerTableStart();
		
//		tableStart("bqsummary");
		tableStart("");
		titleRow("BQ SUMMARY", "");
//		titleRow("BQ SUMMARY", "bqsummarytitle");
		writer.println("<tr valign='top'><td>");
		innerTableStart();
		
		writer.print("<tr bgcolor='#EBC79E' width='100%'>");
		tableColumnStart("BQ NAME");
		tableColumnStart("TOTAL CASES");
		tableColumnStart("PASSED");
		tableColumnStart("FAILED");
		tableColumnStart("SKIPPED");
		tableColumnStart("STATUS");
		tableColumnStart("TOTAL<br/>TIME(hh:mm:ss)");
		writer.println("</tr>");
		
		for (int i = 0; i < classesInfo.size(); i++) {
			
			HashMap<String, Object> classInfo = classesInfo.get(i);
			
			startSummaryRow(classInfo.get("classname").toString(), false);
			summaryCell(classInfo.get("allcases").toString(), true);
			summaryCell(classInfo.get("passedcases").toString(), true);
			summaryCell(classInfo.get("failedcases").toString(), true);
			summaryCell(classInfo.get("skippedcases").toString(), true);
			
			if (Integer.valueOf(classInfo.get("failedcases").toString()) > 0) {
				summaryCell("FAILED", "RED", classInfo.get("classname").toString());
			}else if (Integer.valueOf(classInfo.get("skippedcases").toString()) > 0) {
				summaryCell("SKIPPED", "ORANGE", classInfo.get("classname").toString());
			}else {
				summaryCell("PASSED", "GREEN", null);
			}
			
			summaryCell(timeConversion(Long.valueOf(classInfo.get("executiontime").toString())), true);
			writer.println("</tr>");
		}
		
		innerTableEnd();
		writer.println("</td></tr>");
		writer.println("</table>");
		
		outerTableEnd();
	}
	
	public void composeFaildSummary(){
		
		outerTableStart();
		tableStart("");		
		titleRow("FAILED CASES INFORMATION", "");
		writer.println("<tr valign='top'><td>");
		innerTableStart();
		
		writer.print("<tr bgcolor='#EBC79E' width='100%'>");
		tableColumnStart("BQ NAME");
		tableColumnStart("TEST METHOD/CASE");
		tableColumnStart("FAILURE REASON");
		writer.println("</tr>");
		
		for (int i = 0; i < failuresInfo.size(); i++) {
			HashMap<String, Object> failureInfo = failuresInfo.get(i);
			
			startSummaryRow(failureInfo.get("classname").toString(), true);
			summaryCell(failureInfo.get("methodname").toString());
			summaryCell(failureInfo.get("errormessage").toString());
			
			writer.println("</tr>");
		}
		innerTableEnd();
		writer.println("</td></tr>");
		writer.println("</table>");
		outerTableEnd();
	}
	
	public void composeSkippedSummary(){
		
		outerTableStart();
		tableStart("");		
		titleRow("SKIPPED CASES INFORMATION", "");
		writer.println("<tr valign='top'><td>");
		innerTableStart();
		
		writer.print("<tr bgcolor='#EBC79E' width='100%'>");
		tableColumnStart("BQ NAME");
		tableColumnStart("TEST METHOD/CASE");
		tableColumnStart("SKIP REASON");
		writer.println("</tr>");
		
		for (int i = 0; i < skipsInfo.size(); i++) {
			HashMap<String, Object> skipInfo = skipsInfo.get(i);
			
			startSummaryRow(skipInfo.get("classname").toString(), true);
			summaryCell(skipInfo.get("methodname").toString());
			summaryCell(skipInfo.get("errormessage").toString());
			
			writer.println("</tr>");
		}
		innerTableEnd();
		writer.println("</td></tr>");
		writer.println("</table>");
		outerTableEnd();
	}
	
	private void outerTableStart() {
		
		writer.println("<font size='2'> <table width='750' style='border-top:1px solid; border-left:1px solid; border-right:1px solid; border-bottom:1px solid' border='0' cellpadding='1'>"
						+"<tr>"
							+"<td>");
	}
	
	private void outerTableEnd() {
		writer.println("</td>");
		writer.println("</tr>");
		writer.println("</table>");
		writer.println("</font>");
		
		writer.println("<br/>");
		writer.println("<br/>");
		writer.println("<br/>");
	}
	
	private void innerTableStart() {
		writer.println("<table width='100%' class='boldtable'>");
	}
	
	private void innerTableEnd() {
		writer.println("</table>");
	}

	private void tableStart(String id) {
		writer.println("<table width='100%'>");		
	}
	
	private void titleRow(String label, String id) {
//		writer.print("<tr bgcolor='#336699'"+ id!=null ? "id="+id : ""+">" );
		writer.println("<tr bgcolor='#336699'>");
		writer.println("<td colspan='2'><center><b><font size='3' color='white'>" + label + "</font></b></center></td></tr>");
	}

	private void tableColumnStart(String label) {
		writer.print("<th colspan='2' class='data'>" + label + "</th>");
	}
	
	/** Finishes HTML stream */
	protected void endHtml(PrintWriter out) {
//		out.println("<center> TestNG Report </center>");
		out.println("</body></html>");
	}
	
	private void startSummaryRow(String label) {
		writer.print("<tr > "
					+ "<td colspan='2' class='data' nowrap='nowrap' id='"+label+"'><b>" + label + "</b></td>");
	}
	
	private void startSummaryRow(String label, boolean setID) {
		if (setID) {
			writer.print("<tr><td colspan='2' class='data' nowrap='nowrap' id='"+label+"'><b>" + label + "</b></td>");	
		}else {
			writer.print("<tr><td colspan='2' class='data' nowrap='nowrap'><b>" + label + "</b></td>");
		}
		
		
	}
	
	private void summaryCell(String value) {
		writer.print("<td colspan='2' class='data' nowrap='nowrap'>" + value + "</td>");
	}
	private void summaryCell(String value, boolean center) {		
		if (center) {
			value = "<center>"+value+"</center>";
		}	
		writer.print("<td colspan='2' class='data' nowrap='nowrap'>" + value + "</td>");
	}

	private void summaryCell(String value, String fontColor, String href) {		
		String tag="";
		if (href !=null && href !="") {
			tag = "<a href='#"+href+"'><font color='"+fontColor+"'>"  + value + "</font></a>";
		}else {
			tag = "<font color='"+fontColor+"'>" + value + "</font>";
		}
		
		writer.print("<td nowrap='nowrap' colspan='2' class='data'>" + tag + "</td>");
	}

	private String timeConversion(long seconds) {

		final int MINUTES_IN_AN_HOUR = 60;
		final int SECONDS_IN_A_MINUTE = 60;

		int minutes = (int) (seconds / SECONDS_IN_A_MINUTE);
		seconds -= minutes * SECONDS_IN_A_MINUTE;

		int hours = minutes / MINUTES_IN_AN_HOUR;
		minutes -= hours * MINUTES_IN_AN_HOUR;

		return prefixZeroToDigit(hours) + ":" + prefixZeroToDigit(minutes) + ":" + prefixZeroToDigit((int) seconds);
	}
	
	private String prefixZeroToDigit(int num) {
		int number = num;
		if (number <= 9) {
			String sNumber = "0" + number;
			return sNumber;
		} else
			return "" + number;

	}
	
	/**
	 * Returns the last modified file in path provided
	 * 
	 * @author Rakesh.Chalamala
	 * @param dir
	 * @return
	 */
	public static File lastFileModified(String dir) {
	    File fl = new File(dir);
	    File[] files = fl.listFiles(new FileFilter() {          
	        public boolean accept(File file) {
	            return file.isFile();
	        }
	    });
	    long lastMod = Long.MIN_VALUE;
	    File choice = null;
	    for (File file : files) {
	        if (file.lastModified() > lastMod) {
	            choice = file;
	            lastMod = file.lastModified();
	        }
	    }
	    return choice;
	}
	
	public static String getDate() {	
		SimpleDateFormat dateFormater = new SimpleDateFormat("dd-MM-yyyy");
		String date = dateFormater.format(new Date());
		
		return date;
	}
}