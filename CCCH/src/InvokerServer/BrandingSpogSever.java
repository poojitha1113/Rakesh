package InvokerServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import dataPreparation.JsonPreparation;
import genericutil.ErrorHandler;
import invoker.BrandingSpogInvoker;
import invoker.Log4GatewayInvoker;
import invoker.Log4SPOGInvoker;
import invoker.SPOGInvoker;
import invoker.UserSpogInvoker;
import io.restassured.response.Response;

public class BrandingSpogSever {

	private static JsonPreparation jp = new JsonPreparation();
	private UserSpogInvoker userSpogInvoker;
	private BrandingSpogSever BrandingSpogSever;
	private BrandingSpogInvoker BrandingSpogInvoker;
	private SPOGServer spogServer;
	public static ErrorHandler errorHandle = ErrorHandler.getErrorHandler();
	private ExtentTest test;

	private HashMap<String, Object> Uuid = new HashMap<>();

	public BrandingSpogSever(String baseURI, String port) {
		BrandingSpogInvoker = new BrandingSpogInvoker(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
	}

	public void setToken(String token) {
		BrandingSpogInvoker.setToken(token);
	}

	/**
	 * Create branding emailer for logged in user and return the response
	 * 
	 * @author malleswari.sykam
	 * @param organization_id
	 * @param emailer_name
	 * @param email
	 * @param signature
	 * @param support_call
	 * @param support_chat
	 * @param support_sales
	 * @param facebook_link
	 * @param twitter_link
	 * @param linkdin_link
	 * @param social_media_platform
	 * @param legal_notice
	 * @param contact_us
	 * @param privacy
	 * @param copyrights
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response createBrandingemailerForOrganization(String organization_id, String emailer_name, String email,
			String signature, String support_call, String support_chat, String support_sales,String support_email, String facebook_link,
			String twitter_link, String linkdin_link, String social_media_platform, String legal_notice,
			String contact_us, String privacy, String copyrights, String branding_from, ExtentTest test) {

		test.log(LogStatus.INFO, "Compose Braningemailerinfo");
		HashMap<String, Object> BrandingInfo = jp.ComposeBrandingemailerinfo(organization_id, emailer_name, email,
				signature, support_call, support_chat, support_sales,support_email, facebook_link, twitter_link, linkdin_link,
				social_media_platform, legal_notice, contact_us, privacy, copyrights, branding_from);

		test.log(LogStatus.INFO, "Call the Rest API to create user filters");
		Response response = BrandingSpogInvoker.createBrandingemailerfororganization(organization_id, BrandingInfo);

		return response;
	}

	/**
	 * Create branding emailer for logged in user and return the response
	 * 
	 * @author malleswari.sykam
	 * @param organization_id
	 * @param test
	 * @return response
	 */
	public Response getbrandingemailerFororganization(String organization_id, ExtentTest test) {

		test.log(LogStatus.INFO, "Call the Rest API to create user filters");
		Response response = BrandingSpogInvoker.getbrandingemailerfororganizaton(organization_id);

		return response;
	}

	/**
	 * update branding emailer for logged in user and return the response
	 * 
	 * @author malleswari.sykam
	 * @param organization_id
	 * @param emailer_name
	 * @param email
	 * @param signature
	 * @param support_call
	 * @param support_chat
	 * @param support_sales
	 * @param facebook_link
	 * @param twitter_link
	 * @param linkdin_link
	 * @param social_media_platform
	 * @param legal_notice
	 * @param contact_us
	 * @param privacy
	 * @param copyrights
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response updateBrandingemailerFororganization(String organization_id, String emailer_name, String email,
			String signature, String support_call, String support_chat, String support_sales,String support_email, String facebook_link,
			String twitter_link, String linkdin_link, String legal_notice,
			String contact_us, String privacy, String copyrights, String branding_from, ExtentTest test) {

		test.log(LogStatus.INFO, "Compose Braningemailerinfo");
		HashMap<String, Object> BrandingInfo = jp.ComposeBrandingemailerinfo(organization_id, emailer_name, email, signature, support_call,
				support_chat, support_sales, support_email, facebook_link, twitter_link, linkdin_link, legal_notice,
				contact_us, privacy, copyrights, branding_from);

		test.log(LogStatus.INFO, "Call the Rest API to create user filters");
		Response response = BrandingSpogInvoker.updateBrandingemailerfororganization(organization_id, BrandingInfo);

		return response;
	}

	/**
	 * verify branding emailer for logged in user and return the response
	 * 
	 * @author malleswari.sykam
	 * @param organization_id
	 * @param token
	 * @return response
	 */
	public void verifyBrandingEmailerForOrganization(Response response, String organization_id, String emailer_name,
			String email, String signature, String support_call, String support_chat, String support_sales,String support_email,
			String facebook_link, String twitter_link, String linkdin_link, String social_media_platform,
			String legal_notice, String contact_us, String privacy, String copyrights, String branding_from,
			String disabled, int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		{

			spogServer.checkResponseStatus(response, expectedstatuscode, test);

			if (expectedstatuscode == SpogConstants.SUCCESS_POST
					|| expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
				HashMap<String, Object> actual_response = response.then().extract().path("data");

				test.log(LogStatus.INFO, "Compare the organization_id");
			spogServer.assertResponseItem(organization_id, actual_response.get("organization_id"), test);

				test.log(LogStatus.INFO, "Compare the emailer_name");
				if (actual_response.containsKey("emailer_name") && emailer_name != null && emailer_name != "") {
					if (emailer_name.equals(null)) {
						emailer_name = "Arcserve Support";
					}
					spogServer.assertResponseItem(emailer_name, actual_response.get("emailer_name").toString(), test);

					test.log(LogStatus.INFO, "compare whether the branding disabled or enabled");
					spogServer.assertResponseItem(disabled, actual_response.get("disabled").toString());

				} else {
					test.log(LogStatus.PASS, "user has not filtered on param emailer_name");
				}

				test.log(LogStatus.INFO, "Compare the branding_from");
				if (actual_response.containsKey("branding_from") && branding_from != null && branding_from != "") {
					spogServer.assertResponseItem(branding_from, actual_response.get("branding_from").toString(), test);

				} else {
					test.log(LogStatus.PASS, "user has not filtered on param branding_from");
				}

				test.log(LogStatus.INFO, "Compare the email");
				if (actual_response.containsKey("email") && email != null && email != "") {
					spogServer.assertResponseItem(email, actual_response.get("email").toString(), test);

				} else {
					test.log(LogStatus.PASS, "user has not filtered on param email");
				}

				test.log(LogStatus.INFO, "Compare the signature");
				if (actual_response.containsKey("signature") && signature != null && signature != "") {
					spogServer.assertResponseItem(signature, actual_response.get("signature").toString(), test);

				} else {
					test.log(LogStatus.PASS, "user has not filtered on param signature");
				}
				test.log(LogStatus.INFO, "Compare the support_call");
				if (actual_response.containsKey("support_call") && support_call != null && support_call != "") {
					spogServer.assertResponseItem(support_call, actual_response.get("support_call").toString(), test);
					if (support_call.equals(null)) {
						support_call = "650-590-0967";
					}
				} else {
					test.log(LogStatus.PASS, "user has not filtered on param support_call");
				}
				test.log(LogStatus.INFO, "Compare the support_chat");
				if (actual_response.containsKey("support_chat") && support_chat != null && support_chat != "") {

					spogServer.assertResponseItem(support_chat, actual_response.get("support_chat").toString(), test);

				} else {
					test.log(LogStatus.PASS, "user has not filtered on param support_chat");
				}
				test.log(LogStatus.INFO, "Compare the support_sales");
				if (actual_response.containsKey("support_sales") && support_sales != null && support_sales != "") {
					spogServer.assertResponseItem(support_sales, actual_response.get("support_sales").toString(), test);
					if (support_sales.equals(null)) {
						support_sales = "https://www.arcserve.com/saleschat";
					}
				} else {
					test.log(LogStatus.PASS, "user has not filtered on param support_sales");
				}
				
				test.log(LogStatus.INFO, "Compare the support_email");
				if (actual_response.containsKey("support_email") && support_email != null && support_email != "") {
					spogServer.assertResponseItem(support_email, actual_response.get("support_email").toString(), test);
					if (support_email.equals(null)) {
						support_email = "cloudsupport@arcserve.com";
					}
				} else {
					test.log(LogStatus.PASS, "user has not filtered on param support_email");
				}
				
				test.log(LogStatus.INFO, "Compare the facebook_link");
				if (actual_response.containsKey("facebook_link") && facebook_link != null && facebook_link != "") {

					spogServer.assertResponseItem(facebook_link, actual_response.get("facebook_link").toString(), test);

				} else {
					test.log(LogStatus.PASS, "user has not filtered on param facebook_link");
				}
				test.log(LogStatus.INFO, "Compare the twitter_link");
				if (actual_response.containsKey("twitter_link") && twitter_link != null && twitter_link != "") {
					spogServer.assertResponseItem(twitter_link, actual_response.get("twitter_link").toString(), test);
					if (twitter_link.equals(null)) {
						twitter_link = "https://twitter.com/Arcserve";
					}
				} else {
					test.log(LogStatus.PASS, "user has not filtered on param twitter_link");
				}
				test.log(LogStatus.INFO, "Compare the linkdin_link");
				if (actual_response.containsKey("linkdin_link") && linkdin_link != null && linkdin_link != "") {

					spogServer.assertResponseItem(linkdin_link, actual_response.get("linkdin_link").toString(), test);
					if (linkdin_link.equals(null)) {
						linkdin_link = "https://www.linkedin.com/company/arcserve";
					}

				} else {
					test.log(LogStatus.PASS, "user has not filtered on param linkdin_link");
				}
				test.log(LogStatus.INFO, "Compare the social_media_platform");
				if (actual_response.containsKey("social_media_platform") && social_media_platform != null
						&& social_media_platform != "") {
					spogServer.assertResponseItem(social_media_platform,
							actual_response.get("social_media_platform").toString(), test);

				} else {
					test.log(LogStatus.PASS, "user has not filtered on param linkdin_link");
				}
				test.log(LogStatus.INFO, "Compare the legal_notice");
				if (actual_response.containsKey("legal_notice") && legal_notice != null && legal_notice != "") {
					spogServer.assertResponseItem(legal_notice, actual_response.get("legal_notice").toString(), test);

				} else {
					test.log(LogStatus.PASS, "user has not filtered on param linkdin_link");
				}
				test.log(LogStatus.INFO, "Compare the contact_us");
				if (actual_response.containsKey("contact_us") && contact_us != null && contact_us != "") {
					String actual_status = (String) actual_response.get("contact_us");
					spogServer.assertResponseItem(contact_us, actual_response.get("contact_us").toString(), test);

				} else {
					test.log(LogStatus.PASS, "user has not filtered on param contact_us");
				}
				test.log(LogStatus.INFO, "Compare the privacy");
				if (actual_response.containsKey("privacy") && privacy != null && privacy != "") {
					spogServer.assertResponseItem(privacy, actual_response.get("privacy").toString(), test);

				} else {
					test.log(LogStatus.PASS, "user has not filtered on param privacy");
				}
				test.log(LogStatus.INFO, "Compare the copyrights");
				if (actual_response.containsKey("copyrights") && copyrights != null && copyrights != "") {
					spogServer.assertResponseItem(copyrights, actual_response.get("copyrights").toString(), test);

				} else {
					test.log(LogStatus.PASS, "user has not filtered on param copyrights");
				}
			} else {
				String code = ExpectedErrorMessage.getCodeString();
				String message = ExpectedErrorMessage.getStatus();
				if (code.contains("0030000A")) {
					message = message.replace("{0}", organization_id);

				}
				spogServer.checkErrorCode(response, code);

				test.log(LogStatus.PASS, "The error code matched with the expected " + code);

				spogServer.checkErrorMessage(response, message);

				test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);

			}
		}
	}


