package Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * for post /jobs/:id/data api, it would receive JOBTYPE_*** and return real message in response body;
 * @author Zhaoguo.Ma
 *
 */
public class JobTypeConstants {
//	public static final String JOBTYPE_CATALOG_FS="File System Catalog";
//	public static final String JOBTYPE_VM_CATALOG_FS="File System Catalog(VM)";
//	public static final String JOBTYPE_CATALOG_FS_ONDEMAND="On-demand File System Catalog";
//	public static final String JOBTYPE_VM_CATALOG_FS_ONDEMAND="On-demand File System Catalog";
//	public static final String JOBTYPE_CATALOG_GRT="Exchange GRT Catalog";
//	public static final String JOBTYPE_BACKUP_Full="Backup - Full";
//	public static final String JOBTYPE_BACKUP_Incremental="Backup - Incremental";
//	public static final String JOBTYPE_BACKUP_Resync="Backup - Verify";
//	public static final String JOBTYPE_BACKUP_Unknown="Backup";
//	public static final String JOBTYPE_VM_RECOVERY="Recover VM";
//	public static final String JOBTYPE_RESTORE="Restore";
//	public static final String JOBTYPE_COPY="Copy Recovery Point";
//	public static final String JOBTYPE_FILECOPY_BACKUP="File Copy";
//	public static final String JOBTYPE_FILECOPY_SOURCEDELETE="File Archive";
//	public static final String JOBTYPE_FILECOPY_DELETE="File Copy Delete";
//	public static final String JOBTYPE_FILECOPY_RESTORE="File Copy Restore";
//	public static final String JOBTYPE_FILECOPY_PURGE="File Copy Purge";
//	public static final String JOBTYPE_FILECOPY_CATALOGSYNC="File Copy Catalog Sync";
//	public static final String JOBTYPE_MERGE="Merge";
//	public static final String JOBTYPE_RPS_MERGE="Merge on RPS";
//	public static final String JOBTYPE_RPS_REPLICATE="Replication(Out)";
//	public static final String JOBTYPE_RPS_REPLICATE_IN_BOUND="Replication(In)";
//	public static final String JOBTYPE_RPS_DATA_SEEDING="RPS Jumpstart(Out)";
//	public static final String JOBTYPE_RPS_DATA_SEEDING_IN="RPS Jumpstart(In)";
//	public static final String JOBTYPE_BMR="BMR";
//	public static final String JOBTYPE_CONVERSION="Virtual Standby";
//	public static final String JOBTYPE_START_INSTANT_VM="Start ^AU_ProductName_IVM_SHORT^";
//	public static final String JOBTYPE_STOP_INSTANT_VM="Stop ^AU_ProductName_IVM_SHORT^";	
//	public static final String JOBTYPE_START_INSTANT_VHD="Start ^AU_ProductName_IVHD_SHORT^";
//	public static final String JOBTYPE_STOP_INSTANT_VHD="Stop ^AU_ProductName_IVHD_SHORT^";
//	public static final String JOBTYPE_ARCHIVE_TO_TAPE="Copy To Tape";
//	public static final String JOBTYPE_RPS_PURGE_DATASTORE="Purge";
//	public static final String JOBTYPE_LINUX_INSTANT_VM="^AU_ProductName_IVM_SHORT^";
//	public static final String JOBTYPE_ASSURED_RECOVERY="Assured Recovery";
//	public static final String MOUNT_RECOVERY_POINT="Mount Recovery Point";
//	public static final String ASSURE_RECOVERY="Assured Recovery";
//	public static final String JOBTYPE_CATALOG_APP="App Catalog";
//	
	
//	Map<String, String> job_type = {"JOBTYPE_CATALOG_APP":"App Catalog"};
	public static HashMap<String, String> jobtype = new HashMap<String, String>() {

		{
			put("JOBTYPE_CATALOG_FS", "File System Catalog");
			put("JOBTYPE_VM_CATALOG_FS", "File System Catalog(VM)");
			put("JOBTYPE_CATALOG_FS_ONDEMAND", "On-demand File System Catalog");
			put("JOBTYPE_VM_CATALOG_FS_ONDEMAND", "On-demand File System Catalog");
			put("JOBTYPE_CATALOG_GRT", "Exchange GRT Catalog");
			put("JOBTYPE_BACKUP_Full", "Backup - Full");
			put("JOBTYPE_BACKUP_Incremental", "Backup - Incremental");
			put("JOBTYPE_BACKUP_Resync", "Backup - Verify");
			put("JOBTYPE_BACKUP_Unknown", "Backup");
			put("JOBTYPE_VM_RECOVERY", "Recover VM");
			put("JOBTYPE_RESTORE", "Restore");
			put("JOBTYPE_COPY", "Copy Recovery Point");
			put("JOBTYPE_FILECOPY_BACKUP", "File Copy");
			put("JOBTYPE_FILECOPY_SOURCEDELETE", "File Archive");
			put("JOBTYPE_FILECOPY_DELETE", "File Copy Delete");
			put("JOBTYPE_FILECOPY_RESTORE", "File Copy Restore");
			put("JOBTYPE_FILECOPY_PURGE", "File Copy Purge");
			put("JOBTYPE_FILECOPY_CATALOGSYNC", "File Copy Catalog Sync");
			put("JOBTYPE_MERGE", "Merge");
			put("JOBTYPE_VM_MERGE", "Merge");
			put("JOBTYPE_RPS_MERGE", "Merge on RPS");
			put("JOBTYPE_RPS_REPLICATE", "Replication(Out)");
			put("JOBTYPE_RPS_REPLICATE_IN_BOUND", "Replication(In)");
			put("JOBTYPE_RPS_DATA_SEEDING", "RPS Jumpstart(Out)");
			put("JOBTYPE_RPS_DATA_SEEDING_IN", "RPS Jumpstart(In)");
			put("JOBTYPE_BMR", "BMR");
			put("JOBTYPE_CONVERSION", "Virtual Standby");
			put("JOBTYPE_RPS_CONVERSION", "Virtual Standby");
			put("JOBTYPE_START_INSTANT_VM", "Start ^AU_ProductName_IVM_SHORT^");
			put("JOBTYPE_STOP_INSTANT_VM", "Stop ^AU_ProductName_IVM_SHORT^");
			put("JOBTYPE_START_INSTANT_VHD", "Start ^AU_ProductName_IVHD_SHORT^");
			put("JOBTYPE_STOP_INSTANT_VHD", "Stop ^AU_ProductName_IVHD_SHORT^");
			put("JOBTYPE_ARCHIVE_TO_TAPE", "Copy To Tape");
			put("JOBTYPE_RPS_PURGE_DATASTORE", "Purge");
			put("JOBTYPE_LINUX_INSTANT_VM", "^AU_ProductName_IVM_SHORT^");
			put("JOBTYPE_ASSURED_RECOVERY", "Assured Recovery");
			put("MOUNT_RECOVERY_POINT", "Mount Recovery Point");
			put("ASSURE_RECOVERY", "Assured Recovery");
			put("JOBTYPE_CATALOG_APP", "App Catalog");
			//Yuntao confirms some job types are not used in backend, so the message is empty;
			put("JOBTYPE_VM_RESTORE_FILE_TO_ORIGINAL", "");
			put("JOBTYPE_VMWARE_VAPP_BACKUP", "");
			put("JOBTYPE_VMWARE_VAPP_RECOVERY", "");
			put("JOBTYPE_CATALOG_APP", "");
			put("JOBTYPE_VM_RECOVERY_HYPERV", "");
			put("JOBTYPE_AUTO_PROTECTION_DISCOVERY_VM", "");
			put("JOBTYPE_UDP_CONSOLE_ACTIVITY_LOG", "");
			put("JOBTYPE_UDP_CONSOLE_AGENTSERVICEDOWN", "");
			put("JOBTYPE_DATA_STORE", "");
	    }
	};
	
	public static String getJobTypeMessage(String jobType, String jobMethod) {
		if (jobType.equals("JOBTYPE_BACKUP") || jobType.equals("JOBTYPE_VM_BACKUP") || jobType.equals("JOBTYPE_OFFICE365_BACKUP") ||
				jobType.equals("JOBTYPE_CIFS_BACKUP") || jobType.equals("JOBTYPE_SHAREPOINT_BACKUP")) {
			if (jobMethod.equals("Full")) {
				return jobtype.get("JOBTYPE_BACKUP_Full");
			}
			else if (jobMethod.equals("Incremental")) {
				return jobtype.get("JOBTYPE_BACKUP_Incremental");
			}
			else if (jobMethod.equals("Resync")) {
				return jobtype.get("JOBTYPE_BACKUP_Resync");
			}
			else {
				return jobtype.get("JOBTYPE_BACKUP_Unknown");
			}
		}
		else {
			return jobtype.get(jobType);
		}
	}
}
