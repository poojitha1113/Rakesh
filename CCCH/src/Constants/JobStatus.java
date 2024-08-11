package Constants;

public enum JobStatus {
	all(-1),
	active(0),
	finished(1),
	canceled(2),
	failed(3),
	incomplete(4),// means finish
	idle(5),// no use
	waiting(6),
	crash(7),
	license_failed(9),
	backupjob_proc_exit(10),
	skipped(11),
	stop(12),
	missed(10000)
	;
	
	private int value;
	
	private JobStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}

	public static JobStatus parse(String jobStatus) {
		if (jobStatus != null){
			for (JobStatus status : JobStatus.values()){
				if (jobStatus.equalsIgnoreCase(status.name()))
					return status;
			}
		}
		return null;
	}

}
