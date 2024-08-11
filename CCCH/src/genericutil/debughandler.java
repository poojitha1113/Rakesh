package genericutil;
import java.io.*;

import org.apache.log4j.*;
import com.ibm.staf.*;
import java.net.InetAddress;


/**
This class writes debug information of common function (CF) into a file.

Example:
The CFs to avail the feature, need to call a method as follows:

debughandle dh = debughandler.getdebughandler() ;
dh.writeDebugMsg("babqmgr: getJobStatus: status") ;

*/

public class debughandler
{
	//debug file path
	private static String DEBUG_FILE = "<debug file path>";
	private static String DEBUG_LEVEL = "debug";
	private static String debugFolderPath = null;
	
	//checkpoint name
	private String chkpt_name = "<chkpoint name>" ;
	
	//local boolean 
	private boolean debug_val = true;
	
	private Category log;
	propertyloader prop;
	
	//static handler
	private static debughandler debughandle;
	
	//staf handle to get the BATTHome and execute staf command etc
	private static STAFHandle handle;
	
	//type of the process: [Machine Name][Chkpoint Name] 
	private String processType;
	
	// debug info will contain this value under the column called "component":
	// typical value is: class name or exe name as appopriate	
	private String component = "Default";
	
	//traceHandle to get the classname in which debughandler gets called.
	private Trace traceHandle = null;
	
	private String stdOutEnabled = null;
	
	/**
	 * In this constructor we get the DEBUG_FILE path and CHKPT_NAME.
	 * When the values for those are not found defined, the debug info will be  
	 * sent to stdout
	 */
    protected debughandler( )
    {
	   try 
	   {
	       //the values are sent thru '-D' option
		   this.DEBUG_FILE = System.getProperty( "DEBUG_FILE" );
		   
		   if (this.DEBUG_FILE == null)
		   {
			   this.DEBUG_FILE = "debug.txt";
		   }
		   
		   //Check whether user has given any Debug folder option.
		   debugFolderPath = System.getProperty( "DEBUG_FOLDER" );
		  
		   //If debug folder path not null,
		   if( debugFolderPath != null)
		   {
			   
			   File debugFile = new File(debugFolderPath);
			   
			   //Create the user given debug folder structure, if not exists.
			   if(!(debugFile.exists()))
				   if(!(debugFile.mkdirs()))
						   throw new Exception("Unable to create the Debug Log folder: " + debugFolderPath +
						   		".Please check whether the given path is correct or not...");
			   
	 
			   //Check whether the RID folder already exists.
			   File[] fileList = debugFile.listFiles(new FilenameFilter() 
			   	{
				      public boolean accept(File filePath, String name)
				      {
				    	  if(filePath.isDirectory() && name.startsWith("RID["))
				    		  return true;
				    	  
				    	  return false;
				      }
			   	}
			   );
			   
			   
			   //If no folder exists, with Run ID(RID), create "RID[1]" folder.
			   if(fileList.length == 0)
			   {
				   if(!new File(debugFolderPath + "\\RID[1]").mkdirs())
					   throw new Exception("Unable to create the Run ID folder: RID[1] in : " + debugFolderPath );
				   
				   this.DEBUG_FILE = debugFolderPath + "\\RID[1]\\debug.txt"; 
			   }
			   else
			   {
				   
				   //If already some RID folders exist, get the latest one.
				   File folderName = fileList[fileList.length - 1];
				   String fileName = folderName.getName();
				   
				   //Get the index of RID latest folder.
				   int folderIndex = Integer.parseInt(fileName.substring(
			   				fileName.indexOf("[") + 1, fileName.length() - 1));
				   
				   //Increment the index and create new RID folder with incremented index.
				   if(!new File(debugFolderPath + "\\RID[" + (folderIndex + 1) + "]").mkdirs())
					   throw new Exception("Unable to create the Run ID folder: RID[" + 
							   				(folderIndex + 1) + " in : " + debugFolderPath );
				   
				   this.DEBUG_FILE = debugFolderPath + "\\RID[" + (folderIndex + 1) + "]\\debug.txt";
			   }
		   }
		   
		   
		   this.DEBUG_LEVEL = System.getProperty("DEBUG_LEVEL");
		   if (this.DEBUG_LEVEL  == null)
		   {
			   this.DEBUG_LEVEL = "debug";
		   }
		   this.chkpt_name = System.getProperty( "CHKPT_NAME" );		   
		   this.stdOutEnabled = System.getProperty( "OUTPUT" );
		   
		   /** -Ddebug_file=NONE;-Dtestcase_name=NONE
		    *  => This is for windows EXE to invoke the java class as process
		    * 
		    * 
		    */
		   
		   if( (DEBUG_FILE != null) && (DEBUG_FILE.trim().equalsIgnoreCase("NONE")))
		   {
				String batt_path = excuteStafCmd( );
				String prop_filename = batt_path + "/GeneralLib/CmnFunc/common/temp/prop.txt" ;
				File prop_file = new File( prop_filename );
				if( !prop_file.exists() )
				{
					boolean filecreated = prop_file.createNewFile( );
				}	
				
				//If we call debughandler in executables ,it will get the debugfile name and 
				//chkpt from prop.txt file
				
				prop = new propertyloader( new File( prop_filename ) );				
				this.component = prop.getProperty("exe_name");
				this.DEBUG_FILE = prop.getProperty("debug_file_name");
				this.chkpt_name =  prop.getProperty("chkpt_name");
		   }
		   /**
		    * This is for a java CF to use
		    */		   
		   else if( DEBUG_FILE == null && stdOutEnabled == null )
		   {
				debug_val = true;
				return;
		   }		   
		   else
		   {	
			   this.component = Trace.getCallerClass(4); 		
			   
		   }
		   
		   //Call configureLog4j func to configure log4j
		   configureLog4j( );
	   } 
	   catch( Exception e )
	   {
		   System.out.println("Exception in debughandler");
		   e.printStackTrace();
	   }
	}
    
