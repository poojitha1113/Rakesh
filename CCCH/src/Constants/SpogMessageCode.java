package Constants;

import Constants.SpogConstants;
public enum SpogMessageCode {
	
	//For SuccessFull
	SUCCESS_GET_PUT_DEL(0x0010000,SpogConstants.SUCCESS_GET_PUT_DELETE),
	SUCCESS_POST(0x0010000,SpogConstants.SUCCESS_POST),

	//For InsufficentPermissions
	RESOURCE_PERMISSION_DENY(0x00100101,SpogConstants.ERROR_MESSAGE_PERMISSION_DENY),	
	RESOURCE_NOT_FOUND(0x00100201,SpogConstants.RESOURCE_NOT_FOUND),
	UNABLE_TO_GET_POLICY_FOR_NON_DIRECT_USER(0x00E0005E,SpogConstants.UNABLE_TO_GET_POLICY_FOR_NON_DIRECT_USER),
	CANNOT_RESUME_ORG_ITS_PARENT_ORG_SUSPENDED(0x0030004A,SpogConstants.CANNOT_RESUME_ORG_ITS_PARENT_ORG_SUSPENDED),	
	
	//ForAuthenticationFailure
	COMMON_AUTHENTICATION_FAILED(0x00900006,SpogConstants.ERROR_MESSAGE_INVALID_HEADER),
	COMMON_AUTENTICATION_FAILED_FOR_JWT(0x00900006,SpogConstants.ERROR_MESSAGE_INVALID_TOKEN),
	
	//ForOrganizationFailures
	ORGANIZATION_NOT_FOUND_OR_REMOVED(0x0030000A,SpogConstants.ORGANIZATION_NOT_FOUND_OR_REMOVED),
	REPORT_NOT_FOUND_OR_REMOVED(0x01300012,SpogConstants.REPORT_NOT_FOUND_OR_REMOVED),
	UNABLE_TO_FIND_REPORT(0x01300008,SpogConstants.UNABLE_TO_FIND_REPORT),
	
	//ForinvalidUUID
	ERROR_MESSAGE_ELEMENT_NOT_UUID(0x40000005,SpogConstants.ERROR_MESSAGE_ELEMENT_NOT_UUID),
	ERROR_MESSAGE_ELEMENT_FILTER_ID_NOT_UUID(0x40000005,SpogConstants.ERROR_MESSAGE_ELEMENT_FILTER_ID_NOT_UUID),
	FORBIDDEN_TO_VISIT_THE_RESOURCE(0x00900003,SpogConstants.FORBIDDEN_TO_VISIT_THE_RESOURCE),
	UNABLE_TO_FIND_THE_RESOURCE_WITH_ID(0x00100201,SpogConstants.UNABLE_TO_FIND_THE_RESOURCE_WITH_ID),
	UNABLE_TO_FIND_THE_RESOURCE(0x00100201,SpogConstants.UNABLE_TO_FIND_THE_RESOURCE),
	
	
	//For invalidEnum
	ELEMENT_NOT_VALID_ENUM(0x40000006,SpogConstants.ELEMENT_NOT_VALID_ENUM),
	ELEMENT_ROLE_ID_NOT_VALID_ENUM(0x40000006,SpogConstants.ELEMENT_ROLE_ID_NOT_VALID_ENUM),
	ELEMENT_STATUS_NOT_VALID_ENUM(0x40000006,SpogConstants.ELEMENT_STATUS_NOT_VALID_ENUM),
	
	//For invalid param
	INVALID_PARAMETER(0x00100001,SpogConstants.INVALID_PARAMETER),
	
	//Forinvalid email
	EMAIL_FORMAT_NOT_CORRECT(0x40000004,SpogConstants.EMAIL_FORMAT_NOT_CORRECT),
	SEARCH_STRING_MIN_THREE(0x40000019,SpogConstants.SEARCH_STRING_MIN_THREE),
		
