package ui.spog.pages.configure;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import io.restassured.response.Response;
import ui.base.common.BasePage;
import ui.base.common.SPOGMenuTreePath;
import ui.base.common.UiElementNotFoundException;
import ui.base.elements.Button;
import ui.base.factory.BrowserFactory;
import ui.base.factory.ElementFactory;



public class UserAccountsPage extends BasePage {


	/**
	 * get rows of user table
	 * @author shuo.zhang
	 * @return
	 */
	public List<WebElement>  getUserList(){
		
		return ElementFactory.getElements(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERTABLE_ROWS);
	}
	
	/**
	 * check user list
	 * @author shuo.zhang
	 * @param response
	 */
	public void checkUserList(Response response){
		
		ArrayList<HashMap<String, Object>>  userHashMap  = new ArrayList<HashMap<String, Object>> ();
		if(response!=null){
			 userHashMap     =response.then().extract().path("data");	
		}
		waitForSeconds(2);	
		List<WebElement> userTableHeaders = getUserTableHeaders();
		HashMap<String, Integer> userTableHeaderOrder = getTableHeaderOrder(userTableHeaders);
		List<WebElement> rows = getUserList();
		assertEquals(rows.size(), userHashMap.size());
		for(int i=0; i< rows.size(); i++){

			//get UI item
			WebElement eachRow = rows.get(i);
			String xpath = "." + ElementFactory.getElementAttrFromXML(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERTABLE_COLUMNS).get(0).getValue();
			List<WebElement> columns = eachRow.findElements(By.xpath(xpath));
		//	List<WebElement> columns = eachRow.findElements(By.xpath("./div/div"));
			//get api user item
			HashMap<String, Object> apiUser = userHashMap.get(i);
			System.out.println(apiUser.get("email"));
			Set<String> keySet = userTableHeaderOrder.keySet();
			Iterator<String> keyIter = keySet.iterator();
			while(keyIter.hasNext()){
				String headerName = keyIter.next();
				int order = userTableHeaderOrder.get(headerName);
				if(headerName.equalsIgnoreCase("role")){
					if(apiUser.get("role_id").toString().equalsIgnoreCase("msp_admin")){
						assertEquals(columns.get(order).getText(), "MSP Admin");
					}else if(apiUser.get("role_id").toString().equalsIgnoreCase("msp_account_admin")){
						assertEquals(columns.get(order).getText(), "MSP Account Admin");
					}else{
						assertEquals(columns.get(order).getText(), "Admin");
					}
				}else if(headerName.equalsIgnoreCase("Name")){		
					System.out.println(columns.get(order).getText());
					assertEquals(columns.get(order).getText(), apiUser.get("first_name")+ " " + apiUser.get("last_name") );
				}else if(headerName.equalsIgnoreCase("email")){
					assertEquals(columns.get(order).getText(), apiUser.get("email") );					
				}else if(headerName.equalsIgnoreCase("added on")){
					String str_create_ts = apiUser.get("create_ts").toString();
					long create_ts = Long.valueOf(str_create_ts)*1000;
					Timestamp ts=new Timestamp(create_ts);
					
					String patten = "MM/dd/yyyy h:mm aaa";	
					SimpleDateFormat dateFormat = new SimpleDateFormat(patten);
					
					assertEquals(columns.get(order).getText(), dateFormat.format(ts) );					
				}else if(headerName.equalsIgnoreCase("last logged in")){			
					String str_last_login_ts = apiUser.get("last_login_ts").toString();
					long last_login_ts = Long.valueOf(str_last_login_ts)*1000;
					 if(last_login_ts==0){
							assertEquals(columns.get(order).getText(), "-");	
					 }else{
						 //check last login time
					 }
				}else if(headerName.equalsIgnoreCase("is blocked")){			
					assertEquals(columns.get(order).getText().toLowerCase(), apiUser.get("blocked").toString().toLowerCase() );		
				}else if(headerName.equalsIgnoreCase("action")){
				
					ArrayList<String> actionList = (ArrayList<String>)apiUser.get("allowed_actions");
					if(actionList.size()!=0){
						columns.get(order).findElement(By.xpath(".//button")).click();
					    
						//columns.get(order).click();
						List<WebElement> elements = columns.get(order).findElements(By.xpath(".//ul[@class='dropdown-menu']//span"));
					
						
					
						for(int j=0; j<elements.size();j++){
							String value="";
							if(actionList.get(j).toLowerCase().equalsIgnoreCase("resetpassword"))
								value="reset password";
							else if (actionList.get(j).toLowerCase().equalsIgnoreCase("assignaccount"))
								value="assign account";
							else
								value=actionList.get(j).toLowerCase();
							assertEquals(elements.get(j).getText().toString().toLowerCase(), value );	
						}
						columns.get(order).findElement(By.xpath(".//button")).click();
					}
			
				
				}else {
					assertEquals(columns.get(order).getText().toLowerCase(), apiUser.get(headerName.toLowerCase()) );					
				}
			}
		}
	}
	
	/*
	 * Get user table headers
	 * @author shuo.zhang
	 */
	public List<WebElement>  getUserTableHeaders(){
		 
		List<WebElement> headers = ElementFactory.getElements(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERTABLE_HEADERS);
		return headers;
	}
	
	/**
	 * @author shuo.zhang
	 * @param headerTable
	 * @return
	 */
	public HashMap<String, Integer> getTableHeaderOrder(List<WebElement> headerTable){
		
		HashMap<String, Integer> headerOrderMap = new HashMap<String, Integer> ();
		for(int i=0; i<headerTable.size();i++){
			WebElement eachHeader = headerTable.get(i);
			String text = eachHeader.getText();
			if((text!=null) && !text.equalsIgnoreCase("")){
				headerOrderMap.put(text, i);
			}
		}
		return headerOrderMap;
	}
	
