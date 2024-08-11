package ui.spog.server;

import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.databind.deser.Deserializers.Base;
import ui.base.common.BrowserOperation;
import ui.base.elements.BaseElement;
import ui.base.factory.BrowserFactory;
import ui.spog.pages.LoginPage;
import ui.spog.pages.UserProfilePage;
import static ui.base.elements.BaseElement.setText;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.ClickAction;
import static ui.base.elements.BaseElement.click;

public class UserProfilePageHelper {

  public UserProfilePage     aUserProfilePage;

  public static final String BaseUrl         = "https://tcc.arcserve.com/";
  public static final String loginPage       = BaseUrl + "login";
  public static final String logoutPage      = BaseUrl + "logout";
  public static final String userProfilePage = BaseUrl + "user_profile";


  public UserProfilePage getaUserProfilePage() {

    return aUserProfilePage;
  }


  public UserProfilePageHelper() {

    aUserProfilePage = new UserProfilePage();
  }


  public void openLoginPage() {

    BrowserOperation aBrowserOperation = new BrowserOperation(BrowserFactory.getWebDriver());
    aBrowserOperation.openUrl(logoutPage);
  }


  public void openLogoutPage() {

    BrowserOperation aBrowserOperation = new BrowserOperation(BrowserFactory.getWebDriver());
    aBrowserOperation.openUrl(userProfilePage);
  }


  public void openUserProfilePage() {

    click(aUserProfilePage.userProfileButton);
    click(aUserProfilePage.userProfileMenu);
  }


  public void setFirstName(String aString) {

    setText(aUserProfilePage.firstNameTextFiled, aString);
  }


  public void setLastName(String aString) {

    setText(aUserProfilePage.lastNameTextField, aString);
  }


  public void setWorkPhone(String aString) {

    // setText(aUserProfilePage.countryCode, "Aruba");
    // aUserProfilePage.countryCode.sendKeys("Aruba");
    setText(aUserProfilePage.workPhoneTextField, aString);
  }


  public void cancelChangeProfile() {

    click(aUserProfilePage.cancelButton);
  }


  public void saveChangeProfile() {

    click(aUserProfilePage.saveChangesButton);
  }


  public void setCurrentPassword(String aString) {

    setText(aUserProfilePage.currentPasswordTextFiled, aString);
  }


  public void setNewPassword(String aString) {

    setText(aUserProfilePage.newPasswordTextFiled, aString);
  }


  public void setConfirmPassword(String aString) {

    setText(aUserProfilePage.confirmPasswordTextFiled, aString);
  }


  public void updateNewPassword() {

    click(aUserProfilePage.updatePasswordButton);
  }

}
