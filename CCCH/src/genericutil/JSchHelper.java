package genericutil;

import genericutil.ErrorHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class JSchHelper {
	
	private String USERNAME = "";  //For example: root
	private String PASSWORD = "";  //For example: caworld1!
	private String HOST = "";  //For example: huaha02-rh67x64
	private int PORT = 22;  //The port should always be 22
	
	private ErrorHandler errorHandlerObject = ErrorHandler.getErrorHandler();;
	
	public JSchHelper()
	{
		
	}
	
	public JSchHelper(String userName, String password, String host)
	{
		USERNAME = userName;
		errorHandlerObject.printInfoMessageInDebugFile("Set user name as: " + userName);
		
		PASSWORD = password;
		errorHandlerObject.printInfoMessageInDebugFile("Set password as: " + password);
		
		HOST = host;
		errorHandlerObject.printInfoMessageInDebugFile("Set host as: " + host);
	}
	
	public void setUsername(String userName)
	{
		USERNAME = userName;
		errorHandlerObject.printInfoMessageInDebugFile("Set user name as: " + userName);
	}
	
	public void setPassword(String password)
	{
		PASSWORD = password;
		errorHandlerObject.printInfoMessageInDebugFile("Set password as: " + password);
	}
	
	public void setHost(String host)
	{
		HOST = host;
		errorHandlerObject.printInfoMessageInDebugFile("Set host as: " + host);
	}
	
	public String executeCommand(String command)
	{
        /**
        * Create a new Jsch object
        * This object will execute shell commands or scripts on server
        */
		String allResultStr = "";
		JSch jSch = new JSch();
		ChannelExec channelExec = new ChannelExec();
		Session session = null;
		try {
	         /*
	         * Open a new session, with your username, host and port
	         * Set the password and call connect.
	         * session.connect() opens a new connection to remote SSH server.
	         * Once the connection is established, you can initiate a new channel.
	         * this channel is needed to connect to remotely execution program
	         */
			errorHandlerObject.printInfoMessageInDebugFile("About to initialize JSch session.");
			session = jSch.getSession(USERNAME, HOST, PORT);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(PASSWORD);
			session.setTimeout(3*60*1000);
			
			int retry = 1;
			while(retry <= 3)
			{
				try
				{
					errorHandlerObject.printInfoMessageInDebugFile("This is the " + retry + " time try to connect.");
					session.connect();
					break;
				} catch (Exception e)
				{
					errorHandlerObject.printInfoMessageInDebugFile("Got Exception here: " + e.toString());
					try {
						Thread.sleep(5*60*1000);
					} catch (InterruptedException e1) {
						errorHandlerObject.printInfoMessageInDebugFile(e1.toString());
					}
					retry += 1;
				}
			}
			errorHandlerObject.printInfoMessageInDebugFile("JSch session initialized.");
			
			//Create the execution channel over the session
			channelExec = (ChannelExec)session.openChannel("exec");
			
			//Gets an InputStream for this channel. All data arriving in as messages from the remote side can be read from this stream.
			InputStream in = channelExec.getInputStream();
			
	        //Set the command that you want to execute
	        // In our case its the remote shell script
			//For example: "touch /tmp/ThisIsJSchTest.txt"
	        channelExec.setCommand(command);
	         
	        //Execute the command
	        errorHandlerObject.printInfoMessageInDebugFile("About to execute the command: " + command);
	        channelExec.connect();
	        errorHandlerObject.printInfoMessageInDebugFile("Command executed.");
	         
	        //Read the output from the input stream we set above
	        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	        String line;
	         
	        //Read each line from the buffered reader and add it to result list
	        while ( (line = reader.readLine()) != null)
	        {
	        	allResultStr += line;
	        	errorHandlerObject.printInfoMessageInDebugFile("The result line from remote server is:" + line);
	        }
	         
	        //retrieve the exit status of the remote command corresponding to this channel
	        int exitStatus = channelExec.getExitStatus();
	         
	        errorHandlerObject.printInfoMessageInDebugFile("Exist status code is: " + exitStatus);
	         
//	        if(exitStatus < 0){
//	        	errorHandlerObject.printInfoMessageInDebugFile("Done, but exit status not set.");
//	        	return "Fail";
//	        } else if(exitStatus > 0)
//	        {
//	        	errorHandlerObject.printInfoMessageInDebugFile("Done, but with error.");
//	        	return "Fail";
//	        } else
//	        {
//	        	errorHandlerObject.printInfoMessageInDebugFile("Done, sucessfully!");
//	        	return "Sucess";
//	        }
		} catch (JSchException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	         //Safely disconnect channel and disconnect session. If not done then it may cause resource leak
	        if(channelExec != null) {
	        	channelExec.disconnect();
	        }
	        if(session != null) {
	        	session.disconnect();
	        }
		}
		
		if(allResultStr.trim().equalsIgnoreCase(""))
		{
			return "No output.";
		} else
		{
			return allResultStr;
		}
		
	}
	
	public static void main(String[] args) {
		JSchHelper jsch = new JSchHelper("root", "caworld1!", "huaha02-rh67x64");
//		while(true)
//		{
			String uploadDriverCmd = 
//					"curl.exe " + 
//					"-o C:\\uploadResult.txt " +
//					"-F \"release=v65u2\" -F \"package=@"
//					+ "C:\\Users\\Administrator\\Neon-workspace\\UbuntuDriverMonitor\\curl\\bin\\udp_agent_for_linux_upgrade.sig\""
//					+ " http://wanzh02-bld/cgi-bin/publish-to-aws.py";
					"curl -F \"release=v65u2\" -F \"package=@/udp_agent_for_linux_upgrade.sig\" http://wanzh02-bld/cgi-bin/publish-to-aws.py";
			System.out.println(uploadDriverCmd);
			String re = jsch.executeCommand(uploadDriverCmd);
			System.out.println(re);
//		}

	}

}
