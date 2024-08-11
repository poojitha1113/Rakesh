package ui.user;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JTextField;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import ui.base.elements.BaseElement;
import ui.base.factory.BrowserFactory;
import ui.spog.server.SPOGUIServer;
import ui.spog.server.UserProfilePageHelper;

public class UserProfileUITest {

	private SPOGServer		spogServer;
	private UserSpogServer	userSpogServer;
	private SPOGUIServer	spogUIServer;
	public Org4SPOGServer	org4SPOGServer;
	private String			csrAdminUserName;
	private String			csrAdminPassword;
	private ExtentReports	rep;
	private ExtentTest		test;
	private SQLServerDb		bqdb1;
	public int				Nooftest;
	private long			creationTime;
	private String			BQName			= null;
	private String			runningMachine;
	private testcasescount	count1;
	private String			buildVersion;
	private String			url;
	private String			TestDataPrefix	= getClass().getSimpleName();

	private String			common_password	= "Welcome*02";

	private String			direct_org_id	= null;
	private String			directOrgName;
	private String			directAdmin;
	private String			directAdminFirstName;
	private String			directAdminLastName;
	private String			port;
	private String			apiBaseURI;

	@BeforeClass
	@Parameters({ "apiBaseURI", "baseURI", "port", "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion",
			"browserType", "maxWaitTimeSec" })
	public void beforeClass(String apiBaseURI, String baseURI, String port, String logFolder, String adminUserName,
			String adminPassword, String buildVersion, String browserType, String maxWaitTimeSec)
			throws UnknownHostException {

		spogServer = new SPOGServer(apiBaseURI, port);
		userSpogServer = new UserSpogServer(apiBaseURI, port);
		org4SPOGServer = new Org4SPOGServer(apiBaseURI, port);
		this.url = baseURI;
		this.apiBaseURI = apiBaseURI;
		this.port = port;
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		rep = ExtentManager.getInstance("Test", logFolder);
		test = rep.startTest("beforeClass");

		// destory data for manule testing
		// destroyUserOrgById("bb4f9543-8e61-470e-af47-70048586c47f");
		// destroyUserOrgById("6e9d1c4a-c0bc-4c3f-ad75-ad8403ce94c2");
		// destroyUserOrgById("b694edde-cb2c-4b96-848f-7750f62c7a9d");
		// destroyUserOrgById("793543be-846e-4f34-a0be-197f514e36f7");
		// destroyUserOrgById("9bf98a59-d7fc-426a-ad21-b9c63435946e");
		// destroyUserOrgById("1c966e59-49ad-4f51-8170-7bec05d20ba8");

		// destroyUserOrg("yin.li@arcserve.com", "Abcdef123");
		// destroyUserOrg("zhibin.yang@arcserve.com", "Abcdef123");
		// destroyUserOrg("guohua.chen@arcserve.com", "Abcdef123");

		// prepareEnv(browserType, maxWaitTimeSec);

		this.BQName = this.getClass().getSimpleName();
		String author = "Kanamarlapudi, Chandra Kanth";
		this.runningMachine = InetAddress.getLocalHost().getHostName();
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);
		Nooftest = 0;
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();

