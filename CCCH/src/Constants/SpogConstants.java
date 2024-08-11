package Constants;

public class SpogConstants {
	//Login response json key
		public static final String ACCESS_TOKEN = "token";
		//JWt token payload prefix.
		public static final String HEADER_PREFIX = "Bearer ";
	    //Jwt token header key
		public static final String AUTH_HEADER_NAME = "Authorization";
		//Jwt Claim Key.
		public static final String USER_ID = "userId";
		//Jwt Claim Key.
		public static final String ROLE_ID = "roleId";
		//Jwt Claim Key.
		public static final String EMAIL = "email";

		// Jwt Claim key.
		public static final String ORGANIZATIONID = "organizationId";
		
		//status code
		public static final int SUCCESS_LOGIN = 200;
		public static final int SUCCESS_POST = 201;
		public static final int SUCCESS_GET_PUT_DELETE = 200;
		public static final int REQUIRED_INFO_NOT_EXIST = 400;
		public static final int NOT_LOGGED_IN = 401;
		public static final int INSUFFICIENT_PERMISSIONS = 403;
		public static final int NO_CONTENT = 204;
		public static final int RESOURCE_NOT_EXIST = 404;
		public static final int NOT_ALLOWED_ON_RESOURCE = 405;
		public static final int INTERNAL_SERVER_ERROR = 500;
		public static final int REQUEST_CONFLICT = 409;
		
		//csr org id
		public static final String CSR_ORG_ID = "11111111-2222-3333-1111-222222222222";
		
		//roles
		public static final String DIRECT_ADMIN = "direct_admin";
		public static final String MSP_ADMIN ="msp_admin";
		public static final String CSR_ADMIN = "csr_admin"; 
		public static final String MSP_ACCOUNT_ADMIN= "msp_account_admin";
		public static final String CSR_READ_ONLY_ADMIN= "csr_read_only";
		public static final String DIRECT_MONITOR = "direct_monitor";
		public static final String MSP_MONITOR ="msp_monitor";

		//organization type
		public static final String CSR_ORG = "csr";
		public static final String DIRECT_ORG = "direct";
		public static final String MSP_ORG ="msp";
		public static final String MSP_SUB_ORG ="msp_child";
		
		public static final int MAX_PAGE_SIZE=1000000000;
		
		public static final String ERROR_MESSAGE_ELEMENT_BLANK = "The element is blank.";
		public static final String ERROR_MESSAGE_ELEMENT_LENTH = "The element length is not correct.";
		public static final String ERROR_MESSAGE_ELEMENT_PATTERN = "The element is not match pattern.";
		public static final String ERROR_MESSAGE_EMAIL_FORMAT = "Email format is not correct.";
		public static final String ERROR_MESSAGE_INVALID_TOKEN = "Invalid JWT token:"; 
		public static final String ERROR_MESSAGE_INVALID_HEADER = "Invalid authorization header.";
		public static final String ERROR_MESSAGE_HEADER_BLANK = "Authorization header cannot be blank!";
		public static final String ERROR_MESSAGE_PERMISSION_DENY_CREATE_USER = "Permission deny: create a user with organization id";
		public static final String ERROR_MESSAGE_ELEMENT_NOT_UUID =  "The element id is not a UUID.";
		public static final String ELEMENT_ROLE_ID_NOT_VALID_ENUM="The element role_id is not a valid value.";
		public static final String ELEMENT_STATUS_NOT_VALID_ENUM="The element status is not a valid value."; 
		public static final String ELEMENT_COLUMNID_NOT_UUID =  "The element id is not a UUID.";
		public static final String ELEMENT_ORGANIZATIONID_IS_NOT_UUID =  "The element id is not a UUID.";
		public static final String ELEMENT_USERID_IS_NOT_UUID =  "The element userId is not a UUID.";
		public static final String ELEMENT_FILTERID_IS_NOT_UUID =  "The element filterId is not a UUID.";
		public static final String ERROR_MESSAGE_PERMISSION_DENY="The current user does not have permissions to manage the resource.";
		public static final String CANNOT_RESUME_ORG_ITS_PARENT_ORG_SUSPENDED ="Can not resume this organization, because its parent organization is suspended.";
        public static final String NON_EXISTED="The UUID [null] is invalid.";
		public static final String CODE="Not found resource with id [{0}]";
		public static final String ORGANIZATION_NOT_FOUND_OR_REMOVED="The organization [{0}] is not found or has been removed.";
		public static final String ELEMENT_NOT_VALID_ENUM="The element {0} is not a valid value.";
		public static final String INVALID_PARAMETER="Invalid input parameter.";
		public static final String UNABLE_TO_FIND_REPORT="Unable to find report with ID [{0}].";
		public static final String REPORT_NOT_FOUND_OR_REMOVED="Invalid report with ID [{0}], report type is not match.";
		public static final String RECOVERED_RESOURCE_FILTER_NOT_FOUND_WITH_USER_ID="The recovered resource filter [{0}] is not found for the user [{1}].";
		
		
		//Related to sites:
		public static final String ERROR_INVALID_REGISTRATION_KEY="The site registration key [{0}] is invalid";
		public static final String NOT_FOUND_WITH_SITE_ID = "Unable to find site with ID [{0}].";