    public static void initializeDebugHandler()
    {
    	System.out.println("setting debughandle to null");
    	debughandle.log.removeAllAppenders();
    	debughandle=null;
    }
    
    
    
	/**
	*This method returns the hostname of the local machine
	*/
	public String getHostname()
	{
		String hostname = "Local";
		try 
		{
			InetAddress addr = InetAddress.getLocalHost();
			
			// Get hostname
			hostname = addr.getHostName();
		} 
		catch (Exception e) 
		{
			System.out.println( "Error in getting hostname" + e );
		}
		return hostname;
	}
	
	/**
	 * This method executes the staf command to get the BATT home path
	 */
	public String excuteStafCmd( )
	{
	   String cmd[] = { "STAF", "local", "var","resolve","string", "{CA/BrightStorCFW/Home}" };
	   Process p = null;	
	   String batt_home_path = "";
	   
	   try
	   {
		    //Executes the specified string command in a separate process.
			p = Runtime.getRuntime().exec ( cmd );
			
		   //Storing the output of command in the input buffer
			BufferedReader cmdInput = new BufferedReader(new 
				InputStreamReader(p.getInputStream()));
  			  
			//command output and storing into string buffer					
			String cmdoutput = "";
			for( int i=0; i<=2; i++ )
			{
				batt_home_path = cmdInput.readLine();
			}
			
		}
		catch( Exception err )
		{
			err.printStackTrace( );
		}
		return batt_home_path;
	   
	}
	
	/**This method will retain the same 'debughandle' for all the CFs.*/
	public static debughandler getdebughandler( )
	{
	   if( debughandle == null )
	       debughandle = new debughandler( ); 
	   
	  return debughandle; 
	}
	
	/**
	 *  The debug Level can be either DEBUG/INFO/
	 *  DEBUG -> lowest level it will print all types of messages
	 *	INFO  -> It will print all other types of messages except the debug
	 *	WARN  -> It will print all other types of messages except Debug and Info
	 *	ERROR -> It will print all other only error and fatal messages
	 *  FATAL -> It will print only fatal messages
	 * @param debugLevel
	 */
	public void setDebugLevel()
	{	
		if( DEBUG_LEVEL == null || DEBUG_LEVEL.trim().length() == 0 || DEBUG_LEVEL.equalsIgnoreCase("DEBUG"))
			log.setLevel( ( Level ) Level.DEBUG);
		else if(DEBUG_LEVEL.equalsIgnoreCase("INFO"))
			log.setLevel( ( Level ) Level.INFO);
		else if(DEBUG_LEVEL.equalsIgnoreCase("WARN"))
			log.setLevel( ( Level ) Level.WARN);
		else if(DEBUG_LEVEL.equalsIgnoreCase("ERROR"))
			log.setLevel( ( Level ) Level.ERROR);
		else if(DEBUG_LEVEL.equalsIgnoreCase("FATAL"))
			log.setLevel( ( Level ) Level.FATAL);
	}
	
