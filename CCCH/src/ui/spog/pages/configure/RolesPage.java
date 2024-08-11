package ui.spog.pages.configure;




import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.groovy.transform.SynchronizedASTTransformation;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import ui.base.common.BasePage;
import ui.base.common.SPOGMenuTreePath;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;
import ui.spog.role.constants.RoleConstants;

public class RolesPage extends BasePage {


	/*public void CheckAllroles(String[] ActualHeaders,String[]ActualRows) {

		//Get the Table header
		List<WebElement>UserRolesrows =getRolesRows();
		for(int i=0;i<UserRolesrows.size();i++) {
			String EachRow=	UserRolesrows.get(i).getText();
			System.out.println(EachRow);


		}
	}*/



	/*
	 * Check the User roles 
	 * 
	 * @author Nagamalleswari.Sykam
	 */

	public void checkGetRoles(ArrayList<HashMap<String, Object>> expUserInfo, ExtentTest test) {


		List<WebElement> userRolesrows = getRolesRows();

		HashMap<String, Integer> headerOrder = getTableHeaderOrder(getUserRolesHeader());


		if (userRolesrows.size() == expUserInfo.size()) {
			for (int i = 0; i < expUserInfo.size(); i++) {

				WebElement row = userRolesrows.get(i);			

				String xpath = "."+ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0).getValue();

				List<WebElement> columns = row.findElements(By.xpath(xpath));

				int Columnsize =columns.size();
				test.log(LogStatus.INFO, "compare role");
				String actRole = columns.get(headerOrder.get(RoleConstants.HEADER_ROLE)).getText(); 
				String expRole = expUserInfo.get(i).get(RoleConstants.HEADER_ROLE).toString();
				assertEquals(actRole, expRole);

				test.log(LogStatus.INFO, "compare no of users");
				String actNoOfUsers = columns.get(headerOrder.get(RoleConstants.HEADER_NOOFROLES)).getText();
				String expNoOfUsers = expUserInfo.get(i).get(RoleConstants.HEADER_NOOFROLES).toString();
				assertEquals(actNoOfUsers, expNoOfUsers);

				test.log(LogStatus.INFO, "compare description");
				String actDescription = columns.get(headerOrder.get(RoleConstants.HEADER_DESCRIPTION)).getText();
				String expDescription = expUserInfo.get(i).get(RoleConstants.HEADER_DESCRIPTION).toString();
				assertEquals(actDescription, expDescription);
			}	
		}else {
			assertFalse(true, "users and row count does not match.");
		}
	}



	public List<WebElement> getUserRolesHeader() {
		// TODO Auto-generated method stub
		List<WebElement> Roleheaders=ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_TABLE_HEADERS);
		return Roleheaders;
	}


	/*
	 * List the Userrole rows
	 * 
	 * @author Nagamalleswari.Sykam
	 */

	public List<WebElement> getRolesRows(){
		//		List<WebElement> Rolerows=ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);
		return ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_TABLE_ROWS);

	}

	/*
	 * List the Userrole Columns
	 * 
	 * @author Nagamalleswari.Sykam
	 */

	public List<WebElement>getUserrolesColumns(){
		List<WebElement>RoleColumns =ElementFactory.getElements(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS);
		return RoleColumns;
	}


}