	//For source/group insufficient permissions
	SOURCE_GROUP_NOT_EXIST(0x00500007,SpogConstants.ERROR_MESSAGE_INVALID_UUID),
	SOURCE_NAME_ALREADY_EXISTED(0x00500008,  SpogConstants.AUTH_HEADER_NAME),
	SOURCE_GROUP_NAME_LENGTH_INCORRECT(0x40000002,SpogConstants.SOURCE_GROUP_NAME_LENGTH_INCORRECT),
	SOUCRE_GROUP_NAME_CANNOT_BLANK(0x40000001,SpogConstants.SOUCRE_GROUP_NAME_CANNOT_BLANK),
	SOURCE_GROUP_NO_MAP_SOURCE(0x00500006,SpogConstants.SOURCE_GROUP_NO_MAP_SOURCE),
	SOURCE_ALREADY_MAPPED_TO_SOURCE_GROUP(0x00500005,SpogConstants.SOURCE_ALREADY_MAPPED_TO_SOURCE_GROUP),
	SOURCE_GROUP_NAME_DUPLICATE(0x00500003,SpogConstants.SOURCE_GROUP_NAME_DUPLICATE),
	SOURCE_FILTER_NOT_FOUND_WITH_USER_ID(0x00A00002,SpogConstants.SOURCE_FILTER_NOT_FOUND_WITH_USER_ID),
	
	//For destination
	DESTINATION_NOT_FOUND(0x00C00001,SpogConstants.DESTINATION_NOT_FOUND),
	ELEMENT_DESTINATIONID_IS_NOT_UUID(0x40000005,SpogConstants.ELEMENT_DESTINATIONID_IS_NOT_UUID),
	DESTINATION_FILTER_NOT_FOUND_WITH_USER_ID(0x00A00102,SpogConstants.DESTINATION_FILTER_NOT_FOUND_WITH_USER_ID),
	TIME_STAMP_CANNOT_BLANK(0x40000001,SpogConstants.TIME_STAMP_CANNOT_BLANK),
	DESTINATION_CANNOT_BLANK(0x40000001,SpogConstants.DESTINATION_CANNOT_BLANK),
	DESTINATION_CANNOT_BE_CREATED_FOR_MSP(0x00C00010,SpogConstants.DESTINATION_CANNOT_BE_CREATED_FOR_MSP),
	ELEMENT_DESTINATIONID_NOT_UUID(0x40000005,SpogConstants.ELEMENT_DESTINATIONID_NOT_UUID),
	DESTINATION_TYPE_MUST_BE_CLOUD_DIRECT(0x00C00004,SpogConstants.DESTINATION_TYPE_MUST_BE_CLOUD_DIRECT),
	DESTINATION_TYPE_MUST_BE_CLOUD_HYBRID(0x00C0000A,SpogConstants.DESTINATION_TYPE_MUST_BE_CLOUD_HYBRID),
	DESTINATION_LIMIT_IN_BETWEEN(0x40000005,SpogConstants.DESTINATION_LIMIT_IN_BETWEEN),
	REACHED_LIMIT_TO_CREATE_DESTINATION(0x00C00011,SpogConstants.REACHED_LIMIT_TO_CREATE_DESTINATION),
	ELEMENT_VALUE_LESSTHAN_MINIMUM_VALUE(0x40000010,SpogConstants.ELEMENT_VALUE_LESSTHAN_MINIMUM_VALUE),
	ONLY_CSR_PERFORM_MODIFICATIONS(0x0030003A,SpogConstants.ONLY_CSR_PERFORM_MODIFICATIONS),
	
	//For information related to audit codes
	AUDIT_RESOURCE_NOT_FOUND(0x00900001,SpogConstants.ERROR_MESSAGE_ELEMENT_NOT_FOUND),
	
	//userlogfilters
	USER_FILTER_NOT_FOUND_WITH_USER_ID(0x00A00402,SpogConstants.USER_FILTER_NOT_FOUND_WITH_USER_ID),
	NOT_FOUND_RESOURCE_WITH_ID(0x00100201,SpogConstants.NOT_FOUND_RESOURCE_WITH_ID),
	
	//datacenter
	DATACENTER_NOT_FOUND(0x01500010,SpogConstants.DATACENTER_NOT_FOUND),
	
