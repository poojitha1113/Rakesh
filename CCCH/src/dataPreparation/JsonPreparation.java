package dataPreparation;

import static org.testng.Assert.assertNotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Pattern;

import org.testng.internal.EclipseInterface;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.LogColumnConstants;
import Constants.ProtectionStatus;
import Constants.SourceColumnConstants;
import Constants.SourceProduct;
import Constants.SourceType;

public class JsonPreparation {

	HashMap<String, HashMap<String, Object>> job;
	HashMap<String, Object> actualjobDataInfo;
	static public HashMap<String, Object> destinationInfo = new HashMap<String, Object>();

	/**
	 * the function is used to compose user information HashMap for login
	 * 
	 * @author shuo.zhang
	 * @param userName
	 * @param password
	 * @return the HashMap for login user information
	 */
	public Map<String, String> getUserInfo(String userName, String password) {

		Map<String, String> userInfo = new HashMap<>();
		userInfo.put("username", userName);
		userInfo.put("password", password);
		return userInfo;
	}

	/**
	 * the function is used to compose organization information HashMap for creation
	 * 
	 * @author jing.shan
	 * @param organizationName
	 * @param organizationType
	 * @param organizationEmail
	 * @param organizationPwd
	 * @param organizationFirstName
	 * @param organizationLastName
	 * @return the HashMap for creation organization
	 */
	public Map<String, Object> getOrganizationInfo(String organization_name, String organization_type,
			String organizationEmail, String organizationPwd, String organizationFirstName,
			String organizationLastName) {
		Map<String, Object> organizationInfo = new HashMap<>();
		String prefix="AUTO_";
		if(organization_name!=null&&organization_name!=""){
			organization_name=prefix+organization_name;
		}
		organizationInfo.put("organization_name", organization_name);
		organizationInfo.put("organization_type", organization_type);
		organizationInfo.put("email", organizationEmail);
		organizationInfo.put("password", organizationPwd);
		organizationInfo.put("first_name", organizationFirstName);
		organizationInfo.put("last_name", organizationLastName);
		return organizationInfo;
	}
	
	/**
	 * the function is used to compose organization information HashMap for creation
	 * 
	 * @author jing.shan
	 * @param organizationName
	 * @param organizationType
	 * @param organizationEmail
	 * @param organizationPwd
	 * @param organizationFirstName
	 * @param organizationLastName
	 * @return the HashMap for creation organization
	 */
	public Map<String, Object> getOrganizationInfo(String organization_name, String organization_type,
			String organizationEmail, String organizationPwd, String organizationFirstName,
			String organizationLastName, boolean blocked) {
		Map<String, Object> organizationInfo = new HashMap<>();
		String prefix="AUTO_";
		if(organization_name!=null&&organization_name!=""){
			organization_name=prefix+organization_name;
		}
		organizationInfo.put("organization_name", organization_name);
		organizationInfo.put("organization_type", organization_type);
		organizationInfo.put("email", organizationEmail);
		organizationInfo.put("password", organizationPwd);
		organizationInfo.put("first_name", organizationFirstName);
		organizationInfo.put("last_name", organizationLastName);
		organizationInfo.put("blocked", blocked);
		return organizationInfo;
	}

	/**
	 * the function is used to compose threshold for update
	 * 
	 * @author jing.shan
	 * @param cloud_direct_volume
	 * @param cloud_hybrid_store
	 * @return the HashMap for update threshold
	 */
	public Map<String, String> getThresholdInfo(String cloud_direct_volume, String cloud_hybrid_store) {
		Map<String, String> thresholdInfo = new HashMap<>();
		thresholdInfo.put("cloud_direct_volume_capacity", cloud_direct_volume);
		thresholdInfo.put("cloud_hybrid_store_capacity", cloud_hybrid_store);
		return thresholdInfo;
	}

	/**
	 * the function is used to compose account information HashMap for creation
	 * 
	 * @author jing.shan
	 * @param accountName
	 * @param parentId
	 * @return the HashMap for creation account
	 */
	public Map<String, String> getAccountInfo(String accountName, String parentId) {
		Map<String, String> accountInfo = new HashMap<>();
		accountInfo.put("organization_name", accountName);
		accountInfo.put("parent_id", parentId);
		return accountInfo;
	}

	/**
	 * the function is used to compose account information HashMap for update
	 * 
	 * @author jing.shan
	 * @param accountName
	 * @return the HashMap for update account
	 */
	public Map<String, String> getUpdateAccountInfo(String accountName) {
		Map<String, String> accountInfo = new HashMap<>();
		accountInfo.put("organization_name", accountName);
		return accountInfo;
	}

	/**
	 * the function is used to compose user information HashMap for create user
	 * 
	 * @author shuo.zhang
	 * @param email:
	 *            created user's email
	 * @param password:
	 *            created user's password
	 * @param first_name:
	 *            created user's first_name
	 * @param last_name:
	 *            created user's last_name
	 * @param role_id:
	 *            created user's role_id please refer to SpogConstants
	 *            DIRECT_ADMIN/MSP_ADMIN/CSR_ADMIN
	 * @param organization_id:
	 *            created user's organization_id. this is optional
	 * @return the HashMap for create user information
	 */
	public Map<String, String> getUserInfo(String email, String password, String first_name, String last_name,
			String role_id, String organization_id) {
		// TODO Auto-generated method stub

		Map<String, String> userInfo = new HashMap<>();
		userInfo.put("email", email);
		userInfo.put("password", password);
		userInfo.put("first_name", first_name);
		userInfo.put("last_name", last_name);
		userInfo.put("role_id", role_id);
		if ((organization_id != null) && (!organization_id.equals(""))) {
			userInfo.put("organization_id", organization_id);
		}

		return userInfo;
	}

	/**
	 * the function is used to update user information HashMap
	 * 
	 * @author liu.yuefen
	 * @param first_name:
	 *            update first name
	 * @param last_name:
	 *            update last nmae
	 * @param password:
	 *            update password
	 */
	public Map<String, String> updateUserInfo(String email, String password, String first_name, String last_name,
			String role_id, String organization_id) {

		Map<String, String> updateUserInfo = new HashMap<>();
		if ((email != null) && (!email.equals(""))) {
			updateUserInfo.put("email", email);
		}
		if ((password != null) && (!password.equals(""))) {
			updateUserInfo.put("password", password);
		}
		if ((first_name != null) && (!first_name.equals(""))) {
			updateUserInfo.put("first_name", first_name);
		}
		if ((last_name != null) && (!last_name.equals(""))) {
			updateUserInfo.put("last_name", last_name);
		}
		if ((role_id != null) && (!role_id.equals(""))) {
			updateUserInfo.put("role_id", role_id);
		}
		if ((organization_id != null) && (!organization_id.equals(""))) {
			updateUserInfo.put("organization_id", organization_id);
		}

		return updateUserInfo;
	}

	public HashMap<String, String> updateUserInfo(String email, String password, String first_name, String last_name,
			String role_id, String organization_id, String phone_number) {

		HashMap<String, String> updateUserInfo = new HashMap<>();
		if ((email != null) && (!email.equals(""))) {
			updateUserInfo.put("email", email);
		}
		if ((password != null) && (!password.equals(""))) {
			updateUserInfo.put("password", password);
		}
		if ((first_name != null) && (!first_name.equals(""))) {
			updateUserInfo.put("first_name", first_name);
		}
		if ((last_name != null) && (!last_name.equals(""))) {
			updateUserInfo.put("last_name", last_name);
		}
		if ((role_id != null) && (!role_id.equals(""))) {
			updateUserInfo.put("role_id", role_id);
		}
		if ((organization_id != null) && (!organization_id.equals(""))) {
			updateUserInfo.put("organization_id", organization_id);
		}
		if ((phone_number != null) && (!phone_number.equals(""))) {
			updateUserInfo.put("phone_number", phone_number);
		}
		return updateUserInfo;
	}

	/**
	 * the function is used to change user's password HashMap
	 * 
	 * @author liu.yuefen
	 * @param oldPassword
	 * @param newPassword
	 */
	public Map<String, String> passwordInfo(String old_password, String new_password) {

		Map<String, String> passwordInfo = new HashMap<>();
		passwordInfo.put("old_password", old_password);
		passwordInfo.put("new_password", new_password);

		return passwordInfo;
	}

	public Map<String, String> updateOrganizationInfo(String organization_name) {

		Map<String, String> updateOrgInfo = new HashMap<>();
		if ((organization_name != null) && (!organization_name.equals(""))) {
			updateOrgInfo.put("organization_name", organization_name);
		}

		return updateOrgInfo;
	}
	
	public Map<String, String> updateOrganizationInfo(String organization_name, String blocked) {

		Map<String, String> updateOrgInfo = new HashMap<>();
		if (organization_name == null) {
			updateOrgInfo.put("organization_name", organization_name);
		} else if (!organization_name.equalsIgnoreCase("none")) {
			updateOrgInfo.put("organization_name", organization_name);
		}
		
		if (blocked == null) {
			updateOrgInfo.put("blocked", blocked);
		} else if (!blocked.equalsIgnoreCase("none")) {
			updateOrgInfo.put("blocked", blocked);
		}

		return updateOrgInfo;
	}
	
	/**
	 * This function is used to update org block status
	 * 
	 * @author shan.jing
	 * @param blocked
	 * @return updateOrgInfo
	 */
	public Map<String, Object> updateOrganizationBlockStatus(boolean blocked) {

		Map<String, Object> updateOrgInfo = new HashMap<>();
		updateOrgInfo.put("blocked", blocked);
		return updateOrgInfo;
	}

	/**
	 * This function is used to compose site information HashMap for login
	 * 
	 * @author BharadwajReddy
	 * @param siteName
	 * @param siteType(cloud_direct,gateway)
	 * @param organizationId
	 * @return HashMap for created siteInfo
	 */
	public Map<String, String> composeSiteInfoMap(String siteName, String siteType, String organizationId) {

		Map<String, String> siteInfoMap = new HashMap<>();
		siteInfoMap.put("site_name", siteName);
		siteInfoMap.put("site_type", siteType);
		if (organizationId != "") {
			siteInfoMap.put("organization_id", organizationId);
		}

		return siteInfoMap;

	}

	/**
	 * This function is used to compose Register site Information HashMap of Login
	 * 
	 * @author BharadwajReddy
	 * @param site_registration_key
	 * @param gateway_id
	 * @param gateway
	 *            host_name
	 * @param site_version
	 */
	public Map<String, String> composeSiteRegisterInfoMap(String site_registration_key, String gateway_id,
			String gateway_hostname, String site_version) {

		Map<String, String> siteRegInfoMap = new HashMap<>();
		siteRegInfoMap.put("site_registration_key", site_registration_key);
		siteRegInfoMap.put("gateway_id", gateway_id);
		siteRegInfoMap.put("gateway_hostname", gateway_hostname);
		siteRegInfoMap.put("site_version", site_version);
		return siteRegInfoMap;

	}

	/**
	 * This function is used to compose Information for site login
	 * 
	 * @author KiranSripada
	 * @param site_id
	 * @param secret_key
	 */
	public Map<String, String> composeSiteLoginInfoMap(String site_id, String secret_key, String gateway_id) {

		Map<String, String> siteLoginInfoMap = new HashMap<>();
		siteLoginInfoMap.put("site_id", site_id);
		siteLoginInfoMap.put("site_secret", secret_key);
		siteLoginInfoMap.put("gateway_id", gateway_id);
		return siteLoginInfoMap;

	}

	public Map<String, String> composeSiteLoginInfoMap(String site_secret) {
		Map<String, String> siteLoginInfoMap = new HashMap<>();
		siteLoginInfoMap.put("site_secret", site_secret);
		return siteLoginInfoMap;
	}

	/**
	 * This function is used to compose job info for post
	 * 
	 * @author Zhaoguo.Ma
	 * @param job_id
	 * @param start_time_ts
	 * @param server_id
	 * @param resource_id
	 * @param rps_id
	 * @param datastore_id
	 * @param organization_id
	 * @param plan_id
	 * @param message_job_id
	 * @param message_job_type
	 * @param message_job_method
	 * @param message_resource_id
	 * @param message_organization_id
	 * @param message_server_id
	 * @param message_rps_id
	 * @param message_datastore_id
	 * @param message_plan_id
	 * @param message_start_time_ts
	 * @param message_end_time_ts
	 * @return
	 */
	public Map<String, Object> composeJobInfoMap(String site_id, String job_id, long start_time_ts, String server_id,
			String resource_id, String rps_id, String datastore_id, String organization_id, String plan_id,
			String message_job_id, String message_job_type, String message_job_method, String message_job_status,
			String message_resource_id, String message_organization_id, String message_server_id, String message_rps_id,
			String message_datastore_id, String message_plan_id, long message_start_time_ts, long message_end_time_ts) {
		Map<String, Object> jobInfo = new HashMap<>();
		Map<String, Object> messageInfo = new HashMap<>();
		// kiran - Added hash of hash which will be helpful for my test validation
		HashMap<String, HashMap<String, Object>> deep = new HashMap<String, HashMap<String, Object>>();

		messageInfo.put("job_id", message_job_id);
		messageInfo.put("job_type", message_job_type);
		messageInfo.put("job_method", message_job_method);
		messageInfo.put("job_status", message_job_status);
		messageInfo.put("source_id", message_resource_id);
		messageInfo.put("organization_id", message_organization_id);
		messageInfo.put("server_id", message_server_id);
		messageInfo.put("rps_id", message_rps_id);
		messageInfo.put("datastore_id", message_datastore_id);
		messageInfo.put("plan_id", message_plan_id);
		messageInfo.put("start_time_ts", message_start_time_ts);
		messageInfo.put("end_time_ts", message_end_time_ts);

		jobInfo.put("site_id", site_id);
		jobInfo.put("job_id", job_id);
		jobInfo.put("start_time_ts", start_time_ts);
		jobInfo.put("server_id", server_id);
		jobInfo.put("resource_id", resource_id);
		jobInfo.put("rps_id", rps_id);
		jobInfo.put("datastore_id", datastore_id);
		jobInfo.put("organization_id", organization_id);
		jobInfo.put("plan_id", plan_id);
		// kiran - Added the jobs info to the hash
		deep.put("jobs", (HashMap<String, Object>) jobInfo);
		jobInfo.put("message", messageInfo);
		// kiran - Added the message info to the hash
		deep.put("message", (HashMap<String, Object>) messageInfo);
		setjobsInfo(deep);
		return jobInfo;
	}

	public void setjobsInfo(HashMap<String, HashMap<String, Object>> jobdata) {

		job = jobdata;
		System.out.println(" The actual data is " + job);
	}

	public HashMap<String, HashMap<String, Object>> getjobsInfo() {
		return job;
	}

	public HashMap<String, String> composeJobDataInfoMap(String job_seq, String severity, String percent_complete,
			String protected_data_size, String raw_data_size, String sync_read_size, String ntfs_volume_size,
			String virtual_disk_provision_size) {
		HashMap<String, String> jobDataInfo = new HashMap<>();

		if (!"none".equalsIgnoreCase(job_seq)) {
			jobDataInfo.put("job_seq", job_seq);
		}
		if (!"none".equalsIgnoreCase(severity)) {
			jobDataInfo.put("severity", severity);
		}
		if (!"none".equalsIgnoreCase(percent_complete)) {
			jobDataInfo.put("percent_complete", percent_complete);
		}

		if (!"none".equalsIgnoreCase(protected_data_size)) {
			jobDataInfo.put("protected_data_size", protected_data_size);
		}
		if (!"none".equalsIgnoreCase(raw_data_size)) {
			jobDataInfo.put("raw_data_size", raw_data_size);
		}
		if (!"none".equalsIgnoreCase(sync_read_size)) {
			jobDataInfo.put("sync_read_size", sync_read_size);
		}
		if (!"none".equalsIgnoreCase(ntfs_volume_size)) {
			jobDataInfo.put("ntfs_volume_size", ntfs_volume_size);
		}
		if (!"none".equalsIgnoreCase(virtual_disk_provision_size)) {
			jobDataInfo.put("virtual_disk_provision_size", virtual_disk_provision_size);
		}

		return jobDataInfo;
	}

	public HashMap<String, Object> composeJobDataInfoMap(String job_seq, String severity, Double percent_complete,
			String protected_data_size, String raw_data_size, String sync_read_size, String ntfs_volume_size,
			String virtual_disk_provision_size) {
		HashMap<String, Object> jobDataInfo = new HashMap<>();

		if (!"none".equalsIgnoreCase(job_seq)) {
			jobDataInfo.put("job_seq", job_seq);
		}
		if (!"none".equalsIgnoreCase(severity)) {
			jobDataInfo.put("job_severity", severity);
		}
		jobDataInfo.put("percent_complete", percent_complete);
		if (!"none".equalsIgnoreCase(protected_data_size)) {
			jobDataInfo.put("protected_data_size", protected_data_size);
		}
		if (!"none".equalsIgnoreCase(raw_data_size)) {
			jobDataInfo.put("raw_data_size", raw_data_size);
		}
		if (!"none".equalsIgnoreCase(sync_read_size)) {
			jobDataInfo.put("sync_read_size", sync_read_size);
		}
		if (!"none".equalsIgnoreCase(ntfs_volume_size)) {
			jobDataInfo.put("ntfs_volume_size", ntfs_volume_size);
		}
		if (!"none".equalsIgnoreCase(virtual_disk_provision_size)) {
			jobDataInfo.put("virtual_disk_provision_size", virtual_disk_provision_size);
		}

		return jobDataInfo;
	}

	public HashMap<String, String> composeJobDataInfoMap(String job_id, String job_seq, String job_type,
			String job_method, String job_status,

			String end_time_ts, String protected_data_size, String raw_data_size, String sync_read_size,
			String ntfs_volume_size, String virtual_disk_provision_size) {
		HashMap<String, String> jobDataInfo = new HashMap<>();
		if (!"none".equalsIgnoreCase(job_id)) {
			jobDataInfo.put("job_id", job_id);
		}
		if (!"none".equalsIgnoreCase(job_seq)) {
			jobDataInfo.put("job_seq", job_seq);
		}
		if (!"none".equalsIgnoreCase(job_type)) {
			jobDataInfo.put("job_type", job_type);
		}
		if (!"none".equalsIgnoreCase(job_method)) {
			jobDataInfo.put("job_method", job_method);
		}
		if (!"none".equalsIgnoreCase(job_status)) {
			jobDataInfo.put("job_status", job_status);
		}
		if (!"none".equalsIgnoreCase(end_time_ts)) {
			jobDataInfo.put("end_time_ts", end_time_ts);
		}
		if (!"none".equalsIgnoreCase(protected_data_size)) {
			jobDataInfo.put("protected_data_size", protected_data_size);
		}
		if (!"none".equalsIgnoreCase(raw_data_size)) {
			jobDataInfo.put("raw_data_size", raw_data_size);
		}
		if (!"none".equalsIgnoreCase(sync_read_size)) {
			jobDataInfo.put("sync_read_size", sync_read_size);
		}
		if (!"none".equalsIgnoreCase(ntfs_volume_size)) {
			jobDataInfo.put("ntfs_volume_size", ntfs_volume_size);
		}
		if (!"none".equalsIgnoreCase(virtual_disk_provision_size)) {
			jobDataInfo.put("virtual_disk_provision_size", virtual_disk_provision_size);
		}

		// kiran - Storing the user inputs to hash for validation
		// setjobdata((HashMap<String, Object>) jobDataInfo);

		return jobDataInfo;
	}

