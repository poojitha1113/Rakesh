package InvokerServer;
import java.util.Iterator;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.plaf.ActionMapUIResource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.internal.EclipseInterface;

//import api.Datacenter;

import com.google.inject.PrivateBinder;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.CloudDirectRetentionValues;
import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import dataPreparation.JsonPreparation;
import genericutil.ErrorHandler;
import invoker.SPOGDestinationInvoker;
import invoker.SPOGHypervisorsInvoker;
import invoker.SPOGInvoker;
import io.restassured.filter.Filter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class SPOGHypervisorsServer {

	private static JsonPreparation jp = new JsonPreparation();
	private SPOGServer spogServer;
	private SPOGHypervisorsInvoker spogHypervisorsInvoker;
	public static ErrorHandler errorHandle = ErrorHandler.getErrorHandler();
	private ExtentTest test;

	private ArrayList<HashMap<String, Object>> hypervisorComposeInfo = new ArrayList<>(); 
	
	public void setToken(String token) {
		spogHypervisorsInvoker.setToken(token);
	}

	public SPOGHypervisorsServer(String baseURI, String port) {	
		spogHypervisorsInvoker = new SPOGHypervisorsInvoker(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
	}

	/*
	 * Stores the compose hypervisor info
	 * @author Rakesh.Chalamala
	 */	
	public void setHypervisorComposeInfo(Map<String, Object> hypervisorInfo) {
		
		this.hypervisorComposeInfo.add((HashMap<String, Object>) hypervisorInfo);
	}
	
	public ArrayList<HashMap<String, Object>> getHypervisorComposeInfo(){
		
		return this.hypervisorComposeInfo;
	}
	
	public Map<String, Object> composeHypervisorInfo(String hypervisor_id, String hypervisor_name, String hypervisor_type, String hypervisor_product, String status, 
			String site, String organization_id, 
			String sync_discovered_vms, String vm_refresh_ts, String cloud_direct_baas_destination, String cloud_direct_draas_destination, String schedule, String schedule_type, 
			String agent_name, String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade,
			String vmware_api_version, String vmware_vcenter_host, String vmware_resource_pools, String vmware_data_stores, String vmware_data_centers, String vmware_host_systems,
			String bind_datacenter, String bind_host
			) {
		Map<String, Object> hypervisorInfo = new HashMap<String, Object>();
		if (hypervisor_id == null) {
			hypervisorInfo.put("hypervisor_id", hypervisor_id);
		} else if (!hypervisor_id.equalsIgnoreCase("none")) {
			hypervisorInfo.put("hypervisor_id", hypervisor_id);
		}
		if (hypervisor_name == null) {
			hypervisorInfo.put("hypervisor_name", hypervisor_name);
		} else if (!hypervisor_name.equalsIgnoreCase("none")) {
			hypervisorInfo.put("hypervisor_name", hypervisor_name);
		}
		if (hypervisor_type == null) {
			hypervisorInfo.put("hypervisor_type", hypervisor_type);
		} else if (!hypervisor_type.equalsIgnoreCase("none")) {
			hypervisorInfo.put("hypervisor_type", hypervisor_type);
		}
		if (hypervisor_product == null) {
			hypervisorInfo.put("hypervisor_product", hypervisor_product);
		} else if (!hypervisor_product.equalsIgnoreCase("none")) {
			hypervisorInfo.put("hypervisor_product", hypervisor_product);
		}
		if (status == null) {
			hypervisorInfo.put("status", status);
		} else if (!status.equalsIgnoreCase("none")) {
			hypervisorInfo.put("status", status);
		}
		if (site == null) {
			hypervisorInfo.put("site_id", site);
		} else if (!site.equalsIgnoreCase("none")) {
			hypervisorInfo.put("site_id", site);
		}
		if (organization_id == null) {
			hypervisorInfo.put("organization_id", organization_id);
		} else if (!organization_id.equalsIgnoreCase("none")) {
			hypervisorInfo.put("organization_id", organization_id);
		}
		
		String[] emptyArray = new String[0];
		
		Map<String, Object> agentInfo = composeAgentInfo(agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade);
		
		if (hypervisor_type == null) {
		} else if (hypervisor_type.equalsIgnoreCase("hyperv")) {
			Map<String, Object> hypervInfo = new HashMap<String, Object>();
			if (sync_discovered_vms == null) {
				hypervInfo.put("sync_discovered_vms", sync_discovered_vms);
			} else if (sync_discovered_vms.equalsIgnoreCase("true") || sync_discovered_vms.equalsIgnoreCase("false")) {
				hypervInfo.put("sync_discovered_vms", Boolean.valueOf(sync_discovered_vms));
			} else if (!sync_discovered_vms.equalsIgnoreCase("none")) {
				hypervInfo.put("sync_discovered_vms", sync_discovered_vms);
			}
			if (vm_refresh_ts == null) {
				hypervInfo.put("vm_refresh_ts", vm_refresh_ts);
			} else if (!vm_refresh_ts.equalsIgnoreCase("none")) {
				hypervInfo.put("vm_refresh_ts", vm_refresh_ts);
			}
			if (cloud_direct_baas_destination == null) {
				hypervInfo.put("cloud_direct_baas_destination", cloud_direct_baas_destination);
			} else if (!cloud_direct_baas_destination.equalsIgnoreCase("none")) {
				hypervInfo.put("cloud_direct_baas_destination", cloud_direct_baas_destination);
			}
			if (cloud_direct_draas_destination == null) {
				hypervInfo.put("cloud_direct_draas_destination", cloud_direct_draas_destination);
			} else if (!cloud_direct_draas_destination.equalsIgnoreCase("none")) {
				hypervInfo.put("cloud_direct_draas_destination", cloud_direct_draas_destination);
			}
			if (schedule == null) {
				hypervInfo.put("schedule", schedule);
			} else if (!schedule.equalsIgnoreCase("none")) {
				hypervInfo.put("schedule", schedule);
			}
			if (schedule_type == null) {
				hypervInfo.put("schedule_type", schedule_type);
			} else if (!schedule_type.equalsIgnoreCase("none")) {
				hypervInfo.put("schedule_type", schedule_type);
			}
			hypervInfo.put("agent", agentInfo);
			hypervisorInfo.put("cloud_direct_hyperv", hypervInfo);
			
			
		} else if (hypervisor_type.equalsIgnoreCase("vmware")) {
			Map<String, Object> vmwareInfo = new HashMap<String, Object>();
			if (sync_discovered_vms == null) {
				vmwareInfo.put("sync_discovered_vms", sync_discovered_vms);
			} else if (sync_discovered_vms.equalsIgnoreCase("true") || sync_discovered_vms.equalsIgnoreCase("false")) {
				vmwareInfo.put("sync_discovered_vms", Boolean.valueOf(sync_discovered_vms));
			} else if (!sync_discovered_vms.equalsIgnoreCase("none")) {
				vmwareInfo.put("sync_discovered_vms", sync_discovered_vms);
			}
			if (vm_refresh_ts == null) {
				vmwareInfo.put("vm_refresh_ts", vm_refresh_ts);
			} else if (!vm_refresh_ts.equalsIgnoreCase("none")) {
				vmwareInfo.put("vm_refresh_ts", vm_refresh_ts);
			}
			if (cloud_direct_baas_destination == null) {
				vmwareInfo.put("cloud_direct_baas_destination", cloud_direct_baas_destination);
			} else if (!cloud_direct_baas_destination.equalsIgnoreCase("none")) {
				vmwareInfo.put("cloud_direct_baas_destination", cloud_direct_baas_destination);
			}
			if (cloud_direct_draas_destination == null) {
				vmwareInfo.put("cloud_direct_draas_destination", cloud_direct_draas_destination);
			} else if (!cloud_direct_draas_destination.equalsIgnoreCase("none")) {
				vmwareInfo.put("cloud_direct_draas_destination", cloud_direct_draas_destination);
			}
			if (schedule == null) {
				vmwareInfo.put("schedule", schedule);
			} else if (!schedule.equalsIgnoreCase("none")) {
				vmwareInfo.put("schedule", schedule);
			}
			if (schedule_type == null) {
				vmwareInfo.put("schedule_type", schedule_type);
			} else if (!schedule_type.equalsIgnoreCase("none")) {
				vmwareInfo.put("schedule_type", schedule_type);
			}
			if (vmware_api_version == null) {
				vmwareInfo.put("vmware_api_version", vmware_api_version);
			} else if (!vmware_api_version.equalsIgnoreCase("none")) {
				vmwareInfo.put("vmware_api_version", vmware_api_version);
			}
			if (vmware_vcenter_host == null) {
				vmwareInfo.put("vmware_vcenter_host", vmware_vcenter_host);
			} else if (!vmware_vcenter_host.equalsIgnoreCase("none")) {
				vmwareInfo.put("vmware_vcenter_host", vmware_vcenter_host);
			}			
			if (vmware_resource_pools == null || vmware_resource_pools == "") {
				vmwareInfo.put("vmware_resource_pools", vmware_resource_pools);
			} else if (vmware_resource_pools.equalsIgnoreCase("emptyarray")) {
				vmwareInfo.put("vmware_resource_pools", emptyArray);
			} else if (!vmware_resource_pools.equalsIgnoreCase("none")) {
				vmwareInfo.put("vmware_resource_pools", vmware_resource_pools.replace(" ", "").split(","));
			}
			
			if (vmware_data_stores == null || vmware_data_stores == "") {
				vmwareInfo.put("vmware_data_stores", vmware_data_stores);
			} else if (vmware_data_stores.equalsIgnoreCase("emptyarray")) {
				vmwareInfo.put("vmware_data_stores", emptyArray);
			} else if (!vmware_data_stores.equalsIgnoreCase("none")) {
				vmwareInfo.put("vmware_data_stores", vmware_data_stores.replace(" ", "").split(","));
			}
			
			if (vmware_data_centers == null || vmware_data_centers == "") {
				vmwareInfo.put("vmware_data_centers", vmware_data_centers);
			} else if (vmware_data_centers.equalsIgnoreCase("emptyarray")) {
				vmwareInfo.put("vmware_data_centers", emptyArray);
			} else if (!vmware_data_centers.equalsIgnoreCase("none")) {
				vmwareInfo.put("vmware_data_centers", vmware_data_centers.replace(" ", "").split(","));
			}
			
			if (vmware_host_systems == null || vmware_host_systems == "") {
				vmwareInfo.put("vmware_host_systems", vmware_host_systems);
			} else if (vmware_host_systems.equalsIgnoreCase("emptyarray")) {
				vmwareInfo.put("vmware_host_systems", emptyArray);
			} else if (!vmware_host_systems.equalsIgnoreCase("none")) {
				vmwareInfo.put("vmware_host_systems", vmware_host_systems.replace(" ", "").split(","));
			}
			
			if (bind_datacenter == null) {
				vmwareInfo.put("bind_datacenter", bind_datacenter);
			} else if (!bind_datacenter.equalsIgnoreCase("none")) {
				vmwareInfo.put("bind_datacenter", bind_datacenter);
			}
			if (bind_host == null) {
				vmwareInfo.put("bind_host", bind_host);
			} else if (!bind_host.equalsIgnoreCase("none")) {
				vmwareInfo.put("bind_host", bind_host);
			}
					
			vmwareInfo.put("agent", agentInfo);
			hypervisorInfo.put("cloud_direct_vmware", vmwareInfo);
		}
		return hypervisorInfo;
	}

	public Map<String, Object> composeAgentInfo(String agent_name, String agent_current_version, String agent_upgrade_version,
			String agent_upgrade_link, String agent_autoupgrade) {
		Map<String, Object> agentInfo = new HashMap<String, Object>();
		if (agent_name == null) {
			agentInfo.put("agent_name", agent_name);
		} else if (!agent_name.equalsIgnoreCase("none")) {
			agentInfo.put("agent_name", agent_name);
		}
		if (agent_current_version == null) {
			agentInfo.put("agent_current_version", agent_current_version);
		} else if (!agent_current_version.equalsIgnoreCase("none")) {
			agentInfo.put("agent_current_version", agent_current_version);
		}
		if (agent_upgrade_version == null) {
			agentInfo.put("agent_upgrade_version", agent_upgrade_version);
		} else if (!agent_upgrade_version.equalsIgnoreCase("none")) {
			agentInfo.put("agent_upgrade_version", agent_upgrade_version);
		}
		if (agent_upgrade_link == null) {
			agentInfo.put("agent_upgrade_link", agent_upgrade_link);
		} else if (!agent_upgrade_link.equalsIgnoreCase("none")) {
			agentInfo.put("agent_upgrade_link", agent_upgrade_link);
		}
		if (agent_autoupgrade == null) {
			agentInfo.put("agent_autoupgrade", agent_autoupgrade);
		} else if (agent_autoupgrade.equalsIgnoreCase("true") || agent_autoupgrade.equalsIgnoreCase("false")) {
			agentInfo.put("agent_autoupgrade", Boolean.valueOf(agent_autoupgrade));
		} else if (!agent_autoupgrade.equalsIgnoreCase("none")) {
			agentInfo.put("agent_autoupgrade", agent_autoupgrade);
		}
		
		return agentInfo;
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param hypervisor_id
	 * @param hypervisor_name
	 * @param hypervisor_type
	 * @param hypervisor_product
	 * @param status
	 * @param site
	 * @param organization_id
	 * @param sync_discovered_vms
	 * @param vm_refresh_ts
	 * @param cloud_direct_baas_destination
	 * @param cloud_direct_draas_destination
	 * @param schedule
	 * @param schedule_type
	 * @param agent_name
	 * @param agent_current_version
	 * @param agent_upgrade_version
	 * @param agent_upgrade_link
	 * @param agent_autoupgrade
	 * @param vmware_api_version
	 * @param vmware_vcenter_host
	 * @param vmware_resource_pools
	 * @param vmware_data_stores
	 * @param vmware_data_centers
	 * @param vmware_host_systems
	 * @param bind_datacenter
	 * @param bind_host
	 * @param test
	 * @return
	 */
	public String createHypervisorWithCheck(String hypervisor_id, String hypervisor_name, String hypervisor_type, String hypervisor_product, String status, 
			String site, String organization_id, 
			String sync_discovered_vms, String vm_refresh_ts, String cloud_direct_baas_destination, String cloud_direct_draas_destination, String schedule, String schedule_type, 
			String agent_name, String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade,
			String vmware_api_version, String vmware_vcenter_host, String vmware_resource_pools, String vmware_data_stores, String vmware_data_centers, String vmware_host_systems,
			String bind_datacenter, String bind_host, ExtentTest test) {
		
		Map<String, Object> hypervisorInfo = composeHypervisorInfo(hypervisor_id, hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, 
				sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, 
				agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, 
				vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host);
		
		Response response = spogHypervisorsInvoker.createHypervisor(hypervisorInfo);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		
		/*
		 * @author Rakesh.Chalmala
		 * Used for storing the composed hypervisor info, which can be used for validating the Get hypervisors
		 */
		setHypervisorComposeInfo(hypervisorInfo);
		
		if (hypervisor_type.equalsIgnoreCase("hyperv")) {
			verifyHyperv(response, hypervisor_id, hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, 
					sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, 
					agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade);
		} else if (hypervisor_type.equalsIgnoreCase("vmware")) {
			verifyVmware(response, hypervisor_id, hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, 
					sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, 
					agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, 
					vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host);
		}
		
		String response_hypervisor_id = response.then().extract().path("data.hypervisor_id");
		return response_hypervisor_id;
	}
	
	public void createHypervisorWithCodeCheck(String hypervisor_id, String hypervisor_name, String hypervisor_type, String hypervisor_product, String status, 
			String site, String organization_id, 
			String sync_discovered_vms, String vm_refresh_ts, String cloud_direct_baas_destination, String cloud_direct_draas_destination, String schedule, String schedule_type, 
			String agent_name, String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade,
			String vmware_api_version, String vmware_vcenter_host, String vmware_resource_pools, String vmware_data_stores, String vmware_data_centers, String vmware_host_systems,
			String bind_datacenter, String bind_host, int status_code, String error_code, ExtentTest test) {
		
		Map<String, Object> hypervisorInfo = composeHypervisorInfo(hypervisor_id, hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, 
				sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, 
				agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, 
				vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host);
		
		Response response = spogHypervisorsInvoker.createHypervisor(hypervisorInfo);
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);
	
	}
	
	/**
	 * @author Zhaoguo.Ma
	 * @param hypervisor_id_url
	 * @param hypervisor_id
	 * @param hypervisor_name
	 * @param hypervisor_type
	 * @param hypervisor_product
	 * @param status
	 * @param site
	 * @param organization_id
	 * @param sync_discovered_vms
	 * @param vm_refresh_ts
	 * @param cloud_direct_baas_destination
	 * @param cloud_direct_draas_destination
	 * @param schedule
	 * @param schedule_type
	 * @param agent_name
	 * @param agent_current_version
	 * @param agent_upgrade_version
	 * @param agent_upgrade_link
	 * @param agent_autoupgrade
	 * @param vmware_api_version
	 * @param vmware_vcenter_host
	 * @param vmware_resource_pools
	 * @param vmware_data_stores
	 * @param vmware_data_centers
	 * @param vmware_host_systems
	 * @param bind_datacenter
	 * @param bind_host
	 * @param test
	 */
	public void updateHypervisorWithCheck(String hypervisor_id_url, String hypervisor_id, String hypervisor_name, String hypervisor_type, String hypervisor_product, String status, 
			String site, String organization_id, 
			String sync_discovered_vms, String vm_refresh_ts, String cloud_direct_baas_destination, String cloud_direct_draas_destination, String schedule, String schedule_type, 
			String agent_name, String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade,
			String vmware_api_version, String vmware_vcenter_host, String vmware_resource_pools, String vmware_data_stores, String vmware_data_centers, String vmware_host_systems,
			String bind_datacenter, String bind_host, ExtentTest test) {
		
		Map<String, Object> hypervisorInfo = composeHypervisorInfo(hypervisor_id, hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, 
				sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, 
				agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, 
				vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host);
		
		Response responseOri =  spogHypervisorsInvoker.getHypervisor(hypervisor_id);
		
		Response response = spogHypervisorsInvoker.updateHypervisor(hypervisor_id_url, hypervisorInfo);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		if (hypervisor_type.equalsIgnoreCase("hyperv")) {
			verifyHypervUpdate(responseOri, response, hypervisor_id, hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, 
					sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, 
					agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade);
		} else if (hypervisor_type.equalsIgnoreCase("vmware")) {
			verifyVmwareUpdate(responseOri, response, hypervisor_id, hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, 
					sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, 
					agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, 
					vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host);
		}
	}
	
	public Response updateHypervisorResponse(String hypervisor_id_url, String hypervisor_id, String hypervisor_name, String hypervisor_type, String hypervisor_product, String status, 
			String site, String organization_id, 
			String sync_discovered_vms, String vm_refresh_ts, String cloud_direct_baas_destination, String cloud_direct_draas_destination, String schedule, String schedule_type, 
			String agent_name, String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade,
			String vmware_api_version, String vmware_vcenter_host, String vmware_resource_pools, String vmware_data_stores, String vmware_data_centers, String vmware_host_systems,
			String bind_datacenter, String bind_host, ExtentTest test) {
		
		Map<String, Object> hypervisorInfo = composeHypervisorInfo(hypervisor_id, hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, 
				sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, 
				agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, 
				vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host);
		
		Response response = spogHypervisorsInvoker.updateHypervisor(hypervisor_id_url, hypervisorInfo);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		return response;
	}
	
	public void updateHypervisorWithCodeCheck(String hypervisor_id_url, String hypervisor_id, String hypervisor_name, String hypervisor_type, String hypervisor_product, String status, 
			String site, String organization_id, 
			String sync_discovered_vms, String vm_refresh_ts, String cloud_direct_baas_destination, String cloud_direct_draas_destination, String schedule, String schedule_type, 
			String agent_name, String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade,
			String vmware_api_version, String vmware_vcenter_host, String vmware_resource_pools, String vmware_data_stores, String vmware_data_centers, String vmware_host_systems,
			String bind_datacenter, String bind_host, int status_code, String error_code, ExtentTest test) {
		
		Map<String, Object> hypervisorInfo = composeHypervisorInfo(hypervisor_id, hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, 
				sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, 
				agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, 
				vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host);
		
		Response response = spogHypervisorsInvoker.updateHypervisor(hypervisor_id_url, hypervisorInfo);
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);

	}
	
	public void deleteHypervisorWithCheck(String hypervisor_id, ExtentTest test) {
	
		Response response = spogHypervisorsInvoker.deleteHypervisor(hypervisor_id);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		
	}
	
	public void deleteAllHypervisors(ExtentTest test) {
		Response response = spogHypervisorsInvoker.getHypervisors("", test);
		int pages = response.then().extract().path("pagination.total_page");
		if (pages == 0) return;
		
		while(pages > 0) {
			ArrayList<String> hypervisor_ids = response.then().extract().path("data.hypervisor_id");
			if(hypervisor_ids.size() == 0) {
				return;
			}
			for (String hypervisor_id : hypervisor_ids) {
				deleteHypervisorWithCheck(hypervisor_id, test);
			}
			response = spogHypervisorsInvoker.getHypervisors("", test);
			pages = response.then().extract().path("pagination.total_page");
		}
	}

	public void deleteHypervisorWithCodeCheck(String hypervisor_id, int status_code, String error_code, ExtentTest test) {
		Response response = spogHypervisorsInvoker.deleteHypervisor(hypervisor_id);
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);
	}
	
	public Response getHypervisorWithCheck(String hypervisor_id, ExtentTest test) {
		
		Response response = spogHypervisorsInvoker.getHypervisor(hypervisor_id);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		return response;
		
	}
	
	public void getHypervisorWithCodeCheck(String hypervisor_id, int status_code, String error_code, ExtentTest test) {
		
		Response response = spogHypervisorsInvoker.getHypervisor(hypervisor_id);
		spogServer.checkResponseStatus(response, status_code);
		spogServer.checkErrorCode(response, error_code);
		
	}
	

	private void verifyHyperv(Response response, String hypervisor_id, String hypervisor_name, String hypervisor_type,
			String hypervisor_product, String status, String site, String organization_id, String sync_discovered_vms,
			String vm_refresh_ts, String cloud_direct_baas_destination, String cloud_direct_draas_destination,
			String schedule, String schedule_type, String agent_name, String agent_current_version,
			String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade) {
		
		if(!(hypervisor_id==null) && (!hypervisor_id.equalsIgnoreCase("none"))) {
			String response_hypervisor_id = response.then().extract().path("data.hypervisor_id");
			spogServer.assertResponseItem(hypervisor_id, response_hypervisor_id);
		}
		String response_hypervisor_name = response.then().extract().path("data.hypervisor_name");
		spogServer.assertResponseItem(hypervisor_name, response_hypervisor_name);
		
		String repsonse_hypervisor_type = response.then().extract().path("data.hypervisor_type");
		spogServer.assertResponseItem(hypervisor_type, repsonse_hypervisor_type);

		String repsonse_hypervisor_product = response.then().extract().path("data.hypervisor_product");
		spogServer.assertResponseItem(hypervisor_product, repsonse_hypervisor_product);
		
		String repsonse_status = response.then().extract().path("data.status");
		spogServer.assertResponseItem(status, repsonse_status);
		
		String repsonse_site = response.then().extract().path("data.site.site_id");
		spogServer.assertResponseItem(site, repsonse_site);
		
		String repsonse_organization_id = response.then().extract().path("data.organization_id");
		spogServer.assertResponseItem(organization_id, repsonse_organization_id);
		
		Boolean response_sync_discovered_vms = response.then().extract().path("data.cloud_direct_hyperv.sync_discovered_vms");
		if (sync_discovered_vms == null) {
			assertEquals(response_sync_discovered_vms, Boolean.valueOf(false));
		} else if (sync_discovered_vms.equalsIgnoreCase("true") || sync_discovered_vms.equalsIgnoreCase("false")) {
			assertEquals(response_sync_discovered_vms, Boolean.valueOf(sync_discovered_vms));
		}
		
		String response_cloud_direct_baas_destination = response.then().extract().path("data.cloud_direct_hyperv.cloud_direct_baas_destination");
		spogServer.assertResponseItem(cloud_direct_baas_destination, response_cloud_direct_baas_destination);
		
		String response_cloud_direct_draas_destination = response.then().extract().path("data.cloud_direct_hyperv.cloud_direct_draas_destination");
		spogServer.assertResponseItem(cloud_direct_draas_destination, response_cloud_direct_draas_destination);
		
		String response_schedule = response.then().extract().path("data.cloud_direct_hyperv.schedule");
		spogServer.assertResponseItem(schedule, response_schedule);
		
		String response_schedule_type = response.then().extract().path("data.cloud_direct_hyperv.schedule_type");
		spogServer.assertResponseItem(schedule_type, response_schedule_type);
		
		String response_agent_name = response.then().extract().path("data.cloud_direct_hyperv.agent.agent_name");
		spogServer.assertResponseItem(agent_name, response_agent_name);
		
		String response_agent_current_version = response.then().extract().path("data.cloud_direct_hyperv.agent.agent_current_version");
		spogServer.assertResponseItem(agent_current_version, response_agent_current_version);
		
		String response_agent_upgrade_version = response.then().extract().path("data.cloud_direct_hyperv.agent.agent_upgrade_version");
		spogServer.assertResponseItem(agent_upgrade_version, response_agent_upgrade_version);
		
		String response_agent_upgrade_link = response.then().extract().path("data.cloud_direct_hyperv.agent.agent_upgrade_link");
		spogServer.assertResponseItem(agent_upgrade_link, response_agent_upgrade_link);
		
		Boolean response_agent_autoupgrade = response.then().extract().path("data.cloud_direct_hyperv.agent.agent_autoupgrade");
		if (agent_autoupgrade == null){
			assertEquals(response_agent_autoupgrade, Boolean.valueOf(false));
		}else if (agent_autoupgrade.equalsIgnoreCase("true") || agent_autoupgrade.equalsIgnoreCase("false")) {
			assertEquals(response_agent_autoupgrade, Boolean.valueOf(agent_autoupgrade));
		}
	}
	
	private void verifyHypervUpdate(Response responseOri, Response response, String hypervisor_id, String hypervisor_name, String hypervisor_type,
			String hypervisor_product, String status, String site_id, String organization_id, String sync_discovered_vms,
			String vm_refresh_ts, String cloud_direct_baas_destination, String cloud_direct_draas_destination,
			String schedule, String schedule_type, String agent_name, String agent_current_version,
			String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade) {
		
		if(!(hypervisor_id==null) && (!hypervisor_id.equalsIgnoreCase("none"))) {
			String response_hypervisor_id = response.then().extract().path("data.hypervisor_id");
			spogServer.assertResponseItem(hypervisor_id, response_hypervisor_id);
		}
		
		String response_hypervisor_name = response.then().extract().path("data.hypervisor_name");
		String responseOri_hypervisor_name = responseOri.then().extract().path("data.hypervisor_name");
		if(hypervisor_name==null || hypervisor_name.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_hypervisor_name, response_hypervisor_name);
		} else {
			spogServer.assertResponseItem(hypervisor_type, response_hypervisor_name);
		}
		
		String repsonse_hypervisor_type = response.then().extract().path("data.hypervisor_type");
		String repsonseOri_hypervisor_type = responseOri.then().extract().path("data.hypervisor_type");
		if(hypervisor_type==null || hypervisor_type.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(repsonseOri_hypervisor_type, repsonse_hypervisor_type);
		} else {
			spogServer.assertResponseItem(hypervisor_type, repsonse_hypervisor_type);
		}

		String repsonse_hypervisor_product = response.then().extract().path("data.hypervisor_product");
		String repsonseOri_hypervisor_product = responseOri.then().extract().path("data.hypervisor_product");
		if(hypervisor_product==null || hypervisor_product.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(repsonseOri_hypervisor_product, repsonse_hypervisor_product);
		} else {
			spogServer.assertResponseItem(hypervisor_product, repsonse_hypervisor_product);
		}
		
		String repsonse_status = response.then().extract().path("data.status");
		String repsonseOri_status = responseOri.then().extract().path("data.status");
		if(status==null || status.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(repsonseOri_status, repsonse_status);
		} else {
			spogServer.assertResponseItem(status, repsonse_status);
		}
		
		String repsonse_site = response.then().extract().path("data.site.site_id");
		String repsonseOri_site = responseOri.then().extract().path("data.site.site_id");
		if(site_id==null || site_id.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(repsonseOri_site, repsonse_site);
		} else {
			spogServer.assertResponseItem(site_id, repsonse_site);
		}
		
		String repsonseOri_organization_id = responseOri.then().extract().path("data.organization_id");
		String repsonse_organization_id = response.then().extract().path("data.organization_id");
		if(organization_id==null || organization_id.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(repsonseOri_organization_id, repsonse_organization_id);
		} else {
			spogServer.assertResponseItem(organization_id, repsonse_organization_id);
		}
		
		Boolean responseOri_sync_discovered_vms = responseOri.then().extract().path("data.cloud_direct_hyperv.sync_discovered_vms");
		Boolean response_sync_discovered_vms = response.then().extract().path("data.cloud_direct_hyperv.sync_discovered_vms");
	
		if(sync_discovered_vms==null || sync_discovered_vms.equalsIgnoreCase("none")) {
			assertEquals(response_sync_discovered_vms, responseOri_sync_discovered_vms);
		} else {
			assertEquals(response_sync_discovered_vms, Boolean.valueOf(sync_discovered_vms));
		}
	
		String responseOri_cloud_direct_baas_destination = responseOri.then().extract().path("data.cloud_direct_hyperv.cloud_direct_baas_destination");
		String response_cloud_direct_baas_destination = response.then().extract().path("data.cloud_direct_hyperv.cloud_direct_baas_destination");
		if(cloud_direct_baas_destination==null || cloud_direct_baas_destination.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_cloud_direct_baas_destination, response_cloud_direct_baas_destination);
		} else {
			spogServer.assertResponseItem(cloud_direct_baas_destination, response_cloud_direct_baas_destination);
		}
		
		String responseOri_cloud_direct_draas_destination = responseOri.then().extract().path("data.cloud_direct_hyperv.cloud_direct_draas_destination");
		String response_cloud_direct_draas_destination = response.then().extract().path("data.cloud_direct_hyperv.cloud_direct_draas_destination");
		if(cloud_direct_draas_destination==null || cloud_direct_draas_destination.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_cloud_direct_draas_destination, response_cloud_direct_draas_destination);
		} else {
			spogServer.assertResponseItem(cloud_direct_draas_destination, response_cloud_direct_draas_destination);
		}
		
		String responseOri_schedule = responseOri.then().extract().path("data.cloud_direct_hyperv.schedule");
		String response_schedule = response.then().extract().path("data.cloud_direct_hyperv.schedule");
		if(schedule==null || schedule.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_schedule, response_schedule);
		} else {
			spogServer.assertResponseItem(schedule, response_schedule);
		}
		
		String responseOri_schedule_type = responseOri.then().extract().path("data.cloud_direct_hyperv.schedule_type");
		String response_schedule_type = response.then().extract().path("data.cloud_direct_hyperv.schedule_type");
		if(schedule_type==null || schedule_type.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_schedule_type, response_schedule_type);
		} else {
			spogServer.assertResponseItem(schedule_type, response_schedule_type);
		}
		
		String responseOri_agent_name = responseOri.then().extract().path("data.cloud_direct_hyperv.agent.agent_name");
		String response_agent_name = response.then().extract().path("data.cloud_direct_hyperv.agent.agent_name");
		if(agent_name==null || agent_name.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_agent_name, response_agent_name);
		} else {
			spogServer.assertResponseItem(agent_name, response_agent_name);
		}
		
		String responseOri_agent_current_version = responseOri.then().extract().path("data.cloud_direct_hyperv.agent.agent_current_version");
		String response_agent_current_version = response.then().extract().path("data.cloud_direct_hyperv.agent.agent_current_version");
		if(agent_current_version==null || agent_current_version.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_agent_current_version, response_agent_current_version);
		} else {
			spogServer.assertResponseItem(agent_current_version, response_agent_current_version);
		}
		
		String responseOri_agent_upgrade_version = responseOri.then().extract().path("data.cloud_direct_hyperv.agent.agent_upgrade_version");
		String response_agent_upgrade_version = response.then().extract().path("data.cloud_direct_hyperv.agent.agent_upgrade_version");
		if(agent_upgrade_version==null || agent_upgrade_version.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_agent_upgrade_version, response_agent_upgrade_version);
		} else {
			spogServer.assertResponseItem(agent_upgrade_version, response_agent_upgrade_version);
		}
		
		String responseOri_agent_upgrade_link = responseOri.then().extract().path("data.cloud_direct_hyperv.agent.agent_upgrade_link");
		String response_agent_upgrade_link = response.then().extract().path("data.cloud_direct_hyperv.agent.agent_upgrade_link");
		if(agent_upgrade_link==null || agent_upgrade_link.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_agent_upgrade_link, response_agent_upgrade_link);
		} else {
			spogServer.assertResponseItem(agent_upgrade_link, response_agent_upgrade_link);
		}
		
		Boolean responseOri_agent_autoupgrade = response.then().extract().path("data.cloud_direct_hyperv.agent.agent_autoupgrade");
		Boolean response_agent_autoupgrade = response.then().extract().path("data.cloud_direct_hyperv.agent.agent_autoupgrade");
		if(agent_autoupgrade==null || agent_autoupgrade.equalsIgnoreCase("none")) {
			assertEquals(responseOri_agent_autoupgrade, response_agent_autoupgrade);
		} else {
			assertEquals(response_agent_autoupgrade, Boolean.valueOf(agent_autoupgrade));
		}
	}
	
	private void verifyVmware(Response response, String hypervisor_id, String hypervisor_name, String hypervisor_type,
			String hypervisor_product, String status, String site, String organization_id, String sync_discovered_vms,
			String vm_refresh_ts, String cloud_direct_baas_destination, String cloud_direct_draas_destination,
			String schedule, String schedule_type, String agent_name, String agent_current_version,
			String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade,
			String vmware_api_version, String vmware_vcenter_host, String vmware_resource_pools,
			String vmware_data_stores, String vmware_data_centers, String vmware_host_systems, String bind_datacenter,
			String bind_host) {
		if(!(hypervisor_id==null) && (!hypervisor_id.equalsIgnoreCase("none"))) {
			String response_hypervisor_id = response.then().extract().path("data.hypervisor_id");
			spogServer.assertResponseItem(hypervisor_id, response_hypervisor_id);
		}
		String response_hypervisor_name = response.then().extract().path("data.hypervisor_name");
		spogServer.assertResponseItem(hypervisor_name, response_hypervisor_name);
		
		String repsonse_hypervisor_type = response.then().extract().path("data.hypervisor_type");
		spogServer.assertResponseItem(hypervisor_type, repsonse_hypervisor_type);

		String repsonse_hypervisor_product = response.then().extract().path("data.hypervisor_product");
		spogServer.assertResponseItem(hypervisor_product, repsonse_hypervisor_product);
		
		String repsonse_status = response.then().extract().path("data.status");
		spogServer.assertResponseItem(status, repsonse_status);
		
		String repsonse_site = response.then().extract().path("data.site.site_id");
		spogServer.assertResponseItem(site, repsonse_site);
		
		String repsonse_organization_id = response.then().extract().path("data.organization_id");
		spogServer.assertResponseItem(organization_id, repsonse_organization_id);
		
		Boolean response_sync_discovered_vms = response.then().extract().path("data.cloud_direct_vmware.sync_discovered_vms");
		if (sync_discovered_vms == null) {
			assertEquals(response_sync_discovered_vms, Boolean.valueOf(false));
		} else if (sync_discovered_vms.equalsIgnoreCase("true") || sync_discovered_vms.equalsIgnoreCase("false")) {
			assertEquals(response_sync_discovered_vms, Boolean.valueOf(sync_discovered_vms));
		}
		
		String response_cloud_direct_baas_destination = response.then().extract().path("data.cloud_direct_vmware.cloud_direct_baas_destination");
		spogServer.assertResponseItem(cloud_direct_baas_destination, response_cloud_direct_baas_destination);
		
		String response_cloud_direct_draas_destination = response.then().extract().path("data.cloud_direct_vmware.cloud_direct_draas_destination");
		spogServer.assertResponseItem(cloud_direct_draas_destination, response_cloud_direct_draas_destination);
		
		String response_schedule = response.then().extract().path("data.cloud_direct_vmware.schedule");
		spogServer.assertResponseItem(schedule, response_schedule);
		
		String response_schedule_type = response.then().extract().path("data.cloud_direct_vmware.schedule_type");
		spogServer.assertResponseItem(schedule_type, response_schedule_type);
		
		String response_vmware_api_version = response.then().extract().path("data.cloud_direct_vmware.vmware_api_version");
		spogServer.assertResponseItem(vmware_api_version, response_vmware_api_version);
		
		String response_vmware_vcenter_host = response.then().extract().path("data.cloud_direct_vmware.vmware_vcenter_host");
		spogServer.assertResponseItem(vmware_vcenter_host, response_vmware_vcenter_host);
		
		String response_bind_datacenter = response.then().extract().path("data.cloud_direct_vmware.bind_datacenter");
		spogServer.assertResponseItem(bind_datacenter, response_bind_datacenter);
		
		String response_bind_host = response.then().extract().path("data.cloud_direct_vmware.bind_host");
		spogServer.assertResponseItem(bind_host, response_bind_host);
		
		ArrayList<String> response_vmware_resource_pools = response.then().extract().path("data.cloud_direct_vmware.vmware_resource_pools");
		spogServer.assertFilterItem(vmware_resource_pools, response_vmware_resource_pools); 
		
		ArrayList<String> response_vmware_data_stores = response.then().extract().path("data.cloud_direct_vmware.vmware_data_stores");
		spogServer.assertFilterItem(vmware_data_stores, response_vmware_data_stores); 
		
		ArrayList<String> response_vmware_data_centers = response.then().extract().path("data.cloud_direct_vmware.vmware_data_centers");
		spogServer.assertFilterItem(vmware_data_centers, response_vmware_data_centers); 
		
		ArrayList<String> response_vmware_host_systems = response.then().extract().path("data.cloud_direct_vmware.vmware_host_systems");
		spogServer.assertFilterItem(vmware_host_systems, response_vmware_host_systems); 
		
		String response_agent_name = response.then().extract().path("data.cloud_direct_vmware.agent.agent_name");
		spogServer.assertResponseItem(agent_name, response_agent_name);
		
		String response_agent_current_version = response.then().extract().path("data.cloud_direct_vmware.agent.agent_current_version");
		spogServer.assertResponseItem(agent_current_version, response_agent_current_version);
		
		String response_agent_upgrade_version = response.then().extract().path("data.cloud_direct_vmware.agent.agent_upgrade_version");
		spogServer.assertResponseItem(agent_upgrade_version, response_agent_upgrade_version);
		
		String response_agent_upgrade_link = response.then().extract().path("data.cloud_direct_vmware.agent.agent_upgrade_link");
		spogServer.assertResponseItem(agent_upgrade_link, response_agent_upgrade_link);
		
		Boolean response_agent_autoupgrade = response.then().extract().path("data.cloud_direct_vmware.agent.agent_autoupgrade");
		if (agent_autoupgrade == null){
			assertEquals(response_agent_autoupgrade, Boolean.valueOf(false));
		}else if (agent_autoupgrade.equalsIgnoreCase("true") || agent_autoupgrade.equalsIgnoreCase("false")) {
			assertEquals(response_agent_autoupgrade, Boolean.valueOf(agent_autoupgrade));
		}
	}
	

	
	/*
	 * @author Rakesh.Chalamala
	 * @param hypervisor_id
	 * @param test
	 * 
	 * Used to get the specified hypervisor by id 
	 */
	public Response getHypervisorsById(String hypervisor_id,ExtentTest test) {
		
		test.log(LogStatus.INFO, "Get hypervisors by id");
		Response response = spogHypervisorsInvoker.getHypervisorsById(hypervisor_id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		return response;
	}
	
	
	/*
	 * @author Rakesh.Chalamala
	 * @param hypervisor_id_url
	 * @param hypervisor_id
	 * @param hypervisor_name
	 * @param hypervisor_type
	 * @param hypervisor_product
	 * @param status
	 * @param site
	 * @param organization_id
	 * @param sync_discovered_vms
	 * @param vm_refresh_ts
	 * @param cloud_direct_baas_destination
	 * @param cloud_direct_draas_destination
	 * @param schedule
	 * @param schedule_type
	 * @param agent_name
	 * @param agent_current_version
	 * @param agent_upgrade_version
	 * @param agent_upgrade_link
	 * @param agent_autoupgrade
	 * @param vmware_api_version
	 * @param vmware_vcenter_host
	 * @param vmware_resource_pools
	 * @param vmware_data_stores
	 * @param vmware_data_centers
	 * @param vmware_host_systems
	 * @param bind_datacenter
	 * @param bind_host
	 * @param test
	 */
	public void getHypervisorsByIdWithCheck(String hypervisor_id, String hypervisor_name, String hypervisor_type, String hypervisor_product, String status, 
			String site, String organization_id, String sync_discovered_vms, String vm_refresh_ts, String cloud_direct_baas_destination, String cloud_direct_draas_destination, String schedule, String schedule_type, 
			String agent_name, String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade,
			String vmware_api_version, String vmware_vcenter_host, String vmware_resource_pools, String vmware_data_stores, String vmware_data_centers, String vmware_host_systems,
			String bind_datacenter, String bind_host,int expectedStatusCode,SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		
		Response response = spogHypervisorsInvoker.getHypervisorsById( hypervisor_id, test);
		spogServer.checkResponseStatus(response,expectedStatusCode,test);
		if (expectedStatusCode==(SpogConstants.SUCCESS_GET_PUT_DELETE)) {

			if (hypervisor_type.equalsIgnoreCase("hyperv")) {
				verifyHyperv(response, hypervisor_id, hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, 
						sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, 
						agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade);
			}
			else if (hypervisor_type.equalsIgnoreCase("vmware")) {
				verifyVmware(response, hypervisor_id, hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, 
						sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, 
						agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, 
						vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host);
			}
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();			
			if(code.contains("01000003")){
    		message = message.replace("{0}", hypervisor_id);
			}
			spogServer.checkErrorCode(response,code);
			test.log(LogStatus.PASS, "The error code matched with the expected "+code);
			spogServer.checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
			
		}
		
	}
	
	/*
	 * @author Rakesh.Chalamala
	 * @param expectedHypervisorsInfo
	 * @param curr_page
	 * @param page_size
	 * @param filterStr
	 * @param SortStr
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 */
	public void getHypervisorsWithCheck(ArrayList<HashMap<String, Object>> expectedHypervisorsInfo,int curr_page, int page_size,String filterStr, String SortStr,int expectedStatusCode, SpogMessageCode expectedErrorMessage,ExtentTest test) {


		String additionalURL="";
		ArrayList<HashMap<String, Object>> filteredHypervisorInfo = new ArrayList<>();

		if((filterStr!=null && filterStr!="") || (SortStr!=null && SortStr!="")) {
			if (curr_page == 0||curr_page==-1) {
				curr_page = 1;
			}
			if (page_size == 0 ||page_size==-1) {
				page_size = 20;
			}else if(page_size>=100&&page_size<SpogConstants.MAX_PAGE_SIZE){
				page_size=100;
			}
			additionalURL = spogServer.PrepareURL(filterStr, SortStr, curr_page, page_size, test);
		}
		Response response = spogHypervisorsInvoker.getHypervisors(additionalURL, test);
		spogServer.setToken(spogHypervisorsInvoker.getToken());
		spogServer.checkResponseStatus(response, expectedStatusCode);

		if(expectedHypervisorsInfo==null)
			return ;

		if(filterStr!=null && !filterStr.isEmpty()) {

			String[] filterStrArray = filterStr.split(",");
			for (int i = 0; i < filterStrArray.length; i++) {
				String[] eachFilterStr = filterStrArray[i].split(";");

				filteredHypervisorInfo = new ArrayList<>();
				for (int j = 0; j < expectedHypervisorsInfo.size(); j++) {

					if(eachFilterStr[0].equals("hypervisor_name")) {
						if(expectedHypervisorsInfo.get(j).get("hypervisor_name").toString().contains(eachFilterStr[2])) {
							filteredHypervisorInfo.add(expectedHypervisorsInfo.get(j));
						}
					}
					else if(eachFilterStr[0].equals("hypervisor_type")) {
						if (eachFilterStr[2].contains("|")) {
							String[] multiple = eachFilterStr[2].split("\\|");
							if(expectedHypervisorsInfo.get(j).containsValue(multiple[0])||expectedHypervisorsInfo.get(j).containsValue(multiple[1])) {
								filteredHypervisorInfo.add(expectedHypervisorsInfo.get(j));
							}
						}else {
							if(expectedHypervisorsInfo.get(j).containsValue(eachFilterStr[2])) {
								filteredHypervisorInfo.add(expectedHypervisorsInfo.get(j));
							}
						}
					}
					else if(eachFilterStr[0].equals("organization_id")) {
						if(expectedHypervisorsInfo.get(j).containsValue(eachFilterStr[2])) {
							filteredHypervisorInfo.add(expectedHypervisorsInfo.get(j));
						}
					}
					else if(eachFilterStr[0].equals("hypervisor_product")) {
						if(expectedHypervisorsInfo.get(j).containsValue(eachFilterStr[2])) {
							filteredHypervisorInfo.add(expectedHypervisorsInfo.get(j));
						}
					}
					else if(eachFilterStr[0].equals("site_id")) {
						if(expectedHypervisorsInfo.get(j).containsValue(eachFilterStr[2])) {
							filteredHypervisorInfo.add(expectedHypervisorsInfo.get(j));
						}
					}
					/*else if(eachFilterStr[0].equals("create_user_id")) {
						if(expectedHypervisorsInfo.get(j).containsValue(eachFilterStr[2])) {
							filteredHypervisorInfo.add(expectedHypervisorsInfo.get(j));
						}
					}*/
				}
				expectedHypervisorsInfo = filteredHypervisorInfo;
			}

		}else {
			filteredHypervisorInfo = expectedHypervisorsInfo;
		}

		ArrayList<HashMap<String, Object>> actualHypervisorsInfo = new ArrayList<>();

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			actualHypervisorsInfo = response.then().extract().path("data");
			
			if (filteredHypervisorInfo!=null
					&& filteredHypervisorInfo.size() >= actualHypervisorsInfo.size()) {

				test.log(LogStatus.INFO, "check response For the Hypervisors ");		
				if (((SortStr == null) || (SortStr.equals("")))
						&& ((filterStr == null) || (filterStr.equals("")))) {
					// If we Don't mention any Sorting the default sorting is descending order 
					//validating the response for hypervisors	
					int j=0;
					if (page_size == 20) {
						j = actualHypervisorsInfo.size() - 1;
						if(filteredHypervisorInfo.size()>=actualHypervisorsInfo.size()) {
							j=filteredHypervisorInfo.size()-1;
						}					
					}
					if (curr_page != 1) {
						j = (filteredHypervisorInfo.size() - 1) - (curr_page - 1) * page_size;
					}	
					for (int i = 0; i < actualHypervisorsInfo.size() && j >= 0; i++, j--) {
						HashMap<String,Object> actual_hypervisor_data=actualHypervisorsInfo.get(i),expected_hypervisor_data=filteredHypervisorInfo.get(j);
						//validate the response for the hypervisors
						checkForHypervisorData(expected_hypervisor_data,actual_hypervisor_data,test);						
					}
				}			
				// SortStr
				else if ( //For sorting only and no filtering but includes pagination
						(((SortStr != null) || (!SortStr.equals("")))
								&&((filterStr == null) || (filterStr.equals(""))))
						//For Sorting and Filtering both 
						||(((SortStr != null)||(!SortStr.equals(""))) &&((filterStr != null) || (!filterStr.equals(""))))
						) {

					test.log(LogStatus.INFO,"Validating the response for the get hypervisor ");
					// validating the response for all the users who are sorted based on response in ascending
					// order and descending order
					if (SortStr.contains("asc")) {
						int j = 0;
						if (curr_page != 1) {
							j = (curr_page - 1) * page_size;
						}	
						for(int i=0;i<actualHypervisorsInfo.size();i++,j++){
							HashMap<String,Object> actual_hypervisor_data=actualHypervisorsInfo.get(i),expected_hypervisor_data=filteredHypervisorInfo.get(j);

							//validate the response for the hypervisors
							checkForHypervisorData(expected_hypervisor_data,actual_hypervisor_data,test);
						}
					} else if(SortStr.contains("desc")){
						int j = 0;
						if (page_size == 20) {
							j = actualHypervisorsInfo.size() - 1;
							if(filteredHypervisorInfo.size()>=actualHypervisorsInfo.size()) {
								j=filteredHypervisorInfo.size()-1;
							}					
						} else {
							j = filteredHypervisorInfo.size() - 1;
							if (curr_page != 1) {
								j = (filteredHypervisorInfo.size() - 1) - (curr_page - 1) * page_size;
							}
						}						
						for (int i = 0; i < actualHypervisorsInfo.size() && j >= 0; i++, j--) {		
							HashMap<String,Object> actual_hypervisor_data=actualHypervisorsInfo.get(i),expected_hypervisor_data=filteredHypervisorInfo.get(j);
							//validate the response for the hypervisors
							checkForHypervisorData(expected_hypervisor_data,actual_hypervisor_data,test);
						}							
					}
				}
			}else {
				System.out.println("Actual number of hypervisors is more than expected and the count is: "+actualHypervisorsInfo.size());
				assertTrue(false, "Actual number of hypervisors is more than expected and the count is: "+actualHypervisorsInfo.size());
			}
		}else {

			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();			

			spogServer.checkErrorCode(response,code);
			test.log(LogStatus.PASS, "The error code matched with the expected "+code);
			spogServer.checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/*
	 * @author Rakesh.Chalamala
	 * @param expectedHypervisorInfo
	 * @param actualHypervisorInfo
	 * @param test
	 * 
	 * Compares the data in both actual and expected
	 */
	public void checkForHypervisorData(HashMap<String, Object> expectedHypervisorInfo,HashMap<String, Object> actualHypervisorInfo,ExtentTest test) {
		
		test.log(LogStatus.INFO, "Compare the hypervisor id ");
		System.out.println(expectedHypervisorInfo.get("hypervisor_id").toString() +" = "+ actualHypervisorInfo.get("hypervisor_id").toString());
		test.log(LogStatus.INFO, "expected hyperviosr id is: "+expectedHypervisorInfo.get("hypervisor_id").toString() + "actual hypervisor id is: "+actualHypervisorInfo.get("hypervisor_id").toString());
		spogServer.assertResponseItem(expectedHypervisorInfo.get("hypervisor_id").toString(),actualHypervisorInfo.get("hypervisor_id").toString(),test);
		
		test.log(LogStatus.INFO, "Compare the hypervisor_name ");
		spogServer.assertResponseItem(expectedHypervisorInfo.get("hypervisor_name").toString(), actualHypervisorInfo.get("hypervisor_name").toString(), test);
		
		test.log(LogStatus.INFO, "Compare the hypervisor_type ");
		spogServer.assertResponseItem(expectedHypervisorInfo.get("hypervisor_type").toString(), actualHypervisorInfo.get("hypervisor_type").toString(), test);
		
		test.log(LogStatus.INFO, "Compare the hypervisor_product ");
		spogServer.assertResponseItem(expectedHypervisorInfo.get("hypervisor_product").toString(), actualHypervisorInfo.get("hypervisor_product").toString(), test);
		
		test.log(LogStatus.INFO, "Compare the status ");
		spogServer.assertResponseItem(expectedHypervisorInfo.get("status"), actualHypervisorInfo.get("status"), test);
		
		test.log(LogStatus.INFO, "Compare the organization_id ");
		spogServer.assertResponseItem(expectedHypervisorInfo.get("organization_id").toString(), actualHypervisorInfo.get("organization_id").toString(), test);
		
		if(actualHypervisorInfo.containsKey("site")) {
			HashMap<String, Object> actual_site = (HashMap<String, Object>) actualHypervisorInfo.get("site");

			test.log(LogStatus.INFO, "Compare the site_id ");
			
			
			if (!expectedHypervisorInfo.get("site_id").toString().equals(actual_site.get("site_id").toString())) {
				spogServer.setToken(spogHypervisorsInvoker.getToken());
				Response sites_response = spogServer.getSites();
				ArrayList<HashMap<String, Object>> sites = sites_response.then().extract().path("data");
				
				for (int i = 0; i < sites.size(); i++) {
					if (!(sites.get(i).get("organization_id").equals(expectedHypervisorInfo.get("organization_id").toString()))) {
						spogServer.assertResponseItem(sites.get(i).get("site_id").toString(), actual_site.get("site_id").toString());
					}
				}
			} else {
				spogServer.assertResponseItem(expectedHypervisorInfo.get("site_id").toString(), actual_site.get("site_id").toString(), test);
			}
		}
		
		if(actualHypervisorInfo.containsKey("cloud_direct_hyperv")) {
			HashMap<String, Object> expectedHypervs = (HashMap<String, Object>) expectedHypervisorInfo.get("cloud_direct_hyperv");
			HashMap<String, Object> actualHypervs = (HashMap<String, Object>) actualHypervisorInfo.get("cloud_direct_hyperv");
			
			test.log(LogStatus.INFO, "Compare the sync_discovered_vms ");
			spogServer.assertResponseItem(expectedHypervs.get("sync_discovered_vms"), actualHypervs.get("sync_discovered_vms"), test);
			
			test.log(LogStatus.INFO, "Compare the cloud_direct_baas_destination ");
			spogServer.assertResponseItem(expectedHypervs.get("cloud_direct_baas_destination"), actualHypervs.get("cloud_direct_baas_destination"), test);
			
			if(expectedHypervs.containsKey("cloud_direct_draas_destination")){
			test.log(LogStatus.INFO, "Compare the cloud_direct_draas_destination ");
			spogServer.assertResponseItem(expectedHypervs.get("cloud_direct_draas_destination"), actualHypervs.get("cloud_direct_draas_destination"), test);
			}
			test.log(LogStatus.INFO, "Compare the schedule ");
			spogServer.assertResponseItem(expectedHypervs.get("schedule").toString(), actualHypervs.get("schedule").toString(), test);
			
			test.log(LogStatus.INFO, "Compare the schedule_type ");
			spogServer.assertResponseItem(expectedHypervs.get("schedule_type").toString(), actualHypervs.get("schedule_type").toString(), test);
			
			if(actualHypervs.containsKey("agent")) {

				HashMap<String, Object> expectedAgentInfo = (HashMap<String, Object>) expectedHypervs.get("agent");
				HashMap<String, Object> actualAgentInfo = (HashMap<String, Object>) actualHypervs.get("agent");
				
				test.log(LogStatus.INFO, "Compare the agent_name ");
				spogServer.assertResponseItem(expectedAgentInfo.get("agent_name"), actualAgentInfo.get("agent_name"), test);
				
				test.log(LogStatus.INFO, "Compare the agent_current_version ");
				spogServer.assertResponseItem(expectedAgentInfo.get("agent_current_version"), actualAgentInfo.get("agent_current_version"), test);
				
				test.log(LogStatus.INFO, "Compare the agent_upgrade_version ");
				spogServer.assertResponseItem(expectedAgentInfo.get("agent_upgrade_version"), actualAgentInfo.get("agent_upgrade_version"), test);
				
				test.log(LogStatus.INFO, "Compare the agent_upgrade_link ");
				spogServer.assertResponseItem(expectedAgentInfo.get("agent_upgrade_link"), actualAgentInfo.get("agent_upgrade_link"), test);
				
				test.log(LogStatus.INFO, "Compare the agent_autoupgrade ");
				spogServer.assertResponseItem(expectedAgentInfo.get("agent_autoupgrade"),actualAgentInfo.get("agent_autoupgrade"), test);
			}
			
			if (expectedHypervs.containsKey("schedule_options")) {
				
				ArrayList<String> expectedScheduleOptions = (ArrayList<String>) expectedHypervs.get("schedule_options");
				ArrayList<String> actualScheduleOptions = (ArrayList<String>) actualHypervs.get("schedule_options");
				
				if(expectedScheduleOptions.size()==actualScheduleOptions.size()) {
					for (int i = 0; i < actualScheduleOptions.size(); i++) {
						test.log(LogStatus.INFO, "Compare the schedule_options ");
						spogServer.assertResponseItem(expectedScheduleOptions.get(i).toString(), actualScheduleOptions.get(i).toString(),test);
					}
				}
			}
			
			if (expectedHypervs.containsKey("throttles")) {
				
				ArrayList<String> expectedThrottles = (ArrayList<String>) expectedHypervs.get("throttles");
				ArrayList<String> actualThrottles = (ArrayList<String>) actualHypervs.get("throttles");
				
				if(expectedThrottles.size()==actualThrottles.size()) {
					for (int i = 0; i < actualThrottles.size(); i++) {
						test.log(LogStatus.INFO, "Compare the throttles ");
						spogServer.assertResponseItem(expectedThrottles.get(i).toString(), actualThrottles.get(i).toString(),test);
					}
				}
			}
			
			if (expectedHypervs.containsKey("available_actions")) {
				
				ArrayList<String> expectedAvailableActions = (ArrayList<String>) expectedHypervs.get("available_actions");
				ArrayList<String> actualAvailableActions = (ArrayList<String>) actualHypervs.get("available_actions");
				
				if(expectedAvailableActions.size()==actualAvailableActions.size()) {
					for (int i = 0; i < actualAvailableActions.size(); i++) {
						test.log(LogStatus.INFO, "Compare the available_actions ");
						spogServer.assertResponseItem(expectedAvailableActions.get(i).toString(), actualAvailableActions.get(i).toString(),test);
					}
				}
			}
			test.log(LogStatus.INFO, "Compare the deleted option ");
			spogServer.assertResponseItem(expectedHypervs.get("deleted"), actualHypervs.get("deleted"), test);
			
		}
		
		if(actualHypervisorInfo.containsKey("cloud_direct_vmware")) {
			HashMap<String, Object> expectedVmwares = (HashMap<String, Object>) expectedHypervisorInfo.get("cloud_direct_vmware");
			HashMap<String, Object> actualVmwares = (HashMap<String, Object>) actualHypervisorInfo.get("cloud_direct_vmware");
			
			test.log(LogStatus.INFO, "Compare the sync_discovered_vms ");
			spogServer.assertResponseItem(expectedVmwares.get("sync_discovered_vms"), actualVmwares.get("sync_discovered_vms"), test);
			
			/*test.log(LogStatus.INFO, "Compare the vm_refresh_ts ");
			spogServer.assertResponseItem(expectedVmwares.get("vm_refresh_ts"),actualVmwares.get("vm_refresh_ts"), test);*/
			
			test.log(LogStatus.INFO, "Compare the cloud_direct_baas_destination ");
			spogServer.assertResponseItem(expectedVmwares.get("cloud_direct_baas_destination"), actualVmwares.get("cloud_direct_baas_destination"), test);
			
			if(expectedVmwares.containsKey("cloud_direct_draas_destination")){
			test.log(LogStatus.INFO, "Compare the cloud_direct_draas_destination ");
			spogServer.assertResponseItem(expectedVmwares.get("cloud_direct_draas_destination"), actualVmwares.get("cloud_direct_draas_destination"), test);
			}
			test.log(LogStatus.INFO, "Compare the schedule ");
			spogServer.assertResponseItem(expectedVmwares.get("schedule").toString(), actualVmwares.get("schedule").toString(), test);
			
			test.log(LogStatus.INFO, "Compare the schedule_type ");
			spogServer.assertResponseItem(expectedVmwares.get("schedule_type").toString(), actualVmwares.get("schedule_type").toString(), test);
			
			if(actualVmwares.containsKey("agent")) {
				HashMap<String, Object> expectedAgentInfo = (HashMap<String, Object>) expectedVmwares.get("agent");
				HashMap<String, Object> actualAgentInfo = (HashMap<String, Object>) actualVmwares.get("agent");
				
				test.log(LogStatus.INFO, "Compare the agent_name ");
				spogServer.assertResponseItem(expectedAgentInfo.get("agent_name").toString(), actualAgentInfo.get("agent_name").toString(), test);
				
				test.log(LogStatus.INFO, "Compare the agent_current_version ");
				spogServer.assertResponseItem(expectedAgentInfo.get("agent_current_version").toString(), actualAgentInfo.get("agent_current_version").toString(), test);
				
				test.log(LogStatus.INFO, "Compare the agent_upgrade_version ");
				spogServer.assertResponseItem(expectedAgentInfo.get("agent_upgrade_version").toString(), actualAgentInfo.get("agent_upgrade_version").toString(), test);
				
				test.log(LogStatus.INFO, "Compare the agent_upgrade_link ");
				spogServer.assertResponseItem(expectedAgentInfo.get("agent_upgrade_link").toString(), actualAgentInfo.get("agent_upgrade_link").toString(), test);
				
				test.log(LogStatus.INFO, "Compare the agent_autoupgrade ");
				spogServer.assertResponseItem(expectedAgentInfo.get("agent_autoupgrade"),actualAgentInfo.get("agent_autoupgrade"), test);
			}
			
			if (expectedVmwares.containsKey("schedule_options")) {
				
				ArrayList<String> expectedScheduleOptions = (ArrayList<String>) expectedVmwares.get("schedule_options");
				ArrayList<String> actualScheduleOptions = (ArrayList<String>) actualVmwares.get("schedule_options");
				
				if(expectedScheduleOptions.size()==actualScheduleOptions.size()) {
					for (int i = 0; i < actualScheduleOptions.size(); i++) {
						test.log(LogStatus.INFO, "Compare the schedule_options ");
						spogServer.assertResponseItem(expectedScheduleOptions.get(i).toString(), actualScheduleOptions.get(i).toString(),test);
					}
				}
			}
			
			if (expectedVmwares.containsKey("throttles")) {
				
				ArrayList<String> expectedThrottles = (ArrayList<String>) expectedVmwares.get("throttles");
				ArrayList<String> actualThrottles = (ArrayList<String>) actualVmwares.get("throttles");
				
				if(expectedThrottles.size()==actualThrottles.size()) {
					for (int i = 0; i < actualThrottles.size(); i++) {
						test.log(LogStatus.INFO, "Compare the throttles ");
						spogServer.assertResponseItem(expectedThrottles.get(i).toString(), actualThrottles.get(i).toString(),test);
					}
				}
			}
			
			if (expectedVmwares.containsKey("available_actions")) {
				
				ArrayList<String> expectedAvailableActions = (ArrayList<String>) expectedVmwares.get("available_actions");
				ArrayList<String> actualAvailableActions = (ArrayList<String>) actualVmwares.get("available_actions");
				
				if(expectedAvailableActions.size()==actualAvailableActions.size()) {
					for (int i = 0; i < actualAvailableActions.size(); i++) {
						test.log(LogStatus.INFO, "Compare the available_actions ");
						spogServer.assertResponseItem(expectedAvailableActions.get(i).toString(), actualAvailableActions.get(i).toString(),test);
					}
				}
			}
			test.log(LogStatus.INFO, "Compare the deleted option ");
			spogServer.assertResponseItem(expectedVmwares.get("deleted"), actualVmwares.get("deleted"), test);
			
		}
	}
	
	/*
	 * @author Rakesh.Chalamala
	 * @param hypervisor_id
	 * @test
	 * 
	 * Get vms in a specified hypervisor
	 */
	public ArrayList<String> getVMSForSpecifiedHypervisor(String hypervisor_id,ExtentTest test) {
		
		ArrayList<String> vm_ids = new ArrayList<>();

		test.log(LogStatus.INFO, "Call Hypervisor Invoker to get the vms for specified hypervisor");
		Response response = spogHypervisorsInvoker.getVMSForSpecifiedHypervisor(hypervisor_id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		ArrayList<HashMap<String, Object>> actual_vms = response.then().extract().path("data");
		for (int i = 0; i < actual_vms.size(); i++) {
				vm_ids.add(actual_vms.get(i).get("source_id").toString());						
		}
		
		return vm_ids;
	}
	
	public String[] getVMSForSpecifiedHypervisorWithCheck(String hypervisor_id,ArrayList<HashMap<String, Object>> expected_vms,int expectedStatusCode,SpogMessageCode ExpectedErrorMessage,ExtentTest test) {

		String[] vm_ids = null ;

		test.log(LogStatus.INFO, "Call Hypervisor Invoker to get the vms for specified hypervisor");
		Response response = spogHypervisorsInvoker.getVMSForSpecifiedHypervisor(hypervisor_id, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);

		if(expectedStatusCode==SpogConstants.SUCCESS_GET_PUT_DELETE) {

			ArrayList<HashMap<String, Object>> actual_vms = response.then().extract().path("data");
			for (int i = 0; i < actual_vms.size(); i++) {

				if(actual_vms.get(i).get("hypervisor_id").toString().equals(expected_vms.get(i).get("hypervisor_id").toString())) {
					test.log(LogStatus.INFO, "Compare the source id of actual and expected vms of specified hypervisor");
					spogServer.assertResponseItem(actual_vms.get(i).get("source_id").toString(),expected_vms.get(i).get("source_id").toString(),test);

					vm_ids[i] = actual_vms.get(i).get("source_id").toString();				
				}		
			}
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();			
			if (code.equals("01000003")) {
				message = message.replace("{0}", hypervisor_id);
			}
			spogServer.checkErrorCode(response,code);
			test.log(LogStatus.PASS, "The error code matched with the expected "+code);
			spogServer.checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
		return vm_ids;
	}
	
	/*
	 * @author Rakesh.Chalamala
	 * @param hypervisor_id
	 * @param vm_ids[]
	 * @param test
	 * 
	 * Enable the vms in the hypervisor
	 */
	public Response enableVMSForSpecifiedHypervisor(String hypervisor_id,ArrayList<String> vm_ids,int expectedStatusCode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		
		HashMap<String,Object> enableVMlist = new HashMap<>();
		enableVMlist.put("vm_ids", vm_ids);
		
		Response response = spogHypervisorsInvoker.enableVMSToSpecifiedHypervisor(hypervisor_id, enableVMlist, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);
		if(expectedStatusCode==SpogConstants.SUCCESS_POST) {
			test.log(LogStatus.INFO, "Enable vms for specified hypervisor returned success.");
		}else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();			
			if(code.contains("01000003")){
	    		message = message.replace("{0}", hypervisor_id);
			}
			spogServer.checkErrorCode(response,code);
			test.log(LogStatus.PASS, "The error code matched with the expected "+code);
			spogServer.checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
		
		
		return response;
	}
	
	/*
	 * @author Rakesh.Chalamala
	 * @param hypervisor_id
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param test
	 * 
	 * Refresh the vms in the specified hypervisor ( Time stamp will be updated )
	 */
	public void refreshVMSForSpecifiedHypervisorWithCheck(String hypervisor_id,int expectedStatusCode,SpogMessageCode ExpectedErrorMessage,ExtentTest test) {
		
		Response response = spogHypervisorsInvoker.refreshVMSForSpecifiedHypervisor( hypervisor_id, test);
		spogServer.checkResponseStatus(response, expectedStatusCode, test);
		
		if(expectedStatusCode==SpogConstants.SUCCESS_POST) {
			test.log(LogStatus.INFO, "Refresh vms for specified hypervisor returned success.");
		}else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();			
			if(code.contains("01000003")){
	    		message = message.replace("{0}", hypervisor_id);
				}
			//should add the code for 500 also
			spogServer.checkErrorCode(response,code);
			test.log(LogStatus.PASS, "The error code matched with the expected "+code);
			spogServer.checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	
	}	


	private void verifyVmwareUpdate(Response responseOri, Response response, String hypervisor_id, String hypervisor_name, String hypervisor_type,
			String hypervisor_product, String status, String site_id, String organization_id, String sync_discovered_vms,
			String vm_refresh_ts, String cloud_direct_baas_destination, String cloud_direct_draas_destination,
			String schedule, String schedule_type, String agent_name, String agent_current_version,
			String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade,
			String vmware_api_version, String vmware_vcenter_host, String vmware_resource_pools,
			String vmware_data_stores, String vmware_data_centers, String vmware_host_systems, String bind_datacenter,
			String bind_host) {
		
		if(!(hypervisor_id==null) && (!hypervisor_id.equalsIgnoreCase("none"))) {
			String response_hypervisor_id = response.then().extract().path("data.hypervisor_id");
			spogServer.assertResponseItem(hypervisor_id, response_hypervisor_id);
		}
		
		String response_hypervisor_name = response.then().extract().path("data.hypervisor_name");
		String responseOri_hypervisor_name = responseOri.then().extract().path("data.hypervisor_name");
		if(hypervisor_name==null || hypervisor_name.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_hypervisor_name, response_hypervisor_name);
		} else {
			spogServer.assertResponseItem(hypervisor_name, response_hypervisor_name);
		}
		
		String repsonse_hypervisor_type = response.then().extract().path("data.hypervisor_type");
		String repsonseOri_hypervisor_type = responseOri.then().extract().path("data.hypervisor_type");
		if(hypervisor_type==null || hypervisor_type.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(repsonseOri_hypervisor_type, repsonse_hypervisor_type);
		} else {
			spogServer.assertResponseItem(hypervisor_type, repsonse_hypervisor_type);
		}

		String repsonse_hypervisor_product = response.then().extract().path("data.hypervisor_product");
		String repsonseOri_hypervisor_product = responseOri.then().extract().path("data.hypervisor_product");
		if(hypervisor_product==null || hypervisor_product.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(repsonseOri_hypervisor_product, repsonse_hypervisor_product);
		} else {
			spogServer.assertResponseItem(hypervisor_product, repsonse_hypervisor_product);
		}
		
		String repsonse_status = response.then().extract().path("data.status");
		String repsonseOri_status = responseOri.then().extract().path("data.status");
		if(status==null || status.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(repsonseOri_status, repsonse_status);
		} else {
			spogServer.assertResponseItem(status, repsonse_status);
		}
		
		String repsonse_site = response.then().extract().path("data.site.site_id");
		String repsonseOri_site = responseOri.then().extract().path("data.site.site_id");
		if(site_id==null || site_id.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(repsonseOri_site, repsonse_site);
		} else {
			spogServer.assertResponseItem(site_id, repsonse_site);
		}
		
		String repsonseOri_organization_id = responseOri.then().extract().path("data.organization_id");
		String repsonse_organization_id = response.then().extract().path("data.organization_id");
		if(organization_id==null || organization_id.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(repsonseOri_organization_id, repsonse_organization_id);
		} else {
			spogServer.assertResponseItem(organization_id, repsonse_organization_id);
		}
		
		Boolean responseOri_sync_discovered_vms = responseOri.then().extract().path("data.cloud_direct_vmware.sync_discovered_vms");
		Boolean response_sync_discovered_vms = response.then().extract().path("data.cloud_direct_vmware.sync_discovered_vms");
	
		if(sync_discovered_vms==null || sync_discovered_vms.equalsIgnoreCase("none") || sync_discovered_vms == "") {
			assertEquals(response_sync_discovered_vms, responseOri_sync_discovered_vms);
		} 
		else {
			assertEquals(response_sync_discovered_vms, Boolean.valueOf(sync_discovered_vms));
		}
	
		String responseOri_cloud_direct_baas_destination = responseOri.then().extract().path("data.cloud_direct_vmware.cloud_direct_baas_destination");
		String response_cloud_direct_baas_destination = response.then().extract().path("data.cloud_direct_vmware.cloud_direct_baas_destination");
		if(cloud_direct_baas_destination==null || cloud_direct_baas_destination.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_cloud_direct_baas_destination, response_cloud_direct_baas_destination);
		} else {
			spogServer.assertResponseItem(cloud_direct_baas_destination, response_cloud_direct_baas_destination);
		}
		
		String responseOri_cloud_direct_draas_destination = responseOri.then().extract().path("data.cloud_direct_vmware.cloud_direct_draas_destination");
		String response_cloud_direct_draas_destination = response.then().extract().path("data.cloud_direct_vmware.cloud_direct_draas_destination");
		if(cloud_direct_draas_destination==null || cloud_direct_draas_destination.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_cloud_direct_draas_destination, response_cloud_direct_draas_destination);
		} else {
			spogServer.assertResponseItem(cloud_direct_draas_destination, response_cloud_direct_draas_destination);
		}
		
		String responseOri_schedule = responseOri.then().extract().path("data.cloud_direct_vmware.schedule");
		String response_schedule = response.then().extract().path("data.cloud_direct_vmware.schedule");
		if(schedule==null || schedule.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_schedule, response_schedule);
		} else {
			spogServer.assertResponseItem(schedule, response_schedule);
		}
		
		String responseOri_schedule_type = responseOri.then().extract().path("data.cloud_direct_vmware.schedule_type");
		String response_schedule_type = response.then().extract().path("data.cloud_direct_vmware.schedule_type");
		if(schedule_type==null || schedule_type.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_schedule_type, response_schedule_type);
		} else {
			spogServer.assertResponseItem(schedule_type, response_schedule_type);
		}
		
		String responseOri_agent_name = responseOri.then().extract().path("data.cloud_direct_vmware.agent.agent_name");
		String response_agent_name = response.then().extract().path("data.cloud_direct_vmware.agent.agent_name");
		if(agent_name==null || agent_name.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_agent_name, response_agent_name);
		} else {
			spogServer.assertResponseItem(agent_name, response_agent_name);
		}
		
		String responseOri_agent_current_version = responseOri.then().extract().path("data.cloud_direct_vmware.agent.agent_current_version");
		String response_agent_current_version = response.then().extract().path("data.cloud_direct_vmware.agent.agent_current_version");
		if(agent_current_version==null || agent_current_version.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_agent_current_version, response_agent_current_version);
		} else {
			spogServer.assertResponseItem(agent_current_version, response_agent_current_version);
		}
		
		String responseOri_agent_upgrade_version = responseOri.then().extract().path("data.cloud_direct_vmware.agent.agent_upgrade_version");
		String response_agent_upgrade_version = response.then().extract().path("data.cloud_direct_vmware.agent.agent_upgrade_version");
		if(agent_upgrade_version==null || agent_upgrade_version.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_agent_upgrade_version, response_agent_upgrade_version);
		} else {
			spogServer.assertResponseItem(agent_upgrade_version, response_agent_upgrade_version);
		}
		
		String responseOri_agent_upgrade_link = responseOri.then().extract().path("data.cloud_direct_vmware.agent.agent_upgrade_link");
		String response_agent_upgrade_link = response.then().extract().path("data.cloud_direct_vmware.agent.agent_upgrade_link");
		if(agent_upgrade_link==null || agent_upgrade_link.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_agent_upgrade_link, response_agent_upgrade_link);
		} else {
			spogServer.assertResponseItem(agent_upgrade_link, response_agent_upgrade_link);
		}
		
		Boolean responseOri_agent_autoupgrade = responseOri.then().extract().path("data.cloud_direct_vmware.agent.agent_autoupgrade");
		Boolean response_agent_autoupgrade = response.then().extract().path("data.cloud_direct_vmware.agent.agent_autoupgrade");
		if(agent_autoupgrade==null || agent_autoupgrade.equalsIgnoreCase("none") || agent_autoupgrade == "") {
			assertEquals(responseOri_agent_autoupgrade, response_agent_autoupgrade);
		} else {
			assertEquals(response_agent_autoupgrade, Boolean.valueOf(agent_autoupgrade));
		}
	
		String responseOri_vmware_api_version = responseOri.then().extract().path("data.cloud_direct_vmware.vmware_api_version");
		String response_vmware_api_version = response.then().extract().path("data.cloud_direct_vmware.vmware_api_version");
		if(vmware_api_version==null || vmware_api_version.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_vmware_api_version, response_vmware_api_version);
		} else {
			spogServer.assertResponseItem(vmware_api_version, response_vmware_api_version);
		}
		
		String responseOri_vmware_vcenter_host = responseOri.then().extract().path("data.cloud_direct_vmware.vmware_vcenter_host");
		String response_vmware_vcenter_host = response.then().extract().path("data.cloud_direct_vmware.vmware_vcenter_host");
		if(vmware_vcenter_host==null || vmware_vcenter_host.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_vmware_vcenter_host, response_vmware_vcenter_host);
		} else {
			spogServer.assertResponseItem(vmware_vcenter_host, response_vmware_vcenter_host);
		}
		
		String responseOri_bind_datacenter = responseOri.then().extract().path("data.cloud_direct_vmware.bind_datacenter");
		String response_bind_datacenter = response.then().extract().path("data.cloud_direct_vmware.bind_datacenter");
		if(bind_datacenter==null || bind_datacenter.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_bind_datacenter, response_bind_datacenter);
		} else {
			spogServer.assertResponseItem(bind_datacenter, response_bind_datacenter);
		}
		
		String responseOri_bind_host = responseOri.then().extract().path("data.cloud_direct_vmware.bind_host");
		String response_bind_host = response.then().extract().path("data.cloud_direct_vmware.bind_host");
		if(bind_host==null || bind_host.equalsIgnoreCase("none")) {
			spogServer.assertResponseItem(responseOri_bind_host, response_bind_host);
		} else {
			spogServer.assertResponseItem(bind_host, response_bind_host);
		}
		
		ArrayList<String> responseOri_vmware_resource_pools = responseOri.then().extract().path("data.cloud_direct_vmware.vmware_resource_pools");
		ArrayList<String> response_vmware_resource_pools = response.then().extract().path("data.cloud_direct_vmware.vmware_resource_pools");
		if(vmware_resource_pools==null || vmware_resource_pools.equalsIgnoreCase("none")) {
			spogServer.assertFilterItem(String.join(",", responseOri_vmware_resource_pools), response_vmware_resource_pools);
		} else {
			spogServer.assertFilterItem(vmware_resource_pools, response_vmware_resource_pools);
		}
		
		ArrayList<String> responseOri_vmware_data_stores = responseOri.then().extract().path("data.cloud_direct_vmware.vmware_data_stores");
		ArrayList<String> response_vmware_data_stores = response.then().extract().path("data.cloud_direct_vmware.vmware_data_stores");
		if(vmware_data_stores==null || vmware_data_stores.equalsIgnoreCase("none")) {
			spogServer.assertFilterItem(String.join(",", responseOri_vmware_data_stores), response_vmware_data_stores);
		} else {
			spogServer.assertFilterItem(vmware_data_stores, response_vmware_data_stores);
		}
		
		ArrayList<String> responseOri_vmware_data_centers = responseOri.then().extract().path("data.cloud_direct_vmware.vmware_data_centers");
		ArrayList<String> response_vmware_data_centers = response.then().extract().path("data.cloud_direct_vmware.vmware_data_centers");
		if(vmware_data_centers==null || vmware_data_centers.equalsIgnoreCase("none")) {
			spogServer.assertFilterItem(String.join(",", responseOri_vmware_data_centers), response_vmware_data_centers);
		} else {
			spogServer.assertFilterItem(vmware_data_centers, response_vmware_data_centers);
		}
		
		ArrayList<String> responseOri_vmware_host_systems = responseOri.then().extract().path("data.cloud_direct_vmware.vmware_host_systems");
		ArrayList<String> response_vmware_host_systems = response.then().extract().path("data.cloud_direct_vmware.vmware_host_systems");
		if(vmware_host_systems==null || vmware_host_systems.equalsIgnoreCase("none")) {
			spogServer.assertFilterItem(String.join(",", responseOri_vmware_host_systems), response_vmware_host_systems);
		} else {
			spogServer.assertFilterItem(vmware_host_systems, response_vmware_host_systems);
		}
		
	}
	
	/* Get Hypervisors amount
	 * 
	 * @autor Rakesh.Chalamala
	 * @param expected_response
	 * @param expectedStatusCode
	 * @param ExpectedErrorMessage
	 * @param response
	 */
	public void getHypervisorsAmount(HashMap<String, Object> expected_response,String user,  int expectedStatusCode,SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		test.log(LogStatus.INFO, "Call the rest API to get the response");
		Response response = spogHypervisorsInvoker.getHypervisorsAmount();
		spogServer.checkResponseStatus(response, expectedStatusCode, test);
		
		if(expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			HashMap<String , Object> actual_response = response.then().extract().path("data");
			
			test.log(LogStatus.INFO, "Compare the amount");
			if (!user.contains("csr")) {
				spogServer.assertResponseItem(expected_response.get("amount"), actual_response.get("amount"), test);	
			} else {
				assertTrue(actual_response.get("amount") != null , "Actual amount of all hypervisors: "+actual_response.get("amount"));
			}			
			
			test.log(LogStatus.INFO, "Compare the amount_type");
			spogServer.assertResponseItem(expected_response.get("amount_type"), actual_response.get("amount_type"), test);
			
		}else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();			
			spogServer.checkErrorCode(response,code);
			test.log(LogStatus.PASS, "The error code matched with the expected "+code);
			spogServer.checkErrorMessage(response,message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}
	
	/**
	 * Creates the hypervisor and returns the response
	 * 
	 * @author Rakesh.Chalamala
	 * 
	 * @param hypervisor_id
	 * @param hypervisor_name
	 * @param hypervisor_type
	 * @param hypervisor_product
	 * @param status
	 * @param site
	 * @param organization_id
	 * @param sync_discovered_vms
	 * @param vm_refresh_ts
	 * @param cloud_direct_baas_destination
	 * @param cloud_direct_draas_destination
	 * @param schedule
	 * @param schedule_type
	 * @param agent_name
	 * @param agent_current_version
	 * @param agent_upgrade_version
	 * @param agent_upgrade_link
	 * @param agent_autoupgrade
	 * @param vmware_api_version
	 * @param vmware_vcenter_host
	 * @param vmware_resource_pools
	 * @param vmware_data_stores
	 * @param vmware_data_centers
	 * @param vmware_host_systems
	 * @param bind_datacenter
	 * @param bind_host
	 * @param test
	 * @return
	 */
	public Response createHypervisor(String hypervisor_id, String hypervisor_name, String hypervisor_type, String hypervisor_product, String status, 
			String site, String organization_id, 
			String sync_discovered_vms, String vm_refresh_ts, String cloud_direct_baas_destination, String cloud_direct_draas_destination, String schedule, String schedule_type, 
			String agent_name, String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade,
			String vmware_api_version, String vmware_vcenter_host, String vmware_resource_pools, String vmware_data_stores, String vmware_data_centers, String vmware_host_systems,
			String bind_datacenter, String bind_host, ExtentTest test) {
		
		Map<String, Object> hypervisorInfo = composeHypervisorInfo(hypervisor_id, hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, 
				sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, 
				agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, 
				vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host);
		
		Response response = spogHypervisorsInvoker.createHypervisor(hypervisorInfo);
		
		return response;
	}

}

	