	//For clouds
	NON_EXIST_CLOUD_ID(0x00100003,SpogConstants.NON_EXISTED),
	RESOURCE_NOT_FOUNDED(0x00900001,SpogConstants.ERROR_MESSAGE_ELEMENT_NOT_FOUND),
	INVALID_ORGANIZATION_ID(0x00100022,SpogConstants.INVALID_ORGANIZATION_ID), 
	
	//Related to Policies
	POLICY_ID_NOT_FOUND(0x00E00008,SpogConstants.NOT_FOUND_POLICY_WITH_ID),
	UNABLE_TO_FIND_POLICY_ID(0x00E00008,SpogConstants.UNABLE_TO_FIND_POLICY_ID),
	
	//job coloumns
	ELEMENT_IS_NOT_UUID(0x40000005,SpogConstants.ERROR_MESSAGE_ELEMENT_NOT_UUID),
	ELEMENT_COLUMNID_NOT_UUID(0x40000005,SpogConstants.ELEMENT_COLUMNID_NOT_UUID),
	ELEMENT_ORGANIZATIONID_IS_NOT_UUID(0x40000005,SpogConstants.ELEMENT_ORGANIZATIONID_IS_NOT_UUID),
	ELEMENT_USERID_IS_NOT_UUID(0x40000005,SpogConstants.ELEMENT_USERID_IS_NOT_UUID),
	ELEMENT_FILTERID_IS_NOT_UUID(0x40000005,SpogConstants.ELEMENT_FILTERID_IS_NOT_UUID),
	COLUMN_ID_DOESNOT_EXIST(0x00D00003,SpogConstants.COLUMN_ID_DOESNOT_EXIST),
	COLUMN_NOT_EXIST(0x00D00002,SpogConstants.COLUMN_NOT_EXIST),
	COLUMN_ID_ALREADY_EXIST(0x00D00004,SpogConstants.COLUMN_ID_ALREADY_EXIST),
	ORDER_ID_ALREADY_EXIST(0x00D00005,SpogConstants.ORDER_ID_ALREADY_EXIST),
	ORDER_ID_ATLEAST_1(0x40000016,SpogConstants.ORDER_ID_ATLEAST_1),
	ORDER_ID_LESSTHAN_MAX(0x00D00006,SpogConstants.ORDER_ID_LESSTHAN_MAX),
	ORDER_ID_SHOULD_BE_LESS_THAN_7(0x00D00006,SpogConstants.ORDER_ID_SHOULD_BE_LESS_THAN_7),
	COLUMN_EXIST(0x00D00001,SpogConstants.COLUMN_EXIST),
	COLUMN_CANNOT_BLANK(0x40000001,SpogConstants.COLUMN_CANNOT_BLANK),
	ORDER_ID_SHOULD_BE_LESS_THAN_MAX_COUNT(0x0D00006,SpogConstants.ORDER_ID_SHOULD_BE_LESS_THAN_MAX_COUNT), 
		
	//For jobs
	JOB_NOT_FOUND(0x00600003,SpogConstants.JOB_NOT_FOUND),
	JOB_NOT_FOUND_FILTER(0x00600004,SpogConstants.JOB_NOT_FOUND_FILTER),
	JOB_FILTER_NOT_FOUND_WITH_CURRENT_USER(0x00A00202,SpogConstants.JOB_FILTER_NOT_FOUND_WITH_CURRENT_USER),
	
	//For logs
	LOG_FILTER_NOT_FOUND_WITH_USER_ID(0x00A00302,SpogConstants.LOG_FILTER_NOT_FOUND_WITH_USER_ID),
	LOG_EXPORTED_NOT_FOUND(0x00700003,SpogConstants.LOG_NOT_EXPLORED),
	INVALID_LOG_ID(0x00700001,SpogConstants.LOG_RESOURCE_NOT_FOUND), 
	
