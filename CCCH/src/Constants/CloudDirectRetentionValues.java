package Constants;

public enum CloudDirectRetentionValues {
	DAYS7("0","0","7","0","0","0"),
	DAYS14("0","0","14","0","0","0"),
	MONTHS1("0","0","31","0","0","0"),
	MONTHS2("0","0","31","0","2","0"),
	MONTHS3("0","0","31","0","3","0"),
	MONTHS6("0","0","31","0","6","0"),
	YEARS1("0","0","31","0","12","0"),
	YEARS2("0","0","31","0","12","2"),
	YEARS3("0","0","31","0","12","3"),
	YEARS7("0","0","31","0","12","7"),
	YEARS10("0","0","31","0","12","10"),
	forever("0","0","31","0","12","-1"),
	DAYS7HF("0","42","7","0","0","0"),
	DAYS14HF("0","42","14","0","0","0"),
	MONTHS1HF("0","42","31","0","0","0");
	
	private String hours;
	private String four_hours;
	private String days;
	private String weeks;
	private String months;
	private String years;

	CloudDirectRetentionValues(String hours,String four_hours,String days, String weeks, String months, String years){
		this.hours = hours;
		this.four_hours=four_hours;
		this.days = days;
		this.weeks = weeks;
		this.months = months;
		this.years = years;
	}
	
	public String gethours(){
		return hours;
	}
	
	public String getmax4hours(){
		return four_hours;
	}
	
	public String getdays(){
		return days;
	}
	
	public String getweeks(){
		return weeks;
	}
	
	public String getmonths(){
		return months;
	}
	
	public String getyears(){
		return years;
	}
}