	public HashMap<String, String> composeJobDataInfoMap(String job_id, String job_seq, String severity,
			String percent_complete, String protected_data_size, String raw_data_size, String sync_read_size,
			String ntfs_volume_size, String virtual_disk_provision_size,

			// String bucket_id,
			String error_count, String warning_count, String processed_bytes_changed, String processed_bytes_processed,
			String processed_directories, String processed_files, String transferred_bytes,
			String transferred_directories, String transferred_files, String transferred_uncompressed_bytes) {
		HashMap<String, String> jobDataInfo = new HashMap<>();
		// if (!"none".equalsIgnoreCase(job_id)) {
		// jobDataInfo.put("job_id", job_id);
		// }
		if (!"none".equalsIgnoreCase(job_seq)) {
			jobDataInfo.put("job_seq", job_seq);
		}
		if (!"none".equalsIgnoreCase(severity)) {
			jobDataInfo.put("severity", severity);
		}
		if (!"none".equalsIgnoreCase(percent_complete)) {
			jobDataInfo.put("percent_complete", percent_complete);
		}

		if (!"none".equalsIgnoreCase(protected_data_size)) {
			jobDataInfo.put("protected_data_size", protected_data_size);
		}
		if (!"none".equalsIgnoreCase(raw_data_size)) {
			jobDataInfo.put("raw_data_size", raw_data_size);
		}
		if (!"none".equalsIgnoreCase(sync_read_size)) {
			jobDataInfo.put("sync_read_size", sync_read_size);
		}
		if (!"none".equalsIgnoreCase(ntfs_volume_size)) {
			jobDataInfo.put("ntfs_volume_size", ntfs_volume_size);
		}
		if (!"none".equalsIgnoreCase(virtual_disk_provision_size)) {
			jobDataInfo.put("virtual_disk_provision_size", virtual_disk_provision_size);
		}

		// if (!"none".equalsIgnoreCase(bucket_id)) {
		// jobDataInfo.put("bucket_id", bucket_id);
		// }
		if (!"none".equalsIgnoreCase(error_count)) {
			jobDataInfo.put("error_count", error_count);
		}
		if (!"none".equalsIgnoreCase(warning_count)) {
			jobDataInfo.put("warning_count", warning_count);
		}
		if (!"none".equalsIgnoreCase(processed_bytes_changed)) {
			jobDataInfo.put("processed_bytes_changed", processed_bytes_changed);
		}
		if (!"none".equalsIgnoreCase(processed_bytes_processed)) {
			jobDataInfo.put("processed_bytes_processed", processed_bytes_processed);
		}
		if (!"none".equalsIgnoreCase(processed_directories)) {
			jobDataInfo.put("processed_directories", processed_directories);
		}
		if (!"none".equalsIgnoreCase(processed_files)) {
			jobDataInfo.put("processed_files", processed_files);
		}
		if (!"none".equalsIgnoreCase(transferred_bytes)) {
			jobDataInfo.put("transferred_bytes", transferred_bytes);
		}
		if (!"none".equalsIgnoreCase(transferred_directories)) {
			jobDataInfo.put("transferred_directories", transferred_directories);
		}
		if (!"none".equalsIgnoreCase(transferred_files)) {
			jobDataInfo.put("transferred_files", transferred_files);
		}
		if (!"none".equalsIgnoreCase(transferred_uncompressed_bytes)) {
			jobDataInfo.put("transferred_uncompressed_bytes", transferred_uncompressed_bytes);
		}

		return jobDataInfo;
	}

	public void setjobdata(HashMap<String, Object> jobdata) {

		actualjobDataInfo = jobdata;
	}

	public HashMap<String, Object> getjobdata() {
		return actualjobDataInfo;
	}

	/**
	 * the function is used to compose group information HashMap
	 * 
	 * @author yuefen.liu
	 * @param organization_id
	 * @param group_name
	 * @param group_description
	 * @return the HashMap for creation account
	 */
	public Map<String, String> groupInfo(String organization_id, String group_name, String group_description) {
		Map<String, String> groupInfo = new HashMap<>();
		if ((organization_id != null) && (!organization_id.equals(""))) {
			groupInfo.put("organization_id", organization_id);
		}
		groupInfo.put("group_name", group_name);
		if ((group_description != null) && (!group_description.equals(""))) {
			groupInfo.put("group_description", group_description);
		}

		return groupInfo;
	}

	/**
	 * @author shuo.zhang
	 * @param source_name
	 * @param source_type
	 * @param source_product
	 * @param organization_id
	 * @param site_id
	 * @param protection_status
	 * @param connection_status
	 * @param os_major
	 * @param application
	 * @return
	 */
	public Map<String, Object> getSourceInfo(String source_name, String source_type, String source_product,
			String organization_id, String site_id, String protection_status, String connection_status,
			// String os_major, String[] application, String create_user_id){
			String os_major, String[] application) {

		HashMap<String, Object> sourceInfo = new HashMap<>();
		sourceInfo.put("source_name", source_name);
		sourceInfo.put("source_type", source_type);
		sourceInfo.put("source_product", source_product);
		sourceInfo.put("organization_id", organization_id);
		sourceInfo.put("site_id", site_id);
		sourceInfo.put("protection_status", protection_status);
		sourceInfo.put("connection_status", connection_status);
		sourceInfo.put("os_major", os_major);
		sourceInfo.put("applications", application);
		// sourceInfo.put("create_user_id", create_user_id);

		/*
		 * HashMap<String,Object> agentInfo = new HashMap<>(); agentInfo.put("vm_name",
		 * ""); agentInfo.put("hypervisor_id", ""); agentInfo.put("agent_name", "");
		 * agentInfo.put("agent_current_version", "");
		 * agentInfo.put("agent_upgrade_version", "");
		 * agentInfo.put("agent_upgrade_link", ""); agentInfo.put("os_name", "");
		 * agentInfo.put("os_architecture", ""); sourceInfo.put("agent", agentInfo);
		 */

		return sourceInfo;
	}

	/**
	 * used to compose filter info
	 * 
	 * @author Zhaoguo.Ma
	 * @param filter_name,
	 *            String
	 * @param protection_status,
	 *            String[]
	 * @param connection_status,
	 *            String[]
	 * @param protection_policy,
	 *            String[]
	 * @param backup_status,
	 *            String[]
	 * @param source_group,
	 *            String[]
	 * @param operating_system,
	 *            String[]
	 * @param applications,
	 *            String[]
	 * @return
	 */
	public Map<String, Object> composeFilterInfo(String filter_name, String protection_status, String connection_status,
			String policy_id, String last_backup_status, String group_id, String os_major) {
		Map<String, Object> filterInfo = new HashMap<String, Object>();
		String[] emptyArray = new String[0];

		if (filter_name == null) {
			filterInfo.put("filter_name", filter_name);
		} else if (!filter_name.equalsIgnoreCase("none")) {
			filterInfo.put("filter_name", filter_name);
		}

		if (protection_status == null || protection_status == "") {
			filterInfo.put(SourceColumnConstants.PROTECTION_STATUS_FILTER, protection_status);
		} else if (protection_status.equalsIgnoreCase("emptyarray")) {
			filterInfo.put(SourceColumnConstants.PROTECTION_STATUS_FILTER, emptyArray);
		} else if (!protection_status.equalsIgnoreCase("none")) {
			filterInfo.put(SourceColumnConstants.PROTECTION_STATUS_FILTER,
					protection_status.replace(" ", "").split(","));
		}

		if (connection_status == null || connection_status == "") {
			filterInfo.put(SourceColumnConstants.CONNECTION_FILTER, connection_status);
		} else if (connection_status.equalsIgnoreCase("emptyarray")) {
			filterInfo.put(SourceColumnConstants.CONNECTION_FILTER, emptyArray);
		} else if (!connection_status.equalsIgnoreCase("none")) {
			filterInfo.put(SourceColumnConstants.CONNECTION_FILTER, connection_status.replace(" ", "").split(","));
		}

//		if (policy_id == null || policy_id == "") {
//			filterInfo.put(SourceColumnConstants.PROTECTION_POLICY_FILTER, policy_id);
//		} else if (policy_id.equalsIgnoreCase("emptyarray")) {
//			filterInfo.put(SourceColumnConstants.PROTECTION_POLICY_FILTER, emptyArray);
//		} else if (!policy_id.equalsIgnoreCase("none")) {
//			filterInfo.put(SourceColumnConstants.PROTECTION_POLICY_FILTER, policy_id.replace(" ", "").split(","));
//		}
		
		if (policy_id == null || policy_id == "") {
			filterInfo.put("policy_id", policy_id);
		} else if (policy_id.equalsIgnoreCase("emptyarray")) {
			filterInfo.put("policy_id", emptyArray);
		} else if (!policy_id.equalsIgnoreCase("none")) {
			filterInfo.put("policy_id", policy_id.replace(" ", "").split(","));
		}

		if (last_backup_status == null || last_backup_status == "") {
			filterInfo.put(SourceColumnConstants.LAST_BACKUP_STATUS_FILTER, last_backup_status);
		} else if (last_backup_status.equalsIgnoreCase("emptyarray")) {
			filterInfo.put(SourceColumnConstants.LAST_BACKUP_STATUS_FILTER, emptyArray);
		} else if (!last_backup_status.equalsIgnoreCase("none")) {
			filterInfo.put(SourceColumnConstants.LAST_BACKUP_STATUS_FILTER,
					last_backup_status.replace(" ", "").split(","));
		}

//		if (group_id == null || group_id == "") {
//			filterInfo.put(SourceColumnConstants.SOURCE_GROUP_FILTER, group_id);
//		} else if (group_id.equalsIgnoreCase("emptyarray")) {
//			filterInfo.put(SourceColumnConstants.SOURCE_GROUP_FILTER, emptyArray);
//		} else if (!group_id.equalsIgnoreCase("none")) {
//			filterInfo.put(SourceColumnConstants.SOURCE_GROUP_FILTER, group_id.replace(" ", "").split(","));
//		}
		
		if (group_id == null || group_id == "") {
			filterInfo.put("group_id", group_id);
		} else if (group_id.equalsIgnoreCase("emptyarray")) {
			filterInfo.put("group_id", emptyArray);
		} else if (!group_id.equalsIgnoreCase("none")) {
			filterInfo.put("group_id", group_id.replace(" ", "").split(","));
		}

		if (os_major == null || os_major == "") {
			filterInfo.put(SourceColumnConstants.OPERATING_SYSTEM_FILTER, os_major);
		} else if (os_major.equalsIgnoreCase("emptyarray")) {
			filterInfo.put(SourceColumnConstants.OPERATING_SYSTEM_FILTER, emptyArray);
		} else if (!os_major.equalsIgnoreCase("none")) {
			filterInfo.put(SourceColumnConstants.OPERATING_SYSTEM_FILTER, os_major.replace(" ", "").split(","));
		}

		return filterInfo;
	}

	/**
	 * used to compose filter info
	 * 
	 * @author Zhaoguo.Ma
	 * @param filter_name,
	 *            String
	 * @param protection_status,
	 *            String[]
	 * @param connection_status,
	 *            String[]
	 * @param protection_policy,
	 *            String[]
	 * @param backup_status,
	 *            String[]
	 * @param source_group,
	 *            String[]
	 * @param operating_system,
	 *            String[]
	 * @param applications,
	 *            String[]
	 * @return
	 */
	public Map<String, Object> composeFilterInfo(String filter_name, String protection_status, String connection_status,
			String policy_id, String last_backup_status, String group_id, String os_major, String source_type,
			String is_default) {
		Map<String, Object> filterInfo = composeFilterInfo(filter_name, protection_status, connection_status, policy_id,
				last_backup_status, group_id, os_major);

		if (is_default == null || is_default == "") {
			filterInfo.put("is_default", is_default);
		} else if (is_default.equalsIgnoreCase("true")) {
			filterInfo.put("is_default", true);
		} else if (is_default.equalsIgnoreCase("false")) {
			filterInfo.put("is_default", false);
		} else if (!is_default.equalsIgnoreCase("none")) {
			filterInfo.put("is_default", is_default);
		}

		if (is_default == null || is_default == "") {
			filterInfo.put(SourceColumnConstants.IS_DEFAULT, is_default);
		} else if (is_default.equalsIgnoreCase("true")) {
			filterInfo.put(SourceColumnConstants.IS_DEFAULT, true);
		} else if (is_default.equalsIgnoreCase("false")) {
			filterInfo.put(SourceColumnConstants.IS_DEFAULT, false);
		} else if (!is_default.equalsIgnoreCase("none")) {
			filterInfo.put(SourceColumnConstants.IS_DEFAULT, is_default);
		}

		if (!"none".equalsIgnoreCase(source_type)) {
			filterInfo.put(SourceColumnConstants.TYPE_FILTER, source_type);
		}
		return filterInfo;
	}

	/**
	 * @author Zhaoguo.Ma
	 * @param filter_name
	 * @param protection_status
	 * @param connection_status
	 * @param policy_id
	 * @param last_backup_status
	 * @param group_id
	 * @param os_major
	 * @param applications
	 * @param site_id
	 * @param source_name
	 * @param source_type
	 * @param is_default
	 * @return
	 */
	public Map<String, Object> composeFilterInfo(String filter_name, String protection_status, String connection_status,
			String policy_id, String last_backup_status, String group_id, String os_major, String site_id,
			String source_name, String source_type, String is_default) {
		Map<String, Object> filterInfo = composeFilterInfo(filter_name, protection_status, connection_status, policy_id,
				last_backup_status, group_id, os_major, source_type, is_default);
		String[] emptyArray = new String[0];

//		if (site_id == null || site_id == "") {
//			filterInfo.put(SourceColumnConstants.SOURCE_SITE_FILTER, site_id);
//		} else if (site_id.equalsIgnoreCase("emptyarray")) {
//			filterInfo.put(SourceColumnConstants.SOURCE_SITE_FILTER, emptyArray);
//		} else if (!site_id.equalsIgnoreCase("none")) {
//			filterInfo.put(SourceColumnConstants.SOURCE_SITE_FILTER, site_id.replace(" ", "").split(","));
//		}
		
		if (site_id == null || site_id == "") {
			filterInfo.put("site_id", site_id);
		} else if (site_id.equalsIgnoreCase("emptyarray")) {
			filterInfo.put("site_id", emptyArray);
		} else if (!site_id.equalsIgnoreCase("none")) {
			filterInfo.put("site_id", site_id.replace(" ", "").split(","));
		}

		if (!"none".equalsIgnoreCase(source_name)) {
			filterInfo.put(SourceColumnConstants.NAME_FILTER, source_name);
		}

		return filterInfo;
	}

	/**
	 * used to compose log info
	 * 
	 * @author shan.jing
	 * @param job_id,
	 *            String
	 * @param organization_id,
	 *            String
	 * @param log_severity_type,
	 *            String[]
	 * @param log_source_type,
	 *            String[]
	 * @param message_id,
	 *            String
	 * @param logMessageDataInfo
	 * @return
	 */
	public Map<String, Object> composeLogInfo(long log_generate_time, String log_id, String job_id, String organization_id,
			String source_id, String log_severity_type, String log_source_type, String message_id,
			String logMessageDataInfo) {
		Map<String, Object> logInfo = new HashMap<String, Object>();
		String[] emptyArray = new String[0];
		if (log_generate_time != -1) {
			logInfo.put("log_ts", log_generate_time);
		}
		if (job_id == null || job_id == "") {
			logInfo.put("job_id", job_id);
		} else if (!job_id.equalsIgnoreCase("none")) {
			logInfo.put("job_id", job_id);
		}
		if (log_id == null || log_id == "") {
			logInfo.put("log_id", log_id);
		} else if (!log_id.equalsIgnoreCase("none")) {
			logInfo.put("log_id", log_id);
		}
		
		if (organization_id == null || organization_id == "") {
			logInfo.put("organization_id", organization_id);
		} else if (!organization_id.equalsIgnoreCase("none")) {
			logInfo.put("organization_id", organization_id);
		}
		if (source_id == null || source_id == "") {
			logInfo.put("source_id", source_id);
		} else if (!source_id.equalsIgnoreCase("none")) {
			logInfo.put("source_id", source_id);
		}
		if (log_severity_type == null || log_severity_type == "") {
			logInfo.put("log_severity_type", log_severity_type);
		} else if (!log_severity_type.equalsIgnoreCase("none")) {
			logInfo.put("log_severity_type", log_severity_type);
		}
		if (log_source_type == null || log_source_type == "") {
			logInfo.put("log_source_type", log_source_type);
		} else if (!log_source_type.equalsIgnoreCase("none")) {
			logInfo.put("log_source_type", log_source_type);
		}
		if (!message_id.equalsIgnoreCase("none")) {
			logInfo.put("message_id", message_id);
		}
		if (logMessageDataInfo == null || logMessageDataInfo == "") {
			logInfo.put("message_data", logMessageDataInfo);
		} else if (logMessageDataInfo.equalsIgnoreCase("emptyarray")) {
			logInfo.put("message_data", emptyArray);
		} else if (!logMessageDataInfo.equalsIgnoreCase("none")) {
			logInfo.put("message_data", logMessageDataInfo.replace(" ", "").split(","));
		}
		return logInfo;
	}
	
	public HashMap<String, Object> composeLogInbulkInfo(long log_generate_time, String log_id,String job_id, String organization_id,
			String source_id, String log_severity_type, String log_source_type, String message_id,
			String logMessageDataInfo) {
		HashMap<String, Object> logInbulkInfo = new HashMap<String, Object>();
		String[] emptyArray = new String[0];
		if (log_generate_time != -1) {
			logInbulkInfo.put("log_ts", log_generate_time);
		}
		if (job_id == null || job_id == "") {
			logInbulkInfo.put("job_id", job_id);
		} else if (!job_id.equalsIgnoreCase("none")) {
			logInbulkInfo.put("job_id", job_id);
		}
		
		if (log_id == null || log_id == "") {
			logInbulkInfo.put("log_id", log_id);
		} else if (!log_id.equalsIgnoreCase("none")) {
			logInbulkInfo.put("log_id", log_id);
		}
		if (organization_id == null || organization_id == "") {
			logInbulkInfo.put("organization_id", organization_id);
		} else if (!organization_id.equalsIgnoreCase("none")) {
			logInbulkInfo.put("organization_id", organization_id);
		}
		if (source_id == null || source_id == "") {
			logInbulkInfo.put("source_id", source_id);
		} else if (!source_id.equalsIgnoreCase("none")) {
			logInbulkInfo.put("source_id", source_id);
		}
		if (log_severity_type == null || log_severity_type == "") {
			logInbulkInfo.put("log_severity_type", log_severity_type);
		} else if (!log_severity_type.equalsIgnoreCase("none")) {
			logInbulkInfo.put("log_severity_type", log_severity_type);
		}
		if (log_source_type == null || log_source_type == "") {
			logInbulkInfo.put("log_source_type", log_source_type);
		} else if (!log_source_type.equalsIgnoreCase("none")) {
			logInbulkInfo.put("log_source_type", log_source_type);
		}
		if (!message_id.equalsIgnoreCase("none")) {
			logInbulkInfo.put("message_id", message_id);
		}
		if (logMessageDataInfo == null || logMessageDataInfo == "") {
			logInbulkInfo.put("message_data", logMessageDataInfo);
		} else if (logMessageDataInfo.equalsIgnoreCase("emptyarray")) {
			logInbulkInfo.put("message_data", emptyArray);
		} else if (!logMessageDataInfo.equalsIgnoreCase("none")) {
			logInbulkInfo.put("message_data", logMessageDataInfo.replace(" ", "").split(","));
		}
		return logInbulkInfo;
	}

	public Map<String, Object> composeLogInfo(long log_generate_time, String log_id, String job_id, String organization_id,
			String source_id, String log_severity_type, String log_source_type, String message_id,
			String help_message_id, String logMessageDataInfo) {
		Map<String, Object> logInfo = composeLogInfo(log_generate_time, log_id, job_id, organization_id, source_id,
				log_severity_type, log_source_type, message_id, logMessageDataInfo);
		if (help_message_id == null || help_message_id == "") {
			logInfo.put("help_message_id", help_message_id);
		} else if (!help_message_id.equalsIgnoreCase("none")) {
			logInfo.put("help_message_id", help_message_id);
		}
		return logInfo;
	}

	public Map<String, Object> composeLogInfo(long log_generate_time,String log_id, String job_id, String organization_id,
			String source_id, String log_severity_type, String log_source_type, String message_id,
			String help_message_id, String logMessageDataInfo, String job_type) {
		Map<String, Object> logInfo = composeLogInfo(log_generate_time, log_id, job_id, organization_id, source_id,
				log_severity_type, log_source_type, message_id, help_message_id, logMessageDataInfo);
		if (job_type == null || job_type == "") {
			logInfo.put("job_type", job_type);
		} else if (!job_type.equalsIgnoreCase("none")) {
			logInfo.put("job_type", job_type);
		}
		return logInfo;
	}

	/**
	 * used to update site name
	 * 
	 * @author Kiran.Sripada
	 * @param newSiteName
	 * @return
	 */
	public Map<String, String> updatesitebyId(String newSiteName) {
		Map<String, String> updatesiteInfo = new HashMap<>();
		updatesiteInfo.put("site_name", newSiteName);
		return updatesiteInfo;
	}

	/**
	 * used to add sources to a group
	 * 
	 * @author Kiran.Sripada
	 * @param source_id
	 * @return
	 */
	public Map<String, Object> addsourcetogroupInfo(String[] source_Id) {

		ArrayList<Map<String, String>> list = new ArrayList<>();

		int len = source_Id.length;
		for (int i = 0; i < len; i++) {
			Map<String, String> addsourcetogroupInfo = new HashMap<>();
			addsourcetogroupInfo.put("source_id", source_Id[i]);
			list.add(addsourcetogroupInfo);
		}

		Map<String, Object> addsourcetogroup = new HashMap<>();
		addsourcetogroup.put("sources", list);
		return addsourcetogroup;
	}

	public Map<String, Object> updateSourceInfo(String source_id, String source_name, SourceType source_type,
			SourceProduct source_product, String organization_id, String site_id, String policy_id,
			ProtectionStatus protection_status, ConnectionStatus connection_status, String os_major) {

		HashMap<String, Object> sourceInfo = new HashMap<>();
		sourceInfo.put("source_id", source_id);
		sourceInfo.put("source_name", source_name);
		sourceInfo.put("source_type", source_type.name());
		sourceInfo.put("source_product", source_product.name());
		sourceInfo.put("organization_id", organization_id);
		sourceInfo.put("site_id", site_id);
		sourceInfo.put("policy_id", policy_id);
		sourceInfo.put("protection_status", protection_status.name());
		sourceInfo.put("connection_status", connection_status.name());
		sourceInfo.put("os_major", os_major);
		// sourceInfo.put("application", application);

		HashMap<String, Object> agentInfo = new HashMap<>();
		agentInfo.put("vm_name", "");
		agentInfo.put("hypervisor_id", "");
		agentInfo.put("agent_name", "");
		agentInfo.put("agent_current_version", "");
		agentInfo.put("agent_upgrade_version", "");
		agentInfo.put("agent_upgrade_link", "");
		// agentInfo.put("os_name", "");
		// agentInfo.put("os_architecture", "");
		sourceInfo.put("agent", agentInfo);

		HashMap<String, Object> osInfo = new HashMap<>();
		osInfo.put("os_type", "windows");
		osInfo.put("os_name", "Windows Server 2012R2 standard");
		sourceInfo.put("operating_system", osInfo);
		return sourceInfo;
	}

