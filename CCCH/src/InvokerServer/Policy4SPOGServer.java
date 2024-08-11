package InvokerServer;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.AssertJUnit.assertTrue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.testng.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ErrorCode;
import Constants.PolicyColumnConstants;
import Constants.SourceColumnConstants;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import dataPreparation.JsonPreparation;
import dataPreparation.policyPreparation;
import genericutil.ErrorHandler;
import invoker.Policy4SPOGInvoker;
import invoker.Source4SPOGInvoker;
import io.restassured.response.Response;

public class Policy4SPOGServer {
	private Policy4SPOGInvoker policy4SPOGInvoker;
	private GatewayServer gatewayServer;
	private SPOGServer spogServer;
	private static policyPreparation pp = new policyPreparation();
	static ErrorHandler errorHandle = ErrorHandler.getErrorHandler();
	private static JsonPreparation jp = new JsonPreparation();
	private Source4SPOGServer source4SpogServer;

	public Policy4SPOGServer(String baseURI, String port) {
		policy4SPOGInvoker = new Policy4SPOGInvoker(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
		source4SpogServer = new Source4SPOGServer(baseURI, port);
	}

	public void setToken(String token) {

		policy4SPOGInvoker.setToken(token);
		spogServer.setToken(token);
		source4SpogServer.setToken(token);
	}

	/**
	 * create CloudDirectScheduleDTO
	 * 
	 * @author shan.jing
	 * @param crontab_string
	 * @param test
	 * @return cloudDirectScheduleDTO
	 */
	public HashMap<String, Object> createCloudDirectScheduleDTO(String crontab_string, ExtentTest test) {

		HashMap<String, Object> cloudDirectScheduleDTO = null;
		cloudDirectScheduleDTO = pp.composeCloudDirectScheduleDTO(crontab_string);
		return cloudDirectScheduleDTO;
	}

	/**
	 * create CustomScheduleDTO
	 * 
	 * @author shan.jing
	 * @param first_start_time
	 * @param backup_method
	 * @param days_of_week
	 * @param is_repeat_enable
	 * @param interval
	 * @param interval_unit
	 * @param test
	 * @return policyScheduleDTO
	 */
	public HashMap<String, Object> createCustomScheduleDTO(String first_start_time, String backup_method,
			String days_of_week, String is_repeat_enable, String interval, String interval_unit, ExtentTest test) {

		HashMap<String, Object> customScheduleDTO = null;
		customScheduleDTO = pp.composeCustomScheduleDTO(first_start_time, backup_method, days_of_week, is_repeat_enable,
				interval, interval_unit);
		return customScheduleDTO;
	}

	/**
	 * create ScheduleSettingDTO
	 * 
	 * @author shan.jing
	 * @param cloud_direct_schedule
	 * @param custom_schedule
	 * @param test
	 * @return scheduleSettingDTO
	 */
	public HashMap<String, Object> createScheduleSettingDTO(HashMap<String, Object> cloud_direct_schedule,
			HashMap<String, Object> custom_schedule, ExtentTest test) {

		HashMap<String, Object> scheduleSettingDTO = null;
		scheduleSettingDTO = pp.composeScheduleSettingDTO(cloud_direct_schedule, custom_schedule);
		return scheduleSettingDTO;
	}

	/**
	 * create PolicyScheduleDTO
	 * 
	 * @author shan.jing
	 * @param schedule_id
	 * @param schedule_type
	 * @param task_id
	 * @param destination_id
	 * @param schedule
	 * @param start_time
	 * @param end_time
	 * @param test
	 * @return policyScheduleDTO
	 */
	public ArrayList<HashMap<String, Object>> createPolicyScheduleDTO(ArrayList<HashMap<String, Object>> schedules,
			String schedule_id, String schedule_type, String task_id, String destination_id,
			HashMap<String, Object> schedule, String start_time, String end_time, ExtentTest test) {

		HashMap<String, Object> policyScheduleDTO = null;
		policyScheduleDTO = pp.composePolicyScheduleDTO(schedule_id, schedule_type, task_id, destination_id, schedule,
				start_time, end_time);
		if (schedules == null) {
			schedules = new ArrayList<HashMap<String, Object>>();
		}
		schedules.add(policyScheduleDTO);
		return schedules;
	}

	/**
	 * create PolicyScheduleDTO
	 * 
	 * @author shan.jing
	 * @param schedule_id
	 * @param schedule_type
	 * @param task_id
	 * @param destination_id
	 * @param schedule
	 * @param start_time
	 * @param end_time
	 * @param task_type
	 * @param destination_name
	 * @param test
	 * @return policyScheduleDTO
	 */
	public ArrayList<HashMap<String, Object>> createPolicyScheduleDTO(ArrayList<HashMap<String, Object>> schedules,
			String schedule_id, String schedule_type, String task_id, String destination_id,
			HashMap<String, Object> schedule, String start_time, String end_time, String task_type,
			String destination_name, ExtentTest test) {

		HashMap<String, Object> policyScheduleDTO = null;
		policyScheduleDTO = pp.composePolicyScheduleDTO(schedule_id, schedule_type, task_id, destination_id, schedule,
				start_time, end_time);
		if (schedules == null) {
			schedules = new ArrayList<HashMap<String, Object>>();
		}
		schedules.add(policyScheduleDTO);
		return schedules;
	}

	/**
	 * create ScheduleSettingDTO
	 * 
	 * @param cloud_direct_schedule
	 * @param custom_schedule
	 * @param ar_schedule
	 * @param merge_schedule
	 * @param test
	 * @return scheduleSettingDTO
	 */
	public HashMap<String, Object> createScheduleSettingDTO(HashMap<String, Object> cloud_direct_schedule,
			HashMap<String, Object> custom_schedule, HashMap<String, Object> ar_schedule,
			HashMap<String, Object> merge_schedule, ExtentTest test) {

		HashMap<String, Object> scheduleSettingDTO = null;
		scheduleSettingDTO = pp.composeScheduleSettingDTO(cloud_direct_schedule, custom_schedule, ar_schedule,
				merge_schedule);
		return scheduleSettingDTO;
	}

	/**
	 * create ExcludeInfoDTO
	 * 
	 * @author shan.jing
	 * @param excludes
	 * @param type
	 * @param value
	 * @param test
	 * @return ExcludeInfoDTO
	 */
	public ArrayList<HashMap<String, Object>> createExcludeInfoDTO(ArrayList<HashMap<String, Object>> excludes,
			String type, String value, ExtentTest test) {

		HashMap<String, Object> excludeInfoDTO = null;
		excludeInfoDTO = pp.composeExcludeInfoDTO(type, value);
		if (excludes == null) {
			excludes = new ArrayList<HashMap<String, Object>>();
		}
		excludes.add(excludeInfoDTO);
		return excludes;
	}

	public Response getRemotePolicies(String org_id, String console, String user, String pwd, int port, String protocol,
			ExtentTest test) {
		ArrayList<HashMap<String, Object>> remotePolicies = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> udpConsoleServerDTO = null;
		udpConsoleServerDTO = pp.composeUdpConsoleServerDTO(org_id, console, user, pwd, port, protocol);
		Response response = policy4SPOGInvoker.getRemotePolicies(udpConsoleServerDTO);
		// spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		return response;
	}

	/**
	 * create CloudDirectLocalBackupDTO
	 * 
	 * @author shan.jing
	 * @param path
	 * @param value
	 * @param excludes
	 * @param test
	 * @return CloudDirectLocalBackupDTO
	 */
	public HashMap<String, Object> createCloudDirectLocalBackupDTO(String path, String enabled,
			ArrayList<HashMap<String, Object>> excludes, ExtentTest test) {

		HashMap<String, Object> cloudDirectLocalBackupDTO = null;
		cloudDirectLocalBackupDTO = pp.composeCloudDirectLocalBackupDTO(path, enabled, excludes);
		return cloudDirectLocalBackupDTO;
	}

	/**
	 * create CloudDirectLocalBackupDTO
	 * 
	 * @author shan.jing
	 * @param path
	 * @param value
	 * @param test
	 * @return CloudDirectLocalBackupDTO
	 */
	public HashMap<String, Object> createCloudDirectLocalBackupDTO(String path, String enabled, ExtentTest test) {

		HashMap<String, Object> cloudDirectLocalBackupDTO = null;
		cloudDirectLocalBackupDTO = pp.composeCloudDirectLocalBackupDTO(path, enabled);
		return cloudDirectLocalBackupDTO;
	}

	/**
	 * create CloudDirectImageBackupTaskInfoDTO
	 * 
	 * @author shan.jing
	 * @param drives
	 * @param local_backup
	 * @param test
	 * @return CloudDirectImageBackupTaskInfoDTO
	 */
	public HashMap<String, Object> createCloudDirectImageBackupTaskInfoDTO(String drives,
			HashMap<String, Object> local_backup, ExtentTest test) {
		HashMap<String, Object> cloudDirectImageBackupTaskInfoDTO = null;
		cloudDirectImageBackupTaskInfoDTO = pp.composeCloudDirectImageBackupTaskInfoDTO(drives, local_backup);
		return cloudDirectImageBackupTaskInfoDTO;
	}

	/**
	 * create CloudDirectFileBackupTaskInfoDTO
	 * 
	 * @author shan.jing
	 * @param path
	 * @param value
	 * @param test
	 * @return CloudDirectFileBackupTaskInfoDTO
	 */
	public HashMap<String, Object> createCloudDirectFileBackupTaskInfoDTO(String path,
			HashMap<String, Object> local_backup, ExtentTest test) {

		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO = null;
		cloudDirectFileBackupTaskInfoDTO = pp.composeCloudDirectFileBackupTaskInfoDTO(path, local_backup);
		return cloudDirectFileBackupTaskInfoDTO;
	}

	/**
	 * create CloudDirectFileBackupTaskInfoDTO
	 * 
	 * @author shan.jing
	 * @param path
	 * @param value
	 * @param excludes
	 * @param test
	 * @return CloudDirectFileBackupTaskInfoDTO
	 */
	public HashMap<String, Object> createCloudDirectFileBackupTaskInfoDTO(String path,
			HashMap<String, Object> local_backup, ArrayList<HashMap<String, Object>> excludes, ExtentTest test) {

		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO = null;
		cloudDirectFileBackupTaskInfoDTO = pp.composeCloudDirectFileBackupTaskInfoDTO(path, local_backup, excludes);
		return cloudDirectFileBackupTaskInfoDTO;
	}

	public ArrayList<HashMap<String, Object>> createSourceInfo(ArrayList<HashMap<String, Object>> sources,
			String source_id, HashMap<String, Object> vmInfo, ExtentTest test) {
		HashMap<String, Object> sourceInfo = null;
		sourceInfo = pp.composeSourceInfo(source_id, vmInfo);
		if (sources == null) {
			sources = new ArrayList<HashMap<String, Object>>();
		}
		sources.add(sourceInfo);
		return sources;

	}

	public HashMap<String, Object> createVMInfo(String hypervisor_id, String vm_name, String vm_os_major,
			String vm_guest_os, ExtentTest test) {

		HashMap<String, Object> vMInfo = null;
		vMInfo = pp.composeVMInfo(hypervisor_id, vm_name, vm_os_major, vm_guest_os);
		return vMInfo;
	}

	/**
	 * create RetentionPolicyOption
	 * 
	 * @author shan.jing
	 * @param daily_backup
	 * @param weekly_backup
	 * @param monthly_backup
	 * @param manual_backup
	 * @param test
	 * @return RetentionPolicyOption
	 */
	public HashMap<String, Object> createRetentionPolicyOption(String daily_backup, String weekly_backup,
			String monthly_backup, String manual_backup, ExtentTest test) {

		HashMap<String, Object> retentionPolicyOption = null;
		retentionPolicyOption = pp.composeRetentionPolicyOption(daily_backup, weekly_backup, monthly_backup,
				manual_backup);
		return retentionPolicyOption;
	}

	/**
	 * create PerformARTestOption
	 * 
	 * @author shan.jing
	 * @param daily_backup
	 * @param weekly_backup
	 * @param monthly_backup
	 * @param lastet_recovery_point
	 * @param test
	 * @return PerformARTestOption
	 */
	public HashMap<String, Object> createPerformARTestOption(String daily_backup, String weekly_backup,
			String monthly_backup, String lastet_recovery_point, ExtentTest test) {

		HashMap<String, Object> performARTestOption = null;
		performARTestOption = pp.composePerformARTestOption(daily_backup, weekly_backup, monthly_backup,
				lastet_recovery_point);
		return performARTestOption;
	}

	/**
	 * create PolicyThrottleDTO
	 * 
	 * @author shan.jing
	 * @param throttle_id
	 * @param task_id
	 * @param throttle_type
	 * @param rate
	 * @param days_of_week
	 * @param start_time
	 * @param end_time
	 * @param test
	 * @return policyThrottleDTO
	 */
	public ArrayList<HashMap<String, Object>> createPolicyThrottleDTO(ArrayList<HashMap<String, Object>> throttle,
			String throttle_id, String task_id, String throttle_type, String rate, String days_of_week,
			String start_time, String end_time, ExtentTest test) {

		HashMap<String, Object> policyThrottleDTO = null;
		policyThrottleDTO = pp.composePolicyThrottleDTO(throttle_id, task_id, throttle_type, rate, days_of_week,
				start_time, end_time);
		if (throttle == null) {
			throttle = new ArrayList<HashMap<String, Object>>();
		}
		throttle.add(policyThrottleDTO);
		return throttle;
	}

	/**
	 * create PolicyThrottleDTO
	 * 
	 * @author shan.jing
	 * @param throttle_id
	 * @param task_id
	 * @param throttle_type
	 * @param rate
	 * @param days_of_week
	 * @param start_time
	 * @param end_time
	 * @param task_type
	 * @param destination_id
	 * @param destination_name
	 * @param test
	 * @return policyThrottleDTO
	 */
	public ArrayList<HashMap<String, Object>> createPolicyThrottleDTO(ArrayList<HashMap<String, Object>> throttle,
			String throttle_id, String task_id, String throttle_type, String rate, String days_of_week,
			String start_time, String end_time, String task_type, String destination_id, String destination_name,
			ExtentTest test) {

		HashMap<String, Object> policyThrottleDTO = null;
		policyThrottleDTO = pp.composePolicyThrottleDTO(throttle_id, task_id, throttle_type, rate, days_of_week,
				start_time, end_time, task_type, destination_id, destination_name);
		if (throttle == null) {
			throttle = new ArrayList<HashMap<String, Object>>();
		}
		throttle.add(policyThrottleDTO);
		return throttle;
	}

	/**
	 * create PolicyTaskDTO
	 * 
	 * @author shan.jing
	 * @param task_id
	 * @param task_type
	 * @param destination_id
	 * @param parent_id
	 * @param cloud_direct_image_backup
	 * @param cloud_direct_file_backup
	 * @param udp_replication_from_remote
	 * @param test
	 * @return policyTaskDTO
	 */
	public ArrayList<HashMap<String, Object>> createPolicyTaskDTO(ArrayList<HashMap<String, Object>> destinations,
			String task_id, String task_type, String destination_id, String parent_id,
			HashMap<String, Object> cloud_direct_image_backup, HashMap<String, Object> cloud_direct_file_backup,
			HashMap<String, Object> udp_replication_from_remote, ExtentTest test) {

		HashMap<String, Object> policyTaskDTO = null;
		policyTaskDTO = pp.composePolicyTaskDTO(task_id, task_type, destination_id, parent_id,
				cloud_direct_image_backup, cloud_direct_file_backup, udp_replication_from_remote);
		if (destinations == null) {
			destinations = new ArrayList<HashMap<String, Object>>();
		}
		destinations.add(policyTaskDTO);
		return destinations;
	}

	/**
	 * create PolicyTaskDTO
	 * 
	 * @author shan.jing
	 * @param task_id
	 * @param task_type
	 * @param destination_id
	 * @param parent_id
	 * @param cloud_direct_image_backup
	 * @param cloud_direct_file_backup
	 * @param udp_replication_from_remote
	 * @param udp_replication_to_remote
	 * @param test
	 * @return policyTaskDTO
	 */
	public ArrayList<HashMap<String, Object>> createPolicyTaskDTO(ArrayList<HashMap<String, Object>> destinations,
			String task_id, String task_type, String destination_id, String parent_id,
			HashMap<String, Object> cloud_direct_image_backup, HashMap<String, Object> cloud_direct_file_backup,
			HashMap<String, Object> udp_replication_from_remote, HashMap<String, Object> udp_replication_to_remote,
			ExtentTest test) {

		HashMap<String, Object> policyTaskDTO = null;
		policyTaskDTO = pp.composePolicyTaskDTO(task_id, task_type, destination_id, parent_id,
				cloud_direct_image_backup, cloud_direct_file_backup, udp_replication_from_remote,
				udp_replication_to_remote);
		if (destinations == null) {
			destinations = new ArrayList<HashMap<String, Object>>();
		}
		destinations.add(policyTaskDTO);
		return destinations;
	}

	/**
	 * create PolicyTaskDTO
	 * 
	 * @author Rakesh.Chalamala
	 * @param task_id
	 * @param task_type
	 * @param destination_id
	 * @param parent_id
	 * @param cloud_direct_image_backup
	 * @param cloud_direct_file_backup
	 * @param udp_replication_from_remote
	 * @param udp_replication_to_remote
	 * @param test
	 * @return policyTaskDTO
	 */
	public ArrayList<HashMap<String, Object>> createPolicyTaskDTO(ArrayList<HashMap<String, Object>> destinations,
			String task_id, String task_type, String destination_id, String parent_id,
			HashMap<String, Object> cloud_direct_image_backup, HashMap<String, Object> cloud_direct_file_backup,
			HashMap<String, Object> udp_replication_from_remote, HashMap<String, Object> udp_replication_to_remote,
			HashMap<String, Object> cloud_direct_sql_server_backup, ExtentTest test) {

		HashMap<String, Object> policyTaskDTO = null;
		policyTaskDTO = pp.composePolicyTaskDTO(task_id, task_type, destination_id, parent_id,
				cloud_direct_image_backup, cloud_direct_file_backup, udp_replication_from_remote,
				udp_replication_to_remote, cloud_direct_sql_server_backup);
		if (destinations == null) {
			destinations = new ArrayList<HashMap<String, Object>>();
		}
		destinations.add(policyTaskDTO);
		return destinations;
	}

	/**
	 * create SLADTO
	 * 
	 * @author shan.jing
	 * @param recovery_time_objectives
	 * @param recovery_point_objective
	 * @param test
	 * @return sLADTO
	 */
	public HashMap<String, Object> createSLADTO(HashMap<String, Object> recovery_time_objectives,
			HashMap<String, Object> recovery_point_objective, ExtentTest test) {

		HashMap<String, Object> sLADTO = null;
		sLADTO = pp.composeSLADTO(recovery_time_objectives, recovery_point_objective);
		return sLADTO;
	}

	/**
	 * create RecvoeryTimeObjective
	 * 
	 * @author shan.jing
	 * @param assured_recovery_test
	 * @param vm_recovery
	 * @param instant_vm_recovery
	 * @param file_level_restore
	 * @param bare_metal_recovery
	 * @param test
	 * @return recvoeryTimeObjective
	 */
	public HashMap<String, Object> createRecvoeryTimeObjective(HashMap<String, Object> assured_recovery_test,
			HashMap<String, Object> vm_recovery, HashMap<String, Object> instant_vm_recovery,
			HashMap<String, Object> file_level_restore, HashMap<String, Object> bare_metal_recovery, ExtentTest test) {

		HashMap<String, Object> recvoeryTimeObjective = null;
		recvoeryTimeObjective = pp.composeRecvoeryTimeObjective(assured_recovery_test, vm_recovery, instant_vm_recovery,
				file_level_restore, bare_metal_recovery);
		return recvoeryTimeObjective;
	}

	/**
	 * create RecoveryPointObjective
	 * 
	 * @author shan.jing
	 * @param value
	 * @param test
	 * @return recoveryPointObjective
	 */
	public HashMap<String, Object> createRecoveryPointObjective(HashMap<String, Object> value,
			HashMap<String, Object> recovery_point_objective, ExtentTest test) {

		HashMap<String, Object> recoveryPointObjective = null;
		recoveryPointObjective = pp.composeRecoveryPointObjective(value);
		return recoveryPointObjective;
	}

	/**
	 * create SLAParameterValueDTO
	 * 
	 * @author shan.jing
	 * @param value
	 * @param unit
	 * @return sLAParameterValueDTO
	 */
	public HashMap<String, Object> createSLAParameterValueDTO(String value, String unit, ExtentTest test) {

		HashMap<String, Object> sLAParameterValueDTO = null;
		sLAParameterValueDTO = pp.composeSLAParameterValueDTO(value, unit);
		return sLAParameterValueDTO;
	}

	/**
	 * create PolicyTaskDTO
	 * 
	 * @author shan.jing
	 * @param policy_name
	 * @param policy_description
	 * @param policy_type
	 * @param sla
	 * @param is_draft
	 * @param throttle
	 * @param sources
	 * @param destinations
	 * @param schedules
	 * @param policy_id
	 * @param organization_id
	 * @param test
	 * @return policyCreateRequest Response
	 * @throws JsonProcessingException
	 */
	public Response createPolicy(String policy_name, String policy_description, String policy_type,
			HashMap<String, Object> sla, String is_draft, String sources,
			ArrayList<HashMap<String, Object>> destinations, ArrayList<HashMap<String, Object>> schedules,
			ArrayList<HashMap<String, Object>> throttles, String policy_id, String organization_id, ExtentTest test) {
		ArrayList<HashMap<String, Object>> sourceInfo = null;
		Map<String, Object> policyCreateRequest = null;
		if (sources != null) {
			sourceInfo = createSourceInfo(null, sources, null, test);
			policyCreateRequest = pp.composePolicyCreateRequestDTO(policy_name, policy_description, policy_type, sla,
					is_draft, throttles, sourceInfo, destinations, schedules, policy_id, organization_id);
		} else {
			policyCreateRequest = pp.composePolicyCreateRequestDTO(policy_name, policy_description, policy_type, sla,
					is_draft, throttles, null, destinations, schedules, policy_id, organization_id);
		}

		// try {
		// System.out.println("policy info:"+ new
		// ObjectMapper().writeValueAsString(policyCreateRequest));
		// } catch (JsonProcessingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		Response createPolicy = policy4SPOGInvoker.createPolicy(policyCreateRequest);
		return createPolicy;
	}

	public Response createPolicy(String policy_name, String policy_description, String policy_type,
			HashMap<String, Object> sla, Boolean is_draft, String sources,
			ArrayList<HashMap<String, Object>> destinations, ArrayList<HashMap<String, Object>> schedules,
			ArrayList<HashMap<String, Object>> throttles, String policy_id, String organization_id, ExtentTest test) {
		ArrayList<HashMap<String, Object>> sourceInfo = null;
		Map<String, Object> policyCreateRequest = null;
		if (sources != null) {
			sourceInfo = createSourceInfo(null, sources, null, test);
			policyCreateRequest = pp.composePolicyCreateRequestDTO(policy_name, policy_description, policy_type, sla,
					String.valueOf(is_draft), throttles, sourceInfo, destinations, schedules, policy_id,
					organization_id);
		} else {
			policyCreateRequest = pp.composePolicyCreateRequestDTO(policy_name, policy_description, policy_type, sla,
					String.valueOf(is_draft), throttles, null, destinations, schedules, policy_id, organization_id);
		}

		// try {
		// System.out.println("policy info:"+ new
		// ObjectMapper().writeValueAsString(policyCreateRequest));
		// } catch (JsonProcessingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		Response createPolicy = policy4SPOGInvoker.createPolicy(policyCreateRequest);
		return createPolicy;
	}

	public Response getPolicies(HashMap<String, String> params) {

		return policy4SPOGInvoker.getPolicies(params);
	}

	public void checkPolicyDestinations(Response response, int expectedStatusCode,
			ArrayList<HashMap<String, Object>> destinations, ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile("******************checkPolicyDestinations*******************");
		test.log(LogStatus.INFO, "expect status code is " + expectedStatusCode);
		errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
		response.then().statusCode(expectedStatusCode);
		errorHandle.printDebugMessageInDebugFile(response.then().log().body().toString());
		if (expectedStatusCode == SpogConstants.SUCCESS_POST
				|| expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			for (int i = 0; i < destinations.size(); i++) {
				test.log(LogStatus.INFO, "check create_ts");

				response.then()
						.body("data.destinations[" + i + "].task_id", equalTo(destinations.get(i).get("task_id")))
						.body("data.destinations[" + i + "].task_type", equalTo(destinations.get(i).get("task_type")))
						.body("data.destinations[" + i + "].destination_id",
								equalTo(destinations.get(i).get("destination_id")))
						.body("data.destinations[" + i + "].parent_id", equalTo(destinations.get(i).get("parent_id")))
						// .body("data.destinations[" + i + "].cloud_direct_image_backup.local_backup",
						// equalTo(destinations.get(i).get("cloud_direct_image_backup.local_backup")))
						.body("data.destinations[" + i + "].cloud_direct_file_folder_backup",
								equalTo(destinations.get(i).get("cloud_direct_file_folder_backup")))
						.body("data.destinations[" + i + "].udp_replication_from_remote",
								equalTo(destinations.get(i).get("udp_replication_from_remote")));
			}
		}
	}

	public void checkReplicatePolicyDestination(Response response, int expectedStatusCode,
			ArrayList<HashMap<String, Object>> destinations, HashMap<String, Object> performARTestOption,
			HashMap<String, Object> retention_policy, ExtentTest test) {
		errorHandle.printDebugMessageInDebugFile("******************checkPolicyDestinations*******************");
		test.log(LogStatus.INFO, "expect status code is " + expectedStatusCode);
		errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
		response.then().statusCode(expectedStatusCode);
		errorHandle.printDebugMessageInDebugFile(response.then().log().body().toString());
		if (expectedStatusCode == SpogConstants.SUCCESS_POST
				|| expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			for (int i = destinations.size() - 1; i == 0; i--) {
				test.log(LogStatus.INFO, "check create_ts");
				response.then()
						.body("data.destinations[" + i + "].task_id", equalTo(destinations.get(i).get("task_id")))
						.body("data.destinations[" + i + "].task_type", equalTo(destinations.get(i).get("task_type")))
						.body("data.destinations[" + i + "].destination_id",
								equalTo(destinations.get(i).get("destination_id")))
						.body("data.destinations[" + i + "].parent_id", equalTo(destinations.get(i).get("parent_id")))
						// .body("data.destinations[" + i + "].cloud_direct_image_backup",
						// equalTo(destinations.get(i).get("cloud_direct_image_backup")))
						.body("data.destinations[" + i + "].cloud_direct_file_backup",
								equalTo(destinations.get(i).get("cloud_direct_file_backup")))
						.body("data.destinations[" + i + "].udp_replication_from_remote.perform_ar_test",
								equalTo(performARTestOption))
						.body("data.destinations[" + i + "].udp_replication_from_remote.retention",
								equalTo(retention_policy));
			}
		}
	}

	public void checkPolicyThrottles(Response response, int expectedStatusCode,
			ArrayList<HashMap<String, Object>> throttles, ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile("******************checkPolicyThrottles*******************");
		test.log(LogStatus.INFO, "expect status code is " + expectedStatusCode);
		errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
		response.then().statusCode(expectedStatusCode);
		errorHandle.printDebugMessageInDebugFile(response.then().log().body().toString());
		if (expectedStatusCode == SpogConstants.SUCCESS_POST
				|| expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			for (int i = 0; i < throttles.size(); i++) {
				test.log(LogStatus.INFO, "check create_ts");

				if (throttles.get(i).get("throttle_id") == null) {
					response.then().body("data.throttles[" + i + "].throttle_id", not(isEmptyOrNullString()));
				} else {
					response.then().body("data.throttles[" + i + "].throttle_id",
							equalTo(throttles.get(i).get("throttle_id")));
				}
				response.then().body("data.throttles[" + i + "].task_id", equalTo(throttles.get(i).get("task_id")))
						.body("data.throttles[" + i + "].throttle_type", equalTo(throttles.get(i).get("throttle_type")))
						.body("data.throttles[" + i + "].rate", equalTo(throttles.get(i).get("rate")))
						.body("data.throttles[" + i + "].days_of_week", equalTo(throttles.get(i).get("days_of_week")))
						.body("data.throttles[" + i + "].start_time", equalTo(throttles.get(i).get("start_time")))
						.body("data.throttles[" + i + "].end_time", equalTo(throttles.get(i).get("end_time")));
			}
		}
	}

	public void checkPolicySchedules(Response response, int expectedStatusCode,
			ArrayList<HashMap<String, Object>> schedules, ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile("******************checkPolicySchedules*******************");
		test.log(LogStatus.INFO, "expect status code is " + expectedStatusCode);
		errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
		response.then().statusCode(expectedStatusCode);
		errorHandle.printDebugMessageInDebugFile(response.then().log().body().toString());
		if (expectedStatusCode == SpogConstants.SUCCESS_POST
				|| expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			for (int i = 0; i < schedules.size(); i++) {
				test.log(LogStatus.INFO, "check create_ts");
				response.then()
						.body("data.schedules[" + i + "].schedule_id", equalTo(schedules.get(i).get("schedule_id")))
						.body("data.schedules[" + i + "].schedule_type", equalTo(schedules.get(i).get("schedule_type")))
						.body("data.schedules[" + i + "].task_id", equalTo(schedules.get(i).get("task_id")))
						.body("data.schedules[" + i + "].destination_id",
								equalTo(schedules.get(i).get("destination_id")))
						// .body("data.schedules[" + i + "].schedule",
						// equalTo(schedules.get(i).get("schedule")))
						.body("data.schedules[" + i + "].start_time", equalTo(schedules.get(i).get("start_time")))
						.body("data.schedules[" + i + "].end_time", equalTo(schedules.get(i).get("end_time")));

			}
		}
	}

	public void checkPolicyCommon(Response response, int expectedStatusCode, String policy_name,
			String policy_description, String policy_type, HashMap<String, Object> sla, String is_draft,
			String policy_status, String sources, String policy_id, String organization_id, ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile("******************checkPolicyCommonValue*******************");
		test.log(LogStatus.INFO, "expect status code is " + expectedStatusCode);
		errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
		response.then().statusCode(expectedStatusCode);
		String[] sourcesStr = null;
		String[] emptyArray = new String[0];
		if (expectedStatusCode == SpogConstants.SUCCESS_POST
				|| expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			response.then().body("data.policy_name", equalTo(policy_name))
					.body("data.policy_description", equalTo(policy_description))
					.body("data.policy_type", equalTo(policy_type)).body("data.sla", equalTo(sla))
					.body("data.is_draft", equalTo(Boolean.valueOf(is_draft)))
					.body("data.policy_id", equalTo(policy_id)).body("data.organization_id", equalTo(organization_id));
			ArrayList<String> responseSources = response.then().extract().path("data.sources");
			if (sources == null || sources.equalsIgnoreCase("")) {
				ArrayList<String> sourceArrayList = response.then().extract().path("data.sources");
				// assertEquals(sourceArrayList.size(), 0, "it return value is empty array.");
			} else {
				sourcesStr = sources.split(",");
				int length = sourcesStr.length;
				if (length != responseSources.size()) {
					assertTrue("compare sources length passed", false);
				}
				ArrayList<HashMap<String, Object>> responseSourcesdata = response.then().extract().path("data.sources");
				for (int i = 0; i < length; i++) {
					if (responseSourcesdata.get(i).get("source_id").toString().equalsIgnoreCase(sourcesStr[i])) {

						Response response1 = spogServer.getSourceById(sourcesStr[i], test);
						HashMap<String, Object> expected_sourceInfo = response1.then().extract().path("data");
						HashMap<String, Object> expected_sourceInfo_os = response1.then().extract()
								.path("data.operating_system");
						HashMap<String, Object> expected_sourceInfo_Site = response1.then().extract().path("data.site");
						HashMap<String, Object> expected_sourceInfo_Hypervisor = response1.then().extract()
								.path("data.hypervisor");
						ArrayList<String> expected_SourceGroup = response1.then().extract().path("data.source_group");

						if (expected_sourceInfo.get("source_name").toString()
								.equals(responseSourcesdata.get(i).get("source_name").toString())
								&& expected_sourceInfo.get("source_type").toString()
										.equals(responseSourcesdata.get(i).get("source_type").toString())
								&& expected_sourceInfo_os.get("os_major").toString().equals(response.then().extract()
										.path("data.sources[" + i + "].operating_system.os_major").toString())
						// &&expected_sourceInfo_Site.get("site_id").toString().equals(responseSourcesdata.get(i).get("site_id").toString())
						// &&expected_sourceInfo_Site.get("site_name").toString().equals(responseSourcesdata.get(i).get("site_name").toString())
						) {
							if (expected_sourceInfo_Hypervisor != null && expected_SourceGroup.size() > 0) {
								if (expected_sourceInfo_Hypervisor.get("hypervisor_id").toString()
										.equals(responseSourcesdata.get(i).get("hypervisor_id").toString())
										&& expected_sourceInfo_Hypervisor.get("hypervisor_name").toString()
												.equals(responseSourcesdata.get(i).get("hypervisor_name").toString())
										&& expected_SourceGroup.get(i)
												.equals(responseSourcesdata.get(i).get("source_group").toString())) {
									assertTrue("compare source Information " + sourcesStr[i] + "Pass", true);

								}
							}

							test.log(LogStatus.PASS, "Compare source information succesfull" + sourcesStr[i]);

							assertTrue("compare source Information " + sourcesStr[i] + "Pass", true);
						}
					} else {
						assertTrue("compare source " + sourcesStr[i] + "failed", false);
					}
				}
			}

		}
	}

	public String getPolicyId(Response response, int expectedStatusCode, ExtentTest test) {

		String ret = null;
		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {
			ret = response.then().extract().path("data.policy_id");
		}
		return ret;
	}

	public String checkPolicyStatusById(String admin_Token, String policy_id, int expectedStatusCode,
			String policy_status, ExtentTest test) {
		Response response = policy4SPOGInvoker.getPolicyById(admin_Token, policy_id, test);
		String ret = null;
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ret = response.then().extract().path("data.policy_status");
			response.then().body("data.policy_status", equalTo(policy_status));
		}
		return ret;
	}

