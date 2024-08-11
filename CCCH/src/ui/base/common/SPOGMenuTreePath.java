package ui.base.common;

public interface SPOGMenuTreePath {

	public String SPOG_USERNAME = "/SPOG/UserName";
	public String SPOG_PASSWORD = "/SPOG/UserPassword";
	public String SPOG_LOGINBTN = "/SPOG/LoginBtn";

	public String SPOG_LOGIN_ERROR_MSG="/SPOG/LoginErrorMsg";
	public String SPOG_USERLOGOUTWRAPPER = "/SPOG/UserLogoutWrapper";
	public String SPOG_USERLOGOUTWRAPPER_LOGOUT="/SPOG/UserLogoutWrapper/Logout";

	// Monitor
	public String SPOG_MONITOR = "/SPOG/Monitor";
	public String SPOG_MONITOR_SOURCESUMMARY = "/SPOG/Monitor/SourceSummary";
	public String SPOG_MONITOR_SOURCESUMMARY_SOURCECOUNT = "/SPOG/Monitor/SourceSummary/SourceCount";
	public String SPOG_MONITOR_SOURCESUMMARY_PROTECTED = "/SPOG/Monitor/SourceSummary/Protected";
	public String SPOG_MONITOR_SOURCESUMMARY_OFFLINE = "/SPOG/Monitor/SourceSummary/Offline";
	public String SPOG_MONITOR_SOURCESUMMARY_NOTPROTECTED = "/SPOG/Monitor/SourceSummary/NotProtected";

	public String SPOG_MONITOR_USAGESUMMARY = "/SPOG/Monitor/UsageSummary";
	public String SPOG_MONITOR_USAGESUMMARY_CLOUD_DIRECT = "/SPOG/Monitor/UsageSummary/CloudDirect";
	public String SPOG_MONITOR_USAGESUMMARY_CLOUD_HYBRID = "/SPOG/Monitor/UsageSummary/CloudHybrid";
	public String SPOG_MONITOR_USAGESUMMARY_CAPACITY = "/SPOG/Monitor/UsageSummary/Capacity";
	public String SPOG_MONITOR_USAGESUMMARY_USAGE = "/SPOG/Monitor/UsageSummary/Usage";

	public String SPOG_MONITOR_POLICYSUMMARY = "/SPOG/Monitor/PolicySummary";
	public String SPOG_MONITOR_POLICYSUMMARY_POLICYCOUNT = "/SPOG/Monitor/PolicySummary/PolicyCount";
	public String SPOG_MONITOR_POLICYSUMMARY_SUCCESS = "/SPOG/Monitor/PolicySummary/Success";
	public String SPOG_MONITOR_POLICYSUMMARY_DEPLYOING = "/SPOG/Monitor/PolicySummary/Deploying";
	public String SPOG_MONITOR_POLICYSUMMARY_FAILURE = "/SPOG/Monitor/PolicySummary/Failure";

	public String SPOG_MONITOR_CUSTOMERSUMMARY = "/SPOG/Monitor/CustomerSummary";
	public String SPOG_MONITOR_CUSTOMERSUMMARY_TOTALCUSTOMERS = "/SPOG/Monitor/CustomerSummary/totalCustomers";
	public String SPOG_MONITOR_CUSTOMERSUMMARY_CUSTOMERS_COUNT = "/SPOG/Monitor/CustomerSummary/customersCount";
	public String SPOG_MONITOR_CUSTOMERSUMMARY_FAILED = "/SPOG/Monitor/CustomerSummary/Failed";
	public String SPOG_MONITOR_CUSTOMERSUMMARY_SUCCESS = "/SPOG/Monitor/CustomerSummary/Success";

	public String SPOG_MONITOR_USAGE_SUMMARY_ACROSS_CUSTOMERS = "/SPOG/Monitor/UsageSummaryAcrossCustomers";
	public String SPOG_MONITOR_SOURCE_SUMMARY_ACROSS_CUSTOMERS = "/SPOG/Monitor/SourceSummaryAcrossCustomers";
	public String SPOG_MONITOR_SOURCE_SUMMARY_ACROSS_CUSTOMERS_TOTAL_SOURCES = "/SPOG/Monitor/SourceSummaryAcrossCustomers/totalSources";
	public String SPOG_MONITOR_SOURCE_SUMMARY_ACROSS_CUSTOMERS_TOTAL_SOURCES_COUNT = "/SPOG/Monitor/SourceSummaryAcrossCustomers/totalSourcesCount";

	public String SPOG_MONITOR_WIDGETS_BACKUPJOBSUMMARY = "/SPOG/Monitor/Widgets/BackupJobSummary";
	public String SPOG_MONITOR_WIDGETS_BACKUPJOBSUMMARY_LAST24HOURS = "/SPOG/Monitor/Widgets/BackupJobSummary/Last24Hours";
	public String SPOG_MONITOR_WIDGETS_RECENT10JOBS = "/SPOG/Monitor/Widgets/Recent10Jobs";
	public String SPOG_MONITOR_WIDGETS_RECENT10JOBS_VIEWALLJOBS = "/SPOG/Monitor/Widgets/Recent10Jobs/ViewAllJobs";
	public String SPOG_MONITOR_WIDGETS_TOP10SOURCES = "/SPOG/Monitor/Widgets/Top10Sources";
	public String SPOG_MONITOR_WIDGETS_TOP10POLICES = "/SPOG/Monitor/Widgets/Top10Policies";
	public String SPOG_MONITOR_WIDGETS_TOP10POLICES_BYJOBSTATUS = "/SPOG/Monitor/Widgets/Top10Policies/ByJobStatus";
	public String SPOG_MONITOR_WIDGETS_TOP10CUSTOMERS = "/SPOG/Monitor/Widgets/Top10Customers";
	public String SPOG_MONITOR_WIDGETS_TOP10CUSTOMERS_BYCAPACITYUSAGE = "/SPOG/Monitor/Widgets/Top10Customers/ByCapacityUsage";
	public String SPOG_MONITOR_WIDGETS_DATA_TRANSFER_FOR_CLOUD_DIRECT = "/SPOG/Monitor/Widgets/DataTransferForCloudDirect";
	public String SPOG_MONITOR_WIDGETS_DEDUPE_SAVINGS_FOR_CLOUD_HYBRID = "/SPOG/Monitor/Widgets/DedupeSavingsForCloudHybrid";
	public String SPOG_MONITOR_WIDGETS_USAGE_TREND_FOR_CLOUD_DIRECT = "/SPOG/Monitor/Widgets/UsageTrendForCloudDirect";
	public String SPOG_MONITOR_WIDGETS_USAGE_TREND_FOR_CLOUD_DIRECT_BYFULLBACKUPDATA = "/SPOG/Monitor/Widgets/UsageTrendForCloudDirect/ByFullBackupData";
	public String SPOG_MONITOR_WIDGETS_USAGE_TREND_FOR_CLOUD_HYBRID = "/SPOG/Monitor/Widgets/UsageTrendForCloudHybrid";
	public String SPOG_MONITOR_WIDGETS_RECENT_10_JOBS_IN_PROGRESS = "/SPOG/Monitor/Widgets/Recent10JobsInProgress";

	public String SPOG_MONITOR_ALERTS = "/SPOG/Monitor/Alerts";
	public String SPOG_MONITOR_ALERTS_CRITICAL = "/SPOG/Monitor/Alerts/Critical";
	public String SPOG_MONITOR_ALERTS_WARNING = "/SPOG/Monitor/Alerts/Warning";
	public String SPOG_MONITOR_ALERTS_INFORMATION = "/SPOG/Monitor/Alerts/Information";
	public String SPOG_MONITOR_ALERTS_CRITICAL_COUNT = "/SPOG/Monitor/Alerts/CriticalCount";
	public String SPOG_MONITOR_ALERTS_WARNING_COUNT = "/SPOG/Monitor/Alerts/WarningCount";
	public String SPOG_MONITOR_ALERTS_INFORMATION_COUNT = "/SPOG/Monitor/Alerts/InformationCount";
	public String SPOG_MONITOR_ALERTS_ACKNOWLEDGE_ALL = "/SPOG/Monitor/Alerts/AcknowledgeAll";
	public String SPOG_MONITOR_ALERTS_ACKNOWLEDGE_SELECTED = "/SPOG/Monitor/Alerts/AcknowledgeSelected";

	//configure
	public String SPOG_CONFIGURE = "/SPOG/Configure";
	public String SPOG_CONFIGURE_CAPACITY_USAGE_TREND =  "/SPOG/Configure/CapacityUsageTrend";
	public String SPOG_CONFIGURE_USERACCOUNTS = "/SPOG/Configure/UserAccounts";
	public String SPOG_CONFIGURE_USERACCOUNTS_ADDUSER = "/SPOG/Configure/UserAccounts/AddUser";
	public String SPOG_CONFIGURE_USERACCOUNTS_USERTABLE_HEADERS =  "/SPOG/Configure/UserAccounts/UserTable/Headers";
	public String SPOG_CONFIGURE_USERACCOUNTS_USERTABLE_ROWS =  "/SPOG/Configure/UserAccounts/UserTable/Rows";
	public String SPOG_CONFIGURE_USERACCOUNTS_USERTABLE_COLUMNS =  "/SPOG/Configure/UserAccounts/UserTable/Columns";
	public String SPOG_CONFIGURE_USERACCOUNTS_USERTABLE_BODY =  "/SPOG/Configure/UserAccounts/UserTable/Body";	
	public String SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_FIRSTNAME = "/SPOG/Configure/UserAccounts/UserAccountDetails/FirstName";
	public String SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_LASTNAME = "/SPOG/Configure/UserAccounts/UserAccountDetails/LastName";
	public String SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_ROLE = "/SPOG/Configure/UserAccounts/UserAccountDetails/Role";
	public String SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_EMAIL = "/SPOG/Configure/UserAccounts/UserAccountDetails/Email";
	public String SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_CANCEL = "/SPOG/Configure/UserAccounts/UserAccountDetails/Cancel";
	public String SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_ROLEMENU = "/SPOG/Configure/UserAccounts/UserAccountDetails/RoleMenu";
	public String SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_SAVE = "/SPOG/Configure/UserAccounts/UserAccountDetails/Save";
	public String SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_FIRSTNAMELENGTHERROR  ="/SPOG/Configure/UserAccounts/UserAccountDetails/FirstNameLengthError";
	public String SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_LASTNAMELENGTHERROR  ="/SPOG/Configure/UserAccounts/UserAccountDetails/LastNameLengthError";
	public String SPOG_SETPASSWORD_CREATEPASSWORD= "/SPOG/SetPassword/CreatePassword";
	public String SPOG_SETPASSWORD_CONFIRMPASSWORD= "/SPOG/SetPassword/ConfirmPassword";
	public String SPOG_SETPASSWORD_CREATEACCOUNT= "/SPOG/SetPassword/CreateAcount";
	public String SPOG_SETPASSWORD_AGREE= "/SPOG/SetPassword/Agree";



	//Branding