	/**
	 * Create branding for logged in user and return the response
	 * 
	 * @author malleswari.sykam
	 * @param organization_id
	 * @param organization_name
	 * @param portal_url
	 * @param logo_img_url
	 * @param primary_color
	 * @param secondary_color
	 * @param is_branding_logo
	 * @param login_img_url
	 * @param branding_msg
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response createBrandingFororganization(String organization_id, String organization_name, String portal_url,
			String primary_color, String secondary_color, String branding_msg, ExtentTest test) {

		test.log(LogStatus.INFO, "Compose Braningemailerinfo");
		HashMap<String, Object> BrandingInfo = jp.ComposeBrandinginfo(organization_id, organization_name, portal_url,
				primary_color, secondary_color, branding_msg);

		test.log(LogStatus.INFO, "Call the Rest API to create user filters");
		Response response = BrandingSpogInvoker.createbrandingfororganiztaion(organization_id, BrandingInfo);

		return response;
	}

	
	/**
	 * Turn  on Branding 
	 * @author malleswari.sykam
	 * @param organization_id
	 * @param token
	 * @param test
	 * @return response
	 */
	
	public void  TurnonBrandingFororganizationwithchcek(String organization_id, String token, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		Response response = BrandingSpogInvoker.TurnonBrandingfororganiztaion(organization_id,token);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "Turnon Bradning Successfully");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			/*
			 * if(code.contains("00A00302")){ message = message.replace("{0}", filterID);
			 * message= message.replace("{1}", userID); }
			 */
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}

	}
	
	
	/**
	 * Turn  on Branding 
	 * @author malleswari.sykam
	 * @param organization_id
	 * @param token
	 * @param test
	 * @return response
	 */
	
	public void  TurnonBrandingEmailerFororganizationwithchcek(String organization_id, String token, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		Response response = BrandingSpogInvoker.TurnonBrandingEmailerfororganiztaion(organization_id,token);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "Turnon Bradning Emaielr Successfully");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			/*
			 * if(code.contains("00A00302")){ message = message.replace("{0}", filterID);
			 * message= message.replace("{1}", userID); }
			 */
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}

	}
	
	/**
	 * Turn  on Branding 
	 * @author malleswari.sykam
	 * @param organization_id
	 * @param token
	 * @param test
	 * @return response
	 */
	
	public void  TurnoffBrandingEmailerFororganizationwithchcek(String organization_id, String token, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		Response response = BrandingSpogInvoker.TurnoffBrandingEmailerfororganiztaion(organization_id,token);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "Turnoff Bradning Emaielr Successfully");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			/*
			 * if(code.contains("00A00302")){ message = message.replace("{0}", filterID);
			 * message= message.replace("{1}", userID); }
			 */
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}

	}
	
	/**
	 * Turn  on Branding 
	 * @author malleswari.sykam
	 * @param organization_id
	 * @param token
	 * @param test
	 * @return response
	 */
	
	public void  TurnoffBrandingFororganizationwithchcek(String organization_id, String token, int expectedstatuscode,
			SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		Response response = BrandingSpogInvoker.TurnoffBrandingfororganiztaion(organization_id,token);
		if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			test.log(LogStatus.PASS, "The log columns are successfully deleted");
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			/*
			 * if(code.contains("00A00302")){ message = message.replace("{0}", filterID);
			 * message= message.replace("{1}", userID); }
			 */
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + code);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);
		}

	}
	
	
	/**
	 * get branding for logged in user and return the response
	 * 
	 * @author malleswari.sykam
	 * @param organization_id
	 * @param test
	 * @return response
	 */
	public Response getbrandingfoorganization(String organization_id,

			ExtentTest test) {

		test.log(LogStatus.INFO, "Call the Rest API to create user filters");
		Response response = BrandingSpogInvoker.getbrandingfororganization(organization_id);

		return response;
	}

	/**
	 * getOrganizationBranding
	 * 
	 * @author malleswari.sykam
	 * @param organization_id
	 * @param additionalURL
	 * @param test
	 * @return response
	 */
	public Response getOrganizationBranding(String organization_id, String additionalURL, ExtentTest test) {

		test.log(LogStatus.INFO, "Call the Rest API to create user filters");
		Response response = BrandingSpogInvoker.getOrganizationBranding(organization_id, additionalURL, test);

		return response;
	}

	/**
	 * verifybrandingfororganization
	 * 
	 * @author malleswari.sykam
	 * 
	 * @param response
	 * @param organization_id
	 * @param organization_name
	 * @param portal_url
	 * @param primary_color
	 * @param secondary_color
	 * @param branding_msg
	 * @param disabled
	 * @param expectedstatuscode
	 * @param ExpectedErrorMessage
	 * @param test
	 * @return
	 */
	public void verifybrandingfororganization(Response response, String organization_id, String organization_name,
			String portal_url, String primary_color, String secondary_color, String branding_msg, String disabled,
			int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		{

			spogServer.checkResponseStatus(response, expectedstatuscode, test);

			if (expectedstatuscode == SpogConstants.SUCCESS_POST
					|| expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
				HashMap<String, Object> actual_response = response.then().extract().path("data");

				test.log(LogStatus.INFO, "compare whether the branding disabled or enabled");
				spogServer.assertResponseItem(disabled, actual_response.get("disabled").toString());

				test.log(LogStatus.INFO, "Compare the organization_name");
				if (actual_response.containsKey("organization_name") && organization_name != null
						&& organization_name != "") {
					spogServer.assertResponseItem(organization_name,
							actual_response.get("organization_name").toString(), test);

				} else {
					test.log(LogStatus.PASS, "user has not filtered on param organization_name");
				}

				test.log(LogStatus.INFO, "Compare the portal_url");
				if (actual_response.containsKey("portal_url") && portal_url != null && portal_url != "") {
					spogServer.assertResponseItem(portal_url, actual_response.get("portal_url").toString(), test);

				} else {
					test.log(LogStatus.PASS, "user has branding  on param portal_url");

				}

				/*
				 * test.log(LogStatus.INFO, "Compare the logo_img_url");
				 * if(actual_response.containsKey("logo_img_url") && logo_img_url != null &&
				 * logo_img_url != "") { spogServer.assertResponseItem(logo_img_url,
				 * actual_response.get("null").toString(), test);
				 * 
				 * }else { test.log(LogStatus.PASS,
				 * "user has not filtered on param logo_img_url"); }
				 */
				test.log(LogStatus.INFO, "Compare the primary_color");
				if (actual_response.containsKey("primary_color") && primary_color != null && primary_color != "") {
					spogServer.assertResponseItem(primary_color, actual_response.get("primary_color").toString(), test);

				} else {
					test.log(LogStatus.PASS, "user has not filtered on param primary_color");
				}
				test.log(LogStatus.INFO, "Compare the secondary_color");
				if (actual_response.containsKey("secondary_color") && secondary_color != null
						&& secondary_color != "") {

					spogServer.assertResponseItem(secondary_color, actual_response.get("secondary_color").toString(),
							test);

				} else {
					test.log(LogStatus.PASS, "user has not filtered on param secondary_color");
				}
			} else {
				String code = ExpectedErrorMessage.getCodeString();
				String message = ExpectedErrorMessage.getStatus();
				if (code.contains("0030000A")) {
					message = message.replace("{0}", organization_id);

				}
				// spogServer.checkErrorCode(response,code);

				test.log(LogStatus.PASS, "The error code matched with the expected " + code);

				spogServer.checkErrorMessage(response, message);

				test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);

			}
		}
	}

	/**
	 * Create branding for logged in user and return the response
	 * 
	 * @author malleswari.sykam
	 * @param organization_id
	 * @param organization_name
	 * @param portal_url
	 * @param logo_img_url
	 * @param primary_color
	 * @param secondary_color
	 * @param is_branding_logo
	 * @param login_img_url
	 * @param branding_msg
	 * @param token
	 * @param test
	 * @return response
	 */
	public Response updateBrandingFororganization(String organization_id, String organization_name, String portal_url,
			String primary_color, String secondary_color, String branding_msg, ExtentTest test) {

		test.log(LogStatus.INFO, "Compose Braningemailerinfo");
		HashMap<String, Object> BrandingInfo = jp.ComposeBrandinginfo(organization_id, organization_name, portal_url,
				primary_color, secondary_color, branding_msg);

		test.log(LogStatus.INFO, "Call the Rest API to create user filters");
		Response response = BrandingSpogInvoker.updateBrandingfororganization(organization_id, BrandingInfo);

		return response;
	}

	/**
	 * upload logo for logged in user
	 * 
	 * @author malleswari.sykam
	 * @param Organization_id
	 * @param logoPath
	 * @param test
	 * @return
	 */

	public Response uploadlogoFororganization(String Organization_id, String logoPath, ExtentTest test) {

		Response response = BrandingSpogInvoker.uploadLogofororganiztaion(Organization_id, logoPath);

		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);

		String imageUrl = response.then().extract().path("data.image_url");

		// download the image from url in response and compare to original
		String tempImageURL = logoPath + "download";
		try {
			URL url = new URL(imageUrl);
			InputStream inputStream = url.openStream();
			FileOutputStream fileOutputStream = new FileOutputStream(new File(tempImageURL));
			int length = -1;
			byte[] buffer = new byte[1024];
			while ((length = inputStream.read(buffer)) > -1) {
				fileOutputStream.write(buffer, 0, length);
			}
			fileOutputStream.close();
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File file1 = new File(logoPath);
		File file2 = new File(tempImageURL);

		try {
			boolean result = FileUtils.contentEquals(file1, file2);
			assertEquals(result, true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		file2.delete();
		return response;
	}

	/**
	 * Call the REST api to gettheLoggedInUser
	 * 
	 * @author Bharadwaj.Ghadaim
	 * @param Token
	 * @param statuscode
	 * @param test
	 * @return response
	 */
	public Response getLoggedInUser(String Token, int expectedcode, String ExpectedErrorMessage, ExtentTest test) {

		Response response = BrandingSpogInvoker.getLoggedinUserInfo(Token, test);

		System.out.println("The value of the geneated user is :" + response.getBody().asString());
		return response;
	}

	/**
	 * Get the JWT token of the logged in user
	 * 
	 * @author malleswari
	 * @return JWTToken
	 */
	public String getJWTToken() {

		String JWTToken = null;
		JWTToken = BrandingSpogInvoker.getToken();
		return JWTToken;
	}

	/**
	 * upload logo for logged in user with error check
	 * 
	 * @param logoPath
	 * @param organization_id
	 * @param statusCode
	 * @param errorCode
	 * @param test
	 */
	public void uploadlogoFororganiztaionwithcheck(String organization_id, String logoPath, int errorCode,
			String errormessage, ExtentTest test) {
		Response response = BrandingSpogInvoker.uploadLogofororganiztaion(organization_id, logoPath);
		spogServer.checkResponseStatus(response, errorCode);
		spogServer.checkErrorCode(response, errormessage);
	}

	/**
	 * upload logo for logged in user
	 * 
	 * @author malleswari.sykam
	 * @param Organization_id
	 * @param logoPath
	 * @param test
	 * @return
	 */

	public Response uploadloginimageFororganization(String Organization_id, String loginimagePath, ExtentTest test) {

		Response response = BrandingSpogInvoker.uploadLoginimageforLoggedinuser(Organization_id, loginimagePath);

		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);

		String imageUrl = response.then().extract().path("data.image_url");

		// download the image from url in response and compare to original
		String tempImageURL = loginimagePath + "download";
		try {
			URL url = new URL(imageUrl);
			InputStream inputStream = url.openStream();
			FileOutputStream fileOutputStream = new FileOutputStream(new File(tempImageURL));
			int length = -1;
			byte[] buffer = new byte[1024];
			while ((length = inputStream.read(buffer)) > -1) {
				fileOutputStream.write(buffer, 0, length);
			}
			fileOutputStream.close();
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File file1 = new File(loginimagePath);
		File file2 = new File(tempImageURL);

		try {
			boolean result = FileUtils.contentEquals(file1, file2);
			assertEquals(result, true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		file2.delete();
		return response;
	}

	/**
	 * upload loginimage for logged in user with error check
	 * 
	 * @param logoPath
	 * @param organization_id
	 * @param statusCode
	 * @param errorCode
	 * @param test
	 */
	public void uploadloginimageFororganizationWithcheck(String organization_id, String loginimagePath, int errorCode,
			String errormessage, ExtentTest test) {
		Response response = BrandingSpogInvoker.uploadLogofororganiztaion(organization_id, loginimagePath);
		spogServer.checkResponseStatus(response, errorCode);
		spogServer.checkErrorCode(response, errormessage);
	}

	public void verifybrandingwithcheck(String organizationType, String orgname, String organization_id,
			int expectedstatuscode, String ExpectedErrorMessage, ExtentTest test) {
		{

			HashMap<String, Object> actual_response = new HashMap<String, Object>();

			Response response = BrandingSpogInvoker.getbrandingfororganization(organization_id);
			actual_response = response.then().extract().path("data");

			if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
				spogServer.checkResponseStatus(response, expectedstatuscode, test);

				spogServer.assertResponseItem(actual_response.get("organization_name"), orgname, test);
				spogServer.assertResponseItem(actual_response.get("brandingLogo"), true, test);
				spogServer.assertResponseItem(actual_response.get("portal_url"), "https://arcserve.com", test);
				spogServer.assertResponseItem(actual_response.get("logo_img_url"),
						"http://tspog.arcserve.com/images/arcserve_logo.png", test);
				spogServer.assertResponseItem(actual_response.get("primary_color"), "#4cbfa5", test);
				spogServer.assertResponseItem(actual_response.get("secondary_color"), "#196ba2", test);
				spogServer.assertResponseItem(actual_response.get("is_branding_logo"), true, test);
				spogServer.assertResponseItem(actual_response.get("login_img_url"),
						"http://tspog.arcserve.com/images/arcserve_logo.png", test);
				spogServer.assertResponseItem(actual_response.get("branding_msg"),
						"Guaranteed data protection through seamless integrations of backup-restore", test);

			} else {

				spogServer.checkErrorCode(response, ExpectedErrorMessage);

				test.log(LogStatus.PASS, "The error code matched with the expected " + ExpectedErrorMessage);

				spogServer.checkErrorMessage(response, ExpectedErrorMessage);

				test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);

			}
		}
	}

	public void verifybrandingemailerfororganizationwithcheck(String organizationType, String orgname,
			String organization_id, int expectedstatuscode, String ExpectedErrorMessage, ExtentTest test) {
		{

			HashMap<String, Object> actual_response = new HashMap<String, Object>();

			Response response = BrandingSpogInvoker.getbrandingemailerfororganizaton(organization_id);
			actual_response = response.then().extract().path("data");

			if (expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
				spogServer.checkResponseStatus(response, expectedstatuscode, test);

				spogServer.assertResponseItem(actual_response.get("organization_id"), organization_id, test);
				spogServer.assertResponseItem(actual_response.get("emailer_name"), "Arcserve Support", test);
				spogServer.assertResponseItem(actual_response.get("email"), "support@zetta.net", test);
				spogServer.assertResponseItem(actual_response.get("signature"),
						"Arcserve Support <br> support@zetta.com <br> 650-590-0967", test);
				spogServer.assertResponseItem(actual_response.get("support_call"), "650-590-0967", test);
				spogServer.assertResponseItem(actual_response.get("support_chat"), "https://arcserve.com/support-chat",
						test);
				spogServer.assertResponseItem(actual_response.get("support_sales"), "https://arcserve.com/sales", test);
				spogServer.assertResponseItem(actual_response.get("facebook_link"),
						"https://www.facebook.com/ArcserveLLC", test);
				spogServer.assertResponseItem(actual_response.get("twitter_link"), "https://twitter.com/Arcserve",
						test);
				spogServer.assertResponseItem(actual_response.get("linkdin_link"),
						"https://www.linkedin.com/company/arcserve", test);
				spogServer.assertResponseItem(actual_response.get("social_media_platform"),
						"http://www.arcserve.com/no/social-community.aspx", test);
				spogServer.assertResponseItem(actual_response.get("legal_notice"),
						"https://documentation.arcserve.com/Arcserve-UDP/Available/V6.5/ENU/Bookshelf_Files/legal.html",
						test);
				spogServer.assertResponseItem(actual_response.get("contact_us"),
						"https://www.arcserve.com/no/contact-arcserve.aspx", test);
				spogServer.assertResponseItem(actual_response.get("privacy"),
						"http://www.arcserve.com/kr/privacy-notice.aspx", test);
				spogServer.assertResponseItem(actual_response.get("copyrights"),
						"Copyrights @ 2017 Arcserve (USA), LLC and its affiliates and subsidiaries. All rights reserved. All trademarks, trade names,services marks and logosrefrenced herein belong to their respective owners.",
						test);
				spogServer.assertResponseItem(actual_response.get("branding_from"), "branding_from", test);

			} else {

				spogServer.checkErrorCode(response, ExpectedErrorMessage);

				test.log(LogStatus.PASS, "The error code matched with the expected " + ExpectedErrorMessage);

				spogServer.checkErrorMessage(response, ExpectedErrorMessage);

				test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);

			}
		}
	}

	/**
	 * DisableBrandingEmailerForOrganization
	 * 
	 * @author Ramya.Nagepalli
	 * @param Token
	 * @param organization_id
	 * @param ExpectedStatusCode
	 * @param errormessage
	 * @param test
	 */
	public Response DisableBrandingEmailerForOrganization(String Token, String organization_id, int ExpectedStatusCode,
			SpogMessageCode errormessage, ExtentTest test) {
		Response response = BrandingSpogInvoker.DisableBrandingEmailerForOrganization(Token, organization_id, test);
		spogServer.checkResponseStatus(response, ExpectedStatusCode);

		if (ExpectedStatusCode == SpogConstants.SUCCESS_POST) {

		} else {

			String code = errormessage.getCodeString();
			String message = errormessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + errormessage);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + ExpectedStatusCode);

		}
		return response;
	}

	/**
	 * EnableBrandingEmailerForOrganization
	 * 
	 * @author Ramya.Nagepalli
	 * @param Token
	 * @param organization_id
	 * @param ExpectedStatusCode
	 * @param errormessage
	 * @param test
	 */
	public Response EnableBrandingEmailerForOrganization(String Token, String organization_id, int ExpectedStatusCode,
			SpogMessageCode errormessage, ExtentTest test) {
		Response response = BrandingSpogInvoker.EnableBrandingEmailerForOrganization(Token, organization_id, test);
		spogServer.checkResponseStatus(response, ExpectedStatusCode);

		if (ExpectedStatusCode == SpogConstants.SUCCESS_POST) {

		} else {

			String code = errormessage.getCodeString();
			String message = errormessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + errormessage);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + ExpectedStatusCode);

		}
		return response;
	}

	/**
	 * DisableBrandingForOrganization
	 * 
	 * @author Ramya.Nagepalli
	 * @param Token
	 * @param organization_id
	 * @param ExpectedStatusCode
	 * @param errormessage
	 * @param test
	 */
	public Response DisableBrandingForOrganization(String Token, String organization_id, int ExpectedStatusCode,
			SpogMessageCode errormessage, ExtentTest test) {
		Response response = BrandingSpogInvoker.DisableBrandingForOrganization(Token, organization_id, test);
		spogServer.checkResponseStatus(response, ExpectedStatusCode);

		if (ExpectedStatusCode == SpogConstants.SUCCESS_POST) {

		} else {

			String code = errormessage.getCodeString();
			String message = errormessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + errormessage);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + ExpectedStatusCode);

		}
		return response;
	}

	/**
	 * EnableBrandingForOrganization
	 * 
	 * @author Ramya.Nagepalli
	 * @param Token
	 * @param organization_id
	 * @param ExpectedStatusCode
	 * @param errormessage
	 * @param test
	 */
	public Response EnableBrandingForOrganization(String Token, String organization_id, int ExpectedStatusCode,
			SpogMessageCode errormessage, ExtentTest test) {
		Response response = BrandingSpogInvoker.EnableBrandingForOrganization(Token, organization_id, test);
		spogServer.checkResponseStatus(response, ExpectedStatusCode);

		if (ExpectedStatusCode == SpogConstants.SUCCESS_POST) {

		} else {

			String code = errormessage.getCodeString();
			String message = errormessage.getStatus();
			spogServer.checkErrorCode(response, code);
			test.log(LogStatus.PASS, "The error code matched with the expected " + errormessage);
			spogServer.checkErrorMessage(response, message);
			test.log(LogStatus.PASS, "The expected error message matched " + ExpectedStatusCode);

		}
		return response;
	}
	
	/**
	 * createBrandingemailerForOrganization - removed  param "social media platform" - Sprint 34
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
	 * @param test
	 * @return
	 */
	public Response createBrandingemailerForOrganization(String organization_id, String emailer_name, String email,
			String signature, String support_call, String support_chat, String support_sales,String support_email, String facebook_link,
			String twitter_link, String linkdin_link, String legal_notice,
			String contact_us, String privacy, String copyrights, String branding_from, ExtentTest test) {

		test.log(LogStatus.INFO, "Compose Braningemailerinfo");
		HashMap<String, Object> BrandingInfo = jp.ComposeBrandingemailerinfo(organization_id, emailer_name, email,
				signature, support_call, support_chat, support_sales,support_email, facebook_link, twitter_link, linkdin_link,
				 legal_notice, contact_us, privacy, copyrights, branding_from);

		test.log(LogStatus.INFO, "Call the Rest API to create user filters");
		Response response = BrandingSpogInvoker.createBrandingemailerfororganization(organization_id, BrandingInfo);

		return response;
	}
	
	/**
	 * verifyBrandingEmailerForOrganization - removed  param "social media platform" - Sprint 34
	 * 
	 * @author Ramya.Nagepalli
	 * @param response
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
	 * @param disabled
	 * @param expectedstatuscode
	 * @param ExpectedErrorMessage
	 * @param test
	 */
	public void verifyBrandingEmailerForOrganization(Response response, String organization_id, String emailer_name,
			String email, String signature, String support_call, String support_chat, String support_sales,
			String support_email, String facebook_link, String twitter_link, String linkdin_link, String legal_notice,
			String contact_us, String privacy, String copyrights, String branding_from, String disabled,
			int expectedstatuscode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {

		spogServer.checkResponseStatus(response, expectedstatuscode, test);

		if (expectedstatuscode == SpogConstants.SUCCESS_POST
				|| expectedstatuscode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			HashMap<String, Object> actual_response = response.then().extract().path("data");

			test.log(LogStatus.INFO, "Compare the organization_id");
			spogServer.assertResponseItem(organization_id, actual_response.get("organization_id"), test);

			test.log(LogStatus.INFO, "Compare the emailer_name");
			if (actual_response.containsKey("emailer_name") && emailer_name != null && emailer_name != "") {
				if (emailer_name.equals(null)) {
					emailer_name = "Arcserve Support";
				}
				spogServer.assertResponseItem(emailer_name, actual_response.get("emailer_name").toString(), test);

				test.log(LogStatus.INFO, "compare whether the branding disabled or enabled");
				spogServer.assertResponseItem(disabled, actual_response.get("disabled").toString());

			} else {
				test.log(LogStatus.PASS, "user has not filtered on param emailer_name");
			}

			test.log(LogStatus.INFO, "Compare the branding_from");
			if (actual_response.containsKey("branding_from") && branding_from != null && branding_from != "") {
				spogServer.assertResponseItem(branding_from, actual_response.get("branding_from").toString(), test);

			} else {
				test.log(LogStatus.PASS, "user has not filtered on param branding_from");
			}

			test.log(LogStatus.INFO, "Compare the email");
			if (actual_response.containsKey("email") && email != null && email != "") {
				spogServer.assertResponseItem(email, actual_response.get("email").toString(), test);

			} else {
				test.log(LogStatus.PASS, "user has not filtered on param email");
			}

			test.log(LogStatus.INFO, "Compare the signature");
			if (actual_response.containsKey("signature") && signature != null && signature != "") {
				spogServer.assertResponseItem(signature, actual_response.get("signature").toString(), test);

			} else {
				test.log(LogStatus.PASS, "user has not filtered on param signature");
			}
			test.log(LogStatus.INFO, "Compare the support_call");
			if (actual_response.containsKey("support_call") && support_call != null && support_call != "") {
				spogServer.assertResponseItem(support_call, actual_response.get("support_call").toString(), test);
				if (support_call.equals(null)) {
					support_call = "650-590-0967";
				}
			} else {
				test.log(LogStatus.PASS, "user has not filtered on param support_call");
			}
			test.log(LogStatus.INFO, "Compare the support_chat");
			if (actual_response.containsKey("support_chat") && support_chat != null && support_chat != "") {

				spogServer.assertResponseItem(support_chat, actual_response.get("support_chat").toString(), test);

			} else {
				test.log(LogStatus.PASS, "user has not filtered on param support_chat");
			}
			test.log(LogStatus.INFO, "Compare the support_sales");
			if (actual_response.containsKey("support_sales") && support_sales != null && support_sales != "") {
				spogServer.assertResponseItem(support_sales, actual_response.get("support_sales").toString(), test);
				if (support_sales.equals(null)) {
					support_sales = "https://www.arcserve.com/saleschat";
				}
			} else {
				test.log(LogStatus.PASS, "user has not filtered on param support_sales");
			}

			test.log(LogStatus.INFO, "Compare the support_email");
			if (actual_response.containsKey("support_email") && support_email != null && support_email != "") {
				spogServer.assertResponseItem(support_email, actual_response.get("support_email").toString(), test);
				if (support_email.equals(null)) {
					support_email = "cloudsupport@arcserve.com";
				}
			} else {
				test.log(LogStatus.PASS, "user has not filtered on param support_email");
			}

			test.log(LogStatus.INFO, "Compare the facebook_link");
			if (actual_response.containsKey("facebook_link") && facebook_link != null && facebook_link != "") {

				spogServer.assertResponseItem(facebook_link, actual_response.get("facebook_link"), test);

			} else {
				test.log(LogStatus.PASS, "user has not filtered on param facebook_link");
			}
			test.log(LogStatus.INFO, "Compare the twitter_link");
			if (actual_response.containsKey("twitter_link") && twitter_link != null && twitter_link != "") {
				spogServer.assertResponseItem(twitter_link, actual_response.get("twitter_link").toString(), test);
				if (twitter_link.equals(null)) {
					twitter_link = "https://twitter.com/Arcserve";
				}
			} else {
				test.log(LogStatus.PASS, "user has not filtered on param twitter_link");
			}
			test.log(LogStatus.INFO, "Compare the linkdin_link");
			if (actual_response.containsKey("linkdin_link") && linkdin_link != null && linkdin_link != "") {

				spogServer.assertResponseItem(linkdin_link, actual_response.get("linkdin_link").toString(), test);
				if (linkdin_link.equals(null)) {
					linkdin_link = "https://www.linkedin.com/company/arcserve";
				}

			} else {
				test.log(LogStatus.PASS, "user has not filtered on param linkdin_link");
			}

			test.log(LogStatus.INFO, "Compare the legal_notice");
			if (actual_response.containsKey("legal_notice") && legal_notice != null && legal_notice != "") {
				spogServer.assertResponseItem(legal_notice, actual_response.get("legal_notice").toString(), test);

			} else {
				test.log(LogStatus.PASS, "user has not filtered on param legal_notice");
			}
			test.log(LogStatus.INFO, "Compare the contact_us");
			if (actual_response.containsKey("contact_us") && contact_us != null && contact_us != "") {
				String actual_status = (String) actual_response.get("contact_us");
				spogServer.assertResponseItem(contact_us, actual_response.get("contact_us").toString(), test);

			} else {
				test.log(LogStatus.PASS, "user has not filtered on param contact_us");
			}
			test.log(LogStatus.INFO, "Compare the privacy");
			if (actual_response.containsKey("privacy") && privacy != null && privacy != "") {
				spogServer.assertResponseItem(privacy, actual_response.get("privacy").toString(), test);

			} else {
				test.log(LogStatus.PASS, "user has not filtered on param privacy");
			}
			test.log(LogStatus.INFO, "Compare the copyrights");
			if (actual_response.containsKey("copyrights") && copyrights != null && copyrights != "") {
				spogServer.assertResponseItem(copyrights, actual_response.get("copyrights").toString(), test);

			} else {
				test.log(LogStatus.PASS, "user has not filtered on param copyrights");
			}
		} else {
			String code = ExpectedErrorMessage.getCodeString();
			String message = ExpectedErrorMessage.getStatus();
			if (code.contains("0030000A")) {
				message = message.replace("{0}", organization_id);

			}
			spogServer.checkErrorCode(response, code);

			test.log(LogStatus.PASS, "The error code matched with the expected " + code);

			spogServer.checkErrorMessage(response, message);

			test.log(LogStatus.PASS, "The expected error message matched " + expectedstatuscode);

		}
	}
}
