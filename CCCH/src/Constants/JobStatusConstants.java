package Constants;

import java.util.HashMap;

public class JobStatusConstants {
	public static HashMap<String, String> jobstatus = new HashMap<String, String>() {
		{
			put("Active", "active");
			put("Finished", "finished");
			put("Canceled", "canceled");
			put("Failed", "failed");
			put("Incomplete", "incomplete");
			put("Waiting", "waiting");
			put("Crash", "crash");
			put("LicenseFailed", "license failed");
			put("Skipped", "skipped");
			put("Stop", "stop");
			put("Missed", "missed");
			//Yuntao confirms "backupjob_proc_exit" equals "finished" in backend;
			put("BackupJob_PROC_EXIT", "finished");
		}
	};
}
