package InvokerServer;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import dataPreparation.JsonPreparation;
import dataPreparation.policyPreparation;
import genericutil.ErrorHandler;
import invoker.SPOGAlertsInvoker;
import invoker.SPOGAlertsInvoker;
import io.restassured.response.Response;

public class SPOGAlertsServer {
	private SPOGAlertsInvoker spogAlertsInvoker;
	private SPOGServer spogServer;
	private static policyPreparation pp = new policyPreparation();
	static ErrorHandler errorHandle = ErrorHandler.getErrorHandler();
	private static JsonPreparation jp = new JsonPreparation();

	public SPOGAlertsServer(String baseURI, String port) {
		spogAlertsInvoker = new SPOGAlertsInvoker(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
	}

	public void setToken(String token) {
		spogAlertsInvoker.setToken(token);
	}

	/**
	 * submitEmailsForAlertswithcheck
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param expectedData
	 * @param user_id
	 * @param user_email
	 * @param actions
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * @return
	 */
	public String submitEmailsForAlertswithcheck(String token, Map<String, Object> expectedData, String user_id,
			String user_email, ArrayList<String> actions, int expectedStatusCode, SpogMessageCode expectedErrorMessage,
			ExtentTest test) {

		String alert_email_recipients_id = "";
		Response response = submitEmailsForAlerts(token, expectedData, expectedStatusCode, test);
		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {
			HashMap<String, Object> actualData = response.then().extract().path("data");

			test.log(LogStatus.INFO, "compare actual data with expected data");
			assertEquals(actualData.get("alert_type").toString(), expectedData.get("alert_type").toString());
			assertEquals(actualData.get("alert_name").toString(), expectedData.get("alert_name").toString());
			assertEquals(actualData.get("organization_id").toString(), expectedData.get("organization_id").toString());
			assertEquals(actualData.get("recipient").toString(), expectedData.get("recipient").toString());

			HashMap<String, Object> act_user = (HashMap<String, Object>) actualData.get("create_user");
			assertEquals(act_user.get("create_user_id"), user_id);
			assertEquals(act_user.get("create_username").toString().toLowerCase(), user_email.toLowerCase().toString());

			HashMap<String, Object> act_report_for = (HashMap<String, Object>) actualData.get("report_for");
			HashMap<String, Object> exp_report_for = (HashMap<String, Object>) expectedData.get("report_for");
			assertEquals(act_report_for.get("type"), exp_report_for.get("type").toString());

			ArrayList<HashMap<String, Object>> act_details = (ArrayList<HashMap<String, Object>>) act_report_for
					.get("details");
			ArrayList<String> exp_details = (ArrayList<String>) exp_report_for.get("details");
			if (!(act_details == null && exp_details == null)) {
				for (int i = 0; i < act_details.size(); i++) {
					if (exp_report_for.get("type").equals("selected_source_groups"))
						assertEquals(act_details.get(i).get("source_group_id").toString(),
								exp_details.get(i).toString());
					if (exp_report_for.get("type").equals("selected_organizations"))
						assertEquals(act_details.get(i).get("organization_id").toString(),
								exp_details.get(i).toString());
				}

			}
			assertEquals(actualData.get("available_actions").toString(), actions.toString());
			alert_email_recipients_id = actualData.get("alert_email_recipients_id").toString();

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedErrorMessage);
		}
		return alert_email_recipients_id;
	}

	/**
	 * submitEmailsForAlerts
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param expectedData
	 * @param expectedStatusCode
	 * @param test
	 * @return response
	 */
	private Response submitEmailsForAlerts(String token, Map<String, Object> expectedData, int expectedStatusCode,
			ExtentTest test) {
		test.log(LogStatus.INFO, "submit Emails For Alerts");
		Response response = spogAlertsInvoker.submitEmailsForAlerts(token, expectedData, test);
		spogServer.checkResponseStatus(response, expectedStatusCode);
		return response;
	}

