package genericutil;

public class testcasescount {
	private int passedcount=0;
	private int failedcount=0;
	private int skippedcount=0;
	private long startTime;
	private int hit;
	public void setpassedcount() {
		passedcount++;
		
	}
	
	public void setfailedcount() {
		failedcount++;
	}
	
	public void setskippedcount() {
		skippedcount++;
	}
	
	public int getpassedcount() {
		return passedcount;
	}
	
	public int getskippedcount() {
		return skippedcount;
	}
	
	public int getfailedcount() {
		return failedcount;
	}

	
	public void setcreationtime(long creationtime) {
		
		startTime=creationtime;
		System.out.println("Into test case count, the creation time is "+startTime);
		hit=1;
	}
	
	public long getcreationtime() {
		return startTime;
	}
	
	public int isstarttimehit() {
		return hit;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