	public void checkPolicyWithErrorInfo(Response response, int expectedStatusCode, String expectedErrorCode,
			String expectedErrorMessage, ExtentTest test) {

		response.then().statusCode(expectedStatusCode);
		checkErrorCode(response, expectedErrorCode);
		checkErrorMessage(response, expectedErrorMessage);
	}

	public void checkErrorCode(Response response, String expectedErrorCode) {

		List<String> messageArray = response.body().jsonPath().getList("errors.code");
		boolean find = false;
		for (int i = 0; i < messageArray.size(); i++) {
			if (messageArray.get(i).contains(expectedErrorCode)) {
				find = true;
				break;
			}
		}
		if (find) {
			assertTrue("error code check is correct", true);
		} else {
			assertEquals(messageArray.get(0), expectedErrorCode);
		}
	}

	public void checkErrorMessage(Response response, String expectedErrorMessage) {

		List<String> messageArray = response.body().jsonPath().getList("errors.message");
		List<String> detailArray = response.body().jsonPath().getList("errors.detailMessage");
		System.out.println("The value of the message :" + messageArray.get(0));
		System.out.println("The value of the detail Message:" + detailArray.get(0));
		boolean find = false;
		for (int i = 0; i < messageArray.size(); i++) {
			if (messageArray.get(i).contains(expectedErrorMessage)) {
				find = true;
				break;
			}
		}
		for (int i = 0; i < detailArray.size(); i++) {
			if (detailArray.get(i).contains(expectedErrorMessage)) {
				find = true;
				break;
			}
		}
		if (find) {
			assertTrue("error message check is correct", true);
		} else {
			assertEquals(messageArray.get(0), expectedErrorMessage);
		}
	}