	/**
	 * deleteEmailsForSpecifiedAlert
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param alert_email_id
	 * @param expectedStatusCode
	 * @param test
	 * @param expectedErrorMessage
	 */
	public void deleteEmailsForSpecifiedAlert(String token, String alert_email_id, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {
		test.log(LogStatus.INFO, "delete Emails For Specified Alert");
		Response response = spogAlertsInvoker.deleteEmailsForSpecifiedAlert(token, alert_email_id, test);
		spogServer.checkResponseStatus(response, expectedStatusCode);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

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
	 * putEmailsForSpecifiedAlertwithcheck
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param alert_email_id
	 * @param expectedData
	 * @param user_id
	 * @param user_email
	 * @param actions
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 * @return
	 */
	public Response putEmailsForSpecifiedAlertwithcheck(String token, String alert_email_id,
			Map<String, Object> expectedData, String user_id, String user_email, ArrayList<String> actions,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage, ExtentTest test) {

		Response response = putEmailsForSpecifiedAlert(token, alert_email_id, expectedData, expectedStatusCode, test);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			HashMap<String, Object> actualData = response.then().extract().path("data");

			test.log(LogStatus.INFO, "compare actual data with expected data");
			assertEquals(actualData.get("alert_type").toString(), expectedData.get("alert_type").toString());
			assertEquals(actualData.get("alert_name").toString(), expectedData.get("alert_name").toString());
			assertEquals(actualData.get("organization_id").toString(), expectedData.get("organization_id").toString());
			assertEquals(actualData.get("recipient").toString(), expectedData.get("recipient").toString());

			HashMap<String, Object> act_user = (HashMap<String, Object>) actualData.get("create_user");
			assertEquals(act_user.get("create_user_id"), user_id);
			assertEquals(act_user.get("create_username").toString().toLowerCase(), user_email.toLowerCase().toString());

			HashMap<String, Object> act_report_for = (HashMap<String, Object>) actualData.get("report_for");
			HashMap<String, Object> exp_report_for = (HashMap<String, Object>) expectedData.get("report_for");
			assertEquals(act_report_for.get("type"), exp_report_for.get("type").toString());

			ArrayList<HashMap<String, Object>> act_details = (ArrayList<HashMap<String, Object>>) act_report_for
					.get("details");
			ArrayList<String> exp_details = (ArrayList<String>) exp_report_for.get("details");
			if (!(act_details == null && exp_details == null)) {
				for (int i = 0; i < act_details.size(); i++) {
					if (exp_report_for.get("type").equals("selected_source_groups"))
						assertEquals(act_details.get(i).get("source_group_id").toString(),
								exp_details.get(i).toString());
					if (exp_report_for.get("type").equals("selected_organizations"))
						assertEquals(act_details.get(i).get("organization_id").toString(),
								exp_details.get(i).toString());
				}

			}
			assertEquals(actualData.get("available_actions").toString(), actions.toString());

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedErrorMessage);
		}
		return response;
	}

	/**
	 * putEmailsForSpecifiedAlert
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param expectedData
	 * @param expectedStatusCode
	 * @param test
	 * @return response
	 */
	public Response putEmailsForSpecifiedAlert(String token, String alert_email_id, Map<String, Object> expectedData,
			int expectedStatusCode, ExtentTest test) {
		test.log(LogStatus.INFO, "update Emails For Alerts");
		Response response = spogAlertsInvoker.putEmailsForSpecifiedAlert(token, alert_email_id, expectedData, test);
		spogServer.checkResponseStatus(response, expectedStatusCode);
		return response;
	}