	//Related to user filter
	LOGIN_USER_ORG_NOT_SAME_AS_GET_USER(0x00200017,SpogConstants.LOGIN_USER_ORG_NOT_SAME_AS_GET_USER),
	DIRECT_ADMIN_CANNOT_VIEW_MSP_CSR(0x00200018,SpogConstants.DIRECT_ADMIN_CANNOT_VIEW_MSP_CSR),
	MSP_ADMIN_CANNOT_VIEW_CSR(0x00200016,SpogConstants.MSP_ADMIN_CANNOT_VIEW_CSR),
	USER_ID_DOESNOT_EXIST(0x00200007,SpogConstants.USER_ID_DOESNOT_EXIST),
	//Related to Sites
	SITE_NOT_FOUND(0x00400201, SpogConstants.NOT_FOUND_WITH_SITE_ID),
	SITE_INVALID_REGISTRATION_KEY(0x00400003,SpogConstants.ERROR_INVALID_REGISTRATION_KEY ),

	//Policies
	NOT_FOUND_POLICY_WITH_ID(0x00E00008,SpogConstants.NOT_FOUND_POLICY_WITH_ID),
	NOT_FOUND_POLICY_WITH_SOURCE_ID(0x00E00058,SpogConstants.NOT_FOUND_POLICY_WITH_SOURCE_ID),
	ELEMENT_POLICYID_IS_NOT_UUID(0x40000005,SpogConstants.ELEMENT_POLICYID_IS_NOT_UUID),
	UNABLE_TO_DELETE_POLICY(0x00E00040,SpogConstants.UNABLE_TO_DELETE_POLICY),
	UNABLE_TO_DELETE_DISABLED_POLICY(0x00E0005F, SpogConstants.UNABLE_TO_DELETE_DISABLED_POLICY),
	CANNOT_CREATE_POLICY_SUSPENDED_ORGANIZATION(0x00E00062,SpogConstants.CANNOT_CREATE_POLICY_SUSPENDED_ORGANIZATION),
	CANNOT_DELETE_POLICY_SUSPENDED_ORGANIZATION(0x00E0005F,SpogConstants.CANNOT_DELETE_POLICY_SUSPENDED_ORGANIZATION),
	CANNOT_EDIT_POLICY_SUSPENDED_ORGANIZATION(0x00E0005B,SpogConstants.CANNOT_EDIT_POLICY_SUSPENDED_ORGANIZATION),
	//Policies-task_type:SQLServerBackup
	SQLSERVER_DEFAULT_BACKUP_MUST_BE_TRUE_LOCAL_INSTANCE_DOESNOT_HAVE_VALUE(0x00E00056,SpogConstants.SQLSERVER_DEFAULT_BACKUP_MUST_BE_TRUE_LOCAL_INSTANCE_DOESNOT_HAVE_VALUE),
	SQL_BACKUP_DEFAULT_CANNOT_BE_BLANK(0x40000001,SpogConstants.SQL_BACKUP_DEFAULT_CANNOT_BE_BLANK),
	ELEMENT_NOT_A_BOOLEAN_VALUE(0x40000012,SpogConstants.ELEMENT_NOT_A_BOOLEAN_VALUE),
	SQL_BACKUP_TYPE_IS_NOT_A_VALID_VALUE(0x40000006,SpogConstants.SQL_BACKUP_TYPE_IS_NOT_A_VALID_VALUE),
	
	//For cloud RPS
	CLOUD_RPS_SERVER_LENGTH_NOT_CORRECT(0x40000002,SpogConstants.CLOUD_RPS_SERVER_LENGTH_NOT_CORRECT),
	CLOUD_RPS_SERVER_PROTOCOL_CAN_NOT_BE_BLANK(0x40000001,SpogConstants.CLOUD_RPS_SERVER_PROTOCOL_CAN_NOT_BE_BLANK),
	RECOVERY_POINT_SERVER_WITH_ID_DOESNOT_EXIST(0X01200002,SpogConstants.RECOVERY_POINT_SERVER_WITH_ID_DOESNOT_EXIST),
	RECOVERY_POINT_SERVER_ALREADY_EXISTS(0x01200001,SpogConstants.RECOVERY_POINT_SERVER_ALREADY_EXISTS),
	ELEMENT_IS_NOT_A_VALID_INT_VALUE(0x40000009,SpogConstants.ELEMENT_IS_NOT_A_VALID_INT_VALUE),
	ELEMENT_ELEMENT_IS_NOT_A_UUID(0x40000005,SpogConstants.ELEMENT_ELEMENT_IS_NOT_A_UUID),
	NUMBER_SHOULD_BE_POSITIVE(0x00100009,SpogConstants.NUMBER_SHOULD_BE_POSITIVE),
		