	/**
	 * Check CloudDirectSQLServerBackupTask response
	 * 
	 * @author Rakesh.Chalamala
	 * @param response
	 * @param expectedInfo
	 * 
	 */
	public void checkCDSQLServerBackupTask(Response response, HashMap<String, Object> expectedResponse,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE
				|| expectedStatusCode == SpogConstants.SUCCESS_POST) {

			// Reading data from expected response and giving input to the already written
			// methods
			ArrayList<HashMap<String, Object>> throttles = (ArrayList<HashMap<String, Object>>) expectedResponse
					.get("throttles");
			ArrayList<HashMap<String, Object>> schedules = (ArrayList<HashMap<String, Object>>) expectedResponse
					.get("schedules");
			String policy_name = (String) expectedResponse.get("policy_name");
			String policy_description = (String) expectedResponse.get("policy_description");
			String policy_type = (String) expectedResponse.get("policy_type");
			HashMap<String, Object> sla = (HashMap<String, Object>) expectedResponse.get("sla");
			String is_draft = (String) expectedResponse.get("is_draft");
			String policy_status = (String) expectedResponse.get("policy_status");
			ArrayList<HashMap<String, Object>> sources = (ArrayList<HashMap<String, Object>>) expectedResponse
					.get("sources");
			String policy_id = (String) expectedResponse.get("policy_id");
			String organization_id = (String) expectedResponse.get("organization_id");

			checkPolicyCommon(response, expectedStatusCode, policy_name, policy_description, policy_type, sla, is_draft,
					policy_status, sources.get(0).get("source_id").toString(), policy_id, organization_id, test);
			checkPolicyThrottles(response, expectedStatusCode, throttles, test);
			checkPolicySchedules(response, expectedStatusCode, schedules, test);

			checkSQLServerPolicyDestinations(response, expectedResponse, test);

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			// spogServer.checkErrorCode(response,code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}

	}

	public void checkSQLServerPolicyDestinations(Response response, HashMap<String, Object> expectedResponse,
			ExtentTest test) {
		// Validate destinations part
		ArrayList<HashMap<String, Object>> expDestinations = (ArrayList<HashMap<String, Object>>) expectedResponse
				.get("destinations");
		ArrayList<HashMap<String, Object>> actDestinations = (ArrayList<HashMap<String, Object>>) response.then()
				.extract().path("data.destinations");

		if (expDestinations.size() == actDestinations.size()) {
			for (int i = 0; i < expDestinations.size(); i++) {
				HashMap<String, Object> expData = expDestinations.get(i);
				HashMap<String, Object> actData = actDestinations.get(i);

				spogServer.assertResponseItem(expData.get("task_id").toString(), actData.get("task_id").toString(),
						test);
				spogServer.assertResponseItem(expData.get("task_type").toString(), actData.get("task_type").toString(),
						test);
				spogServer.assertResponseItem(expData.get("destination_id").toString(),
						actData.get("destination_id").toString(), test);
				spogServer.assertResponseItem(expData.get("cloud_direct_image_backup"),
						actData.get("cloud_direct_image_backup"), test);
				spogServer.assertResponseItem(expData.get("cloud_direct_file_backup"),
						actData.get("cloud_direct_file_backup"), test);
				spogServer.assertResponseItem(expData.get("udp_replication_from_remote"),
						actData.get("udp_replication_from_remote"), test);
				spogServer.assertResponseItem(expData.get("udp_replication_to_remote"),
						actData.get("udp_replication_to_remote"), test);
				assertNotNull(actData.get("destination_name"));
				assertNotNull(actData.get("destination_type"));

				checkSQLServerTaskInfo(expData, actData, test);

			}
		} else {
			test.log(LogStatus.FAIL, "Expected destinations count: " + expDestinations.size()
					+ " does not match with actual count: " + actDestinations.size());
			assertTrue(expDestinations.size() == actDestinations.size());
		}
	}

	/**
	 * Verify SQL Server backup task infromation
	 * 
	 * @param expData
	 * @param actData
	 * @param test
	 */
	public void checkSQLServerTaskInfo(HashMap<String, Object> expData, HashMap<String, Object> actData,
			ExtentTest test) {

		HashMap<String, Object> expTaskData = (HashMap<String, Object>) expData.get("cloud_direct_sql_server_backup");
		HashMap<String, Object> actTaskData = (HashMap<String, Object>) actData.get("cloud_direct_sql_server_backup");

		spogServer.assertResponseItem(expTaskData.get("sql_backup_type").toString(),
				actTaskData.get("sql_backup_type").toString(), test);

		// when sql_backup_default value is null or "" in request, the api will consider
		// the default value as true
		if (expTaskData.get("sql_backup_default") == null
				|| expTaskData.get("sql_backup_default").toString().equals("")) {
			spogServer.assertResponseItem("true", actTaskData.get("sql_backup_default").toString(), test);
		} else {
			spogServer.assertResponseItem(expTaskData.get("sql_backup_default").toString(),
					actTaskData.get("sql_backup_default").toString(), test);
		}

		spogServer.assertResponseItem(expTaskData.get("sql_backup_local_instance"),
				actTaskData.get("sql_backup_local_instance"), test);
		spogServer.assertResponseItem(expTaskData.get("sql_backup_path"), actTaskData.get("sql_backup_path"), test);

		// when sql_verify_enabled value is null or "" in request, the api will consider
		// the default value as false
		if (expTaskData.get("sql_verify_enabled") == null
				|| expTaskData.get("sql_verify_enabled").toString().equals("")) {
			spogServer.assertResponseItem("false", actTaskData.get("sql_verify_enabled").toString(), test);
		} else {
			spogServer.assertResponseItem(expTaskData.get("sql_verify_enabled").toString(),
					actTaskData.get("sql_verify_enabled").toString(), test);
		}

		// verify local backup data
		HashMap<String, Object> expLB = (HashMap<String, Object>) expTaskData.get("local_backup");
		HashMap<String, Object> actLB = (HashMap<String, Object>) actTaskData.get("local_backup");
		spogServer.assertResponseItem(expLB.get("path"), actLB.get("path"), test);
		spogServer.assertResponseItem(expLB.get("enabled").toString(), actLB.get("enabled").toString(), test);

		test.log(LogStatus.PASS, "SQL Server backup task infromation validation Passed");
	}

	/**
	 * create CloudDirectFileBackupTaskInfoDTO
	 * 
	 * @author Kiran.Sripada
	 * @param path
	 * @param value
	 * @param excludes
	 * @param test
	 * @return CloudDirectFileBackupTaskInfoDTO
	 */
	public HashMap<String, Object> createCloudDirectImageBackupTaskInfoDTO(ArrayList<String> path,
			HashMap<String, Object> local_backup, ExtentTest test) {
		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO = null;
		cloudDirectFileBackupTaskInfoDTO = pp.composeCloudDirectImageBackupTaskInfoDTO(path, local_backup);
		return cloudDirectFileBackupTaskInfoDTO;
	}

	/**
	 * Create CloudDirectSQLServerBackupTaskInfoDTO
	 * 
	 * @author Rakesh.Chalamala
	 * @param sql_backup_type
	 * @param sql_backup_default
	 * @param sql_backup_local_instance
	 * @param sql_backup_path
	 * @param sql_verify_enabled
	 * @param local_backup
	 * @return cloudDirectSQLServerBackupTaskInfo
	 */
	public HashMap<String, Object> createCloudDirectSQLServerBackupTaskInfoDTO(String sql_backup_type,
			String sql_backup_default, ArrayList<String> sql_backup_local_instance, String sql_backup_path,
			String sql_verify_enabled, HashMap<String, Object> local_backup, ExtentTest test) {
		HashMap<String, Object> CloudDirectSQLServerBackupTaskInfo = null;
		CloudDirectSQLServerBackupTaskInfo = pp.composeCloudDirectSQLServerBackupTaskInfoDTO(sql_backup_type,
				sql_backup_default, sql_backup_local_instance, sql_backup_path, sql_verify_enabled, local_backup);
		return CloudDirectSQLServerBackupTaskInfo;
	}