		public static final String RESOURCE_NOT_FOUND="Not found resource with id [{0}].";
		public static final String LOG_RESOURCE_NOT_FOUND="Unable to find log with ID [{0}]."; 
		public static final String UNABLE_TO_GET_POLICY_FOR_NON_DIRECT_USER="Unable to get policy using none direct admin user. ";
		
		//Related to jobs
		public static final String JOB_NOT_FOUND="Unable to find job with ID [{0}]";
		public static final String JOB_NOT_FOUND_FILTER="Not found job(s) with the query filters";
		
		//Related to logs
		public static final String LOG_FILTER_NOT_FOUND_WITH_USER_ID="The log filter [{0}] is not found for the user [{1}].";
		public static final String LOG_NOT_EXPLORED ="Not found log can be exported.";
		
		public static final String INVALID_ORGANIZATION_ID="not found with organization id";
		public static final String ERROR_MESSAGE_INVALID_UUID="The source group does not exist";
		public static final String ERROR_MESSAGE_ELEMENT_NOT_FOUND="The resource you visit doesn't found"; 
		
		public static final String ERROR_MESSAGE_ELEMENT_FILTER_ID_NOT_UUID = "The element filterId is not a UUID.";
		
		//Sourcegroups
		public static final String SOURCE_GROUP_NO_MAP_SOURCE = "Mapping of source with id [{0}] to group with id [{1}] does not exist.";
		public static final String SOURCE_ALREADY_MAPPED_TO_SOURCE_GROUP = "Source with id [{0}] is already mapped to group with id [{1}].";
		public static final String SOURCE_GROUP_NAME_DUPLICATE="The source group name is duplication.";
		public static final String SOURCE_GROUP_NAME_LENGTH_INCORRECT="The length of name is incorrect.";
		public static final String SOUCRE_GROUP_NAME_CANNOT_BLANK="The name cannot be blank.";
		
		//Destinaions
		public static final String DESTINATION_NOT_FOUND="Not found destination with id [{0}]";
		public static final String ELEMENT_DESTINATIONID_IS_NOT_UUID =  "The element destination_id is not a UUID.";
		public static final String DESTINATION_FILTER_NOT_FOUND_WITH_USER_ID="The destination filter [{0}] is not found for the user [{1}].";
		public static final String TIME_STAMP_CANNOT_BLANK="The timestamp can not be blank.";
		public static final String DESTINATION_CANNOT_BLANK="The destination_id can not be blank.";
		public static final String DESTINATION_CANNOT_BE_CREATED_FOR_MSP = "Destination cannot be created for an MSP organization.";
		public static final String ELEMENT_DESTINATIONID_NOT_UUID = /*"The element destination_id is not a UUID."*/ "The element id is not a UUID.";
		public static final String DESTINATION_TYPE_MUST_BE_CLOUD_DIRECT = "Destination type must be cloud_direct_volume.";
		public static final String DESTINATION_TYPE_MUST_BE_CLOUD_HYBRID = "Destination type must be cloud_hybrid_store.";
		public static final String REACHED_LIMIT_TO_CREATE_DESTINATION = "You have reached the number of Cloud Direct destinations allowed. Please contact Support to request more";
		public static final String DESTINATION_LIMIT_IN_BETWEEN="must be between 1 and 9223372036854775807";
		public static final String ELEMENT_VALUE_LESSTHAN_MINIMUM_VALUE="The value of element is less then the minimum value.";
		public static final String ONLY_CSR_PERFORM_MODIFICATIONS="Only CSR can perform modifications.";
		