	public Map<String, String> composeSourceGroupInfo(String sourceGroupName, String sourceGroupDesc) {

		HashMap<String, String> sourceGroupInfo = new HashMap<>();
		sourceGroupInfo.put("group_name", sourceGroupName);
		sourceGroupInfo.put("group_description", sourceGroupDesc);

		return sourceGroupInfo;
	}
	
	/**
	 * @author Nagamalleswari.Sykam
	 * @param sourceGroupName
	 * @param sourceGroupDesc
	 * @return
	 */
	
	public HashMap<String, Object> composeSourceGroupsInfo(String sourceGroupName, String sourceGroupDesc) {

		HashMap<String, Object> sourceGroupInfo = new HashMap<>();
			sourceGroupInfo.put("group_name", sourceGroupName);
			sourceGroupInfo.put("group_description", sourceGroupDesc);
		return sourceGroupInfo;
	}
	


	public Map<String, String> composeCloudAccountInfo(String cloudAccountKey, String cloudAccountSecret,
			String cloudAccountName, String cloudAccountType, String organizationID) {
		Map<String, String> cloudAccountInfo = new HashMap<String, String>();
		if (!"none".equalsIgnoreCase(cloudAccountKey)) {
			cloudAccountInfo.put("cloud_account_key", cloudAccountKey);
		}
		if (!"none".equalsIgnoreCase(cloudAccountSecret)) {
			cloudAccountInfo.put("cloud_account_secret", cloudAccountSecret);
		}
		if (!"none".equalsIgnoreCase(cloudAccountName)) {
			cloudAccountInfo.put("cloud_account_name", cloudAccountName);
		}
		if (!"none".equalsIgnoreCase(cloudAccountType)) {
			cloudAccountInfo.put("cloud_account_type", cloudAccountType);
		}
		if (!"none".equalsIgnoreCase(organizationID)) {
			cloudAccountInfo.put("organization_id", organizationID);
		}

		return cloudAccountInfo;
	}

	public Map<String, String> composeCloudAccountInfo(String cloudAccountKey, String cloudAccountSecret,
			String cloudAccountName, String cloudAccountType, String organizationID, String orderID,
			String fulfillmentID) {
		Map<String, String> cloudAccountInfo = new HashMap<String, String>();
		if (!"none".equalsIgnoreCase(cloudAccountKey)) {
			cloudAccountInfo.put("cloud_account_key", cloudAccountKey);
		}
		if (!"none".equalsIgnoreCase(cloudAccountSecret)) {
			cloudAccountInfo.put("cloud_account_secret", cloudAccountSecret);
		}
		if (!"none".equalsIgnoreCase(cloudAccountName)) {
			cloudAccountInfo.put("cloud_account_name", cloudAccountName);
		}
		if (!"none".equalsIgnoreCase(cloudAccountType)) {
			cloudAccountInfo.put("cloud_account_type", cloudAccountType);
		}
		if (!"none".equalsIgnoreCase(organizationID)) {
			cloudAccountInfo.put("organization_id", organizationID);
		}
		if (!"none".equalsIgnoreCase(orderID)) {
			cloudAccountInfo.put("order_id", orderID);
		}
		if (!"none".equalsIgnoreCase(fulfillmentID)) {
			cloudAccountInfo.put("fulfillment_id", fulfillmentID);
		}

		return cloudAccountInfo;
	}

	// composeCloudAccountInfo(cloudAccountKey, cloudAccountSecret,
	// cloudAccountName, cloudAccountType, organizationID, orderID, fulfillmentID,
	// datacenter_id);

	public Map<String, String> composeCloudAccountInfo(String cloudAccountKey, String cloudAccountSecret,
			String cloudAccountName, String cloudAccountType, String organizationID, String orderID,
			String fulfillmentID, String datacenterID) {
		Map<String, String> cloudAccountInfo = new HashMap<String, String>();
		if (!"none".equalsIgnoreCase(cloudAccountKey)) {
			cloudAccountInfo.put("cloud_account_key", cloudAccountKey);
		}
		if (!"none".equalsIgnoreCase(cloudAccountSecret)) {
			cloudAccountInfo.put("cloud_account_secret", cloudAccountSecret);
		}
		if (!"none".equalsIgnoreCase(cloudAccountName)) {
			cloudAccountInfo.put("cloud_account_name", cloudAccountName);
		}
		if (!"none".equalsIgnoreCase(cloudAccountType)) {
			cloudAccountInfo.put("cloud_account_type", cloudAccountType);
		}
		if (!"none".equalsIgnoreCase(organizationID)) {
			cloudAccountInfo.put("organization_id", organizationID);
		}
		if (!"none".equalsIgnoreCase(orderID)) {
			cloudAccountInfo.put("order_id", orderID);
		}
		if (!"none".equalsIgnoreCase(fulfillmentID)) {
			cloudAccountInfo.put("fulfillment_id", fulfillmentID);
		}

		if (!"none".equalsIgnoreCase(datacenterID)) {
			cloudAccountInfo.put("datacenter_id", datacenterID);
		}

		return cloudAccountInfo;
	}

	public Map<String, String> composeCloudAccountInfo(String cloudAccountKey, String cloudAccountSecret,
			String cloudAccountName, String cloudAccountType, String organizationID, String orderID,
			String fulfillmentID, String enrollTicket, String datacenterID) {
		Map<String, String> cloudAccountInfo = new HashMap<String, String>();
		if (!"none".equalsIgnoreCase(cloudAccountKey)) {
			cloudAccountInfo.put("cloud_account_key", cloudAccountKey);
		}
		if (!"none".equalsIgnoreCase(cloudAccountSecret)) {
			cloudAccountInfo.put("cloud_account_secret", cloudAccountSecret);
		}
		if (!"none".equalsIgnoreCase(cloudAccountName)) {
			cloudAccountInfo.put("cloud_account_name", cloudAccountName);
		}
		if (!"none".equalsIgnoreCase(cloudAccountType)) {
			cloudAccountInfo.put("cloud_account_type", cloudAccountType);
		}
		if (!"none".equalsIgnoreCase(organizationID)) {
			cloudAccountInfo.put("organization_id", organizationID);
		}
		if (!"none".equalsIgnoreCase(orderID)) {
			cloudAccountInfo.put("order_id", orderID);
		}
		if (!"none".equalsIgnoreCase(fulfillmentID)) {
			cloudAccountInfo.put("fulfillment_id", fulfillmentID);
		}
		if (!"none".equalsIgnoreCase(enrollTicket)) {
			cloudAccountInfo.put("enroll_ticket", enrollTicket);
		}
		if (!"none".equalsIgnoreCase(datacenterID)) {
			cloudAccountInfo.put("datacenter_id", datacenterID);
		}

		return cloudAccountInfo;
	}

	/**
	 * create job message data, changed in sprint6;
	 * 
	 * @author Zhaoguo.Ma
	 * @param jobMessages
	 * @return
	 */
	public Map<String, String> composeJobMessageDataInfo(String... jobMessages) {
		Map<String, String> jobMessageDataInfo = new HashMap<String, String>();
		int length = jobMessages.length;
		if (length == 0) {
			return null;
		}
		for (int i = 0; i < length; i++) {
			jobMessageDataInfo.put(Integer.toString(i), jobMessages[i]);
		}
		return jobMessageDataInfo;
	}

	/**
	 * create log time range information
	 * 
	 * @author shan.jing
	 * @param logTimeRangeInfo
	 * @return
	 */
	public Map<String, Object> composeLogTimeRangeInfo(String type,long... timerange) {
		Map<String, Object> logTimeRangeInfo = new HashMap<String, Object>();
		logTimeRangeInfo.put("type", type);
		int length = timerange.length;
		if (length == 0) {
			return null;
		} else if (length >= 2) {
			logTimeRangeInfo.put("start_ts", timerange[0]);
			logTimeRangeInfo.put("end_ts", timerange[1]);
		}
		return logTimeRangeInfo;
	}

	/***
	 * create log filter info
	 * 
	 * @author shan.jing
	 * @param filter_name
	 * @param log_ts
	 * @param job_type
	 * @param log_severity_type
	 * @param is_default
	 * @param test
	 * @return
	 */
	public Map<String, Object> composeLogFilterInfo(String filter_name, Map<String, Object> log_ts, String job_type,
			String log_severity_type, String is_default, ExtentTest test) {
		Map<String, Object> logFilterInfo = new HashMap<>();
		if (null != log_ts) {
			logFilterInfo.put(LogColumnConstants.DATETIME_FILTER, log_ts);
		}
		if (!"none".equalsIgnoreCase(filter_name)) {
			logFilterInfo.put("filter_name", filter_name);
		}

		if (null == is_default) {
			logFilterInfo.put("is_default", is_default);
		} else if (!"none".equalsIgnoreCase(is_default)) {
			if (is_default.equalsIgnoreCase("true") || is_default.equalsIgnoreCase("false")) {
				logFilterInfo.put("is_default", Boolean.valueOf(is_default));
			} else {
				logFilterInfo.put("is_default", is_default);
			}
		}

		String[] emptyArray = new String[0];

		if (job_type == null || job_type == "" || job_type.equalsIgnoreCase("invalidString")) {
			logFilterInfo.put(LogColumnConstants.JOB_TYPE_FILTER, job_type);
		} else if (job_type.equalsIgnoreCase("emptyarray")) {
			logFilterInfo.put(LogColumnConstants.JOB_TYPE_FILTER, emptyArray);
		} else if (!job_type.equalsIgnoreCase("none")) {
			logFilterInfo.put(LogColumnConstants.JOB_TYPE_FILTER, job_type.replace(" ", "").split(","));
		}

		if (log_severity_type == null || log_severity_type == ""
				|| log_severity_type.equalsIgnoreCase("invalidString")) {
			logFilterInfo.put(LogColumnConstants.SERERITY_FILTER, log_severity_type);
		} else if (log_severity_type.equalsIgnoreCase("emptyarray")) {
			logFilterInfo.put(LogColumnConstants.SERERITY_FILTER, emptyArray);
		} else if (!log_severity_type.equalsIgnoreCase("none")) {
			logFilterInfo.put(LogColumnConstants.SERERITY_FILTER, log_severity_type.replace(" ", "").split(","));
		}

		return logFilterInfo;
	}

	/***
	 * create log filter info
	 * 
	 * @author shan.jing
	 * @param filter_name
	 * @param log_ts
	 * @param job_type
	 * @param log_severity_type
	 * @param is_default
	 * @param message_id
	 * @param source_id
	 * @param message
	 * @param source_name
	 * @param test
	 * @return
	 */
	public Map<String, Object> composeLogFilterInfo(String filter_name, Map<String, Object> log_ts, String job_type,
			String log_severity_type, String is_default, String message_id, String source_id, String message,
			String source_name, String origin, ExtentTest test) {
		Map<String, Object> logFilterInfo = new HashMap<>();
		if (!"none".equalsIgnoreCase(source_id)) {
			logFilterInfo.put("source_id", source_id);
		}
		if (!"none".equalsIgnoreCase(message)) {
			logFilterInfo.put("message", message);
		}
		if (!"none".equalsIgnoreCase(source_name)) {
			logFilterInfo.put("source_name", source_name);
		}
		if (!"none".equalsIgnoreCase(origin)) {
			logFilterInfo.put("origin", origin);
		}
		if (null != log_ts) {
			logFilterInfo.put(LogColumnConstants.DATETIME_FILTER, log_ts);
		}
		if (!"none".equalsIgnoreCase(filter_name)) {
			logFilterInfo.put("filter_name", filter_name);
		}

		if (null == is_default) {
			logFilterInfo.put("is_default", is_default);
		} else if (!"none".equalsIgnoreCase(is_default)) {
			if (is_default.equalsIgnoreCase("true") || is_default.equalsIgnoreCase("false")) {
				logFilterInfo.put("is_default", Boolean.valueOf(is_default));
			} else {
				logFilterInfo.put("is_default", is_default);
			}
		}

		String[] emptyArray = new String[0];

		if (message_id == null || message_id == "" || message_id.equalsIgnoreCase("invalidString")) {
			logFilterInfo.put(LogColumnConstants.MESSAGE_ID_FILTER, message_id);
		} else if (message_id.equalsIgnoreCase("emptyarray")) {
			logFilterInfo.put(LogColumnConstants.MESSAGE_ID_FILTER, emptyArray);
		} else if (!message_id.equalsIgnoreCase("none")) {
			logFilterInfo.put(LogColumnConstants.MESSAGE_ID_FILTER, message_id.replace(" ", "").split(","));
		}
		if (source_id == null || source_id == "" || source_id.equalsIgnoreCase("invalidString")) {
			logFilterInfo.put("source_id", source_id);
		} else if (source_id.equalsIgnoreCase("emptyarray")) {
			logFilterInfo.put("source_id", emptyArray);
		} else if (!source_id.equalsIgnoreCase("none")) {
			logFilterInfo.put("source_id", source_id.replace(" ", "").split(","));
		}
		if (job_type == null || job_type == "" || job_type.equalsIgnoreCase("invalidString")) {
			logFilterInfo.put(LogColumnConstants.JOB_TYPE_FILTER, job_type);
		} else if (job_type.equalsIgnoreCase("emptyarray")) {
			logFilterInfo.put(LogColumnConstants.JOB_TYPE_FILTER, emptyArray);
		} else if (!job_type.equalsIgnoreCase("none")) {
			logFilterInfo.put(LogColumnConstants.JOB_TYPE_FILTER, job_type.replace(" ", "").split(","));
		}

		if (log_severity_type == null || log_severity_type == ""
				|| log_severity_type.equalsIgnoreCase("invalidString")) {
			logFilterInfo.put(LogColumnConstants.SERERITY_FILTER, log_severity_type);
		} else if (log_severity_type.equalsIgnoreCase("emptyarray")) {
			logFilterInfo.put(LogColumnConstants.SERERITY_FILTER, emptyArray);
		} else if (!log_severity_type.equalsIgnoreCase("none")) {
			logFilterInfo.put(LogColumnConstants.SERERITY_FILTER, log_severity_type.replace(" ", "").split(","));
		}

		return logFilterInfo;
	}
	

	/**
	 * @author Nagamalleswari.Sykam
	 * @param filter_name
	 * @param log_ts
	 * @param log_severity_type
	 * @param is_default
	 * @param origin
	 * @param test
	 * @return
	 */
	public Map<String, Object> composeLogFiltersInfo(String filter_name, Map<String, Object> log_ts,
			String log_severity_type, String is_default,
			 ExtentTest test) {
		Map<String, Object> logFilterInfo = new HashMap<>();
		
		if (null != log_ts) {
			logFilterInfo.put(LogColumnConstants.DATETIME_FILTER, log_ts);
		}
		if (!"none".equalsIgnoreCase(filter_name)) {
			logFilterInfo.put("filter_name", filter_name);
		}

		if (null == is_default) {
			logFilterInfo.put("is_default", is_default);
		} else if (!"none".equalsIgnoreCase(is_default)) {
			if (is_default.equalsIgnoreCase("true") || is_default.equalsIgnoreCase("false")) {
				logFilterInfo.put("is_default", Boolean.valueOf(is_default));
			} else {
				logFilterInfo.put("is_default", is_default);
			}
		}

		String[] emptyArray = new String[0];

		if (log_severity_type == null || log_severity_type == ""
				|| log_severity_type.equalsIgnoreCase("invalidString")) {
			logFilterInfo.put(LogColumnConstants.SERERITY_FILTER, log_severity_type);
		} else if (log_severity_type.equalsIgnoreCase("emptyarray")) {
			logFilterInfo.put(LogColumnConstants.SERERITY_FILTER, emptyArray);
		} else if (!log_severity_type.equalsIgnoreCase("none")) {
			logFilterInfo.put(LogColumnConstants.SERERITY_FILTER, log_severity_type.replace(" ", "").split(","));
		}

		return logFilterInfo;
	}
	

	/***
	 * create job info, changed in sprint 6
	 * 
	 * @author Zhaoguo.Ma
	 * @param startTimeTS
	 * @param serverID
	 * @param resourceID
	 * @param rpsID
	 * @param datastoreID
	 * @param siteID
	 * @param organizationID
	 * @param policyID
	 * @param messageID
	 * @param jobMessageDataInfo
	 * @return
	 */
	public Map<String, Object> composeJobInfo(long startTimeTS, long endTimeTS, String organizationID, String serverID,
			String resourceID, String rpsID, String destinationID, String policyID, String jobType, String jobMethod,
			String jobStatus) {
		Map<String, Object> jobInfo = new HashMap<>();
		Map<String, Object> policyInfo = new HashMap<>();

		if (0 != startTimeTS) {
			jobInfo.put("start_time_ts", startTimeTS);
		}
		if (0 != endTimeTS) {
			jobInfo.put("end_time_ts", endTimeTS);
		}
		if (!"none".equalsIgnoreCase(serverID)) {
			jobInfo.put("server_id", serverID);
		}
		if (!"none".equalsIgnoreCase(resourceID)) {
			jobInfo.put("resource_id", resourceID);
			jobInfo.put("source_id", resourceID);
		}
		if (!"none".equalsIgnoreCase(rpsID)) {
			jobInfo.put("rps_id", rpsID);
		}
		if (!"none".equalsIgnoreCase(destinationID)) {
			jobInfo.put("destination_id", destinationID);
		}
		if (!"none".equalsIgnoreCase(organizationID)) {
			jobInfo.put("organization_id", organizationID);
		}
		if (!"none".equalsIgnoreCase(policyID)) {
		/*	policyInfo.put("policy_id", policyID);
			jobInfo.put("policy", policyInfo);*/
			jobInfo.put("policy_id", policyID);
		}
		if (!"none".equalsIgnoreCase(jobType)) {
			jobInfo.put("job_type", jobType);
		}
		if (!"none".equalsIgnoreCase(jobMethod)) {
			jobInfo.put("job_method", jobMethod);
		}
		if (!"none".equalsIgnoreCase(jobStatus)) {
			jobInfo.put("job_status", jobStatus);
		}

		return jobInfo;
	}

	public Map<String, Object> composeJobInfo(String jobID, long startTimeTS, long endTimeTS, String organizationID,
			String serverID, String resourceID, String rpsID, String destinationID, String policyID, String jobType,
			String jobMethod, String jobStatus) {
		Map<String, Object> jobInfo = new HashMap<>();
		Map<String, Object> policyInfo = new HashMap<>();

		if (!"none".equalsIgnoreCase(jobID)) {
			jobInfo.put("job_id", jobID);
		}

		if (0 != startTimeTS) {
			jobInfo.put("start_time_ts", startTimeTS);
		}
		if (0 != endTimeTS) {
			jobInfo.put("end_time_ts", endTimeTS);
		}
		if (!"none".equalsIgnoreCase(serverID)) {
			jobInfo.put("server_id", serverID);
		}
		if (!"none".equalsIgnoreCase(resourceID)) {
			jobInfo.put("source_id", resourceID);
		}
		if (!"none".equalsIgnoreCase(rpsID)) {
			jobInfo.put("rps_id", rpsID);
		}
		if (!"none".equalsIgnoreCase(destinationID)) {
			jobInfo.put("destination_id", destinationID);
		}
		if (!"none".equalsIgnoreCase(organizationID)) {
			jobInfo.put("organization_id", organizationID);
		}
		if (!"none".equalsIgnoreCase(policyID)) {
			policyInfo.put("policy_id", policyID);
			jobInfo.put("policy", policyInfo);
		}
		if (!"none".equalsIgnoreCase(jobType)) {
			jobInfo.put("job_type", jobType);
		}
		if (!"none".equalsIgnoreCase(jobMethod)) {
			jobInfo.put("job_method", jobMethod);
		}
		if (!"none".equalsIgnoreCase(jobStatus)) {
			jobInfo.put("job_status", jobStatus);
		}

		return jobInfo;
	}

	/**
	 * the function is used to compose cloud account information HashMap
	 * 
	 * @author yuefen.liu
	 * @param cloud_account_key
	 * @param cloud_account_secret
	 * @return the HashMap for login cloud account
	 */
	public Map<String, String> getCloudDirectAccountInfo(String cloudAccountKey, String cloudAccountSecret) {
		Map<String, String> cloudDirectAccountInfo = new HashMap<>();

		cloudDirectAccountInfo.put("cloud_account_key", cloudAccountKey);
		cloudDirectAccountInfo.put("cloud_account_secret", cloudAccountSecret);

		return cloudDirectAccountInfo;
	}

