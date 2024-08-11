package Constants;

public class AuditCodeConstants {
	
	public static final String CREATE_USER="1";
	public static final String MODIFY_USER="2";
	public static final String DELETE_USER="3"; 
	public static final String LOGIN_USER="4"; 
	public static final String MODIFY_LOGIN_USER="5";
	public static final String MODIFY_USER_PASSWORD="6";
	public static final String MODIFY_LOGIN_USER_PASSWORD="7";
	
	public static final String CREATE_ORGANIZATION="101";
	public static final String MODIFY_ORGANIZATION="102";
	public static final String DELETE_ORGANIZATION="103";
	public static final String CREATE_SUB_ORGANIZATION="105";
	public static final String MODIFY_SUB_ORGANIZATION="106";
	public static final String DELETE_SUB_ORGANIZATION="107";
	public static final String MODIFY_LOGIN_USER_ORGANIZATION="108";
	
	public static final String create_cloud_account="301";
	public static final String update_cloud_account="302";
	public static final String delete_cloud_account="303";
	public static final String login_cloud_account="304";
	
	public static final String CREATE_SITE="401";
	public static final String MODIFY_SITE="402";
	public static final String DELETE_SITE="403";
	public static final String LOGIN_SITE="404";
	public static final String REGISTER_SITE="405";

	public static final String CREATE_SOURCE="501";
	public static final String MODIFY_SOURCE="502";
	public static final String DELETE_SOURCE="503";

	public static final String create_destination="601";
	public static final String update_destination="602";
	public static final String delete_destination="603";
	
	public static final String create_cloud_direct_destination="604";
	
	public static final String create_source_group = "701";
	public static final String modify_source_group = "702";
	public static final String delete_source_group = "703";
	
	public static final String add_source_to_group = "801";
	public static final String delete_source_from_group = "802";
	
	public static final String create_source_filter = "1001";
	public static final String modify_source_filter = "1002";
	public static final String delete_source_filter = "1003";
	
	public static final String create_destination_filter = "1004";
	public static final String modify_destination_filter = "1005";
	public static final String delete_destination_filter = "1006";
	
	public static final String CREATE_JOB_FILTER = "1007";
	public static final String MODIFY_JOB_FILTER = "1008";
	public static final String DELETE_JOB_FILTER = "1009";
	
	public static final String CREATE_LOG_FILTER = "1010";
	public static final String MODIFY_LOG_FILTER = "1011";
	public static final String DELETE_LOG_FILTER = "1012";
	
	public static final String CREATE_USER_FILTER = "1013";
	public static final String MODIFY_USER_FILTER = "1014";
	public static final String DELETE_USER_FILTER = "1015";
	
	public static final String CREATE_HYPERVISOR_FILTER = "1016";
	public static final String MODIFY_HYPERVISOR_FILTER = "1017";
	public static final String DELETE_HYPERVISOR_FILTER = "1018";
	
	public static final String CREATE_RECOVEREDRESOURCE_FILTER = "1019";
	public static final String MODIFY_RECOVEREDRESOURCE_FILTER = "1020";
	public static final String DELETE_RECOVEREDRESOURCE_FILTER = "1021";
	
	public static final String CREATE_POLICY_FILTER = "1022";
	public static final String MODIFY_POLICY_FILTER = "1023";
	public static final String DELETE_POLICY_FILTER = "1024";
	
	public static final String CREATE_REPORT_LIST_FILTER = "1025";
	public static final String MODIFY_REPORT_LIST_FILTER = "1026";
	public static final String DELETE_REPORT_LIST_FILTER = "1027";
	
	public static final String CREATE_REPORT_FILTER = "1028";
	public static final String MODIFY_REPORT_FILTER = "1029";
	public static final String DELETE_REPORT_FILTER = "1030";
	
	public static final String CREATE_USER_SOURCE_COLUMN = "2001";
	public static final String MODIFY_USER_SOURCE_COLUMN ="2002";
	public static final String DELETE_USER_SOURCE_COLUMN ="2003";
    