	//Branding
	BRANDING_ALREADY_EXIST(0x00300024,SpogConstants.BRANDING_ALREADY_EXIST),
	ORGANIZATION_NAME_CANNOT_BLANK(0x40000001,SpogConstants.ORGANIZATION_NAME_CANNOT_BLANK),
	ORGANIZATION_ID_CANNOT_BLANK(0x40000001,SpogConstants.ORGANIZATION_ID_CANNOT_BLANK),
	SUPPORT_CHAT_URL_FORMAT_NOT_VALID(0x40000020,SpogConstants.SUPPORT_CHAT_URL_FORMAT_NOT_VALID),
	SUPPORT_SALES_URL_FORMAT_NOT_VALID(0x40000020,SpogConstants.SUPPORT_SALES_URL_FORMAT_NOT_VALID),
	FACEBOOK_LINK_URL_FORMAT_NOT_VALID(0x40000020,SpogConstants.FACEBOOK_LINK_URL_FORMAT_NOT_VALID),
	TWITTER_LINK_URL_FORMAT_NOT_VALID(0x40000020,SpogConstants.TWITTER_LINK_URL_FORMAT_NOT_VALID),
	LINKEDIN_LINK_URL_FORMAT_NOT_VALID(0x40000020,SpogConstants.LINKEDIN_LINK_URL_FORMAT_NOT_VALID),
	SOCIAL_MEDIA_PLATFORM_URL_FORMAT_NOT_VALID(0x40000020,SpogConstants.SOCIAL_MEDIA_PLATFORM_URL_FORMAT_NOT_VALID),
	LEGAL_NOTICE_URL_FORMAT_NOT_VALID(0x40000020,SpogConstants.LEGAL_NOTICE_URL_FORMAT_NOT_VALID),
	CONTACT_US_URL_FORMAT_NOT_VALID(0x40000020,SpogConstants.CONTACT_US_URL_FORMAT_NOT_VALID),
	PRIVACY_URL_FORMAT_NOT_VALID(0x40000020,SpogConstants.PRIVACY_URL_FORMAT_NOT_VALID),
	UNSUPORTED_IMAGE_TYPE(0x00100006,SpogConstants.UNSUPORTED_IMAGE_TYPE),
	BAD_REQUEST(0x00900005,SpogConstants.BAD_REQUEST),
	SUPPORT_EMAIL_LENGTH_NOT_CORRECT(0x40000002,SpogConstants.SUPPORT_EMAIL_LENGTH_NOT_CORRECT),
	SUPPORT_EMAIL_INCORRECT_FORMAT(0x40000004,SpogConstants.SUPPORT_EMAIL_INCORRECT_FORMAT),
	
	//Schedules
	NOT_FOUND_SCHEDULE_WITH_ID(0x01300009,SpogConstants.NOT_FOUND_SCHEDULE_WITH_ID),
	ELEMENT_SCHEDULEID_NOT_UUID(0x40000005,SpogConstants.ELEMENT_SCHEDULEID_NOT_UUID),
	NOT_A_VALID_CRON_EXPRESSION(0x01300011,SpogConstants.NOT_A_VALID_CRON_EXPRESSION),

	//CloudRPSDatastores
	DATASTORE_WITH_ID_DOESNOT_EXIST(0x01500002, SpogConstants.DATASTORE_WITH_ID_DOESNOT_EXIST),
	DATASTORE_IS_ALREADY_RUNNING(0x01500009,SpogConstants.DATASTORE_IS_ALREADY_RUNNING),
	DATASTORE_IS_ALREADY_STOPPED(0x0150000A,SpogConstants.DATASTORE_IS_ALREADY_STOPPED),
	ELEMENT_NOT_LONG_VALUE(0x40000007,SpogConstants.ELEMENT_NOT_LONG_VALUE),
	
