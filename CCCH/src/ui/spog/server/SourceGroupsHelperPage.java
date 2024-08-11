package ui.spog.server;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;

import com.relevantcodes.extentreports.ExtentTest;

import ui.spog.pages.configure.SourceGroupsPage;

public class SourceGroupsHelperPage extends SPOGUIServer {

	public SourceGroupsHelperPage(String browserType, int maxTimeWaitSec) {
		super(browserType, maxTimeWaitSec);
		// TODO Auto-generated constructor stub
	}


	public void createSoucregroup(String soucreGroupName,String toastmessage) {
		SourceGroupsPage sourceGroupsPage = goToSourceGroupPage();
		sourceGroupsPage.setdetailstoCreateSoucreGroup(soucreGroupName);
		if(soucreGroupName.contains("cancel")) {
			sourceGroupsPage.canceltocreatesoucregroup(soucreGroupName);
		}
		else if(soucreGroupName.contains("close")) {
			sourceGroupsPage.closeSoucregroupmenu(soucreGroupName);
		}else {
			sourceGroupsPage.clickCreateBtn(toastmessage);
		}


	}

	public void CreateSoucreGroupWithCheck(String SoucreGroupName,String errorMessage) {
		SourceGroupsPage sourceGroupsPage = goToSourceGroupPage();
		if(SoucreGroupName!=null) {
			String result=sourceGroupsPage.checkSoucreGroupName(SoucreGroupName,errorMessage);
			assertEquals(result, "Pass");
		}

		sourceGroupsPage.clickonCancelBtn();
	}

	public void checksourcegroupList(String soucreGroupName,ExtentTest test) {
		SourceGroupsPage sourceGroupsPage = goToSourceGroupPage();
		sourceGroupsPage.checksoucregroupnameinSoucregroupTable(soucreGroupName,test);	

	}
	public void deleteSoucreGroup(String soucreGroupName,ExtentTest test) {
		SourceGroupsPage sourceGroupsPage = goToSourceGroupPage();
		sourceGroupsPage.deleteSoucreGroup(soucreGroupName);
		sourceGroupsPage.confirmtodeleteSoucregroup(soucreGroupName);
	}



	public void searchbygroupname(String sourcegroup) {
		SourceGroupsPage sourceGroupsPage = goToSourceGroupPage();
		sourceGroupsPage.searchgroupName(sourcegroup);
	}



	public void  addSoucrestoSourceGroup(String sourceGroupName,String []sources,String toastMessage) {

		SourceGroupsPage sourceGroupsPage = goToSourceGroupPage();
		sourceGroupsPage.isGroupExist(sourceGroupName);
		sourceGroupsPage.clickonGroupName(sourceGroupName);
		sourceGroupsPage.addSoucrestoGroup( sourceGroupName,sources);
		sourceGroupsPage.addSelectedSoucresBtn(toastMessage);
	}


	public void removeSourcesFromSoucreGroup(String sourceGroupName,String []sources,String toastMessage) {
		SourceGroupsPage sourceGroupsPage = goToSourceGroupPage();
		sourceGroupsPage.isGroupExist(sourceGroupName);
		sourceGroupsPage.clickonGroupName(sourceGroupName);
		sourceGroupsPage.removeSoucresFromSoucreGroup(sourceGroupName, sources);
		sourceGroupsPage.bulkActionBtn();
		sourceGroupsPage.removesoucrefromGroupBtn();
		sourceGroupsPage.confirmbtn();

	}
	
	
	public  void addAllsoucrestotheSoucregroup(String sourceGroupName,ArrayList<String> sources,String toastMessage) {
		SourceGroupsPage sourceGroupsPage = goToSourceGroupPage();
		sourceGroupsPage.clickonGroupName(sourceGroupName);
		sourceGroupsPage.addAllSoucrestoGroup( sourceGroupName,sources);
		sourceGroupsPage.deleteSoucreGroup(sourceGroupName);
		
	}

}