		//userfilters
		public static final String LOGIN_USER_ORG_NOT_SAME_AS_GET_USER="Login user organization is not same as get user.";
		public static final String DIRECT_ADMIN_CANNOT_VIEW_MSP_CSR="Direct admin can not view csr or msp details.";
		public static final String MSP_ADMIN_CANNOT_VIEW_CSR="Msp can not view csr details.";
		public static final String USER_ID_DOESNOT_EXIST = "Either the user does not exist or the status is not active. ";
		public static final String JOB_FILTER_NOT_FOUND_WITH_CURRENT_USER="job filter [{0}] is not found for the user [{1}].";
		public static final String USER_FILTER_NOT_FOUND_WITH_USER_ID="The user filter [{0}] is not found for the user [{1}].";
		public static final String SEARCH_STRING_MIN_THREE = "The minimum length of search_string is 3";
		public static final String NOT_FOUND_RESOURCE_WITH_ID="Not found resource with id [{0}]";
		
		//filter
		public static final String PROTETION_STATUS = "protect,unprotect";
		public static final String CONNECTION_STATUS = "online,offline";
		public static final String BACKUP_STATUS = "active,finished";
		public static final String OPERATING_SYSTEM = "windows";
		public static final String APPLICATIONS = "SQL_SERVER,EXCHANGE,All";
		public static final String SOURCE_FILTER_NOT_FOUND_WITH_USER_ID = "The source filter [{0}] is not found for the user [{1}].";
		
		//columns errors
		public static final String COLUMN_ID_DOESNOT_EXIST = "This column ID[{0}] does not exist.";
		public static final String COLUMN_NOT_EXIST =  "This user column does not exist.";
		public static final String COLUMN_ID_ALREADY_EXIST = "Column ID[{0}] already existed.";
		public static final String ORDER_ID_ALREADY_EXIST = "Order ID[{0}] already existed. ";
		public static final String ORDER_ID_ATLEAST_1 = "The minimum length of order_id is 1.";
		public static final String ORDER_ID_LESSTHAN_MAX = "Order ID should be less than [{0}].";
		public static final String COLUMN_EXIST = "This user column is already existed.";
		public static final String COLUMN_CANNOT_BLANK="The column_id can not be blank.";
		public static final String ORDER_ID_SHOULD_BE_LESS_THAN_7="Order ID should be less than [7].";
		public static final String ORDER_ID_SHOULD_BE_LESS_THAN_MAX_COUNT = "Order ID should be less than [{0}].";
		public static final String EMAIL_FORMAT_NOT_CORRECT = "Email format is not correct.";
		
		//datacenters
		public static final String DATACENTER_NOT_FOUND = "The data center ID [{0}] is not found";
		
		//policies
		public static final String NOT_FOUND_POLICY_WITH_ID = "Not found policy with id [{0}].";
		public static final String NOT_FOUND_POLICY_WITH_SOURCE_ID="Unable to find policy with source id [{0}].";
		public static final String ELEMENT_POLICYID_IS_NOT_UUID = "The element id is not a UUID.";
		public static final String UNABLE_TO_FIND_POLICY_ID = "Unable to find policy with id [{0}].";
		public static final String UNABLE_TO_DELETE_POLICY = "Unable to delete policy with type cloud_direct_hypervisor.";
		public static final String UNABLE_TO_DELETE_DISABLED_POLICY = "Unable to delete a policy in the disabled status. ";
		public static final String CANNOT_CREATE_POLICY_SUSPENDED_ORGANIZATION="Can not create policy due to organization suspended.";
		public static final String CANNOT_DELETE_POLICY_SUSPENDED_ORGANIZATION="Unable to delete a policy in the disabled status. ";
		public static final String CANNOT_EDIT_POLICY_SUSPENDED_ORGANIZATION="Unable to modify the disabled policy.";
		
		//Policies-task_type:SQLServerBackup
		public static final String SQLSERVER_DEFAULT_BACKUP_MUST_BE_TRUE_LOCAL_INSTANCE_DOESNOT_HAVE_VALUE = "SQL Server default backup must be true since local instances does not have value";
		public static final String SQL_BACKUP_DEFAULT_CANNOT_BE_BLANK = "The sql_backup_default cannot be blank.";
		public static final String ELEMENT_NOT_A_BOOLEAN_VALUE = "The element is not a valid boolean value.";
		public static final String SQL_BACKUP_TYPE_IS_NOT_A_VALID_VALUE = "The element sql_backup_type is not a valid value.";
		
		//hypervisors
		public static final String THE_HYPERVISOR_WITH_ID_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED = "The hypervisor with id[{0}] does not exist or has been deleted.";
		public static final String INVALID_UUID_STRING = "Invalid UUID string: {0}";
		public static final String ELEMENT_HYPERVISORID_IS_NOT_A_UUID = "The element hypervisorId is not a UUID.";		
		public static final String CANNOT_GET_HYPERVISORID = "Can not get hypervisor id.";
		public static final String NOT_SUPPORTED_TO_GET_SOURCES_WITH_POLICY_TYPE = "This API is not supported to get sources with the policy type [cloud_hybrid_replication]. ";
		public static final String HYPERVISOR_NOT_IN_ORG = "hypervisor  [{0}] is not in the organization  [{1}].";
		
