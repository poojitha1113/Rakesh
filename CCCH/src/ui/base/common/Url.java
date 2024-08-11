package ui.base.common;

public class Url {
	public static final String baseURL = /*"https://tcc.arcserve.com";*/ "";
	
	public static final String login = baseURL+"/login";
	
	public static final String protect = baseURL+"/protect/sources/all";
	public static final String sources = baseURL+"/protect/sources/all";
	public static final String machines = baseURL+"/protect/sources/physical_machines";
	public static final String AgentlessVMs = baseURL+"/protect/sources/virtual_machines";
	public static final String RecoveredVMs = baseURL+"/protect/recovered_resources/recovered_vms";
	public static final String destinations = baseURL+"/protect/destinations/all";
	public static final String cloudDirectVolumes = baseURL+"/protect/destinations/cloud_volumes";
	public static final String cloudHybridStores = baseURL+"/protect/destinations/cloud_stores";
	public static final String policies = baseURL+"/protect/policies/all";
	public static final String customerAccounts = baseURL+"/protect/customer-accounts/all";
	
	public static final String analyze = baseURL+"/analyze/jobs/all";
	public static final String jobs = baseURL+"/analyze/jobs/all";
	public static final String logs = baseURL+"/analyze/log/all";
	public static final String reports = baseURL+"/analyze/reports/all";
	public static final String backupJobReports = baseURL+"/analyze/reports/backup_jobs";
	public static final String recoveryJobReports = baseURL+"/analyze/reports/restore_jobs";
	public static final String dataTransferReports = baseURL+"/analyze/reports/data_transfer";
	public static final String capacityUsageReports = baseURL+"/analyze/reports/capacity_usage";
	public static final String manageReportSchedules = baseURL+"/analyze/reports/manage_schedules";
	
	public static final String userAccounts = baseURL+"/configure/access_control/user_accounts";
	
}