	/**
	 * @author shuo.zhang
	 * @param name
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param role
	 */
	public void checkUserDetails(String name, String firstName, String lastName, String email, String role){
		
		try{
			WebElement tableBody = ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERTABLE_BODY);
			WebElement userElement = tableBody.findElement(By.xpath(".//span[text()='" + name+ "']"));
			userElement.click();
			getErrorHandler().printInfoMessageInDebugFile("check first name");
			WebElement firstNameEle = ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_FIRSTNAME);
			assertEquals(firstNameEle.getAttribute("value"), firstName);
			
			getErrorHandler().printInfoMessageInDebugFile("check last name");
			WebElement lastNameEle = ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_LASTNAME);
			assertEquals(lastNameEle.getAttribute("value"), lastName);
			
			getErrorHandler().printInfoMessageInDebugFile("check email");
			WebElement emailEle = ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_EMAIL);
			assertEquals(emailEle.getText().toLowerCase(), email.toLowerCase());
			
			if(role!=null){
				getErrorHandler().printInfoMessageInDebugFile("check role");
				WebElement roleEle = ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_ROLE);
				assertEquals(roleEle.getText(), role);
			}
		

		}catch(Exception e){
			e.printStackTrace();
			Assert.fail("exception", e);
			
			
		}finally{
			if(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_CANCEL)){
				Button cancelBtn = new Button(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_CANCEL);
				cancelBtn.click();
			}
			
		}

	}
	
	/**
	 * @author shuo.zhang
	 * @param name
	 * @param firstName
	 * @param lastName
	 * @param role
	 */
	public void editUserDetails(String name, String firstName, String lastName){
		
		try{
			WebElement tableBody = ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERTABLE_BODY);
			WebElement userElement = tableBody.findElement(By.xpath(".//span[text()='" + name+ "']"));
			userElement.click();
			
			if(firstName!=null){
				getErrorHandler().printInfoMessageInDebugFile("set first name");
				WebElement firstNameEle = ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_FIRSTNAME);
				firstNameEle.clear();
				firstNameEle.sendKeys(firstName);
			}

			if(lastName!=null){
				getErrorHandler().printInfoMessageInDebugFile("set last name");
				WebElement lastNameEle = ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_LASTNAME);
				lastNameEle.clear();
				lastNameEle.sendKeys(lastName);
			}

		
			getErrorHandler().printInfoMessageInDebugFile("click save");
			Button saveBtn = new Button(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_SAVE);
			saveBtn.click();
			waitForSeconds(5);

		}catch(Exception e){
			Assert.fail("exception", e);
			getErrorHandler().printErrorMessageInDebugFile(e.getStackTrace().toString());
			if(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_CANCEL)){
				Button cancelBtn = new Button(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_CANCEL);
				cancelBtn.click();
			}
			
		}
		
	}
	
	public void editUserDetailsErrorHandling(String name, String firstName, String lastName){
		
		try{
			WebElement tableBody = ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERTABLE_BODY);
			WebElement userElement = tableBody.findElement(By.xpath(".//span[text()='" + name+ "']"));
			userElement.click();
			
			WebElement firstNameEle = ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_FIRSTNAME);
			WebElement lastNameEle = ElementFactory.getElement(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_LASTNAME);
			if(firstName!=null){
				getErrorHandler().printInfoMessageInDebugFile("set first name");			
				firstNameEle.clear();
				firstNameEle.sendKeys(firstName);
				lastNameEle.click();
			}

			if(lastName!=null){
				getErrorHandler().printInfoMessageInDebugFile("set last name");			
				lastNameEle.clear();
				lastNameEle.sendKeys(lastName);
				firstNameEle.click();
			}

			getErrorHandler().printDebugMessageInDebugFile("check save button whether is disabled");
			Button saveBtn = new Button(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_SAVE);		
	
			boolean saveFlag=true;
			if((firstName!=null) && firstName.equalsIgnoreCase("")){
				saveFlag=false;
				try{
					WebElement requiredEle = firstNameEle.findElement(By.xpath(".//..//..//span[text()='Required']"));
					Assert.assertTrue(true, "Find require text for first name");
					assertFalse(saveBtn.isEnabled());
				}catch (UiElementNotFoundException e) {
					getErrorHandler().printErrorMessageInDebugFile(e.getMessage());
					Assert.assertTrue(false, "No require text for first name");
				} 		
			}
			
			
			if((lastName!=null) && lastName.equalsIgnoreCase("")){
				saveFlag=false;
				try{
					WebElement requiredEle = lastNameEle.findElement(By.xpath(".//..//..//span[text()='Required']"));
					Assert.assertTrue(true, "Find require text for last name");
					assertFalse(saveBtn.isEnabled());
				}catch (UiElementNotFoundException e) {
					getErrorHandler().printErrorMessageInDebugFile(e.getMessage());
					Assert.assertTrue(false, "No require text for last name");
				} 		
			}
			
			if(saveFlag){
				saveBtn.click();
				this.waitUntilElementDisappear(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_SAVE);
				if((firstName!=null) && firstName.length()>128){
					ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_FIRSTNAMELENGTHERROR,10);
				}
				if((lastName!=null) && lastName.length()>128){
					ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_LASTNAMELENGTHERROR,10);
				}
			}

		}catch(Exception e){
			Assert.fail("exception", e);
			
		}finally{
			if(ElementFactory.checkElementExists(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_CANCEL)){
				Button cancelBtn = new Button(SPOGMenuTreePath.SPOG_CONFIGURE_USERACCOUNTS_USERACCOUNTDETAILS_CANCEL);
				cancelBtn.click();
			}
			
		}
		
	}
	
	
	
	
}