		if (count1.isstarttimehit() == 0) {

			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			// creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(this.BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
						String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",
						author + " and Rest server is " + baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@BeforeMethod
	@Parameters({ "browserType", "maxWaitTimeSec" })
	public void beforeMethod(String browserType, String maxWaitTimeSec) {

		// spogUIServer = new SPOGUIServer(browserType,
		// Integer.valueOf(maxWaitTimeSec));
		// spogUIServer.openUrl(url);

		// LoginPage loginPage = new LoginPage();
		// String result = loginPage.login_Spog(csrAdminUserName,
		// csrAdminPassword);

	}

	public void Sleep(int s) {

		try {
			Thread.sleep(s);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setWorkPhone(String phoneNumber) {

		UserProfilePageHelper test = new UserProfilePageHelper();

		List<WebElement> elements = BrowserFactory.getBrowser()
				.findElements(By.xpath("//*[contains(text(),'Country')]"));

		if (elements.size() > 0) {
			elements.get(0).click();
			Sleep(1000);
			try {
				keyTypeString("Aruba");
				keyTypeCommand(KeyEvent.VK_ENTER);

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		test.setWorkPhone(phoneNumber);
	}

	@DataProvider(name = "correctProfileData")
	public final Object[][] correctProfileData() {

		return new Object[][] { { "first name could be a data", "1", "lastname", "4055171234" },
				{ "first name could be a latter", "a", "lastname", "4055171234" },
				{ "first name could be a symbol", "##", "lastname", "4055171234" },
				{ "first name could be 256 letters", RandomStringUtils.randomAlphabetic(128), "lastname",
						"4055171234" },
				{ "first name has blank space in middle", "a b", "lastname", "4055171234" },
				{ "lastname name could be a data", "firstname", "1", "4055171234" },
				{ "lastname name could be a letter", "firstname", "a", "4055171234" },
				{ "lastname name could be a symbol", "firstname", "?", "4055171234" },
				{ "first name could be 256 letters", "firstname", RandomStringUtils.randomAlphabetic(128),
						"4055171234" },
				{ "last name has blank space in middle", "firstname", "a b", "4055171234" },
				// {"workphone is optional", "firstname", "a b", ""}

		};
	}

	@Test(dataProvider = "correctProfileData")
	public void updateUserProfileSuccessfully(String testDesc, String firstName, String lastName, String workPhone) {

		spogUIServer.login_Spog(directAdmin, common_password);
		// spogUIServer.login_Spog(csrAdminUserName, csrAdminPassword);

		UserProfilePageHelper test = new UserProfilePageHelper();

		test.openUserProfilePage();
		Sleep(3000);
		test.setFirstName(firstName);
		test.setLastName(lastName);
		// BrowserFactory.getBrowser().findElement(By.xpath("//*[contains(text(),'Country')]")).click();
		// Sleep(2000);
		// try {
		// keyTypeString("Aruba");
		// keyTypeCommand(KeyEvent.VK_ENTER);
		//
		// } catch (Exception e1) {
		// e1.printStackTrace();
		// }
		// test.setWorkPhone(workPhone);
		setWorkPhone(workPhone);
		test.saveChangeProfile();
		// BrowserFactory.getBrowser().findElement(By.xpath("//*[contains(text(),
		// 'information')]"));

		spogUIServer.openUrl("https://tcc.arcserve.com/logout");
		spogUIServer.login_Spog(directAdmin, common_password);
		test.openUserProfilePage();
		Sleep(3000);
		String updateFirstName = BaseElement.getText(test.getaUserProfilePage().firstNameTextFiled);
		assertEquals(updateFirstName, firstName);
		String updateLastName = BaseElement.getText(test.getaUserProfilePage().lastNameTextField);
		assertEquals(updateLastName, lastName);
		String updateWorkPhone = BaseElement.getText(test.getaUserProfilePage().workPhoneTextField);
		assertEquals(updateWorkPhone, workPhone);
	}

	@DataProvider(name = "incorrectProfileData")
	public final Object[][] incorrectProfileData() {

		return new Object[][] { { "first name could not be blank", "", "lastname" },
				{ "last name could not be blank", "firstname", "" } };

	}

	@Test(dataProvider = "incorrectProfileData")
	public void updateUserProfileFailed(String testDesc, String firstName, String lastName) {

		spogUIServer.login_Spog(directAdmin, common_password);
		// spogUIServer.login_Spog(csrAdminUserName, csrAdminPassword);

		UserProfilePageHelper test = new UserProfilePageHelper();

		test.openUserProfilePage();
		Sleep(3000);
		String origFirstName = BaseElement.getText(test.getaUserProfilePage().firstNameTextFiled);
		String origLastName = BaseElement.getText(test.getaUserProfilePage().lastNameTextField);
		String origWorkPhone = BaseElement.getText(test.getaUserProfilePage().workPhoneTextField);

		test.setFirstName(firstName);
		test.setLastName(lastName);
		setWorkPhone("123456");

		assertFalse(BrowserFactory.getWebDriver().findElement(By.id("contact-info-save")).isEnabled());

		spogUIServer.openUrl("https://tcc.arcserve.com/logout");
		spogUIServer.login_Spog(directAdmin, common_password);
		test.openUserProfilePage();
		Sleep(3000);
		String updateFirstName = BaseElement.getText(test.getaUserProfilePage().firstNameTextFiled);
		assertEquals(updateFirstName, origFirstName);
		String updateLastName = BaseElement.getText(test.getaUserProfilePage().lastNameTextField);
		assertEquals(updateLastName, origLastName);

	}

	@Test
	public void cancelUpdateUserProfile() {

		spogUIServer.login_Spog(directAdmin, common_password);
		// spogUIServer.login_Spog(csrAdminUserName, csrAdminPassword);

		UserProfilePageHelper test = new UserProfilePageHelper();

		test.openUserProfilePage();
		Sleep(3000);

		String origFirstName = BaseElement.getText(test.getaUserProfilePage().firstNameTextFiled);
		String origLastName = BaseElement.getText(test.getaUserProfilePage().lastNameTextField);
		String origWorkPhone = BaseElement.getText(test.getaUserProfilePage().workPhoneTextField);

		test.setFirstName("firstName");
		test.setLastName("lastName");
		// test.setWorkPhone("405 517 999");
		setWorkPhone("123456");
		test.cancelChangeProfile();

		spogUIServer.openUrl("http://tcc.arcserve.com/logout");
		spogUIServer.login_Spog(directAdmin, common_password);
		test.openUserProfilePage();
		Sleep(3000);
		String updateFirstName = BaseElement.getText(test.getaUserProfilePage().firstNameTextFiled);
		assertEquals(updateFirstName, origFirstName);
		String updateLastName = BaseElement.getText(test.getaUserProfilePage().lastNameTextField);
		assertEquals(updateLastName, origLastName);
		String updateWorkPhone = BaseElement.getText(test.getaUserProfilePage().workPhoneTextField);
		assertEquals(updateWorkPhone, origWorkPhone);

	}

	// @Test
	public void changeUserPhoto() {

		spogUIServer.login_Spog(directAdmin, common_password);
		// spogUIServer.login_Spog(csrAdminUserName, csrAdminPassword);

		UserProfilePageHelper test = new UserProfilePageHelper();

		test.openUserProfilePage();

		Sleep(2000);
		test.aUserProfilePage.updatePhotoLink.click();

		Sleep(2000);
		BrowserFactory.getBrowser().findElement(By.xpath("//*[contains(text(),'Choose Photo')]")).click();
		Sleep(2000);
		try {
			// keyTypeString("C:" + File.separator + "Windows" + File.separator
			// + "abcd.gif");
			keyTypeString("help.jpg");
			keyTypeCommand(KeyEvent.VK_ENTER);

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		test.cancelChangeProfile();

		spogUIServer.openUrl("https://tcc.arcserve.com/logout");
		// spogUIServer.login_Spog(directAdmin, common_password);
		// test.openUserProfilePage();
		// Sleep(3000);

	}

	public void keyTypeCommand(int value) throws SecurityException, NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException, InterruptedException, AWTException {

		Robot aRobot = new Robot();
		aRobot.keyPress(value);
		aRobot.keyRelease(value);
	}

	public void keyTypeString(String value) throws SecurityException, NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException, InterruptedException, AWTException {

		boolean shiftOn = false;
		String key = null;
		int keyCode;
		char[] character = new char[value.length()];
		for (int index = 0; index < value.length(); index++) {
			character[index] = value.charAt(index);
		}
		for (int index = 0; index < character.length; index++) {
			switch (character[index]) {
				case ' ':
					key = "VK_SPACE";
					shiftOn = false;
					break;
				case 'a':
					key = "VK_A";
					shiftOn = false;
					break;
				case 'b':
					key = "VK_B";
					shiftOn = false;
					break;
				case 'c':
					key = "VK_C";
					shiftOn = false;
					break;
				case 'd':
					key = "VK_D";
					shiftOn = false;
					break;
				case 'e':
					key = "VK_E";
					shiftOn = false;
					break;
				case 'f':
					key = "VK_F";
					shiftOn = false;
					break;
				case 'g':
					key = "VK_G";
					shiftOn = false;
					break;
				case 'h':
					key = "VK_H";
					shiftOn = false;
					break;
				case 'i':
					key = "VK_I";
					shiftOn = false;
					break;
				case 'j':
					key = "VK_J";
					shiftOn = false;
					break;
				case 'k':
					key = "VK_K";
					shiftOn = false;
					break;
				case 'l':
					key = "VK_L";
					shiftOn = false;
					break;
				case 'm':
					key = "VK_M";
					shiftOn = false;
					break;
				case 'n':
					key = "VK_N";
					shiftOn = false;
					break;
				case 'o':
					key = "VK_O";
					shiftOn = false;
					break;
				case 'p':
					key = "VK_P";
					shiftOn = false;
					break;
				case 'q':
					key = "VK_Q";
					shiftOn = false;
					break;
				case 'r':
					key = "VK_R";
					shiftOn = false;
					break;
				case 's':
					key = "VK_S";
					shiftOn = false;
					break;
				case 't':
					key = "VK_T";
					shiftOn = false;
					break;
				case 'u':
					key = "VK_U";
					shiftOn = false;
					break;
				case 'v':
					key = "VK_V";
					shiftOn = false;
					break;
				case 'w':
					key = "VK_W";
					shiftOn = false;
					break;
				case 'x':
					key = "VK_X";
					shiftOn = false;
					break;
				case 'y':
					key = "VK_Y";
					shiftOn = false;
					break;
				case 'z':
					key = "VK_Z";
					shiftOn = false;
					break;
				case 'A':
					key = "VK_A";
					shiftOn = true;
					break;
				case 'B':
					key = "VK_B";
					shiftOn = true;
					break;
				case 'C':
					key = "VK_C";
					shiftOn = true;
					break;
				case 'D':
					key = "VK_D";
					shiftOn = true;
					break;
				case 'E':
					key = "VK_E";
					shiftOn = true;
					break;
				case 'F':
					key = "VK_F";
					shiftOn = true;
					break;
				case 'G':
					key = "VK_G";
					shiftOn = true;
					break;
				case 'H':
					key = "VK_H";
					shiftOn = true;
					break;
				case 'I':
					key = "VK_I";
					shiftOn = true;
					break;
				case 'J':
					key = "VK_J";
					shiftOn = true;
					break;
				case 'K':
					key = "VK_K";
					shiftOn = true;
					break;
				case 'L':
					key = "VK_L";
					shiftOn = true;
					break;
				case 'M':
					key = "VK_M";
					shiftOn = true;
					break;
				case 'N':
					key = "VK_N";
					shiftOn = true;
					break;
				case 'O':
					key = "VK_O";
					shiftOn = true;
					break;
				case 'P':
					key = "VK_P";
					shiftOn = true;
					break;
				case 'Q':
					key = "VK_Q";
					shiftOn = true;
					break;
				case 'R':
					key = "VK_R";
					shiftOn = true;
					break;
				case 'S':
					key = "VK_S";
					shiftOn = true;
					break;
				case 'T':
					key = "VK_T";
					shiftOn = true;
					break;
				case 'U':
					key = "VK_U";
					shiftOn = true;
					break;
				case 'V':
					key = "VK_V";
					shiftOn = true;
					break;
				case 'W':
					key = "VK_W";
					shiftOn = true;
					break;
				case 'X':
					key = "VK_X";
					shiftOn = true;
					break;
				case 'Y':
					key = "VK_Y";
					shiftOn = true;
					break;
				case 'Z':
					key = "VK_Z";
					shiftOn = true;
					break;
				case '0':
					key = "VK_0";
					shiftOn = false;
					break;
				case '1':
					key = "VK_1";
					shiftOn = false;
					break;
				case '2':
					key = "VK_2";
					shiftOn = false;
					break;
				case '3':
					key = "VK_3";
					shiftOn = false;
					break;
				case '4':
					key = "VK_4";
					shiftOn = false;
					break;
				case '5':
					key = "VK_5";
					shiftOn = false;
					break;
				case '6':
					key = "VK_6";
					shiftOn = false;
					break;
				case '7':
					key = "VK_7";
					shiftOn = false;
					break;
				case '8':
					key = "VK_8";
					shiftOn = false;
					break;
				case '9':
					key = "VK_9";
					shiftOn = false;
					break;
				case ')':
					key = "VK_0";
					shiftOn = true;
					break;
				case '!':
					key = "VK_1";
					shiftOn = true;
					break;
				case '@':
					key = "VK_2";
					shiftOn = true;
					break;
				case '#':
					key = "VK_3";
					shiftOn = true;
					break;
				case '$':
					key = "VK_4";
					shiftOn = true;
					break;
				case '%':
					key = "VK_5";
					shiftOn = true;
					break;
				case '^':
					key = "VK_6";
					shiftOn = true;
					break;
				case '&':
					key = "VK_7";
					shiftOn = true;
					break;
				case '*':
					key = "VK_8";
					shiftOn = true;
					break;
				case '(':
					key = "VK_9";
					shiftOn = true;
					break;
				case '-':
					key = "VK_OEM_MINUS";
					shiftOn = false;
					break;
				case '_':
					key = "VK_OEM_MINUS";
					shiftOn = true;
					break;
				case '[':
					key = "VK_OEM_4";
					shiftOn = false;
					break;
				case '{':
					key = "VK_OEM_4";
					shiftOn = true;
					break;
				case ']':
					key = "VK_OEM_6";
					shiftOn = false;
					break;
				case '}':
					key = "VK_OEM_6";
					shiftOn = true;
					break;
				case '|':
					key = "VK_OEM_102";
					shiftOn = true;
					break;
				case ';':
					key = "VK_SEMICOLON";
					shiftOn = false;
					break;
				case ':':
					key = "VK_SEMICOLON";
					shiftOn = true;
					break;
				case '"':
					key = "VK_OEM_7";
					shiftOn = true;
					break;
				case ',':
					key = "VK_COMMA";
					shiftOn = false;
					break;
				case '<':
					key = "VK_COMMA";
					shiftOn = true;
					break;
				case '.':
					key = "VK_PERIOD";
					shiftOn = false;
					break;
				case '>':
					key = "VK_PERIOD";
					shiftOn = true;
					break;
				case '/':
					key = "VK_SLASH";
					shiftOn = false;
					break;
				case '?':
					key = "VK_SLASH";
					shiftOn = true;
					break;

				default:
					System.out.println("Keyboard: Invalid Value");
					break;
			}

			Robot aRobot = new Robot();
			KeyEvent ke = new KeyEvent(new JTextField(), 0, 0, 0, 0, ' ');
			Class clazz = ke.getClass();
			Field field = clazz.getField(key);
			keyCode = field.getInt(ke);
			if (shiftOn == true) {
				aRobot.keyPress(KeyEvent.VK_SHIFT);
			}
			aRobot.keyPress(keyCode);
			if (shiftOn == true) {
				aRobot.keyRelease(KeyEvent.VK_SHIFT);
			}
			Sleep(100);

		}
	}

	@DataProvider(name = "incorrectWorkPhone")
	public final Object[][] incorrectWorkPhone() {

		return new Object[][] { { "work phone contain letter", "405517123a" },
				{ "work phone contain symbol", "40517123?" },
				// {"work phone has not country code", "405517123"}
		};

	}

	@Test(dataProvider = "incorrectWorkPhone")
	public void updateIncorrectWorkPhone(String testDesc, String workPhone) {

		spogUIServer.login_Spog(directAdmin, common_password);
		// spogUIServer.login_Spog(csrAdminUserName, csrAdminPassword);

		UserProfilePageHelper test = new UserProfilePageHelper();

		test.openUserProfilePage();
		Sleep(3000);
		String origWorkPhone = BaseElement.getText(test.getaUserProfilePage().workPhoneTextField);

		setWorkPhone(workPhone);
		String isDisable = test.getaUserProfilePage().saveChangesButton.getAttribute("class");
		// boolean isEnabled =
		// test.getaUserProfilePage().saveChangesButton.isEnabled();
		// assertFalse(isEnabled);
		// assertTrue(isDisable);
		// test.saveChangeProfile();

		spogUIServer.openUrl("https://tcc.arcserve.com/logout");
		spogUIServer.login_Spog(directAdmin, common_password);
		test.openUserProfilePage();

		String updateWorkPhone = BaseElement.getText(test.getaUserProfilePage().workPhoneTextField);
		assertEquals(updateWorkPhone, origWorkPhone);
	}

	@DataProvider(name = "incorrectPassword")
	public final Object[][] incorrectPassword() {

		return new Object[][] { { "password has not upper case", "welcome*02" },
				{ "password has not lower case", "WELCOME*02" }, { "password has not number", "Welcome*aa" },
				// {"password has not symbol", "Welcomea02"}
		};

	}

	@Test(dataProvider = "incorrectPassword")
	public void updateIncorrectPassword(String testDesc, String newPassword) {

		spogUIServer.login_Spog(directAdmin, common_password);
		// spogUIServer.login_Spog(csrAdminUserName, csrAdminPassword);

		UserProfilePageHelper test = new UserProfilePageHelper();

		test.openUserProfilePage();
		Sleep(3000);
		test.setCurrentPassword(common_password);
		test.setNewPassword(newPassword);
		test.setConfirmPassword(newPassword);

		if (BrowserFactory.getBrowser().findElement(By.id("change-pass-submit")).isEnabled()) {
			BrowserFactory.getBrowser().findElement(By.id("change-pass-submit")).click();
			Sleep(3000);

			BrowserFactory.getBrowser().findElement(By.xpath("//*[contains(text(),'OK')]")).click();
			// BrowserFactory.getBrowser().findElement(By.xpath(".//..//..//span[text()='OK']"));
			Sleep(3000);
			// BrowserFactory.getBrowser().findElement(By.id("change-pass-submit")).click();

			spogUIServer.openUrl("http://tcc.arcserve.com/logout");
			spogUIServer.login_Spog_with_error(directAdmin, newPassword, "Invalid credentials");
		} else {
			assertFalse(BrowserFactory.getBrowser().findElement(By.id("change-pass-submit")).isEnabled());
		}

	}

	@DataProvider(name = "incorrectPasswordInput")
	public final Object[][] incorrectPasswordInput() {

		return new Object[][] { // {"no current password provided", "",
								// "Welcome*03", "Welcome*03"},
				{ "incorrect current password provided", "welcome*04", "Welcome*03", "Welcome*03" },
				// {"no new password provide", common_password, "Welcome*03",
				// ""},
				{ "no confirm password provide", common_password, "", "Welcome*03" },
				// {"different password", common_password, "Welcome*04",
				// "Welcome*03"}
		};

	}

	@Test(dataProvider = "incorrectPasswordInput")
	public void updateIncorrectPasswordInput(String testDesc, String currentPassword, String newPassword,
			String confirmPassword) {

		spogUIServer.login_Spog(directAdmin, common_password);
		// spogUIServer.login_Spog(csrAdminUserName, csrAdminPassword);

		UserProfilePageHelper test = new UserProfilePageHelper();

		test.openUserProfilePage();
		Sleep(3000);
		test.setCurrentPassword(currentPassword);
		test.setNewPassword(newPassword);
		test.setConfirmPassword(confirmPassword);

		if (BrowserFactory.getBrowser().findElement(By.id("change-pass-submit")).isEnabled()) {
			BrowserFactory.getBrowser().findElement(By.id("change-pass-submit")).click();
			Sleep(3000);

			BrowserFactory.getBrowser().findElement(By.xpath("//*[contains(text(),'OK')]")).click();
			// BrowserFactory.getBrowser().findElement(By.xpath(".//..//..//span[text()='OK']"));
			Sleep(3000);
			// BrowserFactory.getBrowser().findElement(By.id("change-pass-submit")).click();

			spogUIServer.openUrl("http://tcc.arcserve.com/logout");
			spogUIServer.login_Spog_with_error(directAdmin, newPassword, "Invalid credentials");
		} else {
			assertFalse(BrowserFactory.getBrowser().findElement(By.id("change-pass-submit")).isEnabled());
		}

	}

	@Test()
	public void updatePasswordCorrect() {

		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);

		String tdirectOrgName = "directOrg_" + TestDataPrefix + RandomStringUtils.randomAlphanumeric(4);
		String tdirectAdmin = "directAdmin_" + TestDataPrefix + RandomStringUtils.randomAlphanumeric(3) + "@abc.com";
		String tdirectAdminFirstName = RandomStringUtils.randomAlphanumeric(8);
		String tdirectAdminLastName = RandomStringUtils.randomAlphanumeric(8);
		String tcommon_password = "Welcome*03";
		String tdirect_org_id = spogServer.CreateOrganizationWithCheck(tdirectOrgName, SpogConstants.DIRECT_ORG,
				tdirectAdmin, tcommon_password, tdirectAdminFirstName, tdirectAdminLastName, test);

		spogUIServer.login_Spog(tdirectAdmin, tcommon_password);
		// spogUIServer.login_Spog(csrAdminUserName, csrAdminPassword);

		UserProfilePageHelper test = new UserProfilePageHelper();

		test.openUserProfilePage();
		Sleep(3000);
		String newPassword = "Welcome*88";
		test.setCurrentPassword(tcommon_password);
		test.setNewPassword(newPassword);
		test.setConfirmPassword(newPassword);

		BrowserFactory.getBrowser().findElement(By.id("change-pass-submit")).click();

		spogUIServer.openUrl("https://tcc.arcserve.com/logout");
		spogUIServer.login_Spog(tdirectAdmin, newPassword);
	}

	@AfterMethod
	public void afterMethod() {

		spogUIServer.openUrl("https://tcc.arcserve.com/logout");

	}

	@Parameters({ "browserType", "maxWaitTimeSec" })
	private void prepareEnv(String browserType, String maxWaitTimeSec) {

		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);

		directOrgName = "directOrg_spog_qa_" + TestDataPrefix + RandomStringUtils.randomAlphanumeric(5);
		directAdmin = "directAdmin_" + TestDataPrefix + RandomStringUtils.randomAlphanumeric(3) + "@abc.com";
		directAdminFirstName = RandomStringUtils.randomAlphanumeric(8);
		directAdminLastName = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO, "create a direct org");
		direct_org_id = spogServer.CreateOrganizationByEnroll(directOrgName, SpogConstants.DIRECT_ORG, directAdmin,
				common_password, directAdminFirstName, directAdminLastName);

		spogUIServer = new SPOGUIServer(browserType, Integer.valueOf(maxWaitTimeSec));
		spogUIServer.openUrl(url);
	}

	private void deleteEnv() {

		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);

		test.log(LogStatus.INFO, "delete direct org");
		spogServer.errorHandle.printInfoMessageInDebugFile("delete direct organization");
		spogServer.DeleteOrganizationWithCheck(direct_org_id, test);

		// test.log(LogStatus.INFO, "delete msp org");
		// spogServer.errorHandle.printInfoMessageInDebugFile("delete msp
		// organization");
		// spogServer.DeleteOrganizationWithCheck(msp_org_id, test);

	}

	@AfterMethod
	public void getResult(ITestResult result) {

		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();
			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());
		} else if (result.getStatus() == ITestResult.SKIP) {
			count1.setskippedcount();
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();
		}
		// ending test
		// endTest(logger) : It ends the current test and prepares to create
		// HTML report
		rep.endTest(test);
	}

