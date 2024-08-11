package ui.spog.server;

import java.util.ArrayList;
import java.util.HashMap;

import com.relevantcodes.extentreports.ExtentTest;

import ui.spog.pages.configure.RolesPage;

public class RolesHelperPage  extends SPOGUIServer {
	
	static String reportsPage= "https://tcc.arcserve.com/configure/access_control/user_roles";
	
	public RolesHelperPage(String browserType, int maxTimeWaitSec) {
		super(browserType, maxTimeWaitSec);
		// TODO Auto-generated constructor stub
	}

	
	
	public void getRolesInformation(ArrayList<HashMap<String, Object>> expUserInfo, ExtentTest test) {
		
		RolesPage rolespage= goToRolesPage();
		rolespage.checkGetRoles(expUserInfo, test);
		
	}

}