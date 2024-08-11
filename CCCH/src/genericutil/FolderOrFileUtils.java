package genericutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class FolderOrFileUtils {
	public static String getD2DBuildFromFile(String filePath) throws IOException{
		BufferedReader bufRead;
		String line;    // String that holds current file line
		bufRead= new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));				
		line = bufRead.readLine();
		bufRead.close();
		return line;		
	}
	
	public static void runPowerShell(String strCmd) throws IOException{
		Runtime rt = Runtime.getRuntime();
		Process process = rt.exec("powershell " + strCmd );
		process.getOutputStream().close();		
	}
	
	public static long mountNetDrive(String sharePath, String volume, String userName, String password) throws IOException, InterruptedException{
		//String delCmd="c:\\windows\\system32\\net.exe use " + volume + " /del /y";
		String checkExistingMount="net use";
		long status1 = ExecCmd(checkExistingMount,sharePath+";"+volume);
		if(status1==0){
			return status1;
		}
		String delCmd="net use " + volume + " /del /y";
		status1 = ExecCmd(delCmd,"successfully");
		String cmdSample="net use x: \\\\111.111.111.111\\c$";
		String command = "net use " + volume + " " + sharePath + " /user:" + userName + " " + password;
		long status = ExecCmd(command,"successfully");
		return status;	
	}
	
	public static long  ExecCmd(String strCmd, String strExp) throws IOException, InterruptedException{
		Runtime rt = Runtime.getRuntime();
		String[]filters = strExp.split(";");	
		long status = -1;	
		String cmdOutput=null;			
		Process process = rt.exec(strCmd);
		process.waitFor();			
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream())); 
		do {
			cmdOutput = bufferedReader.readLine();
			System.out.println(cmdOutput);
			for(int i=0;i<filters.length;i++){
				if(cmdOutput!=null){
					if (cmdOutput.toLowerCase().matches(filters[i].toLowerCase())||(cmdOutput.toLowerCase().contains(filters[i].toLowerCase()))){
						status ++;					
					}
				}				
				if(status==filters.length-1){
					status=0;
					return status;
				}
			}
		}while(cmdOutput!=null);
		return status;
	}
	public static String getContents(String filePath) {
	    //...checks on aFile are elided
		File aFile=new File(filePath);
	    StringBuilder contents = new StringBuilder();	    
	    try {
	      //use buffering, reading one line at a time
	      //FileReader always assumes default encoding is OK!
	      BufferedReader input =  new BufferedReader(new FileReader(aFile));
	      try {
	        String line = null; //not declared within while loop
	        /*
	        * readLine is a bit quirky :
	        * it returns the content of a line MINUS the newline.
	        * it returns null only for the END of the stream.
	        * it returns an empty String if two newlines appear in a row.
	        */
	        while (( line = input.readLine()) != null){
	          contents.append(line);
	          contents.append(System.getProperty("line.separator"));
	        }
	      }
	      finally {
	        input.close();
	      }
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
	    return contents.toString();
	  }
	
	public static long removeNetDrive() throws IOException, InterruptedException{
		String command = "c:\\windows\\system32\\net.exe use * /delete /y";
		long status = ExecCmd(command,"successfully");
		return status;		
	}

	public static  boolean EmptyDirectory(File path) {
		File[] files;
		boolean rsValue = true;
	    if( path.exists() ) {
	      files = path.listFiles();
	      for(int i=0; i<files.length; i++) {
	         if(files[i].isDirectory()) {
	           deleteDirectory(files[i]);
	         }
	         else {
	           files[i].delete();
	         }
	      }
	      if (files.length !=0) {
	    	  rsValue = false;
	  	  } 
	    }	   
	    return rsValue;
	  }
	
	public static boolean deleteDirectory(File path) {
	    if( path.exists() ) {
	      File[] files = path.listFiles();
	      for(int i=0; i<files.length; i++) {
	         if(files[i].isDirectory()) {
	           deleteDirectory(files[i]);
	         }
	         else {
	           files[i].delete();
	         }
	      }
	      return( path.delete() );
	    }
	    else {
	    	return true;
	    }	   
	  }
	
	public static void copyFile(String srcPath, String destPath){
		File srcFile = new File(srcPath);
		File destFile = new File(destPath);
		InputStream in;
		try {
			in = new FileInputStream(srcFile);
			OutputStream out = new FileOutputStream(destFile);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0){
			  out.write(buf, 0, len);
			  out.flush();
			 }
			 in.close();
			 out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	  }
	
	public static void createDirecotry(String path){
		File fDir = new File (path);
		if (!fDir.exists()){
			fDir.mkdir();
		}
	}
	
	public static boolean deleteFile(String path) {
		File fFile = new File(path);
		if (fFile.exists()){
			try {
				fFile.delete();
				Thread.sleep(1500);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return !checkFileExist(path);
	}
	
	public static void cleanOrCreateFolder(String buildInfoPath){
		File fBuildInfoPath = new File(buildInfoPath);
		if (fBuildInfoPath.exists()){
			EmptyDirectory(fBuildInfoPath);
		}
		else{
			fBuildInfoPath.mkdir();
		}		
	}
	
	public static boolean checkFileExist(String filePath) {
		File fFile = new File(filePath);
		if (fFile.exists()){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static boolean waitPowerOnFinish(String filePath, long timeout) throws InterruptedException{
		File fFile = new File(filePath);
		long count = 0;
		while ((!fFile.exists()) && (count  < timeout)){
			Thread.sleep(5000);
			count = count + 5000;
		}
		return fFile.exists();
	}	 
	
	public boolean DeleteFileOrDirectorOnLinuxNode(String linuxNodeName, String userName, String pwd, String deleteFileOrDirectorScript,String fullFileName)
	{
    	System.out.println("************Invoking operation: DeleteFileOrDirectorOnLinuxNode");
		JSchHelper jsch = new JSchHelper(userName, pwd, linuxNodeName);
		String cmd = deleteFileOrDirectorScript + " "+ fullFileName;
		System.out.println("The command for deleting file Or director is: " + cmd);
		
		String result = jsch.executeCommand(cmd);
		System.out.println("The result of deleting file Or director is: " + result);
		
		if(result.contains("true"))
		{
			return true;
		} else
		{
			return false;
		}
		
		
	}
    public boolean CheckMd5ForFile(String linuxNodeName, String userName, String pwd, String filePath,String comparedMd5)
	{
    	System.out.println("************Invoking operation: CheckMd5ForFile");
		JSchHelper jsch = new JSchHelper(userName, pwd, linuxNodeName);
		String cmd = "md5sum "+ filePath;
		System.out.println("The command for checking the md5 of file is: " + cmd);
		
		String result = jsch.executeCommand(cmd);
		System.out.println("The result of checking the md5 of file is: " + result);
		
		if(result.equalsIgnoreCase(comparedMd5))
		{
			return true;
		} else
		{
			return false;
		}
		
	}
    
    public boolean CheckFileExistOnLinuxNode(String linuxNodeName, String userName, String pwd, String checkFileExistScript,String fullFileName, String ExpectValue)
	{
    	System.out.println("************Invoking operation: CheckFileExistOnLinuxNode");
    	JSchHelper jsch = new JSchHelper(userName, pwd, linuxNodeName);
		String cmd = checkFileExistScript + " "+ fullFileName;
		System.out.println("The command for checking file exist is: " + cmd);
		
		String result = jsch.executeCommand(cmd);
		System.out.println("The result of checking file exist is: " + result);
		if(result.equalsIgnoreCase(ExpectValue))
		{
			return true;
		} else
		{
			return false;
		}
	}
    
    public boolean CheckFileContentOnLinuxNode(String linuxNodeName, String userName, String pwd, String checkFileContentScript,String fullFileName, String checkedString)
	{
    	System.out.println("************Invoking operation: CheckFileContentOnLinuxNode");
		
		JSchHelper jsch = new JSchHelper(userName, pwd, linuxNodeName);
		String cmd = checkFileContentScript + " "+ fullFileName + " " + checkedString;
		System.out.println("The command for checking file content is: " + cmd);
		
		String result = jsch.executeCommand(cmd);
		System.out.println("The result of checking file content is: " + result);
		
		if(result.contains("true"))
		{
			return true;
		} else
		{
			return false;
		}
	}
    
    public boolean CopyFileOnLinuxNode(String linuxNodeName, String userName, String pwd, String copyFileScript,String sourceFilepath,String destFilePath)
	{
    	System.out.println("************Invoking operation: CopyFileOnLinuxNode");
		
		JSchHelper jsch = new JSchHelper(userName, pwd, linuxNodeName);
		String cmd = copyFileScript + " "+ sourceFilepath+ " "+ destFilePath;
		System.out.println("The command for copying file is: " + cmd);
		
		String result = jsch.executeCommand(cmd);
		System.out.println("The result of copying file is: " + result);
		
		if(result.contains("true"))
		{
			return true;
		} else
		{
			return false;
		}
	}
    
    public boolean CreateFolderOnLinuxNode(String linuxNodeName, String userName, String pwd, String createFolderScript,String folderPath)
	{
    	System.out.println("************Invoking operation: CopyFileOnLinuxNode");
		
		JSchHelper jsch = new JSchHelper(userName, pwd, linuxNodeName);
		String cmd = createFolderScript + " "+ folderPath;
		System.out.println("The command for creating folder is: " + cmd);
		
		String result = jsch.executeCommand(cmd);
		System.out.println("The result of creating folder is: " + result);
		
		if(result.contains("true"))
		{
			return true;
		} else
		{
			return false;
		}
	}
    
}