	/**This method will configure the log4j with category,appender and layout*/	
	 private void configureLog4j( )throws Exception 
	 {
		   // Set root category priority to DEBUG and set its only appender to log
	       log = Category.getRoot();
		   String pattern = "%p    %d{dd/MM/yyyy HH:mm:ss}    %m%n";
		   
		   //String pattern = "%d %-5p[HOST:" + getHostname() + ", CHKPT:" + chkpt_name + "] - %m%n" ;
		   //processType = "[HOST:" + getHostname() + ", CHKPT:" + chkpt_name + "]" ;
		   processType = "";
		   
		   try
		   { 
			if( DEBUG_FILE != null)
			{
				FileAppender fileAppender = new FileAppender( (new PatternLayout(pattern)),this.DEBUG_FILE , true ) ;
				log.addAppender( fileAppender );
				
			}
			else if( (DEBUG_FILE == null ) && (stdOutEnabled.equalsIgnoreCase("stdout")))
			{
				org.apache.log4j.ConsoleAppender  consoleAppender = new org.apache.log4j.ConsoleAppender( (new PatternLayout(pattern)) );
				log.addAppender( consoleAppender );
			}
			else
			{
				return;
			}	
				
	        //log.setLevel( ( Level ) Level.WARN );
			//Setting the debug level given by user
			setDebugLevel();
		   
		   }
		   catch( Exception e )
		   {
			  throw e;
		   }
	 }
	
	
	/**This method will write the message as information message in the file*/	
	public void writeInfoMsg ( String msg )
	{
		//log.log(Level.INFO, "debugHandler:325", null);
		//ganla01: set component
		String[] classes = Trace.getCallerClasses(null, 100);
		/*for(String clas : classes)
			log.log(Level.INFO, "classes: " + clas, null);*/
		//ganla01: set the component value to the latest calling class details 
		component=classes[3];
		//System.out.println("class got is: " + component);
		
		//log.log(Level.INFO, "component class: " + classes[3], null);
 		if( debug_val )
		{
			String msg2 = processType + "    " + component + "    " + msg;
			log.log(Level.INFO, msg2, null);
		}
		else
			return;
	}
	
	public void writeInfoMsg( File file, String msg ) {
	    return;
	}
	
	/**This method will write the message as debug message in the file*/	
	public void writeDebugMsg ( String msg )
	{		
		//ganla01: set component
		String[] classes = Trace.getCallerClasses(null, 100);
		
		//ganla01: set the component value to the latest calling class details 
		component=classes[3];
		
 		if( debug_val )
		{
			String msg2 = processType + "    " + component + "    " + msg;
			log.log(Level.INFO, msg2, null);
		}
		else
			return;
	}
	
	/**This method will write the message as error  message in the file*/	
	public void writeErrorMsg ( String msg )
	{	  
		//ganla01: set component
		String[] classes = Trace.getCallerClasses(null, 100);
		
		//ganla01: set the component value to the latest calling class details 
		component=classes[3];
		 
		  if( debug_val )
		  {
			String msg2 = processType + "    " + component + "    " + msg;
			log.log( Level.ERROR, msg2, null);
		  }
		  else
			return;
	}
	
	
	/**This method will write the message as warning  message in the file*/  
	public void writeWarnMsg ( String msg )
	{
		//ganla01: set component
		String[] classes = Trace.getCallerClasses(null, 100);
		
		//ganla01: set the component value to the latest calling class details 
		component=classes[3];
		
		if( debug_val )
		{
			String msg2 = processType + "    " + component + "    " + msg;
			log.log( Level.WARN, msg2, null);
		}
		else
			return;

	}
	
	/**This method will write the message as Fatal  message in the file*/  
	public void writeFatalMsg ( String msg )
	{
		//ganla01: set component
		String[] classes = Trace.getCallerClasses(null, 100);
		
		//ganla01: set the component value to the latest calling class details 
		component=classes[3];
		
		if( debug_val )
		{
			 String msg2 = processType + "    " + component + "    " + msg;
		  log.log( Level.FATAL, msg2, null);
		}
		else
			return;
	}