	/**
	 * TreeMap to compose the message data information
	 * 
	 * @author BhardwajReddy
	 * @param message
	 * @param test
	 * @return TreeMap
	 */
	public TreeMap<String, String> composeMessageData(String message, ExtentTest test) {
		test.log(LogStatus.INFO, "Preparing the request body for the message data");
		TreeMap<String, String> message_data = new TreeMap<String, String>();
		// TODO Auto-generated method stub
		String[] messages = message.split(";");
		for (int i = 0; i < messages.length; i++) {
			String[] messagedata = messages[i].split(",");
			message_data.put(messagedata[0], messagedata[1]);
		}
		return message_data;
	}

	/**
	 * This method is used to compose the LogInfo
	 * 
	 * @param job_id
	 * @param organization_id
	 * @param site_id
	 * @param severityType
	 * @param sourceTypes
	 * @param message_id
	 * @param message_data
	 * @param create_ts
	 * @param test
	 * @return logInfo
	 */

	public TreeMap<String, Object> composeLogInfo(long log_generate_time, String log_id, String job_id, String organization_id,
			String source_id, String site_id, String severityType, String sourceType, String message_id,
			Map<String, String> message_data, ExtentTest test) {
		test.log(LogStatus.INFO, "Preparing the request body for the post Logs");
		// TODO Auto-generated method stub
		TreeMap<String, Object> logInfo = new TreeMap<String, Object>();
		logInfo.put("log_ts", log_generate_time);
		logInfo.put("job_id", job_id);
		logInfo.put("log_id", log_id);
		logInfo.put("source_id", source_id);
		logInfo.put("organization_id", organization_id);
		logInfo.put("site_id", site_id);
		logInfo.put("log_severity_type", severityType);
		logInfo.put("log_source_type", sourceType);
		logInfo.put("message_id", message_id);
		logInfo.put("message_data", message_data);
		return logInfo;

	}

	public ArrayList<HashMap<String, String>> composedeletesourcefromsourcegroup(String source_Id) {
		ArrayList<HashMap<String, String>> deletesourceId = new ArrayList<>();
		String[] arrayofsources = source_Id.split(",");

		for (int i = 0; i < arrayofsources.length; i++) {
			HashMap<String, String> delete_sourceId = new HashMap<>();
			delete_sourceId.put("source_id", arrayofsources[i]);
			deletesourceId.add(delete_sourceId);
		}

		return deletesourceId;

	}

	/**
	 * compose the destination filter info
	 * 
	 * @author Zhaoguo.Ma
	 * @param filterName
	 * @param destinationName
	 * @param policyID
	 * @param destinationType
	 * @param isDefault
	 * @return
	 */
	public Map<String, Object> composeDestinationFilterInfo(String filterName, String destinationName, String policyID,
			String destinationType, String isDefault) {
		Map<String, Object> destinationFilterInfo = new HashMap<String, Object>();

		destinationFilterInfo.put("filter_name", filterName);
		if (!"none".equalsIgnoreCase(destinationName)) {
			destinationFilterInfo.put("destination_name", destinationName);
		}
		String[] emptyArray = new String[0];

		if (policyID == null || policyID == "" || policyID.equalsIgnoreCase("invalidString")) {
			destinationFilterInfo.put("policy_id", policyID);
		} else if (policyID.equalsIgnoreCase("emptyarray")) {
			destinationFilterInfo.put("policy_id", emptyArray);
		} else if (!policyID.equalsIgnoreCase("none")) {
			destinationFilterInfo.put("policy_id", policyID.replace(" ", "").split(","));
		}
		if (!"none".equalsIgnoreCase(destinationType)) {
			destinationFilterInfo.put("destination_type", destinationType);
		}
		if (null == isDefault) {
			destinationFilterInfo.put("is_default", isDefault);
		} else if (!"none".equalsIgnoreCase(isDefault)) {
			if (isDefault.equalsIgnoreCase("true") || isDefault.equalsIgnoreCase("false")) {
				destinationFilterInfo.put("is_default", Boolean.valueOf(isDefault));
			} else {
				destinationFilterInfo.put("is_default", isDefault);
			}
		}
		return destinationFilterInfo;
	}

	/**
	 * @author shuo.zhang
	 * @param vm_name
	 * @param hypervisor_id
	 * @param agent_name
	 * @param os_name
	 * @param os_architecture
	 * @param agent_current_version
	 * @param agent_upgrade_version
	 * @param agent_upgrade_link
	 * @return
	 */
	public Map<String, String> composeSourceAgentInfo(String vm_name, String hypervisor_id, String agent_name,
			String os_name, String os_architecture, String agent_current_version, String agent_upgrade_version,
			String agent_upgrade_link) {

		Map<String, String> sourceAgentInfo = new HashMap<String, String>();
		sourceAgentInfo.put("vm_name", vm_name);
		sourceAgentInfo.put("hypervisor_id", hypervisor_id);
		sourceAgentInfo.put("agent_name", agent_name);
		sourceAgentInfo.put("os_name", os_name);
		sourceAgentInfo.put("os_architecture", os_architecture);
		sourceAgentInfo.put("agent_current_version", agent_current_version);
		sourceAgentInfo.put("agent_upgrade_version", agent_upgrade_version);
		sourceAgentInfo.put("agent_upgrade_link", agent_upgrade_link);
		return sourceAgentInfo;
	}

	/**
	 * @author shuo.zhang
	 * @param source_name
	 * @param source_type
	 * @param source_product
	 * @param organization_id
	 * @param site_id
	 * @param protection_status
	 * @param connection_status
	 * @param os_major
	 * @param application
	 * @param agentInfo
	 * @return
	 */
	public Map<String, Object> getSourceInfo(String source_name, String source_type, String source_product,
			String organization_id, String site_id, String protection_status, String connection_status, String os_major,
			String[] application, Map<String, String> agentInfo) {

		HashMap<String, Object> sourceInfo = new HashMap<>();
		sourceInfo.put("source_name", source_name);
		sourceInfo.put("source_type", source_type);
		sourceInfo.put("source_product", source_product);
		sourceInfo.put("organization_id", organization_id);
		sourceInfo.put("site_id", site_id);
		sourceInfo.put("protection_status", protection_status);
		sourceInfo.put("connection_status", connection_status);
		sourceInfo.put("os_major", os_major);
		sourceInfo.put("applications", application);
		sourceInfo.put("agent", agentInfo);
		return sourceInfo;
	}

	/**
	 * @author leiyu.wang
	 * @param data_store_folder
	 * @param data_destination
	 * @param index_destination
	 * @param hash_destination
	 * @param concurrent_active_node
	 * @param is_deduplicated
	 * @param block_size
	 * @param hash_memory
	 * @param is_compressed
	 * @param encryption_password
	 * @param occupied_space
	 * @param store_data
	 * @param deduplication_rate
	 * @param compression_rate
	 * @return
	 */
	public Map<String, String> composeDestinationCloudDedupVolumeInfo(String concurrent_active_node, String is_deduplicated, String block_size, String is_compressed) {
		Map<String, String> cloudDedupVolumeInfo = new HashMap<String, String>();
		cloudDedupVolumeInfo.put("concurrent_active_node", concurrent_active_node);
		cloudDedupVolumeInfo.put("is_deduplicated", is_deduplicated);
		cloudDedupVolumeInfo.put("block_size", block_size);
		cloudDedupVolumeInfo.put("is_compressed", is_compressed);
		return cloudDedupVolumeInfo;
	}

	/**
	 * @author leiyu.wang
	 * @param age_hours_max
	 * @param age_four_hours_max
	 * @param age_days_max
	 * @param age_weeks_max
	 * @param age_months_max
	 * @param age_years_max
	 * @return
	 */
	public Map<String, String> composeCloudDirectVolumeRetentionInfo(String age_hours_max, String age_four_hours_max,
			String age_days_max, String age_weeks_max, String age_months_max, String age_years_max) {
		Map<String, String> cloudDirectVolumeRetentionInfo = new HashMap<String, String>();
		cloudDirectVolumeRetentionInfo.put("age_hours_max", age_hours_max);
		cloudDirectVolumeRetentionInfo.put("age_four_hours_max", age_four_hours_max);
		cloudDirectVolumeRetentionInfo.put("age_days_max", age_days_max);
		cloudDirectVolumeRetentionInfo.put("age_weeks_max", age_weeks_max);
		cloudDirectVolumeRetentionInfo.put("age_months_max", age_months_max);
		cloudDirectVolumeRetentionInfo.put("age_years_max", age_years_max);
		return cloudDirectVolumeRetentionInfo;
	}

	/**
	 * @author leiyu.wang
	 * @param primary_usage
	 * @param snapshot_usage
	 * @param total_usage
	 * @param volume_type
	 * @param hostname
	 * @param retention_id
	 * @param cloudDirectVolumeRetentionInfo
	 * @return
	 */
	public Map<String, Object> composeDestinationCloudDirectVolumeInfo(String cloud_account_id, String volume_type,
			String hostname, String retention_id, String retention_name,
			Map<String, String> cloudDirectVolumeRetentionInfo) {
		Map<String, Object> cloudDirectVolume = new HashMap<String, Object>();
		//cloudDirectVolume.put("cloud_account_id", cloud_account_id);
		cloudDirectVolume.put("volume_type", volume_type);
		cloudDirectVolume.put("hostname", hostname);
		cloudDirectVolume.put("retention_id", retention_id);
		cloudDirectVolume.put("retention_name", retention_name);
		cloudDirectVolume.put("retention", cloudDirectVolumeRetentionInfo);
		return cloudDirectVolume;
	}

	/**
	 * @author leiyu.wang
	 * @param organization_id
	 * @param site_id
	 * @param datacenter_id
	 * @param destination_type
	 * @param destination_status
	 * @param cloudDirectVolumeInfo
	 * @param cloudDedupVolumeInfo
	 * @return
	 */

	public Map<String, Object> getDestinationInfo(String destination_id, String organization_id, String site_id,
			String datacenter_id, String destination_type, String destination_status, String dedupe_savings,
			Map<String, Object> cloudDirectVolumeInfo, Map<String, String> cloud_hybrid_store,
			String destination_name) {
		Map<String, Object> destinationInfo = new HashMap<String, Object>();

		destinationInfo.put("destination_id", destination_id);
		destinationInfo.put("organization_id", organization_id);
		//destinationInfo.put("site_id", site_id);
		destinationInfo.put("datacenter_id", datacenter_id);
		destinationInfo.put("destination_type", destination_type);
		destinationInfo.put("destination_status", destination_status);
		destinationInfo.put("dedupe_savings", dedupe_savings);
		destinationInfo.put("cloud_direct_volume", cloudDirectVolumeInfo);
		destinationInfo.put("cloud_hybrid_store", cloud_hybrid_store);
		destinationInfo.put("destination_name", destination_name);
		return destinationInfo;
	}

	/**
	 * the function is used to compose order information HashMap
	 * 
	 * @author liu.yuefen
	 * @param orderID
	 * @param fulfillmentID
	 * @param organizationID
	 */
	public Map<String, String> composeOrderInfo(String orderID, String fulfillmentID, String organizationID) {

		Map<String, String> orderInfo = new HashMap<>();
		if ((orderID != null) && (!orderID.equals(""))) {
			orderInfo.put("order_id", orderID);
		}
		if ((fulfillmentID != null) && (!fulfillmentID.equals(""))) {
			orderInfo.put("fulfillment_id", fulfillmentID);
		}
		if ((organizationID != null) && (!organizationID.equals(""))) {
			orderInfo.put("organization_id", organizationID);
		}
		return orderInfo;
	}

	public Map<String, Object> composeJobFilter(String jobStatus, String policyID, String resourceID, String jobType,
			String startTimeTSStart, String startTimeTSEnd, String endTimeTSStart, String endTimeTSEnd,
			String filterName, String isDefault) {
		Map<String, Object> jobFilterInfo = new HashMap<>();

		if (!"none".equalsIgnoreCase(filterName)) {
			jobFilterInfo.put("filter_name", filterName);
		}

		if (null == isDefault) {
			jobFilterInfo.put("is_default", isDefault);
		} else if (!"none".equalsIgnoreCase(isDefault)) {
			if (isDefault.equalsIgnoreCase("true") || isDefault.equalsIgnoreCase("false")) {
				jobFilterInfo.put("is_default", Boolean.valueOf(isDefault));
			} else {
				jobFilterInfo.put("is_default", isDefault);
			}
		}

		String[] emptyArray = new String[0];

		if (jobStatus == null || jobStatus == "" || jobStatus.equalsIgnoreCase("invalidString")) {
			jobFilterInfo.put("job_status", jobStatus);
		} else if (jobStatus.equalsIgnoreCase("emptyarray")) {
			jobFilterInfo.put("job_status", emptyArray);
		} else if (!jobStatus.equalsIgnoreCase("none")) {
			jobFilterInfo.put("job_status", jobStatus.replace(" ", "").split(","));
		}

		if (policyID == null || policyID == "" || policyID.equalsIgnoreCase("invalidString")) {
			jobFilterInfo.put("policy_id", policyID);
		} else if (policyID.equalsIgnoreCase("emptyarray")) {
			jobFilterInfo.put("policy_id", emptyArray);
		} else if (!policyID.equalsIgnoreCase("none")) {
			jobFilterInfo.put("policy_id", policyID.replace(" ", "").split(","));
		}

		if (resourceID == null || resourceID == "" || resourceID.equalsIgnoreCase("invalidString")) {
			jobFilterInfo.put("source_id", resourceID);
		} else if (resourceID.equalsIgnoreCase("emptyarray")) {
			jobFilterInfo.put("source_id", emptyArray);
		} else if (!resourceID.equalsIgnoreCase("none")) {
			jobFilterInfo.put("source_id", resourceID.replace(" ", "").split(","));
		}

		if (jobType == null || jobType == "" || jobType.equalsIgnoreCase("invalidString")) {
			jobFilterInfo.put("job_type", jobType);
		} else if (jobType.equalsIgnoreCase("emptyarray")) {
			jobFilterInfo.put("job_type", emptyArray);
		} else if (!jobType.equalsIgnoreCase("none")) {
			jobFilterInfo.put("job_type", jobType.replace(" ", "").split(","));
		}

		Map<String, String> startTimeTSInfo = new HashMap<String, String>();
		if (!"none".equalsIgnoreCase(startTimeTSStart)) {
			startTimeTSInfo.put("start", startTimeTSStart);
		}
		if (!"none".equalsIgnoreCase(startTimeTSEnd)) {
			startTimeTSInfo.put("end", startTimeTSEnd);
		}
		if (startTimeTSInfo.size() != 0) {
			jobFilterInfo.put("start_time_ts", startTimeTSInfo);
		}

		// Map<String, String> endTimeTSInfo = new HashMap<String, String>();
		// if (!"none".equalsIgnoreCase(endTimeTSStart)) {
		// endTimeTSInfo.put("start", endTimeTSStart);
		// }
		// if (!"none".equalsIgnoreCase(endTimeTSEnd)) {
		// endTimeTSInfo.put("end", endTimeTSEnd);
		// }
		// if (startTimeTSInfo.size()!=0) {
		// jobFilterInfo.put("end_time_ts", endTimeTSInfo);
		// }

		return jobFilterInfo;
	}

	/**
	 * compose job filter removed endTimeTS
	 * 
	 * @author Zhaoguo.Ma
	 * @param jobStatus
	 * @param policyID
	 * @param resourceID
	 * @param jobType
	 * @param startTimeTSStart
	 * @param startTimeTSEnd
	 * @param filterName
	 * @param isDefault
	 * @return
	 */
	public Map<String, Object> composeJobFilter(String jobStatus, String policyID, String resourceID, String jobType,
			String startTimeTSStart, String startTimeTSEnd, String filterName, String isDefault) {
		Map<String, Object> jobFilterInfo = new HashMap<>();

		if (!"none".equalsIgnoreCase(filterName)) {
			jobFilterInfo.put("filter_name", filterName);
		}

		if (null == isDefault) {
			jobFilterInfo.put("is_default", isDefault);
		} else if (!"none".equalsIgnoreCase(isDefault)) {
			if (isDefault.equalsIgnoreCase("true") || isDefault.equalsIgnoreCase("false")) {
				jobFilterInfo.put("is_default", Boolean.valueOf(isDefault));
			} else {
				jobFilterInfo.put("is_default", isDefault);
			}
		}

		String[] emptyArray = new String[0];

		if (jobStatus == null || jobStatus == "" || jobStatus.equalsIgnoreCase("invalidString")) {
			jobFilterInfo.put("job_status", jobStatus);
		} else if (jobStatus.equalsIgnoreCase("emptyarray")) {
			jobFilterInfo.put("job_status", emptyArray);
		} else if (!jobStatus.equalsIgnoreCase("none")) {
			jobFilterInfo.put("job_status", jobStatus.replace(" ", "").split(","));
		}

		if (policyID == null || policyID == "" || policyID.equalsIgnoreCase("invalidString")) {
			jobFilterInfo.put("policy_id", policyID);
		} else if (policyID.equalsIgnoreCase("emptyarray")) {
			jobFilterInfo.put("policy_id", emptyArray);
		} else if (!policyID.equalsIgnoreCase("none")) {
			jobFilterInfo.put("policy_id", policyID.replace(" ", "").split(","));
		}

		if (resourceID == null || resourceID == "" || resourceID.equalsIgnoreCase("invalidString")) {
			jobFilterInfo.put("source_id", resourceID);
		} else if (resourceID.equalsIgnoreCase("emptyarray")) {
			jobFilterInfo.put("source_id", emptyArray);
		} else if (!resourceID.equalsIgnoreCase("none")) {
			jobFilterInfo.put("source_id", resourceID.replace(" ", "").split(","));
		}

		if (jobType == null || jobType == "" || jobType.equalsIgnoreCase("invalidString")) {
			jobFilterInfo.put("job_type", jobType);
		} else if (jobType.equalsIgnoreCase("emptyarray")) {
			jobFilterInfo.put("job_type", emptyArray);
		} else if (!jobType.equalsIgnoreCase("none")) {
			jobFilterInfo.put("job_type", jobType.replace(" ", "").split(","));
		}

		Map<String, String> startTimeTSInfo = new HashMap<String, String>();
		if (!"none".equalsIgnoreCase(startTimeTSStart)) {
			startTimeTSInfo.put("start", startTimeTSStart);
		}
		if (!"none".equalsIgnoreCase(startTimeTSEnd)) {
			startTimeTSInfo.put("end", startTimeTSEnd);
		}
		if (startTimeTSInfo.size() != 0) {
			jobFilterInfo.put("start_time_ts", startTimeTSInfo);
		}

		return jobFilterInfo;
	}
	
	/**
	 * 2018-08-09, added start_time_ts.type
	 * @author Zhaoguo.Ma
	 * @param jobStatus
	 * @param policyID
	 * @param resourceID
	 * @param jobType
	 * @param startTimeTSStart
	 * @param startTimeTSEnd
	 * @param filterName
	 * @param isDefault
	 * @return
	 */
	public Map<String, Object> composeJobFilterEx(String jobStatus, String policyID, String resourceID, String jobType, String startTimeType,
			String startTimeTSStart, String startTimeTSEnd, String sourceName, String filterName, String isDefault) {
		Map<String, Object> jobFilterInfo = new HashMap<>();

		if (!"none".equalsIgnoreCase(filterName)) {
			jobFilterInfo.put("filter_name", filterName);
		}
		
		if (!"none".equalsIgnoreCase(sourceName)) {
			jobFilterInfo.put("source_name", sourceName);
		}

		if (null == isDefault) {
			jobFilterInfo.put("is_default", isDefault);
		} else if (!"none".equalsIgnoreCase(isDefault)) {
			if (isDefault.equalsIgnoreCase("true") || isDefault.equalsIgnoreCase("false")) {
				jobFilterInfo.put("is_default", Boolean.valueOf(isDefault));
			} else {
				jobFilterInfo.put("is_default", isDefault);
			}
		}

		String[] emptyArray = new String[0];

		if (jobStatus == null || jobStatus == "" || jobStatus.equalsIgnoreCase("invalidString")) {
			jobFilterInfo.put("job_status", jobStatus);
		} else if (jobStatus.equalsIgnoreCase("emptyarray")) {
			jobFilterInfo.put("job_status", emptyArray);
		} else if (!jobStatus.equalsIgnoreCase("none")) {
			jobFilterInfo.put("job_status", jobStatus.replace(" ", "").split(","));
		}

		if (policyID == null || policyID == "" || policyID.equalsIgnoreCase("invalidString")) {
			jobFilterInfo.put("policy_id", policyID);
		} else if (policyID.equalsIgnoreCase("emptyarray")) {
			jobFilterInfo.put("policy_id", emptyArray);
		} else if (!policyID.equalsIgnoreCase("none")) {
			jobFilterInfo.put("policy_id", policyID.replace(" ", "").split(","));
		}

		if (resourceID == null || resourceID == "" || resourceID.equalsIgnoreCase("invalidString")) {
			jobFilterInfo.put("source_id", resourceID);
		} else if (resourceID.equalsIgnoreCase("emptyarray")) {
			jobFilterInfo.put("source_id", emptyArray);
		} else if (!resourceID.equalsIgnoreCase("none")) {
			jobFilterInfo.put("source_id", resourceID.replace(" ", "").split(","));
		}

		if (jobType == null || jobType == "" || jobType.equalsIgnoreCase("invalidString")) {
			jobFilterInfo.put("job_type", jobType);
		} else if (jobType.equalsIgnoreCase("emptyarray")) {
			jobFilterInfo.put("job_type", emptyArray);
		} else if (!jobType.equalsIgnoreCase("none")) {
			jobFilterInfo.put("job_type", jobType.replace(" ", "").split(","));
		}

		Map<String, String> startTimeTSInfo = new HashMap<String, String>();
		if (!"none".equalsIgnoreCase(startTimeType)) {
			startTimeTSInfo.put("type", startTimeType);
		}
		if (!"none".equalsIgnoreCase(startTimeTSStart)) {
			startTimeTSInfo.put("start_ts", startTimeTSStart);
		}
		if (!"none".equalsIgnoreCase(startTimeTSEnd)) {
			startTimeTSInfo.put("end_ts", startTimeTSEnd);
		}
		if (startTimeTSInfo.size() != 0) {
			jobFilterInfo.put("start_time_ts", startTimeTSInfo);
		}

		return jobFilterInfo;
	}
	