	public String SPOG_CONFIGURE_BRANDING= "/SPOG/Configure/Branding";
	public String SPOG_CONFIGURE_BRANDING_ORGANIZATIONNAME="/SPOG/Configure/Branding/OrganizationName";
	public String SPOG_CONFIGURE_BRANDING_SAVE="/SPOG/Configure/Branding/Save";
	public String SPOG_CONFIGURE_BRANDING_REQUIREDERROR= "/SPOG/Configure/Branding/RequiredError";
	public String SPOG_CONFIGURE_BRANDING_MINLENGTHERROR ="/SPOG/Configure/Branding/MinLengthError";
	public String SPOG_CONFIGURE_BRANDING_EXCEEDORGNAMEERROR ="/SPOG/Configure/Branding/ExceedOrgNameError";
	public String SPOG_CONFIGURE_BRANDING_CANCEL="/SPOG/Configure/Branding/Cancel";
	public String  SPOG_CONFIGURE_BRANDING_PRIMARYCOLOR="/SPOG/Configure/Branding/Primarycolor";
	public String SPOG_CONFIGURE_BRANDING_SECONDARYCOLOR = "/SPOG/Configure/Branding/SecondaryColor";
	public String SPOG_CONFIGURE_BRANDING_INVALIDHEXCODE= "/SPOG/Configure/Branding/InvaliHexCode";
	public String SPOG_CONFIGURE_BRANDING_EMAILTAB="/SPOG/Configure/Branding/EmailTab";
	public String SPOG_CONFIGURE_BRANDING_EMAILMESSAGE="/SPOG/Configure/Branding/EmailMessage";


	//Branding Emialer
	public String SPOG_CONFIGURE_BRANDING_FOREMAILERNAME="/SPOG/Configure/Branding/ForEmailerName";
	public String SPOG_CONFIGURE_BRANDING_FOREMAILADRESS="/SPOG/Configure/Branding/FromEmailAdress";
	public String SPOG_CONFIGURE_BRANDING_SUUPPORTCHART="/SPOG/Configure/Branding/SupportChart";
	public String SPOG_CONFIGURE_BRANDING_SALESCHART="/SPOG/Configure/Branding/SalesChart";
	public String SPOG_CONFIGURE_BRANDING_FACEBOOKLINK="/SPOG/Configure/Branding/FaceBookLink";
	public String SPOG_CONFIGURE_BRANDING_TWITTERLINK="/SPOG/Configure/Branding/TwitterLink";
	public String SPOG_CONFIGURE_BRANDING_LINKEDINLINK="/SPOG/Configure/Branding/LinkedInLink";
	public String SPOG_CONFIGURE_BRANDING_SOCIALMEDIAPLATFORM="/SPOG/Configure/Branding/SocialMediaPlatForm";
	public String SPOG_CONFIGURE_BRANDING_CONTACTUS="/SPOG/Configure/Branding/ContactUs";
	public String SPOG_CONFIGURE_BRANDING_LEGALNOTICE="/SPOG/Configure/Branding/LegalNotice";
	public String SPOG_CONFIGURE_BRANDING_PRIVACY="/SPOG/Configure/Branding/Privacy";


	//Upload logo
	public String SPOG_CONFIGURE_BRANDING_UPLOADLOGO="/SPOG/Configure/Branding/Uploadlogo";
	public String SPOG_CONFIGURE_BRANDING_CHOOSEPHOTO="/SPOG/Configure/Branding/ChoosePhoto";
	public String SPOG_CONFIGURE_BRANDING_UPDATEPHOTO="/SPOG/Configure/Branding/UpdatePhoto";
	public String SPOG_CONFIGURE_BRANDING_VERIFYCRAPIMAGEMESSAGE="/SPOG/Configure/Branding/VerifyCrapImgaeMessage";
	public String SPOG_CONFIGURE_BRANDING_EXCEEDFILESIZE ="/SPOG/Configure/Branding/ExceedFilesize";
	public String SPOG_CONFIGURE_BRANDING_VALIDFILESIZE = "/SPOG/Configure/Branding/ValidFileSize";


	//Upload LoginImage
	public String SPOG_CONFIGURE_BRANDING_PORTALURL="/SPOG/Configure/Branding/PortalUrl";
	public String SPOG_CONFIGURE_BRANDING_LOGINBTN="/SPOG/Configure/Branding/LoginBtn";
	public String SPOG_CONFIGURE_BRANDING_USESAMELOGOASYOURBRANDSECTION="/SPOG/Configure/Branding/UsesamelogoasYourBrandSection";
	public String SPOG_CONFIGURE_BRANDING_UPLOADLOGINIMAGE="/SPOG/Configure/Branding/UploadLogInImage";
	public String SPOG_CONFIGURE_BRANDING_VERIFYTEXT="/SPOG/Configure/Branding/VerifyText";
	public String SPOG_CONFIGURE_BRANDING_CANCELUPLOADLOGINIMAGE="/SPOG/Configure/Branding/CancelUplogoLoginImage";

	//protect
	public String SPOG_PROTECT =  "/SPOG/Protect";
	public String SPOG_PROTECT_POLICY =  "/SPOG/Protect/Policy";
	public String SPOG_PROTECT_POLICY_ADDPOLICY =  "/SPOG/Protect/Policy/AddPolicy";
	public String SPOG_PROTECT_POLICY_POLICYFORM_BASICS = "/SPOG/Protect/Policy/PolicyForm/Basics";
	public String SPOG_PROTECT_POLICY_POLICYFORM_BASICS_POLICYNAME =  "/SPOG/Protect/Policy/PolicyForm/Basics/PolicyName";
	public String SPOG_PROTECT_POLICY_POLICYFORM_BASICS_BAASPOLICY =  "/SPOG/Protect/Policy/PolicyForm/Basics/BaasPolicy";
	public String SPOG_PROTECT_POLICY_POLICYFORM_BASICS_DRAASPOLICY =  "/SPOG/Protect/Policy/PolicyForm/Basics/DraasPolicy";
	public String SPOG_PROTECT_POLICY_POLICYFORM_BASICS_REPLICATIONPOLICY =  "/SPOG/Protect/Policy/PolicyForm/Basics/ReplicationPolicy";
	public String SPOG_PROTECT_POLICY_POLICYFORM_BASICS_DESCRIPTION =  "/SPOG/Protect/Policy/PolicyForm/Basics/Description";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION =  "/SPOG/Protect/Policy/PolicyForm/Destination";
	public String SPOG_PROTECT_POLICY_POLICYFORM_TASK =  "/SPOG/Protect/Policy/PolicyForm/Task";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_DESTINATIONCONNECTOR =  "/SPOG/Protect/Policy/PolicyForm/Destination/DestinationConnector";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_NEWDESTINATIONBLOCK =  "/SPOG/Protect/Policy/PolicyForm/Destination/NewDestinationBlock";

	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_REPLICATEFROM =  "/SPOG/Protect/ReplicateFrom";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_REPLICATETO =  "/SPOG/Protect/ReplicateTo";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_ACTIVETYPE =  "/SPOG/Protect/Policy/PolicyForm/Destination/WhatToProtect/ActiveType";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_WINDOWSIMAGE =  "/SPOG/Protect/Policy/PolicyForm/Destination/WhatToProtect/WindowsImage";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_WINDOWSIMAGE_FULLSYSTEM =  "/SPOG/Protect/Policy/PolicyForm/Destination/WhatToProtect/WindowsImage/FullSystem";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_WINDOWSIMAGE_SELECTDRIVE =  "/SPOG/Protect/Policy/PolicyForm/Destination/WhatToProtect/WindowsImage/SelectDrive";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_WINDOWSIMAGE_SELECTDRIVE_DRIVECONTAINER =  "/SPOG/Protect/Policy/PolicyForm/Destination/WhatToProtect/WindowsImage/SelectDrive/DriveContainer";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_BACKUPFILE =  "/SPOG/Protect/Policy/PolicyForm/Destination/WhatToProtect/BackupFile";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT = "/SPOG/Protect/Policy/PolicyForm/Destination/WhereToProtect";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_DESTINATION = "/SPOG/Protect/Policy/PolicyForm/Destination/WhereToProtect/Destination";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_DESTINATIONCONTAINER = "/SPOG/Protect/Policy/PolicyForm/Destination/WhereToProtect/DestinationContainer";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_CREATELOCALBACKUP4IMAGE = "/SPOG/Protect/Policy/PolicyForm/Destination/WhereToProtect/CreateLocalBackup4Image";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_CREATELOCALBACKUPLABEL4IMAGE = "/SPOG/Protect/Policy/PolicyForm/Destination/WhereToProtect/CreateLocalBackupLabel4Image";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_LOCALBACKUPPATH4IMAGE = "/SPOG/Protect/Policy/PolicyForm/Destination/WhereToProtect/LocalBackupPath4Image";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_CREATELOCALBACKUP4FILE = "/SPOG/Protect/Policy/PolicyForm/Destination/WhereToProtect/CreateLocalBackup4File";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_CREATELOCALBACKUPLABEL4FILE = "/SPOG/Protect/Policy/PolicyForm/Destination/WhereToProtect/CreateLocalBackupLabel4File";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_LOCALBACKUPPATH4FILE = "/SPOG/Protect/Policy/PolicyForm/Destination/WhereToProtect/LocalBackupPath4File";

	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_REMOTECONSOLESERVERNAME= "/SPOG/Protect/Policy/PolicyForm/Destination/WhereToProtect/RemoteConsoleServerName";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_REMOTECONSOLEUSERNAME = "/SPOG/Protect/Policy/PolicyForm/Destination/WhereToProtect/RemoteConsoleUsername";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_REMOTECONSOLEPASSWORD = "/SPOG/Protect/Policy/PolicyForm/Destination/WhereToProtect/RemoteConsolePassword";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_REMOTECONSOLEPORT = "/SPOG/Protect/Policy/PolicyForm/Destination/WhereToProtect/RemoteConsolePort";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_HTTPPROTOCOL = "/SPOG/Protect/Policy/PolicyForm/Destination/WhereToProtect/HTTPProtocol";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_HTTPSPROTOCOL = "/SPOG/Protect/Policy/PolicyForm/Destination/WhereToProtect/HTTPSProtocol";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_CONNECTBUTTON = "/SPOG/Protect/Policy/PolicyForm/Destination/WhereToProtect/ConnectButton";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_SELECTPOLICYDROPDOWN = "/SPOG/Protect/Policy/PolicyForm/Destination/WhereToProtect/SelectPolicyDropDown";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_RETRYMINUTES = "/SPOG/Protect/Policy/PolicyForm/Destination/WhereToProtect/RetryMinutes";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHERETOPROTECT_RETRYNUMBER = "/SPOG/Protect/Policy/PolicyForm/Destination/WhereToProtect/RetryNumber";

	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHENTOPROTECT = "/SPOG/Protect/Policy/PolicyForm/Destination/WhenToProtect";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHENTOPROTECT_BACKUPSCHEDULE = "/SPOG/Protect/Policy/PolicyForm/Destination/WhenToProtect/BackupSchedule";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHENTOPROTECT_STARTTIMEHOUR = "/SPOG/Protect/Policy/PolicyForm/Destination/WhenToProtect/StartTimeHour";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHENTOPROTECT_STARTTIMEMINUTE= "/SPOG/Protect/Policy/PolicyForm/Destination/WhenToProtect/StartTimeMinute";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHENTOPROTECT_STARTTIMECLOCK= "/SPOG/Protect/Policy/PolicyForm/Destination/WhenToProtect/StartTimeClock";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHENTOPROTECT_THROTTLESCHEDULEADD= "/SPOG/Protect/Policy/PolicyForm/Destination/WhenToProtect/ThrottleScheduleAdd";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHENTOPROTECT_THROTTLESCHEDULELINE= "/SPOG/Protect/Policy/PolicyForm/Destination/WhenToProtect/ThrottleScheduleLine";

	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHENTOPROTECT_SCHEDULELINE= "/SPOG/Protect/Policy/PolicyForm/Destination/WhenToProtect/ScheduleLine";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHENTOPROTECT_MERGESCHEDULEADD= "/SPOG/Protect/Policy/PolicyForm/Destination/WhenToProtect/MergeScheduleAdd";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHENTOPROTECT_CLEARALLSCHEDULE = "/SPOG/Protect/Policy/PolicyForm/Destination/WhenToProtect/ClearAllSchedule";