	/**
	 * getEmailsForAlertswithcheck
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param expectedData
	 * @param test
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @return alert_email_recipients_id
	 */
	public Response getEmailsForAlertswithcheck(String token, ArrayList<HashMap> expectedData, String user_id,
			String user_email, ArrayList<String> allowed_actions, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		Response response = getEmailsForAlerts(token, expectedData, expectedStatusCode, test);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap> actualData = response.then().extract().path("data");
			test.log(LogStatus.INFO, "compare actual data with expected data");

			actualData.stream().forEach(act -> {
				expectedData.stream().forEach(exp -> {
					if (act.get("alert_type").toString().equals(exp.get("alert_type").toString())) {
						assertEquals(act.get("alert_type").toString(), exp.get("alert_type").toString());
						assertEquals(act.get("alert_name").toString(), exp.get("alert_name").toString());
						assertEquals(act.get("organization_id").toString(), exp.get("organization_id").toString());
						assertEquals(act.get("recipient").toString(), exp.get("recipient").toString());

						HashMap<String, Object> act_user = (HashMap<String, Object>) act.get("create_user");
						assertEquals(act_user.get("create_user_id"), user_id);
						assertEquals(act_user.get("create_username").toString().toLowerCase(),
								user_email.toLowerCase().toString());

						HashMap<String, Object> act_report_for = (HashMap<String, Object>) act.get("report_for");
						HashMap<String, Object> exp_report_for = (HashMap<String, Object>) exp.get("report_for");
						assertEquals(act_report_for.get("type"), exp_report_for.get("type").toString());

						ArrayList<HashMap<String, Object>> act_details = (ArrayList<HashMap<String, Object>>) act_report_for
								.get("details");
						ArrayList<String> exp_details = (ArrayList<String>) exp_report_for.get("details");
						if (!(act_details == null && exp_details == null)) {
							for (int i = 0; i < act_details.size(); i++) {
								if (exp_report_for.get("type").equals("selected_source_groups"))
									assertEquals(act_details.get(i).get("source_group_id").toString(),
											exp_details.get(i).toString());
								if (exp_report_for.get("type").equals("selected_organizations"))
									assertEquals(act_details.get(i).get("organization_id").toString(),
											exp_details.get(i).toString());
							}

						}
						assertEquals(act.get("available_actions").toString(), allowed_actions.toString());
					}
				});

			});

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedErrorMessage);
		}
		return response;
	}

	/**
	 * getEmailsForAlerts
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param expectedData
	 * @param expectedStatusCode
	 * @param test
	 * @return response
	 */
	public Response getEmailsForAlerts(String token, ArrayList<HashMap> expectedData, int expectedStatusCode,
			ExtentTest test) {
		test.log(LogStatus.INFO, "get Emails For Alerts");
		Response response = spogAlertsInvoker.getEmailsForAlerts(token, test);
		spogServer.checkResponseStatus(response, expectedStatusCode);
		return response;
	}

	/**
	 * getEmailsForAlertswithcheck
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param expectedData
	 * @param test
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @return alert_email_recipients_id
	 */
	public Response getEmailsForSpecifiedAlertswithcheck(String token, Map<String, Object> expectedData,
			String alert_email_id, String user_id, String user_email, ArrayList<String> actions, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		Response response = getEmailsForSpecifiedAlert(token, alert_email_id, expectedStatusCode, test);
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			HashMap<String, Object> actualData = response.then().extract().path("data");

			test.log(LogStatus.INFO, "compare actual data with expected data");
			assertEquals(actualData.get("alert_type").toString(), expectedData.get("alert_type").toString());
			assertEquals(actualData.get("alert_name").toString(), expectedData.get("alert_name").toString());
			assertEquals(actualData.get("organization_id").toString(), expectedData.get("organization_id").toString());
			assertEquals(actualData.get("recipient").toString(), expectedData.get("recipient").toString());

			HashMap<String, Object> act_user = (HashMap<String, Object>) actualData.get("create_user");
			assertEquals(act_user.get("create_user_id"), user_id);
			assertEquals(act_user.get("create_username").toString().toLowerCase(), user_email.toLowerCase().toString());

			HashMap<String, Object> act_report_for = (HashMap<String, Object>) actualData.get("report_for");
			HashMap<String, Object> exp_report_for = (HashMap<String, Object>) expectedData.get("report_for");
			assertEquals(act_report_for.get("type"), exp_report_for.get("type").toString());

			ArrayList<HashMap<String, Object>> act_details = (ArrayList<HashMap<String, Object>>) act_report_for
					.get("details");
			ArrayList<String> exp_details = (ArrayList<String>) exp_report_for.get("details");
			if (!(act_details == null && exp_details == null)) {
				for (int i = 0; i < act_details.size(); i++) {
					if (exp_report_for.get("type").equals("selected_source_groups"))
						assertEquals(act_details.get(i).get("source_group_id").toString(),
								exp_details.get(i).toString());
					if (exp_report_for.get("type").equals("selected_organizations"))
						assertEquals(act_details.get(i).get("organization_id").toString(),
								exp_details.get(i).toString());
				}

			}
			assertEquals(actualData.get("available_actions").toString(), actions.toString());
			alert_email_id = actualData.get("alert_email_recipients_id").toString();

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedErrorMessage);
		}
		return response;
	}

	/**
	 * getEmailsForSpecifiedAlert
	 * 
	 * @author Ramya.Nagepalli
	 * @param token
	 * @param expectedData
	 * @param expectedStatusCode
	 * @param test
	 * @return response
	 */
	public Response getEmailsForSpecifiedAlert(String token, String alert_email_id, int expectedStatusCode,
			ExtentTest test) {
		test.log(LogStatus.INFO, "get Emails For Specified Alerts");
		Response response = spogAlertsInvoker.getEmailsForSpecifiedAlert(token, alert_email_id, test);
		spogServer.checkResponseStatus(response, expectedStatusCode);
		return response;
	}

	/**
	 * the function is used to create Organization alert
	 * 
	 * @author ramya.Nagepalli
	 * @param job_id
	 * @param jobtype
	 * @param jobSeverity
	 * @param adminToken
	 * @param org_id
	 *            return response
	 * @param alert_data
	 * @param alert_details
	 * @param expectedcode
	 */
	public Response createOrganizationAlerts(String adminToken, String org_id, String job_id, String jobtype,
			String jobSeverity, HashMap<String, Object> alert_details, ArrayList<String> alert_data, int statusCode,
			ExtentTest test) {
		Map<String, Object> alertInfo = new HashMap<String, Object>();
		alertInfo = jp.composeAlertInfo(job_id, jobtype, jobSeverity, alert_details, alert_data);
		Response response = spogAlertsInvoker.createOrganizationAlerts(adminToken, org_id, alertInfo);
		spogServer.checkResponseStatus(response, statusCode);
		return response;

	}

	/**
	 * the function is used to get Organization alerts
	 * 
	 * @author ramya.Nagepalli
	 * @param adminToken
	 * @param org_id
	 * @param statusCode
	 *            return response
	 */
	public Response getOrganizationAlerts(String adminToken, String org_id, int statusCode, ExtentTest test) {
		Response response = spogAlertsInvoker.getOrganizationAlerts(adminToken, org_id);
		spogServer.checkResponseStatus(response, statusCode);
		return response;

	}

	/**
	 * the function is used to get Organization alerts
	 * 
	 * @author ramya.Nagepalli
	 * @param adminToken
	 * @param org_id
	 * @param alert_id
	 * @param statusCode
	 *            return response
	 */
	public Response getOrganizationAlertsByAlertId(String adminToken, String org_id, String alert_id, int statusCode,
			ExtentTest test) {
		Response response = spogAlertsInvoker.getOrganizationAlertsByAlertId(adminToken, org_id, alert_id);
		spogServer.checkResponseStatus(response, statusCode);
		return response;

	}

	/**
	 * the function is used to delete Organization alerts by alert id
	 * 
	 * @author ramya.Nagepalli
	 * @param adminToken
	 * @param org_id
	 * @param alert_id
	 * @param statusCode
	 *            return response
	 */
	public Response deleteOrganizationAlertsByAlertId(String adminToken, String org_id, String alert_id, int statusCode,
			ExtentTest test) {
		Response response = spogAlertsInvoker.deleteOrganizationAlertsByAlertId(adminToken, org_id, alert_id);
		spogServer.checkResponseStatus(response, statusCode);
		return response;
	}

	/**
	 * deleteOrganizationAlertsByAlertIdWithCheck
	 * 
	 * @author Ramya.Nagepalli
	 * @param adminToken
	 * @param org_id
	 * @param alert_id
	 * @param errorCode
	 * @param errormessage
	 * @param test
	 */
	public void deleteOrganizationAlertsByAlertIdWithCheck(String adminToken, String org_id, String alert_id,
			int errorCode, SpogMessageCode errormessage, ExtentTest test) {
		Response response = deleteOrganizationAlertsByAlertId(adminToken, org_id, alert_id, errorCode, test);
		if (errorCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "successfully deleted all alerts for the Organization. ");
		} else {
			String code = errormessage.getCodeString();
			String message = errormessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + errorCode);
		}
	}

	/**
	 * the function is used to update Organization alert
	 * 
	 * @author ramya.Nagepalli
	 * @param job_id
	 * @param jobtype
	 * @param jobSeverity
	 * @param adminToken
	 * @param org_id
	 *            return response
	 * @param job_severity
	 */
	public Response updateOrganizationAlerts(String adminToken, String alert_id, String org_id, String job_id,
			String jobtype, String jobSeverity, HashMap<String, Object> alert_details, ArrayList<String> alert_data,
			int statusCode, ExtentTest test) {
		Map<String, Object> alertInfo = new HashMap<String, Object>();
		alertInfo = jp.composeAlertInfo(job_id, jobtype, jobSeverity, alert_details, alert_data);
		Response response = spogAlertsInvoker.updateOrganizationAlerts(adminToken, org_id, alertInfo, alert_id);
		spogServer.checkResponseStatus(response, statusCode);
		return response;

	}

	/**
	 * the function is used to compose alert information
	 * 
	 * @author Ramya.Nagepalli
	 * @param response
	 * @param alert_id
	 * @param org_id
	 * @param job_id
	 * @param jobtype
	 * @param jobSeverity
	 *            return response
	 * @param job_severity
	 * @param alert_data
	 * @param alert_details
	 * @param exp
	 * @param errorMessage
	 * @param expectedStstusCode
	 */
	public void checkAlertData(Response response, String alert_id, String organization_id, String org_name,
			String job_id, String jobtype, String jobSeverity, HashMap<String, Object> alert_details,
			ArrayList<String> alert_data, ArrayList<String> expected_actions, int expectedStatusCode,
			SpogMessageCode errorCode, ExtentTest test) {

		String description = null;

		String title = null;
		if (jobtype.contains("backup") && alert_details.containsKey("source_name")) {
			if (jobtype == "backup_missed") {
				description = alert_details.get("source_name").toString()
						+ " could have been disconnected or powered off.";
				title = "Source Missed Scheduled Backup";
			} else if (jobSeverity.equals("critical")) {
				description = alert_details.get("source_name").toString() + " backup completed with errors.";
				title = "Backup Errors";
			} else {
				description = alert_details.get("source_name").toString() + " backup failed.";
				title = "Backup Failed";
			}
		} else if (jobtype.contains("recovery") && alert_details.containsKey("source_name")) {
			if (jobSeverity.equals("critical")) {
				description = alert_details.get("source_name").toString() + " recovery completed with errors.";
				title = "Recovery Errors";
			} else {
				description = alert_details.get("source_name").toString() + " recovery failed.";
				title = "Recovery Failed";
			}
		}
		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {

			String actual_alert_id = response.then().extract().path("data.alert_id").toString();
			spogServer.assertResponseItem(alert_id, actual_alert_id);

			String actual_org_id = response.then().extract().path("data.organization_id").toString();
			spogServer.assertResponseItem(organization_id, actual_org_id);

			String actual_job_id = response.then().extract().path("data.job_id");
			spogServer.assertResponseItem(job_id, actual_job_id);

			ArrayList<String> actual_alert_data = response.then().extract().path("data.alert_data");
			HashMap<String, Object> actual_details = response.then().extract().path("data.alert_details");
			if (!alert_details.isEmpty() && !alert_details.equals(null)) {
				spogServer.assertResponseItem(alert_details.get("source_name").toString(),
						actual_details.get("target_name").toString());
				
				spogServer.assertResponseItem(jobtype, actual_details.get("job_type").toString());
				test.log(LogStatus.PASS, "The expected data matched with the actual data");
			}

		}else if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			String actual_alert_id = response.then().extract().path("data.alert_id");
			spogServer.assertResponseItem(alert_id, actual_alert_id);

			String actual_jobtype = response.then().extract().path("data.type");
			spogServer.assertResponseItem(jobtype, actual_jobtype);

			String actual_description = response.then().extract().path("data.description").toString();
			spogServer.assertResponseItem(description, actual_description);

			String actual_title = response.then().extract().path("data.title");
			spogServer.assertResponseItem(title, actual_title);

			String actual_job_id = response.then().extract().path("data.job_id");
			spogServer.assertResponseItem(job_id, actual_job_id);

			String actual_Severity = response.then().extract().path("data.severity");
			spogServer.assertResponseItem(jobSeverity, actual_Severity);

			ArrayList<String> actual_available_actions = response.then().extract().path("data.allowed_actions");
			spogServer.assertResponseItem(expected_actions.toString(), actual_available_actions.toString());

			String actual_org_id = response.then().extract().path("data.organization.organization_id").toString();
			spogServer.assertResponseItem(organization_id, actual_org_id);

			String actual_org_name = response.then().extract().path("data.organization.organization_name").toString();
			// spogServer.assertResponseItem("spog_udp_qa_"+org_name,actual_org_name);

			String actual_type = response.then().extract().path("data.organization.type");
			spogServer.assertResponseItem(null, actual_type);

			Boolean actual_blocked = response.then().extract().path("data.organization.blocked");
			spogServer.assertResponseItem("false", actual_blocked.toString());

			HashMap<String, Object> actual_details = response.then().extract().path("data.details");
			if (!alert_details.isEmpty() && !alert_details.equals(null)) {
				/*
				 * spogServer.assertResponseItem(alert_details.get("source_name").toString(),
				 * actual_details.get("source_name").toString());
				 * spogServer.assertResponseItem(alert_details.get("destination_name").toString(
				 * ), actual_details.get("destination_name").toString());
				 * spogServer.assertResponseItem(alert_details.get("policy_name").toString(),
				 * actual_details.get("policy_name").toString()); test.log(LogStatus.PASS,
				 * "The expected data matched with the actual data");
				 */
			}

		} else {
			String code = errorCode.getCodeString();
			String message = errorCode.getStatus();
			if (code.contains("0030000A")) {
				message = message.replace("{0}", organization_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	public void checkAlertData(Response response, String alert_id, String organization_id, String job_id,
			String jobtype, String jobSeverity, HashMap<String, Object> alert_details, ArrayList<String> alert_data,
			ArrayList<String> expected_actions, int expectedStatusCode, SpogMessageCode errorCode, ExtentTest test) {

		String description = null;

		String title = null;
		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {

			if (jobtype.contains("backup")) {
				if (jobtype == "backupmissed") {
					description = alert_data.get(0).toString() + " could have been disconnected or powered off.";
					title = "Source Missed Scheduled Backup";
				} else if (jobSeverity.equals("critical")) {
					description = alert_data.get(0).toString() + " backup completed with errors.";
					title = "Backup Errors";
				} else {
					description = alert_data.get(0).toString() + " backup failed.";
					title = "Backup Failed";
				}
			} else {
				if (jobSeverity.equals("critical")) {
					description = alert_data.get(0).toString() + " recovery completed with errors.";
					title = "Recovery Errors";
				} else {
					description = alert_data.get(0).toString() + " recovery failed.";
					title = "Recovery Failed";
				}
			}

			String actual_alert_id = response.then().extract().path("data.alert_id").toString();
			spogServer.assertResponseItem(alert_id, actual_alert_id);

			String actual_org_id = response.then().extract().path("data.organization_id").toString();
			spogServer.assertResponseItem(organization_id, actual_org_id);

			String actual_job_id = response.then().extract().path("data.job_id");
			spogServer.assertResponseItem(job_id, actual_job_id);

			String actual_jobtype = response.then().extract().path("data.type");
			spogServer.assertResponseItem(jobtype, actual_jobtype);

			ArrayList<String> actual_alert_data = response.then().extract().path("data.alert_data");
			HashMap<String, Object> actual_details = response.then().extract().path("data.alert_details");
			if (!alert_details.isEmpty() && !alert_details.equals(null)) {
				spogServer.assertResponseItem(alert_details.get("source_name").toString(),
						actual_details.get("source_name").toString());
				spogServer.assertResponseItem(alert_details.get("destination_name").toString(),
						actual_details.get("destination_name").toString());
				spogServer.assertResponseItem(alert_details.get("policy_name").toString(),
						actual_details.get("policy_name").toString());
				test.log(LogStatus.PASS, "The expected data matched with the actual data");
			}

		} else if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

			if (jobtype == "backupmissed") {
				description = alert_details.get("source_name").toString()
						+ " could have been disconnected or powered off.";
				title = "Source Missed Scheduled Backup";
			} else if (jobSeverity.equals("critical")) {
				description = alert_details.get("source_name").toString() + " backup completed with errors.";
				title = "Backup Errors";
			} else {
				description = alert_details.get("source_name").toString() + " backup failed.";
				title = "Backup Failed";
			}

			ArrayList<String> actual_alert_id = response.then().extract().path("data.alert_id");
			spogServer.assertFilterItem(alert_id, actual_alert_id);

			String actual_jobtype = response.then().extract().path("data[" + 0 + "].type");
			spogServer.assertResponseItem(jobtype, actual_jobtype);

			String actual_description = response.then().extract().path("data[" + 0 + "].description").toString();
			spogServer.assertResponseItem(description, actual_description);

			String actual_title = response.then().extract().path("data[" + 0 + "].title");
			spogServer.assertResponseItem(title, actual_title);

			String actual_job_id = response.then().extract().path("data[" + 0 + "].job_id");
			spogServer.assertResponseItem(job_id, actual_job_id);

			String actual_Severity = response.then().extract().path("data[" + 0 + "].severity");
			spogServer.assertResponseItem(jobSeverity, actual_Severity);

			ArrayList<String> actual_available_actions = response.then().extract()
					.path("data[" + 0 + "].allowed_actions");

			// HashMap<String, Object>
			// actual_alertDetail=response.then().extract().path("data.alertDetail");

			HashMap<String, Object> actual_details = response.then().extract().path("data[" + 0 + "].details");

			String actual_org_id = response.then().extract().path("data[" + 0 + "].organization.organization_id")
					.toString();
			spogServer.assertResponseItem(organization_id, actual_org_id);

			String actual_org_name = response.then().extract().path("data[" + 0 + "].organization.organization_name")
					.toString();
			// spogServer.assertResponseItem("spog_udp_qa_"+org_name,actual_org_name);

			String actual_type = response.then().extract().path("data[" + 0 + "].organization.type");
			spogServer.assertResponseItem(null, actual_type);

			Boolean actual_blocked = response.then().extract().path("data[0].organization.blocked");
			spogServer.assertResponseItem("false", actual_blocked.toString());

			actual_details = response.then().extract().path("data[0].details");
			if (!alert_details.isEmpty() && !alert_details.equals(null)) {
				spogServer.assertResponseItem(alert_details.get("source_name").toString(),
						actual_details.get("source_name").toString());
				spogServer.assertResponseItem(alert_details.get("destination_name").toString(),
						actual_details.get("destination_name").toString());
				spogServer.assertResponseItem(alert_details.get("policy_name").toString(),
						actual_details.get("policy_name").toString());
				test.log(LogStatus.PASS, "The expected data matched with the actual data");
			}

		} else {
			String code = errorCode.getCodeString();
			String message = errorCode.getStatus();
			if (code.contains("0030000A")) {
				message = message.replace("{0}", organization_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
	}

	/**
	 * the function is used to generateOrganizationCapacityAlert
	 * 
	 * @author Ramya.Nagepalli
	 * @param Token
	 * @param organization_id
	 * @param expectedStatusCode
	 * @param expectedErrorMessage
	 * @param test
	 *            return response
	 */
	public Response generateOrganizationCapacityAlert(String token, String organization_id, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, ExtentTest test) {

		Response response = spogAlertsInvoker.generateOrganizationCapacityAlert(token, organization_id, test);
		spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {

		} else {
			String code = expectedErrorMessage.getCodeString();
			String message = expectedErrorMessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);
		}
		return response;
	}

	/**
	 * the function is used to getAlertsForLoggedInUser
	 * 
	 * @author ramya.Nagepalli
	 * @param adminToken
	 * @param statusCode
	 *            return response
	 */
	public Response getAlertsForLoggedInUser(String adminToken, String additionalUrl, int statusCode, ExtentTest test) {
		Response response = spogAlertsInvoker.getAlertsForLoggedInUser(adminToken, additionalUrl, test);
		spogServer.checkResponseStatus(response, statusCode);
		return response;

	}

	/**
	 * the function is used to Acknowledge all Organization alerts
	 * 
	 * deleteOrganizationAlerts_Acknowledgeall
	 * 
	 * @author ramya.Nagepalli
	 * @param adminToken
	 * @param statusCode
	 * @param expectedcode
	 *            return response
	 */

	public void deleteOrganizationAlerts_Acknowledgeall(String adminToken, int statusCode, SpogMessageCode errorCode,
			ExtentTest test) {

		Response response = spogAlertsInvoker.acknowledgeAllOrganizationAlerts(adminToken, test);
		spogServer.checkResponseStatus(response, statusCode);
		if (statusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "successfully deleted all alerts for the Organization. ");
		} else {
			String code = errorCode.getCodeString();
			String message = errorCode.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + statusCode);
		}
	}

	/**
	 * the function is used to deleteAlertsByAlertId
	 * 
	 * @author ramya.Nagepalli
	 * @param adminToken
	 * @param alert_id
	 * @param statusCode
	 * @param expectedcode
	 *            return response
	 */

	public void deleteAlertsByAlertId(String adminToken, String alert_id, int statusCode, SpogMessageCode errorCode,
			ExtentTest test) {

		Response response = spogAlertsInvoker.deleteAlertsByAlertId(adminToken, alert_id, test);
		spogServer.checkResponseStatus(response, statusCode);
		if (statusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "successfully deleted all alerts for the Organization. ");
		} else {
			String code = errorCode.getCodeString();
			String message = errorCode.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + statusCode);

		}
	}

	/**
	 * the function is used to validate alert information
	 * 
	 * @author ramya.Nagepalli
	 * @param response
	 * @param org_id
	 * @param org_name
	 * @param org_type
	 * @param job_id
	 * @param jobSeverity
	 * @param alert_details
	 * @param allowed_actions
	 * @param expectedStstusCode
	 * @param errorCode
	 *            return response
	 */
	public void checkGetAlertsData(Response response, String organization_id, String org_name, String org_type,
			String job_id, String Severity, HashMap<String, Object> alert_details, ArrayList<String> allowed_actions,
			int expectedStatusCode, SpogMessageCode errorCode, ExtentTest test) {

		spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap> actual = response.then().extract().path("data");
			String description = null;

			String title = null;
			int size = actual.size();

			for (int i = 0; i < size; i++) {
				String exp_type = response.then().extract().path("data[" + i + "].type").toString();

				if (exp_type.equals("clouddirect_trial_start")) {
					description = "Your Cloud Direct trial has started.";
					title = "Cloud Direct Trial Started";
				} else if (exp_type.equals("cloudhybrid_trial_start")) {
					description = "Your Cloud Hybrid trial has started.";
					title = "Cloud Hybrid Trail Started";
				}
				if (exp_type.contains("recovery")) {
					description = alert_details.get("source_name").toString() + " recovery completed with errors.";
					title = "Recovery Errors";
				}

				String actual_org_id = response.then().extract().path("data[" + i + "].organization.organization_id")
						.toString();
				spogServer.assertResponseItem(organization_id, actual_org_id);

				String actual_org_name = response.then().extract()
						.path("data[" + i + "].organization.organization_name").toString();
				// spogServer.assertResponseItem(org_name,actual_org_name);

				Object actual_org_type = response.then().extract().path("data[" + i + "].organization.type");
				// spogServer.assertResponseItem(org_type,actual_org_type);

				Object actual_blocked = response.then().extract().path("data[" + i + "].organization.blocked");

				String actual_alerttype = response.then().extract().path("data[" + i + "].type").toString();
				spogServer.assertResponseItem(actual_alerttype, actual_alerttype);

				String actual_Severity = response.then().extract().path("data[" + i + "].severity").toString();
				spogServer.assertResponseItem(Severity, actual_Severity);

				String actual_title = response.then().extract().path("data[" + i + "].title").toString().toLowerCase();
				spogServer.assertResponseItem(title.toLowerCase(), actual_title);

				String actual_description = response.then().extract().path("data[" + i + "].description").toString()
						.toLowerCase();
				spogServer.assertResponseItem(description.toLowerCase(), actual_description);

			}
			test.log(LogStatus.PASS, "The expected data is null in the response");
		}

		else {
			String code = errorCode.getCodeString();
			String message = errorCode.getStatus();
			if (code.contains("0030000A")) {
				message = message.replace("{0}", organization_id);
			}
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedStatusCode);

		}
	}
}