		//recovery point servers
		public static final String CLOUD_RPS_SERVER_LENGTH_NOT_CORRECT = "The length of {0} is not correct.";
		public static final String CLOUD_RPS_SERVER_PROTOCOL_CAN_NOT_BE_BLANK = "The server_protocol can not be blank.";
		public static final String RECOVERY_POINT_SERVER_WITH_ID_DOESNOT_EXIST = "Recovery Point Server with id [{0}] does not exist.";
		public static final String RECOVERY_POINT_SERVER_ALREADY_EXISTS = "Recovery Point Server with name [{0}] already exists";
		public static final String ADMIN_PRIVILEGE_REQUIRED = "Administrator privilege is required";
		public static final String ELEMENT_IS_NOT_A_VALID_INT_VALUE = "The element is not a valid int value.";
		public static final String ELEMENT_ELEMENT_IS_NOT_A_UUID = "The element {0} is not a UUID.";
		public static final String NUMBER_SHOULD_BE_POSITIVE = "The number should be positive.";
		
		//cloud hybrid stores
		public static final String INCORRECT_DRIVER_LETTER_FORMAT = "The driver letter format is not correct. Only 1 capital letter allowed.";
		public static final String DRIVER_LETTER_IS_IN_USE = "Unable to create RPS volume on Recovery Point Server [{0}] VDS code: [8004255c] The driver letter [C] is in use.";
		
		//Datastores
		public static final String DATASTORE_WITH_ID_DOESNOT_EXIST = "Data Store with id [{0}] does not exist.";
		public static final String CAN_NOT_BE_BLANK = "The {0} can not be blank.";
		public static final String INPUT_PARAMETER_IS_EMPTY = "The input parameter is empty.";
		public static final String USERNAME_NOT_PROVIDED = "Username not provided for [{0}].";
		public static final String PASSWORD_NOT_PROVIDED = "Password not provided for [{0}].";
		public static final String PATH_PROVIDED_IS_NOT_VALID = "Path Provided for [{0}] is not valid.";
		public static final String MIN_CURRENT_ACTIVE_NODES_SHOULD_BE_1 = "Min. Concurrent Active Nodes should be 1.";
		public static final String DATASTORE_IS_ALREADY_RUNNING = "Data Store is already running.";
		public static final String DATASTORE_IS_ALREADY_STOPPED = "Data Store is already stopped.";
		public static final String DATASTORE_USED_BY_CLOUDHYBRIDSTORE_UNABLE_TO_DELETE = "Data Store [{0}] in use by cloud_hybrid_store[{1}], unable to delete.";
		public static final String ELEMENT_NOT_LONG_VALUE = "The element is not a long value.";
		public static final String DATASTORE_ALREADY_ASSIGNED_TO_ANOTHER_DESTINATION = "The Data Store is already assigned to other destination [{0}]. ";
		public static final String DESTINATION_ALREADY_ASSIGNED_BY_OTHER_DATASTORE = "The destination [{0}] is already assigned by other Data Store [{1}].";
		public static final String UNABLE_TO_ERASE_CLOUD_HYBRID_USING_DATASTORE = "Unable to erase data completely. Cloud Hybrid Store [{0}] is using the data store [{1}].";
		public static final String CAPACITY_MUST_BE_GREATERTHAN_ZERO = "The capacity must greater than zero.";
		
		//ReportFilters
		public static final String REPORT_LIST_FILTER_NOT_FOUND_WITH_USER_ID="The report filter [{0}] is not found for the user [{1}].";
		public static final String REPORT_FILTER_NOT_FOUND_WITH_USER_ID="The report filter [{0}] is not found for the user [{1}].";
		
		//Reports
		public static final String UNABLE_TO_FIND_REPORT_WITH_ID = "Unable to find report with ID [{0}].";
		
		//Branding
		public static final String BRANDING_ALREADY_EXIST="Branding already exist for this organization";
		public static final String ORGANIZATION_NAME_CANNOT_BLANK="The organization_name can not be blank.";
		public static final String ORGANIZATION_ID_CANNOT_BLANK="The organization_id can not be blank.";

