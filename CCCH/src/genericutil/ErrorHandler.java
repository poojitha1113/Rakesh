package genericutil;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;


public class ErrorHandler {
	//static handler
	private static Queue queue;
	private static ErrorHandler errorHandle;
	static debughandler dh; 
	
	public ErrorHandler()
	{	
		dh = debughandler.getdebughandler();
		queue = new LinkedList();
	}
	public static ErrorHandler getErrorHandler()
	{
	   if( errorHandle == null )
		   errorHandle = new ErrorHandler( ); 
	   
	  return errorHandle; 
	}
	public static void initializeErrorHandler()
	{
		System.out.println("setting errorHandler to null");
		errorHandle = null; 
	}
	
	/**
	 * This method pushs the given error message into stack and also writes the error message into debug file
	 * @param errMsg :error message
	 * @param errType : error type can be either 0(info) or 1(warn) or 2(error) or 3(debug) or 4(fatal). 
	 */
	public void addMsgToQueue(String errMsg)
	{
		queue.add(errMsg + "\n");
		//dh.writeMessage(errMsg,errType);
	}
	/**
	 * ErrorType 0->Info
	 * 			 1->Warn
	 * 			 2->Error
	 * 			 3->Fatal
	 * @param errMsg
	 * @param errType
	 */
	public void printMessageInDebugFile(String errMsg, int errType)
	{
		dh.writeMessage(errMsg,errType);
	}
    public void printInfoMessageInDebugFile(String msg)
    {
        dh.writeInfoMsg(msg);
    }
    public void printInfoMessageInDebugFile( File file, String msg )
    {
        dh.writeInfoMsg( file, msg );
    }
	public void printDebugMessageInDebugFile(String msg)
	{
		dh.writeDebugMsg(msg);
	}
	public void printWarnMessageInDebugFile(String msg)
	{
		dh.writeWarnMsg(msg);
	}
	public void printErrorMessageInDebugFile(String msg)
	{
		dh.writeErrorMsg(msg);
	}
	public void printFatalMessageInDebugFile(String msg)
	{
		dh.writeFatalMsg(msg);
	}
	public void addStackTraceToQueue(Exception e)
	{
		String stackMessages = debughandler.stack2string(e);
		queue.add(stackMessages);
	}
	
	/**This method writes the exception's stack trace to debug file*/
	public void printStackTraceInDebugFile( Exception e )
	{
		dh.printStackTrace(e);
	}
	
	public void printMessageToConsole(String errMsg)
	{
		dh.writeToConsole(errMsg);
	}
	
	/**
	 * This method writes the errormessages in the stack to debugfile and also 
	 * returns all the errormessages as a string which can be used to print in the report. 
	 *
	 */
	public String getMessagesInQueue()
	{
		StringBuffer buff = new StringBuffer();
		while(!queue.isEmpty())
		{
			String errMsg = (String)queue.remove();
			buff.append(errMsg);
		}
		return buff.toString();
	}
	
	public void printQueueInDebugFile()
	{
		String errorMessages = getMessagesInQueue();
		dh.writeErrorMsg(errorMessages);
	}
	
	public void logExceptionMessage(Exception e, String errorMsg)
	{
		errorHandle.addMsgToQueue(errorMsg);
		//errorHandle.addMsgToQueue(e.getMessage());
		errorHandle.printStackTraceInDebugFile(e);
	}
	
	public void logExceptionMessage(Exception e)
	{
		errorHandle.addMsgToQueue(e.getMessage());
		errorHandle.printStackTraceInDebugFile(e);		
	}
	

}
