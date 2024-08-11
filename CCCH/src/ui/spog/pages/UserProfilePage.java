package ui.spog.pages;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import com.gargoylesoftware.htmlunit.javascript.host.Element;
import bsh.This;
import genericutil.ErrorHandler;
import ui.base.common.BasePage;
import ui.base.common.SPOGMenuTreePath;
import ui.base.elements.Button;
import ui.base.elements.TextField;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;

public class UserProfilePage {

  public WebDriver         webDriver;

  @FindBy(
      xpath = "//div[@class='logout-wrapper d-flex align-items-center']")
  public static WebElement userProfileButton;

  @FindBy(
      xpath = "/html/body/div[@id='popover-logout']/div[@class='popover-content']/a[1]")
  public WebElement        userProfileMenu;

  @FindBy(
      id = "first_name")
  public WebElement        firstNameTextFiled;

  @FindBy(
      id = "last_name")
  public WebElement        lastNameTextField;

  @FindBy(
      xpath = "//*[contains(text(), 'Country')]")
  public WebElement        countryCode;

  @FindBy(
      id = "phone_number")
  public WebElement        workPhoneTextField;

  @FindBy(
      name = "old_password")
  public WebElement        currentPasswordTextFiled;

  @FindBy(
      name = "new_password")
  public WebElement        newPasswordTextFiled;

  @FindBy(
      name = "confirmNewPassword")
  public WebElement        confirmPasswordTextFiled;

  @FindBy(
      xpath = "//*[contains(text(), 'Cancel')]")
  public WebElement        cancelButton;

  @FindBy(
      xpath = "//*[contains(text(), 'Save')]")
  public WebElement        saveChangesButton;

  @FindBy(
      xpath = "//*[contains(text(), 'Update Password')]")
  public WebElement        updatePasswordButton;


  @FindBy(
      id = "contact-info-update-photo")
  public WebElement        updatePhotoLink;


  public UserProfilePage(WebDriver aDriver) {
    this.webDriver = aDriver;
    PageFactory.initElements(webDriver, this);
  }


  public UserProfilePage() {
    this.webDriver = BrowserFactory.getWebDriver();
    PageFactory.initElements(webDriver, this);
  }


  public void clickUserProfileButton() {

    userProfileButton.click();
  }


  public void clickUserProfileMenu() {

    userProfileMenu.click();
  }

  //
  // public void cleanFirstNameField() {
  //
  // firstNameTextFiled.clear();
  // }
  //
  //
  // public void enterTextInFirstNameField(String aString) {
  //
  // firstNameTextFiled.sendKeys(aString);
  // }
  //
  //
  // public void cleanLastNameField() {
  //
  // lastNameTextField.clear();
  // }
  //
  //
  // public void enterTextInLastNameField(String aString) {
  //
  // lastNameTextField.sendKeys(aString);
  // }
  //
  //
  // public void cleanworkPhoneTextField() {
  //
  // workPhoneTextField.clear();
  // }
  //
  //
  // public void enterTextInworkPhoneTextField(String aString) {
  //
  // workPhoneTextField.sendKeys(aString);
  // }
  //
  //
  // public void clickSaveChangeButton() {
  //
  // saveChangesButton.click();
  // }
  //
  //
  // public void clickUpdatePasswordButton() {
  //
  // updatePasswordButton.click();
  // }
  //
  //
  // public void clickCancelButton() {
  //
  // cancelButton.click();
  // }

}