		public static final String SUPPORT_CHAT_URL_FORMAT_NOT_VALID="The support_chat URL format is not valid";
        public static final String SUPPORT_SALES_URL_FORMAT_NOT_VALID="The support_sales URL format is not valid";
        public static final String FACEBOOK_LINK_URL_FORMAT_NOT_VALID="The facebook_link URL format is not valid";
        public static final String TWITTER_LINK_URL_FORMAT_NOT_VALID="The twitter_link URL format is not valid";
        public static final String LINKEDIN_LINK_URL_FORMAT_NOT_VALID="The linkdin_link URL format is not valid";
        public static final String SOCIAL_MEDIA_PLATFORM_URL_FORMAT_NOT_VALID="The social_media_platform URL format is not valid";
        public static final String LEGAL_NOTICE_URL_FORMAT_NOT_VALID="The legal_notice URL format is not valid";
        public static final String CONTACT_US_URL_FORMAT_NOT_VALID="The contact_us URL format is not valid";
        public static final String PRIVACY_URL_FORMAT_NOT_VALID="The privacy URL format is not valid";
        public static final String UNSUPORTED_IMAGE_TYPE="Unsupported image type. Only support [[gif, png, jpg]].";
        public static final String BAD_REQUEST="This is a bad request.";
    	public static final String FORBIDDEN_TO_VISIT_THE_RESOURCE="Forbidden to visit the resource.";
    	public static final String UNABLE_TO_FIND_THE_RESOURCE_WITH_ID="Unable to find resource with ID [{0}].";
    	public static final String UNABLE_TO_FIND_THE_RESOURCE="Unable to find resource with ID [{0}].";
    	public static final String SUPPORT_EMAIL_LENGTH_NOT_CORRECT="supportEmail: The length of '' is incorrect.";
    	public static final String SUPPORT_EMAIL_INCORRECT_FORMAT="supportEmail: Incorrect Email format.";
    	
		//Seacrh_string 
		public static final String SEARCH_STRING_CANNOT_BLANK="The search_string can not be blank.";
		public static final String SEARCH_STRING_LEANGTH_NOT_CORRECT="The search_string length is not correct.";
		public static final String ELEMENT_USER_ID_NOT_UUID =  "The element userId is not a UUID.";
		public static final String FORBIDEEDN_TO_VISIT_RESOURCE="Forbidden to visit the resource.";

		//Dedupe datastore
		public static final String ENCRYPTION_PASSWORD_REQUIRED="Encryption Password is required.";
		public static final String BLOCK_SIZE_NOT_VALID = "Block Size mentioned is not valid.";
		public static final String COMPRESSION_TYPE_REQUIRED = "Compression type is required.";
		
		//Schedules
		public static final String NOT_FOUND_SCHEDULE_WITH_ID = "Not found schedule with id [{0}].";
		public static final String ELEMENT_SCHEDULEID_NOT_UUID = "The element id is not a UUID.";
		public static final String NOT_A_VALID_CRON_EXPRESSION = "Not a valid cron expression.";	

		// widgets
		public static final String WIDGET_ALREADY_EXISTS = "The user dashboard widget already exists.";
		public static final String WIDGET_NOT_UUID = "The element widget_id is not a UUID.";
		public static final String WIDGET_DOESNOT_EXISTS = "Dashboard widget ID [{0}] does not exist.";
		public static final String ORDER_ID_SHOULD_BE_LESS_THAN_MAX_WIDGETS = "Entry for Order ID should be less than [{0}], number of existing columns.";
		
		//Entitlements
		public static final String PROBLEM_WITH_ORDER_CONTACT_SUPPORT = "There was a problem with your order. Please contact Support.";

		//user
		public static final String PREFERENCE_LANGUAGE_FORMAT_DOESNOT_MATCH = "The format of preferenceLanguage does not match the pattern.";
		
		//converttorootmsp
		public static final String ORGANIZATION_CANT_BE_CHANGED_TO_ROOT_MSP = "The organization [{0}] cannot be changed to a root MSP.";
		public static final String INVALID_PARAM_COMBINATION = "Invalid parameter combination on creating account Either MSP is removed or target is not an MSP.";
		public static final String EMAIL_ALREADY_EXISTS = "The email [{0}] already exists.";
		public static final String DATACENTER_ID_NOT_FOUND = "The data center ID [{0}] is not found.";
		
		//Post users search
		public static final String AT_LEAST_THREE_CHARACTRES_SEARCH_STRING = "At least 3 characters for search string.";
	
		//filter
		public static final String FILTER_NOT_FOUND_WITH_USER_ID="The filter [{0}] is not found for the user [{1}].";
		
		//suspend
		public static final String SUSPEND_ORGANIZATION="suspend";
		
}