	//Hypervisors
	THE_HYPERVISOR_WITH_ID_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED(0x01000003,SpogConstants.THE_HYPERVISOR_WITH_ID_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED),
	ELEMENT_HYPERVISORID_IS_NOT_A_UUID(0x40000005,SpogConstants.ELEMENT_HYPERVISORID_IS_NOT_A_UUID),
	CANNOT_GET_HYPERVISORID(0x00500022,SpogConstants.CANNOT_GET_HYPERVISORID),
	NOT_SUPPORTED_TO_GET_SOURCES_WITH_POLICY_TYPE(0x00500014,SpogConstants.NOT_SUPPORTED_TO_GET_SOURCES_WITH_POLICY_TYPE),
	HYPERVISOR_NOT_IN_ORG(0x00500023,SpogConstants.HYPERVISOR_NOT_IN_ORG),

	//CLOUD HYBRID STORES
	INCORRECT_DRIVER_LETTER_FORMAT(0x4000000B,SpogConstants.INCORRECT_DRIVER_LETTER_FORMAT),
	DRIVER_LETTER_IS_IN_USE(0x01200009,SpogConstants.DRIVER_LETTER_IS_IN_USE),
	
	
	//cloud rps datastore
	ENCRYPTION_PASSWORD_REQUIRED(0x01500003,SpogConstants.ENCRYPTION_PASSWORD_REQUIRED),
	BLOCK_SIZE_NOT_VALID(0x01500004,SpogConstants.BLOCK_SIZE_NOT_VALID),
	COMPRESSION_TYPE_REQUIRED(0x01500005,SpogConstants.COMPRESSION_TYPE_REQUIRED),
	CAN_NOT_BE_BLANK(0x40000001,SpogConstants.CAN_NOT_BE_BLANK),
	INPUT_PARAMETER_IS_EMPTY(0x00100002,SpogConstants.INPUT_PARAMETER_IS_EMPTY),
	USERNAME_NOT_PROVIDED(0x01500008,SpogConstants.USERNAME_NOT_PROVIDED),
	PASSWORD_NOT_PROVIDED(0x01500008,SpogConstants.PASSWORD_NOT_PROVIDED),
	PATH_PROVIDED_IS_NOT_VALID(0x01500006,SpogConstants.PATH_PROVIDED_IS_NOT_VALID),
	MIN_CURRENT_ACTIVE_NODES_SHOULD_BE_1(0x01500007,SpogConstants.MIN_CURRENT_ACTIVE_NODES_SHOULD_BE_1),
	DATASTORE_USED_BY_CLOUDHYBRIDSTORE_UNABLE_TO_DELETE(0x00C0000C,SpogConstants.DATASTORE_USED_BY_CLOUDHYBRIDSTORE_UNABLE_TO_DELETE),
	DATASTORE_ALREADY_ASSIGNED_TO_ANOTHER_DESTINATION(0x00C0000B,SpogConstants.DATASTORE_ALREADY_ASSIGNED_TO_ANOTHER_DESTINATION),
	DESTINATION_ALREADY_ASSIGNED_BY_OTHER_DATASTORE(0x00C0000D,SpogConstants.DESTINATION_ALREADY_ASSIGNED_BY_OTHER_DATASTORE),
	UNABLE_TO_ERASE_CLOUD_HYBRID_USING_DATASTORE(0x00C0000F,SpogConstants.UNABLE_TO_ERASE_CLOUD_HYBRID_USING_DATASTORE),
	CAPACITY_MUST_BE_GREATERTHAN_ZERO(0x0000000,SpogConstants.CAPACITY_MUST_BE_GREATERTHAN_ZERO),
	
	//ReportFilters
	REPORT_LIST_FILTER_NOT_FOUND_WITH_USER_ID(0x00A00602,SpogConstants.REPORT_LIST_FILTER_NOT_FOUND_WITH_USER_ID),
	REPORT_FILTER_NOT_FOUND_WITH_USER_ID(0x00A00702,SpogConstants.REPORT_FILTER_NOT_FOUND_WITH_USER_ID),
	