	/**
	 * updae PolicyTaskDTO
	 * 
	 * @author kiran.sripada
	 * @param policy_name
	 * @param policy_description
	 * @param policy_type
	 * @param sla
	 * @param is_draft
	 * @param throttle
	 * @param sources
	 * @param destinations
	 * @param schedules
	 * @param policy_id
	 * @param organization_id
	 * @param test
	 * @return policyCreateRequest Response
	 */
	public Response updatePolicy(String policy_name, String policy_description, String policy_type,
			HashMap<String, Object> sla, String is_draft,
			// String policy_status,
			String sources, ArrayList<HashMap<String, Object>> destinations,
			ArrayList<HashMap<String, Object>> schedules, ArrayList<HashMap<String, Object>> throttles,
			String policy_id, String organization_id, String validToken, ExtentTest test) {
		Map<String, Object> policyCreateRequest = null;
		ArrayList<HashMap<String, Object>> sourceInfo = createSourceInfo(null, sources, null, test);
		policyCreateRequest = pp.composePolicyCreateRequestDTO(policy_name, policy_description, policy_type, sla,
				is_draft, throttles, sourceInfo, destinations, schedules, policy_id, organization_id);
		Response updatePolicy = policy4SPOGInvoker.updatePolicy(validToken, policy_id, policyCreateRequest, test);
		return updatePolicy;
	}

	/**
	 * Update hypervisor policy include hyeprvisor id in request
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 * @param policy_name
	 * @param policy_description
	 * @param policy_type
	 * @param sla
	 * @param is_draft
	 * @param sources
	 * @param destinations
	 * @param schedules
	 * @param throttles
	 * @param policy_id
	 * @param organization_id
	 * @param validToken
	 * @param hypervisor_id
	 * @param test
	 * @return
	 */
	public Response updatePolicy(String policy_name, String policy_description, String policy_type,
			HashMap<String, Object> sla, String is_draft,
			// String policy_status,
			String sources, ArrayList<HashMap<String, Object>> destinations,
			ArrayList<HashMap<String, Object>> schedules, ArrayList<HashMap<String, Object>> throttles,
			String policy_id, String organization_id, String validToken, String hypervisor_id, ExtentTest test) {
		Map<String, Object> policyCreateRequest = null;
		ArrayList<HashMap<String, Object>> sourceInfo = /* createSourceInfo( null,sources, null,test); */ new ArrayList<>();
		policyCreateRequest = pp.composePolicyCreateRequestDTO(policy_name, policy_description, policy_type, sla,
				is_draft, throttles, sourceInfo, destinations, schedules, policy_id, organization_id);
		policyCreateRequest.put("hypervisor_id", hypervisor_id);
		Response updatePolicy = policy4SPOGInvoker.updatePolicy(validToken, policy_id, policyCreateRequest, test);
		return updatePolicy;
	}

	/**
	 * compose UdpReplicationToRemoteInfoDTO
	 * 
	 * @author shan.jing
	 * @param perform_ar_test
	 * @param retention_policy
	 * @param test
	 * @return udp_replication_from_remote_DTO
	 */
	public HashMap<String, Object> createUdpReplicationToRemoteInfoDTO(String remote_console_name, String user_name,
			String password, String port, String protocol, String remote_plan_global_uuid, String remote_plan_uuid,
			String remote_plan_name, String remote_plan_rpspolicy_uuid, String remote_plan_rpspolicy_name,
			String retry_start, String retry_times, ExtentTest test) {

		// TODO Auto-generated method stub

		HashMap<String, Object> udp_replication_to_remote_DTO = null;

		udp_replication_to_remote_DTO = pp.composeUdpReplicationToRemoteInfoDTO(remote_console_name, user_name,
				password, port, protocol, remote_plan_global_uuid, remote_plan_uuid, remote_plan_name,
				remote_plan_rpspolicy_uuid, remote_plan_rpspolicy_name, retry_start, retry_times);
		return udp_replication_to_remote_DTO;
	}

	/**
	 * compose udp_replication_from_remote_DTO
	 * 
	 * @author shan.jing
	 * @param perform_ar_test
	 * @param retention_policy
	 * @param test
	 * @return udp_replication_from_remote_DTO
	 */
	public HashMap<String, Object> createUdpReplicationFromRemoteInfoDTO(HashMap<String, Object> perform_ar_test,
			HashMap<String, Object> retention_policy, ExtentTest test) {
		// TODO Auto-generated method stub

		HashMap<String, Object> udp_replication_from_remote_DTO = null;

		udp_replication_from_remote_DTO = pp.composeUdpReplicationFromRemoteInfoDTO(perform_ar_test, retention_policy);

		return udp_replication_from_remote_DTO;
	}

	/**
	 * compose composeJobCondition
	 * 
	 * @author Ramya.Nagepalli
	 * @param job_missed
	 * @param rep_failed
	 * @param on_replicate_successed
	 * @param merge_successed
	 * @param merge_failed
	 * @return JobCondition
	 */
	public HashMap<String, Object> composeJobCondition(String job_missed, String rep_failed,
			String on_replicate_successed, String merge_successed, String merge_failed, ExtentTest test) {
		// TODO Auto-generated method stub

		HashMap<String, Object> jobCondition = new HashMap<String, Object>();

		jobCondition = pp.composeJobConditionDTO(job_missed, rep_failed, on_replicate_successed, merge_successed,
				merge_failed);

		return jobCondition;
	}

	/**
	 * compose composeemail_send_condition
	 * 
	 * @author Ramya.Nagepalli
	 * @param jobCondition
	 * @param Test
	 * @return email_send_condition
	 */
	public HashMap<String, Object> composeemail_send_condition(HashMap<String, Object> jobCondition, ExtentTest test) {
		// TODO Auto-generated method stub

		HashMap<String, Object> email_send_condition = new HashMap<String, Object>();

		email_send_condition = pp.composeReplicationFromRemoteEmailSendCondition(jobCondition);

		return email_send_condition;
	}

	/**
	 * compose composeProxyOption
	 * 
	 * @author Ramya.Nagepalli
	 * @param server_address
	 * @param port
	 * @param username
	 * @param password
	 * @param Test
	 * @return ProxyOption
	 */
	public HashMap<String, Object> composeProxyOption(String server_address, String port, String username,
			String password, ExtentTest test) {
		// TODO Auto-generated method stub

		HashMap<String, Object> ProxyOption = new HashMap<String, Object>();

		ProxyOption = pp.composeEmailProxyOptionDTO(server_address, port, username, password);

		return ProxyOption;
	}

	/**
	 * compose compose_email_setting
	 * 
	 * @author Ramya.Nagepalli
	 * @param mail_server_name
	 * @param smp_port
	 * @param username
	 * @param password
	 * @param subject
	 * @param from
	 * @param recipients
	 * @param is_enable_ssl
	 * @param is_enable_tsl
	 * @param is_enable_html_format
	 * @param email_send_condition
	 * @param Test
	 * @return email_setting
	 */

	public HashMap<String, Object> compose_email_setting(HashMap<String, Object> proxy_option, String mail_server_name,
			String smp_port, String username, String password, String subject, String from, String recipients,
			String is_enable_ssl, String is_enable_tsl, String is_enable_html_format,
			HashMap<String, Object> email_send_condition, ExtentTest test) {
		// TODO Auto-generated method stub

		HashMap<String, Object> email_setting = new HashMap<String, Object>();

		email_setting = pp.composeUdpReplicationFromRemoteEmailDTO(proxy_option, mail_server_name, smp_port, username,
				password, subject, from, recipients, is_enable_ssl, is_enable_tsl, is_enable_html_format,
				email_send_condition);

		return email_setting;
	}

	/**
	 * compose compose_nat_option
	 * 
	 * @author Ramya.Nagepalli
	 * @param host_name
	 * @param port
	 * @param Test
	 * @return nat_option
	 */
	public HashMap<String, Object> compose_nat_option(String host_name, String port, ExtentTest test) {
		// TODO Auto-generated method stub

		HashMap<String, Object> nat_option = new HashMap<String, Object>();

		nat_option = pp.composeNATOptionDTO(host_name, port);

		return nat_option;
	}

	public HashMap<String, Object> compose_CustomScheduleSettingDTO(String first_start_time, String backup_method,
			String days_of_week, String is_repeat_enable, String interval, String interval_unit, ExtentTest test) {
		// TODO Auto-generated method stub

		HashMap<String, Object> CustomScheduleSettingDTO = new HashMap<String, Object>();

		CustomScheduleSettingDTO = pp.composeCustomScheduleDTO(first_start_time, backup_method, days_of_week,
				is_repeat_enable, interval, interval_unit);

		return CustomScheduleSettingDTO;
	}

	/**
	 * @author Rakesh.Chalamala
	 * 
	 * @param token
	 * @param policy_id
	 * @return
	 */
	public Response deletePolicybyPolicyId(String token, String policy_id) {

		errorHandle.printInfoMessageInDebugFile("Calling delete policy API for policy_id:" + policy_id);
		Response response = policy4SPOGInvoker.deletePolicy(token, policy_id);
		errorHandle.printDebugMessageInDebugFile("Response status is " + response.getStatusCode());

		return response;
	}

