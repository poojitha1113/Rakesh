package Constants;

import com.fasterxml.jackson.databind.ser.std.StaticListSerializerBase;

public class ErrorCode {


	public static final String ELEMENT_BLANK ="40000001";
	public static final String NOT_FIT_LENGTH =  "40000002";
	public static final String PASSWORD_PATTERN = "40000003";
	public static final String EMAIL_FORMAT = "40000004";
	public static final String ELEMENT_NOT_UUID = "40000005";
	public static final String NOT_VALID_ENUM = "40000006";
	public static final String INVALID_ORDER_ID = "40000016";
	public static final String INVALID_PHONE_NUMBER = "4000000A";

	public static final String RESOURCE_PERMISSION ="00100101";
	public static final String NOT_FOUND_RESOURCE_ID ="00100201";
	public static final String FILTER_ELEMENT_IS_INVALID ="00100004";
	public static final String RESOURCE_NOT_FOUND_OR_REMOVED ="0030000A";
	public static final String ORGANIZATION_ID_INVALID = "00100022";
	public static final String SPECIFY_ORG_IN_PARAMETER = "00100024";
	public static final String SOURCE_FILTER_NOT_FOUND ="00A00002";

	
	public static final String UNAUTHORIZED = "00200001";
	public static final String EMAIL_EXISTS = "00200002";
	public static final String ROLE_ID_INVALID = "00200003";
	public static final String CREATE_USER_INVALID_ROLE= "00200004";
	public static final String ORG_ID_INVALID = "00200005";
	public static final String CREATE_USER_INVALID_ORG = "00200006";
	public static final String USER_NOT_EXIST_OR_INACTIVE ="00200007";
	public static final String MSP_CANNOT_VIEW_CSR ="00200016";
	public static final String LOGGIN_USER_ORG_IS_DIFF="00200017";
	public static final String DIRECT_CANNOT_VIEW_CSR ="00200018";
	public static final String USER_NOT_EXISTS ="00200022";
	public static final String PASSWORD_INCORRECT ="00200024";
	public static final String MSP_CAN_NOT_OPERATE ="00100025";
	public static final String USER_IS_BLOCKED = "00200026";
	public static final String USER_IS_UNVERIFIED = "00200027";
	public static final String USER_IS_NOT_MSP_ACCOUNT_ADMIN = "00200029";
	public static final String MSP_ACCOUNT_ADMIN_NOT_ALLOWED = "00200030";
	public static final String MSP_ACCOUNT_ADMIN_NOT_ALLOWED_GET_OTHER_MSP_ACCOUNTS = "00200031";
	public static final String MSP_ACCOUNT_ID_REQUIRED = "00200033";
	public static final String MULTIPLE_OCCURENCE_ORG_ID = "00200034";
	public static final String CSR_READ_ONLY_PERMISSION_DENY = "00200037";
	
	public static final String INVALID_CREATING_ACCOUNT ="00300002";
	public static final String NO_PERMISSION_DEL_ITSELF ="00300001";
	public static final String NOT_ALLOWED_TO_CREATE_CSR ="0030000C";
	public static final String CAN_NOT_FIND_ORG = "0030000A"; 
	public static final String NOT_VALID_MSP_ACCOUNT = "0030000E";
	public static final String MSP_AA_CAN_NOT_DEL_ORG = "00300016"; 
	public static final String MSP_AA_EXISITS = "00300012";
	public static final String MSP_AA_NOT_BELONG_TO_MSP_ORG = "00300017";
	public static final String ORG_IS_BLCOKED = "00300019";
	public static final String ENROLLMENT_IS_NOT_ALLOWED_ORG_TYPE = "00300028";
	public static final String ORG_NOT_FOUND_SITE = "00300030";
	
	public static final String NOT_FOUND_SITE_ID = "00400201";
	public static final String NOT_VALID_SITE_OF_ORG = "00400103";
	
	public static final String NOT_FOUND_SOURCE_ID ="00500002";
	public static final String SOURCE_NAME_EXIST = "00500008";
	public static final String SOURCE_ID_EXIST = "00500009";

	public static final String INVALID_TIME_RANGE = "00700004";
	public static final String SOURCE_ID_IS_NOT_MATCHED = "00700006";
	
	public static final String RESOURCE_NOT_FOUND ="00900001";
	public static final String NO_ALLOWED_GET_RESOURCE ="00900002";
	public static final String AUTHORIZATION_HEADER_BLANK ="00900006";
	public static final String FORBIDDEN_TO_VISIT_RESOURCE ="00900003";
	
	public static final String FILTERNAME_IS_EXISTED ="00A00001";
	public static final String DATACENTER_IS_NOT_FOUND = "00B00012";
	
	public static final String INVALID_RETENTION_ID="00C00002";
	public static final String INVALID_PARAMETERS="00100001";
	public static final String PARAMETER_EMPTY="00100002";
	public static final String UUID_NULL_EMPTY="00100007";
	
	public static final String COLUMN_EXIST = "00D00001";
	public static final String COLUMN_NOT_EXIST = "00D00003";
	public static final String ORDER_ID_INVALID = "00D00006";
}