	/**This method writes the exception's stack trace to debug file*/
	public void printStackTrace( Exception e )
	{
		//ganla01: set component
		String[] classes = Trace.getCallerClasses(null, 100);
		
		//ganla01: set the component value to the latest calling class details 
		component=classes[3];
		
		if( debug_val )
		{
		  String exceptionStr = stack2string( e );
		  String msg2 = processType + "    " + component + "    " + exceptionStr;
		  log.log( Level.ERROR, msg2, null);
		}
		else
			return;
	}
	
	/**This method converts the stacktrace to a string  */
	 public static String stack2string ( Exception e ) {
		  try {
		    StringWriter sw = new StringWriter();
		    PrintWriter pw = new PrintWriter(sw);
		    e.printStackTrace(pw);
		    return "------\r\n" + sw.toString() + "------\r\n";
		    }
		  catch(Exception e2) {
		    return "bad stack2string";
		    }
		  } 
	 
	 public void writeToConsole(String msg)
	 {	
		// Set root category priority to DEBUG and set its only appender to log		 
		log = Category.getRoot();
		String pattern = "%m%n";
		org.apache.log4j.ConsoleAppender  consoleAppender = new org.apache.log4j.ConsoleAppender( (new PatternLayout(pattern)) );
		log.removeAllAppenders();
		log.addAppender( consoleAppender );
		log.log(Level.INFO, msg, null);
		if(DEBUG_FILE != null)
		{
			log.removeAllAppenders();		
			pattern = "%p    %d{dd/MM/yyyy HH:mm:ss}    %m%n";
			FileAppender fileAppender = null;
			try {
				fileAppender = new FileAppender( (new PatternLayout(pattern)),this.DEBUG_FILE , true );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			log.addAppender(fileAppender);
			log.setLevel( ( Level ) Level.INFO );
		}
		
	 }
	 
	 public void writeMessage( String message, int type )
		{
			if( type == 0 )
				writeInfoMsg( message );
			else if( type == 1 )	
				writeWarnMsg( message );
			else if( type == 2 )
				writeErrorMsg( message );			
			else if( type == 3)	
				writeFatalMsg( message );
		}

	public void writeMessage( String type, String message )
	{
		if( (type.trim()).equalsIgnoreCase("-info"))
			writeInfoMsg( message );
		else if( (type.trim()).equalsIgnoreCase("-warn"))	
			writeWarnMsg( message );
		else if( (type.trim()).equalsIgnoreCase("-error"))
			writeErrorMsg( message );		
		else if( (type.trim()).equalsIgnoreCase("-fatal"))	
			writeFatalMsg( message );
	}

    /**
     * @param args
     */
    public static void main(String args[])
    {
	   try
	   {
		   System.setProperty( "DEBUG_FILE" , "C:\\Automation\\D2DWorkSpace\\mydebug.log.txt");
		   debughandler dh = debughandler.getdebughandler( );
		   String debug_type = "";
		   String debug_message = "";		   
		   dh.writeInfoMsg("InfoMessage");
		   dh.writeDebugMsg("DebugMessage");
		   dh.writeFatalMsg("FStarting");
		   dh.writeWarnMsg("WStarting");
		   dh.writeErrorMsg("EStarting");
		   dh.writeInfoMsg("IStarting");		   
		   //System.out.println("++++++++++");
		 //  dh.writeToConsole("abcdef");
		   //System.out.println("++++++++++");
		   //dh.writeToConsole("sarada");
		  // dh.writeInfoMsg("Test message2est message2Test message2est message2Test message2est");
		  // dh.writeToConsole("santhi");
		   //dh.writeInfoMsg("Ending");
		  /**
		   *  The usage:
		   *  java debughandler -type [info|error|debug|fatal|warn] -msg <message> 
		   *  
		   */
		   		   
		 /* for( int i=0; i<args.length; i++ )
		  {
			if( args[i].equalsIgnoreCase( "-type" ) )
				debug_type = args[ i+1 ];
			else if( args[i].equalsIgnoreCase( "-msg" )  )
			   debug_message = args[ i+1 ];
			
		 }	
			dh.writeMessage( debug_type, debug_message );*/
	   }
	   catch( Exception e )
	   {
		 System.out.println ( "debughandler:main:" + e );
	   }
	}
    	
}