	public String SPOG_PROTECT_POLICY_POLICYFORM_CREATEPOLICYBTN= "/SPOG/Protect/Policy/PolicyForm/CreatePolicyBtn";
	public String SPOG_PROTECT_POLICY_POLICYTABLE_HEADERS= "/SPOG/Protect/Policy/PolicyTable/Headers";
	public String SPOG_PROTECT_POLICY_POLICYTABLE_ROWS= "/SPOG/Protect/Policy/PolicyTable/Rows";
	public String SPOG_PROTECT_POLICY_POLICYTABLE_COLUMNS= "/SPOG/Protect/Policy/PolicyTable/Columns";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_BACKUPFILEADD= "/SPOG/Protect/Policy/PolicyForm/Destination/WhatToProtect/BackupFileAdd";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_WHATTOPROTECT_PATHLINE= "/SPOG/Protect/Policy/PolicyForm/Destination/WhatToProtect/PathLine";
	public String SPOG_PROTECT_POLICY_POLICYFORM_SOURCE= "/SPOG/Protect/Policy/PolicyForm/Source";
	public String SPOG_PROTECT_POLICY_POLICYFORM_SOURCE_SELECTSOURCEBTN= "/SPOG/Protect/Policy/PolicyForm/Source/SelectSourceBtn";
	public String SPOG_PROTECT_POLICY_POLICYFORM_SOURCE_ADDSOURCEBTN= "/SPOG/Protect/Policy/PolicyForm/Source/AddSourceBtn";

	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_ADDITIONALSETTING = "/SPOG/Protect/Policy/PolicyForm/Destination/AdditionalSetting";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_ADDITIONALSETTING_EXCLUDERULEADD = "/SPOG/Protect/Policy/PolicyForm/Destination/AdditionalSetting/ExcludeRuleAdd";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_ADDITIONALSETTING_EXCLUDERULELINE = "/SPOG/Protect/Policy/PolicyForm/Destination/AdditionalSetting/ExcludeRuleLine";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_ADDITIONALSETTING_DAILYBACKUPSRETENTION = "/SPOG/Protect/Policy/PolicyForm/Destination/AdditionalSetting/DailyBackupsRetention";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_ADDITIONALSETTING_MONTHLYBACKUPSRETENTION = "/SPOG/Protect/Policy/PolicyForm/Destination/AdditionalSetting/MonthlyBackupsRetention";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_ADDITIONALSETTING_WEEKLYBACKUPSRETENTION = "/SPOG/Protect/Policy/PolicyForm/Destination/AdditionalSetting/WeeklyBackupsRetention";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_ADDITIONALSETTING_MANUALBACKUPSRETENTION = "/SPOG/Protect/Policy/PolicyForm/Destination/AdditionalSetting/ManualBackupsRetention";
	public String SPOG_PROTECT_POLICY_POLICYFORM_DESTINATION_ADDITIONALSETTING_RETENTIONPOLICY = "/SPOG/Protect/Policy/PolicyForm/Destination/AdditionalSetting/RetentionPolicy";

	public String SPOG_PROTECT_CUSTOMERACCOUNTS = "/SPOG/Protect/CustomerAccounts";
	public String SPOG_PROTECT_CUSTOMERACCOUNTS_TOTALCUSTOMERACCOUNTS_LABEL = "/SPOG/Protect/CustomerAccounts/TotalCustomerAccountsLabel";
	public String SPOG_PROTECT_CUSTOMERACCOUNTS_TOTALCUSTOMERACCOUNTS_COUNT = "/SPOG/Protect/CustomerAccounts/TotalCustomerAccountsCount";
	public String SPOG_PROTECT_ADDCUSTOMERACCOUNT = "/SPOG/Protect/CustomerAccounts/AddCustomerAccount";
	public String SPOG_PROTECT_ADDCUSTOMERACCOUNT_TITLE = "/SPOG/Protect/CustomerAccounts/AddCustomerAccount/Title";
	public String SPOG_PROTECT_ADDCUSTOMERACCOUNT_CUSTOMERNAME = "/SPOG/Protect/CustomerAccounts/AddCustomerAccount/CustomerName";
	public String SPOG_PROTECT_ADDCUSTOMERACCOUNT_ADDCUSTOMER = "/SPOG/Protect/CustomerAccounts/AddCustomerAccount/AddCustomer";
	public String SPOG_PROTECT_CUSTOMERACCOUNTS_CUSTOMERACCOUNTDETAILS_FORM = "/SPOG/Protect/CustomerAccounts/CustomerAccountDetails/Form";
	public String SPOG_PROTECT_CUSTOMERACCOUNTS_CUSTOMERACCOUNTDETAILS_CUSTOMERNAME = "/SPOG/Protect/CustomerAccounts/CustomerAccountDetails/CustomerName";
	public String SPOG_PROTECT_CUSTOMERACCOUNTS_CUSTOMERACCOUNTDETAILS_ACTIONS = "/SPOG/Protect/CustomerAccounts/CustomerAccountDetails/Actions";
	public String SPOG_PROTECT_CUSTOMERACCOUNTS_SETUSAGETHRESHOLD_TITLE = "/SPOG/Protect/CustomerAccounts/SetUsageThreshold/Title";
	public String SPOG_PROTECT_CUSTOMERACCOUNTS_SETUSAGETHRESHOLD_CDUSAGETHRESHOLD = "/SPOG/Protect/CustomerAccounts/SetUsageThreshold/CDUsageThreshold";
	public String SPOG_PROTECT_CUSTOMERACCOUNTS_SETUSAGETHRESHOLD_CDUSAGETHRESHOLD_CAPACITY = "/SPOG/Protect/CustomerAccounts/SetUsageThreshold/CDUsageThresholdCapacity";
	public String SPOG_PROTECT_CUSTOMERACCOUNTS_SETUSAGETHRESHOLD_CHUSAGETHRESHOLD_DIV = "/SPOG/Protect/CustomerAccounts/SetUsageThreshold/CHUsageThresholdDiv";
	public String SPOG_PROTECT_CUSTOMERACCOUNTS_SETUSAGETHRESHOLD_CHUSAGETHRESHOLD = "/SPOG/Protect/CustomerAccounts/SetUsageThreshold/CHUsageThreshold";
	public String SPOG_PROTECT_CUSTOMERACCOUNTS_SETUSAGETHRESHOLD_CHUSAGETHRESHOLD_CAPACITY = "/SPOG/Protect/CustomerAccounts/SetUsageThreshold/CHUsageThresholdCapacity";
	public String SPOG_PROTECT_CUSTOMERACCOUNTS_ASSIGNMSPACCOUNTADMIN_TITLE = "/SPOG/Protect/CustomerAccounts/AssignMSPAccountAdmin/Title";
	public String SPOG_PROTECT_CUSTOMERACCOUNTS_ASSIGNMSPACCOUNTADMIN_SEARCHADMIN = "/SPOG/Protect/CustomerAccounts/AssignMSPAccountAdmin/SearchAdmin";
	public String SPOG_PROTECT_CUSTOMERACCOUNTS_ASSIGNMSPACCOUNTADMIN_ADD = "/SPOG/Protect/CustomerAccounts/AssignMSPAccountAdmin/Add";
	public String SPOG_PROTECT_CUSTOMERACCOUNTS_ASSIGNMSPACCOUNTADMIN_CONTENTDIV = "/SPOG/Protect/CustomerAccounts/AssignMSPAccountAdmin/ContentDiv";
	public String SPOG_PROTECT_CUSTOMERACCOUNTS_ASSIGNMSPACCOUNTADMIN_ASSIGN = "/SPOG/Protect/CustomerAccounts/AssignMSPAccountAdmin/Assign";
	public String SPOG_PROTECT_CUSTOMERACCOUNTS_VIEWASENDUSERADMIN_BANNERDIV = "/SPOG/Protect/CustomerAccounts/ViewAsEndUserAdmin/BannerDiv";
	public String SPOG_PROTECT_CUSTOMERACCOUNTS_DELETEACTION_DELETE = "/SPOG/Protect/CustomerAccounts/DeleteAction/Delete";


	//analyze @Prasad.Deverakonda
	public String SPOG_ANALYZE =  "/SPOG/Analyze";
	public String SPOG_ANALYZE_JOBS = "/SPOG/Analyze/Jobs";
	public String SPOG_ANALYZE_LOGS = "/SPOG/Analyze/Logs";
	public String SPOG_ANALYZE_REPORTS = "/SPOG/Analyze/Reports";
	public String SPOG_ANALYZE_BACKUPJOBS = "/SPOG/Analyze/Reports";
	public String SPOG_ANALYZE_RECOVERYJOBS = "/SPOG/Analyze/Reports";
	public String SPOG_ANALYZE_DATATRANSFER = "/SPOG/Analyze/Reports";
	public String SPOG_ANALYZE_CAPACITYUSAGE = "/SPOG/Analyze/Reports";
	public String SPOG_ANALYZE_MANAGEREPORTSCHEDULES = "/SPOG/Analyze/ManageReportSchedules";


	//analyze - Jobs
	public String SPOG_ANALYZE_JOBS_SEARCH_BYNAME = "/SPOG/Analyze/Jobs/SearchByName";
	public String SPOG_ANALYZE_JOBS_SEARCH_SCHEDULEDFOR = "/SPOG/Analyze/Jobs/SearchScheduledFor";
	public String SPOG_ANALYZE_JOBS_SEARCH_GENERATEDON = "/SPOG/Analyze/Jobs/SearchGeneratedOn";
	public String SPOG_ANALYZE_JOBS_SEARCH_DATERANGE = "/SPOG/Analyze/Jobs/SearchDateRange";
	public String SPOG_ANALYZE_JOBS_SEARCH_CLEARALL = "/SPOG/Analyze/Jobs/SearchClearAll";
	public String SPOG_ANALYZE_JOBS_SEARCH_SEARCH = "/SPOG/Analyze/Jobs/SearchSearch";
	public String SPOG_ANALYZE_JOBS_MINLENGTHERROR = "/SPOG/Analyze/Jobs/SearchSearch/MinLengthError";
	public String SPOG_ANALYZE_JOBS_MAXLENGTHERROR = "/SPOG/Analyze/Jobs/SearchSearch/MaxLengthError";
	public String SPOG_ANALYZE_JOBS_SAVED_SEARCHES = "/SPOG/Analyze/Jobs/SavedSearches";
	public String SPOG_ANALYZE_JOBS_SEARCH_SEARCHRESULTSFOR = "/SPOG/Analyze/Jobs/SearchResultsFor";
	public String SPOG_ANALYZE_JOBS_SELECTED_LABEL = "/SPOG/Analyze/Jobs/SelectedLable";
	public String SPOG_ANALYZE_JOBS_SELECTED_COUNT = "/SPOG/Analyze/Jobs/SelectedCount";
	public String SPOG_ANALYZE_JOBS_ACTIONS_DROPDOWN = "/SPOG/Analyze/Jobs/ActionsDropDown";
	public String SPOG_ANALYZE_JOBS_ACTIONS_DELETE_REPORT = "/SPOG/Analyze/Jobs/Actions_Delete_Report";
	public String SPOG_ANALYZE_JOBS_MANAGE_SETTINGS_BUTTON = "/SPOG/Analyze/Jobs/ManageSettingsButton";
	public String SPOG_ANALYZE_JOBS_MANAGE_SAVED_SEARCHES = "/SPOG/Analyze/Jobs/ManageSavedSearches";