	@AfterClass
	public void aftertest() {

		spogUIServer.destroy();

		deleteEnv();
		test.log(LogStatus.INFO, "The total test cases passed are " + count1.getpassedcount());
		test.log(LogStatus.INFO, "the total test cases failed are " + count1.getfailedcount());
		rep.flush();
	}

	@AfterTest
	public void updatebd() {

		String orgHasString = this.getClass().getSimpleName();
		System.out.println("in father afterclass");
		System.out.println("class in father is:" + orgHasString);
		destroyOrg(orgHasString);
		try {
			if (count1.getfailedcount() > 0) {
				Nooftest = count1.getpassedcount() + count1.getfailedcount() + count1.getskippedcount();
				bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest),
						Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()),
						String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Failed");
			} else {
				Nooftest = count1.getpassedcount() + count1.getfailedcount() + count1.getskippedcount();
				bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest),
						Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()),
						String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Passed");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void destroyOrg(String orgString) {

		spogServer = new SPOGServer(apiBaseURI, port);
		org4SPOGServer = new Org4SPOGServer(apiBaseURI, port);
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		org4SPOGServer.setToken(spogServer.getJWTToken());
		int total_page = org4SPOGServer.getOrgPagesBySearchStringWithCsrLogin(orgString, spogServer.getJWTToken());
		if (total_page > 0) {
			for (int i = 1; i <= total_page; i++) {
				ArrayList<String> ret = org4SPOGServer.getOrgIdsBySearchStringWithCsrLogin(spogServer.getJWTToken(),
						orgString, i);
				if (ret != null) {
					if (ret.size() > 0) {
						for (int del = 0; del < ret.size(); del++) {
							org4SPOGServer.destroyOrganizationWithoutCheck(ret.get(del).toString());
						}
					}
				}
			}
		}
	}

	public void destroyUserOrg(String username, String password) {

		spogServer = new SPOGServer(apiBaseURI, port);
		org4SPOGServer = new Org4SPOGServer(apiBaseURI, port);

		spogServer.userLogin(username, password);
		String orgId = spogServer.GetLoggedinUserOrganizationID();

		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		org4SPOGServer.setToken(spogServer.getJWTToken());

		org4SPOGServer.destroyOrganizationWithoutCheck(orgId);

	}

	public void destroyUserOrgById(String orgId) {

		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		org4SPOGServer.setToken(spogServer.getJWTToken());

		org4SPOGServer.destroyOrganizationWithoutCheck(orgId);

	}

}