	/**
	 * @author Bharadwaj.Ghadiam Composing a JSONObject for the Destination
	 *         information.
	 * @param organization_id
	 * @param site_id
	 * @param destinationType
	 * @param destination_name
	 * @param cloud_volume(cloud_direct_volume,cloud_hybrid_store,share_folder)
	 * @param test
	 * @return
	 */
	public HashMap<String, Object> ComposeDestinationInfo(String organization_id, String site_id, String datacenter_id,
			String destination_type, String destination_status, String destination_name,
			HashMap<String, Object> cloud_volume, ExtentTest test) {
		// TODO Auto-generated method stub
		test.log(LogStatus.INFO, "Composing a HAshMap for the Destination Information");
		HashMap<String, Object> DestinationInfo = new HashMap<String, Object>();
		DestinationInfo.put("organization_id", organization_id);
		DestinationInfo.put("site_id", site_id);
		DestinationInfo.put("datacenter_id", datacenter_id);
		DestinationInfo.put("destination_type", destination_type);
		DestinationInfo.put("destination_status", destination_status);
		DestinationInfo.put("destination_name", destination_name);
		if (destination_type.equals("cloud_direct_volume")) {
			DestinationInfo.put("cloud_direct_volume", cloud_volume);
		} else if (destination_type.equals("cloud_hybrid_store")) {
			DestinationInfo.put("cloud_hybrid_store", cloud_volume);
		} else {
			DestinationInfo.put("share_folder", cloud_volume);
		}
		setDestinationInfo(DestinationInfo);
		return DestinationInfo;
	}

	public HashMap<String, Object> ComposeDestinationInfo(String destination_id, String cloud_account_id,
			String organization_id, String site_id, String datacenter_id, String dedupe_savings,
			String destination_type, String destination_status, String destination_name,
			HashMap<String, Object> cloud_volume, ExtentTest test) {
		// TODO Auto-generated method stub
		test.log(LogStatus.INFO, "Composing a HAshMap for the Destination Information");
		HashMap<String, Object> DestinationInfo = new HashMap<String, Object>();
		DestinationInfo.put("destination_id", destination_id);
		DestinationInfo.put("cloud_account_id", cloud_account_id);
		DestinationInfo.put("parent_id", cloud_account_id);
		DestinationInfo.put("organization_id", organization_id);
		DestinationInfo.put("site_id", site_id);
		DestinationInfo.put("datacenter_id", datacenter_id);
		DestinationInfo.put("dedupe_savings", dedupe_savings);
		DestinationInfo.put("destination_type", destination_type);
		DestinationInfo.put("destination_status", destination_status);
		DestinationInfo.put("destination_name", destination_name);
		if (destination_type.equals("cloud_direct_volume")) {
			DestinationInfo.put("cloud_direct_volume", cloud_volume);
		} else if (destination_type.equals("cloud_hybrid_store")) {
			DestinationInfo.put("cloud_hybrid_store", cloud_volume);
		} else {
			DestinationInfo.put("share_folder", cloud_volume);
		}
		setDestinationInfo(DestinationInfo);
		return DestinationInfo;
	}

	/**
	 * @author Bharadwaj.Ghadiam
	 * @param destinationInfo
	 */
	public void setDestinationInfo(HashMap<String, Object> destinationInfo) {
		// TODO Auto-generated method stub
		this.destinationInfo = destinationInfo;
	}

	/**
	 * @author Bharadwaj.Ghadiam
	 * @return the destinationInforamtion
	 */

	public HashMap<String, Object> getDestinationInfo() {
		return this.destinationInfo;
	}

	/**
	 * @author shuo.zhang
	 * @param source_name
	 * @param source_type
	 * @param source_product
	 * @param organization_id
	 * @param site_id
	 * @param protection_status
	 * @param connection_status
	 * @param os_major
	 * @param application
	 * @param agentInfo
	 * @param source_id
	 * @return
	 */
	public Map<String, Object> getSourceInfo(String source_name, String source_type, String source_product,
			String organization_id, String site_id, String protection_status, String connection_status, String os_major,
			String[] application, Map<String, String> agentInfo, String source_id) {

		HashMap<String, Object> sourceInfo = new HashMap<>();
		sourceInfo.put("source_name", source_name);
		sourceInfo.put("source_type", source_type);
		sourceInfo.put("source_product", source_product);
		sourceInfo.put("organization_id", organization_id);
		sourceInfo.put("site_id", site_id);
		sourceInfo.put("protection_status", protection_status);
		sourceInfo.put("connection_status", connection_status);
		sourceInfo.put("os_major", os_major);
		sourceInfo.put("applications", application);
		sourceInfo.put("agent", agentInfo);
		sourceInfo.put("source_id", source_id);
		return sourceInfo;
	}

	/***
	 * @author shuo.zhang
	 * @param columnsList
	 * @return
	 */
	public Map<String, Object> getUsersSourcesColumnsInfo(ArrayList<HashMap<String, Object>> columnsList) {

		HashMap<String, Object> sourcesColumnsInfo = new HashMap<String, Object>();
		sourcesColumnsInfo.put("columns", columnsList);
		return sourcesColumnsInfo;
	}

	public Map<String, ArrayList<HashMap<String, Object>>> jobColumnInfo(
			ArrayList<HashMap<String, Object>> expected_columns) {
		// TODO Auto-generated method stub
		Map<String, ArrayList<HashMap<String, Object>>> jobColumns = new HashMap<>();

		jobColumns.put("columns", expected_columns);
		return jobColumns;
	}

	/***
	 * hypervisor column
	 * 
	 * @author yuefen.liu
	 * @param columnsList
	 * @return
	 */
	public Map<String, Object> getHypervisorColumnsInfo(ArrayList<HashMap<String, Object>> columnsList) {

		HashMap<String, Object> hypervisorColumnsInfo = new HashMap<String, Object>();
		hypervisorColumnsInfo.put("columns", columnsList);
		return hypervisorColumnsInfo;
	}

	/**
	 * hypervisor filter
	 * 
	 * @author Zhaoguo.Ma
	 * @param filterName
	 * @param status
	 * @param hypervisorProduct
	 * @param hypervisorType
	 * @param hypervisorName
	 * @param isDefault
	 * @return
	 */
	public Map<String, Object> composeHypervisorFilterInfo(String filterName, String status, String hypervisorProduct,
			String hypervisorType, String hypervisorName, String isDefault) {
		Map<String, Object> filterInfo = new HashMap<String, Object>();
		String[] emptyArray = new String[0];

		if (filterName == null) {
			filterInfo.put("filter_name", filterName);
		} else if (!filterName.equalsIgnoreCase("none")) {
			filterInfo.put("filter_name", filterName);
		}

		if (status == null || status == "") {
			filterInfo.put("status", status);
		} else if (status.equalsIgnoreCase("emptyarray")) {
			filterInfo.put("status", emptyArray);
		} else if (!status.equalsIgnoreCase("none")) {
			filterInfo.put("status", status.replace(" ", "").split(","));
		}

		if (hypervisorProduct == null || hypervisorProduct == "") {
			filterInfo.put("hypervisor_product", hypervisorProduct);
		} else if (hypervisorProduct.equalsIgnoreCase("emptyarray")) {
			filterInfo.put("hypervisor_product", emptyArray);
		} else if (!hypervisorProduct.equalsIgnoreCase("none")) {
			filterInfo.put("hypervisor_product", hypervisorProduct.replace(" ", "").split(","));
		}

		if (hypervisorType == null || hypervisorType == "") {
			filterInfo.put("hypervisor_type", hypervisorType);
		} else if (hypervisorType.equalsIgnoreCase("emptyarray")) {
			filterInfo.put("hypervisor_type", emptyArray);
		} else if (!hypervisorType.equalsIgnoreCase("none")) {
			filterInfo.put("hypervisor_type", hypervisorType.replace(" ", "").split(","));
		}

		if (hypervisorName == null) {
			filterInfo.put("hypervisor_name", hypervisorName);
		} else if (!hypervisorName.equalsIgnoreCase("none")) {
			filterInfo.put("hypervisor_name", hypervisorName);
		}

		if (isDefault == null || isDefault == "") {
			filterInfo.put("is_default", isDefault);
		} else if (isDefault.equalsIgnoreCase("true")) {
			filterInfo.put("is_default", true);
		} else if (isDefault.equalsIgnoreCase("false")) {
			filterInfo.put("is_default", false);
		} else if (!isDefault.equalsIgnoreCase("none")) {
			filterInfo.put("is_default", isDefault);
		}

		return filterInfo;
	}

	public Map<String, String> getHypervisorDefaultColumnInfo(String column_id, String long_label, String short_label,
			String key, String sort, String filter, String visible, String order_id) {
		// TODO Auto-generated method stub

		Map<String, String> hypervisorDefaultColumnInfo = new HashMap<>();
		hypervisorDefaultColumnInfo.put("column_id", column_id);
		hypervisorDefaultColumnInfo.put("long_label", long_label);
		hypervisorDefaultColumnInfo.put("short_label", short_label);
		hypervisorDefaultColumnInfo.put("key", key);
		hypervisorDefaultColumnInfo.put("sort", sort);
		hypervisorDefaultColumnInfo.put("filter", filter);
		hypervisorDefaultColumnInfo.put("visible", visible);
		hypervisorDefaultColumnInfo.put("order_id", order_id);

		return hypervisorDefaultColumnInfo;
	}

	public HashMap<String, Object> composeUserFilterInfo(String filter_name, String search_string, String user_is_blocked,
			String user_status, String role_id, String is_default) {

		HashMap<String, Object> userFilterInfo = new HashMap<>();
		userFilterInfo.put("filter_name", filter_name);
		if (search_string == null || search_string == "") {
			userFilterInfo.put("search_string", search_string);
		} else if (!search_string.equalsIgnoreCase("none")) {
			userFilterInfo.put("search_string", search_string);
		}

		if (user_is_blocked == null || user_is_blocked == "") {
			userFilterInfo.put("blocked", user_is_blocked);
		} else if (!user_is_blocked.equalsIgnoreCase("none")) {
			userFilterInfo.put("blocked", user_is_blocked);
		}

		if (user_status == null || user_status == "") {
			userFilterInfo.put("status", user_status);
		} else if (!user_status.equalsIgnoreCase("none")) {
			userFilterInfo.put("status", user_status);
		}

		if (role_id == null || role_id == "") {
			userFilterInfo.put("role_id", role_id);
		} else if (!role_id.equalsIgnoreCase("none")) {
			userFilterInfo.put("role_id", role_id);
		}

		if (is_default == null || is_default == "") {
			userFilterInfo.put("is_default", true);
		} else {
			userFilterInfo.put("is_default", Boolean.parseBoolean(is_default));
		}

		return userFilterInfo;
	}

	/**
	 * used assign msp account admin to msp account
	 * 
	 * @author yuefen.liu
	 * @param user_Id
	 * @return
	 */
	public Map<String, Object> assignMspAccountAdminsInfo(String[] userIds) {

		ArrayList<Map<String, String>> list = new ArrayList<>();

		for (int i = 0; i < userIds.length; i++) {
			Map<String, String> assignAdminInfo = new HashMap<>();
			assignAdminInfo.put("user_id", userIds[i]);
			list.add(assignAdminInfo);
		}

		Map<String, Object> assignAdmins = new HashMap<>();
		assignAdmins.put("users", list);
		return assignAdmins;
	}
	
	
	public HashMap<String, String> composeJobDataInfoMapForCD(String percent_complete, String start_time_ts,
			String end_time_ts, String elapsed_time, String error_count, String warning_count, String severity,
			String job_status, String processed_bytes_processed, String processed_bytes_changed, String processed_files,
			String transferred_uncompressed_bytes, String backup_throughput, String restore_data_source,
			String restore_data_location, String restore_data_path) {
		HashMap<String, String> jobDataInfo = new HashMap<>();
		if (!"none".equalsIgnoreCase(percent_complete)) {
			jobDataInfo.put("percent_complete", percent_complete);
		}
		if (!"none".equalsIgnoreCase(start_time_ts)) {
			jobDataInfo.put("start_time_ts", start_time_ts);
		}
		if (!"none".equalsIgnoreCase(end_time_ts)) {
			jobDataInfo.put("end_time_ts", end_time_ts);
		}
		if (!"none".equalsIgnoreCase(elapsed_time)) {
			jobDataInfo.put("elapsed_time", elapsed_time);
		}
		if (!"none".equalsIgnoreCase(error_count)) {
			jobDataInfo.put("error_count", error_count);
		}
		if (!"none".equalsIgnoreCase(warning_count)) {
			jobDataInfo.put("warning_count", warning_count);
		}
		if (!"none".equalsIgnoreCase(severity)) {
			jobDataInfo.put("severity", severity);
		}
		if (!"none".equalsIgnoreCase(job_status)) {
			jobDataInfo.put("job_status", job_status);
		}
		if (!"none".equalsIgnoreCase(processed_bytes_processed)) {
			jobDataInfo.put("processed_bytes_processed", processed_bytes_processed);
		}
		if (!"none".equalsIgnoreCase(processed_bytes_changed)) {
			jobDataInfo.put("processed_bytes_changed", processed_bytes_changed);
		}
		if (!"none".equalsIgnoreCase(processed_files)) {
			jobDataInfo.put("processed_files", processed_files);
		}
		if (!"none".equalsIgnoreCase(transferred_uncompressed_bytes)) {
			jobDataInfo.put("transferred_uncompressed_bytes", transferred_uncompressed_bytes);
		}
		if (!"none".equalsIgnoreCase(backup_throughput)) {
			jobDataInfo.put("backup_throughput", backup_throughput);
		}
		if (!"none".equalsIgnoreCase(restore_data_source)) {
			jobDataInfo.put("restore_data_source", restore_data_source);
		}
		if (!"none".equalsIgnoreCase(restore_data_location)) {
			jobDataInfo.put("restore_data_location", restore_data_location);
		}
		if (!"none".equalsIgnoreCase(restore_data_path)) {
			jobDataInfo.put("restore_data_path", restore_data_path);
		}
		return jobDataInfo;
	}
	
