package ui.spog.pages.configure;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.IHookable;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import freemarker.ext.dom.XPathSupport;
import genericutil.ErrorHandler;
import net.bytebuddy.agent.builder.AgentBuilder.InitializationStrategy.SelfInjection.Split;
import ui.base.common.BasePage;
import ui.base.common.SPOGMenuTreePath;
import ui.base.common.TableHeaders;
import ui.base.elements.Link;
import ui.base.elements.TextField;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;
import ui.spog.role.constants.RoleConstants;

public class SourceGroupsPage extends BasePage {

	/*
	 * Set Sourcegroup name to create source group 
	 * 
	 * @Nagamalleswari.sykam

	 */

	private ErrorHandler errorHandle = BasePage.getErrorHandler();
	public void setdetailstoCreateSoucreGroup(String SoucreGroupName) {


		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_CREATESOURCEGROUPBTN, BrowserFactory.getMaxTimeWaitUIElement());

		WebElement createSoucreGroupBtn =ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_CREATESOURCEGROUPBTN);
		createSoucreGroupBtn.click();

		if(!(SoucreGroupName==null && SoucreGroupName.equalsIgnoreCase(""))) {

			waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_SOUCREGROUPNAME, BrowserFactory.getMaxTimeWaitUIElement());
			WebElement SourceNameFd = ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_SOUCREGROUPNAME);
			SourceNameFd.clear();
			SourceNameFd.sendKeys(SoucreGroupName);
		}

	}

	/*
	 * Click create Button 
	 * 
	 * @Nagamalleswari.Sykam
	 */
	public void clickCreateBtn(String toastmessgae) {
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_CREATE, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement createBtn = ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_CREATE);
		createBtn.click();
		//waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TOAST_MESSGAE);
		/*String Actmessage = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TOAST_MESSGAE).getText();
		System.out.println(Actmessage);
		assertTrue(Actmessage.contains(toastmessgae));
		 */

	}

	/*
	 * Click Cancel Button 
	 * 
	 * @Nagamalleswari.Sykam
	 */

	public void clickonCancelBtn() {
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_CANCEL, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement createBtn = ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_CANCEL);
		createBtn.click();
	}

	/*
	 * Check Soucre group name 
	 * 
	 * @Nagamalleswari.Sykam
	 * 
	 */

	public String checkSoucreGroupName(String SoucreGroupName,String errorMessage) {


		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS, BrowserFactory.getMaxTimeWaitUIElement());
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_CREATESOURCEGROUPBTN, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement createSoucreGroupBtn =ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_CREATESOURCEGROUPBTN);
		createSoucreGroupBtn.click();

		String result="fail";
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_SOUCREGROUPNAME, BrowserFactory.getMaxTimeWaitUIElement());
		TextField SourceNameFd=new TextField(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_SOUCREGROUPNAME);
		// SourceNameFd = ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_SOUCREGROUPNAME);
		SourceNameFd.clear();
		SourceNameFd.sendKeys(SoucreGroupName);
		SourceNameFd.sendKeys(Keys.TAB.toString());

		if (SoucreGroupName.length()==0) {
			if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_REQUIREDERROR,BrowserFactory.getMaxTimeWaitUIElement())){
				errorHandle.printInfoMessageInDebugFile("The message: "+SPOGMenuTreePath.SPOG_COMMON_REQUIREDERROR);
				result= "Pass";
			}

		}else if(SoucreGroupName.length()>128) {
			if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_EXCEEDORGNAMEERROR,BrowserFactory.getMaxTimeWaitUIElement())){
				errorHandle.printInfoMessageInDebugFile("The message: "+SPOGMenuTreePath.SPOG_COMMON_EXCEEDORGNAMEERROR);
				result="Pass";
			}
		}else if(SoucreGroupName.length()<3) {
			if (ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_COMMON_MINLENTGHERROR,BrowserFactory.getMaxTimeWaitUIElement())){
				errorHandle.printInfoMessageInDebugFile("The message: "+SPOGMenuTreePath.SPOG_COMMON_MINLENTGHERROR);
				result= "Pass";
			}
		}
		else {
			assertFalse(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_REQUIREDERROR,BrowserFactory.getMaxTimeWaitUIElement()));
			assertFalse(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_MINLENGTHERROR,BrowserFactory.getMaxTimeWaitUIElement()));
			assertFalse(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_CONFIGURE_BRANDING_EXCEEDORGNAMEERROR,BrowserFactory.getMaxTimeWaitUIElement()));
		}

		return result;



	}


	/*
	 * Check the Group name in the Table
	 * 
	 * @author Nagamalleswari.sykam
	 * 
	 */

	public void  checksoucregroupnameinSoucregroupTable(String SourceGroupname, ExtentTest test) {

		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_HEADERS);

		HashMap<String, Integer> headerOrdr = getTableHeaderOrder(getTableHeaders());

		List<WebElement> rows = getRows();
		//HashMap<String, Integer> headerOrder = getTableHeaderOrder(getSoucregroupheaders());

		for (int i = 0; i < rows.size(); i++) {
			WebElement eachRow = rows.get(i);
			String xpath = "."+ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0).getValue();
			List<WebElement> columns = eachRow.findElements(By.xpath(xpath));

			if (columns.get(headerOrdr.get(TableHeaders.name)).getText().contains(SourceGroupname)) {
				assertEquals(columns.get(headerOrdr.get(TableHeaders.name)).getText(), SourceGroupname);
				break;
			}

			if(i == rows.size()-1) {
				test.log(LogStatus.FAIL, "Source group with name:"+SourceGroupname+" not found in table.");
				assertFalse(false, "Source group with name:"+SourceGroupname+" not found in table.");
			}

		}
	}


	/*
	 * Checking the groupName is Exist in the Table
	 * 
	 * @author Nagamalleswari.sykam
	 * 
	 * @param GroupName 
	 * 
	 */
	public boolean isGroupExist(String groupName) {

		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_HEADERS);
		HashMap<String, Integer> headerOrdr = getTableHeaderOrder(getTableHeaders());
		List<WebElement> rows = getRows();

		for (int i = 0; i < rows.size(); i++) {
			WebElement eachRow = rows.get(i);
			String xpath = "."+ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0).getValue();
			List<WebElement> columns = eachRow.findElements(By.xpath(xpath));

			if (columns.get(headerOrdr.get(TableHeaders.name)).getText().contains(groupName)) {
				return true;
			}
		}
		return false;
	}


	/*
	 * Delete the Group name using the Contextual Actions
	 * 
	 * @author Nagamalleswari.sykam
	 * 
	 * @param GroupName 
	 * 
	 * @Action
	 */
	public void deleteSoucreGroup(String soucreGroupName) {
		clickContextualActionForSpecifiedElement(soucreGroupName,"Delete Group" );

	}


	public void confirmtodeleteSoucregroup(String soucreGroupName) {
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_CONFIRM, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement Confirm=ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_CONFIRM);
		Confirm.click();
	}


	public void searchgroupName(String groupName) {
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS);

		if(groupName!=null) {
			TextField searchinput=   new  TextField(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_SEARCHINPUT);
			searchinput.clear();
			searchinput.sendKeys(groupName);

		}

		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_SEARCHICON, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement searchIcon = ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_SEARCHICON);
		searchIcon.click();
		waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TABLE_HEADERS);
		waitForSeconds(2);
		HashMap<String, Integer> headerOrdr = getTableHeaderOrder(getTableHeaders());
		List<WebElement> rows = getRows();
		for (int i = 0; i < rows.size(); i++) {
			WebElement eachRow = rows.get(i);
			String xpath = "."+ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0).getValue();
			List<WebElement> columns = eachRow.findElements(By.xpath(xpath));
			String column = columns.get(i).getText();
			System.out.println(column);
			String  ActGroupName = columns.get(headerOrdr.get(TableHeaders.name)).getText();
			assertEquals(ActGroupName, groupName, "");
		}



		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_CLEARALL, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement clearallBtn = ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_CLEARALL);
		clearallBtn.click();
	}


	public void canceltocreatesoucregroup(String sourcegroupName) {
		WebElement cancel =ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_CANCEL);
		cancel.click();
	}

	/*
	 * Clos the SourceGroupMenu
	 * 
	 * @author Nagamalleswari.sykam
	 * 
	 * @param GroupName 
	 * 
	 */
	public  void closeSoucregroupmenu(String sourcegroupName) {
		WebElement closeMenu =ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_CLOSEMENU);
		closeMenu.click();
	}


	/*
	 * Click on the SourceGroupname
	 * 
	 * @author Nagamalleswari.sykam
	 * 
	 * @param GroupName 
	 * 
	 */

	public void clickonGroupName(String groupName) {

		HashMap<String, Integer> headerOrdr = getTableHeaderOrder(getTableHeaders());
		List<WebElement> rows = getRows();

		for (int i = 0; i < rows.size(); i++) {
			WebElement eachRow = rows.get(i);
			String xpath = "."+ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0).getValue();

			try {
				List<WebElement> columns = eachRow.findElements(By.xpath(xpath));

				if (columns.get(headerOrdr.get(TableHeaders.name)).getText().contains(groupName)) {
					assertEquals(columns.get(headerOrdr.get(TableHeaders.name)).getText(), groupName);
					WebElement GroupNameEle =	columns.get(headerOrdr.get(TableHeaders.name));
					GroupNameEle.click();
					break;
				}
			}catch (NoSuchElementException e) {
				if(i == rows.size()-1) {
					//test.log(LogStatus.FAIL, "Source group with name:"+groupName+" not found in table.");
					assertTrue(false, "Source group with name:"+groupName+" not found in table.");
				}
			} catch (Exception e) {
				assertTrue(false, e.getMessage());
			}
		}

	}


	/*
	 * Add the soucres to the Source group
	 * 
	 * @author Nagamalleswari.sykam
	 * 
	 * @param GroupName 
	 * 
	 */
	public void addSoucrestoGroup(String soucreGorupName,String[] Soucres) {


		//String []soucres=Soucres.split(",");
		addSoucresToGroupbtn();
		HashMap<String, Integer> headerOrdr = getTableHeaderOrder(getTableHeaders());
		List<WebElement> rows = getRows();
		for(int j=0; j<Soucres.length;j++) {

			for (int i = 0; i < rows.size(); i++) {
				WebElement eachRow = rows.get(i);
				String xpath = "."+ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0).getValue();

				try {
					List<WebElement> columns = eachRow.findElements(By.xpath(xpath));
					if (columns.get(headerOrdr.get(TableHeaders.name)).getText().contains(Soucres[j])) {
						assertEquals(columns.get(headerOrdr.get(TableHeaders.name)).getText(), Soucres[j]);
						WebElement checkBox =columns.get(0);

						if(!checkBox.isSelected()) {
							checkBox.click();
						}
						break;
					};

				}catch (NoSuchElementException e) {
					if(i == rows.size()-1) {
						//test.log(LogStatus.FAIL, "Source group with name:"+groupName+" not found in table.");
						assertTrue(false, "Source group with name:"+Soucres+" not found in table.");
					}
				} catch (Exception e) {
					assertTrue(false, e.getMessage());
				}
			}


		}
	}


	/*
	 * Add Selected Source groups
	 * 
	 * @author Nagamalleswari.sykam
	 * 
	 * @param GroupName 
	 * 
	 */
	public void addSelectedSoucresBtn(String toastMessage) {

		//click on  the add Selected soucres Button
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_ADDSELECTEDSOURCES, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement addSelctedsourcesBtn = ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_ADDSELECTEDSOURCES);
		addSelctedsourcesBtn.click();
		addSoucresToGroupbtn();

		/*waitUntilElementAppear(SPOGMenuTreePath.SPOG_COMMON_TOAST_MESSGAE);
		String actualMessgae = ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_TOAST_MESSGAE).getText();
		assertTrue(actualMessgae.contains(toastMessage));
		 */
	}


	/*
	 * Click on the add soucres to the source group button 
	 * 
	 * @author Nagamalleswari.sykam
	 * 
	 * @param GroupName 
	 * 
	 */
	public void addSoucresToGroupbtn() {
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_ADDSOURCESTOGROUP, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement addsourcestoGroupBtn= ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_ADDSOURCESTOGROUP);
		addsourcestoGroupBtn.click();

	}


	/*
	 * Remove the sources form the source group 
	 * 
	 * @author Nagamalleswari.sykam
	 * 
	 * @param GroupName 
	 * 
	 */

	public void removeSoucresFromSoucreGroup(String groupName ,String [] Sources) {

		HashMap<String, Integer> headerOrdr = getTableHeaderOrder(getTableHeaders());
		List<WebElement> rows = getRows();
		for(int j=0; j<Sources.length;j++) {

			for (int i = 0; i < rows.size(); i++) {
				WebElement eachRow = rows.get(i);
				String xpath = "."+ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_COMMON_TABLE_COLUMNS).get(0).getValue();

				try {
					List<WebElement> columns = eachRow.findElements(By.xpath(xpath));
					if (columns.get(headerOrdr.get(TableHeaders.name)).getText().contains(Sources[j])) {
						assertEquals(columns.get(headerOrdr.get(TableHeaders.name)).getText(), Sources[j]);
						WebElement checkBox =columns.get(0);

						if(!checkBox.isSelected()) {
							checkBox.click();
						}
						break;
					};

				}catch (NoSuchElementException e) {
					if(i == rows.size()-1) {
						//test.log(LogStatus.FAIL, "Source group with name:"+groupName+" not found in table.");
						assertTrue(false, "Source group with name:"+Sources+" not found in table.");
					}
				} catch (Exception e) {
					assertTrue(false, e.getMessage());
				}
			}


		}
	}

	public void bulkActionBtn() {
		waitForSeconds(5);
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_BULKACTIONS, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement bulkActions =ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_BULKACTIONS);
		bulkActions.click();


	}

	public void removesoucrefromGroupBtn() {

		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_REMOVESOURCESFORMGROUP, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement removeSourceFromSourceGroupBtn = ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_REMOVESOURCESFORMGROUP);
		removeSourceFromSourceGroupBtn.click();

	}


	public void confirmbtn() {

		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_COMMON_CONFIRM, BrowserFactory.getMaxTimeWaitUIElement());
		WebElement confirmBtn =ElementFactory.getElement(SPOGMenuTreePath.SPOG_COMMON_CONFIRM);
		confirmBtn.click();

	}



	public void addAllSoucrestoGroup(String soucreGorupName,ArrayList<String> Soucres) {


		//String []soucres=Soucres.split(",");
		
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_ADDSOURCESTOGROUP, BrowserFactory.getMaxTimeWaitUIElement());
		addSoucresToGroupbtn();
		HashMap<String, Integer> headerOrder = getTableHeaderOrder(getTableHeaders());

		List<WebElement> headers = getTableHeaders();


		WebElement firsteHeader = headers.get(0);
		firsteHeader.click();
		
		waitUntilElementIsClickable(SPOGMenuTreePath.SPOG_CONFIGURE_SOURCEGROUPS_SELECTALLSOURCES,BrowserFactory.getMaxTimeWaitUIElement());
		addSelectedSoucresBtn(null);
		//addSoucresToGroupbtn();

	}

}