	/**
	 * Delete the policy by policy Id
	 * 
	 * @author Kiran.Sripada
	 * @param admin_token
	 * @param policy_id
	 * @param expectedstatuscode
	 * @param expectederrorMessage
	 * @param test
	 * 
	 */
	public void deletePolicybyPolicyId(String admin_Token, String policy_id, int expectedstatuscode,
			SpogMessageCode expectederrorMessage, ExtentTest test) {
		test.log(LogStatus.INFO, "Prepare the URL for deletion of policy");
		Response response = policy4SPOGInvoker.deletePolicy(admin_Token, policy_id, test);
		spogServer.checkResponseStatus(response, expectedstatuscode, test);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "The policy is succesfully deleted");
		} else {
			String code = expectederrorMessage.getCodeString();
			String message = expectederrorMessage.getStatus();
			if (code.contains("00E00008")) {
				message = message.replace("{0}", policy_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}

	}

	/**
	 * get amount and policy type with check
	 * 
	 * @author leiyu.wang
	 * @param org_id
	 * @param backup_recovery_amount
	 * @param disaster_recovery_amount
	 * @param archiving_amount
	 * @param rha_amount
	 * @param test
	 */
	public void getAmountAndPolicyTypeWithCheck(String org_id, int backup_recovery_amount, int disaster_recovery_amount,
			int archiving_amount, int rha_amount, ExtentTest test) {
		test.log(LogStatus.INFO, "check amount for each Policy Type ");
		Response response = policy4SPOGInvoker.getAmountAndPolicyType(org_id);

		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		ArrayList<Integer> amount = response.then().extract().path("data.amount");
		if (amount.size() == 4) {
			if (amount.get(0) == backup_recovery_amount)
				test.log(LogStatus.INFO, "check amount for backup_recovery policy is correct ");
			else
				test.log(LogStatus.FAIL, "check amount for backup_recovery policy is wrong ");
			if (amount.get(1) == disaster_recovery_amount)
				test.log(LogStatus.INFO, "check amount for disaster_recovery policy is correct ");
			else
				test.log(LogStatus.FAIL, "check amount for disaster_recovery policy is wrong ");
			if (amount.get(2) == archiving_amount)
				test.log(LogStatus.INFO, "check amount for archiving policy is correct ");
			else
				test.log(LogStatus.FAIL, "check amount for archiving policy is wrong ");
			if (amount.get(3) == rha_amount)
				test.log(LogStatus.INFO, "check amount for rha policy is correct ");
			else
				test.log(LogStatus.FAIL, "check amount for rha policy is wrong ");
		} else
			test.log(LogStatus.FAIL, "fail to get all policy count");

	}

	/**
	 * get amount and policy type fail case
	 * 
	 * @author leiyu.wang
	 * @param org_id
	 * @param statusCode
	 * @param test
	 */
	public void getAmountforPolicyTypeFail(String org_id, int statusCode, ExtentTest test) {
		test.log(LogStatus.INFO, "check amount for each Policy Type ");
		Response response = policy4SPOGInvoker.getAmountAndPolicyType(org_id);
		spogServer.checkResponseStatus(response, statusCode);
	}

	/**
	 * get policy columns
	 * 
	 * @author shuo.zhang
	 * @param token
	 * @return
	 */
	public Response getPolicyColumns(String token) {

		return policy4SPOGInvoker.getPolicyColumns(token);

	}

	/**
	 * get policy columns and check
	 * 
	 * @author shuo.zhang
	 * @param token
	 * @param test
	 */
	public void getPolicyColumnsWithCheck(String token, int expectedStatusCode, String expectedErrorCode,
			ExtentTest test) {

		Response response = getPolicyColumns(token);
		checkGetPolicyColumns(response, expectedStatusCode, expectedErrorCode, test);
	}

	/**
	 * @author shuo.zhang
	 * @param response
	 * @param expectedStatusCode
	 * @param expectedErrorCode
	 * @param test
	 */
	public void checkGetPolicyColumns(Response response, int expectedStatusCode, String expectedErrorCode,
			ExtentTest test) {

		response.then().statusCode(expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap> expectedColumnsHeadContents = new ArrayList<HashMap>();
			HashMap<String, String> columnHeadContent1 = new HashMap();
			columnHeadContent1.put("long_label", PolicyColumnConstants.LONG_NAME);
			columnHeadContent1.put("short_label", PolicyColumnConstants.SHORT_NAME);
			columnHeadContent1.put("filter", PolicyColumnConstants.NAME_FILTER);
			columnHeadContent1.put("visible", "true");
			expectedColumnsHeadContents.add(columnHeadContent1);

			HashMap<String, String> columnHeadContent2 = new HashMap();
			columnHeadContent2.put("long_label", PolicyColumnConstants.LONG_STATUS);
			columnHeadContent2.put("short_label", PolicyColumnConstants.SHORT_STATUS);
			columnHeadContent2.put("filter", PolicyColumnConstants.STATUS_FILTER);
			columnHeadContent2.put("visible", "true");
			expectedColumnsHeadContents.add(columnHeadContent2);

			HashMap<String, String> columnHeadContent3 = new HashMap();
			columnHeadContent3.put("long_label", PolicyColumnConstants.LONG_PROTECTED_SOURCES);
			columnHeadContent3.put("short_label", PolicyColumnConstants.SHORT_PROTECTED_SOURCES);
			columnHeadContent3.put("filter", PolicyColumnConstants.PROTECTED_SOURCES_FILTER);
			columnHeadContent3.put("visible", "true");
			expectedColumnsHeadContents.add(columnHeadContent3);

			HashMap<String, String> columnHeadContent4 = new HashMap();
			columnHeadContent4.put("long_label", PolicyColumnConstants.LONG_NOT_PROTECTED_SOURCES);
			columnHeadContent4.put("short_label", PolicyColumnConstants.SHORT_NOT_PROTECTED_SOURCES);
			columnHeadContent4.put("filter", PolicyColumnConstants.NOT_PROTECTED_SOURCES_FILTER);
			columnHeadContent4.put("visible", "true");
			expectedColumnsHeadContents.add(columnHeadContent4);

			HashMap<String, String> columnHeadContent5 = new HashMap();
			columnHeadContent5.put("long_label", PolicyColumnConstants.LONG_POLICY_GROUP);
			columnHeadContent5.put("short_label", PolicyColumnConstants.SHORT_POLICY_GROUP);
			columnHeadContent5.put("filter", PolicyColumnConstants.POLICY_GROUP_FILTER);
			columnHeadContent5.put("visible", "true");
			expectedColumnsHeadContents.add(columnHeadContent5);

			HashMap<String, String> columnHeadContent7 = new HashMap();
			columnHeadContent7.put("long_label", PolicyColumnConstants.LONG_LATEST_JOB);
			columnHeadContent7.put("short_label", PolicyColumnConstants.SHORT_LATEST_JOB);
			columnHeadContent7.put("filter", PolicyColumnConstants.LATEST_JOB_FILTER);
			columnHeadContent7.put("visible", "true");
			expectedColumnsHeadContents.add(columnHeadContent7);

			HashMap<String, String> columnHeadContent6 = new HashMap();
			columnHeadContent6.put("long_label", PolicyColumnConstants.LONG_DESCRIPTION);
			columnHeadContent6.put("short_label", PolicyColumnConstants.SHORT_DESCRIPTION);
			columnHeadContent6.put("filter", PolicyColumnConstants.DESCRIPTION_FILTER);
			columnHeadContent6.put("visible", "false");
			expectedColumnsHeadContents.add(columnHeadContent6);

			spogServer.CompareColumnsHeadContent(response, expectedColumnsHeadContents, test);
		} else {
			spogServer.checkErrorCode(response, expectedErrorCode);
		}

	}

	/**
	 * @author shuo.zhang
	 * @param test
	 */
	public void getPolicyColumnsWithoutLoginWithCheck(ExtentTest test) {

		Response response = policy4SPOGInvoker.getPolicyColumnsWithoutLogin();
		checkGetPolicyColumns(response, SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK, test);
	}

	/**
	 * create specified user policy columns
	 * 
	 * @author shuo.zhang
	 * @param userId
	 * @param columnsList
	 * 
	 * @param token
	 * @param columnsList
	 * @return
	 */
	public Response createUsersPolicyColumns(String userId, ArrayList<HashMap<String, Object>> columnsList,
			String token) {

		errorHandle.printDebugMessageInDebugFile("******************createUsersPolicyColumns*******************");

		Map<String, Object> columnsInfo = jp.getUsersSourcesColumnsInfo(columnsList);
		return policy4SPOGInvoker.createUsersPolicyColumns(userId, columnsInfo, token);
	}

	/**
	 * 
	 * @param userId
	 * @param columnsList
	 * @param token
	 * @param statusCode
	 * @param errorCode
	 */
	public void createUsersPolicyColumnsWithCheck(String userId, ArrayList<HashMap<String, Object>> columnsList,
			String token, int statusCode, String errorCode) {

		errorHandle
				.printDebugMessageInDebugFile("******************createUsersPolicyColumnsWithCheck*******************");
		Response response = createUsersPolicyColumns(userId, columnsList, token);
		checkUsersPolicyColumns(response, columnsList, token, statusCode, errorCode);

	}

	/**
	 * get user policy column array list, it is used for preparing column info
	 * 
	 * @author shuo.zhang
	 * @param specifiedSourceColumns
	 *            column_short_name;visible;order_id ex:"Name;true;1,Status;true;2"
	 * @param token
	 * @return
	 */
	public ArrayList<HashMap<String, Object>> getPolicyColumnArrayList(String specifiedSourceColumns, String token,
			ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile("******************getPolicyColumnArrayList*******************");
		spogServer.setToken(token);
		ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>>();
		String[] specifiedSourceColumnArray = specifiedSourceColumns.split(",");
		Response response = policy4SPOGInvoker.getPolicyColumns(token);
		ArrayList<HashMap<String, Object>> sourceColumnsInfo = response.then().extract().path("data");

		for (int j = 0; j < specifiedSourceColumnArray.length; j++) {
			String eachSpecifiedSourceColumn = specifiedSourceColumnArray[j];
			String[] eachSpecifiedSourceColumnDetailInfo = eachSpecifiedSourceColumn.split(";");
			for (int i = 0; i < sourceColumnsInfo.size(); i++) {
				String key = String.valueOf(sourceColumnsInfo.get(i).get("short_label"));
				if (key.equalsIgnoreCase(eachSpecifiedSourceColumnDetailInfo[0])) {

					HashMap<String, Object> sourceColumnMap = source4SpogServer.getSourceColumnInfo(
							String.valueOf(sourceColumnsInfo.get(i).get("column_id")),
							eachSpecifiedSourceColumnDetailInfo[1], eachSpecifiedSourceColumnDetailInfo[2]);

					columnsList.add(sourceColumnMap);
					break;
				}
				if (i == sourceColumnsInfo.size() - 1) {
					errorHandle.printDebugMessageInDebugFile("can't find the policy column " + key);
					return null;
				}
			}
		}
		return columnsList;

	}

	/**
	 * @author shuo.zhang
	 * @param response
	 * @param columnsList
	 * @param token
	 * @param statusCode
	 * @param errorCode
	 */
	public void checkUsersPolicyColumns(Response response, ArrayList<HashMap<String, Object>> columnsList, String token,
			int statusCode, String errorCode) {

		errorHandle.printDebugMessageInDebugFile("******************checkUsersPolicyColumns*******************");
		response.then().statusCode(statusCode);

		/*
		 * if(statusCode == SpogConstants.SUCCESS_POST || statusCode ==
		 * SpogConstants.SUCCESS_GET_PUT_DELETE ){ ArrayList<HashMap<String, Object>>
		 * sourceColumnsInfo = response.then().extract().path("data");
		 * ArrayList<HashMap<String, Object>> expectedSourceColumns =
		 * getExpectedUserPolicyColumns(columnsList, token );
		 * assertEquals(expectedSourceColumns.size(), sourceColumnsInfo.size() );
		 * response.then().log().all();
		 * 
		 * for(int i=0; i<sourceColumnsInfo.size(); i++) { HashMap<String, Object>
		 * responseInfo = sourceColumnsInfo.get(i); String column_id =
		 * String.valueOf(responseInfo.get("column_id")); boolean find = false; for(int
		 * j=0; j<expectedSourceColumns.size();j++){ HashMap<String, Object>
		 * expectedSourceColumn = expectedSourceColumns.get(j);
		 * if(String.valueOf(expectedSourceColumn.get("column_id")).equalsIgnoreCase(
		 * column_id)){ //find find = true; Iterator<String> keyIter =
		 * responseInfo.keySet().iterator(); while(keyIter.hasNext()){ String key =
		 * keyIter.next(); System.out.println(key); Object expectedValue =
		 * expectedSourceColumn.get(key); if(expectedValue != null){
		 * assertEquals(responseInfo.get(key).toString(), expectedValue.toString());
		 * }else{ assertNull(responseInfo.get(key)); }
		 * 
		 * } break; } }
		 * 
		 * Assert.assertTrue(find);
		 * 
		 * }
		 * 
		 * 
		 * }else{ spogServer.checkErrorCode(response, errorCode); }
		 */

		if (statusCode == SpogConstants.SUCCESS_POST || statusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap<String, Object>> sourceColumnsInfo = response.then().extract().path("data");
			ArrayList<HashMap<String, Object>> expectedSourceColumns = getExpectedUserPolicyColumns(columnsList, token);
			response.then().log().all();

			for (int j = 0; j < expectedSourceColumns.size(); j++) {
				HashMap<String, Object> expectedSourceColumn = expectedSourceColumns.get(j);
				boolean find = false;
				for (int i = 0; i < sourceColumnsInfo.size(); i++) {
					HashMap<String, Object> responseInfo = sourceColumnsInfo.get(i);
					String column_id = String.valueOf(responseInfo.get("column_id"));

					if (String.valueOf(expectedSourceColumn.get("column_id")).equalsIgnoreCase(column_id)) {
						// find
						find = true;
						Iterator<String> keyIter = responseInfo.keySet().iterator();
						while (keyIter.hasNext()) {
							String key = keyIter.next();
							System.out.println(key);
							Object expectedValue = expectedSourceColumn.get(key);
							if (expectedValue != null) {
								assertEquals(responseInfo.get(key).toString(), expectedValue.toString());
							} else {
								assertNull(responseInfo.get(key));
							}

						}
						break;
					}
				}
				Assert.assertTrue(find);
			}

		} else {
			spogServer.checkErrorCode(response, errorCode);
		}

	}

	/**
	 * @author shuo.zhang
	 * @param columnsList
	 * @param token
	 * @return
	 */
	public ArrayList<HashMap<String, Object>> getExpectedUserPolicyColumns(
			ArrayList<HashMap<String, Object>> columnsList, String token) {

		errorHandle.printDebugMessageInDebugFile("******************getExpectedUserPolicyColumns*******************");
		spogServer.setToken(token);

		ArrayList<HashMap<String, Object>> expectedColumnsList = new ArrayList<HashMap<String, Object>>();

		Response response = getPolicyColumns(token);
		ArrayList<HashMap<String, Object>> sourceColumnsInfo = response.then().extract().path("data");

		for (int j = 0; j < columnsList.size(); j++) {
			HashMap<String, Object> eachSpecifiedSourceColumn = columnsList.get(j);
			String column_id = eachSpecifiedSourceColumn.get("column_id").toString();
			Iterator<HashMap<String, Object>> expectedIter = expectedColumnsList.iterator();
			boolean isExisted = false;
			while (expectedIter.hasNext()) {
				if (expectedIter.next().get("column_id").toString().equals(column_id)) {
					isExisted = true;
				}
			}
			if (!isExisted) {
				for (int i = 0; i < sourceColumnsInfo.size(); i++) {
					String key = String.valueOf(sourceColumnsInfo.get(i).get("column_id"));
					if (key.equalsIgnoreCase(eachSpecifiedSourceColumn.get("column_id").toString())) {
						HashMap<String, Object> sourceColumnMap = new HashMap<String, Object>();
						sourceColumnMap.put("column_id", String.valueOf(sourceColumnsInfo.get(i).get("column_id")));
						sourceColumnMap.put("long_label", String.valueOf(sourceColumnsInfo.get(i).get("long_label")));
						sourceColumnMap.put("short_label", String.valueOf(sourceColumnsInfo.get(i).get("short_label")));
						sourceColumnMap.put("key", String.valueOf(sourceColumnsInfo.get(i).get("key")));
						sourceColumnMap.put("sort", (boolean) sourceColumnsInfo.get(i).get("sort"));
						sourceColumnMap.put("filter", (boolean) sourceColumnsInfo.get(i).get("filter"));
						if (eachSpecifiedSourceColumn.containsKey("visible")) {
							sourceColumnMap.put("visible", eachSpecifiedSourceColumn.get("visible"));
						} else {
							sourceColumnMap.put("visible", sourceColumnsInfo.get(i).get("visible"));
						}
						if (eachSpecifiedSourceColumn.containsKey("order_id")) {
							sourceColumnMap.put("order_id", eachSpecifiedSourceColumn.get("order_id"));
						} else {
							sourceColumnMap.put("order_id", null);
						}

						expectedColumnsList.add(sourceColumnMap);
						break;
					}
					if (i == sourceColumnsInfo.size() - 1) {
						errorHandle.printDebugMessageInDebugFile("can't find the source column " + key);
						return null;
					}
				}
			}

		}

		return expectedColumnsList;
	}

	/**
	 * delete specified user policy columns with check
	 * 
	 * @author shuo.zhang
	 * @param userId
	 * @param token
	 * @param statusCode
	 * @param errorCode
	 */
	public void deleteUsersPolicyColumnsWithCheck(String userId, String token, int statusCode, String errorCode) {

		errorHandle
				.printDebugMessageInDebugFile("******************deleteUsersPolicyColumnsWithCheck*******************");
		Response response = deleteUsersPolicyColumns(userId, token);
		checkDeleteUsersPolicyColumns(response, statusCode, errorCode);
	}

	private void checkDeleteUsersPolicyColumns(Response response, int statusCode, String errorCode) {
		// TODO Auto-generated method stub
		response.then().statusCode(statusCode);
		if (statusCode != SpogConstants.SUCCESS_GET_PUT_DELETE) {
			spogServer.checkErrorCode(response, errorCode);
		}
	}

	/**
	 * @author shuo.zhang
	 * @param userId
	 * @param token
	 * @return
	 */
	private Response deleteUsersPolicyColumns(String userId, String token) {
		// TODO Auto-generated method stub
		errorHandle.printDebugMessageInDebugFile("******************deleteUsersPolicyColumns*******************");
		return policy4SPOGInvoker.deleteUsersPolicyColumns(userId, token);
	}

	/**
	 * get policies amount with check
	 * 
	 * @author leiyu.wang
	 * @param policyAmount
	 * @param test
	 */
	public void getPoliciesAmountWithCheck(int policyAmount, ExtentTest test) {
		test.log(LogStatus.INFO, "get policy amount and check");
		Response response = policy4SPOGInvoker.getPoliciesAmount();
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		int amount = response.then().extract().path("data.amount");
		if (policyAmount == amount)
			test.log(LogStatus.INFO, "check policies is correct");
		else
			test.log(LogStatus.FAIL, "check policies is not correct");
	}

	/**
	 * get policies amount fail case
	 * 
	 * @author leiyu.wang
	 * @param statusCode
	 * @param test
	 */
	public void getPoliciesAmountFail(int statusCode, ExtentTest test) {
		test.log(LogStatus.INFO, "get policies amount");
		Response response = policy4SPOGInvoker.getPoliciesAmount();
		spogServer.checkResponseStatus(response, statusCode);
	}

	/**
	 * @author shuo.zhang
	 * @param userId
	 */

	public void createUsersPolicyColumnsWithoutLoginAndCheck(String userId) {

		errorHandle.printDebugMessageInDebugFile(
				"******************createUsersPolicyColumnsWithoutLoginAndCheck*******************");

		ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>>();
		Map<String, Object> columnsInfo = jp.getUsersSourcesColumnsInfo(columnsList);
		Response response = policy4SPOGInvoker.createUsersPolicyColumnsWithoutLogin(userId, columnsInfo);
		checkUsersPolicyColumns(response, columnsList, null, SpogConstants.NOT_LOGGED_IN,
				ErrorCode.AUTHORIZATION_HEADER_BLANK);

	}

	/**
	 * @author shuo.zhang
	 * @param userId
	 * @param token
	 * @return
	 */
	public void deleteUsersPolicyColumnsWithoutLoginAndCheck(String userId) {
		// TODO Auto-generated method stub
		errorHandle.printDebugMessageInDebugFile(
				"******************deleteUsersPolicyColumnsWithoutLoginAndCheck*******************");
		Response response = policy4SPOGInvoker.deleteUsersPolicyColumnsWithoutLogin(userId);
		checkDeleteUsersPolicyColumns(response, SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK);
	}

	/**
	 * @author shuo.zhang
	 * @param userId
	 * @param columnsList
	 * @param token
	 * @return
	 */
	public Response updateUsersPolicyColumns(String userId, ArrayList<HashMap<String, Object>> columnsList,
			String token) {

		errorHandle.printDebugMessageInDebugFile("******************updateUsersPolicyColumns*******************");
		Map<String, Object> columnsInfo = jp.getUsersSourcesColumnsInfo(columnsList);
		return policy4SPOGInvoker.updateUsersPolicyColumns(userId, columnsInfo, token);
	}

	/**
	 * @author shuo.zhang
	 * @param userId
	 * @param columnsList
	 * @param token
	 * @param statusCode
	 * @param errorCode
	 */
	public void updateUsersPolicyColumnsWithCheck(String userId, ArrayList<HashMap<String, Object>> columnsList,
			String token, int statusCode, String errorCode) {

		errorHandle
				.printDebugMessageInDebugFile("******************updateUsersPolicyColumnsWithCheck*******************");
		Response response = updateUsersPolicyColumns(userId, columnsList, token);
		this.checkUsersPolicyColumns(response, columnsList, token, statusCode, errorCode);

	}

	/**
	 * @author shuo.zhang
	 * @param userId
	 */
	public void updateUsersPolicyColumnsWithoutLoginAndCheck(String userId) {

		errorHandle.printDebugMessageInDebugFile(
				"******************updateUsersPolicyColumnsWithoutLoginAndCheck*******************");
		ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>>();
		Map<String, Object> columnsInfo = jp.getUsersSourcesColumnsInfo(columnsList);
		Response response = policy4SPOGInvoker.updateUsersPolicyColumnsWithoutLogin(userId, columnsInfo);
		checkUsersPolicyColumns(response, columnsList, null, SpogConstants.NOT_LOGGED_IN,
				ErrorCode.AUTHORIZATION_HEADER_BLANK);

	}

	/**
	 * @author shuo.zhang
	 * @param userId
	 * @param token
	 * @return
	 */
	public Response getUsersPolicyColumns(String userId, String token) {

		errorHandle.printDebugMessageInDebugFile("******************getUsersPolicyColumns*******************");
		return policy4SPOGInvoker.getUsersPolicyColumns(userId, token);
	}

	/**
	 * @author shuo.zhang
	 * @param userId
	 * @param columnsList
	 * @param token
	 * @param statusCode
	 * @param errorCode
	 */
	public void getUsersPolicyColumnsWithCheck(String userId, ArrayList<HashMap<String, Object>> columnsList,
			String token, int statusCode, String errorCode) {

		errorHandle.printDebugMessageInDebugFile("******************getUsersPolicyColumnsWithCheck*******************");
		Response response = getUsersPolicyColumns(userId, token);
		checkUsersPolicyColumns(response, columnsList, token, statusCode, errorCode);
	}

	/**
	 * @author shuo.zhang
	 * @param userId
	 */
	public void getUsersPolicyColumnsWithoutLoginAndCheck(String userId) {

		errorHandle.printDebugMessageInDebugFile(
				"******************getUsersPolicyColumnsWithoutLoginAndCheck*******************");
		Response response = policy4SPOGInvoker.getUsersPolicyColumnsWithoutLogin(userId);
		checkUsersPolicyColumns(response, null, null, SpogConstants.NOT_LOGGED_IN,
				ErrorCode.AUTHORIZATION_HEADER_BLANK);

	}

	/**
	 * @author shuo.zhang
	 * @param userId
	 * @param columnsInfo
	 * @param token
	 * @return
	 */
	public Response createLoggedInUserPolicyColumns(ArrayList<HashMap<String, Object>> columnsList, String token) {

		errorHandle
				.printDebugMessageInDebugFile("******************createLoggedInUserPolicyColumns*******************");
		Map<String, Object> columnsInfo = jp.getUsersSourcesColumnsInfo(columnsList);
		Response response = policy4SPOGInvoker.createLoggedInUserPolicyColumns(columnsInfo, token);
		return response;
	}

	/**
	 * @author shuo.zhang
	 * 
	 * @param columnsList
	 * @param token
	 * @param statusCode
	 * @param errorCode
	 */
	public void createLoggedInUserPolicyColumnsWithCheck(ArrayList<HashMap<String, Object>> columnsList, String token,
			int statusCode, String errorCode) {

		errorHandle.printDebugMessageInDebugFile(
				"******************createLoggedInUserPolicyColumnsWithCheck*******************");
		Response response = createLoggedInUserPolicyColumns(columnsList, token);
		checkUsersPolicyColumns(response, columnsList, token, statusCode, errorCode);
	}

	/**
	 * @author shuo.zhang
	 */
	public void createLoggedInUserPolicyColumnsWithoutLoginAndCheck() {

		errorHandle.printDebugMessageInDebugFile(
				"******************createLoggedInUserPolicyColumnsWithoutLoginAndCheck*******************");
		ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>>();
		Map<String, Object> columnsInfo = jp.getUsersSourcesColumnsInfo(columnsList);
		Response response = policy4SPOGInvoker.createLoggedInUserPolicyColumnsWithoutLogin(columnsInfo);
		checkUsersPolicyColumns(response, columnsList, null, SpogConstants.NOT_LOGGED_IN,
				ErrorCode.AUTHORIZATION_HEADER_BLANK);

	}

	/**
	 * @author shuo.zhang
	 * @param token
	 * @return
	 */
	public Response deleteLoggedInUserPolicyColumns(String token) {

		errorHandle
				.printDebugMessageInDebugFile("******************deleteLoggedInUserPolicyColumns*******************");
		return policy4SPOGInvoker.deleteLoggedInUserPolicyColumns(token);
	}

	/**
	 * @author shuo.zhang
	 * @param token
	 * @param statusCode
	 * @param errorCode
	 */
	public void deleteLoggedInUserPolicyColumnsWithCheck(String token, int statusCode, String errorCode) {

		errorHandle.printDebugMessageInDebugFile(
				"******************deleteLoggedInUserPolicyColumnsWithCheck*******************");
		Response response = deleteLoggedInUserPolicyColumns(token);
		checkDeleteUsersPolicyColumns(response, statusCode, errorCode);
	}

	/**
	 * @author shuo.zhang
	 */
	public void deleteLoggedInUserPolicyColumnsWithoutLoginAndCheck() {

		errorHandle.printDebugMessageInDebugFile(
				"******************deleteLoggedInUserPolicyColumnsWithoutLoginAndCheck*******************");
		Response response = policy4SPOGInvoker.deleteLoggedInUserPolicyColumnsWithoutLogin();
		checkDeleteUsersPolicyColumns(response, SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK);
	}

	/**
	 * @author shuo.zhang
	 * @param columnsList
	 * @param token
	 * @return
	 */
	public Response updateLoggedInUserPolicyColumns(ArrayList<HashMap<String, Object>> columnsList, String token) {

		errorHandle
				.printDebugMessageInDebugFile("******************updateLoggedInUserPolicyColumns*******************");
		Map<String, Object> columnsInfo = jp.getUsersSourcesColumnsInfo(columnsList);
		return policy4SPOGInvoker.updateLoggedInUserPolicyColumns(columnsInfo, token);
	}

	/**
	 * @author shuo.zhang
	 * @param columnsList
	 * @param token
	 * @param statusCode
	 * @param errorCode
	 */
	public void updateLoggedInUserPolicyColumnsWithCheck(ArrayList<HashMap<String, Object>> columnsList, String token,
			int statusCode, String errorCode) {

		errorHandle.printDebugMessageInDebugFile(
				"******************updateLoggedInUserPolicyColumnsWithCheck*******************");
		Response response = updateLoggedInUserPolicyColumns(columnsList, token);
		this.checkUsersPolicyColumns(response, columnsList, token, statusCode, errorCode);

	}

	/**
	 * @author shuo.zhang
	 */
	public void updateLoggedInUserPolicyColumnsWithoutLoginAndCheck() {

		errorHandle.printDebugMessageInDebugFile(
				"******************updateLoggedInUserPolicyColumnsWithoutLoginAndCheck*******************");
		ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>>();
		Map<String, Object> columnsInfo = jp.getUsersSourcesColumnsInfo(columnsList);
		Response response = policy4SPOGInvoker.updateLoggedInUserPolicyColumnsWithoutLogin(columnsInfo);
		checkUsersPolicyColumns(response, columnsList, null, SpogConstants.NOT_LOGGED_IN,
				ErrorCode.AUTHORIZATION_HEADER_BLANK);

	}

	/**
	 * @author shuo.zhang
	 * 
	 * @param token
	 * @return
	 */
	public Response getLoggedInUserPolicyColumns(String token) {

		errorHandle.printDebugMessageInDebugFile("******************getLoggedInUserPolicyColumns*******************");
		return policy4SPOGInvoker.getLoggedInUserPolicyColumns(token);
	}

	/**
	 * @author shuo.zhang
	 * 
	 * @param columnsList
	 * @param token
	 * @param statusCode
	 * @param errorCode
	 */
	public void getLoggedInUserPolicyColumnsWithCheck(ArrayList<HashMap<String, Object>> columnsList, String token,
			int statusCode, String errorCode) {

		errorHandle.printDebugMessageInDebugFile(
				"******************getLoggedInUserPolicyColumnsWithCheck*******************");
		Response response = getLoggedInUserPolicyColumns(token);
		checkUsersPolicyColumns(response, columnsList, token, statusCode, errorCode);
	}

	/**
	 * @author shuo.zhang
	 */
	public void getLoggedInUserPolicyColumnsWithoutLoginAndCheck() {

		errorHandle.printDebugMessageInDebugFile(
				"******************getLoggedInUserPolicyColumnsWithoutLoginAndCheck*******************");
		Response response = policy4SPOGInvoker.getLoggedInUserPolicyColumnsWithoutLogin();
		checkUsersPolicyColumns(response, null, null, SpogConstants.NOT_LOGGED_IN,
				ErrorCode.AUTHORIZATION_HEADER_BLANK);

	}

	/**
	 * @author ramya.Nagepalli
	 * @param policy_id
	 * @param policy_name
	 * @param policy_description
	 * @param policy_type
	 * @param sla
	 * @param is_draft
	 * @param sources
	 * @param organization_id
	 * @param destinations
	 * @param throttles
	 * @param schedules
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * 
	 */
	public Response getPolicyById(String admin_Token, String policy_id, String policy_name, String policy_description,
			String policy_type, HashMap<String, Object> sla, String is_draft, String sources, String organization_id,
			ArrayList<HashMap<String, Object>> destinations, ArrayList<HashMap<String, Object>> throttles,
			ArrayList<HashMap<String, Object>> schedules, int expectedStatusCode, SpogMessageCode ExpectedErrorMessage,
			ExtentTest test) {

		Response response = policy4SPOGInvoker.getPolicyById(admin_Token, policy_id, test);
		test.log(LogStatus.INFO, "expect status code is " + expectedStatusCode);
		errorHandle.printInfoMessageInDebugFile("expect status code is " + expectedStatusCode);
		response.then().statusCode(expectedStatusCode);
		spogServer.checkResponseStatus(response, expectedStatusCode);
		// String Policy_id= response.then().extract().path("data.policy_id");
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			errorHandle.printDebugMessageInDebugFile("******************checkPolicyCommonValue*******************");

			response.then().body("data.policy_name", equalTo(policy_name))
					.body("data.policy_description", equalTo(policy_description))
					.body("data.policy_type", equalTo(policy_type)).body("data.sla", equalTo(sla))
					.body("data.policy_id", equalTo(policy_id)).body("data.organization_id", equalTo(organization_id));
			ArrayList<String> responseSources = response.then().extract().path("data.sources");
			ArrayList<HashMap<String, Object>> responseSourcesdata = response.then().extract().path("data.sources");

			if (sources == null) {

			} else {
				String[] sourcesStr = sources.split(",");
				int length = sourcesStr.length;
				if (length != responseSources.size()) {
					assertTrue("compare sources length passed", false);
				}
				for (int i = 0; i < length; i++) {
					if (responseSourcesdata.get(i).get("source_id").toString().equalsIgnoreCase(sourcesStr[i])) {

						Response response1 = spogServer.getSourceById(admin_Token, sourcesStr[i], test);
						HashMap<String, Object> expected_sourceInfo = response1.then().extract().path("data");
						HashMap<String, Object> expected_sourceInfo_os = response1.then().extract()
								.path("data.operating_system");
						HashMap<String, Object> expected_sourceInfo_Site = response1.then().extract().path("data.site");
						HashMap<String, Object> expected_sourceInfo_Hypervisor = response1.then().extract()
								.path("data.hypervisor");
						ArrayList<String> expected_SourceGroup = response1.then().extract().path("data.source_group");

						if (expected_sourceInfo.get("source_name").toString()
								.equals(responseSourcesdata.get(i).get("source_name").toString())
								&& expected_sourceInfo.get("source_type").toString()
										.equals(responseSourcesdata.get(i).get("source_type").toString())) {

							test.log(LogStatus.PASS, "Compare source information succesfull" + sourcesStr[i]);

							assertTrue("compare source Information " + sourcesStr[i] + "Pass", true);
						}
					} else {
						assertTrue("compare source " + sourcesStr[i] + "failed", false);
					}
				}
			}

			errorHandle.printDebugMessageInDebugFile("******************checkPolicyDestinations*******************");

			errorHandle.printDebugMessageInDebugFile(response.then().log().body().toString());

			test.log(LogStatus.INFO, "Validation Policy destinations is Success ");

			errorHandle.printDebugMessageInDebugFile("******************checkPolicyThrottles*******************");

			test.log(LogStatus.INFO, "check Policy throttles ");
			ArrayList<HashMap<String, Object>> act_throttles = response.then().extract().path("data.throttles");
			if (!(act_throttles == null) && !(act_throttles.isEmpty())) {
				for (int i = 0; i < throttles.size(); i++) {
					test.log(LogStatus.INFO, "check create_ts");
					response.then()
							.body("data.throttles[" + i + "].throttle_id", equalTo(throttles.get(i).get("throttle_id")))
							.body("data.throttles[" + i + "].task_id", equalTo(throttles.get(i).get("task_id")))
							.body("data.throttles[" + i + "].throttle_type",
									equalTo(throttles.get(i).get("throttle_type")))
							.body("data.throttles[" + i + "].rate", equalTo(throttles.get(i).get("rate")))
							.body("data.throttles[" + i + "].days_of_week",
									equalTo(throttles.get(i).get("days_of_week")))
							.body("data.throttles[" + i + "].start_time", equalTo(throttles.get(i).get("start_time")))
							.body("data.throttles[" + i + "].end_time", equalTo(throttles.get(i).get("end_time")));

				}
				test.log(LogStatus.INFO, "Validation Policy throttles is Success ");
			}

			errorHandle.printDebugMessageInDebugFile("******************checkPolicySchedules*******************");

			errorHandle.printDebugMessageInDebugFile(response.then().log().body().toString());

			HashMap<String, HashMap<String, Object>> Act_sch = response.then().extract()
					.path("data.schedules[" + 0 + "].schedule");

			HashMap<String, HashMap<String, Object>> schedule;
			schedule = (HashMap<String, HashMap<String, Object>>) schedules.get(0).get("schedule");

			HashMap<String, Object> act_custom_schedule = Act_sch.get("custom_schedule");

			if (!(act_custom_schedule == null)) {
				if (Act_sch.toString().equals(schedule.toString())) {
					test.log(LogStatus.INFO, "check Custom Schedule got passed");
				}
				for (int i = 0; i < schedules.size(); i++) {
					test.log(LogStatus.INFO, "check create_ts");
					response.then()
							.body("data.schedules[" + i + "].schedule_id", equalTo(schedules.get(i).get("schedule_id")))
							.body("data.schedules[" + i + "].schedule_type",
									equalTo(schedules.get(i).get("schedule_type")))
							.body("data.schedules[" + i + "].task_id", equalTo(schedules.get(i).get("task_id")))
							.body("data.schedules[" + i + "].destination_id",
									equalTo(schedules.get(i).get("destination_id")))
							.body("data.schedules[" + i + "].start_time", equalTo(schedules.get(i).get("start_time")))
							.body("data.schedules[" + i + "].end_time", equalTo(schedules.get(i).get("end_time")));
				}
			} else {
				for (int i = 0; i < schedules.size(); i++) {
					test.log(LogStatus.INFO, "check create_ts");
					response.then()
							.body("data.schedules[" + i + "].schedule_id", equalTo(schedules.get(i).get("schedule_id")))
							.body("data.schedules[" + i + "].schedule_type",
									equalTo(schedules.get(i).get("schedule_type")))
							.body("data.schedules[" + i + "].task_id", equalTo(schedules.get(i).get("task_id")))
							.body("data.schedules[" + i + "].destination_id",
									equalTo(schedules.get(i).get("destination_id")))
							.body("data.schedules[" + i + "].schedule", equalTo(schedules.get(i).get("schedule")))
							.body("data.schedules[" + i + "].start_time", equalTo(schedules.get(i).get("start_time")))
							.body("data.schedules[" + i + "].end_time", equalTo(schedules.get(i).get("end_time")));

				}
				test.log(LogStatus.INFO, "Validation Policy schedules is Success ");
			}
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("00E00008")) {
				message = message.replace("{0}", policy_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
		return response;
	}

	/**
	 * @author ramya.Nagepalli
	 */
	public Response getPolicyById(String admin_Token, String policy_id, ExtentTest test) {

		Response response = policy4SPOGInvoker.getPolicyById(admin_Token, policy_id, test);

		return response;
	}

	/**
	 * assign policy to sources
	 * 
	 * @author shan.jing
	 * @param policyId
	 * @param sources
	 * @return
	 */
	public Response assignPolicy(String policyId, String sources) {

		errorHandle.printDebugMessageInDebugFile("******************assignPolicy*******************");
		String[] assignPolicyInfo = null;
		if (sources == null || sources.equalsIgnoreCase("")) {
			return policy4SPOGInvoker.assignPolicy(policyId, null);
		} else {
			assignPolicyInfo = jp.getAssignPolicyInfo(sources);
		}
		return policy4SPOGInvoker.assignPolicy(policyId, assignPolicyInfo);
	}

	public void assignPolicy(String policyId, String sources, int expect_status, String error_code,
			String error_message) {

		errorHandle.printDebugMessageInDebugFile("******************assignPolicy*******************");
		String[] assignPolicyInfo = null;
		Response response = null;
		if (sources == null || sources.equalsIgnoreCase("")) {
			response = policy4SPOGInvoker.assignPolicy(policyId, null);
		} else {
			assignPolicyInfo = jp.getAssignPolicyInfo(sources);
			response = policy4SPOGInvoker.assignPolicy(policyId, assignPolicyInfo);
		}
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	/**
	 * REST API to call the post policies under policy type for sources
	 * 
	 * @author shan.jing
	 * @param sources
	 * @param test
	 * @return response
	 */
	public Response postPoliciesByTypeUnderSources(String sources, ExtentTest test) {

		errorHandle.printDebugMessageInDebugFile("******************postPoliciesByTypeUnderSources*******************");
		String[] sourcesInfo = null;
		if (sources == null || sources.equalsIgnoreCase("")) {
			return policy4SPOGInvoker.postPoliciesByTypeUnderSources(null, test);
		} else {
			sourcesInfo = jp.getAssignPolicyInfo(sources);
		}
		return policy4SPOGInvoker.postPoliciesByTypeUnderSources(sourcesInfo, test);
	}

	/**
	 * REST API to call the getPoliciesByTypeUnderSource
	 * 
	 * @author shan.jing
	 * @param source_id
	 * @param test
	 * @return response
	 */
	public Response getPoliciesByTypeUnderSource(String source_id, ExtentTest test) {
		errorHandle.printDebugMessageInDebugFile("******************getPoliciesByTypeUnderSource*******************");
		return policy4SPOGInvoker.getPoliciesByTypeUnderSource(source_id, test);
	}

	/**
	 * unassign policy from sources
	 * 
	 * @author shan.jing
	 * @param policyId
	 * @param sources
	 * @return
	 */
	public Response unassignPolicy(String policyId, String sources) {
		errorHandle.printDebugMessageInDebugFile("******************unassignPolicy*******************");
		String[] unassignPolicyInfo = null;
		if (sources == null || sources.equalsIgnoreCase("")) {
			return policy4SPOGInvoker.unassignPolicy(policyId, null);
		} else {
			unassignPolicyInfo = jp.getUnAssignPolicyInfo(sources);
		}
		return policy4SPOGInvoker.unassignPolicy(policyId, unassignPolicyInfo);
	}

	public void unassignPolicy(String policyId, String sources, int expect_status, String error_code,
			String error_message) {
		Response response = null;
		errorHandle.printDebugMessageInDebugFile("******************unassignPolicy*******************");
		String[] unassignPolicyInfo = null;
		if (sources == null || sources.equalsIgnoreCase("")) {
			response = policy4SPOGInvoker.unassignPolicy(policyId, null);
		} else {
			unassignPolicyInfo = jp.getUnAssignPolicyInfo(sources);
			response = policy4SPOGInvoker.unassignPolicy(policyId, unassignPolicyInfo);
		}
		response.then().statusCode(expect_status);
		checkErrorCode(response, error_code);
		checkErrorMessage(response, error_message);
	}

	/**
	 * REST API to remove source from policy
	 * 
	 * @author shan.jing
	 * @param source_id
	 * @param response
	 * @return
	 */
	public Response removeSourceFromPolicy(String source_id, ExtentTest test) {
		test.log(LogStatus.INFO, "remove source id from policy");
		Response response = policy4SPOGInvoker.removeSourceFromPolicy(source_id);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
	}

	/**
	 * REST API to get source under org's policy
	 * 
	 * @author shan.jing
	 * @param org_id
	 * @param policy_type
	 * @param response
	 * @return
	 */
	public Response getSourcesUnderOrgByPolicyType(String org_id, String policy_type, ExtentTest test) {
		test.log(LogStatus.INFO, "get source under org's policy");
		Response response = policy4SPOGInvoker.getSourcesUnderOrgByPolicyType(org_id, policy_type);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
	}

	/**
	 * REST API to get source under org's policy
	 * 
	 * @author shan.jing
	 * @param org_id
	 * @param policy_type
	 * @param response
	 * @return
	 */
	public Response getSourcesUnderOrgByPolicyTypeWithoutCheck(String org_id, String policy_type, ExtentTest test) {
		test.log(LogStatus.INFO, "get source under org's policy");
		Response response = policy4SPOGInvoker.getSourcesUnderOrgByPolicyType(org_id, policy_type);
		return response;
	}

	/**
	 * REST API to get source under org's policy
	 * 
	 * @author shan.jing
	 * @param org_id
	 * @param policy_type
	 * @param response
	 * @return
	 */
	public void checkSourcesUnderOrgByPolicyType(Response res, String org_id, String org_name, String policy_name,
			String policy_id, String source_id, String source_name, ExtentTest test) {
		// ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,
		// Object>>();
		ArrayList<HashMap<String, Object>> data = res.then().extract().path("data");
		if (data == null) {
			assertNotNull(data, "get source is not right, it's null");
		} else {
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).get("source_id").toString().equalsIgnoreCase(source_id)) {
					res.then().body("data[" + i + "].policy[0].policy_id", equalTo(policy_id))
							.body("data[" + i + "].organization_name", equalTo(org_name))
							.body("data[" + i + "].organization_id", equalTo(org_id))
							.body("data[" + i + "].policy[0].policy_name", equalTo(policy_name))
							.body("data[" + i + "].source_name", equalTo(source_name));
					return;
				}
			}
			assertNotNull(data, "get source is not right, no matched");
		}
	}

	public void checkSourcesUnderOrgByPolicyType(Response res, String org_id, String org_name, String source_id,
			String source_name, ExtentTest test) {
		// ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,
		// Object>>();
		ArrayList<HashMap<String, Object>> data = res.then().extract().path("data");
		if (data == null) {
			assertNotNull(data, "get source is not right, it's null");
		} else {
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).get("source_id").toString().equalsIgnoreCase(source_id)) {
					res.then().body("data[" + i + "].organization_name", equalTo(org_name))
							.body("data[" + i + "].organization_id", equalTo(org_id))
							.body("data[" + i + "].policy", equalTo(null))
							.body("data[" + i + "].source_name", equalTo(source_name));
					return;
				}
			}
			assertNotNull(data, "get source is not right, no matched");
		}
	}

	/**
	 * create ScheduleSettingDTO
	 * 
	 * @param cloud_direct_schedule
	 * @param custom_schedule
	 * @param test
	 * @return scheduleSettingDTO
	 */
	public HashMap<String, Object> createScheduleSettingDTO(HashMap<String, Object> cloud_direct_schedule,
			HashMap<String, Object> custom_schedule, HashMap<String, Object> ar_schedule,
			HashMap<String, Object> merge_schedule, HashMap<String, Object> replication_schedule, ExtentTest test) {

		HashMap<String, Object> scheduleSettingDTO = null;
		scheduleSettingDTO = pp.composeScheduleSettingDTO(cloud_direct_schedule, custom_schedule, ar_schedule,
				merge_schedule, replication_schedule);
		return scheduleSettingDTO;
	}

	/**
	 * create PolicyTaskDTO
	 * 
	 * @author Ramya.Nagepalli
	 * @param task_id
	 * @param task_type
	 * @param destination_id
	 * @param parent_id
	 * @param cloud_direct_image_backup
	 * @param cloud_direct_file_backup
	 * @param udp_replication_from_remote
	 * @param udp_replication_to_remote
	 * @param cloud_direct_sql_server_backup,
	 *            HashMap<String, Object>
	 * @param cloud_direct_vmware_backup,
	 *            HashMap<String, Object>
	 * @param test
	 * @return policyTaskDTO
	 */
	public ArrayList<HashMap<String, Object>> createPolicyTaskDTO(String destination_name,
			ArrayList<HashMap<String, Object>> destinations, String task_id, String task_type, String destination_id,
			String parent_id, HashMap<String, Object> cloud_direct_image_backup,
			HashMap<String, Object> cloud_direct_file_backup, HashMap<String, Object> udp_replication_from_remote,
			HashMap<String, Object> udp_replication_to_remote, HashMap<String, Object> cloud_direct_sql_server_backup,
			HashMap<String, Object> cloud_direct_vmware_backup, String destination_type, ExtentTest test) {

		HashMap<String, Object> policyTaskDTO = null;
		policyTaskDTO = pp.composePolicyTaskDTO(destination_name, task_id, task_type, destination_id, parent_id,
				cloud_direct_image_backup, cloud_direct_file_backup, udp_replication_from_remote,
				udp_replication_to_remote, cloud_direct_sql_server_backup, cloud_direct_vmware_backup,
				destination_type);
		if (destinations == null) {
			destinations = new ArrayList<HashMap<String, Object>>();
		}
		destinations.add(policyTaskDTO);
		return destinations;
	}

	/**
	 * create PolicyTaskDTO Included hypervisor id in request payload
	 * 
	 * @author Ramya.Nagepalli
	 * @param policy_name
	 * @param policy_description
	 * @param policy_type
	 * @param sla
	 * @param is_draft
	 * @param throttle
	 * @param sources
	 * @param destinations
	 * @param schedules
	 * @param policy_id
	 * @param organization_id
	 * @param hypervisor_id
	 * @param test
	 * @return policyCreateRequest Response
	 * @throws JsonProcessingException
	 */
	public Response createPolicy(String policy_name, String policy_description, String policy_type, Boolean is_draft,
			String sources, ArrayList<HashMap<String, Object>> destinations,
			ArrayList<HashMap<String, Object>> schedules, ArrayList<HashMap<String, Object>> throttles,
			String policy_id, String organization_id, String hypervisor_id, ExtentTest test) {

		Map<String, Object> policyCreateRequest = null;
		policyCreateRequest = pp.composePolicyCreateRequestDTO(policy_name, policy_description, policy_type, is_draft,
				throttles, sources, destinations, schedules, policy_id, organization_id, hypervisor_id);

		Response createPolicy = policy4SPOGInvoker.createPolicy(policyCreateRequest);
		return createPolicy;
	}

	/**
	 * getSourcesOfPolicyTypes
	 * 
	 * @author Ramya.Nagepalli
	 * @param validToken
	 * @param organization_id
	 * @param policyType
	 * @param filter
	 * @param expectedSources
	 * @param test
	 */
	public void getSourcesOfPolicyTypes(String validToken, String organization_id, String policyType, String filter,
			ArrayList<HashMap> expectedSources, int ExpectedStatusCode, SpogMessageCode ExpectedErrorMessage,
			ExtentTest test) {

		test.log(LogStatus.INFO, "get Sources under org by policy type ");
		Response response = policy4SPOGInvoker.getSourcesUnderOrgByPolicyType(validToken, organization_id, policyType,
				filter);

		if (ExpectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.INFO, "check the response status");
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

			ArrayList<HashMap> ActualSources = new ArrayList<>();
			ActualSources = response.then().extract().path("data");

			test.log(LogStatus.INFO, "validate the sources of policy_type : " + policyType);
			validateSources(expectedSources, ActualSources, test);

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + ExpectedStatusCode);

		}

	}

	/**
	 * validateSources
	 * 
	 * @author Ramya.Nagepalli
	 * @param expectedSources
	 * @param actualSources
	 * @param test
	 */
	private void validateSources(ArrayList<HashMap> expectedSources, ArrayList<HashMap> actualSources,
			ExtentTest test) {

		test.log(LogStatus.INFO, "Compare the Actual Sources with the Expected");

		if (!expectedSources.isEmpty() && !actualSources.isEmpty())
			for (int i = 0; i < expectedSources.size(); i++) {

				for (int j = 0; j < actualSources.size(); j++) {

					if (expectedSources.get(i).get("source_id").toString()
							.equals(actualSources.get(j).get("source_id").toString())) {
						String act_source_name = actualSources.get(j).get("source_name").toString();
						String exp_source_name = expectedSources.get(i).get("source_name").toString();
						spogServer.assertResponseItem(act_source_name, exp_source_name);

						String act_source_type = actualSources.get(j).get("source_type").toString();
						String exp_source_type = expectedSources.get(i).get("source_type").toString();
						spogServer.assertResponseItem(act_source_type, exp_source_type);

						String act_source_product = actualSources.get(j).get("source_product").toString();
						String exp_source_product = expectedSources.get(i).get("source_product").toString();
						spogServer.assertResponseItem(act_source_product, exp_source_product);

						String act_organization_id = actualSources.get(j).get("organization_id").toString();
						String exp_organization_id = expectedSources.get(i).get("organization_id").toString();
						spogServer.assertResponseItem(act_organization_id, exp_organization_id);

						String act_organization_name = actualSources.get(j).get("organization_name").toString();
						String exp_organization_name = expectedSources.get(i).get("organization_name").toString();
						spogServer.assertResponseItem(act_organization_name, exp_organization_name);

						String act_protection_status = actualSources.get(j).get("protection_status").toString();
						String exp_protection_status = expectedSources.get(i).get("protection_status").toString();
						spogServer.assertResponseItem(act_protection_status, exp_protection_status);

						String act_connection_status = actualSources.get(j).get("connection_status").toString();
						String exp_connection_status = expectedSources.get(i).get("connection_status").toString();
						spogServer.assertResponseItem(act_connection_status, exp_connection_status);

						if (actualSources.get(j).containsKey("source_group")) {
							String act_source_group = actualSources.get(j).get("source_group").toString();
							String exp_source_group = null;

							exp_source_group = expectedSources.get(i).get("source_group").toString();
							spogServer.assertResponseItem(act_source_group, exp_source_group);
						}

						String act_operating_system = actualSources.get(j).get("operating_system").toString();
						String exp_operating_system = expectedSources.get(i).get("operating_system").toString();
						spogServer.assertResponseItem(act_operating_system, exp_operating_system);

						spogServer.assertResponseItem(actualSources.get(j).get("hypervisor"),
								expectedSources.get(i).get("hypervisor"), test);

						String act_policy = actualSources.get(j).get("policy").toString();
						String exp_policy = expectedSources.get(i).get("policy").toString();
						spogServer.assertResponseItem(act_policy, exp_policy);

						spogServer.assertResponseItem(actualSources.get(i).get("available_actions"),
								expectedSources.get(i).get("available_actions"), test);
					}
				}

			}

	}

	/**
	 * getPolicies
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param filter
	 * @return
	 */
	public Response getPolicies(String token, String filter) {
		return policy4SPOGInvoker.getPolicies(token, filter);
	}

	/**
	 * Call API - GET: /policies with specified filter and validate status code
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 * @param token
	 * @param filter
	 * @param expectedStatusCode
	 * @param test
	 * @return
	 */
	public Response getPolicies(String token, String filter, int expectedStatusCode, ExtentTest test) {

		test.log(LogStatus.INFO, "Call API - GET: /policies with specified filter");
		Response response = policy4SPOGInvoker.getPolicies(token, filter, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		return response;
	}

	/**
	 * postDeployPolicyById - deploy policy configuration with policy_id
	 * 
	 * @author Ramya.Nagepalli
	 * @param validToken
	 * @param policy_id
	 * @param act_response
	 * @param expectedStatusCode
	 * @param errorMessage
	 * @param test
	 * @return
	 */
	public Response postDeployPolicyById(String validToken, String policy_id, int ExpectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, HashMap<String, Object> exp_response, ExtentTest test) {

		test.log(LogStatus.INFO, "get Sources under org by policy type ");
		Response response = policy4SPOGInvoker.postDeployPolicyById(validToken, policy_id, test);

		if (ExpectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			HashMap<String, Object> act_response = response.then().extract().path("data");
			spogServer.checkResponseStatus(response, ExpectedStatusCode);
			validatePolicies(exp_response, act_response, test);
			test.log(LogStatus.PASS, "deployment for the policies has been sent successfully");

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + ExpectedStatusCode);

		}
		return response;
	}

	/**
	 * validatePoliciesInfo
	 * 
	 * @author Ramya.Nagepalli
	 * @param exp_response
	 * @param act_response
	 * @param test
	 */
	private void validatePolicies(HashMap<String, Object> exp_response, HashMap<String, Object> act_response,
			ExtentTest test) {
		String policy_status = (String) act_response.get("policy_status");

		if (policy_status.equals("deploying"))
			test.log(LogStatus.PASS, "actual policy data matched with expected");
		else if (exp_response.equals(act_response))
			test.log(LogStatus.PASS, "policy info matched");
		else
			assertFalse(true, "actual policy data not matched with expected");
	}

	/**
	 * postDeploySourceById - deploy policy configuration with source_id
	 * 
	 * @author Ramya.Nagepalli
	 * @param validToken
	 * @param source_id
	 * @param act_response
	 * @param expectedStatusCode
	 * @param errorMessage
	 * @param test
	 */
	public void postDeploySourceById(String validToken, String source_id, int ExpectedStatusCode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		test.log(LogStatus.INFO, "get Sources under org by policy type ");
		Response response = policy4SPOGInvoker.postDeploySourceById(validToken, source_id, test);

		if (ExpectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			spogServer.checkResponseStatus(response, ExpectedStatusCode);
			test.log(LogStatus.PASS, "deployment for the sources has been sent successfully");

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + ExpectedStatusCode);

		}
	}

	/**
	 * create PolicyTaskDTO
	 * 
	 * @author Ramya.Nagepalli
	 * @param policy_name
	 * @param policy_description
	 * @param policy_type
	 * @param sla
	 * @param is_draft
	 * @param throttle
	 * @param sources
	 * @param destinations
	 * @param schedules
	 * @param policy_id
	 * @param organization_id
	 * @param hypervisor_id
	 * @param test
	 * @return policyCreateRequest Response
	 * @throws JsonProcessingException
	 */
	public Response createPolicy(String policy_name, String policy_description, String policy_type, String is_draft,
			String sources, ArrayList<HashMap<String, Object>> destinations,
			ArrayList<HashMap<String, Object>> schedules, ArrayList<HashMap<String, Object>> throttles,
			String policy_id, String organization_id, String hypervisor_id, ExtentTest test) {

		Map<String, Object> policyCreateRequest = null;
		policyCreateRequest = pp.composePolicyCreateRequestDTO(policy_name, policy_description, policy_type, is_draft,
				throttles, sources, destinations, schedules, policy_id, organization_id, hypervisor_id);

		Response createPolicy = policy4SPOGInvoker.createPolicy(policyCreateRequest);
		return createPolicy;
	}

	/**
	 * 
	 * getSharedPoliciesofLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param validtoken
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param expected_data
	 * @param test
	 * @return
	 */
	public void getSharedPoliciesofLoggedInUserWithCheck(String validtoken, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ArrayList<HashMap> expected_data, ExtentTest test) {

		Response response = getSharedPoliciesofLoggedInUser(validtoken, expectedStatusCode, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap> actual_data = new ArrayList<HashMap>();
			actual_data = response.then().extract().path("data");

			test.log(LogStatus.INFO, "compare the policies count with expected count");
			assertEquals(actual_data.size(), expected_data.size());

			actual_data.stream().forEach(act_policy -> {
				expected_data.stream().forEach(exp_policy -> {

					if (act_policy.get("policy_name").toString().equals(exp_policy.get("policy_name").toString())) {
						assertEquals(act_policy.get("policy_name").toString(),
								exp_policy.get("policy_name").toString());
						assertEquals(act_policy.get("policy_type").toString(),
								exp_policy.get("policy_type").toString());
						assertEquals(act_policy.get("policy_status").toString(),
								exp_policy.get("policy_status").toString());

						assertNotNull(
								"The response contains the policy_description:" + act_policy.get("policy_description"));
						assertNotNull("The response contains the policy_description:" + act_policy.get("usage_status"));
						assertNotNull(
								"The response contains the policy_description:" + act_policy.get("protected_sources"));
						assertNotNull("The response contains the policy_description:"
								+ act_policy.get("unprotected_sources"));
						assertNotNull("The response contains the policy_description:" + act_policy.get("source_group"));
						assertNotNull("The response contains the policy_description:" + act_policy.get("last_job"));
						assertNotNull("The response contains the policy_description:" + act_policy.get("hypervisor"));
						assertNotNull(
								"The response contains the policy_description:" + act_policy.get("rps_policy_id"));
						assertNotNull(
								"The response contains the policy_description:" + act_policy.get("available_actions"));

					}
				});
			});

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);

		}

	}

	/**
	 * 
	 * getSharedPoliciesofLoggedInUser
	 * 
	 * @author Ramya.Nagepalli
	 * @param validtoken
	 * @param test
	 * @return
	 */
	public Response getSharedPoliciesofLoggedInUser(String validtoken, int expectedStatusCode, ExtentTest test) {

		test.log(LogStatus.INFO, "get Shared policies of logged in user");
		Response response = policy4SPOGInvoker.getSharedPoliciesofLoggedInUser(validtoken, test);
		spogServer.checkResponseStatus(response, expectedStatusCode);
		return response;
	}

	/**
	 * enableSpecifiedOrgPolicies
	 * 
	 * Sprint 35 - API- POST :/organizations/{id}/policies/enable
	 * 
	 * @author Ramya.Nagepalli
	 * @param validtoken
	 * @param organization_id
	 * @param expectedStatusCode
	 * @param test
	 */
	public Response enableSpecifiedOrgPolicies(String validtoken, String organization_id, int expectedStatusCode,
			ExtentTest test) {
		test.log(LogStatus.INFO, "Enable Specified organization policies");

		Response response = policy4SPOGInvoker.enableSpecifiedOrgPolicies(validtoken, organization_id, test);
		spogServer.checkResponseStatus(response, expectedStatusCode);

		return response;
	}

	/**
	 * enableSpecifiedOrgPoliciesWithCheck
	 * 
	 * Sprint 35 - API- POST :/organizations/{id}/policies/enable
	 * 
	 * @author Ramya.Nagepalli
	 * @param validtoken
	 * @param organization_id
	 * @param ExpectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 * @return
	 */
	public Response enableSpecifiedOrgPoliciesWithCheck(String validtoken, String organization_id,
			int ExpectedStatusCode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		test.log(LogStatus.INFO, "Enable Specified organization policies");

		Response response = enableSpecifiedOrgPolicies(validtoken, organization_id, ExpectedStatusCode, test);

		if (ExpectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + ExpectedStatusCode);
		}
		return response;
	}

	/**
	 * disableSpecifiedOrgPolicies
	 * 
	 * Sprint 35 - API- POST :/organizations/{id}/policies/disable
	 * 
	 * @author Ramya.Nagepalli
	 * @param validtoken
	 * @param organization_id
	 * @param expectedStatusCode
	 * @param test
	 */
	public Response disableSpecifiedOrgPolicies(String validtoken, String organization_id, int expectedStatusCode,
			ExtentTest test) {
		test.log(LogStatus.INFO, "disable Specified organization policies");

		Response response = policy4SPOGInvoker.disableSpecifiedOrgPolicies(validtoken, organization_id, test);
		spogServer.checkResponseStatus(response, expectedStatusCode);

		return response;
	}

	/**
	 * disableSpecifiedOrgPoliciesWithCheck
	 * 
	 * Sprint 35 - API- POST :/organizations/{id}/policies/disable
	 * 
	 * @author Ramya.Nagepalli
	 * @param validtoken
	 * @param organization_id
	 * @param ExpectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 * @return
	 */
	public Response disableSpecifiedOrgPoliciesWithCheck(String validtoken, String organization_id,
			int ExpectedStatusCode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		test.log(LogStatus.INFO, "disable Specified organization policies");

		Response response = disableSpecifiedOrgPolicies(validtoken, organization_id, ExpectedStatusCode, test);

		if (ExpectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + ExpectedStatusCode);
		}
		return response;
	}

	public Response disablePolicyById(String token, String policy_id, int expectedStatusCode, ExtentTest test) {

		Response response = policy4SPOGInvoker.disablePolicyById(token, policy_id, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		return response;
	}

	public Response enablePolicyById(String token, String policy_id, int expectedStatusCode, ExtentTest test) {

		Response response = policy4SPOGInvoker.enablePolicyById(token, policy_id, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		return response;
	}

	/**
	 * Disable a specified policy by it's id and validate the response
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param policy_id
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void disablePolicyByIdWithCheck(String token, String policy_id, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		Response response = disablePolicyById(token, policy_id, expectedStatusCode, test);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			String policyUsageStatus = response.then().extract().path("data.usage_status").toString();
			spogServer.assertResponseItem("disabled", policyUsageStatus.toLowerCase(), test);
			test.log(LogStatus.PASS, "Policy with id: " + policy_id + " disabled successfully.");

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedErrorMessage);
		}
	}

	/**
	 * Enable a specified policy by it's id and validate the response
	 * 
	 * @author Rakesh.Chalamala
	 * @param token
	 * @param policy_id
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void enablePolicyByIdWithCheck(String token, String policy_id, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		Response response = enablePolicyById(token, policy_id, expectedStatusCode, test);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			String policyUsageStatus = response.then().extract().path("data.usage_status").toString();
			spogServer.assertResponseItem("enabled", policyUsageStatus.toLowerCase(), test);
			test.log(LogStatus.PASS, "Policy with id: " + policy_id + " enabled successfully.");
		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedErrorMessage);
		}
	}

	
}