	public static final String CREATE_USER_DESTINATION_COLUMN="2004";
	public static final String MODIFY_USER_DESTINATION_COLUMN="2005";
	public static final String DELETE_USER_DESTINATION_COLUMN="2006";
    
	public static final String CREATE_USER_HYPERVISOR_COLUMN="2007";
	public static final String MODIFY_USER_HYPERVISOR_COLUMN="2008";
	public static final String DELETE_USER_HYPERVISOR_COLUMN="2009";
  
	public static final String CREATE_USER_COLUMN="2010";
	public static final String MODIFY_USER_COLUMN="2011";
	public static final String DELETE_USER_COLUMN="2012";

	public static final String CREATE_USER_JOB_COLUMN="2013";
	public static final String MODIFY_USER_JOB_COLUMN="2014";
	public static final String DELETE_USER_JOB_COLUMN="2015";

	public static final String CREATE_USER_LOG_COLUMN="2016";
	public static final String MODIFY_USER_LOG_COLUMN="2017";
	public static final String DELETE_USER_LOG_COLUMN="2018";
	
	public static final String CREATE_USER_RECOVEREDRESOURCE_COLUMN="2019";
	public static final String MODIFY_USER_RECOVEREDRESOURCE_COLUMN="2020";
	public static final String DELETE_USER_RECOVEREDRESOURCE_COLUMN="2021";
	
	public static final String CREATE_USER_POLICY_COLUMN="2022";
	public static final String MODIFY_USER_POLICY_COLUMN="2023";
	public static final String DELETE_USER_POLICY_COLUMN="2024";
	
	public static final String CREATE_USER_BACKUPJOBREPORT_COLUMN="2025";
	public static final String MODIFY_USER_BACKUPJOBREPORT_COLUMN="2026";
	public static final String DELETE_USER_BACKUPJOBREPORT_COLUMN="2027";
	
	public static final String CREATE_USER_RESTOREJOBREPORT_COLUMN="2028";
	public static final String MODIFY_USER_RESTOREJOBREPORT_COLUMN="2029";
	public static final String DELETE_USER_RESTOREJOBREPORT_COLUMN="2030";

	public static final String CREATE_HYPERVISOR="3001";
	public static final String MODIFY_HYPERVISOR="3002";
	public static final String DELETE_HYPERVISOR="3003";

	public static final String CREATE_LOG = "4001";
	public static final String MODIFY_LOG = "4002";
	public static final String DELETE_LOG = "4003";
	public static final String CREATE_LOG_DATA = "4004";
	public static final String MODIFY_LOG_DATA = "4005";

	public static final String CREATE_JOB="4006";
	public static final String MODIFY_JOB="4007";
	public static final String CREATE_JOB_DATA="4008";
	public static final String MODIFY_JOB_DATA="4009";

	public static final String CREATE_POLICY = "4010";
	public static final String MODIFY_POLICY = "4011";
	public static final String DELETE_POLICY="4012";
	public static final String ASSING_POLICY="4013";
	
	public static final String CREATE_REPORT_SCHEDULE="4015";
	public static final String MODIFY_REPORT_SCHEDULE="4016";
	public static final String DELETE_REPORT_SCHEDULE="4017";


	public static final String CREATE_RECOVERY_POINT_SERVER="5001";
	public static final String MODIFY_RECOVERY_POINT_SERVER="5002";
	public static final String DELETE_RECOVERY_POINT_SERVER="5003";
	public static final String CLOUD_DIRECT_RECOVER = "5004";
	
	public static final String CREATE_LINUX_BACKUP_SERVER="6001";
	public static final String MODIFY_LINUX_BACKUP_SERVER="6002";
	public static final String DELETE_LINUX_BACKUP_SERVER="6003";
	
	public static final String CREATE_DATA_STORE="7001";
	public static final String MODIFY_DATA_STORE="7002";
	public static final String DELETE_DATA_STORE="7003";
	public static final String START_DATA_STORE="7005";
	public static final String STOP_DATA_STORE="7006";
	

		
}