	RECOVERED_RESOURCE_FILTER_NOT_FOUND_WITH_USER_ID(0x00A00902,SpogConstants.RECOVERED_RESOURCE_FILTER_NOT_FOUND_WITH_USER_ID),
	FILTER_NOT_FOUND_WITH_USER_ID(0x00A00A02,SpogConstants.FILTER_NOT_FOUND_WITH_USER_ID),
	
	//Reports
	UNABLE_TO_FIND_REPORT_WITH_ID(0x01300008,SpogConstants.UNABLE_TO_FIND_REPORT_WITH_ID),
	
	// USER WIDGETS
	WIDGET_ALREADY_EXISTS(0x00D00007, SpogConstants.WIDGET_ALREADY_EXISTS), 
	ORDER_ID_SHOULD_BE_LESS_THAN_MAX_WIDGETS(
			0x00D00009, SpogConstants.ORDER_ID_SHOULD_BE_LESS_THAN_MAX_WIDGETS), 
	WIDGET_DOESNOT_EXISTS(0x00D00008,
					SpogConstants.WIDGET_DOESNOT_EXISTS), WIDGET_NOT_UUID(0x40000005, SpogConstants.WIDGET_NOT_UUID),

	//search_string
	SEARCH_STRING_CANNOT_BLANK(0x40000001,SpogConstants.SEARCH_STRING_CANNOT_BLANK),
	SEARCH_STRING_LEANGTH_NOT_CORRECT(0x40000002,SpogConstants.SEARCH_STRING_LEANGTH_NOT_CORRECT),
	ELEMENT_USER_ID_NOT_UUID(0x40000005,SpogConstants.ELEMENT_USER_ID_NOT_UUID),
	
	//Entitlements
	PROBLEM_WITH_ORDER_CONTACT_SUPPORT(0x0030003B,SpogConstants.PROBLEM_WITH_ORDER_CONTACT_SUPPORT),
	
	//user
	PREFERENCE_LANGUAGE_FORMAT_DOESNOT_MATCH(0x40000003, SpogConstants.PREFERENCE_LANGUAGE_FORMAT_DOESNOT_MATCH),
	
	//convertorootmsp
	ORGANIZATION_CANT_BE_CHANGED_TO_ROOT_MSP(0x00300042, SpogConstants.ORGANIZATION_CANT_BE_CHANGED_TO_ROOT_MSP),
	INVALID_PARAM_COMBINATION(0x00300002, SpogConstants.INVALID_PARAM_COMBINATION),
	EMAIL_ALREADY_EXISTS(0x00200002, SpogConstants.EMAIL_ALREADY_EXISTS),
	DATACENTER_ID_NOT_FOUND(0x00B00012, SpogConstants.DATACENTER_ID_NOT_FOUND),
	
	//UsersSeacrch
	AT_LEAST_THREE_CHARACTRES_SEARCH_STRING(0x0010000A,SpogConstants.AT_LEAST_THREE_CHARACTRES_SEARCH_STRING);
	
	;
	
	/*SITE_NOT_REGISTERED(0x00400101,),
	SITE_REGISTERED_BY_OTHER(0x00400102,),
	SITE_NOT_FOUND(0x00400201,);*/
	
	SpogMessageCode(int code,String Status){
		this.code = code;
		this.Status=Status;
	}

	SpogMessageCode(int code,int Status){
		this.code=code;
		this.Status1=Status;
	}
	
	//variables for codes,Status,ErrorMessages
	private int code, Status1;
	private String Status;
	
	public int getCode() {
		return code;
	}
	public String getStatus(){
		return Status;
	}
     
	public int getStatus1(){
		return Status1;
	}

	//Converting Hexadecimal to integer and Then to String
	public String getCodeString(){
		return String.format("%08X", code & 0xFFFFFFFF);
	}

	/*public static void main(String args[]) {		
 for(SpogMessageCode s:SpogMessageCode.values()){
	 System.out.println("The values are :s"+s.getCodeString()+"The value of the status:"+s.getStatus());
 }
}*/

}
