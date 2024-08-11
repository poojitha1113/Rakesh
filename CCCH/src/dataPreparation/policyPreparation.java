package dataPreparation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class policyPreparation {
  /**
   * used to compose PolicyCreateRequestDTO info
   * 
   * @author shan.jing
   * @param policy_name, String[]
   * @param policy_description, String
   * @param policy_type , String
   * @param sla, HashMap<String, Object>
   * @param is_traft, String
   * @param throttle Array[PolicyThrottleDTO]
   * @param destinations Array[PolicyTaskDTO]
   * @param sources, Array[SourceInfo]
   * @param schedules Array[PolicyScheduleDTO]
   * @param policy_id, String
   * @param organization_id String
   * @return
   */
  public Map<String, Object> composePolicyCreateRequestDTO(String policy_name,
      String policy_description, String policy_type, HashMap<String, Object> sla, String is_draft,
      ArrayList<HashMap<String, Object>> throttle, ArrayList<HashMap<String, Object>>  sources,
      ArrayList<HashMap<String, Object>> destinations, ArrayList<HashMap<String, Object>> schedules,
      String policy_id, String organization_id) {

    Map<String, Object> policyCreateRequestInfo = new HashMap<String, Object>();
    String[] emptyArray = new String[0];
    if (policy_name == null || policy_name == "") {
      policyCreateRequestInfo.put("policy_name", policy_name);
    } else if (!policy_name.equalsIgnoreCase("none")) {
      policyCreateRequestInfo.put("policy_name", policy_name);
    }
    if (policy_description == null || policy_description == "") {
      policyCreateRequestInfo.put("policy_description", policy_description);
    } else if (!policy_description.equalsIgnoreCase("none")) {
      policyCreateRequestInfo.put("policy_description", policy_description);
    }
    if (policy_type == null || policy_type == "") {
      policyCreateRequestInfo.put("policy_type", policy_type);
    } else if (!policy_type.equalsIgnoreCase("none")) {
      policyCreateRequestInfo.put("policy_type", policy_type);
    }
    policyCreateRequestInfo.put("sla", sla);
    if (is_draft == null || is_draft == "") {
      policyCreateRequestInfo.put("is_draft", is_draft);
    } else if (!is_draft.equalsIgnoreCase("none")) {
      policyCreateRequestInfo.put("is_draft", is_draft);
    }
    policyCreateRequestInfo.put("throttles", throttle);
//    if (sources == null || sources == "") {
//      policyCreateRequestInfo.put("sources", sources);
//    } else if (sources.equalsIgnoreCase("emptyarray")) {
//      policyCreateRequestInfo.put("sources", emptyArray);
//    } else if (!sources.equalsIgnoreCase("none")) {
//
//    	/**
//    	 * Changed by Rakesh.Chalamala
//    	 * Previously the source information is String[] in requestpayload
//    	 * Now the source information should ArrayList<HashMap>
//    	 * 
//    	 * Sprint 29
//    	 * Date: 21/02/2019
//    	 */
//    	 ArrayList<HashMap<String, Object>> allSourcesInfo = new ArrayList<>();
//         HashMap<String, Object> sourceInfo = null;
//         String[] src = sources.replace(" ", "").split(",");
//         
//         for (int i = 0; i < src.length; i++) {
//   		sourceInfo = new HashMap<>();
//   		sourceInfo.put("source_id", src[i]);
//   		allSourcesInfo.add(sourceInfo);
//         }
//         
//         policyCreateRequestInfo.put("sources", allSourcesInfo);
//    }
    policyCreateRequestInfo.put("schedules", schedules);
    policyCreateRequestInfo.put("sources", sources);
    policyCreateRequestInfo.put("destinations", destinations);
    if (policy_id == null || policy_id == "") {
      policyCreateRequestInfo.put("policy_id", policy_id);
    } else if (!policy_id.equalsIgnoreCase("none")) {
      policyCreateRequestInfo.put("policy_id", policy_id);
    }
    if (organization_id == null || organization_id == "") {
      policyCreateRequestInfo.put("organization_id", organization_id);
    } else if (!organization_id.equalsIgnoreCase("none")) {
      policyCreateRequestInfo.put("organization_id", organization_id);
    }
    return policyCreateRequestInfo;
  }


  /**
   * used to compose SLADTO info
   * 
   * @author shan.jing
   * @param recovery_time_objectives, HashMap<String, Object>
   * @param recovery_point_objective, HashMap<String, Object>
   * @return
   */
  public HashMap<String, Object> composeSLADTO(HashMap<String, Object> recovery_time_objectives,
      HashMap<String, Object> recovery_point_objective) {

    HashMap<String, Object> sLADTOInfo = new HashMap<String, Object>();
    if (recovery_time_objectives == null) {
      sLADTOInfo.put("recovery_time_objectives", recovery_time_objectives);
    } else {
      sLADTOInfo.put("recovery_time_objectives", recovery_time_objectives);
    }
    if (recovery_point_objective == null) {
      sLADTOInfo.put("recovery_point_objective", recovery_point_objective);
    } else {
      sLADTOInfo.put("recovery_point_objective", recovery_point_objective);
    }
    return sLADTOInfo;
  }
  
  /**
   * used to compose PolicyThrottleDTO info
   * 
   * @author shan.jing
   * @param task_id, String
   * @param schedule_type , String
   * @param throttle_type , String
   * @param rate , String
   * @param days_of_week , String
   * @param start_time , String
   * @param end_time , String
   * @return
   */
  public HashMap<String, Object> composePolicyThrottleDTO(String throttle_id, String task_id,
      String throttle_type, String rate, String days_of_week, String start_time, String end_time) {

    HashMap<String, Object> policyThrottleDTOInfo = new HashMap<String, Object>();
    if (throttle_id == null || throttle_id == "") {
      policyThrottleDTOInfo.put("throttle_id", throttle_id);
    } else if (!throttle_id.equalsIgnoreCase("none")) {
      policyThrottleDTOInfo.put("throttle_id", throttle_id);
    }
    if (task_id == null || task_id == "") {
      policyThrottleDTOInfo.put("task_id", task_id);
    } else if (!task_id.equalsIgnoreCase("none")) {
      policyThrottleDTOInfo.put("task_id", task_id);
    }
    if (throttle_type == null || throttle_type == "") {
      policyThrottleDTOInfo.put("throttle_type", throttle_type);
    } else if (!throttle_type.equalsIgnoreCase("none")) {
      policyThrottleDTOInfo.put("throttle_type", throttle_type);
    }
    if (rate == null || rate == "") {
      policyThrottleDTOInfo.put("rate", rate);
    } else if (!rate.equalsIgnoreCase("none")) {
      policyThrottleDTOInfo.put("rate", rate);
    }
    if (days_of_week == null || days_of_week == "") {
      policyThrottleDTOInfo.put("days_of_week", days_of_week);
    } else if (!days_of_week.equalsIgnoreCase("none")) {
      policyThrottleDTOInfo.put("days_of_week", days_of_week);
    }
    if (start_time == null || start_time == "") {
      policyThrottleDTOInfo.put("start_time", start_time);
    } else if (!start_time.equalsIgnoreCase("none")) {
      policyThrottleDTOInfo.put("start_time", start_time);
    }
    if (end_time == null || end_time == "") {
      policyThrottleDTOInfo.put("end_time", end_time);
    } else if (!end_time.equalsIgnoreCase("none")) {
      policyThrottleDTOInfo.put("end_time", end_time);
    }
    return policyThrottleDTOInfo;
  }
  
  /**
   * used to compose PolicyThrottleDTO info
   * 
   * @author shan.jing
   * @param task_id, String
   * @param schedule_type , String
   * @param throttle_type , String
   * @param rate , String
   * @param days_of_week , String
   * @param start_time , String
   * @param end_time , String
   * @return
   */
  public HashMap<String, Object> composePolicyThrottleDTO(String throttle_id, String task_id,
      String throttle_type, String rate, String days_of_week, String start_time, String end_time,
      String task_type, String destination_id, String destination_name) {

    HashMap<String, Object> policyThrottleDTOInfo = composePolicyThrottleDTO(throttle_id, task_id, throttle_type, rate,
            days_of_week, start_time, end_time);
    if (task_type == null || throttle_id == "") {
      policyThrottleDTOInfo.put("task_type", task_type);
    } else if (!throttle_id.equalsIgnoreCase("none")) {
      policyThrottleDTOInfo.put("task_type", task_type);
    }
    if (destination_id == null || destination_id == "") {
      policyThrottleDTOInfo.put("destination_id", destination_id);
    } else if (!destination_id.equalsIgnoreCase("none")) {
      policyThrottleDTOInfo.put("destination_id", destination_id);
    }
    if (destination_name == null || destination_name == "") {
      policyThrottleDTOInfo.put("destination_name", destination_name);
    } else if (!throttle_type.equalsIgnoreCase("none")) {
      policyThrottleDTOInfo.put("destination_name", destination_name);
    }
    return policyThrottleDTOInfo;
  }
  
  /**
   * used to compose PolicyTaskDTO info
   * 
   * @author shan.jing
   * @param task_id, String
   * @param task_type, String
   * @param destination_id, String
   * @param parent_id, String
   * @param cloud_direct_image_backup, HashMap<String, Object>
   * @param cloud_direct_file_backup, HashMap<String, Object>
   * @param udp_replication_from_remote, HashMap<String, Object>
   * @return
   */
  public HashMap<String, Object> composePolicyTaskDTO(String task_id, String task_type,
      String destination_id, String parent_id, HashMap<String, Object> cloud_direct_image_backup,
      HashMap<String, Object> cloud_direct_file_backup,
      HashMap<String, Object> udp_replication_from_remote) {

    HashMap<String, Object> cloudPolicyTaskDTOInfo = new HashMap<String, Object>();
    if (task_id == null || task_id == "") {
      cloudPolicyTaskDTOInfo.put("task_id", task_id);
    } else if (!task_id.equalsIgnoreCase("none")) {
      cloudPolicyTaskDTOInfo.put("task_id", task_id);
    }
    if (task_type == null || task_type == "") {
      cloudPolicyTaskDTOInfo.put("task_type", task_type);
    } else if (!task_type.equalsIgnoreCase("none")) {
      cloudPolicyTaskDTOInfo.put("task_type", task_type);
    }
    if (destination_id == null || destination_id == "") {
      cloudPolicyTaskDTOInfo.put("destination_id", destination_id);
    } else if (!destination_id.equalsIgnoreCase("none")) {
      cloudPolicyTaskDTOInfo.put("destination_id", destination_id);
    }
    if (parent_id == null || parent_id == "") {
      cloudPolicyTaskDTOInfo.put("parent_id", parent_id);
    } else if (!parent_id.equalsIgnoreCase("none")) {
      cloudPolicyTaskDTOInfo.put("parent_id", parent_id);
    }
    if (cloud_direct_image_backup == null) {
      cloudPolicyTaskDTOInfo.put("cloud_direct_image_backup", cloud_direct_image_backup);
    } else {
      cloudPolicyTaskDTOInfo.put("cloud_direct_image_backup", cloud_direct_image_backup);
    }
    if (cloud_direct_file_backup == null) {
      cloudPolicyTaskDTOInfo.put("cloud_direct_file_backup", cloud_direct_file_backup);
    } else {
      cloudPolicyTaskDTOInfo.put("cloud_direct_file_backup", cloud_direct_file_backup);
    }
    if (udp_replication_from_remote == null) {
      cloudPolicyTaskDTOInfo.put("udp_replication_from_remote", udp_replication_from_remote);
    } else {
      cloudPolicyTaskDTOInfo.put("udp_replication_from_remote", udp_replication_from_remote);
    }
    
    return cloudPolicyTaskDTOInfo;
  }

  /**
   * used to compose PolicyTaskDTO info
   * 
   * @author shan.jing
   * @param task_id, String
   * @param task_type, String
   * @param destination_id, String
   * @param parent_id, String
   * @param cloud_direct_image_backup, HashMap<String, Object>
   * @param cloud_direct_file_backup, HashMap<String, Object>
   * @param udp_replication_from_remote, HashMap<String, Object>
   * @param udp_replication_to_remote, HashMap<String, Object>
   * @return
   */
  public HashMap<String, Object> composePolicyTaskDTO(String task_id, String task_type,
      String destination_id, String parent_id, HashMap<String, Object> cloud_direct_image_backup,
      HashMap<String, Object> cloud_direct_file_backup,
      HashMap<String, Object> udp_replication_from_remote,
      HashMap<String, Object> udp_replication_to_remote) {

    HashMap<String, Object> cloudPolicyTaskDTOInfo = new HashMap<String, Object>();
    if (task_id == null || task_id == "") {
      cloudPolicyTaskDTOInfo.put("task_id", task_id);
    } else if (!task_id.equalsIgnoreCase("none")) {
      cloudPolicyTaskDTOInfo.put("task_id", task_id);
    }
    if (task_type == null || task_type == "") {
      cloudPolicyTaskDTOInfo.put("task_type", task_type);
    } else if (!task_type.equalsIgnoreCase("none")) {
      cloudPolicyTaskDTOInfo.put("task_type", task_type);
    }
    if (destination_id == null || destination_id == "") {
      cloudPolicyTaskDTOInfo.put("destination_id", destination_id);
    } else if (!destination_id.equalsIgnoreCase("none")) {
      cloudPolicyTaskDTOInfo.put("destination_id", destination_id);
    }
    if (parent_id == null || parent_id == "") {
      cloudPolicyTaskDTOInfo.put("parent_id", parent_id);
    } else if (!parent_id.equalsIgnoreCase("none")) {
      cloudPolicyTaskDTOInfo.put("parent_id", parent_id);
    }
    if (cloud_direct_image_backup == null) {
      cloudPolicyTaskDTOInfo.put("cloud_direct_image_backup", cloud_direct_image_backup);
    } else {
      cloudPolicyTaskDTOInfo.put("cloud_direct_image_backup", cloud_direct_image_backup);
    }
    if (cloud_direct_file_backup == null) {
      cloudPolicyTaskDTOInfo.put("cloud_direct_file_backup", cloud_direct_file_backup);
    } else {
      cloudPolicyTaskDTOInfo.put("cloud_direct_file_backup", cloud_direct_file_backup);
    }
    if (udp_replication_from_remote == null) {
      cloudPolicyTaskDTOInfo.put("udp_replication_from_remote", udp_replication_from_remote);
    } else {
      cloudPolicyTaskDTOInfo.put("udp_replication_from_remote", udp_replication_from_remote);
    }
    if (udp_replication_to_remote == null) {
        cloudPolicyTaskDTOInfo.put("udp_replication_to_remote", udp_replication_to_remote);
      } else {
        cloudPolicyTaskDTOInfo.put("udp_replication_to_remote", udp_replication_to_remote);
      }
    return cloudPolicyTaskDTOInfo;
  }
  
  /**
   * used to compose PolicyTaskDTO info
   * 
   * @author Rakesh.Chalamala
   * @param task_id, String
   * @param task_type, String
   * @param destination_id, String
   * @param parent_id, String
   * @param cloud_direct_image_backup, HashMap<String, Object>
   * @param cloud_direct_file_backup, HashMap<String, Object>
   * @param udp_replication_from_remote, HashMap<String, Object>
   * @param udp_replication_to_remote, HashMap<String, Object>
   * @param cloud_direct_sql_server_backup, HashMap<String, Object>
   * @return
   */
  public HashMap<String, Object> composePolicyTaskDTO(String task_id, String task_type,
      String destination_id, String parent_id, HashMap<String, Object> cloud_direct_image_backup,
      HashMap<String, Object> cloud_direct_file_backup,
      HashMap<String, Object> udp_replication_from_remote,
      HashMap<String, Object> udp_replication_to_remote,
      HashMap<String, Object> cloud_direct_sql_server_backup) {

    HashMap<String, Object> cloudPolicyTaskDTOInfo = new HashMap<String, Object>();
    if (task_id == null || task_id == "") {
      cloudPolicyTaskDTOInfo.put("task_id", task_id);
    } else if (!task_id.equalsIgnoreCase("none")) {
      cloudPolicyTaskDTOInfo.put("task_id", task_id);
    }
    if (task_type == null || task_type == "") {
      cloudPolicyTaskDTOInfo.put("task_type", task_type);
    } else if (!task_type.equalsIgnoreCase("none")) {
      cloudPolicyTaskDTOInfo.put("task_type", task_type);
    }
    if (destination_id == null || destination_id == "") {
      cloudPolicyTaskDTOInfo.put("destination_id", destination_id);
    } else if (!destination_id.equalsIgnoreCase("none")) {
      cloudPolicyTaskDTOInfo.put("destination_id", destination_id);
    }
    if (parent_id == null || parent_id == "") {
      cloudPolicyTaskDTOInfo.put("parent_id", parent_id);
    } else if (!parent_id.equalsIgnoreCase("none")) {
      cloudPolicyTaskDTOInfo.put("parent_id", parent_id);
    }
    if (cloud_direct_image_backup == null) {
      cloudPolicyTaskDTOInfo.put("cloud_direct_image_backup", cloud_direct_image_backup);
    } else {
      cloudPolicyTaskDTOInfo.put("cloud_direct_image_backup", cloud_direct_image_backup);
    }
    if (cloud_direct_file_backup == null) {
      cloudPolicyTaskDTOInfo.put("cloud_direct_file_backup", cloud_direct_file_backup);
    } else {
      cloudPolicyTaskDTOInfo.put("cloud_direct_file_backup", cloud_direct_file_backup);
    }
    if (udp_replication_from_remote == null) {
      cloudPolicyTaskDTOInfo.put("udp_replication_from_remote", udp_replication_from_remote);
    } else {
      cloudPolicyTaskDTOInfo.put("udp_replication_from_remote", udp_replication_from_remote);
    }
    if (udp_replication_to_remote == null) {
        cloudPolicyTaskDTOInfo.put("udp_replication_to_remote", udp_replication_to_remote);
      } else {
        cloudPolicyTaskDTOInfo.put("udp_replication_to_remote", udp_replication_to_remote);
      }
    
    if (cloud_direct_sql_server_backup == null) {
        cloudPolicyTaskDTOInfo.put("cloud_direct_sql_server_backup", cloud_direct_sql_server_backup);
      } else {
        cloudPolicyTaskDTOInfo.put("cloud_direct_sql_server_backup", cloud_direct_sql_server_backup);
      }
    return cloudPolicyTaskDTOInfo;
  }

  /**
   * used to compose PolicyScheduleDTO info
   * 
   * @author shan.jing
   * @param schedule_id, String
   * @param schedule_type , String
   * @param task_id , String
   * @param destination_id , String
   * @param schedule , HashMap<String, Object>
   * @param start_time , String
   * @param end_time , String
   * @return
   */
  public HashMap<String, Object> composePolicyScheduleDTO(String schedule_id, String schedule_type,
      String task_id, String destination_id, HashMap<String, Object> schedule, String start_time,
      String end_time) {

    HashMap<String, Object> policyScheduleDTOInfo = new HashMap<String, Object>();
    if (schedule_id == null || schedule_id == "") {
      policyScheduleDTOInfo.put("schedule_id", schedule_id);
    } else if (!schedule_id.equalsIgnoreCase("none")) {
      policyScheduleDTOInfo.put("schedule_id", schedule_id);
    }
    if (schedule_type == null || schedule_type == "") {
      policyScheduleDTOInfo.put("schedule_type", schedule_type);
    } else if (!schedule_type.equalsIgnoreCase("none")) {
      policyScheduleDTOInfo.put("schedule_type", schedule_type);
    }
    if (task_id == null || task_id == "") {
      policyScheduleDTOInfo.put("task_id", task_id);
    } else if (!task_id.equalsIgnoreCase("none")) {
      policyScheduleDTOInfo.put("task_id", task_id);
    }
    if (destination_id == null || destination_id == "") {
      policyScheduleDTOInfo.put("destination_id", destination_id);
    } else if (!destination_id.equalsIgnoreCase("none")) {
      policyScheduleDTOInfo.put("destination_id", destination_id);
    }
    if (schedule == null) {
      policyScheduleDTOInfo.put("schedule", schedule);
    } else {
      policyScheduleDTOInfo.put("schedule", schedule);
    }
    if (start_time == null || start_time == "") {
      policyScheduleDTOInfo.put("start_time", start_time);
    } else if (!start_time.equalsIgnoreCase("none")) {
      policyScheduleDTOInfo.put("start_time", start_time);
    }
    if (end_time == null || end_time == "") {
      policyScheduleDTOInfo.put("end_time", end_time);
    } else if (!end_time.equalsIgnoreCase("none")) {
      policyScheduleDTOInfo.put("end_time", end_time);
    }
    return policyScheduleDTOInfo;
  }
  
  /**
   * used to compose PolicyScheduleDTO info
   * 
   * @author shan.jing
   * @param schedule_id, String
   * @param schedule_type , String
   * @param task_id , String
   * @param destination_id , String
   * @param schedule , HashMap<String, Object>
   * @param start_time , String
   * @param end_time , String
   * @param task_type , String
   * @param destination_name , String
   * @return
   */
  public HashMap<String, Object> composePolicyScheduleDTO(String schedule_id, String schedule_type,
      String task_id, String destination_id, HashMap<String, Object> schedule, String start_time,
      String end_time,String task_type, String destination_name) {

    HashMap<String, Object> policyScheduleDTOInfo = composePolicyScheduleDTO(schedule_id, schedule_type, task_id,
            destination_id, schedule, start_time, end_time);
    if (task_type == null || task_type == "") {
      policyScheduleDTOInfo.put("task_type", task_type);
    } else if (!task_type.equalsIgnoreCase("none")) {
      policyScheduleDTOInfo.put("task_type", task_type);
    }
    if (destination_name == null || destination_name == "") {
      policyScheduleDTOInfo.put("destination_name", destination_name);
    } else if (!destination_name.equalsIgnoreCase("none")) {
      policyScheduleDTOInfo.put("destination_name", destination_name);
    }
    return policyScheduleDTOInfo;
  }


  /**
   * used to compose RecvoeryTimeObjective info
   * 
   * @author shan.jing
   * @param assured_recovery_test, HashMap<String, Object>
   * @param recovery_point_objective, HashMap<String, Object>
   * @param assured_recovery_test, HashMap<String, Object>
   * @param recovery_point_objective, HashMap<String, Object>
   * @param recovery_point_objective, HashMap<String, Object>
   * @return
   */
  public HashMap<String, Object> composeRecvoeryTimeObjective(
      HashMap<String, Object> assured_recovery_test, HashMap<String, Object> vm_recovery,
      HashMap<String, Object> instant_vm_recovery, HashMap<String, Object> file_level_restore,
      HashMap<String, Object> bare_metal_recovery) {

    HashMap<String, Object> recvoeryTimeObjectiveInfo = new HashMap<String, Object>();
    if (assured_recovery_test == null) {
      recvoeryTimeObjectiveInfo.put("assured_recovery_test", assured_recovery_test);
    } else {
      recvoeryTimeObjectiveInfo.put("assured_recovery_test", assured_recovery_test);
    }
    if (vm_recovery == null) {
      recvoeryTimeObjectiveInfo.put("vm_recovery", vm_recovery);
    } else {
      recvoeryTimeObjectiveInfo.put("vm_recovery", vm_recovery);
    }
    if (instant_vm_recovery == null) {
      recvoeryTimeObjectiveInfo.put("instant_vm_recovery", instant_vm_recovery);
    } else {
      recvoeryTimeObjectiveInfo.put("instant_vm_recovery", instant_vm_recovery);
    }
    if (file_level_restore == null) {
      recvoeryTimeObjectiveInfo.put("file_level_restore ", file_level_restore);
    } else {
      recvoeryTimeObjectiveInfo.put("file_level_restore ", file_level_restore);
    }
    if (bare_metal_recovery == null) {
      recvoeryTimeObjectiveInfo.put("bare_metal_recovery", bare_metal_recovery);
    } else {
      recvoeryTimeObjectiveInfo.put("bare_metal_recovery", bare_metal_recovery);
    }
    return recvoeryTimeObjectiveInfo;
  }


  /**
   * used to compose RecoveryPointObjective info
   * 
   * @author shan.jing
   * @param value, HashMap<String, Object>
   * @return
   */
  public HashMap<String, Object> composeRecoveryPointObjective(HashMap<String, Object> value) {

    HashMap<String, Object> recoveryPointObjectiveInfo = new HashMap<String, Object>();
    if (value == null) {
      recoveryPointObjectiveInfo.put("value", value);
    } else {
      recoveryPointObjectiveInfo.put("value", value);
    }
    return recoveryPointObjectiveInfo;
  }


  /**
   * used to compose CloudDirectImageBackupTaskInfoDTO info
   * 
   * @author shan.jing
   * @param value, Array[String]
   * @param local_backup, HashMap<String, Object>
   * @return
   */
  public HashMap<String, Object> composeCloudDirectImageBackupTaskInfoDTO(String drives,
      HashMap<String, Object> local_backup) {

    HashMap<String, Object> cloudDirectImageBackupTaskInfoDTOInfo = new HashMap<String, Object>();
    String[] emptyArray = new String[0];
    if (drives == null || drives == "") {
      cloudDirectImageBackupTaskInfoDTOInfo.put("drives", drives);
    } else if (drives.equalsIgnoreCase("emptyarray")) {
      cloudDirectImageBackupTaskInfoDTOInfo.put("drives", emptyArray);
    } else if (!drives.equalsIgnoreCase("none")) {
      cloudDirectImageBackupTaskInfoDTOInfo.put("drives", drives.replace(" ", "").split(","));
    }
    if (local_backup == null) {
      cloudDirectImageBackupTaskInfoDTOInfo.put("local_backup", local_backup);
    } else {
      cloudDirectImageBackupTaskInfoDTOInfo.put("local_backup", local_backup);
    }
    return cloudDirectImageBackupTaskInfoDTOInfo;
  }


  /**
   * used to compose CloudDirectFileBackupTaskInfoDTO info
   * 
   * @author shan.jing
   * @param path, String
   * @param local_backup, HashMap<String, Object>
   * @return
   */
  public HashMap<String, Object> composeCloudDirectFileBackupTaskInfoDTO(String paths,
      HashMap<String, Object> local_backup) {
	  
    HashMap<String, Object> cloudDirectFileBackupTaskInfoDTOInfo = new HashMap<String, Object>();
    String[] emptyArray = new String[0];
    ArrayList<String> pathsArrayList=new ArrayList<String>();
    if (paths == null || paths == "") {
    	cloudDirectFileBackupTaskInfoDTOInfo.put("path", paths);
    } else if (paths.equalsIgnoreCase("emptyarray")) {
    	cloudDirectFileBackupTaskInfoDTOInfo.put("path", emptyArray);
    } else if (!paths.equalsIgnoreCase("none")) {
    	String[] pathsArr = paths.split(",");
    	for(int i=0;i<pathsArr.length;i++){
    		pathsArrayList.add(pathsArr[i]);
    	}
    	cloudDirectFileBackupTaskInfoDTOInfo.put("path", pathsArrayList);
    }
    if (local_backup == null) {
      cloudDirectFileBackupTaskInfoDTOInfo.put("local_backup", local_backup);
    } else {
      cloudDirectFileBackupTaskInfoDTOInfo.put("local_backup", local_backup);
    }
    return cloudDirectFileBackupTaskInfoDTOInfo;
  }
  
  public HashMap<String, Object> composeSourceInfo(String source_id,
	      HashMap<String, Object> vmInfo) {

	    HashMap<String, Object> sourceInfo = new HashMap<String, Object>();
	    if (source_id == null || source_id == "") {
	    	sourceInfo.put("source_id", source_id);
	    } else if (!source_id.equalsIgnoreCase("none")) {
	    	sourceInfo.put("source_id", source_id);
	    }
	    if (vmInfo == null) {
	    	sourceInfo.put("vmInfo", vmInfo);
	    } else {
	    	sourceInfo.put("vmInfo", vmInfo);
	    }
	    return sourceInfo;
	  }
  
  
  /**
   * used to compose CloudDirectFileBackupTaskInfoDTO info
   * 
   * @author shan.jing
   * @param path, String
   * @param local_backup, HashMap<String, Object>
   * @param excludes, ArrayList<String, Object>
   * @return
   */
  public HashMap<String, Object> composeCloudDirectFileBackupTaskInfoDTO(String paths,
      HashMap<String, Object> local_backup,ArrayList<HashMap<String, Object>> excludes) {
	  
	HashMap<String, Object> cloudDirectFileBackupTaskInfoDTOInfo = composeCloudDirectFileBackupTaskInfoDTO(paths, local_backup);
    cloudDirectFileBackupTaskInfoDTOInfo.put("excludes", excludes);
    
    return cloudDirectFileBackupTaskInfoDTOInfo;
  }
  /**
   * used to compose PerformARTestOption info
   * 
   * @author shan.jing
   * @param path, String
   * @param path, String
   * @param path, String
   * @param path, String
   * @return
   */
  public HashMap<String, Object> composePerformARTestOption(String daily_backup,String weekly_backup,String monthly_backup,
		  String lastet_recovery_point) {
	  
	HashMap<String, Object> performARTestOption = new HashMap<String, Object>();
	if (daily_backup == null || daily_backup == "") {
		performARTestOption.put("daily_backup", daily_backup);
    } else if (!daily_backup.equalsIgnoreCase("none")) {
    	performARTestOption.put("daily_backup", daily_backup);
    }
	if (weekly_backup == null || weekly_backup == "") {
		performARTestOption.put("weekly_backup", weekly_backup);
    } else if (!weekly_backup.equalsIgnoreCase("none")) {
    	performARTestOption.put("weekly_backup", weekly_backup);
    }
	if (monthly_backup == null || monthly_backup == "") {
		performARTestOption.put("monthly_backup", monthly_backup);
    } else if (!monthly_backup.equalsIgnoreCase("none")) {
    	performARTestOption.put("monthly_backup", monthly_backup);
    }
	if (lastet_recovery_point == null || lastet_recovery_point == "") {
		performARTestOption.put("lastet_recovery_point", lastet_recovery_point);
    } else if (!lastet_recovery_point.equalsIgnoreCase("none")) {
    	performARTestOption.put("lastet_recovery_point", lastet_recovery_point);
    }
	return performARTestOption;
  }

  public HashMap<String, Object> composeVMInfo(String hypervisor_id,String vm_name,String vm_os_major,
		  String vm_guest_os) {
	  
	HashMap<String, Object> performVMInfo = new HashMap<String, Object>();
	if (hypervisor_id == null || hypervisor_id == "") {
		performVMInfo.put("hypervisor_id", hypervisor_id);
    } else if (!hypervisor_id.equalsIgnoreCase("none")) {
    	performVMInfo.put("hypervisor_id", hypervisor_id);
    }
	if (vm_name == null || vm_name == "") {
		performVMInfo.put("vm_name", vm_name);
    } else if (!vm_name.equalsIgnoreCase("none")) {
    	performVMInfo.put("vm_name", vm_name);
    }
	if (vm_os_major == null || vm_os_major == "") {
		performVMInfo.put("vm_os_major", vm_os_major);
    } else if (!vm_os_major.equalsIgnoreCase("none")) {
    	performVMInfo.put("vm_os_major", vm_os_major);
    }
	if (vm_guest_os == null || vm_guest_os == "") {
		performVMInfo.put("vm_guest_os", vm_guest_os);
    } else if (!vm_guest_os.equalsIgnoreCase("none")) {
    	performVMInfo.put("vm_guest_os", vm_guest_os);
    }
	return performVMInfo;
  }
  
  /**
   * used to compose RetentionPolicyOption info
   * 
   * @author shan.jing
   * @param path, String
   * @param path, String
   * @param path, String
   * @param path, String
   * @return
   */
  public HashMap<String, Object> composeRetentionPolicyOption(String daily_backup,String weekly_backup,String monthly_backup,
		  String manual_backup) {
	  
	HashMap<String, Object> retentionPolicyOption = new HashMap<String, Object>();
	if (daily_backup == null || daily_backup == "") {
		retentionPolicyOption.put("daily_backup", daily_backup);
    } else if (!daily_backup.equalsIgnoreCase("none")) {
    	retentionPolicyOption.put("daily_backup", daily_backup);
    }
	if (weekly_backup == null || weekly_backup == "") {
		retentionPolicyOption.put("weekly_backup", weekly_backup);
    } else if (!weekly_backup.equalsIgnoreCase("none")) {
    	retentionPolicyOption.put("weekly_backup", weekly_backup);
    }
	if (monthly_backup == null || monthly_backup == "") {
		retentionPolicyOption.put("monthly_backup", monthly_backup);
    } else if (!monthly_backup.equalsIgnoreCase("none")) {
    	retentionPolicyOption.put("monthly_backup", monthly_backup);
    }
	if (manual_backup == null || manual_backup == "") {
		retentionPolicyOption.put("manual_backup", manual_backup);
    } else if (!manual_backup.equalsIgnoreCase("none")) {
    	retentionPolicyOption.put("manual_backup", manual_backup);
    }
	return retentionPolicyOption;
  }
 

  /**
   * used to compose UdpReplicationToRemoteInfoDTO info
   * 
   * @author shan.jing
   * @param perform_ar_test, HashMap<String, Object>
   * @param retention_policy, HashMap<String, Object>
   * @return
   */
  public HashMap<String, Object> composeUdpReplicationToRemoteInfoDTO(
		  String remote_console_name, 
		  String user_name ,
		  String password ,
		  String port ,
		  String protocol ,
		  String remote_plan_global_uuid ,
		  String remote_plan_uuid ,
		  String remote_plan_name ,
		  String remote_plan_rpspolicy_uuid ,
		  String remote_plan_rpspolicy_name ,
		  String retry_start ,
		  String retry_times ) {

    HashMap<String, Object> udpReplicationToRemoteInfoDTOInfo = new HashMap<String, Object>();
    if (remote_console_name == null) {
    	udpReplicationToRemoteInfoDTOInfo.put("remote_console_name", remote_console_name);
    } else {
    	udpReplicationToRemoteInfoDTOInfo.put("remote_console_name", remote_console_name);
    }
    if (user_name == null) {
    	udpReplicationToRemoteInfoDTOInfo.put("user_name", user_name);
    } else {
    	udpReplicationToRemoteInfoDTOInfo.put("user_name", user_name);
    }
    if (password == null) {
    	udpReplicationToRemoteInfoDTOInfo.put("password", password);
    } else {
    	udpReplicationToRemoteInfoDTOInfo.put("password", password);
    }
    if (port == null) {
    	udpReplicationToRemoteInfoDTOInfo.put("port", port);
    } else {
    	udpReplicationToRemoteInfoDTOInfo.put("port", port);
    }
    if (protocol == null) {
    	udpReplicationToRemoteInfoDTOInfo.put("protocol", protocol);
    } else {
    	udpReplicationToRemoteInfoDTOInfo.put("protocol", protocol);
    }
    if (remote_plan_global_uuid == null) {
    	udpReplicationToRemoteInfoDTOInfo.put("remote_plan_global_uuid", remote_plan_global_uuid);
    } else {
    	udpReplicationToRemoteInfoDTOInfo.put("remote_plan_global_uuid", remote_plan_global_uuid);
    }
    if (remote_plan_uuid == null) {
    	udpReplicationToRemoteInfoDTOInfo.put("remote_plan_uuid", remote_plan_uuid);
    } else {
    	udpReplicationToRemoteInfoDTOInfo.put("remote_plan_uuid", remote_plan_uuid);
    }
    if (remote_plan_name == null) {
    	udpReplicationToRemoteInfoDTOInfo.put("remote_plan_name", remote_plan_name);
    } else {
    	udpReplicationToRemoteInfoDTOInfo.put("remote_plan_name", remote_plan_name);
    }
    if (remote_plan_rpspolicy_uuid == null) {
    	udpReplicationToRemoteInfoDTOInfo.put("remote_plan_rpspolicy_uuid", remote_plan_rpspolicy_uuid);
    } else {
    	udpReplicationToRemoteInfoDTOInfo.put("remote_plan_rpspolicy_uuid", remote_plan_rpspolicy_uuid);
    }
    if (remote_plan_rpspolicy_name == null) {
    	udpReplicationToRemoteInfoDTOInfo.put("remote_plan_rpspolicy_name", remote_plan_rpspolicy_name);
    } else {
    	udpReplicationToRemoteInfoDTOInfo.put("remote_plan_rpspolicy_name", remote_plan_rpspolicy_name);
    }
    if (retry_start == null) {
    	udpReplicationToRemoteInfoDTOInfo.put("retry_start", retry_start);
    } else {
    	udpReplicationToRemoteInfoDTOInfo.put("retry_start", retry_start);
    }
    if (retry_times == null) {
    	udpReplicationToRemoteInfoDTOInfo.put("retry_times", retry_times);
    } else {
    	udpReplicationToRemoteInfoDTOInfo.put("retry_times", retry_times);
    }
    return udpReplicationToRemoteInfoDTOInfo;
  }
  
  /**
   * used to compose UdpReplicationFromRemoteInfoDTO info
   * 
   * @author shan.jing
   * @param perform_ar_test, HashMap<String, Object>
   * @param retention_policy, HashMap<String, Object>
   * @return
   */
  public HashMap<String, Object> composeUdpReplicationFromRemoteInfoDTO(
      HashMap<String, Object> perform_ar_test, HashMap<String, Object> retention_policy) {

    HashMap<String, Object> udpReplicationFromRemoteInfoDTOInfo = new HashMap<String, Object>();
    if (perform_ar_test == null) {
      udpReplicationFromRemoteInfoDTOInfo.put("perform_ar_test", perform_ar_test);
    } else {
      udpReplicationFromRemoteInfoDTOInfo.put("perform_ar_test", perform_ar_test);
    }
    if (retention_policy == null) {
      udpReplicationFromRemoteInfoDTOInfo.put("retention_policy", retention_policy);
    } else {
      udpReplicationFromRemoteInfoDTOInfo.put("retention_policy", retention_policy);
    }
    return udpReplicationFromRemoteInfoDTOInfo;
  }


  /**
   * used to compose ScheduleSettingDTO info
   * 
   * @author shan.jing
   * @param cloud_direct_schedule, HashMap<String, Object>
   * @param custom_schedule , HashMap<String, Object>
   * @return
   */
  public HashMap<String, Object> composeScheduleSettingDTO(
      HashMap<String, Object> cloud_direct_schedule, HashMap<String, Object> custom_schedule) {

    HashMap<String, Object> scheduleSettingDTOInfo = new HashMap<String, Object>();
    if (cloud_direct_schedule == null) {
      scheduleSettingDTOInfo.put("cloud_direct_schedule", cloud_direct_schedule);
    } else {
      scheduleSettingDTOInfo.put("cloud_direct_schedule", cloud_direct_schedule);
    }
    if (custom_schedule == null) {
      scheduleSettingDTOInfo.put("custom_schedule", custom_schedule);
    } else {
      scheduleSettingDTOInfo.put("custom_schedule", custom_schedule);
    }
    return scheduleSettingDTOInfo;
  }


  /**
   * used to compose SLAParameterValueDTO info
   * 
   * @author shan.jing
   * @param value, String
   * @param unit, String
   * @return
   */
  public HashMap<String, Object> composeSLAParameterValueDTO(String value, String unit) {

    HashMap<String, Object> sLAParameterValueDTOInfo = new HashMap<String, Object>();
    if (value == null || value == "") {
      sLAParameterValueDTOInfo.put("value", value);
    } else if (!value.equalsIgnoreCase("none")) {
      sLAParameterValueDTOInfo.put("value", value);
    }
    if (unit == null || unit == "") {
      sLAParameterValueDTOInfo.put("unit", unit);
    } else if (!unit.equalsIgnoreCase("none")) {
      sLAParameterValueDTOInfo.put("unit", unit);
    }
    return sLAParameterValueDTOInfo;
  }


  /**
   * used to compose CloudDirectLocalBackupDTO info
   * 
   * @author shan.jing
   * @param path, String
   * @param enabled, String
   * @param excludes , HashMap<String, Object>
   * @return
   */
  public HashMap<String, Object> composeCloudDirectLocalBackupDTO(String path, String enabled,
      ArrayList<HashMap<String, Object>> excludes) {

    HashMap<String, Object> cloudDirectLocalBackupDTOInfo = new HashMap<String, Object>();
    if (path == null || path == "") {
      cloudDirectLocalBackupDTOInfo.put("path", path);
    } else if (!path.equalsIgnoreCase("none")) {
      cloudDirectLocalBackupDTOInfo.put("path", path);
    }
    if (enabled == null || enabled == "") {
      cloudDirectLocalBackupDTOInfo.put("enabled", enabled);
    } else if (!enabled.equalsIgnoreCase("none")) {
      cloudDirectLocalBackupDTOInfo.put("enabled", enabled);
    }
    if (excludes == null) {
      cloudDirectLocalBackupDTOInfo.put("excludes", excludes);
    } else {
      cloudDirectLocalBackupDTOInfo.put("excludes", excludes);
    }
    return cloudDirectLocalBackupDTOInfo;
  }
  
  /**
   * used to compose CloudDirectLocalBackupDTO info
   * 
   * @author shan.jing
   * @param path, String
   * @param enabled, String
   * @param excludes , HashMap<String, Object>
   * @return
   */
  public HashMap<String, Object> composeUdpConsoleServerDTO(String org_id,String console, String user,
      String pwd,int port,String protocol) {

    HashMap<String, Object> udpConsoleServerDTOInfo = new HashMap<String, Object>();
    if (org_id == null || org_id == "") {
    	udpConsoleServerDTOInfo.put("organization_id", org_id);
    } else if (!org_id.equalsIgnoreCase("none")) {
    	udpConsoleServerDTOInfo.put("organization_id", org_id);
    }
    if (console == null || console == "") {
    	udpConsoleServerDTOInfo.put("remote_console_name", console);
    } else if (!console.equalsIgnoreCase("none")) {
    	udpConsoleServerDTOInfo.put("remote_console_name", console);
    }
    if (user == null || user == "") {
    	udpConsoleServerDTOInfo.put("user_name", user);
    } else if (!user.equalsIgnoreCase("none")) {
    	udpConsoleServerDTOInfo.put("user_name", user);
    }
    if (pwd == null || pwd == "") {
    	udpConsoleServerDTOInfo.put("password", pwd);
    } else if (!pwd.equalsIgnoreCase("none")) {
    	udpConsoleServerDTOInfo.put("password", pwd);
    }
    udpConsoleServerDTOInfo.put("port", port );
    
    if (protocol   == null || protocol   == "") {
    	udpConsoleServerDTOInfo.put("protocol", protocol  );
    } else if (!protocol  .equalsIgnoreCase("none")) {
    	udpConsoleServerDTOInfo.put("protocol", protocol  );
    }
    return udpConsoleServerDTOInfo;
  }
  
  /**
   * used to compose CloudDirectLocalBackupDTO info
   * 
   * @author shan.jing
   * @param path, String
   * @param enabled, String
   * @return
   */
  public HashMap<String, Object> composeCloudDirectLocalBackupDTO(String path, String enabled) {

    HashMap<String, Object> cloudDirectLocalBackupDTOInfo = new HashMap<String, Object>();
    if (path == null || path == "") {
      cloudDirectLocalBackupDTOInfo.put("path", path);
    } else if (!path.equalsIgnoreCase("none")) {
      cloudDirectLocalBackupDTOInfo.put("path", path);
    }
    if (enabled == null || enabled == "") {
      cloudDirectLocalBackupDTOInfo.put("enabled", enabled);
    } else if (!enabled.equalsIgnoreCase("none")) {
      cloudDirectLocalBackupDTOInfo.put("enabled", enabled);
    }
    return cloudDirectLocalBackupDTOInfo;
  }


  /**
   * used to compose NATOptionDTO info
   * 
   * @author shan.jing
   * @param host_name, String
   * @param port, String
   * @return
   */
  public HashMap<String, Object> composeNATOptionDTO(String host_name, String port) {

    HashMap<String, Object> nATOptionDTOInfo = new HashMap<String, Object>();
    if (host_name == null || host_name == "") {
      nATOptionDTOInfo.put("host_name", host_name);
    } else if (!host_name.equalsIgnoreCase("none")) {
      nATOptionDTOInfo.put("host_name", host_name);
    }
    if (port == null || port == "") {
      nATOptionDTOInfo.put("port", port);
    } else if (!port.equalsIgnoreCase("none")) {
      nATOptionDTOInfo.put("port", port);
    }
    return nATOptionDTOInfo;
  }


  /**
   * used to compose UdpReplicationFromRemoteEmailDTO info
   * 
   * @author shan.jing
   * @param proxy_option , HashMap<String, Object>
   * @param mail_server_name, String
   * @param smp_port, String
   * @param username, String
   * @param password, String
   * @param subject , String
   * @param from , String
   * @param recipients, String
   * @param is_enable_ssl, String
   * @param is_enable_tsl, String
   * @param is_enable_html_dormat, String
   * @param email_send_condition, HashMap<String, Object>
   * @return
   */
  public HashMap<String, Object> composeUdpReplicationFromRemoteEmailDTO(
      HashMap<String, Object> proxy_option, String mail_server_name, String smp_port,
      String username, String password, String subject, String from, String recipients,
      String is_enable_ssl, String is_enable_tsl, String is_enable_html_format,
      HashMap<String, Object> email_send_condition) {

    HashMap<String, Object> udpReplicationFromRemoteEmailDTOInfo = new HashMap<String, Object>();
    if (proxy_option == null) {
      udpReplicationFromRemoteEmailDTOInfo.put("proxy_option", proxy_option);
    } else {
      udpReplicationFromRemoteEmailDTOInfo.put("proxy_option", proxy_option);
    }
    if (mail_server_name == null || mail_server_name == "") {
      udpReplicationFromRemoteEmailDTOInfo.put("mail_server_name", mail_server_name);
    } else if (!mail_server_name.equalsIgnoreCase("none")) {
      udpReplicationFromRemoteEmailDTOInfo.put("mail_server_name", mail_server_name);
    }
    if (smp_port == null || smp_port == "") {
      udpReplicationFromRemoteEmailDTOInfo.put("smp_port", smp_port);
    } else if (!smp_port.equalsIgnoreCase("none")) {
      udpReplicationFromRemoteEmailDTOInfo.put("smp_port", smp_port);
    }
    if (username == null || username == "") {
      udpReplicationFromRemoteEmailDTOInfo.put("username", username);
    } else if (!username.equalsIgnoreCase("none")) {
      udpReplicationFromRemoteEmailDTOInfo.put("username", username);
    }
    if (password == null || password == "") {
      udpReplicationFromRemoteEmailDTOInfo.put("password", password);
    } else if (!password.equalsIgnoreCase("none")) {
      udpReplicationFromRemoteEmailDTOInfo.put("password", password);
    }
    if (subject == null || subject == "") {
      udpReplicationFromRemoteEmailDTOInfo.put("subject", mail_server_name);
    } else if (!subject.equalsIgnoreCase("none")) {
      udpReplicationFromRemoteEmailDTOInfo.put("subject", mail_server_name);
    }
    if (from == null || from == "") {
      udpReplicationFromRemoteEmailDTOInfo.put("from", from);
    } else if (!from.equalsIgnoreCase("none")) {
      udpReplicationFromRemoteEmailDTOInfo.put("from", from);
    }
    if (recipients == null || recipients == "") {
      udpReplicationFromRemoteEmailDTOInfo.put("recipients", recipients);
    } else if (!recipients.equalsIgnoreCase("none")) {
      udpReplicationFromRemoteEmailDTOInfo.put("recipients", recipients);
    }
    if (is_enable_ssl == null || is_enable_ssl == "") {
      udpReplicationFromRemoteEmailDTOInfo.put("is_enable_ssl", is_enable_ssl);
    } else if (!is_enable_ssl.equalsIgnoreCase("none")) {
      udpReplicationFromRemoteEmailDTOInfo.put("is_enable_ssl", is_enable_ssl);
    }
    if (is_enable_tsl == null || is_enable_tsl == "") {
      udpReplicationFromRemoteEmailDTOInfo.put("is_enable_tsl", is_enable_tsl);
    } else if (!is_enable_tsl.equalsIgnoreCase("none")) {
      udpReplicationFromRemoteEmailDTOInfo.put("is_enable_tsl", is_enable_tsl);
    }
    if (is_enable_html_format == null || is_enable_html_format == "") {
      udpReplicationFromRemoteEmailDTOInfo.put("is_enable_html_format", is_enable_html_format);
    } else if (!is_enable_html_format.equalsIgnoreCase("none")) {
      udpReplicationFromRemoteEmailDTOInfo.put("is_enable_html_format", is_enable_html_format);
    }
    if (email_send_condition == null) {
      udpReplicationFromRemoteEmailDTOInfo.put("email_send_condition", email_send_condition);
    } else {
      udpReplicationFromRemoteEmailDTOInfo.put("email_send_condition", email_send_condition);
    }
    return udpReplicationFromRemoteEmailDTOInfo;
  }


  /**
   * used to compose CloudDirectScheduleDTO info
   * 
   * @author shan.jing
   * @param crontab_string, String
   * @return
   */
  public HashMap<String, Object> composeCloudDirectScheduleDTO(String crontab_string) {

    HashMap<String, Object> cloudDirectScheduleDTOInfo = new HashMap<String, Object>();
    if (crontab_string == null || crontab_string == "") {
      cloudDirectScheduleDTOInfo.put("crontab_string", crontab_string);
    } else if (!crontab_string.equalsIgnoreCase("none")) {
      cloudDirectScheduleDTOInfo.put("crontab_string", crontab_string);
    }
    return cloudDirectScheduleDTOInfo;
  }


  /**
   * used to compose CustomScheduleDTO info
   * 
   * @author shan.jing
   * @param first_start_time , String
   * @param backup_method , String
   * @param days_of_week , String
   * @param is_repeat_enable , String
   * @param interval , String
   * @param interval_unit , String
   * @return
   */
  public HashMap<String, Object> composeCustomScheduleDTO(String first_start_time,
      String backup_method, String days_of_week, String is_repeat_enable, String interval,
      String interval_unit) {

    HashMap<String, Object> customScheduleDTOInfo = new HashMap<String, Object>();
    if (first_start_time == null || first_start_time == "") {
      customScheduleDTOInfo.put("first_start_time", first_start_time);
    } else if (!first_start_time.equalsIgnoreCase("none")) {
      customScheduleDTOInfo.put("first_start_time", first_start_time);
    }
    if (backup_method == null || backup_method == "") {
      customScheduleDTOInfo.put("backup_method", backup_method);
    } else if (!backup_method.equalsIgnoreCase("none")) {
      customScheduleDTOInfo.put("backup_method", backup_method);
    }
    if (days_of_week == null || days_of_week == "") {
      customScheduleDTOInfo.put("days_of_week", days_of_week);
    } else if (!days_of_week.equalsIgnoreCase("none")) {
      customScheduleDTOInfo.put("days_of_week", days_of_week);
    }
    if (is_repeat_enable == null || is_repeat_enable == "") {
      customScheduleDTOInfo.put("is_repeat_enable", is_repeat_enable);
    } else if (!is_repeat_enable.equalsIgnoreCase("none")) {
      customScheduleDTOInfo.put("is_repeat_enable", is_repeat_enable);
    }
    if (interval == null || interval == "") {
      customScheduleDTOInfo.put("interval", Integer.parseInt(interval));
    } else if (!interval.equalsIgnoreCase("none")) {
      customScheduleDTOInfo.put("interval", Integer.parseInt(interval));
    }
    if (interval_unit == null || interval_unit == "") {
      customScheduleDTOInfo.put("interval_unit", interval_unit);
    } else if (!interval_unit.equalsIgnoreCase("none")) {
      customScheduleDTOInfo.put("interval_unit", interval_unit);
    }
    return customScheduleDTOInfo;
  }


  /**
   * used to compose ExcludeInfoDTO info
   * 
   * @author shan.jing
   * @param type , String
   * @param value , String
   * @return
   */
  public HashMap<String, Object> composeExcludeInfoDTO(String type, String value) {

    HashMap<String, Object> excludeInfoDTOInfo = new HashMap<String, Object>();
    if (type == null || type == "") {
      excludeInfoDTOInfo.put("type", type);
    } else if (!type.equalsIgnoreCase("none")) {
      excludeInfoDTOInfo.put("type", type);
    }
    if (value == null || value == "") {
      excludeInfoDTOInfo.put("value", value);
    } else if (!value.equalsIgnoreCase("none")) {
      excludeInfoDTOInfo.put("value", value);
    }
    return excludeInfoDTOInfo;
  }


  /**
   * used to compose EmailProxyOptionDTO info
   * 
   * @author shan.jing
   * @param server_address , String
   * @param port , String
   * @param username , String
   * @param password , String
   * @return
   */
  public HashMap<String, Object> composeEmailProxyOptionDTO(String server_address, String port,
      String username, String password) {

    HashMap<String, Object> customEmailProxyOptionDTOInfo = new HashMap<String, Object>();
    if (server_address == null || server_address == "") {
      customEmailProxyOptionDTOInfo.put("server_address", server_address);
    } else if (!server_address.equalsIgnoreCase("none")) {
      customEmailProxyOptionDTOInfo.put("server_address", server_address);
    }
    if (port == null || port == "") {
      customEmailProxyOptionDTOInfo.put("port", port);
    } else if (!port.equalsIgnoreCase("none")) {
      customEmailProxyOptionDTOInfo.put("port", port);
    }
    if (username == null || username == "") {
      customEmailProxyOptionDTOInfo.put("username", username);
    } else if (!username.equalsIgnoreCase("none")) {
      customEmailProxyOptionDTOInfo.put("username", username);
    }
    if (password == null || password == "") {
      customEmailProxyOptionDTOInfo.put("password", password);
    } else if (!password.equalsIgnoreCase("none")) {
      customEmailProxyOptionDTOInfo.put("password", password);
    }
    return customEmailProxyOptionDTOInfo;
  }


  /**
   * used to compose ReplicationFromRemoteEmailSendCondition info
   * 
   * @author shan.jing
   * @param job_condition, HashMap<String, Object>
   * @return
   */
  public HashMap<String, Object> composeReplicationFromRemoteEmailSendCondition(
      HashMap<String, Object> job_condition) {

    HashMap<String, Object> replicationFromRemoteEmailSendConditionInfo =
        new HashMap<String, Object>();
    if (job_condition == null) {
      replicationFromRemoteEmailSendConditionInfo.put("job_condition", job_condition);
    } else {
      replicationFromRemoteEmailSendConditionInfo.put("job_condition", job_condition);
    }
    return replicationFromRemoteEmailSendConditionInfo;
  }


  /**
   * used to compose ReplicationFromRemoteJobConditionDTO info
   * 
   * @author shan.jing
   * @param on_job_missed , String
   * @param on_replicate_failed , String
   * @param on_replicate_successed , String
   * @param on_merge_failed , String
   * @param on_merge_successed , String
   * @return
   */
  public HashMap<String, Object> composeReplicationFromRemoteJobConditionDTO(String on_job_missed,
      String on_replicate_failed, String on_replicate_successed, String on_merge_failed,
      String on_merge_successed) {

    HashMap<String, Object> replicationFromRemoteJobConditionDTOInfo =
        new HashMap<String, Object>();
    if (on_job_missed == null || on_job_missed == "") {
      replicationFromRemoteJobConditionDTOInfo.put("on_job_missed", on_job_missed);
    } else if (!on_job_missed.equalsIgnoreCase("none")) {
      replicationFromRemoteJobConditionDTOInfo.put("on_job_missed", on_job_missed);
    }
    if (on_replicate_failed == null || on_replicate_failed == "") {
      replicationFromRemoteJobConditionDTOInfo.put("on_replicate_failed", on_replicate_failed);
    } else if (!on_replicate_failed.equalsIgnoreCase("none")) {
      replicationFromRemoteJobConditionDTOInfo.put("on_replicate_failed", on_replicate_failed);
    }
    if (on_replicate_successed == null || on_replicate_successed == "") {
      replicationFromRemoteJobConditionDTOInfo.put("on_replicate_successed",
          on_replicate_successed);
    } else if (!on_replicate_successed.equalsIgnoreCase("none")) {
      replicationFromRemoteJobConditionDTOInfo.put("on_replicate_successed",
          on_replicate_successed);
    }
    if (on_merge_failed == null || on_merge_failed == "") {
      replicationFromRemoteJobConditionDTOInfo.put("on_merge_failed", on_merge_failed);
    } else if (!on_merge_failed.equalsIgnoreCase("none")) {
      replicationFromRemoteJobConditionDTOInfo.put("on_merge_failed", on_merge_failed);
    }
    if (on_merge_successed == null || on_merge_successed == "") {
      replicationFromRemoteJobConditionDTOInfo.put("on_merge_successed", on_merge_successed);
    } else if (!on_merge_successed.equalsIgnoreCase("none")) {
      replicationFromRemoteJobConditionDTOInfo.put("on_merge_successed", on_merge_successed);
    }
    return replicationFromRemoteJobConditionDTOInfo;
  }


  /**
   * used to compose CloudDirectTask info
   * 
   * @author shan.jing
   * @param volumn_type, String
   * @param drives, String[]
   * @param local_backup, String
   * @param enabled, String
   * @param path, String
   * @param prebackup_script_path String
   * @param prebackup_script_stop, String
   * @param postbackup_script_path String
   * @return
   */
  public HashMap<String, Object> composeCloudDirectTaskInfoDTO(String volumn_type, String drives,
      String local_backup, String enabled, String path, String prebackup_script_path,
      String prebackup_script_stop, String postbackup_script_path) {

    HashMap<String, Object> cloudDirectTaskInfoDTOInfo = new HashMap<String, Object>();
    String[] emptyArray = new String[0];
    if (volumn_type == null || volumn_type == "") {
      cloudDirectTaskInfoDTOInfo.put("volumn_type", volumn_type);
    } else if (!volumn_type.equalsIgnoreCase("none")) {
      cloudDirectTaskInfoDTOInfo.put("volumn_type", volumn_type);
    }
    if (drives == null || drives == "") {
      cloudDirectTaskInfoDTOInfo.put("drives", drives);
    } else if (drives.equalsIgnoreCase("emptyarray")) {
      cloudDirectTaskInfoDTOInfo.put("drives", emptyArray);
    } else if (!drives.equalsIgnoreCase("none")) {
      cloudDirectTaskInfoDTOInfo.put("drives", drives.replace(" ", "").split(","));
    }
    if (local_backup == null || local_backup == "") {
      cloudDirectTaskInfoDTOInfo.put("local_backup", local_backup);
    } else if (!local_backup.equalsIgnoreCase("none")) {
      cloudDirectTaskInfoDTOInfo.put("local_backup", local_backup);
    }
    if (enabled == null || enabled == "") {
      cloudDirectTaskInfoDTOInfo.put("enabled", enabled);
    } else if (!enabled.equalsIgnoreCase("none")) {
      cloudDirectTaskInfoDTOInfo.put("enabled", enabled);
    }
    if (path == null || path == "") {
      cloudDirectTaskInfoDTOInfo.put("path", path);
    } else if (!path.equalsIgnoreCase("none")) {
      cloudDirectTaskInfoDTOInfo.put("path", path);
    }
    if (prebackup_script_path == null || prebackup_script_path == "") {
      cloudDirectTaskInfoDTOInfo.put("prebackup_script_path", prebackup_script_path);
    } else if (!prebackup_script_path.equalsIgnoreCase("none")) {
      cloudDirectTaskInfoDTOInfo.put("prebackup_script_path", prebackup_script_path);
    }
    if (prebackup_script_stop == null || prebackup_script_stop == "") {
      cloudDirectTaskInfoDTOInfo.put("prebackup_script_stop", prebackup_script_stop);
    } else if (!prebackup_script_stop.equalsIgnoreCase("none")) {
      cloudDirectTaskInfoDTOInfo.put("prebackup_script_stop", prebackup_script_stop);
    }
    if (postbackup_script_path == null || postbackup_script_path == "") {
      cloudDirectTaskInfoDTOInfo.put("postbackup_script_path", postbackup_script_path);
    } else if (!postbackup_script_path.equalsIgnoreCase("none")) {
      cloudDirectTaskInfoDTOInfo.put("postbackup_script_path", postbackup_script_path);
    }
    return cloudDirectTaskInfoDTOInfo;
  }


  /**
   * used to compose UdpAgentbaseBackupInfoDTO info
   * 
   * @author shan.jing
   * @param datastore_option, HashMap<String, Object>
   * @param share_folder_option, HashMap<String, Object>
   * @param snapshot_type, String
   * @param sql_log_truncate_range, String
   * @param exchange_log_truncate_range, String
   * @param post_snapshot_script_path, String
   * @param pre_backup_scipt_option, HashMap<String, Object>
   * @param post_backup_scipt_option, HashMap<String, Object>
   * @param username_for_command, String
   * @param password_for_command , String
   * @param volumes, String[]
   * @param email_setting, HashMap<String, Object>
   * @return
   */
  public HashMap<String, Object> composeUdpAgentbaseBackupInfoDTO(
      HashMap<String, Object> datastore_option, HashMap<String, Object> share_folder_option,
      String snapshot_type, String sql_log_truncate_range, String exchange_log_truncate_range,
      String post_snapshot_script_path, HashMap<String, Object> pre_backup_scipt_option,
      HashMap<String, Object> post_backup_scipt_option, String username_for_command,
      String password_for_command, String volumes, HashMap<String, Object> email_setting) {

    HashMap<String, Object> udpAgentbaseBackupInfoDTOInfo = new HashMap<String, Object>();
    String[] emptyArray = new String[0];
    if (datastore_option == null) {
      udpAgentbaseBackupInfoDTOInfo.put("datastore_option", datastore_option);
    } else {
      udpAgentbaseBackupInfoDTOInfo.put("datastore_option", datastore_option);
    }
    if (share_folder_option == null) {
      udpAgentbaseBackupInfoDTOInfo.put("share_folder_option", share_folder_option);
    } else {
      udpAgentbaseBackupInfoDTOInfo.put("share_folder_option", share_folder_option);
    }
    if (snapshot_type == null || snapshot_type == "") {
      udpAgentbaseBackupInfoDTOInfo.put("snapshot_type", snapshot_type);
    } else if (!snapshot_type.equalsIgnoreCase("none")) {
      udpAgentbaseBackupInfoDTOInfo.put("snapshot_type", snapshot_type);
    }
    if (sql_log_truncate_range == null || sql_log_truncate_range == "") {
      udpAgentbaseBackupInfoDTOInfo.put("sql_log_truncate_range", sql_log_truncate_range);
    } else if (!sql_log_truncate_range.equalsIgnoreCase("none")) {
      udpAgentbaseBackupInfoDTOInfo.put("sql_log_truncate_range", sql_log_truncate_range);
    }
    if (exchange_log_truncate_range == null || exchange_log_truncate_range == "") {
      udpAgentbaseBackupInfoDTOInfo.put("exchange_log_truncate_range", exchange_log_truncate_range);
    } else if (!exchange_log_truncate_range.equalsIgnoreCase("none")) {
      udpAgentbaseBackupInfoDTOInfo.put("exchange_log_truncate_range", exchange_log_truncate_range);
    }
    if (post_snapshot_script_path == null || post_snapshot_script_path == "") {
      udpAgentbaseBackupInfoDTOInfo.put("post_snapshot_script_path", post_snapshot_script_path);
    } else if (!post_snapshot_script_path.equalsIgnoreCase("none")) {
      udpAgentbaseBackupInfoDTOInfo.put("post_snapshot_script_path", post_snapshot_script_path);
    }
    if (pre_backup_scipt_option == null) {
      udpAgentbaseBackupInfoDTOInfo.put("pre_backup_scipt_option", pre_backup_scipt_option);
    } else {
      udpAgentbaseBackupInfoDTOInfo.put("pre_backup_scipt_option", pre_backup_scipt_option);
    }
    if (post_backup_scipt_option == null) {
      udpAgentbaseBackupInfoDTOInfo.put("post_backup_scipt_option", post_backup_scipt_option);
    } else {
      udpAgentbaseBackupInfoDTOInfo.put("post_backup_scipt_option", post_backup_scipt_option);
    }
    if (username_for_command == null || username_for_command == "") {
      udpAgentbaseBackupInfoDTOInfo.put("username_for_command", username_for_command);
    } else if (!username_for_command.equalsIgnoreCase("none")) {
      udpAgentbaseBackupInfoDTOInfo.put("username_for_command", username_for_command);
    }
    if (password_for_command == null || password_for_command == "") {
      udpAgentbaseBackupInfoDTOInfo.put("password_for_command", password_for_command);
    } else if (!password_for_command.equalsIgnoreCase("none")) {
      udpAgentbaseBackupInfoDTOInfo.put("password_for_command", password_for_command);
    }
    if (volumes == null || volumes == "") {
      udpAgentbaseBackupInfoDTOInfo.put("volumes", volumes);
    } else if (volumes.equalsIgnoreCase("emptyarray")) {
      udpAgentbaseBackupInfoDTOInfo.put("volumes", emptyArray);
    } else if (!volumes.equalsIgnoreCase("none")) {
      udpAgentbaseBackupInfoDTOInfo.put("volumes", volumes.replace(" ", "").split(","));
    }
    if (email_setting == null) {
      udpAgentbaseBackupInfoDTOInfo.put("email_setting", email_setting);
    } else {
      udpAgentbaseBackupInfoDTOInfo.put("email_setting", email_setting);
    }
    return udpAgentbaseBackupInfoDTOInfo;
  }


  /**
   * used to compose UdpAgentbaseBackupInfoDTO info
   * 
   * @author shan.jing
   * @param datastore_option, HashMap<String, Object>
   * @param share_folder_option, HashMap<String, Object>
   * @param snapshot_type, String
   * @param sql_log_truncate_range, String
   * @param exchange_log_truncate_range, String
   * @param post_snapshot_script_path, String
   * @param pre_backup_scipt_option, HashMap<String, Object>
   * @param post_backup_scipt_option, HashMap<String, Object>
   * @param username_for_command, String
   * @param password_for_command , String
   * @param proxy_option, HashMap<String, Object>
   * @param exclude_disks, String[]
   * @param vmware_snapshot_quiescence_method, String
   * @param take_snapshot_without_quescing_guest, String
   * @param vmware_transport_option, HashMap<String, Object>
   * @param hyperv_transport_option, HashMap<String, Object>
   * @param use_transportable_snapshot , String
   * @param storage_array, String[]
   * @param email_setting, HashMap<String, Object>
   * @return
   */
  public HashMap<String, Object> composeUdpAgentlessBackupInfoDTO(
      HashMap<String, Object> datastore_option, HashMap<String, Object> share_folder_option,
      String snapshot_type, String sql_log_truncate_range, String exchange_log_truncate_range,
      String post_snapshot_script_path, HashMap<String, Object> pre_backup_scipt_option,
      HashMap<String, Object> post_backup_scipt_option, String username_for_command,
      String password_for_command, HashMap<String, Object> proxy_option, String exclude_disks,
      String vmware_snapshot_quiescence_method, String take_snapshot_without_quescing_guest,
      HashMap<String, Object> vmware_transport_option,
      HashMap<String, Object> hyperv_transport_option, String use_transportable_snapshot,
      String storage_array, HashMap<String, Object> email_setting) {

    HashMap<String, Object> udpAgentlessBackupInfoDTOInfo = new HashMap<String, Object>();
    String[] emptyArray = new String[0];
    if (datastore_option == null) {
      udpAgentlessBackupInfoDTOInfo.put("datastore_option", datastore_option);
    } else {
      udpAgentlessBackupInfoDTOInfo.put("datastore_option", datastore_option);
    }
    if (share_folder_option == null) {
      udpAgentlessBackupInfoDTOInfo.put("share_folder_option", share_folder_option);
    } else {
      udpAgentlessBackupInfoDTOInfo.put("share_folder_option", share_folder_option);
    }
    if (snapshot_type == null || snapshot_type == "") {
      udpAgentlessBackupInfoDTOInfo.put("snapshot_type", snapshot_type);
    } else if (!snapshot_type.equalsIgnoreCase("none")) {
      udpAgentlessBackupInfoDTOInfo.put("snapshot_type", snapshot_type);
    }
    if (sql_log_truncate_range == null || sql_log_truncate_range == "") {
      udpAgentlessBackupInfoDTOInfo.put("sql_log_truncate_range", sql_log_truncate_range);
    } else if (!sql_log_truncate_range.equalsIgnoreCase("none")) {
      udpAgentlessBackupInfoDTOInfo.put("sql_log_truncate_range", sql_log_truncate_range);
    }
    if (exchange_log_truncate_range == null || exchange_log_truncate_range == "") {
      udpAgentlessBackupInfoDTOInfo.put("exchange_log_truncate_range", exchange_log_truncate_range);
    } else if (!exchange_log_truncate_range.equalsIgnoreCase("none")) {
      udpAgentlessBackupInfoDTOInfo.put("exchange_log_truncate_range", exchange_log_truncate_range);
    }
    if (post_snapshot_script_path == null || post_snapshot_script_path == "") {
      udpAgentlessBackupInfoDTOInfo.put("post_snapshot_script_path", post_snapshot_script_path);
    } else if (!post_snapshot_script_path.equalsIgnoreCase("none")) {
      udpAgentlessBackupInfoDTOInfo.put("post_snapshot_script_path", post_snapshot_script_path);
    }
    if (pre_backup_scipt_option == null) {
      udpAgentlessBackupInfoDTOInfo.put("pre_backup_scipt_option", pre_backup_scipt_option);
    } else {
      udpAgentlessBackupInfoDTOInfo.put("pre_backup_scipt_option", pre_backup_scipt_option);
    }
    if (post_backup_scipt_option == null) {
      udpAgentlessBackupInfoDTOInfo.put("post_backup_scipt_option", post_backup_scipt_option);
    } else {
      udpAgentlessBackupInfoDTOInfo.put("post_backup_scipt_option", post_backup_scipt_option);
    }
    if (username_for_command == null || username_for_command == "") {
      udpAgentlessBackupInfoDTOInfo.put("username_for_command", username_for_command);
    } else if (!username_for_command.equalsIgnoreCase("none")) {
      udpAgentlessBackupInfoDTOInfo.put("username_for_command", username_for_command);
    }
    if (password_for_command == null || password_for_command == "") {
      udpAgentlessBackupInfoDTOInfo.put("password_for_command", password_for_command);
    } else if (!password_for_command.equalsIgnoreCase("none")) {
      udpAgentlessBackupInfoDTOInfo.put("password_for_command", password_for_command);
    }
    if (proxy_option == null) {
      udpAgentlessBackupInfoDTOInfo.put("proxy_option", proxy_option);
    } else {
      udpAgentlessBackupInfoDTOInfo.put("proxy_option", proxy_option);
    }
    if (exclude_disks == null || exclude_disks == "") {
      udpAgentlessBackupInfoDTOInfo.put("exclude_disks", exclude_disks);
    } else if (exclude_disks.equalsIgnoreCase("emptyarray")) {
      udpAgentlessBackupInfoDTOInfo.put("exclude_disks", emptyArray);
    } else if (!exclude_disks.equalsIgnoreCase("none")) {
      udpAgentlessBackupInfoDTOInfo.put("exclude_disks", exclude_disks.replace(" ", "").split(","));
    }
    if (vmware_snapshot_quiescence_method == null || vmware_snapshot_quiescence_method == "") {
      udpAgentlessBackupInfoDTOInfo.put("vmware_snapshot_quiescence_method",
          vmware_snapshot_quiescence_method);
    } else if (!vmware_snapshot_quiescence_method.equalsIgnoreCase("none")) {
      udpAgentlessBackupInfoDTOInfo.put("vmware_snapshot_quiescence_method",
          vmware_snapshot_quiescence_method);
    }
    if (take_snapshot_without_quescing_guest == null
        || take_snapshot_without_quescing_guest == "") {
      udpAgentlessBackupInfoDTOInfo.put("take_snapshot_without_quescing_guest",
          take_snapshot_without_quescing_guest);
    } else if (!take_snapshot_without_quescing_guest.equalsIgnoreCase("none")) {
      udpAgentlessBackupInfoDTOInfo.put("take_snapshot_without_quescing_guest",
          take_snapshot_without_quescing_guest);
    }
    if (vmware_transport_option == null) {
      udpAgentlessBackupInfoDTOInfo.put("vmware_transport_option", vmware_transport_option);
    } else {
      udpAgentlessBackupInfoDTOInfo.put("vmware_transport_option", vmware_transport_option);
    }
    if (hyperv_transport_option == null) {
      udpAgentlessBackupInfoDTOInfo.put("hyperv_transport_option", hyperv_transport_option);
    } else {
      udpAgentlessBackupInfoDTOInfo.put("hyperv_transport_option", hyperv_transport_option);
    }
    if (use_transportable_snapshot == null || use_transportable_snapshot == "") {
      udpAgentlessBackupInfoDTOInfo.put("use_transportable_snapshot ", use_transportable_snapshot);
    } else if (!vmware_snapshot_quiescence_method.equalsIgnoreCase("none")) {
      udpAgentlessBackupInfoDTOInfo.put("use_transportable_snapshot ", use_transportable_snapshot);
    }
    if (storage_array == null || storage_array == "") {
      udpAgentlessBackupInfoDTOInfo.put("storage_array", storage_array);
    } else if (storage_array.equalsIgnoreCase("emptyarray")) {
      udpAgentlessBackupInfoDTOInfo.put("storage_array", emptyArray);
    } else if (!storage_array.equalsIgnoreCase("none")) {
      udpAgentlessBackupInfoDTOInfo.put("storage_array", storage_array.replace(" ", "").split(","));
    }
    if (email_setting == null) {
      udpAgentlessBackupInfoDTOInfo.put("email_setting", email_setting);
    } else {
      udpAgentlessBackupInfoDTOInfo.put("email_setting", email_setting);
    }
    return udpAgentlessBackupInfoDTOInfo;
  }


  /**
   * used to compose ScheduleSettingDTO info
   * 
   * @author shan.jing
   * @param cloud_direct_schedule, HashMap<String, Object>
   * @param recovery_set_schedule, HashMap<String, Object>
   * @param custom_schedule , HashMap<String, Object>
   * @param daily_schedule , HashMap<String, Object>
   * @param weekly_schedule , HashMap<String, Object>
   * @param monthly_schedule , HashMap<String, Object>
   * @param annual_schedule , HashMap<String, Object>
   * @param immediately_schedule , HashMap<String, Object>
   * @return
   */
  public HashMap<String, Object> composeScheduleSettingDTO(
      HashMap<String, Object> cloud_direct_schedule, HashMap<String, Object> recovery_set_schedule,
      HashMap<String, Object> custom_schedule, HashMap<String, Object> daily_schedule,
      HashMap<String, Object> weekly_schedule, HashMap<String, Object> monthly_schedule,
      HashMap<String, Object> annual_schedule, HashMap<String, Object> immediately_schedule) {

    HashMap<String, Object> scheduleSettingDTOInfo = new HashMap<String, Object>();
    if (cloud_direct_schedule == null) {
      scheduleSettingDTOInfo.put("cloud_direct_schedule", cloud_direct_schedule);
    } else {
      scheduleSettingDTOInfo.put("cloud_direct_schedule", cloud_direct_schedule);
    }
    if (recovery_set_schedule == null) {
      scheduleSettingDTOInfo.put("recovery_set_schedule", recovery_set_schedule);
    } else {
      scheduleSettingDTOInfo.put("recovery_set_schedule", recovery_set_schedule);
    }
    if (custom_schedule == null) {
      scheduleSettingDTOInfo.put("custom_schedule", custom_schedule);
    } else {
      scheduleSettingDTOInfo.put("custom_schedule", custom_schedule);
    }
    if (daily_schedule == null) {
      scheduleSettingDTOInfo.put("daily_schedule", daily_schedule);
    } else {
      scheduleSettingDTOInfo.put("daily_schedule", daily_schedule);
    }
    if (weekly_schedule == null) {
      scheduleSettingDTOInfo.put("weekly_schedule", weekly_schedule);
    } else {
      scheduleSettingDTOInfo.put("weekly_schedule", weekly_schedule);
    }
    if (monthly_schedule == null) {
      scheduleSettingDTOInfo.put("monthly_schedule", monthly_schedule);
    } else {
      scheduleSettingDTOInfo.put("monthly_schedule", monthly_schedule);
    }
    if (annual_schedule == null) {
      scheduleSettingDTOInfo.put("annual_schedule", annual_schedule);
    } else {
      scheduleSettingDTOInfo.put("annual_schedule", annual_schedule);
    }
    if (immediately_schedule == null) {
      scheduleSettingDTOInfo.put("immediately_schedule", immediately_schedule);
    } else {
      scheduleSettingDTOInfo.put("immediately_schedule", immediately_schedule);
    }
    return scheduleSettingDTOInfo;
  }


  /**
   * used to compose DatastoreOptionDTO info
   * 
   * @author shan.jing
   * @param session_password, String
   * @return
   */
  public HashMap<String, Object> composeDatastoreOptionDTO(String session_password) {

    HashMap<String, Object> datastoreOptionDTOInfo = new HashMap<String, Object>();
    if (session_password == null || session_password == "") {
      datastoreOptionDTOInfo.put("session_password", session_password);
    } else if (!session_password.equalsIgnoreCase("none")) {
      datastoreOptionDTOInfo.put("session_password", session_password);
    }
    return datastoreOptionDTOInfo;
  }


  /**
   * used to compose ShareFolderOptionDTO info
   * 
   * @author shan.jing
   * @param encryption_algorithm , String
   * @param encryption_algorithm , String
   * @param encryption_algorithm , String
   * @return
   */
  public HashMap<String, Object> composeShareFolderOptionDTO(String encryption_algorithm,
      String encryption_key, String compression_level) {

    HashMap<String, Object> shareFolderOptionDTOInfo = new HashMap<String, Object>();
    if (encryption_algorithm == null || encryption_algorithm == "") {
      shareFolderOptionDTOInfo.put("encryption_algorithm ", encryption_algorithm);
    } else if (!encryption_algorithm.equalsIgnoreCase("none")) {
      shareFolderOptionDTOInfo.put("encryption_algorithm", encryption_algorithm);
    }
    if (encryption_key == null || encryption_key == "") {
      shareFolderOptionDTOInfo.put("encryption_key", encryption_key);
    } else if (!encryption_key.equalsIgnoreCase("none")) {
      shareFolderOptionDTOInfo.put("encryption_key", encryption_key);
    }
    if (compression_level == null || compression_level == "") {
      shareFolderOptionDTOInfo.put("compression_level", compression_level);
    } else if (!compression_level.equalsIgnoreCase("none")) {
      shareFolderOptionDTOInfo.put("compression_level", compression_level);
    }
    return shareFolderOptionDTOInfo;
  }


  /**
   * used to compose PreBackupScriptOptionDTO info
   * 
   * @author shan.jing
   * @param script_exit_code , String
   * @param continue_job , String
   * @param prebackup_script_path , String
   * @return
   */
  public HashMap<String, Object> composePreBackupScriptOptionDTO(String script_exit_code,
      String continue_job, String prebackup_script_path) {

    HashMap<String, Object> preBackupScriptOptionDTOInfo = new HashMap<String, Object>();
    if (script_exit_code == null || script_exit_code == "") {
      preBackupScriptOptionDTOInfo.put("script_exit_code", script_exit_code);
    } else if (!script_exit_code.equalsIgnoreCase("none")) {
      preBackupScriptOptionDTOInfo.put("script_exit_code", script_exit_code);
    }
    if (continue_job == null || continue_job == "") {
      preBackupScriptOptionDTOInfo.put("continue_job", continue_job);
    } else if (!continue_job.equalsIgnoreCase("none")) {
      preBackupScriptOptionDTOInfo.put("continue_job", continue_job);
    }
    if (prebackup_script_path == null || prebackup_script_path == "") {
      preBackupScriptOptionDTOInfo.put("prebackup_script_path", prebackup_script_path);
    } else if (!prebackup_script_path.equalsIgnoreCase("none")) {
      preBackupScriptOptionDTOInfo.put("prebackup_script_path", prebackup_script_path);
    }
    return preBackupScriptOptionDTOInfo;
  }


  /**
   * used to compose PostBackupScriptOptionDTO info
   * 
   * @author shan.jing
   * @param post_backup_script_path , String
   * @param run_on_job_failure , String
   * @return
   */
  public HashMap<String, Object> composePostBackupScriptOptionDTO(String post_backup_script_path,
      String run_on_job_failure) {

    HashMap<String, Object> postBackupScriptOptionDTOInfo = new HashMap<String, Object>();
    if (post_backup_script_path == null || post_backup_script_path == "") {
      postBackupScriptOptionDTOInfo.put("post_backup_script_path", post_backup_script_path);
    } else if (!post_backup_script_path.equalsIgnoreCase("none")) {
      postBackupScriptOptionDTOInfo.put("post_backup_script_path", post_backup_script_path);
    }
    if (run_on_job_failure == null || run_on_job_failure == "") {
      postBackupScriptOptionDTOInfo.put("run_on_job_failure", run_on_job_failure);
    } else if (!run_on_job_failure.equalsIgnoreCase("none")) {
      postBackupScriptOptionDTOInfo.put("run_on_job_failure", run_on_job_failure);
    }
    return postBackupScriptOptionDTOInfo;
  }



  /**
   * used to compose ProxyInfoDTO info
   * 
   * @author shan.jing
   * @param proxy_id, String
   * @param smp_port, String
   * @param username, String
   * @param password, String
   * @param protocol , String
   * @param port , String
   * @return
   */
  public HashMap<String, Object> composeProxyInfoDTO(String proxy_id, String proxy_name,
      String username, String password, String protocol, String port) {

    HashMap<String, Object> proxyInfoDTOInfo = new HashMap<String, Object>();
    if (proxy_id == null || proxy_id == "") {
      proxyInfoDTOInfo.put("proxy_id", proxy_id);
    } else if (!proxy_id.equalsIgnoreCase("none")) {
      proxyInfoDTOInfo.put("proxy_id", proxy_id);
    }
    if (proxy_name == null || proxy_name == "") {
      proxyInfoDTOInfo.put("proxy_name", proxy_name);
    } else if (!proxy_name.equalsIgnoreCase("none")) {
      proxyInfoDTOInfo.put("proxy_name", proxy_name);
    }
    if (username == null || username == "") {
      proxyInfoDTOInfo.put("username", username);
    } else if (!username.equalsIgnoreCase("none")) {
      proxyInfoDTOInfo.put("username", username);
    }
    if (password == null || password == "") {
      proxyInfoDTOInfo.put("password", password);
    } else if (!password.equalsIgnoreCase("none")) {
      proxyInfoDTOInfo.put("password", password);
    }
    if (protocol == null || protocol == "") {
      proxyInfoDTOInfo.put("protocol", protocol);
    } else if (!protocol.equalsIgnoreCase("none")) {
      proxyInfoDTOInfo.put("protocol", protocol);
    }
    if (port == null || port == "") {
      proxyInfoDTOInfo.put("port", port);
    } else if (!protocol.equalsIgnoreCase("none")) {
      proxyInfoDTOInfo.put("port", port);
    }
    return proxyInfoDTOInfo;
  }


  /**
   * used to compose ExcludeVDDTO info
   * 
   * @author shan.jing
   * @param source_id, String
   * @param controller_type, String
   * @param controller_id , String
   * @param slot_num , String
   * @return
   */
  public HashMap<String, Object> composeExcludeVDDTO(String source_id, String controller_type,
      String controller_id, String slot_num) {

    HashMap<String, Object> excludeVDDTOInfo = new HashMap<String, Object>();
    if (source_id == null || source_id == "") {
      excludeVDDTOInfo.put("source_id", source_id);
    } else if (!source_id.equalsIgnoreCase("none")) {
      excludeVDDTOInfo.put("source_id", source_id);
    }
    if (controller_type == null || controller_type == "") {
      excludeVDDTOInfo.put("controller_type", controller_type);
    } else if (!controller_type.equalsIgnoreCase("none")) {
      excludeVDDTOInfo.put("controller_type", controller_type);
    }
    if (controller_id == null || controller_id == "") {
      excludeVDDTOInfo.put("controller_id", controller_id);
    } else if (!controller_id.equalsIgnoreCase("none")) {
      excludeVDDTOInfo.put("controller_id", controller_id);
    }
    if (slot_num == null || slot_num == "") {
      excludeVDDTOInfo.put("slot_num", slot_num);
    } else if (!slot_num.equalsIgnoreCase("none")) {
      excludeVDDTOInfo.put("slot_num", slot_num);
    }
    return excludeVDDTOInfo;
  }


  /**
   * used to compose VmwareTransportOptionDTO info
   * 
   * @author shan.jing
   * @param select_method_by , String
   * @param method_priorities , String[]
   * @return
   */
  public HashMap<String, Object> composeVmwareTransportOptionDTO(String select_method_by,
      String method_priorities) {

    HashMap<String, Object> vmwareTransportOptionDTOInfo = new HashMap<String, Object>();
    String[] emptyArray = new String[0];
    if (select_method_by == null || select_method_by == "") {
      vmwareTransportOptionDTOInfo.put("select_method_by", select_method_by);
    } else if (!select_method_by.equalsIgnoreCase("none")) {
      vmwareTransportOptionDTOInfo.put("select_method_by", select_method_by);
    }
    if (method_priorities == null || method_priorities == "") {
      vmwareTransportOptionDTOInfo.put("method_priorities", method_priorities);
    } else if (method_priorities.equalsIgnoreCase("emptyArray")) {
      vmwareTransportOptionDTOInfo.put("method_priorities", emptyArray);
    } else if (!method_priorities.equalsIgnoreCase("none")) {
      vmwareTransportOptionDTOInfo.put("method_priorities",
          method_priorities.replace(" ", "").split(","));
    }
    return vmwareTransportOptionDTOInfo;
  }


  /**
   * used to compose HypervTransportOptionDTO info
   * 
   * @author shan.jing
   * @param using_snapshot_generated_by_microsoft_vss , String
   * @param place_in_save_state_before_snapshot , String[]
   * @return
   */
  public HashMap<String, Object> composeHypervTransportOptionDTO(
      String using_snapshot_generated_by_microsoft_vss,
      String place_in_save_state_before_snapshot) {

    HashMap<String, Object> hypervTransportOptionDTOInfo = new HashMap<String, Object>();
    if (using_snapshot_generated_by_microsoft_vss == null
        || using_snapshot_generated_by_microsoft_vss == "") {
      hypervTransportOptionDTOInfo.put("using_snapshot_generated_by_microsoft_vss",
          using_snapshot_generated_by_microsoft_vss);
    } else if (!using_snapshot_generated_by_microsoft_vss.equalsIgnoreCase("none")) {
      hypervTransportOptionDTOInfo.put("using_snapshot_generated_by_microsoft_vss",
          using_snapshot_generated_by_microsoft_vss);
    }
    if (place_in_save_state_before_snapshot == null || place_in_save_state_before_snapshot == "") {
      hypervTransportOptionDTOInfo.put("place_in_save_state_before_snapshot",
          place_in_save_state_before_snapshot);
    } else if (!place_in_save_state_before_snapshot.equalsIgnoreCase("none")) {
      hypervTransportOptionDTOInfo.put("place_in_save_state_before_snapshot",
          place_in_save_state_before_snapshot);
    }
    return hypervTransportOptionDTOInfo;
  }


  /**
   * used to compose StorageArrayDTO info
   * 
   * @author shan.jing
   * @param storage_type, String
   * @param username, String
   * @param password, String
   * @param protocol , String
   * @param port , String
   * @param array_ip, String
   * @param data_ip, String
   * @param rmc_ip , String
   * @param mode , String
   * @return
   */
  public HashMap<String, Object> composeStorageArrayDTO(String storage_type, String username,
      String password, String protocol, String port, String array_ip, String data_ip, String rmc_ip,
      String mode) {

    HashMap<String, Object> storageArrayDTOInfo = new HashMap<String, Object>();
    if (storage_type == null || storage_type == "") {
      storageArrayDTOInfo.put("storage_type", storage_type);
    } else if (!storage_type.equalsIgnoreCase("none")) {
      storageArrayDTOInfo.put("storage_type", storage_type);
    }
    if (username == null || username == "") {
      storageArrayDTOInfo.put("username", username);
    } else if (!username.equalsIgnoreCase("none")) {
      storageArrayDTOInfo.put("username", username);
    }
    if (password == null || password == "") {
      storageArrayDTOInfo.put("password", password);
    } else if (!password.equalsIgnoreCase("none")) {
      storageArrayDTOInfo.put("password", password);
    }
    if (protocol == null || protocol == "") {
      storageArrayDTOInfo.put("protocol", protocol);
    } else if (!protocol.equalsIgnoreCase("none")) {
      storageArrayDTOInfo.put("protocol", protocol);
    }
    if (port == null || port == "") {
      storageArrayDTOInfo.put("port", port);
    } else if (!port.equalsIgnoreCase("none")) {
      storageArrayDTOInfo.put("port", port);
    }
    if (array_ip == null || array_ip == "") {
      storageArrayDTOInfo.put("array_ip", array_ip);
    } else if (!array_ip.equalsIgnoreCase("none")) {
      storageArrayDTOInfo.put("array_ip", array_ip);
    }
    if (data_ip == null || data_ip == "") {
      storageArrayDTOInfo.put("data_ip", data_ip);
    } else if (!data_ip.equalsIgnoreCase("none")) {
      storageArrayDTOInfo.put("data_ip", data_ip);
    }
    if (rmc_ip == null || rmc_ip == "") {
      storageArrayDTOInfo.put("rmc_ip", rmc_ip);
    } else if (!rmc_ip.equalsIgnoreCase("none")) {
      storageArrayDTOInfo.put("rmc_ip", rmc_ip);
    }
    if (mode == null || mode == "") {
      storageArrayDTOInfo.put("mode", mode);
    } else if (!mode.equalsIgnoreCase("none")) {
      storageArrayDTOInfo.put("mode", mode);
    }
    return storageArrayDTOInfo;
  }


  /**
   * used to compose UdpAgentlessBackupEmailDTO info
   * 
   * @author shan.jing
   * @param proxy_option, HashMap<String, Object>
   * @param mail_server_name, String
   * @param smp_port , String
   * @param username, String
   * @param password, String
   * @param subject , String
   * @param from , String
   * @param recipients, String
   * @param is_enable_ssl, String
   * @param is_enable_tsl , String
   * @param is_enable_html_dormat , String
   * @param email_send_condition, HashMap<String, Object>
   * @return
   */
  public HashMap<String, Object> composeUdpAgentlessBackupEmailDTO(
      HashMap<String, Object> proxy_option, String mail_server_name, String smp_port,
      String username, String password, String subject, String from, String recipients,
      String is_enable_ssl, String is_enable_tsl, String is_enable_html_dormat,
      HashMap<String, Object> email_send_condition) {

    HashMap<String, Object> udpAgentlessBackupEmailDTOInfo = new HashMap<String, Object>();
    if (proxy_option == null) {
      udpAgentlessBackupEmailDTOInfo.put("proxy_option", proxy_option);
    } else {
      udpAgentlessBackupEmailDTOInfo.put("proxy_option", proxy_option);
    }
    if (mail_server_name == null || mail_server_name == "") {
      udpAgentlessBackupEmailDTOInfo.put("mail_server_name", mail_server_name);
    } else if (!mail_server_name.equalsIgnoreCase("none")) {
      udpAgentlessBackupEmailDTOInfo.put("mail_server_name", mail_server_name);
    }
    if (smp_port == null || smp_port == "") {
      udpAgentlessBackupEmailDTOInfo.put("smp_port", smp_port);
    } else if (!smp_port.equalsIgnoreCase("none")) {
      udpAgentlessBackupEmailDTOInfo.put("smp_port", smp_port);
    }
    if (username == null || username == "") {
      udpAgentlessBackupEmailDTOInfo.put("username", username);
    } else if (!username.equalsIgnoreCase("none")) {
      udpAgentlessBackupEmailDTOInfo.put("username", username);
    }
    if (password == null || password == "") {
      udpAgentlessBackupEmailDTOInfo.put("password", password);
    } else if (!password.equalsIgnoreCase("none")) {
      udpAgentlessBackupEmailDTOInfo.put("password", password);
    }
    if (subject == null || subject == "") {
      udpAgentlessBackupEmailDTOInfo.put("subject", subject);
    } else if (!subject.equalsIgnoreCase("none")) {
      udpAgentlessBackupEmailDTOInfo.put("subject", subject);
    }
    if (from == null || from == "") {
      udpAgentlessBackupEmailDTOInfo.put("from", from);
    } else if (!from.equalsIgnoreCase("none")) {
      udpAgentlessBackupEmailDTOInfo.put("from", from);
    }
    if (recipients == null || recipients == "") {
      udpAgentlessBackupEmailDTOInfo.put("recipients", recipients);
    } else if (!recipients.equalsIgnoreCase("none")) {
      udpAgentlessBackupEmailDTOInfo.put("recipients", recipients);
    }
    if (is_enable_ssl == null || is_enable_ssl == "") {
      udpAgentlessBackupEmailDTOInfo.put("is_enable_ssl", is_enable_ssl);
    } else if (!is_enable_ssl.equalsIgnoreCase("none")) {
      udpAgentlessBackupEmailDTOInfo.put("is_enable_ssl", is_enable_ssl);
    }
    if (is_enable_tsl == null || is_enable_tsl == "") {
      udpAgentlessBackupEmailDTOInfo.put("is_enable_tsl", is_enable_tsl);
    } else if (!is_enable_tsl.equalsIgnoreCase("none")) {
      udpAgentlessBackupEmailDTOInfo.put("is_enable_tsl", is_enable_tsl);
    }
    if (is_enable_html_dormat == null || is_enable_html_dormat == "") {
      udpAgentlessBackupEmailDTOInfo.put("is_enable_html_dormat", is_enable_html_dormat);
    } else if (!is_enable_html_dormat.equalsIgnoreCase("none")) {
      udpAgentlessBackupEmailDTOInfo.put("is_enable_html_dormat", is_enable_html_dormat);
    }
    if (email_send_condition == null) {
      udpAgentlessBackupEmailDTOInfo.put("email_send_condition", email_send_condition);
    } else {
      udpAgentlessBackupEmailDTOInfo.put("email_send_condition", email_send_condition);
    }
    return udpAgentlessBackupEmailDTOInfo;
  }



  /**
   * used to compose RecoverySetScheduleDTO info
   * 
   * @author shan.jing
   * @param first_start_time , String
   * @param is_start_new_set_by_week , String
   * @param day_of_week , String
   * @param day_of_month , String
   * @param is_start_with_first_backup , String
   * @param full_backup , HashMap<String, Object>
   * @param incremental_backup , HashMap<String, Object>
   * @param verify_backup , HashMap<String, Object>
   * @param generate_catalog , String
   * @param recovery_point_check , String
   * @param end_time , String
   * @return
   */
  public HashMap<String, Object> composeRecoverySetScheduleDTO(String first_start_time,
      String is_start_new_set_by_week, String day_of_week, String day_of_month,
      String is_start_with_first_backup, HashMap<String, Object> full_backup,
      HashMap<String, Object> incremental_backup, HashMap<String, Object> verify_backup,
      String generate_catalog, String recovery_point_check, String retain) {

    HashMap<String, Object> recoverySetScheduleDTOInfo = new HashMap<String, Object>();
    if (first_start_time == null || first_start_time == "") {
      recoverySetScheduleDTOInfo.put("first_start_time", first_start_time);
    } else if (!first_start_time.equalsIgnoreCase("none")) {
      recoverySetScheduleDTOInfo.put("first_start_time", first_start_time);
    }
    if (is_start_new_set_by_week == null || is_start_new_set_by_week == "") {
      recoverySetScheduleDTOInfo.put("is_start_new_set_by_week", is_start_new_set_by_week);
    } else if (!is_start_new_set_by_week.equalsIgnoreCase("none")) {
      recoverySetScheduleDTOInfo.put("is_start_new_set_by_week", is_start_new_set_by_week);
    }
    if (day_of_week == null || day_of_week == "") {
      recoverySetScheduleDTOInfo.put("day_of_week", day_of_week);
    } else if (!day_of_week.equalsIgnoreCase("none")) {
      recoverySetScheduleDTOInfo.put("day_of_week", day_of_week);
    }
    if (day_of_month == null || day_of_month == "") {
      recoverySetScheduleDTOInfo.put("day_of_month", day_of_month);
    } else if (!day_of_month.equalsIgnoreCase("none")) {
      recoverySetScheduleDTOInfo.put("day_of_month", day_of_month);
    }
    if (is_start_with_first_backup == null || is_start_with_first_backup == "") {
      recoverySetScheduleDTOInfo.put("is_start_with_first_backup", is_start_with_first_backup);
    } else if (!is_start_with_first_backup.equalsIgnoreCase("none")) {
      recoverySetScheduleDTOInfo.put("is_start_with_first_backup", is_start_with_first_backup);
    }
    if (full_backup == null) {
      recoverySetScheduleDTOInfo.put("full_backup", full_backup);
    } else {
      recoverySetScheduleDTOInfo.put("full_backup", full_backup);
    }
    if (incremental_backup == null) {
      recoverySetScheduleDTOInfo.put("incremental_backup", incremental_backup);
    } else {
      recoverySetScheduleDTOInfo.put("incremental_backup", incremental_backup);
    }
    if (verify_backup == null) {
      recoverySetScheduleDTOInfo.put("verify_backup ", verify_backup);
    } else {
      recoverySetScheduleDTOInfo.put("verify_backup ", verify_backup);
    }
    if (generate_catalog == null || generate_catalog == "") {
      recoverySetScheduleDTOInfo.put("generate_catalog", generate_catalog);
    } else if (!generate_catalog.equalsIgnoreCase("none")) {
      recoverySetScheduleDTOInfo.put("generate_catalog", generate_catalog);
    }
    if (recovery_point_check == null || recovery_point_check == "") {
      recoverySetScheduleDTOInfo.put("recovery_point_check", recovery_point_check);
    } else if (!recovery_point_check.equalsIgnoreCase("none")) {
      recoverySetScheduleDTOInfo.put("recovery_point_check", recovery_point_check);
    }
    if (retain == null || retain == "") {
      recoverySetScheduleDTOInfo.put("retain ", retain);
    } else if (!retain.equalsIgnoreCase("none")) {
      recoverySetScheduleDTOInfo.put("retain ", retain);
    }
    return recoverySetScheduleDTOInfo;
  }



  /**
   * used to compose DailyScheduleDTO info
   * 
   * @author shan.jing
   * @param first_start_time , String
   * @param backup_method , String
   * @param generate_catalog , String
   * @param recovery_point_check , String
   * @param retain , String
   * @param days_of_week , String
   * @return
   */
  public HashMap<String, Object> composeDailyScheduleDTO(String first_start_time,
      String backup_method, String generate_catalog, String recovery_point_check, String retain,
      String days_of_week) {

    HashMap<String, Object> customDailyScheduleDTOInfo = new HashMap<String, Object>();
    if (first_start_time == null || first_start_time == "") {
      customDailyScheduleDTOInfo.put("first_start_time", first_start_time);
    } else if (!first_start_time.equalsIgnoreCase("none")) {
      customDailyScheduleDTOInfo.put("first_start_time", first_start_time);
    }
    if (backup_method == null || backup_method == "") {
      customDailyScheduleDTOInfo.put("backup_method", backup_method);
    } else if (!backup_method.equalsIgnoreCase("none")) {
      customDailyScheduleDTOInfo.put("backup_method", backup_method);
    }
    if (generate_catalog == null || generate_catalog == "") {
      customDailyScheduleDTOInfo.put("generate_catalog", generate_catalog);
    } else if (!generate_catalog.equalsIgnoreCase("none")) {
      customDailyScheduleDTOInfo.put("generate_catalog", generate_catalog);
    }
    if (recovery_point_check == null || recovery_point_check == "") {
      customDailyScheduleDTOInfo.put("recovery_point_check", recovery_point_check);
    } else if (!recovery_point_check.equalsIgnoreCase("none")) {
      customDailyScheduleDTOInfo.put("recovery_point_check", recovery_point_check);
    }
    if (retain == null || retain == "") {
      customDailyScheduleDTOInfo.put("retain", retain);
    } else if (!retain.equalsIgnoreCase("none")) {
      customDailyScheduleDTOInfo.put("retain", retain);
    }
    if (days_of_week == null || days_of_week == "") {
      customDailyScheduleDTOInfo.put("days_of_week", days_of_week);
    } else if (!days_of_week.equalsIgnoreCase("none")) {
      customDailyScheduleDTOInfo.put("days_of_week", days_of_week);
    }
    return customDailyScheduleDTOInfo;
  }


  /**
   * used to compose WeeklyScheduleDTO info
   * 
   * @author shan.jing
   * @param first_start_time , String
   * @param backup_method , String
   * @param generate_catalog , String
   * @param recovery_point_check , String
   * @param retain , String
   * @param days_of_week , String
   * @return
   */
  public HashMap<String, Object> composeWeeklyScheduleDTO(String first_start_time,
      String backup_method, String generate_catalog, String recovery_point_check, String retain,
      String days_of_week) {

    HashMap<String, Object> customWeeklyScheduleDTOInfo = new HashMap<String, Object>();
    if (first_start_time == null || first_start_time == "") {
      customWeeklyScheduleDTOInfo.put("first_start_time", first_start_time);
    } else if (!first_start_time.equalsIgnoreCase("none")) {
      customWeeklyScheduleDTOInfo.put("first_start_time", first_start_time);
    }
    if (backup_method == null || backup_method == "") {
      customWeeklyScheduleDTOInfo.put("backup_method", backup_method);
    } else if (!backup_method.equalsIgnoreCase("none")) {
      customWeeklyScheduleDTOInfo.put("backup_method", backup_method);
    }
    if (generate_catalog == null || generate_catalog == "") {
      customWeeklyScheduleDTOInfo.put("generate_catalog", generate_catalog);
    } else if (!generate_catalog.equalsIgnoreCase("none")) {
      customWeeklyScheduleDTOInfo.put("generate_catalog", generate_catalog);
    }
    if (recovery_point_check == null || recovery_point_check == "") {
      customWeeklyScheduleDTOInfo.put("recovery_point_check", recovery_point_check);
    } else if (!recovery_point_check.equalsIgnoreCase("none")) {
      customWeeklyScheduleDTOInfo.put("recovery_point_check", recovery_point_check);
    }
    if (retain == null || retain == "") {
      customWeeklyScheduleDTOInfo.put("retain", retain);
    } else if (!retain.equalsIgnoreCase("none")) {
      customWeeklyScheduleDTOInfo.put("retain", retain);
    }
    if (days_of_week == null || days_of_week == "") {
      customWeeklyScheduleDTOInfo.put("days_of_week", days_of_week);
    } else if (!days_of_week.equalsIgnoreCase("none")) {
      customWeeklyScheduleDTOInfo.put("days_of_week", days_of_week);
    }
    return customWeeklyScheduleDTOInfo;
  }


  /**
   * used to compose MonthlyScheduleDTO info
   * 
   * @author shan.jing
   * @param first_start_time , String
   * @param backup_method , String
   * @param generate_catalog , String
   * @param recovery_point_check , String
   * @param retain , String
   * @param is_day_of_month_enable , String
   * @param day_of_month , String
   * @param days_of_week , String
   * @param is_first_of_month , String
   * @return
   */
  public HashMap<String, Object> composeMonthlyScheduleDTO(String first_start_time,
      String backup_method, String generate_catalog, String recovery_point_check, String retain,
      String is_day_of_month_enable, String day_of_month, String days_of_week,
      String is_first_of_month) {

    HashMap<String, Object> customMonthlyScheduleDTOInfo = new HashMap<String, Object>();
    if (first_start_time == null || first_start_time == "") {
      customMonthlyScheduleDTOInfo.put("first_start_time", first_start_time);
    } else if (!first_start_time.equalsIgnoreCase("none")) {
      customMonthlyScheduleDTOInfo.put("first_start_time", first_start_time);
    }
    if (backup_method == null || backup_method == "") {
      customMonthlyScheduleDTOInfo.put("backup_method", backup_method);
    } else if (!backup_method.equalsIgnoreCase("none")) {
      customMonthlyScheduleDTOInfo.put("backup_method", backup_method);
    }
    if (generate_catalog == null || generate_catalog == "") {
      customMonthlyScheduleDTOInfo.put("generate_catalog", generate_catalog);
    } else if (!generate_catalog.equalsIgnoreCase("none")) {
      customMonthlyScheduleDTOInfo.put("generate_catalog", generate_catalog);
    }
    if (recovery_point_check == null || recovery_point_check == "") {
      customMonthlyScheduleDTOInfo.put("recovery_point_check", recovery_point_check);
    } else if (!recovery_point_check.equalsIgnoreCase("none")) {
      customMonthlyScheduleDTOInfo.put("recovery_point_check", recovery_point_check);
    }
    if (retain == null || retain == "") {
      customMonthlyScheduleDTOInfo.put("retain", retain);
    } else if (!retain.equalsIgnoreCase("none")) {
      customMonthlyScheduleDTOInfo.put("retain", retain);
    }
    if (is_day_of_month_enable == null || is_day_of_month_enable == "") {
      customMonthlyScheduleDTOInfo.put("is_repeat_enable", is_day_of_month_enable);
    } else if (!is_day_of_month_enable.equalsIgnoreCase("none")) {
      customMonthlyScheduleDTOInfo.put("is_repeat_enable", is_day_of_month_enable);
    }
    if (day_of_month == null || day_of_month == "") {
      customMonthlyScheduleDTOInfo.put("day_of_month", day_of_month);
    } else if (!day_of_month.equalsIgnoreCase("none")) {
      customMonthlyScheduleDTOInfo.put("day_of_month", day_of_month);
    }
    if (days_of_week == null || days_of_week == "") {
      customMonthlyScheduleDTOInfo.put("days_of_week", days_of_week);
    } else if (!days_of_week.equalsIgnoreCase("none")) {
      customMonthlyScheduleDTOInfo.put("days_of_week", days_of_week);
    }
    if (is_first_of_month == null || is_first_of_month == "") {
      customMonthlyScheduleDTOInfo.put("is_first_of_month", is_first_of_month);
    } else if (!is_first_of_month.equalsIgnoreCase("none")) {
      customMonthlyScheduleDTOInfo.put("is_first_of_month", is_first_of_month);
    }
    return customMonthlyScheduleDTOInfo;
  }


  /**
   * used to compose AnnuallyScheduleDTO info
   * 
   * @author shan.jing
   * @param first_start_time , String
   * @param backup_method , String
   * @param generate_catalog , String
   * @param recovery_point_check , String
   * @param retain , String
   * @param is_day_of_month_enable , String
   * @param day_of_month , String
   * @param days_of_week , String
   * @param is_first_of_month , String
   * @param month_of_year , String
   * @return
   */
  public HashMap<String, Object> composeAnnuallyScheduleDTO(String first_start_time,
      String backup_method, String generate_catalog, String recovery_point_check, String retain,
      String is_day_of_month_enable, String day_of_month, String days_of_week,
      String is_first_of_month, String month_of_year) {

    HashMap<String, Object> customAnnuallyScheduleDTOInfo = new HashMap<String, Object>();
    if (first_start_time == null || first_start_time == "") {
      customAnnuallyScheduleDTOInfo.put("first_start_time", first_start_time);
    } else if (!first_start_time.equalsIgnoreCase("none")) {
      customAnnuallyScheduleDTOInfo.put("first_start_time", first_start_time);
    }
    if (backup_method == null || backup_method == "") {
      customAnnuallyScheduleDTOInfo.put("backup_method", backup_method);
    } else if (!backup_method.equalsIgnoreCase("none")) {
      customAnnuallyScheduleDTOInfo.put("backup_method", backup_method);
    }
    if (generate_catalog == null || generate_catalog == "") {
      customAnnuallyScheduleDTOInfo.put("generate_catalog", generate_catalog);
    } else if (!generate_catalog.equalsIgnoreCase("none")) {
      customAnnuallyScheduleDTOInfo.put("generate_catalog", generate_catalog);
    }
    if (recovery_point_check == null || recovery_point_check == "") {
      customAnnuallyScheduleDTOInfo.put("recovery_point_check", recovery_point_check);
    } else if (!recovery_point_check.equalsIgnoreCase("none")) {
      customAnnuallyScheduleDTOInfo.put("recovery_point_check", recovery_point_check);
    }
    if (retain == null || retain == "") {
      customAnnuallyScheduleDTOInfo.put("retain", retain);
    } else if (!retain.equalsIgnoreCase("none")) {
      customAnnuallyScheduleDTOInfo.put("retain", retain);
    }
    if (is_day_of_month_enable == null || is_day_of_month_enable == "") {
      customAnnuallyScheduleDTOInfo.put("is_repeat_enable", is_day_of_month_enable);
    } else if (!is_day_of_month_enable.equalsIgnoreCase("none")) {
      customAnnuallyScheduleDTOInfo.put("is_repeat_enable", is_day_of_month_enable);
    }
    if (day_of_month == null || day_of_month == "") {
      customAnnuallyScheduleDTOInfo.put("day_of_month", day_of_month);
    } else if (!day_of_month.equalsIgnoreCase("none")) {
      customAnnuallyScheduleDTOInfo.put("day_of_month", day_of_month);
    }
    if (days_of_week == null || days_of_week == "") {
      customAnnuallyScheduleDTOInfo.put("days_of_week", days_of_week);
    } else if (!days_of_week.equalsIgnoreCase("none")) {
      customAnnuallyScheduleDTOInfo.put("days_of_week", days_of_week);
    }
    if (is_first_of_month == null || is_first_of_month == "") {
      customAnnuallyScheduleDTOInfo.put("is_first_of_month", is_first_of_month);
    } else if (!is_first_of_month.equalsIgnoreCase("none")) {
      customAnnuallyScheduleDTOInfo.put("is_first_of_month", is_first_of_month);
    }
    if (month_of_year == null || month_of_year == "") {
      customAnnuallyScheduleDTOInfo.put("month_of_year", month_of_year);
    } else if (!month_of_year.equalsIgnoreCase("none")) {
      customAnnuallyScheduleDTOInfo.put("month_of_year", month_of_year);
    }
    return customAnnuallyScheduleDTOInfo;
  }


  /**
   * used to compose AnnuallyScheduleDTO info
   * 
   * @author shan.jing
   * @param is_run_immediately , String
   * @return
   */
  public HashMap<String, Object> composeImmediatelySchduleDTO(String is_run_immediately) {

    HashMap<String, Object> ImmediatelySchduleDTO = new HashMap<String, Object>();
    if (is_run_immediately == null || is_run_immediately == "") {
      ImmediatelySchduleDTO.put("is_run_immediately", is_run_immediately);
    } else if (!is_run_immediately.equalsIgnoreCase("none")) {
      ImmediatelySchduleDTO.put("is_run_immediately", is_run_immediately);
    }
    return ImmediatelySchduleDTO;
  }



  /**
   * used to compose AgentBaseBackupEmailSendCondition info
   * 
   * @author shan.jing
   * @param job_condition, HashMap<String, Object>
   * @param resource_alert_value , HashMap<String, Object>
   * @return
   */
  public HashMap<String, Object> composeAgentBaseBackupEmailSendCondition(
      HashMap<String, Object> job_condition, HashMap<String, Object> resource_alert_value) {

    HashMap<String, Object> AgentBaseBackupEmailSendConditionInfo = new HashMap<String, Object>();
    if (job_condition == null) {
      AgentBaseBackupEmailSendConditionInfo.put("job_condition", job_condition);
    } else {
      AgentBaseBackupEmailSendConditionInfo.put("job_condition", job_condition);
    }
    if (resource_alert_value == null) {
      AgentBaseBackupEmailSendConditionInfo.put("resource_alert_value", resource_alert_value);
    } else {
      AgentBaseBackupEmailSendConditionInfo.put("resource_alert_value", resource_alert_value);
    }
    return AgentBaseBackupEmailSendConditionInfo;
  }


  /**
   * used to compose AgentlessBackupEmailSendCondition info
   * 
   * @author shan.jing
   * @param job_condition, HashMap<String, Object>
   * @return
   */
  public HashMap<String, Object> composeAgentlessBackupEmailSendCondition(
      HashMap<String, Object> job_condition) {

    HashMap<String, Object> AgentlessBackupEmailSendConditionInfo = new HashMap<String, Object>();
    if (job_condition == null) {
      AgentlessBackupEmailSendConditionInfo.put("job_condition", job_condition);
    } else {
      AgentlessBackupEmailSendConditionInfo.put("job_condition", job_condition);
    }
    return AgentlessBackupEmailSendConditionInfo;
  }


  /**
   * used to compose RecoverySetScheduleDetailDTO info
   * 
   * @author shan.jing
   * @param interval , String
   * @param interval_unit , String
   * @return
   */
  public HashMap<String, Object> composeRecoverySetScheduleDetailDTO(String interval,
      String interval_unit) {

    HashMap<String, Object> customRecoverySetScheduleDetailDTOInfo = new HashMap<String, Object>();

    if (interval == null || interval == "") {
      customRecoverySetScheduleDetailDTOInfo.put("interval", interval);
    } else if (!interval.equalsIgnoreCase("none")) {
      customRecoverySetScheduleDetailDTOInfo.put("interval", interval);
    }
    if (interval_unit == null || interval_unit == "") {
      customRecoverySetScheduleDetailDTOInfo.put("interval_unit", interval_unit);
    } else if (!interval_unit.equalsIgnoreCase("none")) {
      customRecoverySetScheduleDetailDTOInfo.put("interval_unit", interval_unit);
    }
    return customRecoverySetScheduleDetailDTOInfo;
  }


  /**
   * used to compose JobConditionDTO info
   * 
   * @author shan.jing
   * @param on_success , String
   * @param on_failed , String
   * @param on_missed , String
   * @param on_merge_failed , String
   * @param on_merge_successed , String
   * @return
   */
  public HashMap<String, Object> composeJobConditionDTO(String on_success, String on_failed,
      String on_missed, String on_merge_failed, String on_merge_successed) {

    HashMap<String, Object> customJobConditionDTOInfo = new HashMap<String, Object>();
    if (on_success == null || on_success == "") {
      customJobConditionDTOInfo.put("on_job_missed", on_success);
    } else if (!on_success.equalsIgnoreCase("none")) {
      customJobConditionDTOInfo.put("on_job_missed", on_success);
    }
    if (on_failed == null || on_failed == "") {
      customJobConditionDTOInfo.put("on_replicate_successed", on_failed);
    } else if (!on_failed.equalsIgnoreCase("none")) {
      customJobConditionDTOInfo.put("on_replicate_successed", on_failed);
    }
    if (on_missed == null || on_missed == "") {
      customJobConditionDTOInfo.put("on_replicate_failed", on_missed);
    } else if (!on_missed.equalsIgnoreCase("none")) {
      customJobConditionDTOInfo.put("on_replicate_failed", on_missed);
    }
    if (on_merge_failed == null || on_merge_failed == "") {
      customJobConditionDTOInfo.put("on_merge_failed", on_merge_failed);
    } else if (!on_merge_failed.equalsIgnoreCase("none")) {
      customJobConditionDTOInfo.put("on_merge_failed", on_merge_failed);
    }
    if (on_merge_successed == null || on_merge_successed == "") {
      customJobConditionDTOInfo.put("on_merge_successed", on_merge_successed);
    } else if (!on_merge_successed.equalsIgnoreCase("none")) {
      customJobConditionDTOInfo.put("on_merge_successed", on_merge_successed);
    }
    return customJobConditionDTOInfo;
  }


  /**
   * used to compose ResourceAlertValueDTO info
   * 
   * @author shan.jing
   * @param cpu_threshold , String
   * @param memory_threshold , String
   * @param disk_threshold , String
   * @param network_threshold , String
   * @return
   */
  public HashMap<String, Object> composeResourceAlertValueDTO(String cpu_threshold,
      String memory_threshold, String disk_threshold, String network_threshold) {

    HashMap<String, Object> customResourceAlertValueDTOInfo = new HashMap<String, Object>();
    if (cpu_threshold == null || cpu_threshold == "") {
      customResourceAlertValueDTOInfo.put("cpu_threshold", cpu_threshold);
    } else if (!cpu_threshold.equalsIgnoreCase("none")) {
      customResourceAlertValueDTOInfo.put("cpu_threshold", cpu_threshold);
    }
    if (memory_threshold == null || memory_threshold == "") {
      customResourceAlertValueDTOInfo.put("memory_threshold", memory_threshold);
    } else if (!memory_threshold.equalsIgnoreCase("none")) {
      customResourceAlertValueDTOInfo.put("memory_threshold", memory_threshold);
    }
    if (disk_threshold == null || disk_threshold == "") {
      customResourceAlertValueDTOInfo.put("disk_threshold", disk_threshold);
    } else if (!disk_threshold.equalsIgnoreCase("none")) {
      customResourceAlertValueDTOInfo.put("disk_threshold", disk_threshold);
    }
    if (network_threshold == null || network_threshold == "") {
      customResourceAlertValueDTOInfo.put("network_threshold", network_threshold);
    } else if (!network_threshold.equalsIgnoreCase("none")) {
      customResourceAlertValueDTOInfo.put("network_threshold", network_threshold);
    }
    return customResourceAlertValueDTOInfo;
  }


  /**
   * used to compose AgentlessJobConditionDTO info
   * 
   * @author shan.jing
   * @param on_success , String
   * @param on_failed , String
   * @param on_missed , String
   * @param on_merge_failed , String
   * @param on_merge_successed , String
   * @param on_recovery_point_check_failed , String
   * @return
   */
  public HashMap<String, Object> composeAgentlessJobConditionDTO(String on_success,
      String on_failed, String on_missed, String on_merge_failed, String on_merge_successed,
      String on_recovery_point_check_failed) {

    HashMap<String, Object> customAgentlessJobConditionDTOInfo = new HashMap<String, Object>();
    if (on_success == null || on_success == "") {
      customAgentlessJobConditionDTOInfo.put("on_success", on_success);
    } else if (!on_success.equalsIgnoreCase("none")) {
      customAgentlessJobConditionDTOInfo.put("on_success", on_success);
    }
    if (on_failed == null || on_failed == "") {
      customAgentlessJobConditionDTOInfo.put("on_failed", on_failed);
    } else if (!on_failed.equalsIgnoreCase("none")) {
      customAgentlessJobConditionDTOInfo.put("on_failed", on_failed);
    }
    if (on_missed == null || on_missed == "") {
      customAgentlessJobConditionDTOInfo.put("on_missed", on_missed);
    } else if (!on_missed.equalsIgnoreCase("none")) {
      customAgentlessJobConditionDTOInfo.put("on_missed", on_missed);
    }
    if (on_merge_failed == null || on_merge_failed == "") {
      customAgentlessJobConditionDTOInfo.put("on_merge_failed", on_merge_failed);
    } else if (!on_merge_failed.equalsIgnoreCase("none")) {
      customAgentlessJobConditionDTOInfo.put("on_merge_failed", on_merge_failed);
    }
    if (on_merge_successed == null || on_merge_successed == "") {
      customAgentlessJobConditionDTOInfo.put("on_merge_successed", on_merge_successed);
    } else if (!on_merge_successed.equalsIgnoreCase("none")) {
      customAgentlessJobConditionDTOInfo.put("on_merge_successed", on_merge_successed);
    }
    if (on_recovery_point_check_failed == null || on_recovery_point_check_failed == "") {
      customAgentlessJobConditionDTOInfo.put("on_recovery_point_check_failed",
          on_recovery_point_check_failed);
    } else if (!on_recovery_point_check_failed.equalsIgnoreCase("none")) {
      customAgentlessJobConditionDTOInfo.put("on_recovery_point_check_failed",
          on_recovery_point_check_failed);
    }
    return customAgentlessJobConditionDTOInfo;
  }
  
  /**
	 * used to compose CloudDirectImageBackupTaskInfoDTO info
	 * @author Kiran.Sripada
	 * @param drives, Array[String]
	 * @param local_backup, HashMap<String, Object>
	 * @return
	 */	
	public HashMap<String, Object> composeCloudDirectImageBackupTaskInfoDTO (
								ArrayList<String> drives,
								HashMap<String, Object> local_backup
								){
		HashMap<String, Object> cloudDirectImageBackupTaskInfoDTOInfo = new HashMap<String, Object>();
		cloudDirectImageBackupTaskInfoDTOInfo.put("drives", drives);
		if(local_backup==null ){
			cloudDirectImageBackupTaskInfoDTOInfo.put("local_backup", local_backup);
		}else{
			cloudDirectImageBackupTaskInfoDTOInfo.put("local_backup", local_backup);
		}
		return cloudDirectImageBackupTaskInfoDTOInfo;
	}
	
	/**
	 * used to compose CloudDirectImageBackupTaskInfoDTO info
	 * @author Rakesh.Chalamala
	 * @param drives, Array[String]
	 * @param local_backup, HashMap<String, Object>
	 * @return
	 */	
	public HashMap<String, Object> composeCloudDirectSQLServerBackupTaskInfoDTO(String sql_backup_type,
																				String sql_backup_default,
																				ArrayList<String> sql_backup_local_instance,
																				String sql_backup_path,
																				String sql_verify_enabled,
																				HashMap<String, Object> local_backup
																				){
		HashMap<String, Object> cloudDirectSQLServerBackupTaskInfoDTO = new HashMap<String, Object>();
		
		cloudDirectSQLServerBackupTaskInfoDTO.put("sql_backup_type", sql_backup_type);
		cloudDirectSQLServerBackupTaskInfoDTO.put("sql_backup_default", sql_backup_default);
		cloudDirectSQLServerBackupTaskInfoDTO.put("sql_backup_local_instance", sql_backup_local_instance);
		cloudDirectSQLServerBackupTaskInfoDTO.put("sql_backup_path", sql_backup_path);
		cloudDirectSQLServerBackupTaskInfoDTO.put("sql_verify_enabled", sql_verify_enabled);
		cloudDirectSQLServerBackupTaskInfoDTO.put("local_backup", local_backup);
		
		return cloudDirectSQLServerBackupTaskInfoDTO;
	}
	
	/**
	 * used to compose sources for assign policy
	 * @author shan,jing
	 * @param sources
	 * @return
	 */	
	public HashMap<String, Object> composeSourcesForAssignOrUnassignPolicy (
								String sources
								){
		HashMap<String, Object> sourcesForAssignPolicyInfo = new HashMap<String, Object>();
		String[] emptyArray = new String[0];
	    if (sources == null || sources == "") {
	    	sourcesForAssignPolicyInfo.put("sources", sources);
	    } else if (sources.equalsIgnoreCase("emptyarray")) {
	    	sourcesForAssignPolicyInfo.put("sources", emptyArray);
	    } else if (!sources.equalsIgnoreCase("none")) {
	    	sourcesForAssignPolicyInfo.put("sources", sources.replace(" ", "").split(","));
	    }
		return sourcesForAssignPolicyInfo;
	}

	  /**
	   * used to compose ScheduleSettingDTO info
	   * 
	   * @param cloud_direct_schedule, HashMap<String, Object>
	   * @param custom_schedule , HashMap<String, Object>
	   * @return
	   */
	  public HashMap<String, Object> composeScheduleSettingDTO(
	      HashMap<String, Object> cloud_direct_schedule, HashMap<String, Object> custom_schedule, HashMap<String, Object> ar_schedule, HashMap<String, Object> merge_schedule) {

	    HashMap<String, Object> scheduleSettingDTOInfo = new HashMap<String, Object>();
	    if (cloud_direct_schedule == null) {
	      scheduleSettingDTOInfo.put("cloud_direct_schedule", cloud_direct_schedule);
	    } else {
	      scheduleSettingDTOInfo.put("cloud_direct_schedule", cloud_direct_schedule);
	    }
	    if (custom_schedule == null) {
	      scheduleSettingDTOInfo.put("custom_schedule", custom_schedule);
	    } else {
	      scheduleSettingDTOInfo.put("custom_schedule", custom_schedule);
	    }
	    if (ar_schedule == null) {
	        scheduleSettingDTOInfo.put("ar_schedule", ar_schedule);
	      } else {
	        scheduleSettingDTOInfo.put("ar_schedule", ar_schedule);
	      }
	      if (custom_schedule == null) {
	        scheduleSettingDTOInfo.put("merge_schedule", merge_schedule);
	      } else {
	        scheduleSettingDTOInfo.put("merge_schedule", merge_schedule);
	      }
	    return scheduleSettingDTOInfo;
	  }

	  /**
	   * used to compose ScheduleSettingDTO info
	   * 
	   * @param cloud_direct_schedule, HashMap<String, Object>
	   * @param custom_schedule , HashMap<String, Object>
	   * @return
	   */
	  public HashMap<String, Object> composeScheduleSettingDTO(
	      HashMap<String, Object> cloud_direct_schedule, HashMap<String, Object> custom_schedule, HashMap<String, Object> ar_schedule, HashMap<String, Object> merge_schedule,HashMap<String, Object> replication_schedule) {

	    HashMap<String, Object> scheduleSettingDTOInfo = new HashMap<String, Object>();
	    if (cloud_direct_schedule == null) {
	      scheduleSettingDTOInfo.put("cloud_direct_schedule", cloud_direct_schedule);
	    } else {
	      scheduleSettingDTOInfo.put("cloud_direct_schedule", cloud_direct_schedule);
	    }
	    if (custom_schedule == null) {
	      scheduleSettingDTOInfo.put("custom_schedule", custom_schedule);
	    } else {
	      scheduleSettingDTOInfo.put("custom_schedule", custom_schedule);
	    }
	    if (ar_schedule == null) {
	        scheduleSettingDTOInfo.put("ar_schedule", ar_schedule);
	      } else {
	        scheduleSettingDTOInfo.put("ar_schedule", ar_schedule);
	      }
	      if (merge_schedule == null) {
	        scheduleSettingDTOInfo.put("merge_schedule", merge_schedule);
	      } else {
	        scheduleSettingDTOInfo.put("merge_schedule", merge_schedule);
	      }
	      if (replication_schedule == null) {
	          scheduleSettingDTOInfo.put("replication_schedule", replication_schedule);
	        } else {
	          scheduleSettingDTOInfo.put("replication_schedule", replication_schedule);
	        }
	    return scheduleSettingDTOInfo;
	  }

	  /**
	   * used to compose PolicyTaskDTO info
	   * 
	   * @author Ramya.Nagepalli
	   * @param task_id, String
	   * @param task_type, String
	   * @param destination_id, String
	   * @param parent_id, String
	   * @param cloud_direct_image_backup, HashMap<String, Object>
	   * @param cloud_direct_file_backup, HashMap<String, Object>
	   * @param udp_replication_from_remote, HashMap<String, Object>
	   * @param udp_replication_to_remote, HashMap<String, Object>
	   * @param cloud_direct_sql_server_backup, HashMap<String, Object>
	   * @param cloud_direct_vmware_backup, HashMap<String, Object>
	   * @return
	   */
	  public HashMap<String, Object> composePolicyTaskDTO(String destination_name,String task_id, String task_type,
	      String destination_id, String parent_id, HashMap<String, Object> cloud_direct_image_backup,
	      HashMap<String, Object> cloud_direct_file_backup,
	      HashMap<String, Object> udp_replication_from_remote,
	      HashMap<String, Object> udp_replication_to_remote, 
	      HashMap<String, Object> cloud_direct_sql_server_backup,
	      HashMap<String, Object> cloud_direct_vmware_backup,String destination_type) {

	    HashMap<String, Object> cloudPolicyTaskDTOInfo = new HashMap<String, Object>();
	    if (task_id == null || task_id == "") {
	      cloudPolicyTaskDTOInfo.put("task_id", task_id);
	    } else if (!task_id.equalsIgnoreCase("none")) {
	      cloudPolicyTaskDTOInfo.put("task_id", task_id);
	    }
	    if (task_type == null || task_type == "") {
	      cloudPolicyTaskDTOInfo.put("task_type", task_type);
	    } else if (!task_type.equalsIgnoreCase("none")) {
	      cloudPolicyTaskDTOInfo.put("task_type", task_type);
	    }
	    if (destination_id == null || destination_id == "") {
	      cloudPolicyTaskDTOInfo.put("destination_id", destination_id);
	    } else if (!destination_id.equalsIgnoreCase("none")) {
	      cloudPolicyTaskDTOInfo.put("destination_id", destination_id);
	    }
	 
	      cloudPolicyTaskDTOInfo.put("parent_id", parent_id);
	    
	    if (cloud_direct_image_backup == null) {
	      cloudPolicyTaskDTOInfo.put("cloud_direct_image_backup", cloud_direct_image_backup);
	    } else {
	      cloudPolicyTaskDTOInfo.put("cloud_direct_image_backup", cloud_direct_image_backup);
	    }
	    if (cloud_direct_file_backup == null) {
	      cloudPolicyTaskDTOInfo.put("cloud_direct_file_backup", cloud_direct_file_backup);
	    } else {
	      cloudPolicyTaskDTOInfo.put("cloud_direct_file_backup", cloud_direct_file_backup);
	    }
	    if (udp_replication_from_remote == null) {
	      cloudPolicyTaskDTOInfo.put("udp_replication_from_remote", udp_replication_from_remote);
	    } else {
	      cloudPolicyTaskDTOInfo.put("udp_replication_from_remote", udp_replication_from_remote);
	    }
	    if (udp_replication_to_remote == null) {
	        cloudPolicyTaskDTOInfo.put("udp_replication_to_remote", udp_replication_to_remote);
	      } else {
	        cloudPolicyTaskDTOInfo.put("udp_replication_to_remote", udp_replication_to_remote);
	      }

		cloudPolicyTaskDTOInfo.put("cloud_direct_sql_server_backup", cloud_direct_sql_server_backup);

		if(cloud_direct_vmware_backup==null)
		{
			Map<String, Object> compose_vmware_backup=new HashMap<String, Object>();
			/*compose_vmware_backup.put("cloud_direct_vmware_backup", "");*/
		cloudPolicyTaskDTOInfo.put("cloud_direct_vmware_backup", compose_vmware_backup);
		}
		cloudPolicyTaskDTOInfo.put("destination_name", destination_name);
		cloudPolicyTaskDTOInfo.put("destination_type", destination_type);
	    return cloudPolicyTaskDTOInfo;
	  }


	  /**
	   * used to compose PolicyCreateRequestDTO info
	   * 
	   * @author Ramya.Nagepalli
	   * @param policy_name, String[]
	   * @param policy_description, String
	   * @param policy_type , String
	   * @param sla, HashMap<String, Object>
	   * @param is_traft, String
	   * @param throttle Array[PolicyThrottleDTO]
	   * @param destinations Array[PolicyTaskDTO]
	   * @param sources, Array[string]
	   * @param schedules Array[PolicyScheduleDTO]
	   * @param policy_id, String
	   * @param organization_id String
	   * @return
	   */
	  public Map<String, Object> composePolicyCreateRequestDTO(String policy_name,
	      String policy_description, String policy_type, Boolean is_draft,
	      ArrayList<HashMap<String, Object>> throttle, String sources,
	      ArrayList<HashMap<String, Object>> destinations, ArrayList<HashMap<String, Object>> schedules,
	      String policy_id, String organization_id,String hypervisor_id) {

	    Map<String, Object> policyCreateRequestInfo = new HashMap<String, Object>();
	    String[] emptyArray = new String[0];
	    if (policy_name == null || policy_name == "") {
	      policyCreateRequestInfo.put("policy_name", policy_name);
	    } else if (!policy_name.equalsIgnoreCase("none")) {
	      policyCreateRequestInfo.put("policy_name", policy_name);
	    }
	    if (policy_description == null || policy_description == "") {
	      policyCreateRequestInfo.put("policy_description", policy_description);
	    } else if (!policy_description.equalsIgnoreCase("none")) {
	      policyCreateRequestInfo.put("policy_description", policy_description);
	    }
	    if (policy_type == null || policy_type == "") {
	      policyCreateRequestInfo.put("policy_type", policy_type);
	    } else if (!policy_type.equalsIgnoreCase("none")) {
	      policyCreateRequestInfo.put("policy_type", policy_type);
	    }
	    policyCreateRequestInfo.put("is_draft", is_draft);
	    
	    policyCreateRequestInfo.put("throttles", throttle);
	    if (sources == null || sources == "") {
	        policyCreateRequestInfo.put("sources", sources);
	      } else if (sources.equalsIgnoreCase("emptyarray")) {
	        policyCreateRequestInfo.put("sources", emptyArray);
	      } else if (!sources.equalsIgnoreCase("none")) {
	        
	        ArrayList<HashMap<String, Object>> allSourcesInfo = new ArrayList<>();
	        HashMap<String, Object> sourceInfo = null;
	        String[] src = sources.replace(" ", "").split(",");
	        
	        for (int i = 0; i < src.length; i++) {
	              sourceInfo = new HashMap<>();
	              sourceInfo.put("source_id", src[i]);
	              allSourcesInfo.add(sourceInfo);
	        }
	        
	        policyCreateRequestInfo.put("sources", allSourcesInfo);
	  }

	    policyCreateRequestInfo.put("schedules", schedules);
	    policyCreateRequestInfo.put("destinations", destinations);
	    if (policy_id == null || policy_id == "") {
	      policyCreateRequestInfo.put("policy_id", policy_id);
	    } else if (!policy_id.equalsIgnoreCase("none")) {
	      policyCreateRequestInfo.put("policy_id", policy_id);
	    }
	    if (organization_id == null || organization_id == "") {
	      policyCreateRequestInfo.put("organization_id", organization_id);
	    } else if (!organization_id.equalsIgnoreCase("none")) {
	      policyCreateRequestInfo.put("organization_id", organization_id);
	    }
	    policyCreateRequestInfo.put("hypervisor_id", hypervisor_id);
	    
	    return policyCreateRequestInfo;
	  }

	  /**
	   * used to compose PolicyCreateRequestDTO info
	   * 
	   * @author Ramya.Nagepalli
	   * @param policy_name, String[]
	   * @param policy_description, String
	   * @param policy_type , String
	   * @param sla, HashMap<String, Object>
	   * @param is_traft, String
	   * @param throttle Array[PolicyThrottleDTO]
	   * @param destinations Array[PolicyTaskDTO]
	   * @param sources, Array[string]
	   * @param schedules Array[PolicyScheduleDTO]
	   * @param policy_id, String
	   * @param organization_id String
	   * @return
	   */
	  public Map<String, Object> composePolicyCreateRequestDTO(String policy_name,
	      String policy_description, String policy_type, String is_draft,
	      ArrayList<HashMap<String, Object>> throttle, String sources,
	      ArrayList<HashMap<String, Object>> destinations, ArrayList<HashMap<String, Object>> schedules,
	      String policy_id, String organization_id,String hypervisor_id) {

	    Map<String, Object> policyCreateRequestInfo = new HashMap<String, Object>();
	    String[] emptyArray = new String[0];
	    if (policy_name == null || policy_name == "") {
	      policyCreateRequestInfo.put("policy_name", policy_name);
	    } else if (!policy_name.equalsIgnoreCase("none")) {
	      policyCreateRequestInfo.put("policy_name", policy_name);
	    }
	    if (policy_description == null || policy_description == "") {
	      policyCreateRequestInfo.put("policy_description", policy_description);
	    } else if (!policy_description.equalsIgnoreCase("none")) {
	      policyCreateRequestInfo.put("policy_description", policy_description);
	    }
	    if (policy_type == null || policy_type == "") {
	      policyCreateRequestInfo.put("policy_type", policy_type);
	    } else if (!policy_type.equalsIgnoreCase("none")) {
	      policyCreateRequestInfo.put("policy_type", policy_type);
	    }
	    if (is_draft == null || is_draft == "") {
	      policyCreateRequestInfo.put("is_draft", is_draft);
	    } else if (!is_draft.equalsIgnoreCase("none")) {
	      policyCreateRequestInfo.put("is_draft", is_draft);
	    }
	    policyCreateRequestInfo.put("throttles", throttle);
	    if (sources == null || sources == "") {
	        policyCreateRequestInfo.put("sources", sources);
	      } else if (sources.equalsIgnoreCase("emptyarray")) {
	        policyCreateRequestInfo.put("sources", emptyArray);
	      } else if (!sources.equalsIgnoreCase("none")) {
	        
	        ArrayList<HashMap<String, Object>> allSourcesInfo = new ArrayList<>();
	        HashMap<String, Object> sourceInfo = null;
	        String[] src = sources.replace(" ", "").split(",");
	        
	        for (int i = 0; i < src.length; i++) {
	              sourceInfo = new HashMap<>();
	              sourceInfo.put("source_id", src[i]);
	              allSourcesInfo.add(sourceInfo);
	        }
	        
	        policyCreateRequestInfo.put("sources", allSourcesInfo);
	  }

	    policyCreateRequestInfo.put("schedules", schedules);
	    policyCreateRequestInfo.put("destinations", destinations);
	    if (policy_id == null || policy_id == "") {
	      policyCreateRequestInfo.put("policy_id", policy_id);
	    } else if (!policy_id.equalsIgnoreCase("none")) {
	      policyCreateRequestInfo.put("policy_id", policy_id);
	    }
	    if (organization_id == null || organization_id == "") {
	      policyCreateRequestInfo.put("organization_id", organization_id);
	    } else if (!organization_id.equalsIgnoreCase("none")) {
	      policyCreateRequestInfo.put("organization_id", organization_id);
	    }
	    policyCreateRequestInfo.put("hypervisor_id", hypervisor_id);
	    
	    return policyCreateRequestInfo;
	  }

}
