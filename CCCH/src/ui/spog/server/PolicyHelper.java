package ui.spog.server;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.ArrayList;
import java.util.HashMap;

import org.openqa.selenium.WebElement;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.UIConstants;
import ui.base.common.BasePage;
import ui.base.factory.ElementFactory;
import ui.spog.pages.protect.PolicyPage;

public class PolicyHelper extends SPOGUIServer {

	PolicyPage policyPage = new PolicyPage();
	BasePage basepage=new BasePage();

	public PolicyHelper(String browserType, int maxTimeWaitSec) {
		super(browserType, maxTimeWaitSec);
		// TODO Auto-generated constructor stub
	}

	public void addBaasPolicy(String policyName, String policyDescription, String sourceNames, String activeType,
			String activeOption, String drives, String paths, String destVolumeName, String enableLocal,
			String localDestination, String hour, String minute, String clock,
			ArrayList<HashMap<String, String>> throttleScheduleArray, String excludeRules, String expectedStatus) {

		PolicyPage policyPage = goToPolicyPage();
		policyPage.clickAddPolicy();
		policyPage.editBasicInfo(policyName, UIConstants.POLICY_BAAS, policyDescription);
		policyPage.editSourceInfo(sourceNames);
		policyPage.editDestinationWhatToProtectInfo4BaasPolicy(activeType, activeOption, drives, paths);
		policyPage.editDestinationWhereToProtect(activeType, destVolumeName, enableLocal, localDestination);
		policyPage.editDestinationWhenToProtect(UIConstants.POLICY_BAAS, null, hour, minute, clock,
				throttleScheduleArray);
		if (activeType == UIConstants.ACTIVE_TYPE_FILE) {
			// set additional
			policyPage.editAdditionalSetting(excludeRules);
		}
		policyPage.clickCreatePolicy();
		int repeat = 0;
		String status = null;
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//policyPage.checkAlertShows("policy",10);
		do {
			policyPage = goToPolicyPage();
			status = policyPage.getPolicyStatus(policyName);
			repeat++;
			System.out.println("repeat " + repeat);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (status.equalsIgnoreCase("deploying") && (repeat < 20));

		assertEquals(status.toLowerCase(), expectedStatus.toLowerCase());

	}

	public HashMap<String, String> composeThrottleScheduleHashMap(String rate, String schedule, String startTime,
			String startTimeClock, String endTime, String endTimeClock) {

		HashMap<String, String> throttleSchedule = new HashMap<String, String>();
		throttleSchedule.put("rate", rate);
		throttleSchedule.put("schedule", schedule);
		throttleSchedule.put("starTime", startTime);
		throttleSchedule.put("startTimeClock", startTimeClock);
		throttleSchedule.put("endTime", endTime);
		throttleSchedule.put("endTimeClock", endTimeClock);
		return throttleSchedule;

	}

	public void addDraasPolicy(String policyName, String policyDescription, String sourceNames, String activeOption,
			String drives, String destVolumeName, String enableLocal, String localDestination, String backupSchedule,
			String hour, String minute, String clock, ArrayList<HashMap<String, String>> throttleScheduleArray,
			String expectedStatus) {

		PolicyPage policyPage = goToPolicyPage();
		policyPage.clickAddPolicy();
		policyPage.editBasicInfo(policyName, UIConstants.POLICY_DRAAS, policyDescription);
		policyPage.editSourceInfo(sourceNames);

		policyPage.editDestinationWhatToProtectInfo4DraasPolicy(activeOption, drives);
		policyPage.editDestinationWhereToProtect(UIConstants.ACTIVE_TYPE_IMAGE, destVolumeName, enableLocal,
				localDestination);
		policyPage.editDestinationWhenToProtect(UIConstants.POLICY_DRAAS, backupSchedule, hour, minute, clock,
				throttleScheduleArray);

		policyPage.clickCreatePolicy();
		int repeat = 0;
		String status = null;

		do {
			policyPage = goToPolicyPage();
			status = policyPage.getPolicyStatus(policyName);
			repeat++;
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (status.equalsIgnoreCase("deploying") && (repeat < 10));

		assertEquals(status.toLowerCase(), expectedStatus.toLowerCase());

	}

	/**
	 * configureReplicationFromTask
	 * 
	 * @author Ramya.Nagepalli
	 * @param policyName
	 * @param policyDescription
	 * @param destVolumeName
	 * @param expectedScheduleArray
	 * @param retentionInputs
	 * @param test
	 */

	public void configureReplicationFromTask(String policyName, String policyDescription, String destVolumeName,
			ArrayList<HashMap<String, String>> expectedScheduleArray, HashMap<String, String> retentionInputs,
			ExtentTest test) {
		test.log(LogStatus.INFO, "Edit the basic information of the policy");
		policyPage.editBasicInfo(policyName, UIConstants.POLICY_REPLICATION, policyDescription);

		test.log(LogStatus.INFO, "set the task type of the policy");
		policyPage.editDestinationWhatToProtectInfo4ReplicationFromTask(test);

		test.log(LogStatus.INFO, "set the cloud hybrid destination : " + destVolumeName);
		policyPage.editDestinationWhereToProtectInfo4replicationFromTask(destVolumeName, test);

		test.log(LogStatus.INFO, "set the merge schedule to run");
		policyPage.editDestinationWhenToProtect4ReplicatioFromTask(expectedScheduleArray, test);

		test.log(LogStatus.INFO, "set the retention inputs for the policy");
		policyPage.editAdditionalSettings4ReplicationFromTask(retentionInputs, test);
	}

	/**
	 * configureReplicateToRemotelyManagedRPSTask
	 * 
	 * @author Ramya.Nagepalli
	 * @param severname
	 * @param server_username
	 * @param server_password
	 * @param server_port
	 * @param server_protocal
	 * @param remote_policy
	 * @param retry_minutes
	 * @param retry_num
	 * @param throttleScheduleArray
	 * @param mergeScheduleArray
	 * @param test
	 */
	public void configureReplicateToRemotelyManagedRPSTask(String severname, String server_username,
			String server_password, String server_port, String server_protocal, String remote_policy,
			String retry_minutes, String retry_num, ArrayList<HashMap<String, String>> throttleScheduleArray,
			ArrayList<HashMap<String, String>> mergeScheduleArray, ExtentTest test) {

		test.log(LogStatus.INFO, " edit Destination WhatToProtect Info for ReplicationToRemotelyManagedRPSTask");
		policyPage.editDestinationWhatToProtectInfo4ReplicationToRemotelyManagedRPSTask(test);

		test.log(LogStatus.INFO, "edit destination whereToProtect Info for ReplicateToRemotelyManagedRPSTask");
		policyPage.editDestinationWhereToProtectInfo4ReplicationToRemotelyManagedRPSTask(severname, server_username,
				server_password, server_port, server_protocal, remote_policy, retry_minutes, retry_num, test);

		test.log(LogStatus.INFO, "edit destination whereToProtect Info for ReplicateToRemotelyManagedRPSTask");
		policyPage.editDestinationWhenToProtectInfo4ReplicationToRemotelyManagedRPSTask(throttleScheduleArray,
				mergeScheduleArray, test);
	}

	/**
	 * addCloudHybridReplicationPolicy
	 * 
	 * @author Ramya.Nagepalli
	 * @param policyName
	 * @param policyDescription
	 * @param expectedScheduleArray
	 * @param destVolumeName
	 * @param retentionInputs
	 * @param expectedStatus
	 * @param test
	 * @param severname
	 * @param server_username
	 * @param server_password
	 * @param server_port
	 * @param server_protocal
	 * @param remote_policy
	 * @param retry_minutes
	 * @param retry_num
	 * @param throttleScheduleArray
	 * @param mergeScheduleArray
	 */
	public void addCloudHybridReplicationPolicy(String policyName, String policyDescription,
			ArrayList<HashMap<String, String>> expectedScheduleArray, String destVolumeName,
			HashMap<String, String> retentionInputs, String expectedStatus, String severname, String server_username,
			String server_password, String server_port, String server_protocal, String remote_policy,
			String retry_minutes, String retry_num, ArrayList<HashMap<String, String>> throttleScheduleArray,
			ArrayList<HashMap<String, String>> mergeScheduleArray, ExtentTest test) {

		test.log(LogStatus.INFO,
				"navigate to policies page and click on add policy button to create a  cloud hybrid replication policy");
		PolicyPage policyPage = goToPolicyPage();
		policyPage.clickAddPolicy();

		test.log(LogStatus.INFO, "configure replicate from remotely managed RPS task ");
		configureReplicationFromTask(policyName, policyDescription, destVolumeName, expectedScheduleArray,
				retentionInputs, test);

		test.log(LogStatus.INFO, "add new Destination to add replicate to remotely managed rps task");
		policyPage.addNewDestinationInPolicy(test);

		test.log(LogStatus.INFO, "Configure Replicate to remotely managed RPS");
		configureReplicateToRemotelyManagedRPSTask(severname, server_username, server_password, server_port,
				server_protocal, remote_policy, retry_minutes, retry_num, throttleScheduleArray, mergeScheduleArray,
				test);

		test.log(LogStatus.INFO,
				"click on create policy and wait until it changes the state from deploying to success");
		policyPage.clickCreatePolicy();
		int repeat = 0;
		String status = null;

		do {
			policyPage = goToPolicyPage();
			status = policyPage.getPolicyStatus(policyName);
			repeat++;
			basepage.waitForSeconds(5);
		} while (status.equalsIgnoreCase("deploying") && (repeat < 10));

		assertEquals(status.toLowerCase(), expectedStatus.toLowerCase());
	}

	/**
	 * editCloudHybridReplicationPolicy
	 * 
	 * @author Ramya.Nagepalli
	 * @param mod_policyname
	 * @param mod_policyDescription
	 * @param policyName
	 * @param destVolumeName
	 * @param expectedScheduleArray
	 * @param retentionInputs
	 * @param expectedStatus
	 * @param test
	 */
	public void editCloudHybridReplicationPolicy(String mod_policyname, String mod_policyDescription, String policyName,
			String destVolumeName, ArrayList<HashMap<String, String>> expectedScheduleArray,
			HashMap<String, String> retentionInputs, String expectedStatus, ExtentTest test) {
		PolicyPage policyPage = goToPolicyPage();
		test.log(LogStatus.INFO, "Check for the existing policy in the page");

		WebElement policy_ele = ElementFactory.getElementByXpath("//span[text(),'" + policyName + "']");

		basepage.waitUntilElementDisplayAndEnable(policy_ele);

		if (policy_ele.isDisplayed()) {
			test.log(LogStatus.INFO, "click on policy to modify");
			policy_ele.click();

			test.log(LogStatus.INFO, "modify policy_name and description");
			policyPage.editBasicInfo(mod_policyname, UIConstants.POLICY_REPLICATION, mod_policyDescription);

			test.log(LogStatus.INFO, "change the destination of the policy : " + destVolumeName);
			policyPage.editDestinationWhereToProtectInfo4replicationFromTask(destVolumeName, test);

			test.log(LogStatus.INFO, "set the merge schedule to run");
			policyPage.editDestinationWhenToProtect4ReplicatioFromTask(expectedScheduleArray, test);

			test.log(LogStatus.INFO, "set the retention of the policy");
			policyPage.editAdditionalSettings4ReplicationFromTask(retentionInputs, test);

			test.log(LogStatus.INFO, "save the policy");
			policyPage.clickSavePolicy();

			int repeat = 0;
			String status = null;

			do {
				policyPage = goToPolicyPage();
				status = policyPage.getPolicyStatus(policyName);
				repeat++;
				basepage.waitForSeconds(5);
			} while (status.equalsIgnoreCase("deploying") && (repeat < 10));

			assertEquals(status.toLowerCase(), expectedStatus.toLowerCase());
		} else {

			test.log(LogStatus.INFO, "policy with given name does not exist :" + policyName);
			assertFalse(true);
		}

	}

	/**
	 * composeRetentionInputs- to configure retention for Replication task
	 * 
	 * @author Ramya.Nagepalli
	 * 
	 * @param daily
	 * @param weekly
	 * @param monthly
	 * @param manual
	 * @param test
	 * @return
	 */
	public HashMap<String, String> composeRetentionInputs(String daily, String weekly, String monthly, String manual,
			ExtentTest test) {

		HashMap<String, String> composeRetention = new HashMap<String, String>();

		composeRetention.put("daily", daily);
		composeRetention.put("weekly", weekly);
		composeRetention.put("monthly", monthly);
		composeRetention.put("manual", manual);

		return composeRetention;
	}
	
	/**
	 * composeMergeScheduleHashMap
	 * 
	 * @author Ramya.Nagepalli
	 * @param schedule
	 * @param startTime
	 * @param startTimeClock
	 * @param endTime
	 * @param endTimeClock
	 * @return
	 */
	public HashMap<String, String> composeMergeScheduleHashMap(String schedule, String startTime, String startTimeClock,
			String endTime, String endTimeClock) {

		HashMap<String, String> throttleSchedule = new HashMap<String, String>();
		throttleSchedule.put("schedule", schedule);
		throttleSchedule.put("starTime", startTime);
		throttleSchedule.put("startTimeClock", startTimeClock);
		throttleSchedule.put("endTime", endTime);
		throttleSchedule.put("endTimeClock", endTimeClock);
		return throttleSchedule;

	}


}