	public String SPOG_ANALYZE_JOBS_JOBSTABLE_HEADER = "/SPOG/Analyze/Jobs/JobsTable/Headers";
	public String SPOG_ANALYZE_JOBS_JOBSTABLE_ROWS =  "/SPOG/Analyze/Jobs/JobsTable/Rows";
	public String SPOG_ANALYZE_JOBS_JOBSSTABLE_COLUMNS =  "/SPOG/Analyze/Jobs/ReportsTable/Columns";
	public String SPOG_ANALYZE_JOBS_JOBSSTABLE_BODY =  "/SPOG/Analyze/Jobs/ReportsTable/Body";
	public String SPOG_ANALYZE_JOBS_CONTEXTUALACTIONS = "/SPOG/Analyze/Jobs/ContextualActions";
	public String SPOG_ANALYZE_JOBS_CONTEXTUALACTIONS_VIEWJOBSACTION = "/SPOG/Analyze/Jobs/ContextualActions/ViewJobsAction";
	public String SPOG_ANALYZE_JOBS_CONTEXTUALACTIONS_DELETEREPORTACTION_CONFIRM = "/SPOG/Analyze/Jobs/ContextualActions/DeleteAction/Confirm";
	public String SPOG_ANALYZE_JOBS_CONTEXTUALACTIONS_DELETEREPORTACTION_CANCEL = "/SPOG/Analyze/Jobs/ContextualActions/DeleteAction/Cancel";

	//analyze - Logs
	public String SPOG_ANALYZE_LOGS_SEARCH_BYNAME = "/SPOG/Analyze/Logs/SearchByName";
	public String SPOG_ANALYZE_LOGS_SEARCH_SCHEDULEDFOR = "/SPOG/Analyze/Logs/SearchScheduledFor";
	public String SPOG_ANALYZE_LOGS_SEARCH_GENERATEDON = "/SPOG/Analyze/Logs/SearchGeneratedOn";
	public String SPOG_ANALYZE_LOGS_SEARCH_DATERANGE = "/SPOG/Analyze/Logs/SearchDateRange";
	public String SPOG_ANALYZE_LOGS_SEARCH_CLEARALL = "/SPOG/Analyze/Logs/SearchClearAll";
	public String SPOG_ANALYZE_LOGS_SEARCH_SEARCH = "/SPOG/Analyze/Logs/SearchSearch";
	public String SPOG_ANALYZE_LOGS_MINLENGTHERROR = "/SPOG/Analyze/Logs/SearchSearch/MinLengthError";
	public String SPOG_ANALYZE_LOGS_MAXLENGTHERROR = "/SPOG/Analyze/Logs/SearchSearch/MaxLengthError";
	public String SPOG_ANALYZE_LOGS_SAVED_SEARCHES = "/SPOG/Analyze/Logs/SavedSearches";
	public String SPOG_ANALYZE_LOGS_SEARCH_SEARCHRESULTSFOR = "/SPOG/Analyze/Logs/SearchResultsFor";
	public String SPOG_ANALYZE_LOGS_SELECTED_LABEL = "/SPOG/Analyze/Logs/SelectedLable";
	public String SPOG_ANALYZE_LOGS_SELECTED_COUNT = "/SPOG/Analyze/Logs/SelectedCount";
	public String SPOG_ANALYZE_LOGS_ACTIONS_DROPDOWN = "/SPOG/Analyze/Logs/ActionsDropDown";
	public String SPOG_ANALYZE_LOGS_ACTIONS_DELETE_REPORT = "/SPOG/Analyze/Logs/Actions_Delete_Report";
	public String SPOG_ANALYZE_LOGS_MANAGE_SETTINGS_BUTTON = "/SPOG/Analyze/Logs/ManageSettingsButton";
	public String SPOG_ANALYZE_LOGS_MANAGE_SAVED_SEARCHES = "/SPOG/Analyze/Logs/ManageSavedSearches";

	public String SPOG_ANALYZE_LOGS_REPORTSTABLE_HEADER = "/SPOG/Analyze/Reports/ReportsTable/Headers";
	public String SPOG_ANALYZE_LOGS_REPORTSTABLE_ROWS =  "/SPOG/Analyze/Reports/ReportsTable/Rows";
	public String SPOG_ANALYZE_LOGS_REPORTSTABLE_COLUMNS =  "/SPOG/Analyze/Reports/ReportsTable/Columns";
	public String SPOG_ANALYZE_LOGS_REPORTSTABLE_BODY =  "/SPOG/Analyze/Reports/ReportsTable/Body";


	//analyze - reports @Prasad.Deverakonda
	public String SPOG_ANALYZE_REPORTS_SEARCH_BYNAME = "/SPOG/Analyze/Reports/SearchByName";
	public String SPOG_ANALYZE_REPORTS_SEARCH_SCHEDULEDFOR = "/SPOG/Analyze/Reports/SearchScheduledFor";
	public String SPOG_ANALYZE_REPORTS_SEARCH_GENERATEDON = "/SPOG/Analyze/Reports/SearchGeneratedOn";
	public String SPOG_ANALYZE_REPORTS_SEARCH_DATERANGE = "/SPOG/Analyze/Reports/SearchDateRange";
	public String SPOG_ANALYZE_REPORTS_SEARCH_CLEARALL = "/SPOG/Analyze/Reports/SearchClearAll";
	public String SPOG_ANALYZE_REPORTS_SEARCH_SEARCH = "/SPOG/Analyze/Reports/SearchSearch";
	public String SPOG_ANALYZE_REPORTS_MINLENGTHERROR = "/SPOG/Analyze/Reports/SearchSearch/MinLengthError";
	public String SPOG_ANALYZE_REPORTS_MAXLENGTHERROR = "/SPOG/Analyze/Reports/SearchSearch/MaxLengthError";
	public String SPOG_ANALYZE_REPORTS_SAVED_SEARCHES = "/SPOG/Analyze/Reports/SavedSearches";
	public String SPOG_ANALYZE_REPORTS_SEARCH_SEARCHRESULTSFOR = "/SPOG/Analyze/Reports/SearchResultsFor";
	public String SPOG_ANALYZE_REPORTS_SELECTED_LABEL = "/SPOG/Analyze/Reports/SelectedLable";
	public String SPOG_ANALYZE_REPORTS_SELECTED_COUNT = "/SPOG/Analyze/Reports/SelectedCount";
	public String SPOG_ANALYZE_REPORTS_ACTIONS_DROPDOWN = "/SPOG/Analyze/Reports/ActionsDropDown";
	public String SPOG_ANALYZE_REPORTS_ACTIONS_DELETE_REPORT = "/SPOG/Analyze/Reports/Actions_Delete_Report";
	public String SPOG_ANALYZE_REPORTS_MANAGE_SETTINGS_BUTTON = "/SPOG/Analyze/Reports/ManageSettingsButton";
	public String SPOG_ANALYZE_REPORTS_MANAGE_SAVED_SEARCHES = "/SPOG/Analyze/Reports/ManageSavedSearches";

	public String SPOG_ANALYZE_REPORTS_REPORTSTABLE_HEADER = "/SPOG/Analyze/Reports/ReportsTable/Headers";
	public String SPOG_ANALYZE_REPORTS_REPORTSTABLE_ROWS =  "/SPOG/Analyze/Reports/ReportsTable/Rows";
	public String SPOG_ANALYZE_REPORTS_REPORTSTABLE_COLUMNS =  "/SPOG/Analyze/Reports/ReportsTable/Columns";
	public String SPOG_ANALYZE_REPORTS_REPORTSTABLE_BODY =  "/SPOG/Analyze/Reports/ReportsTable/Body";
	public String SPOG_ANALYZE_REPORTS_CONTEXTUALACTIONS = "/SPOG/Analyze/Reports/ContextualActions";
	public String SPOG_ANALYZE_REPORTS_CONTEXTUALACTIONS_DELETEREPORTACTION = "/SPOG/Analyze/Reports/ContextualActions/DeleteReportAction";
	public String SPOG_ANALYZE_REPORTS_CONTEXTUALACTIONS_DELETEREPORTACTION_CONFIRM = "/SPOG/Analyze/Reports/ContextualActions/DeleteAction/Confirm";
	public String SPOG_ANALYZE_REPORTS_CONTEXTUALACTIONS_DELETEREPORTACTION_CANCEL = "/SPOG/Analyze/Reports/ContextualActions/DeleteAction/Cancel";


	//analyze - reports -create_reports @Prasad.Deverakonda
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT = "/SPOG/Analyze/Reports/CreateReport";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_CLOSE = "/SPOG/Analyze/Reports/CreateReport/Close";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_TITLE = "/SPOG/Analyze/Reports/CreateReportTitle";

	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_REPORTTYPE = "/SPOG/Analyze/Reports/CreateReport/ReportType";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_REPORTTYPE_BACKUPJOBS = "/SPOG/Analyze/Reports/CreateReport/ReportType/BackupJobs";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_REPORTTYPE_RECOVERYJOBS = "/SPOG/Analyze/Reports/CreateReport/ReportType/RecoveryJobs";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_REPORTTYPE_DATATRANSFER = "/SPOG/Analyze/Reports/CreateReport/ReportType/DataTransfer";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_REPORTTYPE_CAPACITYUSAGE = "/SPOG/Analyze/Reports/CreateReport/ReportType/CapacityUsage";

	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_REPORTNAME = "/SPOG/Analyze/Reports/CreateReport/ReportName";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_REPORTNAME_REQUIREDERROR = "/SPOG/Analyze/Reports/CreateReport/ReportName/RequiredError";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_REPORTNAME_MINLENGTHERROR = "/SPOG/Analyze/Reports/CreateReport/ReportName/MinLengthError";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_REPORTNAME_MAXLENGTHERROR = "/SPOG/Analyze/Reports/CreateReport/ReportName/MaxLengthError";

	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_REPORTALLORGS = "/SPOG/Analyze/Reports/CreateReport/ReportAllOgs";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_REPORTSELECTEDORGS = "/SPOG/Analyze/Reports/CreateReport/ReportSelectedOrgs";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_REPORTSELECTEDORGS_SELECT = "/SPOG/Analyze/Reports/CreateReport/ReportSelectedOrgs/Select";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_REPORTSELECTEDORGS_SELECT_CLEARALL = "/SPOG/Analyze/Reports/CreateReport/ReportSelectedOrgs/Select/ClearAll";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_REPORTSELECTEDORGS_SELECT_ADD_BUTTON = "/SPOG/Analyze/Reports/CreateReport/ReportSelectedOrgs/Select/AddBUtton";

	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_EMAILRECIPIENTS = "/SPOG/Analyze/Reports/CreateReport/EmailRecipients";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_EMAILRECIPIENTS_CLEAREMAIL = "/SPOG/Analyze/Reports/CreateReport/EmailRecipients/ClearEmail";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_EMAILRECIPIENTS_ADD_BUTTON = "/SPOG/Analyze/Reports/CreateReport/EmailRecipientsAddBUtton";

	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_GENERATEREPORTNOW = "/SPOG/Analyze/Reports/CreateReport/GenerateReportNow";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_GENERATEREPORTNOW_SELECTDATERANGE = "/SPOG/Analyze/Reports/CreateReport/GenerateReportNow/SelectDateRange";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_GENERATEREPORTNOW_SELECTDATERANGE_LAST24HOURS = "/SPOG/Analyze/Reports/CreateReport/GenerateReportNow/SelectDateRange/Last24Hours";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_GENERATEREPORTNOW_SELECTDATERANGE_LAST7DAYS = "/SPOG/Analyze/Reports/CreateReport/GenerateReportNow/SelectDateRange/Last7Days";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_GENERATEREPORTNOW_SELECTDATERANGE_LAST1MONTH = "/SPOG/Analyze/Reports/CreateReport/GenerateReportNow/SelectDateRange/Last1Month";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_GENERATEREPORTNOW_SELECTDATERANGE_CUSTOM = "/SPOG/Analyze/Reports/CreateReport/GenerateReportNow/SelectDateRange/Custom";

	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_SETASCHEDULE = "/SPOG/Analyze/Reports/CreateReport/SetASchedule";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_SETASCHEDULE_DELIVERYTIME = "/SPOG/Analyze/Reports/CreateReport/SetASchedule/DeliveryTime";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_SETASCHEDULE_DELIVERYTIME_TIME = "/SPOG/Analyze/Reports/CreateReport/SetASchedule/DeliveryTime/Time";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_SETASCHEDULE_DELIVERYTIME_AM = "/SPOG/Analyze/Reports/CreateReport/SetASchedule/DeliveryTime/AM";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_SETASCHEDULE_DELIVERYTIME_PM = "/SPOG/Analyze/Reports/CreateReport/SetASchedule/DeliveryTime/PM";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_SETASCHEDULE_FREQUENCY = "/SPOG/Analyze/Reports/CreateReport/SetASchedule/Frequency";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_SETASCHEDULE_FREQUENCY_DAILY = "/SPOG/Analyze/Reports/CreateReport/SetASchedule/Frequency/Daily";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_SETASCHEDULE_FREQUENCY_WEEKLY = "/SPOG/Analyze/Reports/CreateReport/SetASchedule/Frequency/Weekly";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_SETASCHEDULE_FREQUENCY_MONTHLY = "/SPOG/Analyze/Reports/CreateReport/SetASchedule/Frequency/Mothly";

	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_CANCEL = "/SPOG/Analyze/Reports/CreateReport/Cancel";
	public String SPOG_ANALYZE_REPORTS_CREATEREPORT_CREATE = "/SPOG/Analyze/Reports/CreateReport/Create";

