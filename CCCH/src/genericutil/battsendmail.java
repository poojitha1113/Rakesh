package genericutil;

import java.io.*;
import java.text.SimpleDateFormat;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import javax.mail.util.*;

import java.util.*;

public class battsendmail {

	public static void printUsage() {
		System.err.println("\n Usage: \n\n" + "\tjava battsendmail \n " + "\t\t -config <path to battsendmail.ini>\n");
	}

	public static void printToLog(String errorstr) {

		System.err.println(errorstr);

		try {

			if (new File("BATTBQ.log").exists() == false) {
				new File("BATTBQ.log").createNewFile();
			}
			BufferedWriter error = new BufferedWriter(new FileWriter("BATTBQ.log", true));

			// print timestamp
			Calendar now = Calendar.getInstance();
			String timestamp = Integer.toString(new Integer(now.get(Calendar.MONTH)).intValue() + 1) + "-"
					+ now.get(Calendar.DAY_OF_MONTH) + "-" + now.get(Calendar.YEAR) + " "
					+ now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND)
					+ "    ";

			error.write(timestamp + errorstr);
			error.newLine();
			error.close();

		} catch (IOException e) {
			ErrorHandler.getErrorHandler().printStackTraceInDebugFile(e);
			// printToLog("Unable to write to error log");
		}

	}

	public static void printToLog(String errorstr, boolean printToScreen) {

		if (printToScreen) {
			System.err.println(errorstr);
		}

		try {

			BufferedWriter error = new BufferedWriter(new FileWriter("BATTBQ.log", true));

			// print timestamp
			Calendar now = Calendar.getInstance();
			String timestamp = Integer.toString(new Integer(now.get(Calendar.MONTH)).intValue() + 1) + "-"
					+ now.get(Calendar.DAY_OF_MONTH) + "-" + now.get(Calendar.YEAR) + " "
					+ now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND)
					+ "    ";

			error.write(timestamp + errorstr);
			error.newLine();
			error.close();

		} catch (IOException e) {
			printToLog("Unable to write to error log");
		}

	}

	public static MimeBodyPart addAttachment(String path, String name) {

		MimeBodyPart attachment = new MimeBodyPart();

		File checkexist = new File(path);
		if (!checkexist.exists())
			return null;

		FileDataSource fds = new FileDataSource(new File(path));
		try {
			attachment.setDataHandler(new DataHandler(fds));
			attachment.setFileName(name);
		} catch (Exception e) {
			printToLog("Unable to find file used as attachment.");
		}
		return attachment;

	}

	public static MimeBodyPart addEmailBody(String path) {

		MimeBodyPart bodymsg = new MimeBodyPart();
		StringBuffer sb = new StringBuffer();

		BufferedReader in = null;

		try {
			in = new BufferedReader(new FileReader(new File(path)));

			String readline = in.readLine();
			while (readline != null) {
				sb.append(readline);
				readline = in.readLine();
			}

			bodymsg.setDataHandler(new DataHandler(new ByteArrayDataSource(sb.toString(), "text/html")));

			in.close();
		} catch (Exception e) {
			printToLog("Unable to find file for body of email.");
		}

		return bodymsg;

	}

	public static int getNumAttach(String config_path) {

		File config = new File(config_path);
		int numprods = 0;

		if (!config.exists()) {
			// System.err.println("Configuration file not found.");
			return 0;
		}

		if (config.isDirectory()) {
			System.err.println("Path given is a directory.  Please specify a file name.");
			return 0;
		}

		try {
			BufferedReader in = new BufferedReader(new FileReader(config_path));
			String variable = null;
			String value = null;
			String currentline = in.readLine();

			while (currentline != null) {
				variable = currentline.split("\\s*:\\s*", 2)[0];
				value = currentline.split("\\s*:\\s*", 2)[1];

				if (variable != null && value != null) {
					if (variable.equalsIgnoreCase("Attachment")) {
						numprods = numprods + 1;
					}
				}

				currentline = in.readLine();
			}
			in.close();

		} catch (IOException e) {
			printToLog("Problem parsing the BQ Configuration file.  Please check the file.");
			printToLog(e.toString(), false);
			return 0;
		}

		return numprods;
	}

	public static MimeBodyPart addAllAttachments(String config_path, int i) {

		Hashtable config_hash = new Hashtable();
		Hashtable count_prods = new Hashtable();
		count_prods.clear();

		File config = new File(config_path);

		try {
			BufferedReader in = new BufferedReader(new FileReader(config_path));
			String variable = null;
			String value = null;
			String currentline = in.readLine();

			while (currentline != null) {
				variable = currentline.split("\\s*:\\s*", 2)[0];
				value = currentline.split("\\s*:\\s*", 2)[1];

				if (count_prods.containsKey(variable)) {
					String oldval = count_prods.get(variable).toString();
					int newval = Integer.valueOf(oldval) + 1;
					count_prods.put(variable, newval);
				} else {
					count_prods.put(variable, 0);
				}

				if (Integer.valueOf(count_prods.get(variable).toString()) == i) {
					config_hash.put(variable, value);
				}

				currentline = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			printToLog("Problem parsing the BQ Configuration file.  Please check the file.");
			printToLog(e.toString(), false);
		}

		return battsendmail.addAttachment(config_hash.get("Attachment").toString(), config_hash.get("Name").toString());
	}

	private static BodyPart getEmailAttachments(String attachmentPath) {

		BodyPart attachmentBodyPart = new MimeBodyPart();
		FileDataSource fds = null;
		File attFile = new File(attachmentPath);
		if (!attFile.exists()) {
			return null;
		}
		fds = new FileDataSource(attFile);
		try {

			attachmentBodyPart.setDataHandler(new DataHandler(fds));
			// Set attacment file name
			attachmentBodyPart.setFileName(attFile.getName());

		} catch (MessagingException e) {

			printToLog(e.getMessage().toString());
		}

		return attachmentBodyPart;

	}

	private static MimeBodyPart getEmailBody(String mailBodyHTMLFilePath) {

		MimeBodyPart msgBody = new MimeBodyPart();
		StringBuffer bodyMsgBuffer = new StringBuffer();
		BufferedReader in = null;
		File bodyFile = new File(mailBodyHTMLFilePath);
		if (!bodyFile.exists()) {
			return null;
		}

		try {
			msgBody.setDataHandler(new DataHandler(new FileDataSource(bodyFile)));
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * try{
		 * 
		 * in = new BufferedReader(new FileReader(bodyFile)); String strLine = in
		 * .readLine();
		 * 
		 * while(strLine != null) { if (strLine.equalsIgnoreCase("end")) {
		 * bodyMsgBuffer.append(System.lineSeparator());
		 * bodyMsgBuffer.append(System.lineSeparator());
		 * 
		 * }else { bodyMsgBuffer.append(strLine);
		 * bodyMsgBuffer.append(System.lineSeparator()); } strLine = in.readLine(); }
		 * msgBody.setDataHandler(new DataHandler(new FileDataSource(bodyFile)));
		 * in.close();
		 * 
		 * } catch(Exception e){
		 * 
		 * printToLog(e.getMessage().toString()); }
		 */

		return msgBody;

	}

	private static MimeBodyPart getEmailBodyText(String bodyText) {

		MimeBodyPart msgBody = new MimeBodyPart();
		StringBuffer bodyMsgBuffer = new StringBuffer();
		BufferedReader in = null;

		if (bodyText != null && !bodyText.isEmpty()) {

			try {

				String content[] = bodyText.split(";");
				for (String line : content) {
					bodyMsgBuffer.append(System.getProperty("line.separator"));
					bodyMsgBuffer.append(line);
				}
				msgBody.setDataHandler(new DataHandler(new ByteArrayDataSource(bodyMsgBuffer.toString(), "text/html")));

			} catch (Exception e) {
				// TODO: handle exception
			}

			return msgBody;
		}

		return null;

	}

	public static boolean send(String attachment, String smtpHost, String smtpPortstr, String from, String password,
			String to, String subject, String bodypath) {

		int smtpPort = new Integer(smtpPortstr).intValue();

		String mailer = "sendhtml";

		try {
			// Create a mail session
			java.util.Properties props = new java.util.Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", smtpHost);
			props.put("mail.smtp.port", "" + smtpPort);
			props.put("mail.smtp.timeout", "300000");
			props.put("mail.smtp.connectiontimeout", "300000");
			final String username = from;
			final String from_password = password;

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, from_password);
				}
			});
			// Session session = Session.getDefaultInstance(props, null);

			// Construct the message
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from));

			String[] addresses = to.split(";");
			InternetAddress[] iaddresses = new InternetAddress[addresses.length];
			for (int i = 0; i < addresses.length; i++)
				iaddresses[i] = new InternetAddress(addresses[i]);
			msg.setRecipients(Message.RecipientType.TO, iaddresses);
			msg.setSubject(subject);
			//// printToLog(subject);

			// Add body
			Multipart mp = new MimeMultipart();
			if (!StringUtils.isEmptyOrNull(attachment)) {
				mp.addBodyPart(battsendmail.getEmailAttachments(attachment));
				System.out.println();
			}

			String[] paths = bodypath.split(";");
			for (String path : paths) {
				mp.addBodyPart(addEmailBody(path));
			}

			if(subject.contains("ClearOrg"))
			{
			SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
			String date = dateFormater.format(new Date());
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart = new MimeBodyPart();
			String file = "E:\\Orgdata\\" + date + ".txt";
			String fileName = date + ".txt";
			DataSource source = new FileDataSource(file);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(fileName);
			mp.addBodyPart(messageBodyPart);
			}

			msg.setContent(mp);

			// Send the message
			try {
				Transport.send(msg);
			} catch (SendFailedException e) {

				printToLog("Invalid email address found", false);

				Address[] invalidaddress = e.getInvalidAddresses();

				for (int i = 0; i < invalidaddress.length; i++)
					printToLog(invalidaddress[i].toString(), false);

				String[] addresses2 = to.split(";");
				InternetAddress[] iaddresses2 = new InternetAddress[addresses2.length - invalidaddress.length];
				int k = 0;
				for (int i = 0; i < addresses2.length; i++) {
					boolean found = false;
					for (int j = 0; j < invalidaddress.length; j++) {
						if (invalidaddress[j].toString().equalsIgnoreCase(addresses2[i].toString())) {
							found = true;
						}
					}
					if (!found)
						iaddresses2[k++] = new InternetAddress(addresses2[i]);
				}

				msg.setRecipients(Message.RecipientType.TO, iaddresses2);

				msg.setContent(mp);
				try {
					Transport.send(msg);
				} catch (Exception e1) {
					return false;
				}
			}
			return true;
		}

		catch (Exception e) {
			printToLog(e.toString(), false);
			System.out.println(e.getMessage());
			return false;
		}
	}

	public static boolean sendMail(String config_path) {

		Hashtable config_hash = new Hashtable();

		System.out.println("config_path: " + config_path);
		File config = new File(config_path);

		if (!config.exists()) {
			printToLog("SendMail Configuration file not found.");
		}

		if (config.isDirectory()) {
			printToLog("Path given is a directory.  Please specify a file name.");
		}

		try {
			BufferedReader in = new BufferedReader(new FileReader(config_path));
			String variable = null;
			String value = null;
			String currentline = in.readLine();

			while (currentline != null) {
				variable = currentline.split("\\s*:\\s*", 2)[0];
				value = currentline.split("\\s*:\\s*", 2)[1];

				if (variable != null && value != null) {
					config_hash.put(variable, value);

				}

				currentline = in.readLine();
			}
			in.close();

		} catch (IOException e) {
			printToLog("Problem parsing the Configuration file.  Please check the file.");
			printToLog(e.toString(), false);
		}

		try {
			String subscription_link = config_hash.get("Subscription-Link").toString();
			String subscription_name = null;
			try {
				subscription_name = config_hash.get("Subscription-Name").toString();
			} catch (NullPointerException e) {
			}
			battsendmail.insertLink(subscription_link, subscription_name, config_hash.get("Body").toString());
		} catch (NullPointerException e) {
		}

		return battsendmail.send(config_path, config_hash.get("SMTPHost").toString(),
				config_hash.get("Port").toString(), config_hash.get("From").toString(),
				config_hash.get("Password").toString(), config_hash.get("To").toString(),
				config_hash.get("Subject").toString(), config_hash.get("Body").toString());
	}

	public static int ftpBuild(String path, Hashtable config_hash) {

		// FTP build exe to local machine
		// create file with commands

		String ftp_file = "FTP.tmp";
		File ftp_file2 = new File(ftp_file);

		// Check if destination folder exists. If not, create it.

		File dest_folder = new File("Mail");
		String destfile = path.split("\\\\")[path.split("\\\\").length - 1];

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(ftp_file)));
			out.println("open " + config_hash.get("Source-Hostname"));
			out.println("user " + config_hash.get("Source-Username") + " " + config_hash.get("Source-Password"));
			out.println("lcd Mail");
			out.println("get " + path + " " + destfile);
			out.println("quit");
			out.close();
		} catch (IOException e) {
			printToLog("Unable to write to temporary file.");

			return 0;
		}

		String ftp_cmd = "ftp -n -s:" + ftp_file;

		try {
			printToLog("\nCopying attachment to local machine ... ");

			Process pr = Runtime.getRuntime().exec(ftp_cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String currentline = in.readLine();
			while (currentline != null) {
				// printToLog(currentline, false);
				currentline = in.readLine();
			}
			pr.waitFor();
		} catch (Exception e) {
			printToLog("Unable to run command " + ftp_cmd);

			return 0;
		}

		ftp_file2.delete();

		File check_dest = new File("Mail/" + destfile);
		int timeout2 = 0;

		while (!check_dest.exists() && timeout2 < 300) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
			timeout2 = timeout2 + 1;
		}
		if (!check_dest.exists()) {
			printToLog("File was not copied to local machine.  Check space requirements.");

			return 0;
		} else {
			printToLog("Attachment Copy Successful.");
		}

		return 1;

	}

	public static void updateConfig(String path, String mail_config) {

		try {

			BufferedWriter mail = new BufferedWriter(new FileWriter(new File(mail_config), true));
			String destfile = path.split("\\\\")[path.split("\\\\").length - 1];

			mail.newLine();
			mail.write("Attachment:Mail/" + destfile);
			mail.newLine();
			mail.write("Name:" + destfile);
			mail.close();

		} catch (IOException e) {
			printToLog("Unable to write to mail configuration file");
		}
	}

	public static boolean checkConfig(String path, String mail_config) {
		boolean found = false;

		try {
			BufferedReader in = new BufferedReader(new FileReader(mail_config));
			String variable = null;
			String value = null;
			String currentline = in.readLine();

			while (currentline != null && found == false) {
				variable = currentline.split("\\s*:\\s*", 2)[0];
				value = currentline.split("\\s*:\\s*", 2)[1];

				if (variable != null && value != null) {
					if (variable.equalsIgnoreCase("Attachment")) {
						if (value.equalsIgnoreCase("Mail/" + path.split("\\\\")[path.split("\\\\").length - 1]))
							found = true;
					}
				}

				currentline = in.readLine();
			}
			in.close();

		} catch (IOException e) {
			printToLog("Problem parsing the BQ Configuration file.  Please check the file.");
			printToLog(e.toString(), false);
			return false;
		}
		return found;
	}

	public static void getAttachments(String mail_config, String bq_config) {

		Hashtable bq_config_hash = new Hashtable();
		ArrayList attachments = new ArrayList();

		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(bq_config)));
			String variable = null;
			String value = null;
			String currentline = in.readLine();

			while (currentline != null) {
				variable = currentline.split("\\s*:\\s*", 2)[0];
				value = currentline.split("\\s*:\\s*", 2)[1];

				if (variable != null && value != null) {

					bq_config_hash.put(variable, value);

					// Check if this is Source-Attach. If so, add to list
					if (variable.equalsIgnoreCase("Source-Attach"))
						attachments.add(value);

				}

				currentline = in.readLine();
			}
			in.close();

		} catch (IOException e) {
			printToLog("Problem parsing the Configuration file.  Please check the file.");
			printToLog(e.toString(), false);
		}

		Iterator listattach = attachments.iterator();
		while (listattach.hasNext()) {

			String currentpath = (String) listattach.next();
			// Download to Mail/battsendmail folder
			printToLog(currentpath);
			battsendmail.ftpBuild(currentpath, bq_config_hash);

			// Update battsendmail.ini
			if (battsendmail.getNumAttach(mail_config) > 1) {
				if (!battsendmail.checkConfig(currentpath, mail_config))
					battsendmail.updateConfig(currentpath, mail_config);
			} else
				battsendmail.updateConfig(currentpath, mail_config);
		}

	}

	public static void deleteAttachments(String mail_config) {

		Hashtable bq_config_hash = new Hashtable();

		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(mail_config)));
			String variable = null;
			String value = null;
			String currentline = in.readLine();

			while (currentline != null) {
				System.out.println("currentline: " + currentline);
				variable = currentline.split("\\s*:\\s*", 2)[0];
				value = currentline.split("\\s*:\\s*", 2)[1];

				if (variable != null && value != null) {

					if (bq_config_hash.get(variable) == null)
						bq_config_hash.put(variable, value);
				}

				currentline = in.readLine();
			}
			in.close();

			PrintWriter out = new PrintWriter(new FileWriter(new File(mail_config), false));
			out.println("SMTPHost:" + bq_config_hash.get("SMTPHost").toString());
			out.println("Port:" + bq_config_hash.get("Port").toString());
			out.println("From:" + bq_config_hash.get("From").toString());
			out.println("To:" + bq_config_hash.get("To").toString());
			out.println("Subject:" + bq_config_hash.get("Subject").toString());
			if (bq_config_hash.get("Subscription-Link") != null) {
				out.println("Subscription-Link:" + bq_config_hash.get("Subscription-Link").toString());
			}
			if (bq_config_hash.get("Subscription-Name") != null) {
				out.println("Subscription-Name:" + bq_config_hash.get("Subscription-Name").toString());
			}
			out.println("Body:" + bq_config_hash.get("Body").toString());
			if (bq_config_hash.get("Attachment") != null) {
				out.println("Attachment:" + bq_config_hash.get("Attachment").toString());
				out.print("Name:" + bq_config_hash.get("Name").toString());
			}
			out.close();
		} catch (IOException e) {
			printToLog("Problem parsing the Configuration file.  Please check the file.");
			printToLog(e.toString(), false);
		}

	}

	public static void insertLink(String link, String name, String emailBody) {
		try {
			File tempemail = new File(emailBody + ".tmp");
			File originalemail = new File(emailBody);
			originalemail.renameTo(tempemail);

			BufferedReader in = new BufferedReader(new FileReader(tempemail));
			BufferedWriter out = new BufferedWriter(new FileWriter(originalemail, false));

			String currentline = in.readLine();

			while (currentline != null) {
				if (currentline.matches(".*<\\/BODY>.*") && !currentline.matches(".*To change your qualification.*")) {
					String replacestrpre = "<FONT SIZE=\"2\">";
					String replacestrname = "";
					if (name != null) {
						replacestrname = "You are subscribed to &quot;" + name + "&quot;.<BR/>";
					}
					String replacestr = replacestrpre + replacestrname
							+ "To change your qualification subscription options, click <a href=\"" + link
							+ "\">here</a>.</FONT></BODY>";

					out.write(currentline.replaceFirst("<\\/BODY>", replacestr));
					out.newLine();
				} else {
					out.write(currentline);
					out.newLine();
				}
				currentline = in.readLine();
			}

			in.close();
			out.close();
			tempemail.delete();
		} catch (IOException e) {
			printToLog("Unable to read email body mail to insert link");
		}
	}

	public static void main(String[] args) throws Exception {

		// if (args.length < 2) {
		// battsendmail.printUsage();
		// return;
		// }
		//
		// battsendmail.deleteAttachments(args[1]);
		//
		//
		// if (args.length > 2)
		// battsendmail.getAttachments(args[1], args[2]);
		//
		// battsendmail.sendMail(args[1]);
		//
		// File waitformail = new File("Mail/wait");
		// if (waitformail.exists())
		// waitformail.delete();
		//
		// battsendmail.send("Reports\\DetailLog.html", "mail.ca.com", "25",
		// "StorageAutoQA@ca.com", "Team-ARCserve-RPSDedupe@ca.com", "SAQA [ARCserve
		// RPS] Web Services BQ ACCEPTED Version: r17.0.11.0[ Windows Server 2008 R2 -
		// 6.1 - amd64 ]", "Reports\\attachment.html");
		// battsendmail.send("Reports\\DetailLog.html", "mail.ca.com", "25",
		// "StorageAutoQA@ca.com", "Team-ARCserve-RPSDedupe@ca.com", "SAQA [ARCserve
		// RPS] Web Services BQ for GDDSeekFile set-3 REJECTED Version: r17.0.29.0[
		// Windows Server 2008 R2 - 6.1 - amd64 ]", "Reports\\attachment.html");
		battsendmail.send("Reports\\DetailLog.html", "outlook.office365.com", "587",
				"Automation@arcservemail.onmicrosoft.com", "Boxa6033", "Jing.Shan@Arcserve.com",
				"[ EDGE ] [ r5.0.1897.4.1069 ] [ ACCEPTED ] [ CPM Advance Schedule UI BQ ]", "Reports\\attachment.htm");

	}
}
