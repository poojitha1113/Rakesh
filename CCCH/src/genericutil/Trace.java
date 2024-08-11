package genericutil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.logging.*;

import org.apache.log4j.Level;

/**
 *  Trace Information
 *
 *  @author     Jorg Janke
 *  @version    $Id: Trace.java,v 1.1 2017/11/28 06:07:32 shaji02 Exp $
 */
public class Trace
{
	/**
	 * Get Caller Array
	 *
	 * @param caller Optional Thowable/Exception
	 * @param maxNestLevel maximum call nesting level - 0 is all
	 * @return Array of class.method(file:line)
	 */
	public static String[] getCallerClasses (Throwable caller, int maxNestLevel)
	{
		int nestLevel = maxNestLevel;
		if (nestLevel < 1)
			nestLevel = 99;
		//
		ArrayList<String> list = new ArrayList<String>();
		Throwable t = caller;
		if (t == null)
			t = new Throwable();

		StackTraceElement[] elements = t.getStackTrace();
		/*for(StackTraceElement ele : elements)
			System.out.println("clas in trace: " + ele);*/
		
		
		for (int i = 0; i < elements.length && list.size() <= maxNestLevel; i++)
		{
			//ganla01: needed for debug
			/*File file = new File("out.txt");
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			PrintStream ps = new PrintStream(fos);
			System.setOut(ps);
			System.out.println("Ele: " + elements[i]);*/
			
		    //ganla01: get the caller class name, methodname and the lineNumber 
			String className = elements[i].getClassName();
			String methodName = elements[i].getMethodName();
			int lineNumber = elements[i].getLineNumber();
			
			/*System.out.println("class: " + className);
			System.out.println("method: " + methodName);
			System.out.println("line: " + lineNumber);*/
			
			String details = className + ">>" + methodName + ":" + String.valueOf(lineNumber);
			
			if (!(className.startsWith("org.compiere.util.Trace")
				|| className.startsWith("java.lang.Throwable")))
				list.add(details);
		}

		String[] retValue = new String[list.size()];
		list.toArray(retValue);
		return retValue;
	}   //  getCallerClasses

	/**
	 *  Get Caller with nest Level
	 *  @param nestLevel Nesting Level - 0=calling method, 1=previous, ..
	 *  @return class name and line info of nesting level or "" if not exist
	 */
	public static String getCallerClass (int nestLevel)
	{
		String[] array = getCallerClasses (null, nestLevel);
		if (array.length < nestLevel)
			return "";
		return array[nestLevel];
	}   //  getCallerClass

	/**
	 * 	Is the caller Called From the class mentioned
	 *	@param className calling class
	 *	@return the caller was called from className
	 */
	public static boolean isCalledFrom (String className)
	{
		if (className == null || className.length() == 0)
			return false;
		return getCallerClass(1).indexOf(className) != -1;
	}	//	isCalledFrom

	/**
	 *  Print Stack Tace Info (raw) compiereOnly - first9only
	 */
	public static void printStack()
	{
		printStack (false, true);
	}	//	printStack
	
	/**
	 *  Print Stack Tace Info (raw)
	 */
	public static void printStack (boolean compiereOnly, boolean first9only)
	{
		Throwable t = new Throwable();
	    t.printStackTrace();
		int counter = 0;
		StackTraceElement[] elements = t.getStackTrace();
		for (int i = 1; i < elements.length; i++)
		{
			if (elements[i].getClassName().indexOf("util.Trace") != -1)
				continue;
			if (!compiereOnly
				|| (compiereOnly && elements[i].getClassName().startsWith("org.compiere"))
				)
			{
				Logger.global.fine(i + ": " + elements[i]);
				if (first9only && ++counter > 8)
					break;
			}
		}	
	}   //  printStack
	
}   //  Trace