	//analyze - Manage Report Schedule
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_SEARCH_BYNAME = "/SPOG/Analyze/Reports/ManageReportSchedules/SearchByName";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_SEARCH_SCHEDULEDFOR = "/SPOG/Analyze/Reports/ManageReportSchedules/SearchScheduledFor";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_SEARCH_REPORTTYPE = "/SPOG/Analyze/Reports/ManageReportSchedules/SearchGeneratedOn";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_SEARCH_CLEARALL = "/SPOG/Analyze/Reports/ManageReportSchedules/SearchClearAll";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_SEARCH_SEARCH = "/SPOG/Analyze/Reports/ManageReportSchedules/SearchSearch";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_MINLENGTHERROR = "/SPOG/Analyze/Reports/ManageReportSchedules/SearchSearch/MinLengthError";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_MAXLENGTHERROR = "/SPOG/Analyze/Reports/ManageReportSchedules/SearchSearch/MaxLengthError";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_SEARCH_SEARCHRESULTSFOR = "/SPOG/Analyze/ManageReportSchedules/Reports/SearchResultsFor";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_SELECTED_LABEL = "/SPOG/Analyze/Reports/ManageReportSchedules/SelectedLable";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_SELECTED_COUNT = "/SPOG/Analyze/Reports/ManageReportSchedules/SelectedCount";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_ACTIONS_DROPDOWN = "/SPOG/Analyze/Reports/ManageReportSchedules/ActionsDropDown";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_ACTIONS_DELETE = "/SPOG/Analyze/Reports/ManageReportSchedules/Actions_Delete_Report";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_ACTIONS_DELETE_CONFIRM = "/SPOG/Analyze/Reports/ManageReportSchedules/Actions_Delete_Confirm";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_ACTIONS_DELETE_CANCEL = "/SPOG/Analyze/Reports/ManageReportSchedules/Actions_Delete_Cancel";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_ACTIONS_DELETE_SELECTREPORTSCHEDULESINSTANCE = "/SPOG/Analyze/Reports/ManageReportSchedules/Actions_Select_Delete_Schedule_Instance";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_ACTIONS_GENERATENOW = "/SPOG/Analyze/Reports/ManageReportSchedules/Actions_GenerateNow";

	//NOT COMPLETED
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_TABLE_HEADER = "/SPOG/Analyze/Reports/ReportsTable/Headers";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_TABLE_ROWS =  "/SPOG/Analyze/Reports/ReportsTable/Rows";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_TABLE_COLUMNS =  "/SPOG/Analyze/Reports/ReportsTable/Columns";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_TABLE_BODY =  "/SPOG/Analyze/Reports/ReportsTable/Body";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_CONTEXTUALACTIONS = "/SPOG/Analyze/Reports/ContextualActions";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_CONTEXTUALACTIONS_DELETEREPORTACTION = "/SPOG/Analyze/Reports/ContextualActions/DeleteReportAction";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_CONTEXTUALACTIONS_DELETEREPORTACTION_CONFIRM = "/SPOG/Analyze/Reports/ContextualActions/DeleteAction/Confirm";
	public String SPOG_ANALYZE_REPORTS_MANAGEREPORTSCHEDULES_CONTEXTUALACTIONS_DELETEREPORTACTION_CANCEL = "/SPOG/Analyze/Reports/ContextualActions/DeleteAction/Cancel";

	//sources
	public String SPOG_PROTECT_SOURCE = "/SPOG/Protect/Source";
	public String SPOG_PROTECT_SOURCE_SAVEDSEARCHLABEL = "/SPOG/Protect/Source/SavedSearchLabel";
	public String SPOG_PROTECT_SOURCE_ADDSOURCES = "/SPOG/Protect/Source/AddSources";
	public String SPOG_PROTECT_SOURCE_DOWNLOADAGENT = "/SPOG/Protect/Source/DownloadAgent";
	public String SPOG_PROTECT_SOURCE_DOWNLOADAGENT_TITLE = "/SPOG/Protect/Source/DownloadAgent/Title";
	public String SPOG_PROTECT_SOURCE_DOWNLOADAGENT_FORM = "/SPOG/Protect/Source/DownloadAgent/Form";
	public String SPOG_PROTECT_SOURCE_DOWNLOADAGENT_AGENT = "/SPOG/Protect/Source/DownloadAgent/Agent";
	public String SPOG_PROTECT_SOURCE_DOWNLOADAGENT_OVA = "/SPOG/Protect/Source/DownloadAgent/OVA";	
	public String SPOG_PROTECT_SOURCE_SELECTHYPERVISORLABEL = "/SPOG/Protect/Source/SelectHypervisorLabel";
	public String SPOG_PROTECT_SOURCE_LATESTJOBPOPOVERCONTENT = "/SPOG/Protect/Source/LatestJobPopoverContent";

	//Recovered VMs
	public String SPOG_PROTECT_RECOVEREDVMS = "/SPOG/Protect/RecoveredVMs";

	// Destinations
	public String SPOG_PROTECT_DESTINATIONS = "/SPOG/Protect/Destinations";
	public String SPOG_PROTECT_DESTINATIONS_CLOUD_DIRECT_VOLUMES = "/SPOG/Protect/Destinations/CloudDirectVolumes";
	public String SPOG_PROTECT_DESTINATIONS_CLOUD_HYBRID_VOLUMES = "/SPOG/Protect/Destinations/CloudHybridStores";

	public String SPOG_PROTECT_DESTINATIONS_TITLE = "/SPOG/Protect/Destinations/Title";
	public String SPOG_PROTECT_DESTINATIONS_ACTIVE_FILTER = "/SPOG/Protect/Destinations/ActiveFilter";
	public String SPOG_PROTECT_DESTINATIONS_SORTNAME = "/SPOG/Protect/Destinations/SortName";

	public String SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME = "/SPOG/Protect/Destinations/AddCloudVolume";
	public String SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME_ACCOUNTNAME = "/SPOG/Protect/Destinations/AddCloudVolume/AccountName";
	public String SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME_VOLUME_NAME = "/SPOG/Protect/Destinations/AddCloudVolume/VolumeName";
	public String SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME_VOLUME_NAME_ERROR = "/SPOG/Protect/Destinations/AddCloudVolume/VolumeNameError";

	public String SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME_RETENTION = "/SPOG/Protect/Destinations/AddCloudVolume/Retention";
	public String SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME_RETENTION_DROP_DOWN = "/SPOG/Protect/Destinations/AddCloudVolume/RetentionDropDown";
	public String SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME_CANCEL = "/SPOG/Protect/Destinations/AddCloudVolume/cancel";
	public String SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME_DATACENTER = "/SPOG/Protect/Destinations/AddCloudVolume/Datacenter";
	public String SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME_DATACENTER_DROP_DOWN = "/SPOG/Protect/Destinations/AddCloudVolume/DatacenterDropDown";
	public String SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME_COMMENT = "/SPOG/Protect/Destinations/AddCloudVolume/Comments";
	public String SPOG_PROTECT_DESTINATIONS_ADD_CLOUD_VOLUME_BUTTON = "/SPOG/Protect/Destinations/AddCloudVolume/AddCloudVolumeButton";
	public String SPOG_PROTECT_DESTINATIONS_SEARCH_INPUT = "/SPOG/Protect/Destinations/SearchInput";
	public String SPOG_PROTECT_DESTINATIONS_SEARCH_FILTER_BUTTON = "/SPOG/Protect/Destinations/SearchFilterButton";
	public String SPOG_PROTECT_DESTINATIONS_SEARCH_FILTER = "/SPOG/Protect/Destinations/SearchFilter";
	public String SPOG_PROTECT_DESTINATIONS_POLICY_SEARCH_INPUT = "/SPOG/Protect/Destinations/SearchPolicyFilterInput";
	public String SPOGPROTECT_DESTINATIONS_SEARCH_STRING_SUBMIT_BUTTON = "/SPOG/Protect/Destinations/SearchStringSubmitButton";
	public String SPOG_PROTECT_DESTINATIONS_SAVE_SEARCH = "/SPOG/Protect/Destinations/SaveSearch";
	public String SPOG_PROTECT_DESTINATIONS_SAVE_SEARCH_BUTTON = "/SPOG/Protect/Destinations/SaveSearchButton";
	public String SPOG_PROTECT_DESTINATIONS_SAVE_SEARCH_INPUT = "/SPOG/Protect/Destinations/SaveSearchNameInput";
	public String SPOG_PROTECT_DESTINATIONS_CREATED_SAVED_SEARCHES = "/SPOG/Protect/Destinations/CreatedSavedSearches";

	public String SPOG_PROTECT_DESTINATIONS_POLICYFILTER_DROPDOWN = "/SPOG/Protect/Destinations/SearchPolicyFilterDropDown";
	public String SPOG_PROTECT_DESTINATIONS_SELECT_ALL_DESTINATIONS = "/SPOG/Protect/Destinations/SelectAll";

