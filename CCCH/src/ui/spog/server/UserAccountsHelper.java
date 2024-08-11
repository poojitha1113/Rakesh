package ui.spog.server;

import io.restassured.response.Response;
import ui.spog.pages.configure.UserAccountsPage;

public class UserAccountsHelper extends SPOGUIServer{
	
	public UserAccountsHelper(String browserType, int maxTimeWaitSec){
		super(browserType, maxTimeWaitSec);
		
	}
	/**
	 *  @author SHUO.ZHANG
	 * @param response
	 */
	public void checkUserList( Response response){
		
		UserAccountsPage userAccountsPage = goToUserAccountPage();
		userAccountsPage.checkUserList(response);
		
	}
	
	/**
	 *  @author SHUO.ZHANG
	 * @param name
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param role
	 */
	public void checkUserDetail(String name, String firstName, String lastName, String email, String role){
		
		UserAccountsPage userAccountsPage = goToUserAccountPage();
		userAccountsPage.checkUserDetails(name, firstName, lastName, email, role);
	}
	
	/**
	 *  @author SHUO.ZHANG
	 * @param name
	 * @param firstName
	 * @param lastName
	 * @param uiRole
	 * @param email
	 */
	public void editUserDetail(String name, String firstName, String lastName,  String email){
		UserAccountsPage userAccountsPage = goToUserAccountPage();
		userAccountsPage.editUserDetails(name, firstName, lastName);
		userAccountsPage.checkUserDetails(firstName + " " + lastName, firstName, lastName, email, null);
	}
	
	public void editUserDetailsErrorHandling(String name, String firstName, String lastName){
		
		UserAccountsPage userAccountsPage = goToUserAccountPage();
		userAccountsPage.editUserDetailsErrorHandling(name, firstName, lastName);
	}
	
}