	public HashMap<String, String> composeJobDataInfoMapForUDPReplication(String phase, String percent_complete, String start_time_ts, String elapsed_time,
			String estimated_time_remaining, String protection, String compression_level, String target_recovery_point_server, String target_data_store_name, 
			String current_session, String target_cloud_hybrid_store, String start_session, String end_session, String saved_bandwidth_percentage, 
			String source_recovery_point_server, String source_data_store_name, String network_throttle, String pysical_throughput, String logical_throughput, String job_seq) {
		HashMap<String, String> jobDataInfo = new HashMap<>();
		if (!"none".equalsIgnoreCase(phase)) {
			jobDataInfo.put("phase", phase);
		}
		if (!"none".equalsIgnoreCase(percent_complete)) {
			jobDataInfo.put("percent_complete", percent_complete);
		}
		if (!"none".equalsIgnoreCase(start_time_ts)) {
			jobDataInfo.put("start_time_ts", start_time_ts);
		}
		if (!"none".equalsIgnoreCase(elapsed_time)) {
			jobDataInfo.put("elapsed_time", elapsed_time);
		}
		if (!"none".equalsIgnoreCase(estimated_time_remaining)) {
			jobDataInfo.put("estimated_time_remaining", estimated_time_remaining);
		}
		if (!"none".equalsIgnoreCase(protection)) {
			jobDataInfo.put("protection", protection);
		}
		if (!"none".equalsIgnoreCase(compression_level)) {
			jobDataInfo.put("compression_level", compression_level);
		}
		if (!"none".equalsIgnoreCase(target_recovery_point_server)) {
			jobDataInfo.put("target_recovery_point_server", target_recovery_point_server);
		}
		if (!"none".equalsIgnoreCase(target_data_store_name)) {
			jobDataInfo.put("target_data_store_name", target_data_store_name);
		}
		if (!"none".equalsIgnoreCase(current_session)) {
			jobDataInfo.put("current_session", current_session);
		}
		if (!"none".equalsIgnoreCase(target_cloud_hybrid_store)) {
			jobDataInfo.put("target_cloud_hybrid_store", target_cloud_hybrid_store);
		}
		if (!"none".equalsIgnoreCase(start_session)) {
			jobDataInfo.put("start_session", start_session);
		}
		if (!"none".equalsIgnoreCase(end_session)) {
			jobDataInfo.put("end_session", end_session);
		}
		if (!"none".equalsIgnoreCase(saved_bandwidth_percentage)) {
			jobDataInfo.put("saved_bandwidth_percentage", saved_bandwidth_percentage);
		}
		if (!"none".equalsIgnoreCase(source_recovery_point_server)) {
			jobDataInfo.put("source_recovery_point_server", source_recovery_point_server);
		}
		if (!"none".equalsIgnoreCase(source_data_store_name)) {
			jobDataInfo.put("source_data_store_name", source_data_store_name);
		}
		if (!"none".equalsIgnoreCase(network_throttle)) {
			jobDataInfo.put("network_throttle", network_throttle);
		}
		if (!"none".equalsIgnoreCase(pysical_throughput)) {
			jobDataInfo.put("pysical_throughput", pysical_throughput);
		}
		if (!"none".equalsIgnoreCase(logical_throughput)) {
			jobDataInfo.put("logical_throughput", logical_throughput);
		}
		if (!"none".equalsIgnoreCase(job_seq)) {
			jobDataInfo.put("job_seq", job_seq);
		}
		return jobDataInfo;
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param filter_name
	 * @param policy_name
	 * @param group_id
	 * @param last_backup_status
	 * @param status
	 * @param is_default
	 * @return
	 */
	public HashMap<String, Object> composePolicyFilter(String filter_name, String policy_name, String group_id, 
			String last_backup_status, String status, String is_default){
		HashMap<String, Object> filterInfo = new HashMap<String, Object>();
		String[] emptyArray = new String[0];
		
		if (filter_name == null) {
			filterInfo.put("filter_name", filter_name);
		} else if (!filter_name.equalsIgnoreCase("none")) {
			filterInfo.put("filter_name", filter_name);
		}

		if (policy_name == null) {
			filterInfo.put("policy_name", policy_name);
		} else if (!policy_name.equalsIgnoreCase("none")) {
			filterInfo.put("policy_name", policy_name);
		}
		
		if (group_id == null || group_id == "") {
			filterInfo.put("group_id", group_id);
		} else if (group_id.equalsIgnoreCase("emptyarray")) {
			filterInfo.put("group_id", emptyArray);
		} else if (!group_id.equalsIgnoreCase("none")) {
			filterInfo.put("group_id", group_id.replace(" ", "").split(","));
		}
		
		if (last_backup_status == null || last_backup_status == "") {
			filterInfo.put("last_job", last_backup_status);
		} else if (last_backup_status.equalsIgnoreCase("emptyarray")) {
			filterInfo.put("last_job", emptyArray);
		} else if (!last_backup_status.equalsIgnoreCase("none")) {
			filterInfo.put("last_job", last_backup_status.replace(" ", "").split(","));
		}
		
		if (status == null || status == "") {
			filterInfo.put("status", status);
		} else if (status.equalsIgnoreCase("emptyarray")) {
			filterInfo.put("status", emptyArray);
		} else if (!status.equalsIgnoreCase("none")) {
			filterInfo.put("status", status.replace(" ", "").split(","));
		}

		if (is_default == null || is_default == "") {
			filterInfo.put("is_default", is_default);
		} else if (is_default.equalsIgnoreCase("true")) {
			filterInfo.put("is_default", true);
		} else if (is_default.equalsIgnoreCase("false")) {
			filterInfo.put("is_default", false);
		} else if (!is_default.equalsIgnoreCase("none")) {
			filterInfo.put("is_default", is_default);
		}
		
		return filterInfo;
	}
	
	/**
	 * @author Kiran.Sripada
	 * @param server_name
	 * @param server_port
	 * @param server_protocol
	 * @param server_username
	 * @param server_password
	 * @param organization_id
	 * @param site_id
	 * @param datacenter_id
	 * @return cloudRPSInfo
	 */
	
	public HashMap<String,Object> composeCloudRPSInfo(String server_name,
													  String server_port,
													  String server_protocol,
													  String server_username,
													  String server_password,
													  String organization_id,
													  String site_id,
													  String datacenter_id){
		HashMap<String,Object> cloudRPSInfo = new HashMap<>();
		cloudRPSInfo.put("server_name",server_name);
		cloudRPSInfo.put("server_port",server_port);
		cloudRPSInfo.put("server_protocol",server_protocol);
		cloudRPSInfo.put("server_username",server_username);
		cloudRPSInfo.put("server_password",server_password);
		if(!organization_id.equals(null)) {
			cloudRPSInfo.put("organization_id",organization_id);
		}
		if(!site_id.equals(null)) {
			cloudRPSInfo.put("site_id",site_id);
		}
		if(datacenter_id != (null)) {
			cloudRPSInfo.put("datacenter_id",datacenter_id);
		}
		
		/*cloudRPSInfo.put("organization_id",organization_id);
		cloudRPSInfo.put("site_id",site_id);
		cloudRPSInfo.put("datacenter_id",datacenter_id);*/
		
		return cloudRPSInfo;
	}
	
	/**
	 * @author Kiran.Sripada
	 * @param server_name
	 * @param server_port
	 * @param server_protocol
	 * @param server_username
	 * @param server_password
	 * @param organization_id
	 * @param site_id
	 * @param datacenter_id
	 * @return cloudRPSInfo
	 */
	
	public HashMap<String,Object> composetoupdateCloudRPSInfo(String server_name,
													  String server_port,
													  String server_protocol,
													  String server_username,
													  String server_password
													 ){
		HashMap<String,Object> cloudRPSInfo = new HashMap<>();
		cloudRPSInfo.put("server_name",server_name);
		cloudRPSInfo.put("server_port",server_port);
		cloudRPSInfo.put("server_protocol",server_protocol);
		cloudRPSInfo.put("server_username",server_username);
		cloudRPSInfo.put("server_password", server_password);
		return cloudRPSInfo;
	}
	/**
	 * compose postOrgInfoBySearchString elements 
	 * @author Ramya.Nagepalli
	 * @param search_string
	 * @param currPage
	 * @param pageSize
	 * @return OrgInfo
	 */
	public HashMap<String, Object> postOrgInfoBySearchString(String search_string, int page, int page_size) {
		// TODO Auto-generated method stub
		HashMap<String,Object> OrgInfo =new HashMap<String, Object>();
		OrgInfo.put("search_string",search_string);
		if(page==0) {
			
		}else {
			OrgInfo.put("page", page);
			//.put("currPage",currPage);
		}
		
		if(page_size==0) {
			
		}else {
			OrgInfo.put("page_size",page_size);
		}
		
		OrgInfo.put("include_deleted","false");
		
		return OrgInfo;
	}

	/**
	 * composeRecoveredResourcesFilter elements 
	 * @author Ramya.Nagepalli
	 * @param policy_id,state,oSmajor,recoveredResourceType,filter_name,is_default
	 * @return
	 */
	public HashMap<String, Object> composeRecoveredResourcesFilter(String policy_id, String state, String oSmajor,
			String recoveredResourceType, String filter_name, String is_default) {
		// TODO Auto-generated method stub
		HashMap<String,Object>  recoveredResource= new HashMap<String,Object>();
		
		ArrayList<String> policy=new ArrayList<String>();
		policy.add(policy_id);
		
		ArrayList<String> State=new ArrayList<String>();
		State.add(state);
		
		ArrayList<String> OSmajor=new ArrayList<String>();
		OSmajor.add(oSmajor);
		
		ArrayList<String> RecoveredResourceType=new ArrayList<String>();
		RecoveredResourceType.add(recoveredResourceType);
		
		recoveredResource.put("policy_id", policy);
		recoveredResource.put("state", State);
		recoveredResource.put("os_major", OSmajor);
		recoveredResource.put("recovered_resource_type", RecoveredResourceType);
		recoveredResource.put("filter_name", filter_name);
		recoveredResource.put("is_default", is_default);
		
		return recoveredResource;
	}
	
	/**
	 * composeRecoveredResourcesFilter elements
	 * 
	 * @author Ramya.Nagepalli
	 * @param policy_id,state,oSmajor,recoveredResourceType,filter_name,
	 *            is_default
	 * @return
	 */
	public HashMap<String, Object> composeRecoveredResourcesFilter(String policy_id, String state, String oSmajor,
			String recoveredResourceType, String filter_name, String is_default, String view_type) {
		// TODO Auto-generated method stub
		HashMap<String, Object> recoveredResource = new HashMap<String, Object>();

		ArrayList<String> policy = new ArrayList<String>();
		policy.add(policy_id);

		ArrayList<String> State = new ArrayList<String>();
		State.add(state);

		ArrayList<String> OSmajor = new ArrayList<String>();
		OSmajor.add(oSmajor);

		ArrayList<String> RecoveredResourceType = new ArrayList<String>();
		RecoveredResourceType.add(recoveredResourceType);

		recoveredResource.put("policy_id", policy);
		recoveredResource.put("state", State);
		recoveredResource.put("os_major", OSmajor);
		recoveredResource.put("recovered_resource_type", RecoveredResourceType);
		recoveredResource.put("filter_name", filter_name);
		recoveredResource.put("is_default", is_default);

		recoveredResource.put("view_type", view_type);

		return recoveredResource;
	}
	/**
	 * the function is used to compose order information HashMap
	 * 
	 * @author liu.yuefen
	 * @param orderID
	 * @param fulfillmentID
	 */
	public Map<String, String> composeOrderInfoForUser(String orderID, String fulfillmentID) {

		Map<String, String> orderInfo = new HashMap<>();
		if ((orderID != null) && (!orderID.equals(""))) {
			orderInfo.put("order_id", orderID);
		}
		if ((fulfillmentID != null) && (!fulfillmentID.equals(""))) {
			orderInfo.put("fulfillment_id", fulfillmentID);
		}

		return orderInfo;
	}

	/**
	 * the function is used to compose_extra_Inputs
	 * 
	 * @author ramya.Nagepalli
	 * @param image_url
	 * @param status
	 * @param source_status
	 * @param backup_status
	 * @param cloud_direct_usage
	 * @param cloud_hybrid_usage
	 * @param added_by
	 * @param allowed_actions
	 * @param blocked 
	 */
	public HashMap<String, Object> compose_extra_Inputs(String image_url, String status,
		HashMap<String, Object> source_status, HashMap<String, Object> backup_status,
		HashMap<String, Object> cloud_direct_usage, HashMap<String, Object> cloud_hybrid_usage,
		HashMap<String, Object> added_by, ArrayList<String> allowed_actions, boolean blocked) {
	// TODO Auto-generated method stub
	HashMap<String, Object> compose_extra_Inputs=new HashMap<String, Object>();
	
	compose_extra_Inputs.put("image_url",image_url);
	compose_extra_Inputs.put("status",status);
	compose_extra_Inputs.put("source_status",source_status);
	compose_extra_Inputs.put("backup_status",backup_status);
	compose_extra_Inputs.put("cloud_direct_usage",cloud_direct_usage);
	compose_extra_Inputs.put("cloud_hybrid_usage",cloud_hybrid_usage);
	compose_extra_Inputs.put("added_by",added_by);
	compose_extra_Inputs.put("allowed_actions",allowed_actions);
	compose_extra_Inputs.put("blocked",blocked);
	
	return compose_extra_Inputs;
}
	
	/**
	 * the function is used to compose_cloud_direct_usage
	 * 
	 * @author ramya.Nagepalli
	 * @param usage
	 * @param threshold
	 */
	public HashMap<String, Object> compose_cloud_direct_usage(String usage, String threshold) {
		// TODO Auto-generated method stub
		HashMap<String, Object> cloud_direct_usage=new HashMap<String, Object>();
		
		cloud_direct_usage.put("usage", usage);
		cloud_direct_usage.put("threshold", threshold);
		
		
			
			return cloud_direct_usage;
	}

	/**
	 * the function is used to compose_added_by
	 * 
	 * @author ramya.Nagepalli
	 * @param id
	 * @param name
	 * @param email
	 */
	public HashMap<String, Object> compose_added_by(String id, String name, String email) {
		// TODO Auto-generated method stub
		HashMap<String, Object> added_by=new HashMap<String, Object>();
		
		added_by.put("id", id);
		added_by.put("name", name);
		added_by.put("email", email);	
		return added_by;
	}

	/**
	 * posting the Branding emailer
	 * @author malleswari.sykam
	 * @param organization_id
	 * @return
	 */
	
	
	public HashMap<String, Object> ComposeBrandingemailerinfo (String organization_id, 
			String emailer_name, 
			String email,
			String signature,
			String support_call,
			String support_chat,
			String support_sales,
			String support_email,
			String facebook_link,
			String twitter_link,
			String linkdin_link,
			String social_media_platform,
			String legal_notice,
			String contact_us,
			String privacy,
			String copyrights,
			String branding_from
			){

		HashMap<String,Object> BrandingInfo = new HashMap<>();
		BrandingInfo.put("organization_id",organization_id);
		if(organization_id==null||organization_id=="") {
			BrandingInfo.put("organization_id", organization_id);
		}else if(!organization_id.equalsIgnoreCase("none")) {
			BrandingInfo.put("organization_id",organization_id);
		}

		BrandingInfo.put("branding_from",branding_from);
		if(branding_from==null||branding_from=="") {
			BrandingInfo.put("branding_from", branding_from);
		}else if(!branding_from.equalsIgnoreCase("none")) {
			BrandingInfo.put("branding_from",branding_from);
		}
			
		
		if(emailer_name==null||emailer_name=="") {
			BrandingInfo.put("emailer_name",emailer_name);
		}
		else if(!emailer_name.equalsIgnoreCase("none")) {
			BrandingInfo.put("emailer_name",emailer_name);
		}

		if(email==null||email=="") {
			BrandingInfo.put("email",email);
		}
		else if(!email.equalsIgnoreCase("none")) {
			BrandingInfo.put("email",email);
		}

		if(signature==null||signature=="") {
			BrandingInfo.put("signature",signature);
		}
		else if(!signature.equalsIgnoreCase("none")) {
			BrandingInfo.put("signature",signature);
		}

		if(support_call==null||support_call=="") {
			BrandingInfo.put("support_call",support_call);
		}
		else if(!support_call.equalsIgnoreCase("none")) {
			BrandingInfo.put("support_call",support_call);
		}

		if(support_chat==null||support_chat=="") {
			BrandingInfo.put("support_chat",support_chat);
		}
		else if(!support_chat.equalsIgnoreCase("none")) {
			BrandingInfo.put("support_chat",support_chat);
		}

		if(support_sales==null||support_sales=="") {
			BrandingInfo.put("support_sales",support_sales);
		}
		else if(!support_sales.equalsIgnoreCase("none")) {
			BrandingInfo.put("support_sales",support_sales);
		}
		BrandingInfo.put("support_email", support_email);
		
		if(facebook_link==null||facebook_link=="") {
			BrandingInfo.put("facebook_link",facebook_link);
		}
		else if(!facebook_link.equalsIgnoreCase("none")) {
			BrandingInfo.put("facebook_link",facebook_link);
		}
		if(twitter_link==null||twitter_link=="") {
			BrandingInfo.put("twitter_link",twitter_link);
		}
		else if(!twitter_link.equalsIgnoreCase("none")) {
			BrandingInfo.put("twitter_link",twitter_link);
		}
		if(linkdin_link==null||linkdin_link=="") {
			BrandingInfo.put("linkdin_link",linkdin_link);
		}
		else if(!linkdin_link.equalsIgnoreCase("none")) {
			BrandingInfo.put("linkdin_link",linkdin_link);
		}
		if(social_media_platform==null||social_media_platform=="") {
			BrandingInfo.put("social_media_platform",social_media_platform);
		}
		else if(!social_media_platform.equalsIgnoreCase("none")) {
			BrandingInfo.put("social_media_platform",social_media_platform);
		}
		if(legal_notice==null||legal_notice=="") {
			BrandingInfo.put("legal_notice",legal_notice);
		}
		else if(!legal_notice.equalsIgnoreCase("none")) {
			BrandingInfo.put("legal_notice",legal_notice);
		}
		if(contact_us==null||contact_us=="") {
			BrandingInfo.put("contact_us",contact_us);
		}
		else if(!contact_us.equalsIgnoreCase("none")) {
			BrandingInfo.put("contact_us",contact_us);
		}
		if(privacy==null||privacy=="") {
			BrandingInfo.put("privacy",privacy);
		}
		else if(!privacy.equalsIgnoreCase("none")) {
			BrandingInfo.put("privacy",privacy);
		}
		if(copyrights==null||copyrights=="") {
			BrandingInfo.put("copyrights",copyrights);
		}
		else if(!copyrights.equalsIgnoreCase("none")) {
			BrandingInfo.put("copyrights",copyrights);
		}
		return BrandingInfo;
	}

	/**
	 * posting the Branding
	 * @author malleswari.sykam
	 * @param organization_id
	 * @return
	 */
	public HashMap<String, Object> ComposeBrandinginfo (String organization_id, 
			String organization_name, 
			String portal_url,
			String primary_color,  
			String secondary_color,
			String branding_msg
			){

		HashMap<String,Object> BrandingInfo = new HashMap<>();
		BrandingInfo.put("organization_id",organization_id);
		if(organization_id==null||organization_id=="") {
			BrandingInfo.put("organization_id", organization_id);
		}else if(!organization_id.equalsIgnoreCase("none")) {
			BrandingInfo.put("organization_id",organization_id);
		}

		if(organization_name==null||organization_name=="") {
			BrandingInfo.put("organization_name", organization_name);
		}else if(!organization_name.equalsIgnoreCase("none")) {
			BrandingInfo.put("organization_name",organization_name);
		}
		if(portal_url==null||portal_url=="") {
			BrandingInfo.put("portal_url",portal_url);
		}
		else if(!portal_url.equalsIgnoreCase("none")) {
			BrandingInfo.put("portal_url",portal_url);
		}

		

		if(primary_color==null||primary_color=="") {
			BrandingInfo.put("primary_color",primary_color);
		}
		else if(!primary_color.equalsIgnoreCase("none")) {
			BrandingInfo.put("primary_color",primary_color);
		}

		if(secondary_color==null||secondary_color=="") {
			BrandingInfo.put("secondary_color",secondary_color);
		}
		else if(!secondary_color.equalsIgnoreCase("none")) {
			BrandingInfo.put("secondary_color",secondary_color);
		}if(branding_msg==null||branding_msg=="") {
			BrandingInfo.put("branding_msg",branding_msg);
		}


		return BrandingInfo;
	}



	/**
	 * the function is used to compose_source_status
	 * 
	 * @author ramya.Nagepalli
	 * @param num_total
	 * @param num_protected
	 * @param num_unprotected
	 * @param num_online
	 * @param num_offline
	 */
	public HashMap<String, Object> compose_source_status(String num_total, String num_protected, String num_unprotected, String num_online,
			String num_offline) {
		// TODO Auto-generated method stub
		HashMap<String, Object> source_status=new HashMap<String, Object>();
		
		source_status.put("num_total", num_total);
		source_status.put("num_protected", num_protected);
		source_status.put("num_unprotected", num_unprotected);
		source_status.put("num_online", num_online);
		source_status.put("num_offline", num_offline);
		
		return source_status;
	}

	/**
	 * the function is used to compose_backup_status
	 * 
	 * @author ramya.Nagepalli
	 * @param num_success
	 * @param num_missed
	 * @param num_failed
	 * @param num_ar_failed
	 * 
	 */
	public HashMap<String, Object> compose_backup_status(String num_success, String num_missed, String num_failed,
			String num_ar_failed) {
		// TODO Auto-generated method stub
	HashMap<String, Object> backup_status=new HashMap<String, Object>();
		
	backup_status.put("num_success", num_success);
	backup_status.put("num_missed", num_missed);
	backup_status.put("num_failed", num_failed);
	backup_status.put("num_ar_failed", num_ar_failed);
	
		
		return backup_status;
	}

	public HashMap<String, Object> composeCloudRPSDataStoreInfo(String datastore_name, boolean dedupe_enabled, boolean encryption_enabled,
			boolean compression_enabled, String rps_server_id, HashMap<String, Object> datastorepropertiesInfo) {
		HashMap<String,Object> datastoreInfo = new HashMap<>();
		datastoreInfo.put("datastore_name", datastore_name);
		datastoreInfo.put("dedupe_enabled", dedupe_enabled);
		datastoreInfo.put("encryption_enabled", encryption_enabled);
		datastoreInfo.put("compression_enabled", compression_enabled);
		datastoreInfo.put("rps_server_id", rps_server_id);
		datastoreInfo.put("datastore_properties", datastorepropertiesInfo);
		
		return datastoreInfo;
	}

	
	public HashMap<String, Object> composetoUpdateCloudRPSDataStoreInfo(String datastore_name, boolean dedupe_enabled, boolean encryption_enabled,
			boolean compression_enabled,HashMap<String, Object> datastorepropertiesInfo) {
		HashMap<String,Object> datastoreInfo = new HashMap<>();
		datastoreInfo.put("datastore_name", datastore_name);
		datastoreInfo.put("dedupe_enabled", dedupe_enabled);
		datastoreInfo.put("encryption_enabled", encryption_enabled);
		datastoreInfo.put("compression_enabled", compression_enabled);
		datastoreInfo.put("datastore_properties", datastorepropertiesInfo);
		
		return datastoreInfo;
	}
	
	public HashMap<String, Object> composedatastoreInfo(String datastore_id) {
		
		HashMap<String,Object> datastoreInfo = new HashMap<>();
		datastoreInfo.put("datastore_id", datastore_id);
		
		return datastoreInfo;
	}

	
	/***
	 * @author shan.jing
	 * @param sourceList
	 * @return
	 */
	public String[] getAssignPolicyInfo(String sourceList) {
		String[] sourceArrayList=sourceList.replace(" ", "").split(",");
		return sourceArrayList;
	}
	
	/***
	 * @author shan.jing
	 * @param sourceList
	 * @return
	 */
	public String[] getUnAssignPolicyInfo(String sourceList) {
		String[] sourceArrayList=sourceList.replace(" ", "").split(",");
		return sourceArrayList;
	}
	
	/**
	 * @author shuo.zhang
	 * @param first_name
	 * @param last_name
	 * @param email
	 * @param phone_number
	 * @param organization_name
	 * @param organization_type
	 * @param datacenter_id
	 * @return
	 */
	public HashMap<String, Object > composeEnrollmentInfo(String first_name, String last_name, String email, String phone_number,
			String organization_name, String organization_type, String datacenter_id){
		
		HashMap<String, String> userInfo = new HashMap<String, String>();
		userInfo.put("first_name", first_name);
		userInfo.put("last_name", last_name);
		userInfo.put("email", email );
		if(phone_number!="") {
			userInfo.put("phone_number", phone_number );
		}else {
			userInfo.put("phone_number", null );
		}
		HashMap<String, String> orgInfo = new HashMap<String, String>();
		String prefix="AUTO_";
		if(organization_name!=null&&organization_name!=""&&(organization_name.toLowerCase().indexOf("do_not_delete")==-1)){
			organization_name=prefix+organization_name;
		}
		orgInfo.put("organization_name", organization_name);
		orgInfo.put("organization_type", organization_type);
		orgInfo.put("datacenter_id", datacenter_id);
		//orgInfo.put("clouddirect_trial", "true");
		//orgInfo.put("phone_number", "1050890529");

		
		HashMap<String, Object > enrollmentInfo = new HashMap<String, Object >();
		if((first_name==null) && (last_name==null)&& (email==null) && (phone_number==null)){
			enrollmentInfo.put("user" , null);
		}else{
			enrollmentInfo.put("user" , userInfo);
		}
		if((organization_name==null) && (organization_type==null)&& (datacenter_id==null)){
			enrollmentInfo.put("organization", null);
		}else{
			enrollmentInfo.put("organization", orgInfo);
		}
		enrollmentInfo.put("clouddirect_trial", "true");
		
	
		
		return enrollmentInfo;
	}


	/**
	 * the function is used to compose alert information
	 * 
	 * @author ramya.Nagepalli
	 * @param job_id
	 * @param jobtype
	 * @param jobSeverity
	 * @param alert_data 
	 * @param alert_details 
	 */
	public Map<String, Object> composeAlertInfo(String job_id, String jobtype, String jobSeverity, HashMap<String, Object> alert_details, ArrayList<String> alert_data) {
		// TODO Auto-generated method stub
		Map<String, Object> alertInfo = new HashMap<>();
		if ((job_id != null) && (!job_id.equals(""))) {
			alertInfo.put("job_id", job_id);
		}
		if ((jobtype != null) && (!jobtype.equals(""))) {
			alertInfo.put("type", jobtype);
		}
		if ((jobSeverity != null) && (!jobSeverity.equals(""))) {
			alertInfo.put("severity", jobSeverity);
		}
		if ((alert_details != null) && (!alert_details.equals(""))) {
			alertInfo.put("alert_details", alert_details);
		}
		if ((alert_data != null) && (!alert_data.equals(""))) {
			alertInfo.put("alert_data", alert_data);
		}

		return alertInfo;
	}
	
	/**
	 * the function is used to getUpdateAccount information
	 * 
	 * @author ramya.Nagepalli
	 * @param accountName
	 * @param blocked
	 * return accountInfo
	 */
	
	public Map<String, String> getUpdateAccountInfo(String accountName, String blocked) {
		// TODO Auto-generated method stub
		Map<String, String> accountInfo = new HashMap<>();
		accountInfo.put("organization_name", accountName);
		accountInfo.put("blocked", blocked);
		return accountInfo;
	}
	

	public HashMap<String, Object> updateUserInfo(String email, String password, String first_name, String last_name,
			String role_id, String organization_id, String phone_number, HashMap<String, Object> msp_accounts_info) {

		HashMap<String, Object> updateUserInfo = new HashMap<>();
		if ((email != null) && (!email.equals(""))) {
			updateUserInfo.put("email", email);
		}
		if ((password != null) && (!password.equals(""))) {
			updateUserInfo.put("password", password);
		}
		if ((first_name != null) && (!first_name.equals(""))) {
			updateUserInfo.put("first_name", first_name);
		}
		if ((last_name != null) && (!last_name.equals(""))) {
			updateUserInfo.put("last_name", last_name);
		}
		if ((role_id != null) && (!role_id.equals(""))) {
			updateUserInfo.put("role_id", role_id);
		}
		if ((organization_id != null) && (!organization_id.equals(""))) {
			updateUserInfo.put("organization_id", organization_id);
		}
		if ((phone_number != null) && (!phone_number.equals(""))) {
			updateUserInfo.put("phone_number", phone_number);
		}
		if (msp_accounts_info != null)  {
			updateUserInfo.put("msp_accounts", msp_accounts_info);
		}
		return updateUserInfo;
	}

	/**
	 * @author shuo.zhang
	 * @param orgInfo
	 * @return
	 */
	public Map<String, Object> composeAssignAccountsInfo(Object orgInfo) {
		// TODO Auto-generated method stub
		HashMap<String, Object> accountInfo = new HashMap<>();
		accountInfo.put("organizations", orgInfo);		
		return accountInfo;
	}

	/**
	 *  @author shuo.zhang
	 * @param masteredAccountIds
	 * @return
	 */
	public Object composeAssignAccountsIdsInfo(String masteredAccountIds) {
		// TODO Auto-generated method stub
		
			
		HashMap<String, String>  orgMap = new HashMap<String, String> ();
		Object[] orgArray = null;
		if(masteredAccountIds == null){
			orgArray = new Object[1];
			orgMap.put("organization_id", null);
			orgArray[0]=orgMap;
				
		}else if(masteredAccountIds.equals("")){
			orgArray = new Object[1];
			orgMap.put("organization_id", "");
			orgArray[0]=orgMap;
		}else{
			String[] orgIds = masteredAccountIds.split(";");
 			orgArray = new Object[orgIds.length];
 			for(int i=0; i<orgIds.length;i++){
 				orgMap = new HashMap<String, String> ();
 				orgMap.put("organization_id", orgIds[i]);
 				orgArray[i] = orgMap;
 			}
 			
		}
		return orgArray;
	}

	/**
	 * the function is used to composeDestinationUsageDetails 
	 * 
	 * @author ramya.Nagepalli
	 * @param timeStamp
	 * @param blocked
	 * @param primary_usage
	 * @param snapshot_usage
	 * return DestinationUsageDetails
	 */
	
	public Map<String, Object> composeDestinationUsageDetails(String destination_id, String timeStamp,
			String primary_usage, String snapshot_usage) {
		// TODO Auto-generated method stub
		Map<String, Object> DestinationUsageDetails=new HashMap<String,Object>();
		HashMap<String,Object> usage=new HashMap<String,Object>();
		
		if(!(destination_id==null||destination_id==""))
		{
			DestinationUsageDetails.put("destination_id", destination_id);	
		}
		if(!(timeStamp==null||timeStamp==""))
		{
		DestinationUsageDetails.put("timestamp", timeStamp);
		}
		
		if(!(primary_usage==null||primary_usage==""))
		{
		usage.put("primary", primary_usage);
		}
		
		if(!(snapshot_usage==null||snapshot_usage==""))
		{
			usage.put("snapshot", snapshot_usage);
		}
		
		
		
		DestinationUsageDetails.put("usage", usage);
		
		return DestinationUsageDetails;
	}

	/**
	 * @author shuo.zhang
	 * @param email
	 * @return
	 */
	public Map<String, Object> composeEmailInfo(String email){
		
		Map<String, Object> emailInfo=new HashMap<String,Object>();
		emailInfo.put("email", email);
		return emailInfo;
	}

	/**
	 * MapDashboardWidgetsInfo
	 * 
	 * @author Ramya.Nagepalli
	 * @param expected_widgets
	 * @return dashboardWidgetsInfo
	 */
	
	public Map<String, ArrayList<HashMap<String, Object>>> MapDashboardWidgetsInfo(
			ArrayList<HashMap<String, Object>> expected_widgets) {
		// TODO Auto-generated method stub
		Map<String, ArrayList<HashMap<String, Object>>> dashboardWidgetsInfo = new HashMap<>();

		dashboardWidgetsInfo.put("widgets", expected_widgets);
		return dashboardWidgetsInfo;
	}
	/**
	 * composeDestinationFilterInfo
	 * 
	 * @author Ramya.Nagepalli
	 * @param organization_id
	 * @param filterName
	 * @param destinationName
	 * @param policyID
	 * @param destinationType
	 * @param isDefault
	 * @return
	 */
		public Map<String, Object> composeDestinationFilterInfo(String organization_id, String filterName,
				String destinationName, String policyID, String destinationType, String isDefault) {
			Map<String, Object> destinationFilterInfo = new HashMap<String, Object>();

			destinationFilterInfo.put("filter_name", filterName);
			if (!"none".equalsIgnoreCase(destinationName)) {
				destinationFilterInfo.put("destination_name", destinationName);
			}
			String[] emptyArray = new String[0];

			if (policyID == null || policyID == "" || policyID.equalsIgnoreCase("invalidString")) {
				destinationFilterInfo.put("policy_id", policyID);
			} else if (policyID.equalsIgnoreCase("emptyarray")) {
				destinationFilterInfo.put("policy_id", emptyArray);
			} else if (!policyID.equalsIgnoreCase("none")) {
				destinationFilterInfo.put("policy_id", policyID.replace(" ", "").split(","));
			}
			if (!"none".equalsIgnoreCase(destinationType)) {
				destinationFilterInfo.put("destination_type", destinationType);
			}
			if (!"none".equalsIgnoreCase(organization_id)) {
				destinationFilterInfo.put("organization_id", organization_id);
			}
			if (null == isDefault) {
				destinationFilterInfo.put("is_default", isDefault);
			} else if (!"none".equalsIgnoreCase(isDefault)) {
				if (isDefault.equalsIgnoreCase("true") || isDefault.equalsIgnoreCase("false")) {
					destinationFilterInfo.put("is_default", Boolean.valueOf(isDefault));
				} else {
					destinationFilterInfo.put("is_default", isDefault);
				}
			}
			return destinationFilterInfo;
		}

		/**
		 * composeJobFilterEx_savesearch
		 * 
		 * @author Ramya.Nagepalli
		 * @param jobStatus
		 * @param policyID
		 * @param jobType
		 * @param startTimeType
		 * @param startTimeTSStart
		 * @param startTimeTSEnd
		 * @param organization_id
		 * @param filterName
		 * @param isDefault
		 * @param job_filter_type
		 * @return
		 */
		public Map<String, Object> composeJobFilterEx_savesearch(String jobStatus, String policyID, String jobType,
				String startTimeType, String startTimeTSStart, String startTimeTSEnd, String organization_id,
				String filterName, String isDefault, String job_filter_type) {
			// TODO Auto-generated method stub
			Map<String, Object> jobFilterInfo = new HashMap<>();

			if (!"none".equalsIgnoreCase(filterName)) {
				jobFilterInfo.put("filter_name", filterName);
			}

			if (!"none".equalsIgnoreCase(organization_id)) {
				jobFilterInfo.put("organization_id", organization_id);
			}

			if (null == isDefault) {
				jobFilterInfo.put("is_default", isDefault);
			} else if (!"none".equalsIgnoreCase(isDefault)) {
				if (isDefault.equalsIgnoreCase("true") || isDefault.equalsIgnoreCase("false")) {
					jobFilterInfo.put("is_default", Boolean.valueOf(isDefault));
				} else {
					jobFilterInfo.put("is_default", isDefault);
				}
			}

			String[] emptyArray = new String[0];

			if (jobStatus == null || jobStatus == "" || jobStatus.equalsIgnoreCase("invalidString")) {
				jobFilterInfo.put("job_status", jobStatus);
			} else if (jobStatus.equalsIgnoreCase("emptyarray")) {
				jobFilterInfo.put("job_status", emptyArray);
			} else if (!jobStatus.equalsIgnoreCase("none")) {
				jobFilterInfo.put("job_status", jobStatus.replace(" ", "").split(","));
			}

			if (policyID == null || policyID == "" || policyID.equalsIgnoreCase("invalidString")) {
				jobFilterInfo.put("policy_id", policyID);
			} else if (policyID.equalsIgnoreCase("emptyarray")) {
				jobFilterInfo.put("policy_id", emptyArray);
			} else if (!policyID.equalsIgnoreCase("none")) {
				jobFilterInfo.put("policy_id", policyID.replace(" ", "").split(","));
			}

			if (jobType == null || jobType == "" || jobType.equalsIgnoreCase("invalidString")) {
				jobFilterInfo.put("job_type", jobType);
			} else if (jobType.equalsIgnoreCase("emptyarray")) {
				jobFilterInfo.put("job_type", emptyArray);
			} else if (!jobType.equalsIgnoreCase("none")) {
				jobFilterInfo.put("job_type", jobType.replace(" ", "").split(","));
			}

			Map<String, Object> startTimeTSInfo = new HashMap<String, Object>();
			if (!"none".equalsIgnoreCase(startTimeType)) {
				startTimeTSInfo.put("type", startTimeType);
			}
			startTimeTSInfo.put("start_ts", startTimeTSStart);
			startTimeTSInfo.put("end_ts", startTimeTSEnd);

			if (startTimeTSInfo.size() != 0) {
				jobFilterInfo.put("start_time_ts", startTimeTSInfo);
			}
			if (!"none".equalsIgnoreCase(job_filter_type)) {
				jobFilterInfo.put("job_filter_type", job_filter_type);
			}

			return jobFilterInfo;
		}

		/**
		 * composeLogFilterInfo
		 * 
		 * @author Ramya.Nagepalli
		 * @param organization_id
		 * @param filter_name
		 * @param logTimeRange
		 * @param job_type
		 * @param log_severity_type
		 * @param is_default
		 * @param test
		 * @return
		 */
		public Map<String, Object> composeLogFilterInfo(String organization_id, String filter_name,
				Map<String, Object> logTimeRange, String job_type, String log_severity_type, String is_default,
				ExtentTest test) {
			Map<String, Object> logFilterInfo = new HashMap<>();
			if (null != logTimeRange) {
				logFilterInfo.put(LogColumnConstants.DATETIME_FILTER, logTimeRange);
			}
			if (!"none".equalsIgnoreCase(filter_name)) {
				logFilterInfo.put("filter_name", filter_name);
			}

			if (null == is_default) {
				logFilterInfo.put("is_default", is_default);
			} else if (!"none".equalsIgnoreCase(is_default)) {
				if (is_default.equalsIgnoreCase("true") || is_default.equalsIgnoreCase("false")) {
					logFilterInfo.put("is_default", Boolean.valueOf(is_default));
				} else {
					logFilterInfo.put("is_default", is_default);
				}
			}

			String[] emptyArray = new String[0];

			if (job_type == null || job_type == "" || job_type.equalsIgnoreCase("invalidString")) {
				logFilterInfo.put(LogColumnConstants.JOB_TYPE_FILTER, job_type);
			} else if (job_type.equalsIgnoreCase("emptyarray")) {
				logFilterInfo.put(LogColumnConstants.JOB_TYPE_FILTER, emptyArray);
			} else if (!job_type.equalsIgnoreCase("none")) {
				logFilterInfo.put(LogColumnConstants.JOB_TYPE_FILTER, job_type.replace(" ", "").split(","));
			}

			if (log_severity_type == null || log_severity_type == ""
					|| log_severity_type.equalsIgnoreCase("invalidString")) {
				logFilterInfo.put(LogColumnConstants.SERERITY_FILTER, log_severity_type);
			} else if (log_severity_type.equalsIgnoreCase("emptyarray")) {
				logFilterInfo.put(LogColumnConstants.SERERITY_FILTER, emptyArray);
			} else if (!log_severity_type.equalsIgnoreCase("none")) {
				logFilterInfo.put(LogColumnConstants.SERERITY_FILTER, log_severity_type.replace(" ", "").split(","));
			}
			logFilterInfo.put("organization_id", organization_id);

			return logFilterInfo;
		}

		/**
		 * composePolicyFilter
		 * 
		 * @author Ramya.Nagepalli
		 * @param organization_id
		 * @param filter_name
		 * @param policy_name
		 * @param group_id
		 * @param last_backup_status
		 * @param status
		 * @param is_default
		 * @return
		 */

		public HashMap<String, Object> composePolicyFilter_savesearch(String filter_name, String organization_id,
				 String status, String is_default, String policy_filter_type) {
			HashMap<String, Object> filterInfo = new HashMap<String, Object>();
			String[] emptyArray = new String[0];

			if (filter_name == null) {
				filterInfo.put("filter_name", filter_name);
			} else if (!filter_name.equalsIgnoreCase("none")) {
				filterInfo.put("filter_name", filter_name);
			}

			if (policy_filter_type == null) {
				filterInfo.put("policy_filter_type", policy_filter_type);
			} else if (!policy_filter_type.equalsIgnoreCase("none")) {
				filterInfo.put("policy_filter_type", policy_filter_type);
			}


			if (status == null || status == "") {
				filterInfo.put("status", status);
			} else if (status.equalsIgnoreCase("emptyarray")) {
				filterInfo.put("status", emptyArray);
			} else if (!status.equalsIgnoreCase("none")) {
				filterInfo.put("status", status.replace(" ", "").split(","));
			}

			if (is_default == null || is_default == "") {
				filterInfo.put("is_default", is_default);
			} else if (is_default.equalsIgnoreCase("true")) {
				filterInfo.put("is_default", true);
			} else if (is_default.equalsIgnoreCase("false")) {
				filterInfo.put("is_default", false);
			} else if (!is_default.equalsIgnoreCase("none")) {
				filterInfo.put("is_default", is_default);
			}

			filterInfo.put("organization_id", organization_id);
			return filterInfo;
		}

		/**
		 * composeSourceFilterInfo
		 * 
		 * @author Ramya.Nagepalli
		 * @param organization_id
		 * @param filter_name
		 * @param protection_status
		 * @param connection_status
		 * @param policy_id
		 * @param last_backup_status
		 * @param group_id
		 * @param os_major
		 * @param source_type
		 * @param is_default
		 * @return
		 */
		public Map<String, Object> composeSourceFilterInfo(String organization_id, String filter_name,
				String protection_status, String connection_status, String policy_id, String last_backup_status,
				String group_id, String os_major, String source_type, String is_default) {
			Map<String, Object> filterInfo = composeFilterInfo(filter_name, protection_status, connection_status, policy_id,
					last_backup_status, group_id, os_major);

			if (is_default == null || is_default == "") {
				filterInfo.put("is_default", is_default);
			} else if (is_default.equalsIgnoreCase("true")) {
				filterInfo.put("is_default", true);
			} else if (is_default.equalsIgnoreCase("false")) {
				filterInfo.put("is_default", false);
			} else if (!is_default.equalsIgnoreCase("none")) {
				filterInfo.put("is_default", is_default);
			}

			if (is_default == null || is_default == "") {
				filterInfo.put(SourceColumnConstants.IS_DEFAULT, is_default);
			} else if (is_default.equalsIgnoreCase("true")) {
				filterInfo.put(SourceColumnConstants.IS_DEFAULT, true);
			} else if (is_default.equalsIgnoreCase("false")) {
				filterInfo.put(SourceColumnConstants.IS_DEFAULT, false);
			} else if (!is_default.equalsIgnoreCase("none")) {
				filterInfo.put(SourceColumnConstants.IS_DEFAULT, is_default);
			}

			if (!"none".equalsIgnoreCase(source_type)) {
				filterInfo.put(SourceColumnConstants.TYPE_FILTER, source_type);
			}
			if (!"none".equalsIgnoreCase(organization_id)) {
				filterInfo.put("organization_id", organization_id);
			}
			return filterInfo;
		}

		/**
		 *composeUserFilterInfo_savesearch
		 *
		 * @author Ramya.Nagepalli
		 * @param filter_name
		 * @param search_string
		 * @param user_is_blocked
		 * @param user_status
		 * @param role_id
		 * @param is_default
		 * @param organization_id
		 * @param user_filter_type
		 * @return
		 */
		public HashMap<String, Object> composeUserFilterInfo_savesearch(String filter_name, String search_string,
				String user_is_blocked, String user_status, String role_id, String is_default, String organization_id,
				String user_filter_type) {

			HashMap<String, Object> userFilterInfo = new HashMap<>();
			userFilterInfo.put("filter_name", filter_name);
			if (search_string == null || search_string == "") {
				userFilterInfo.put("search_string", search_string);
			} else if (!search_string.equalsIgnoreCase("none")) {
				userFilterInfo.put("search_string", search_string);
			}

			if (user_is_blocked == null || user_is_blocked == "") {
				userFilterInfo.put("blocked", user_is_blocked);
			} else if (!user_is_blocked.equalsIgnoreCase("none")) {
				userFilterInfo.put("blocked", user_is_blocked);
			}

			if (user_status == null || user_status == "") {
				userFilterInfo.put("status", user_status);
			} else if (!user_status.equalsIgnoreCase("none")) {
				userFilterInfo.put("status", user_status);
			}

			if (role_id == null || role_id == "") {
				userFilterInfo.put("role_id", role_id);
			} else if (!role_id.equalsIgnoreCase("none")) {
				userFilterInfo.put("role_id", role_id);
			}

			if (is_default == null || is_default == "") {
				userFilterInfo.put("is_default", true);
			} else {
				userFilterInfo.put("is_default", Boolean.parseBoolean(is_default));
			}

			if (organization_id == null || organization_id == "") {
				userFilterInfo.put("organization_id", organization_id);
			} else if (!role_id.equalsIgnoreCase("none")) {
				userFilterInfo.put("organization_id", organization_id);
			}
			if (user_filter_type == null || user_filter_type == "") {
				userFilterInfo.put("user_filter_type", user_filter_type);
			} else if (!role_id.equalsIgnoreCase("none")) {
				userFilterInfo.put("user_filter_type", user_filter_type);
			}

			return userFilterInfo;
		}

		/**
		 * composeOrgInfo to update org details
		 * 
		 * @author Ramya.Nagepalli
		 * @param limit
		 * @param clouddirect_deletion_queue_length
		 * @param clouddirect_trial_length
		 * @param cloudhybrid_trial_length
		 * @param cloudhybrid_deletion_queue_length
		 * @return
		 */
		public Map<String, String> composeOrgInfo(int limit, int clouddirect_trial_length,
				int clouddirect_deletion_queue_length, int cloudhybrid_trial_length,
				int cloudhybrid_deletion_queue_length) {
			Map<String, String> orginfo = new HashMap<>();

			orginfo.put("clouddirect_volume_count", Integer.toString(limit));
			if (clouddirect_trial_length != 0)
				orginfo.put("clouddirect_trial_length", Integer.toString(clouddirect_trial_length));
			if (clouddirect_deletion_queue_length != 0)
				orginfo.put("clouddirect_deletion_queue_length", Integer.toString(clouddirect_deletion_queue_length));

			if (cloudhybrid_trial_length != 0)
				orginfo.put("cloudhybrid_trial_length", Integer.toString(cloudhybrid_trial_length));
			if (cloudhybrid_deletion_queue_length != 0)
				orginfo.put("cloudhybrid_deletion_queue_length", Integer.toString(cloudhybrid_deletion_queue_length));

			return orginfo;
		}
		
		/**
		 * composeRecoveryInfoForSource
		 * 
		 * @author ramya.nagepalli
		 * @param from_path
		 * @param from_source_id
		 * @param image_format
		 * @param snapshot_host
		 * @param snapshot_path
		 * @param task_type
		 * @param to_path
		 * @param to_source_id
		 * @return
		 */
		public Map<String, Object> composeRecoveryInfoForSource(String from_path, String from_source_id,
				String image_format, String snapshot_host, String snapshot_path, String task_type, String to_path,
				String to_source_id) {
			Map<String, Object> recoveryInfo = new HashMap<>();
			if (!from_path.equals(""))
				recoveryInfo.put("from_path", from_path);
			else
				recoveryInfo.put("from_path", "' '");

			recoveryInfo.put("from_source_id", from_source_id);
			recoveryInfo.put("image_format", image_format);
			recoveryInfo.put("snapshot_host", snapshot_host);
			recoveryInfo.put("snapshot_path", snapshot_path);
			recoveryInfo.put("task_type", task_type);
			if (!to_path.equals(""))
				recoveryInfo.put("to_path", to_path);
			else
				recoveryInfo.put("to_path", "' '");
			recoveryInfo.put("to_source_id", to_source_id);

			return recoveryInfo;
		}
		
		/**
		 * 
		 * ComposeBrandingemailerinfo - removed  param "social media platform" - Sprint 34
		 * 
		 * @author Ramya.Nagepalli
		 * @param organization_id
		 * @param emailer_name
		 * @param email
		 * @param signature
		 * @param support_call
		 * @param support_chat
		 * @param support_sales
		 * @param support_email
		 * @param facebook_link
		 * @param twitter_link
		 * @param linkdin_link
		 * @param legal_notice
		 * @param contact_us
		 * @param privacy
		 * @param copyrights
		 * @param branding_from
		 * @return
		 */
		public HashMap<String, Object> ComposeBrandingemailerinfo (String organization_id, 
				String emailer_name, 
				String email,
				String signature,
				String support_call,
				String support_chat,
				String support_sales,
				String support_email,
				String facebook_link,
				String twitter_link,
				String linkdin_link,
				String legal_notice,
				String contact_us,
				String privacy,
				String copyrights,
				String branding_from
				){
			
			HashMap<String, Object> emailerInfo = new HashMap<>();
			
			if(!(organization_id==null||organization_id==""))
			emailerInfo.put("organization_id", organization_id);
			
			if(!(emailer_name==null||emailer_name==""))
				emailerInfo.put("emailer_name", emailer_name);
			
			if(!(email==null||email==""))
				emailerInfo.put("email", email);
			
			if(!(signature==null||signature==""))
				emailerInfo.put("signature", signature);
			
			if(!(support_call==null||support_call==""))
				emailerInfo.put("support_call", support_call);
			
			if(!(support_chat==null||support_chat==""))
				emailerInfo.put("support_chat", support_chat);
			
			if(!(support_sales==null||support_sales==""))
				emailerInfo.put("support_sales", support_sales);
			
			if(!(support_email==null||support_email==""))
				emailerInfo.put("support_email", support_email);
			
			if(!(facebook_link==null||facebook_link==""))
				emailerInfo.put("facebook_link", facebook_link);
			
			if(!(twitter_link==null||twitter_link==""))
				emailerInfo.put("twitter_link", twitter_link);
			
			if(!(linkdin_link==null||linkdin_link==""))
				emailerInfo.put("linkdin_link", linkdin_link);
			
			if(!(legal_notice==null||legal_notice==""))
					emailerInfo.put("legal_notice", legal_notice);
		
			if(!(contact_us==null||contact_us==""))
				emailerInfo.put("contact_us", contact_us);
			
			if(!(privacy==null||privacy==""))
				emailerInfo.put("privacy", privacy);
			
			if(!(copyrights==null||copyrights==""))
				emailerInfo.put("copyrights", copyrights);
			
			if(!(branding_from==null||branding_from==""))
				emailerInfo.put("branding_from", branding_from);
			
			return emailerInfo;
		}
		
		/**
		 * Includes preference language added in Sprint 34
		 * 
		 * @author Rakesh.Chalamala
		 * @param first_name
		 * @param last_name
		 * @param email
		 * @param phone_number
		 * @param role_id
		 * @param organization_id
		 * @param preference_language
		 * @param password
		 * @return
		 */
		public Map<String, Object> composeUserInfo(String first_name,
														String last_name,
														String email,
														String phone_number,
														String role_id,
														String organization_id,
														String preference_language,
														String password ){
			
			Map<String, Object> userInfo = new HashMap<>();
			userInfo.put("first_name", first_name);
			userInfo.put("last_name", last_name);
			userInfo.put("email", email);
			userInfo.put("phone_number", phone_number);
			userInfo.put("role_id", role_id);
			userInfo.put("organization_id", organization_id);
			if (preference_language == null) {
				userInfo.put("preference_language", preference_language);
			}else if(!preference_language.equalsIgnoreCase("none")) {
				userInfo.put("preference_language", preference_language);	
			}			
			userInfo.put("password", password);
								
			return userInfo;
		}
		/**
		 * 2019-01-24, added group_id and view_type
		 * 
		 * @author Ramya.Nagepalli
		 * @param jobStatus
		 * @param policyID
		 * @param resourceID
		 * @param jobType
		 * @param startTimeTSStart
		 * @param startTimeTSEnd
		 * @param filterName
		 * @param isDefault
		 * @param group_id
		 * @param organization_id
		 * @param view_type
		 * @return
		 */
		public Map<String, Object> composeJobFilterEx(String jobStatus, String policyID, String resourceID, String jobType,
				String startTimeType, String startTimeTSStart, String startTimeTSEnd, String sourceName, String filterName,
				String isDefault, String group_id, String organization_id, String view_type) {
			Map<String, Object> jobFilterInfo = new HashMap<>();

			if (!"none".equalsIgnoreCase(filterName)) {
				jobFilterInfo.put("filter_name", filterName);
			}

			if (!"none".equalsIgnoreCase(sourceName)) {
				jobFilterInfo.put("source_name", sourceName);
			}

			if (null == isDefault) {
				jobFilterInfo.put("is_default", isDefault);
			} else if (!"none".equalsIgnoreCase(isDefault)) {
				if (isDefault.equalsIgnoreCase("true") || isDefault.equalsIgnoreCase("false")) {
					jobFilterInfo.put("is_default", Boolean.valueOf(isDefault));
				} else {
					jobFilterInfo.put("is_default", isDefault);
				}
			}

			String[] emptyArray = new String[0];

			if (jobStatus == null || jobStatus == "" || jobStatus.equalsIgnoreCase("invalidString")) {
				jobFilterInfo.put("job_status", jobStatus);
			} else if (jobStatus.equalsIgnoreCase("emptyarray")) {
				jobFilterInfo.put("job_status", emptyArray);
			} else if (!jobStatus.equalsIgnoreCase("none")) {
				jobFilterInfo.put("job_status", jobStatus.replace(" ", "").split(","));
			}

			if (policyID == null || policyID == "" || policyID.equalsIgnoreCase("invalidString")) {
				jobFilterInfo.put("policy_id", policyID);
			} else if (policyID.equalsIgnoreCase("emptyarray")) {
				jobFilterInfo.put("policy_id", emptyArray);
			} else if (!policyID.equalsIgnoreCase("none")) {
				jobFilterInfo.put("policy_id", policyID.replace(" ", "").split(","));
			}

			if (resourceID == null || resourceID == "" || resourceID.equalsIgnoreCase("invalidString")) {
				jobFilterInfo.put("source_id", resourceID);
			} else if (resourceID.equalsIgnoreCase("emptyarray")) {
				jobFilterInfo.put("source_id", emptyArray);
			} else if (!resourceID.equalsIgnoreCase("none")) {
				jobFilterInfo.put("source_id", resourceID.replace(" ", "").split(","));
			}

			if (jobType == null || jobType == "" || jobType.equalsIgnoreCase("invalidString")) {
				jobFilterInfo.put("job_type", jobType);
			} else if (jobType.equalsIgnoreCase("emptyarray")) {
				jobFilterInfo.put("job_type", emptyArray);
			} else if (!jobType.equalsIgnoreCase("none")) {
				jobFilterInfo.put("job_type", jobType.replace(" ", "").split(","));
			}

			Map<String, String> startTimeTSInfo = new HashMap<String, String>();
			if (!"none".equalsIgnoreCase(startTimeType)) {
				startTimeTSInfo.put("type", startTimeType);
			}
			if (!"none".equalsIgnoreCase(startTimeTSStart)) {
				startTimeTSInfo.put("start_ts", startTimeTSStart);
			}
			if (!"none".equalsIgnoreCase(startTimeTSEnd)) {
				startTimeTSInfo.put("end_ts", startTimeTSEnd);
			}
			if (startTimeTSInfo.size() != 0) {
				jobFilterInfo.put("start_time_ts", startTimeTSInfo);
			}

			if (group_id == null || group_id == "" || group_id.equalsIgnoreCase("invalidString")) {
				jobFilterInfo.put("group_id", group_id);
			} else if (group_id.equalsIgnoreCase("emptyarray")) {
				jobFilterInfo.put("group_id", emptyArray);
			} else if (!group_id.equalsIgnoreCase("none")) {
				jobFilterInfo.put("group_id", group_id.replace(" ", "").split(","));
			}

			jobFilterInfo.put("view_type", view_type);
			jobFilterInfo.put("organization_id", organization_id);

			return jobFilterInfo;
		}

		/**
		 * Composes request payload for API - POST: /organizations/{id}/submspaccounts
		 * 
		 * @author Rakesh.Chalamala
		 * @param organization_name
		 * @param parent_id
		 * @param first_name
		 * @param last_name
		 * @param email
		 * @param datacenter_id
		 * @return
		 */
		public HashMap<String, Object> composeSubMspRqstInfo(String organization_name,
																String parent_id,
																String first_name,
																String last_name,
																String email,
																String datacenter_id
																){
			HashMap<String, Object> requestInfo = new HashMap<>();
			if (organization_name != ("none")) {
				requestInfo.put("organization_name", organization_name);	
			}
			if (parent_id != ("none")) {
				requestInfo.put("parent_id", parent_id);	
			}
			if (first_name != ("none")) {
				requestInfo.put("first_name", first_name);	
			}
			if (last_name != ("none")) {
				requestInfo.put("last_name", last_name);	
			}
			if (email != ("none")) {
				requestInfo.put("email", email);	
			}
			if (datacenter_id != ("none")) {
				requestInfo.put("datacenter_id", datacenter_id);	
			}
						
			return requestInfo;
		}

		/**
		 * @author Ramya.Nagepalli
		 * @param email
		 * @param password
		 * @param first_name
		 * @param last_name
		 * @param role_id
		 * @param organization_id
		 * @param phone_number
		 * @param preference_language
		 * @return
		 */
		public Map<String, String> updateUserInfo(String email, String password, String first_name, String last_name,
				String role_id, String organization_id, String phone_number, String preference_language ) {

			Map<String, String> updateUserInfo = new HashMap<>();
			if ((email != null) && (!email.equals(""))) {
				updateUserInfo.put("email", email);
			}
			if ((password != null) && (!password.equals(""))) {
				updateUserInfo.put("password", password);
			}
			if ((first_name != null) && (!first_name.equals(""))) {
				updateUserInfo.put("first_name", first_name);
			}
			if ((last_name != null) && (!last_name.equals(""))) {
				updateUserInfo.put("last_name", last_name);
			}
			if ((role_id != null) && (!role_id.equals(""))) {
				updateUserInfo.put("role_id", role_id);
			}
			if ((organization_id != null) && (!organization_id.equals(""))) {
				updateUserInfo.put("organization_id", organization_id);
			}
			updateUserInfo.put("phone_number", phone_number);
			
			if (preference_language == null) {
				updateUserInfo.put("preference_language", preference_language);
			}else if(!preference_language.equalsIgnoreCase("none")) {
				updateUserInfo.put("preference_language", preference_language);	
			}

			return updateUserInfo;
		}
		
		public Map<String, Object> createSubMSPAccount(String organizationName, String parantId,
			      String firstName, String lastName, String email,
			      String datacenterId) {
			Map<String, Object> organizationInfo = new HashMap<>();
			String prefix="AUTO_";
			if(organizationName!=null&&organizationName!=""){
				organizationName=prefix+organizationName;
			}
			organizationInfo.put("organization_name", organizationName);
			organizationInfo.put("parent_id", parantId);
			organizationInfo.put("email", email);
			organizationInfo.put("first_name", firstName);
			organizationInfo.put("last_name", lastName);
			if(datacenterId !=null) {
				organizationInfo.put("datacenter_id", datacenterId);
			}
			return organizationInfo;
		}
		
		public Map<String, String> updateOrganizationDatacenterID(String datacenter_id) {

			Map<String, String> updateOrgInfo = new HashMap<>();
			if ((datacenter_id != null) && (!datacenter_id.equals(""))) {
				updateOrgInfo.put("datacenter_id", datacenter_id);
			}

			return updateOrgInfo;
		}

		/**
		 * composeAlertsInfo
		 * 
		 * @author Ramya.Nagepalli
		 * @param alert_type
		 * @param alert_name
		 * @param org_id
		 * @param recipients
		 * @param report_for_type
		 * @return
		 */
	public Map<String, Object> composeAlertsInfo(String alert_type, String alert_name, String org_id, String recipients,
			String report_for_type, String extra_ids) {

		Map<String, Object> alertsInfo = new HashMap<>();
		alertsInfo.put("alert_type", alert_type);
		alertsInfo.put("alert_name", alert_name);
		alertsInfo.put("organization_id", org_id);

		ArrayList<String> item = new ArrayList<>();

		String[] items = recipients.split(Pattern.quote(","));

		if (!recipients.isEmpty() && items.length > 0)
			for (String i : items)
				item.add(i);

		alertsInfo.put("recipient", item);
		ArrayList<String> details = null;
		HashMap<String, Object> report_for = new HashMap<String, Object>();
		if(report_for_type.equalsIgnoreCase("selected_source_groups")||report_for_type.equalsIgnoreCase("selected_organizations"))
		{
			details=new ArrayList<>();
			if(!(extra_ids.equals("")))
			{
			String[] extra_id=extra_ids.split(Pattern.quote(","));
			if(extra_id.length>0)
				for (String i : extra_id)
					details.add(i);
			}
		}
		
		report_for.put("type", report_for_type);
		report_for.put("details", details);
		alertsInfo.put("report_for", report_for);

		return alertsInfo;
	}
	/**
	 * composeFilterInfo
	 * 
	 * @author Ramya.Nagepalli
	 * @param filter_type
	 * @param filter_name
	 * @param user_id
	 * @param organization_id
	 * @return
	 */
	public HashMap<String,Object> composeFilterInfo(String filter_type,String filter_name,String user_id,String organization_id,boolean is_default,HashMap<String,Object> expected_info)
	{
		HashMap<String,Object> filter_info=new HashMap<>();
		filter_info=expected_info;
		
		filter_info.put("is_default", is_default);
		
		if(!filter_type.equalsIgnoreCase("none"))
			filter_info.put("filter_type", filter_type);
		
		if(!filter_name.equalsIgnoreCase("none"))
			filter_info.put("filter_name", filter_name);
		
		if(!user_id.equalsIgnoreCase("none"))
			filter_info.put("user_id", user_id);
		
		if(!organization_id.equalsIgnoreCase("none"))
			filter_info.put("organization_id", organization_id);
			
		return filter_info;
	}


	/**
	 * composeAdditionalFilter
	 * 
	 * @author Ramya.Nagepalli
	 * @param filter_type
	 * @param user_id
	 * @param organization_id
	 * @return
	 */
	public String composeAdditionalFilter(String filter_type,String user_id,String organization_id,String extraURL)
	{
		String additionalURL="";
		if(!(filter_type.equals("none")))
		{
			additionalURL+="filter_type="+filter_type;
		}
		 if(!(user_id.equals("none")))
		{
			additionalURL+="&user_id="+user_id;
		} if(!(organization_id.equals("none")))
		{
			additionalURL+="&organization_id="+organization_id;
		}
		 if(!(extraURL.equals("none"))&&!(extraURL.equals("")))
		{
			additionalURL+="&"+extraURL;
		}
		 if(additionalURL.equals("&"))
			additionalURL=additionalURL.substring(1);
		
		return additionalURL;
	}

	/**
	 * Compose columns with specified column type and for specified user if creating
	 * for logged in user set user_id=none
	 * 
	 * @author Rakesh.Chalamala
	 * @param user_id
	 * @param column_type
	 * @param columnsInfo
	 * @return
	 */
	public HashMap<String, Object> composeColumnsPayload(String user_id, String column_type,
			ArrayList<HashMap<String, Object>> columnsInfo) {

		HashMap<String, Object> payloadInfo = new HashMap<>();

		if (!user_id.equalsIgnoreCase("none")) {
			payloadInfo.put("user_id", user_id);
		}

		payloadInfo.put("column_type", column_type);
		payloadInfo.put("columns", columnsInfo);

		return payloadInfo;
	}

	/**
	 * Used for composing the Normalized column api filter use value = none when you
	 * don't want to pass
	 * 
	 * @author Rakesh.Chalamala
	 * @param column_type
	 * @param user_id
	 * @param role_id
	 * @param organization_id
	 * @return
	 */
	public String composeColumnFilter(String column_type, String user_id, String role_id, String organization_id) {

		String filter = "";

		if (!column_type.equalsIgnoreCase("none")) {
			filter += "column_type=" + column_type;
		}

		if (!user_id.equalsIgnoreCase("none")) {
			filter += "&" + "user_id=" + user_id;
		}

		if (!role_id.equalsIgnoreCase("none")) {
			filter += "&" + "role_id=" + role_id;
		}

		if (!organization_id.equalsIgnoreCase("none")) {
			filter += "&" + "organization_id=" + organization_id;
		}

		if (filter.startsWith("&"))
			filter = filter.substring(1);

		return filter;
	}

	/**
	 * @author Nagamalleswari.Sykam
	 * @param columnId
	 * @param sort
	 * @param filter
	 * @param visible
	 * @param orderId
	 * @return
	 */
	public HashMap<String, Object> composeColumn(String columnId, String sort, String filter, String visible,
			String orderId) {

		HashMap<String, Object> temp = new HashMap<>();

		temp.put("column_id", columnId);
		if (!sort.equals("none")) {
			temp.put("sort", sort);
		}

		if (!filter.equals("none")) {
			temp.put("filter", filter);
		}
		if (!visible.equals("none")) {
			temp.put("visible", visible);
		}
		temp.put("order_id", orderId);

		return temp;
	}
	
	/**
	 * @author Nagamalleswari.Sykam
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param role_Id
	 * @param ogranization_Id
	 * @param preference_language
	 * @return
	 */

		public HashMap<String, Object>composeUserInfo(String firstName,String lastName,String email,String role_Id,String ogranization_Id,String preference_language){

			HashMap<String, Object> temp = new HashMap<>();
			if(!firstName.equals("none")) {
				temp.put("firstName",firstName);
			}
			if(!lastName.equals("none")) {
				temp.put("lastName", lastName);
			}

			if(!email.contains("none")) {
				temp.put("email", email);	
			}
			if(!role_Id.contains("none")) {
				temp.put(role_Id,"role_Id");

			}
			if(!ogranization_Id.contains("none")) {
				temp.put("Ogranization_Id", ogranization_Id);

			}
			if(!preference_language.contains("none")) {
				temp.put("preference_language", preference_language);
			}
			System.out.println(temp);
			return temp;
		
		}
		/**
		 * @author Nagamalleswari.Sykam
		 * @param ogranization_id
		 * @param user_id
		 * @param org_type
		 * @return
		 */
		public HashMap<String, Object> composeGetOrganizationInfo(
				String ogranization_id,
				String user_id,
				String org_type
				){
			HashMap<String, Object> getOrgInfo = new HashMap<>();
			getOrgInfo.put("organization_id", ogranization_id);
			getOrgInfo.put("user_id", user_id);
			getOrgInfo.put("organization_type", org_type);	
			return getOrgInfo;
		}
		
		/**
		 * @author Nagamalleswari.Sykam
		 * @param oldPassword
		 * @param newPassword
		 * @return
		 */
		public HashMap<String, Object> updatepasswordInfo(String oldPassword,String newPassword){
			HashMap<String, Object> passwordInfo= new HashMap<>();
			
			if((oldPassword!=null)&&(oldPassword!="")) {
				passwordInfo.put("old_password", oldPassword);
			}
			passwordInfo.put("new_password", newPassword);
			return passwordInfo;
		}

		
		/**
		 * @author Nagamalleswari.Sykam
		 * @param noofcolumstobecreated
		 * @param columnIdList
		 * @param orderId
		 * @return
		 */
		public ArrayList<HashMap<String, Object>>composeColumnsInfo(int noofcolumstobecreated,ArrayList<String>columnIdList,String[] orderId,String [] sort,String [] filter,String[] visible ) {


			ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();

			HashMap<String, Object> temp = new HashMap<>();

			for (int i = 0; i < noofcolumstobecreated; i++) {

				if(noofcolumstobecreated>=i) {
					int index1 = gen_random_index(sort);
					int index2 = gen_random_index(filter);
					int index3 = gen_random_index(visible);

					temp = composeColumn(columnIdList.get(i), sort[index1], filter[index2], visible[index3], orderId[i]);
					expected_columns.add(temp);

				}else {
					System.out.println("the colums coount info  doesn't matched ");
				}

			}

			return expected_columns;

		}
		/**
		 * @author Nagamalleswari.Sykam
		 * @param job_status
		 * @return
		 */
		public int gen_random_index(String[] job_status) {
			Random generator = new Random();
			int randomindx = generator.nextInt(job_status.length);

			return randomindx;
		}
}