	// Destination settings
	// ManageSaveSearch
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON = "/SPOG/Protect/Destinations/SettingsButton";
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_SAVE_SEARCH = "/SPOG/Protect/Destinations/SettingsButton/ManageSaveSearch";
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_WINDOW_TITLE = "/SPOG/Protect/Destinations/SettingsButton/ManageSaveSearch/ManageSaveSearchWindowTitle";
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_TOOLTIP_LINK = "/SPOG/Protect/Destinations/SettingsButton/ManageSaveSearch/ToolTipLink";
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_TOOLTIP_INFO = "/SPOG/Protect/Destinations/SettingsButton/ManageSaveSearch/TooltipInfo";
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_DEFAULT_FILTER_LABEL = "/SPOG/Protect/Destinations/SettingsButton/ManageSaveSearch/DefaultFilterLabel";
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_SAVESEARCH_INPUT = "/SPOG/Protect/Destinations/SettingsButton/ManageSaveSearch/SaveSearchInput";
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_SEARCHSTRING_INPUT = "/SPOG/Protect/Destinations/SettingsButton/ManageSaveSearch/SearchStringInput";
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_PROTECTIONPOLICY_INPUT = "/SPOG/Protect/Destinations/SettingsButton/ManageSaveSearch/ProtectionPolicyInput";
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_PROTECTIONPOLICY_DROPDOWN = "/SPOG/Protect/Destinations/SettingsButton/ManageSaveSearch/ProtectionPolicyDropDown";
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_PROTECTIONPOLICY_CLEAR = "/SPOG/Protect/Destinations/SettingsButton/ManageSaveSearch/ClearProtectionPolicy";
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_DELETE_BUTTON = "/SPOG/Protect/Destinations/SettingsButton/ManageSaveSearch/DeleteButton";
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_CANCEL_BUTTON = "/SPOG/Protect/Destinations/SettingsButton/ManageSaveSearch/CancelButton";
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGESAVESEARCH_SAVE_BUTTON = "/SPOG/Protect/Destinations/SettingsButton/ManageSaveSearch/SaveButton";

	// ManageColumns
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_LATEST_JOB = "/SPOG/Protect/Destinations/SettingsButton/ManageColumns/LatestJob";
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_LOCATION = "/SPOG/Protect/Destinations/SettingsButton/ManageColumns/Location";
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_DATACENTER_REGION = "/SPOG/Protect/Destinations/SettingsButton/ManageColumns/DataCenterRegion";
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_TYPE = "/SPOG/Protect/Destinations/SettingsButton/ManageColumns/Type";
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_RETENTION = "/SPOG/Protect/Destinations/SettingsButton/ManageColumns/Retention";
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_PROTECTED_DATA = "/SPOG/Protect/Destinations/SettingsButton/ManageColumns/ProtectedData";
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_SOURCE_COUNT = "/SPOG/Protect/Destinations/SettingsButton/ManageColumns/SourceCount";
	public String SPOG_PROTECT_DESTINATIONS_SETTINGS_BUTTON_MANAGE_COLUMNS_PROTECTION_POLICY = "/SPOG/Protect/Destinations/SettingsButton/ManageColumns/ProtectionPolicy";

	// contextual actions
	public String SPOG_PROTECT_DESTINATIONS_CONTEXTUALACTIONS_EDIT = "/SPOG/Protect/Destinations/ContextualActions/Edit";
	public String SPOG_PROTECT_DESTINATIONS_CONTEXTUALACTIONS_VIEW_RECOVERY_POINTS = "/SPOG/Protect/Destinations/ContextualActions/ViewRecoveryPoints";
	public String SPOG_PROTECT_DESTINATIONS_CONTEXTUALACTIONS_DELETE = "/SPOG/Protect/Destinations/ContextualActions/Delete";



	//Analyze
	public String SPOG_ANALYZE_REPORTS_BACKUPJOBREPORTS = "/SPOG/Analyze/Reports/BackupJobReports";
	public String SPOG_ANALYZE_REPORTS_RECOVERYJOBREPORTS = "/SPOG/Analyze/Reports/RecoveryJobReports";
	public String SPOG_ANALYZE_REPORTS_DATATRANSFERREPORT = "/SPOG/Analyze/Reports/DataTransferReports";
	public String SPOG_ANALYZE_REPORTS_CAPACITYUSAGEREPORTS = "/SPOG/Analyze/Reports/CapacityUsageReports";

	//Reports @Nagamalleswari
	public String SPOG_ANALYZE_REPORTS_TITLE="/SPOG/Analyze/Reports/Title";
	public String SPOG_ANALYZE_REPORTS_REPORTTYPETITLE="/SPOG/Analyze/Reports/ReportTypeTitle";
	public String SPOG_ANALYZE_REPORTS_REPORTTYPEBUTTON="/SPOG/Analyze/Reports/ReportTypebutton";
	public String SPOG_ANALYZE_REPORTS_DROPDOWNMENU="/SPOG/Analyze/Reports/DropDownMenu";
	public String SPOG_ANALYZE_REPORTS_REPORTFORALLORGANIZATIONS="/SPOG/Analyze/Reports/ReportForAllOrganziations";
	public String SPOG_ANALYZE_REPORTS_REPORTFORSELECTEDORGANIZATIONS="/SPOG/Analyze/Reports/ReportForSelectedOrganziation";

	//Create reports for all reports common Xpaths @Nagamalleswari

	//public String SPOG_REPORTS_BACKUPJOBS="/SPOG/Analyze/Reports/BackupJobs";
	public String SPOG_ANALYZE_REPORTS_COMMON_CREATEREPORT="/SPOG/Analyze/Reports/Common/CreateReport";
	public String SPOG_ANALYZE_REPORTS_COMMON_REPORTNAME="/SPOG/Analyze/Reports/Common/ReportName";

	public String SPOG_REPORTS_COMMON_RECEPIENTTITLE="/SPOG/Analyze/Reports/Common/RecepientTitile";
	public String SPOG_ANALYZE_REPORTS_COMMON_RECEPIENTID="/SPOG/Analyze/Reports/Common/RecepientID";



	public String SPOG_ANALYZE_REPORTS_COMMON_ALLSOURCES="/SPOG/Analyze/Reports/Common/Allsources";
	public String SPOG_ANALYZE_REPORTS_COMMON_SELECTEDSOURCES="/SPOG/Analyze/Reports/Common/SelectedSources";
	public String SPOG_ANALYZE_REPORTS_COMMON_SELECTEDSOURCEGROUPMENU="/SPOG/Analyze/Reports/Common/SelectedSourceGroupMenu";
	public String SPOG_ANALYZE_REPORTS_COMMON_ADD="/SPOG/Analyze/Reports/Common/Add";
	public String SPOG_ANALYZE_REPORTS_COMMON_GENERATENOW="/SPOG/Analyze/Reports/Common/GenerateNow";
	public String SPOG_ANALYZE_REPORTS_COMMON_SELECTDATARANGE="/SPOG/Analyze/Reports/Common/SelectDatarange";
	public String SPOG_ANALYZE_REPORTS_COMMON_DATARANGEMENU="/SPOG/Analyze/Reports/Common/DatarangeMenu";
	public String SPOG_ANALYZE_REPORTS_COMMON_MENUTITLE="/SPOG/Analyze/Reports/Common/MenuTitle";

	public String SPOG_ANALYZE_REPORTS_COMMON_SCHEDULEREPORT="/SPOG/Analyze/Reports/Common/ScheduleReport";
	public String SPOG_ANALYZE_REPORTS_COMMON_SCHEDULEREPORTDELIVERYTIME="/SPOG/Analyze/Reports/Common/SchedulereportDeliveryTime";
	public String SPOG_ANALYZE_REPORTS_COMMON_DELIVARYTIMEMENU="/SPOG/Analyze/Reports/Common/DelivaryTimemenu";
	public String SPOG_ANALYZE_REPORTS_COMMON_TIMETYPE="/SPOG/Analyze/Reports/Common/TimeType";
	public String SPOG_ANALYZE_REPORTS_COMMON_TIMETYPEMENU="/SPOG/Analyze/Reports/Common/TimeTypeMenu";
	public String SPOG_ANALYZE_REPORTS_COMMON_FREQUENCYTITLE="/SPOG/Analyze/Reports/Common/FrequencyTitle";

	public String SPOG_ANALYZE_REPORTS_COMMON_FREQUENCY="/SPOG/Analyze/Reports/Common/Frequency";
	public String SPOG_ANALYZE_REPORTS_COMMON_FREQUENCYMENU="/SPOG/Analyze/Reports/Common/FrequenceyMenu";
	public String SPOG_ANALYZE_REPORTS_COMMON_FREQUENCYITEMS="/SPOG/Analyze/Reports/Common/FrequenceyItems";
	public String SPOG_ANALYZE_REPORTS_COMMON_CREATEBTN="/SPOG/Analyze/Reports/Common/CreateBtn";
	public String SPOG_ANALYZE_REPORTS_COMMON_SEARCHBTN ="/SPOG/Analyze/Reports/Common/SeacrhcBtn";
	public String SPOG_ANALYZE_REPORTS_COMMON_DATARANGEFILTER="/SPOG/Analyze/Reports/Common/DataRangeFilter";

	public String SPOG_ANALYZE_REPORTS_COMMON_SEARCH= "/SPOG/Analyze/Reports/Common/Search";
	public String SPOG_ANALYZE_REPORTS_COMMON_CLEARALL= "/SPOG/Analyze/Reports/Analyze/BackupJobs/Clearall";
	public String SPOG_ANALYZE_REPORTS_COMMON_SAVESEARCHBTN= "/SPOG/Analyze/Reports/Common/SaveSearchBtn";
	public String SPOG_ANALYZE_REPORTS_COMMONCLEARALLBTN= "/SPOG/Analyze/Reports/Common/ClearallBtn";
	public String  SPOG_ANALYZE_REPORTS_COMMON_SAVESEARCHNAMEFD="/SPOG/Analyze/Reports/Common/SaveSearchNamefd";
	public String  SPOG_ANALYZE_REPORTS_COMMON_CANCELSEARCHNAMEBTN= "/Analyze/SPOG/Reports/Common/CancleSearchNameBtn";
	public String  SPOG_ANALYZE_REPORTS_COMMON_SAVESEARCHNAMEBTN= "/SPOG/Analyze/Reports/Common/SaveSeacrhNameBtn";

	public String  SPOG_ANALYZE_REPORTS_COMMON_MANAGEDSAVESEARCHBTN= "/SPOG/Analyze/Reports/Common/ManagedsaveSearchBtn";
	public String  SPOG_ANALYZE_REPORTS_COMMON_MANAGEDSAVESEARCHS= "/SPOG/Analyze/Reports/Common/MangedSaveSearchs";



	//Custom Reports creation 
	public String  SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_CALENDERBTN= "/SPOG/Analyze/Reports/Common/Calendar/CalendarBtn";
	public String  SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_ALLCALENDARS= "/SPOG/Analyze/Reports/Common/Calendar/AllCalendars";


	public String  SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_LEFTCALENDARYEARBTN= "/SPOG/Analyze/Reports/Common/Calendar/LeftCalendarYearBtn";
	public String  SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_LEFTCALENDARAVAILABLEYEARS= "/SPOG/Analyze/Reports/Common/Calendar/LeftCalendarAvailableYears";
	public String  SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_LEFTCALSELECTEDYEAR= "/SPOG/Analyze/Reports/Common/Calendar/LeftCalSelectedYear";
	public String  SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_LEFTCALENDARMONTHBTN= "/SPOG/Analyze/Reports/Common/Calendar/LeftCalendarMonthBtn";
	public String  SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_LEFTCALENDARAVAILMONTHS= "/SPOG/Analyze/Reports/Common/Calendar/LeftCalenderAvailMonths";
	public String  SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_LEFTCALSELECTEDMONTH= "/SPOG/Analyze/Reports/Common/Calendar/LeftCalSelectedMonth";
	public String  SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_LEFTCALAVAILDATES= "/SPOG/Analyze/Reports/Common/Calendar/LeftCalAvailDates";



	public String  SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_RIGHTCALYEARBTN= "/SPOG/Analyze/Reports/Common/Calendar/RightCalYearBtn";
	public String  SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_RIGHTCALAVAILYEARS= "/SPOG/Analyze/Reports/Common/Calendar/RightCalAvailYears";
	public String  SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_RIGHTCALMONTHBTN= "/SPOG/Analyze/Reports/Common/Calendar/RightCalMonthBtn";
	public String  SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_RIGHTCALENDARAVAILMONTHS= "/SPOG/Analyze/Reports/Common/Calendar/RightCalenderAvailMonths";
	public String  SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_RIGHTCALSELECTEDMONTH= "/SPOG/Analyze/Reports/Common/Calendar/RightCalSelectedMonth";
	public String  SPOG_ANALYZE_REPORTS_COMMON_CALENDAR_RIGHTCALAVAILDATES= "/SPOG/Analyze/Reports/Common/Calendar/RightCalAvailDates";





	public String  SPOG_ANALYZE_REPORTS_COMMON_APPLYBTN= "/SPOG/Analyze/Reports/Common/ApplyBtn";
	public String  SPOG_ANALYZE_REPORTS_COMMON_CANCELBTN= "/SPOG/Analyze/Reports/Common/CancelBtn";

	public String SPOG_ANALYZE_REPORTS_COMMON_SELECTARROW= "/SPOG/Analyze/Reports/Common/Selectarrow";

	//Backup job reports  @Nagamalleswari
	public String SPOG_ANALYZE_REPORTS_BACKUPJOBS="/SPOG/Analyze/Reports/BackupJobs";
	public String SPOG_ANALYZE_REPORTS_BACKUPJOBREPORTS_TITLE="/SPOG/Analyze/Reports/BackupJobReports/Title";
	public String SPOG_ANALYZE_REPORTS_BACKUPJOBS_TOP10SOURCES_FILTER="/SPOG/Analyze/Reports/BackupJobReports/top10sources/filter";
	public String SPOG_ANALYZE_REPORTS_BACKUPJOBS_TOP10SOURCES_FILTEROPTINS="/SPOG/Analyze/Reports/BackupJobReports/top10sources/filterOptions";



	//RecoveryJob Reports @Nagamalleswari

	public String SPOG_ANALYZE_REPORTS_RECOVERYJOBREPORTS_TITLE="/SPOG/Analyze/Reports/RecoveryJobReports/Title";

	//DataTranfer Reports @Naga Malleswari
	public String SPOG_ANALYZE_REPORTS_DATATRANSFERREPORTS_TITLE="/SPOG/Analyze/Reports/DataTransferReports/Title";
	public String SPOG_ANALYZE_REPORTS_CAPACITYUSAGEREPORTS_TITLE="/SPOG/Analyze/Reports/CapacityUsageReports/Title";

	//Managed ReportSchedules @Nagamalleswari
	public String  SPOG_ANALYZE_REPORTS_MANAGEDREPORTSCHEDULES= "/SPOG/Analyze/Reports/ManagedReportSchedules";
	public String  SPOG_ANALYZE_REPORTS_MANAGEDREPORTSCHEDULES_DELETEREPORTSCHEDULETITLE= "/SPOG/Analyze/Reports/ManagedReportSchedules/DeleteReportScheduleTitle";
	public String  SPOG_ANALYZE_REPORTS_MANAGEDREPORTSCHEDULES_DELETEREPORTINSTANCE= "/SPOG/Analyze/Reports/ManagedReportSchedules/DeleteReportInstance";



	public String  SPOG_ANALYZE_REPORTS_BACKUPJOBS_MANAGEDREPORTSCHEDULES_SEARCHTAB= "/SPOG/Analyze/Reports/ManagedReportSchedules/SerachTab";
	public String  SPOG_ANALYZE_REPORTS_BACKUPJOBS_MANAGEDREPORTSCHEDULES_SCHEDULEFOR= "/SPOG/Analyze/Reports/ManagedReportSchedules/ScheduleFor";
	public String  SPOG_ANALYZE_REPORTS_BACKUPJOBS_MANAGEDREPORTSCHEDULES_CLEARSCHEDULEFOR= "/SPOG/Analyze/Reports/ManagedReportSchedules/ClearScheduleFor";
	public String  SPOG_ANALYZE_REPORTS_BACKUPJOBS_MANAGEDREPORTSCHEDULES_SCHEDULEMENU= "/SPOG/Analyze/Reports/ManagedReportSchedules/SelcetsScheduleMenu";
	public String  SPOG_ANALYZE_REPORTS_BACKUPJOBS_MANAGEDREPORTSCHEDULES_REPORTTYPE= "/SPOG/Analyze/Reports/ManagedReportSchedules/ReportTye";
	public String  SPOG_ANALYZE_REPORTS_BACKUPJOBS_MANAGEDREPORTSCHEDULES_SEARCHBTN= "/SPOG/Analyze/Reports/ManagedReportSchedules/SearchBtn";
	public String  SPOG_ANALYZE_REPORTS_BACKUPJOBS_MANAGEDREPORTSCHEDULES_CLEARALLBTN= "/SPOG/Analyze/Reports/ManagedReportSchedules/ClearallBtn";
	public String  SPOG_ANALYZE_REPORTS_BACKUPJOBS_MANAGEDREPORTSCHEDULES_CLEARSEACRCHFILTER= "/SPOG/Analyze/Reports/ManagedReportSchedules/ClearSearchFilter";


	//Entitlements @Nagamalleswari.Sykam
	public String SPOG_CONFIGURE_ENTITLEMENTS="/SPOG/Configure/Entitlements";
	public String SPOG_CONFIGURE_ENTITLEMENTS_ACTIVATENEWORDER="/SPOG/Configure/Entitlements/ActivateNewOrder";
	public String SPOG_CONFIGURE_ENTITLEMENTS_ORDERID="/SPOG/Configure/Entitlements/OrderId";
	public String SPOG_CONFIGURE_ENTITLEMENTS_FULFILLMENTNUMBER="/SPOG/Configure/Entitlements/FullFilMentNumber";
	public String SPOG_CONFIGURE_ENTITLEMENTS_ACTIVATE="/SPOG/Configure/Entitlements/Activate";
	public String SPOG_CONFIGURE_ENTITLEMENTS_CANCEL="/SPOG/Configure/Entitlements/Cancel";
	public String SPOG_CONFIGURE_ENTITLEMENTS_CLOSEACTIVATEORDERMENU="/SPOG/Configure/Entitlements/CloseActiavteOrderMenu";
	public String SPOG_CONFIGURE_ENTITLEMENTS_SUMMARYDIV="/SPOG/Configure/Entitlements/SummaryDiv";
	public String SPOG_CONFIGURE_ENTITLEMENTS_CONTENTDIV="/SPOG/Configure/Entitlements/ContentDiv";
	public String SPOG_CONFIGURE_ENTITLEMENTS_INFORMATIONDIV="/SPOG/Configure/Entitlements/InformationDiv";


	//SourceGroups @Nagamalleswari.Sykam
	public String SPOG_CONFIGURE_SOURCEGROUPS="/SPOG/Configure/SourceGroups";
	public String SPOG_CONFIGURE_SOURCEGROUPS_CREATESOURCEGROUPBTN="/SPOG/Configure/SourceGroups/CreateSourceGroupBtn";
	public String SPOG_CONFIGURE_SOURCEGROUPS_SOUCREGROUPNAME="/SPOG/Configure/SourceGroups/SourceGroupName";
	public String SPOG_CONFIGURE_SOURCEGROUPS_CREATE="/SPOG/Configure/SourceGroups/Create";
	public String SPOG_CONFIGURE_SOURCEGROUPS_CANCEL="/SPOG/Configure/SourceGroups/Cancel";
	public String SPOG_CONFIGURE_SOURCEGROUPS_DELETE="/SPOG/Configure/SourceGroups/DeleteSoucregroup";
	public String SPOG_CONFIGURE_SOURCEGROUPS_SEARCHINPUT="/SPOG/Configure/SourceGroups/SearchInput";
	public String SPOG_CONFIGURE_SOURCEGROUPS_SEARCHICON="/SPOG/Configure/SourceGroups/SearchIcon";
	public String SPOG_CONFIGURE_SOURCEGROUPS_CLEARALL="/SPOG/Configure/SourceGroups/Clearall";

	public String SPOG_CONFIGURE_SOURCEGROUPS_CLICKONSOURCEGROUPNAME="/SPOG/Configure/SourceGroups/ClickonSoucreGroupName";
	public String SPOG_CONFIGURE_SOURCEGROUPS_ADDSOURCESTOGROUP="/SPOG/Configure/SourceGroups/AddSoucrestoGroup";
	public String SPOG_CONFIGURE_SOURCEGROUPS_ADDSELECTEDSOURCES="/SPOG/Configure/SourceGroups/AddSelectedSources";
	public String SPOG_CONFIGURE_SOURCEGROUPS_SELECTALLSOURCES="/SPOG/Configure/SourceGroups/SelectAllSources";
	public String SPOG_CONFIGURE_SOURCEGROUPS_BULKACTIONS="/SPOG/Configure/SourceGroups/BulkActions";
	public String SPOG_CONFIGURE_SOURCEGROUPS_REMOVESOURCESFORMGROUP="/SPOG/Configure/SourceGroups/RemoveSourcesFromGroup";


	//RolesPage @Nagamalleswari.Sykam
	public String SPOG_CONFIGURE_ROLES_FIRSTCOLUMNNAME ="/SPOG/Configure/Roles/Firstcolumnname";
	public String SPOG_CONFIGURE_ROLES_SECONDCOLUMNNAME ="/SPOG/Configure/Roles/SecondColumname";
	public String SPOG_CONFIGURE_ROLES_THIRDCOLUMNNAME ="/SPOG/Configure/Roles/ThirdColumnname";
	public String SPOG_CONFIGURE_ROLES_TABLEBODY="/SPOG/Configure/Roles/TableBody";
	public String SPOG_CONFIGURE_ROLES_CLICKONEXPANDER="/SPOG/Configure/Roles/CickonExpander";
	public String SPOG_CONFIGURE_ROLES_DIRECTADMINROLE="/SPOG/Configure/Roles/DirectAdminRole";
	public String SPOG_CONFIGURE_ROLES_ADMINUSERCOUNT="/SPOG/Configure/Roles/AdminUserCount";
	public String SPOG_CONFIGURE_ROLES_ADMINROLEDESCRIPTION="/SPOG/Configure/Roles/AdminRoleDescription";
	public String SPOG_CONFIGURE_ROLES_MSPADMINROLE="/SPOG/Configure/Roles/MspadminRole";
	public String SPOG_CONFIGURE_ROLES_MSPADMINUSERCOUNT="/SPOG/Configure/Roles/MspAdminUserCount";
	public String SPOG_CONFIGURE_ROLES_MSPADMINDESCRIPTION="/SPOG/Configure/Roles/MspadminRoleDescription";
	public String SPOG_CONFIGURE_ROLES_MSPACCOUNTADMINROLE="/SPOG/Configure/Roles/MSPaccountadminrole";
	public String SPOG_CONFIGURE_ROLES_MSPACCOUNTADMINUSERCOUNT="/SPOG/Configure/Roles/MspaccountadminUserCount";
	public String SPOG_CONFIGURE_ROLES_MSPACCOUNTADMINDESCRIPTION="/SPOG/Configure/Roles/MspAccountadminDescription";


	public String SPOG_CONFIGURE_ROLES ="/SPOG/Configure/Roles";
	public String SPOG_CONFIGURE_ROLES_ROLESTABLE_ROLESHEADERDETAILS_ROLE="/SPOG/Configure/Roles/RolesTable/RolesHeaderDetails/Role";
	public String SPOG_CONFIGURE_ROLES_ROLESTABLE_ROLESHEADERDETAILS_NOOFUSERS="/SPOG/Configure/Roles/RolesTable/RolesHeaderDetails/NoOfUsers";
	public String SPOG_CONFIGURE_ROLES_ROLESTABLE_ROLESHEADERDETAILS_DESCRIPTION="/SPOG/Configure/Roles/RolesTable/RolesHeaderDetails/Description";


	//generic
	public String SPOG_ALERTDIV = "/SPOG/Common/AlertDiv";
	public String SPOG_COMMON_FORM = "/SPOG/Common/Form";
	public String SPOG_COMMON_FORM_CANCEL = "/SPOG/Common/Form/Cancel";
	public String SPOG_COMMON_FORM_SAVE = "/SPOG/Common/Form/Save";
	public String SPOG_COMMON_CANCEL = "/SPOG/Common/Cancel";
	public String SPOG_COMMON_SAVE = "/SPOG/Common/Save";
	public String SPOG_COMMON_TOASTMESSAGE = "/SPOG/Common/ToastMessage";
	public String SPOG_COMMON_TOASTMESSAGE_CLOSE = "/SPOG/Common/ToastMessage/Close";
	public String SPOG_COMMON_CLEARALLFILTERS = "/SPOG/Common/ClearAllFilters";
	public String SPOG_COMMON_TABLE_HEADERS = "/SPOG/Common/Table/Headers";
	public String SPOG_COMMON_TOTALNUMBER = "/SPOG/Common/Totalnumber";
	public String SPOG_COMMON_TABLE_ROWS = "/SPOG/Common/Table/Rows";
	public String SPOG_COMMON_TABLE_COLUMNS = "/SPOG/Common/Table/Columns";
	public String SPOG_COMMON_TABLE_BODY = "/SPOG/Common/Table/Body";
	public String SPOG_COMMON_CONTEXTUALACTIONS = "/SPOG/Common/ContextualActions";
	public String SPOG_COMMON_CONTEXTUALACTIONS_STARTBACKUP = "/SPOG/Common/ContextualActions/StartBackup";
	public String SPOG_COMMON_CONTEXTUALACTIONS_CANCELBACKUP = "/SPOG/Common/ContextualActions/CancelBackup";
	public String SPOG_COMMON_CONTEXTUALACTIONS_STARTRECOVERY = "/SPOG/Common/ContextualActions/StartRecovery";
	public String SPOG_COMMON_CONTEXTUALACTIONS_ASSIGNPOLICY = "/SPOG/Common/ContextualActions/AssignPolicy";
	public String SPOG_COMMON_CONTEXTUALACTIONS_REMOVEPOLICY = "/SPOG/Common/ContextualActions/RemovePolicy";
	public String SPOG_COMMON_CONTEXTUALACTIONS_DEPLOYPOLICY = "/SPOG/Common/ContextualActions/DeployPolicy";
	public String SPOG_COMMON_SEARCHINPUT = "/SPOG/Common/SearchInput";
	public String SPOG_COMMON_SEARCHDROPDOWN = "/SPOG/Common/SearchDropDown";
	public String SPOG_COMMON_SEARCHICON = "/SPOG/Common/SearchIcon";
	public String SPOG_COMMON_SELECTFILTERDIV = "/SPOG/Common/SelectFilterDiv";
	public String SPOG_COMMON_SEARCHBTN = "/SPOG/Common/SearchBtn";
	public String SPOG_COMMON_SAVEDSEARCHESDIV = "/SPOG/Common/SavedSearchesDiv";
	public String SPOG_COMMON_SAVEDSEARCHESLABEL = "/SPOG/Common/SavedSearchesLabel";
	public String SPOG_COMMON_SEARCHRESULTSFORDIV = "/SPOG/Common/SearchResultsForDiv";
	public String SPOG_COMMON_SEARCHRESULTSFOR_SPAN = "/SPOG/Common/SearchResultsForDiv/Span";
	public String SPOG_COMMON_CLEARALL = "/SPOG/Common/ClearAll";
	public String SPOG_COMMON_SAVESEARCH = "/SPOG/Common/SaveSearch";
	public String SPOG_COMMON_SAVESEARCH_TITLE = "/SPOG/Common/SaveSearch/Title";
	public String SPOG_COMMON_SAVESEARCH_NAMEFIELD = "/SPOG/Common/SaveSearch/NameField";
	public String SPOG_COMMON_SAVESEARCH_SAVEBTN = "/SPOG/Common/SaveSearch/SaveSearchBtn";
	public String SPOG_COMMON_SETTINGSBTN = "/SPOG/Common/SettingsBtn";
	public String SPOG_COMMON_MANAGESAVEDSEARCHES = "/SPOG/Common/ManageSavedSearches";
	public String SPOG_COMMON_MANAGESAVEDSEARCHES_TITLE = "/SPOG/Common/ManageSavedSearches/Title";
	public String SPOG_COMMON_MANAGESAVEDSEARCHES_BODYDIV = "/SPOG/Common/ManageSavedSearches/BodyDiv";
	public String SPOG_COMMON_MANAGESAVEDSEARCHES_SEARCHSTRING = "/SPOG/Common/ManageSavedSearches/SearchString";
	public String SPOG_COMMON_MANAGESAVEDSEARCHES_DELETE = "/SPOG/Common/ManageSavedSearches/Delete";
	public String SPOG_COMMON_MANAGESAVEDSEARCHES_MAKEDEFAULTDIV = "/SPOG/Common/ManageSavedSearches/MakeDefaultDiv";
	public String SPOG_COMMON_COLUMNOPTIONSDIV = "/SPOG/Common/ColumnOptionsDiv";
	public String SPOG_COMMON_COLUMNLABELS = "/SPOG/Common/ColumnLabels";
	public String SPOG_COMMON_DRAGDROPHANDLE = "/SPOG/Common/DragDropHandle";
	public String SPOG_COMMON_SELECTEDROWSLABEL = "/SPOG/Common/SelectedRowsLabel";

	public String SPOG_COMMON_PAGINATION_PAGINATIONBUTTON = "/SPOG/Common/Pagination/PaginationButton";
	public String SPOG_COMMON_PAGINATION_PREVIOUSBUTTON = "/SPOG/Common/Pagination/PreviousButton";
	public String SPOG_COMMON_PAGINATION_NEXTBUTTON = "/SPOG/Common/Pagination/NextButton";
	public String SPOG_COMMON_PAGINATION_FIRSTBUTTON = "/SPOG/Common/Pagination/FirstButton";
	public String SPOG_COMMON_PAGINATION_LASTBUTTON = "/SPOG/Common/Pagination/LastButton";
	public String SPOG_COMMON_PAGINATION_ROWSPERPAGE = "/SPOG/Common/Pagination/RowsPerPage";
	public String SPOG_COMMON_PAGINATION_ROWSPERPAGELABEL = "/SPOG/Common/Pagination/RowsPerPageLabel";
	public String SPOG_COMMON_PAGINATION_ACTIVEBUTTON = "/SPOG/Common/Pagination/ActiveButton";
	public String SPOG_COMMON_PAGINATION_TOTALROWS = "/SPOG/Common/Pagination/TotalRows";
	public String SPOG_COMMON_PAGINATION_MENU = "/SPOG/Common/Pagination/PaginationMenu";

	public String SPOG_COMMON_SEARCHRESULTFOR_MSG = "/SPOG/Common/SearchResultsFor";
	public String SPOG_COMMON_ACTIONS_BUTTON = "/SPOG/Common/ActionsButton";
	public String SPOG_COMMON_ACTIONS_BUTTON_NO_ACTIONS_SUPPORT = "/SPOG/Common/ActionsButton/NoActionsSupport";
	public String SPOG_COMMON_BULK_ACTION_CHECKBOX = "/SPOG/Common/BulkActionCheckBox";
	public String SPOG_COMMON_SELECT_ALL_CHECKBOXES = "/SPOG/Common/SelectAllCheckBoxes";
	public String SPOG_COMMON_SELECT_ELEMENT_CHECKBOX="/SPOG/Common/SelectElementCheckBox";
	public String SPOG_COMMON_SELECTEDCOUNTLABEL = "/SPOG/Common/SelectedCountLabel";
	public String SPOG_COMMON_BREAD_CRUMB = "/SPOG/Common/BreadCrumb";

	public String SPOG_COMMON_FILTER_MENU = "/SPOG/Common/FilterMenu";
	public String  SPOG_COMMON_DELETE = "/SPOG/Common/Delete"; 

	public String SPOG_COMMON_CONFIRM = "/SPOG/Common/Confirm";
	public String SPOG_COMMON_CLOSEDIALOG = "/SPOG/Common/CloseDialog";
	public String SPOG_COMMON_GRIDHEADERDIV = "/SPOG/Common/GridHeaderDiv";
	public String SPOG_COMMON_DIALOGTITLE = "/SPOG/Common/DialogTitle";

	public String SPOG_COMMON_ERROR_REQUIRED = "/SPOG/Common/ErrorMessage/Required";
	public String SPOG_COMMON_ERROR_MINLENGTH = "/SPOG/Common/ErrorMessage/MinLength";
	public String SPOG_COMMON_ERROR_MAXLENGTH = "/SPOG/Common/ErrorMessage/MaxLength";
	public String SPOG_COMMON_CLOSEMENU = "/SPOG/Common/CloseMenu";
	public String  SPOG_COMMON_UPDATEPHOTO ="/SPOG/Common/UpdatePhoto";
	public String SPOG_COMMON_REQUIREDERROR="/SPOG/Common/RequiredError";
	public String SPOG_COMMON_MINLENTGHERROR="/SPOG/Common/MinLengthError";
	public String SPOG_COMMON_EXCEEDORGNAMEERROR="/SPOG/Common/ExceedOrgNameError";

	public String SPOG_COMMON_RECOVERYPOINTS="/SPOG/Common/RecoveryPoints";
	public String SPOG_COMMON_RECOVERYWIZARD="/SPOG/Common/RecoveryWizard";
	public String SPOG_COMMON_RECOVERYWIZARD_IMAGEFORMAT="/SPOG/Common/RecoveryWizard/ImageFormat";
	public String SPOG_COMMON_RECOVERYWIZARD_SELECT="/SPOG/Common/RecoveryWizard/Select";	
	public String SPOG_COMMON_RECOVERYWIZARD_DESTINATIONPATH="/SPOG/Common/RecoveryWizard/DestinationPath";
	public String SPOG_COMMON_RECOVERYWIZARD_BROWSERECOVERYPOINT="/SPOG/Common/RecoveryWizard/BrowseRecoveryPoint";
	public String SPOG_COMMON_RECOVERYWIZARD_IMGTABLE="/SPOG/Common/RecoveryWizard/ImgTable";	
	public String SPOG_COMMON_RECOVERYWIZARD_IMGCONTENT="/SPOG/Common/RecoveryWizard/ImgContent";	
	public String SPOG_COMMON_RECOVERYWIZARD_RESTOREUSINGCLOUDDIRECTAGENT="/SPOG/Common/RecoveryWizard/RestoreUsingCloudDirectAgent";	
	public String SPOG_COMMON_RECOVERYWIZARD_NEXTBUTTON="/SPOG/Common/RecoveryWizard/NextButton";
	public String SPOG_COMMON_RECOVERYWIZARD_CANCELBUTTON="/SPOG/Common/RecoveryWizard/CancelButton";
	public String SPOG_COMMON_RECOVERYWIZARD_RECOVERONTHEORIGINALSOURCEMACHINE="/SPOG/Common/RecoveryWizard/RecoverOnTheOriginalSourceMachine";
	public String SPOG_COMMON_RECOVERYWIZARD_RECOVERONTOANOTHERMACHINE="/SPOG/Common/RecoveryWizard/RecoverOnToAnotherMachine";
	public String SPOG_COMMON_RECOVERYWIZARD_RESTOREBUTTON="/SPOG/Common/RecoveryWizard/RestoreButton";



}
