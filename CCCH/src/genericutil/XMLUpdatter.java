package genericutil;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.commons.codec.binary.Hex;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPath;


/**
 *
 * @author punsa02
 * Date: 06/Mar/2009
 * This class is used to update a XMLTemplate with the required elements and attributes.
 */

public class XMLUpdatter {
	
	//Path to xml file
	private String xmlPath = null;
	
	//Xml root element
	private Element xmlRoot;
	
	//Xml document
	private Document xmlDoc;	
	
	private static String xmlString = null;
	public XMLUpdatter(String xmlPath) throws JDOMException, IOException
	{
		
		this.xmlPath = xmlPath;		
		xmlDoc = buildXMLdocument(xmlPath);		
		xmlRoot = xmlDoc.getRootElement();	
	}
	
	public String getXMLFileName()
	{
		return xmlPath;
	}
	
	public XMLUpdatter(String xmlPath, boolean validation) throws JDOMException, IOException
	{
		
			this.xmlPath = xmlPath;		
			xmlDoc = buildXMLWithSchemaValidation(xmlPath,validation);		
			xmlRoot = xmlDoc.getRootElement();
	}
	
	public Document getDocument()
	{
		return xmlDoc;
	}
	
	public Element getRootElement()
	{
		return xmlRoot;
	}
	
	/**
	 * This method will build the XML document and returns it
	 * @param xmlName
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public Document buildXMLdocument(String xmlName) throws JDOMException, IOException
	{
		Document doc = null;
		SAXBuilder sb = new SAXBuilder();	
		
		//doc = sb.build(xmlName);
		doc = sb.build(new File(xmlName));	
		return doc;
	}
	
	
	
	public XMLUpdatter() throws JDOMException, IOException
	{
		xmlDoc = buildXMLString(xmlString);		
		xmlRoot = xmlDoc.getRootElement();	
	}
	
	/**
	 * This method will build the XML document using given string and returns it
	 * if you do not have XML file but you have XML string then this method helps to read
	 * @param xmlName
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public Document buildXMLString(String xmlString) throws JDOMException, IOException
	{
		Document doc = null;
		SAXBuilder sb = new SAXBuilder();				
		doc = sb.build(new StringReader(xmlString));
		return doc;
	}
	public Document buildXMLWithSchemaValidation(String xmlPath, boolean validation) throws JDOMException, IOException
	{
		Document doc = null;
		if(validation)
		{
			SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser", true);
			saxBuilder.setFeature("http://apache.org/xml/features/validation/schema", true);		
			doc = saxBuilder.build(new File(xmlPath));
		}
		else
		{
			doc = buildXMLdocument(xmlPath);
		}
		return doc;

	}
	
	public List getChilds(String xPathToEle) throws JDOMException{
		Element element;		
		element = (Element) XPath.selectSingleNode(
					xmlRoot, xPathToEle);
		if(element!=null){
			return element.getChildren();
		}
		return null;
	}
	
	
	public void saveXML(){
		saveDocument(xmlDoc, xmlPath);
	}
	/**
	 * This method will update/add the given attribute value.
	 * @param xPathToAttribEle
	 * @param attribName
	 * @param attribValue
	 * @throws JDOMException
	 */
	public void setAttribute(String xPathToAttribEle, String attribName, String attribValue) throws JDOMException
	{
		//Get the XML element
		Element element;		
		element = (Element) XPath.selectSingleNode(
				xmlRoot, xPathToAttribEle);
		
		//Get the attribute value
		String  attributeVal = element.getAttributeValue(attribName);
		
		//Set the attribute value
		element.setAttribute(attribName,attribValue);
		
		//Save the document
		saveDocument(xmlDoc,xmlPath);		
	}	
	public static void setXMLString(String strXMLData){
		xmlString = strXMLData;
	}
	
	/**
	 * This method will get the given attribute value.
	 * @param xPathToAttribEle
	 * @param attribName
	 * @param attribValue
	 * @throws JDOMException
	 */
	public String getAttributeValue(String xPathToAttribEle, String attribName) throws JDOMException
	{
		String  attributeVal =null;
		//Get the XML element
		Element element;
		element = (Element) XPath.selectSingleNode(
				xmlRoot, xPathToAttribEle);		
		if(element!=null)
			//Get the attribute value
			attributeVal= element.getAttributeValue(attribName);
		//Set the attribute value
		return attributeVal;
	}
	
	public String getAttributeValues(String xPathToAttribEle, String attribName) throws JDOMException
	{
		String  attributeVal =null;
		//Get the XML element
		List<Element> elements;
		elements = (List<Element>) XPath.selectNodes( xmlRoot, xPathToAttribEle);		
		if(elements!=null)
		{
			attributeVal = "";
			for (Element element : elements) {
				attributeVal= attributeVal + ", " + element.getAttributeValue(attribName);
			}
			//Get the attribute value
			return attributeVal.substring(1);
		}
		//Set the attribute value
		return attributeVal;
	}
	public String getElementValues(String xElementPath)
	{
		String strEleValue=null;
		//Get the XML element
		List<Element> elements;
		try{
			elements = (List<Element>) XPath.selectNodes( xmlRoot, xElementPath);
			if(elements!=null)
			{
				strEleValue = new String();
				for (Element element : elements) 
				{
					strEleValue= strEleValue + ", " + element.getText();
				}
					//Get the attribute value
				return strEleValue.substring(1);
			}
		}catch(Exception e)
		{
			return strEleValue;
		}
		return strEleValue;
	}
	/**
	 * This method will remove the given attribute
	 * @param xPathToEle
	 * @param attribName
	 * @throws JDOMException
	 */
	public void removeAttribute(String xPathToEle, String attribName) throws JDOMException
	{
		
		//Get the XML element
		Element element;		
		element = (Element) XPath.selectSingleNode(
				xmlRoot, xPathToEle);
		//Remove the attribute
		element.removeAttribute(attribName);
		//Save the document
		saveDocument(xmlDoc,xmlPath);
	}
	
	/**
	 * This method will set the element text
	 * @param xPathToEle
	 * @param text
	 * @throws JDOMException
	 */
	public void setElementText(String xPathToEle, String text ) throws JDOMException
	{
		//Get the XML element

		
		Element element;		
		element = (Element) XPath.selectSingleNode(
					xmlRoot, xPathToEle);
		
		 ErrorHandler.getErrorHandler().printInfoMessageInDebugFile("going to update value of element having xpath ele " + xPathToEle  + " with text: " + text);
		if(element == null)
			throw new NullPointerException("Could not find element for the given Xpath: " + xPathToEle);
		//set the element text
		element.setText(text);
		
		 ErrorHandler.getErrorHandler().printInfoMessageInDebugFile("value set to " + xPathToEle  + " is: " + element.getText());
		saveDocument(xmlDoc,xmlPath);

	}
	
	/**
	 * This method will get the element text 
	 * @param xPathToEle
	 * @param text
	 * @throws JDOMException
	 */
	public String getElementText(String xPathToEle) throws JDOMException
	{
		//Get the XML element
		Element element;		
		element = (Element) XPath.selectSingleNode(
					xmlRoot, xPathToEle);
		if(element!=null)
			//set the element text
			return element.getText();
		return null;
	}
	
	
	/**
	 * This method will add a new element to the parent element
	 * @param xPathToParentEle
	 * @param newElement
	 * @throws JDOMException
	 */
	public void addElement(String xPathToParentEle, String newElement) throws JDOMException
	{
		//Get the XML element
		Element element;		
		element = (Element) XPath.selectSingleNode(
					xmlRoot, xPathToParentEle);
//		element.addContent(newElement);
		element.getChildren().add(new Element(newElement));
		
		//Save the document
		saveDocument(xmlDoc,xmlPath);
	}
	
	public void addElement(String xPathToParentEle, String newElementName, String newElementValue) throws JDOMException
	{
		//Get the XML element		
//		xPathToParentEle = "/Testplan/Testcase";
		Element element = (Element) XPath.selectSingleNode(
					xmlRoot, xPathToParentEle);
		
		Element newChild = new Element(newElementName);
		newChild.setName(newElementName);
		if(newElementValue!=null && newElementValue!="")
		{
			newChild.setText(newElementValue);
		}
		
		element.addContent(newChild);
//		element.addContent(newElement);
		//Save the document
		saveDocument(xmlDoc,xmlPath);
	}

	/**
	 * Added by kolpr02. This method will add tree of elements to the XML file.
	 * @param xPathToParentEle
	 * @param newElement
	 * @throws JDOMException
	 * @throws IOException 
	 */
	public void cloneElement(String xPathToParentEle, String eleName) throws JDOMException, IOException
	{
		//Get the XML element of given XPath.
		Element element,element1;
		element = (Element) XPath.selectSingleNode(
					xmlRoot, xPathToParentEle);
		
		//Clone the content of given element.
		element1 = (Element) XPath.selectSingleNode(
				xmlRoot, xPathToParentEle + "/" + eleName);
		List eleList = element1.cloneContent();
		
		//Add the new element and add the clone content to that.
		Element newEle = new Element(eleName);
		element.addContent(newEle);
		newEle.addContent(eleList);
		
		//Save the document
		saveDocument(xmlDoc,xmlPath);
		
	}
	
	public String getChildEleByTagName(String elementXPath, String tagName) throws JDOMException
	{
		//Get the XML element
		Element element;	
		element = (Element) XPath.selectSingleNode(
					xmlRoot, elementXPath);
		Element childEle =  element.getChild(tagName);
		if(childEle !=null)
			return childEle.getTextTrim();
		return null;
	}
	
	
	/**
	 * This method will remove the given element from the parent element
	 * @param xPathToParentEle
	 * @param eleToremove
	 * @throws JDOMException
	 */
	public void removeElement(String xPathToParentEle, String eleToremove) throws JDOMException
	{
		//Get the XML element
		Element element;		
		element = (Element) XPath.selectSingleNode(
					xmlRoot, xPathToParentEle);
		element.removeChild(eleToremove);
		//element.detach();
		//Save the document
		saveDocument(xmlDoc,xmlPath);
	}
	
	
	/**
	 * This method will remove the given element from the parent element
	 * @param xPathToParentEle
	 * @param eleToremove
	 * @throws JDOMException
	 */
	public void removeElementFromXMLObject(String xPathToParentEle, String eleToremove) throws JDOMException
	{
		//Get the XML element
		Element element;		
		element = (Element) XPath.selectSingleNode(
					xmlRoot, xPathToParentEle);
		element.removeChild(eleToremove);
		//element.detach();
		//Save the document
		//saveDocument(xmlDoc,xmlPath);
	}
	
	public boolean removeElementAndSaveXML(Element parentElement, Element elementToRemove) throws JDOMException
	{
		//Get the XML element
		boolean status = parentElement.removeContent(elementToRemove);
		
		//element.detach();
		//Save the document
		saveDocument(xmlDoc,xmlPath);
		return status;
	}
	
	/**
	 * This method is used to get all the values of the an attribute of multiple elements.
	 * @return
	 * @throws JDOMException 
	 */
	public Vector getElementAttributeValues(String eleXPath, String eleName) throws JDOMException
	{
		Vector eleValues = new Vector();
		
		//Get the elements
		Iterator elesIter = XPath.selectNodes(
				xmlRoot, eleXPath).iterator();
		
		//Loop through each element
		while(elesIter.hasNext())
		{
			//element
			Element ele = (Element)elesIter.next();
			String eleVal = ele.getAttributeValue(eleName);
			if(eleVal !=null)
				//Add the element value to eleValues vector
				eleValues.add(eleVal.trim());
		}		
		return eleValues;		
	}
	
/**
	 * This method will return the child element text value.
	 * @param eleXPath
	 * @param eleName
	 * @return
	 * @throws JDOMException
	 */
	public Vector<Element> getElements(String eleXPath) throws JDOMException
	{
		Vector<Element> eleValues = new Vector<Element>();
		
		//Get the elements
		Iterator elesIter = XPath.selectNodes(
				xmlRoot, eleXPath).iterator();
		
		//Loop through each element
		while(elesIter.hasNext())
		{
			//element
			Element ele = (Element)elesIter.next();
			
			//Add the element to eleValues vector
			eleValues.add(ele);
		}		
		return eleValues;
	}
	
	/**
	 * getChildElements is to get a vector of all the child elements. If there is no element which the 
	 * specified xpath exist then it returns empty vector 
	 * @param xPathToEle
	 * @return Vector<Element>
	 * @throws JDOMException
	 */
	public Vector<Element> getChildElements(String xPathToEle) throws JDOMException
	{
		Vector<Element> childElementsVector = new Vector<Element>();
		//Get the XML element
		Element element;		
		element = (Element) XPath.selectSingleNode(
					xmlRoot, xPathToEle);
		if(element!=null){
			//Get all the children
			Iterator childElementsIterator =  element.getChildren().iterator();
			while(childElementsIterator.hasNext())
			{
				//element
				Element childElement = (Element)childElementsIterator.next();
				
				//Add the element to eleValues vector
				childElementsVector.add(childElement);
			}
		}
		return childElementsVector;
	}
	
	/**
	 * getChildElementsNames is to get a vector of all the child elements names. If there is no element which the 
	 * specified xpath exist then it returns empty vector 
	 * @param xPathToEle
	 * @return Vector<Element>
	 * @throws JDOMException
	 */
	public Vector getChildElementsNames(String xPathToEle) throws JDOMException
	{
		Vector childElementsNamesVector = new Vector();
		//Get the XML element
		Element element;		
		element = (Element) XPath.selectSingleNode(
					xmlRoot, xPathToEle);
		if(element!=null){
			//Get all the children
			Iterator childElementsIterator =  element.getChildren().iterator();
			while(childElementsIterator.hasNext())
			{
				//element
				Element childElement = (Element)childElementsIterator.next();
				
				//Add the element to eleValues vector
				childElementsNamesVector.add(childElement.getName());
			}
		}
		System.out.println("childElementsNamesVector:" + childElementsNamesVector.toString());
		return childElementsNamesVector;
	}
	
	/**
	 * isChildElementExists is to check whether the given element is the child of the given xpath or not
	 * @param eleXPath
	 * @param childElementName
	 * @return true/false
	 * @throws JDOMException
	 */
	public boolean isChildElementExists(String eleXPath, String childElementName) throws JDOMException
	{
		Vector childElementsNamesVector = new Vector();
		//Get the XML element
		Element element;		
		element = (Element) XPath.selectSingleNode(
					xmlRoot, eleXPath);
		if(element!=null){
			//Get all the children
			Iterator childElementsIterator =  element.getChildren().iterator();
			while(childElementsIterator.hasNext())
			{
				//element
				Element childElement = (Element)childElementsIterator.next();
				if(childElement.getName().equalsIgnoreCase(childElementName.trim()))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * This method will return the child element text value.
	 * @param eleXPath
	 * @param eleName
	 * @return
	 * @throws JDOMException
	 */
	public Vector getChildElementValues(String eleXPath, String eleName) throws JDOMException
	{
		String finalXPath = eleXPath + "/" + eleName;
		Vector<String> eleValues = new Vector<String>();
		
		//Get the elements
		Iterator elesIter = XPath.selectNodes(
				xmlRoot, finalXPath).iterator();
		
		//Loop through each element
		while(elesIter.hasNext())
		{
			System.out.println("one");
			//element
			Element ele = (Element)elesIter.next();
			
			String eleVal = ele.getText();
			System.out.println("eleVal:" + eleVal);
			if(eleVal !=null)
				//Add the element value to eleValues vector
				eleValues.add(eleVal.trim());
		}		
		return eleValues;		
		
/*Vector<Element> eleValues = new Vector<Element>();
		
		//Get the elements
		Iterator elesIter = XPath.selectNodes(
				xmlRoot, eleXPath).iterator();
		
		//Loop through each element
		while(elesIter.hasNext())
		{
			//element
			Element ele = (Element)elesIter.next();
			
			//Add the element to eleValues vector
			eleValues.add(ele);
		}		
		return eleValues;*/
	}
	
	
	
	
	/**
	 * This method is to save the updated document.
	 * @param doc
	 */
	public void saveDocument(Document doc, String fileName){
		//write the XML document to XML file
		XMLOutputter outputter = new XMLOutputter();
		Vector<Element> eles = null;
		
		outputter.setFormat(Format.getPrettyFormat());
		
		FileWriter writer;
		try {
//			eles =  getElements("/JobScriptXMLDescription/tagASQJDATATRANSFER/pSourceList/tagASNODE[1]/pvDeviceList/tagASDDISK[1]/pASDiskItemList/tagASDISKITEM");
//			System.out.println(eles.size());
			writer = new FileWriter(fileName);
			
			outputter.output(doc, writer);
			
			writer.close();
			
			
			
//			eles =  getElements("/JobScriptXMLDescription/tagASQJDATATRANSFER/pSourceList/tagASNODE[1]/pvDeviceList/tagASDDISK[1]/pASDiskItemList/tagASDISKITEM");
//			System.out.println(eles.size());
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void printUsage()
	{
		
	}
	
	public static void main3(String args[])
	{
		XMLUpdatter xml = null;
		try {
			xml = new XMLUpdatter("C:\\Automation\\D2DWorkSpace\\Framework\\sample_FiledOrNonAttempted.xml");
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Vector<Element> ele = xml.getElements("/Testplan");
			System.out.println("size:" + ele.size());
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main2(String args[])
	{
		XMLUpdatter xml = null;
		try {
			xml = new XMLUpdatter("E:\\D2D_WorkSpace\\CatlogValidationPrj\\sql_testcases.xml");
			System.out.println(xml.getChildElementValues("/TestPlan/TestCase[@Name='Perform Incremental backup of SQL(After dropping one database from SQL instance)']/Backup/NoOfIterations", "OneTime"));
		} catch (JDOMException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static String stringToHex(String base)
    {
     StringBuffer buffer = new StringBuffer();
     int intValue;
     for(int x = 0; x < base.length(); x++)
         {
         int cursor = 0;
         intValue = base.charAt(x);
         String binaryChar = new String(Integer.toBinaryString(base.charAt(x)));
         for(int i = 0; i < binaryChar.length(); i++)
             {
             if(binaryChar.charAt(i) == '1')
                 {
                 cursor += 1;
             }
         }
         if((cursor % 2) > 0)
             {
             intValue += 128;
         }
         buffer.append(Integer.toHexString(intValue) + "00");
     }
     return buffer.toString().toUpperCase();
}
	
	public static String toHex(String data)
	{
		String hexString= "";
		byte[] b1;
		try {
			b1 = data.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		for (byte b : b1) {
			byte[] b2 = new byte[1];
			b2[0] = b;
			String data1 = new String(Hex.encodeHex(b2));
			hexString += data1 + "00";
		}
		return hexString.toUpperCase();
	}
	public static void main(String args[])
	{

		XMLUpdatter xmlUpdater = null;

		try {
	       /*xmlUpdater = new XMLUpdatter("C:\\javaexamples\\Tungsten_Automation\\Automationxml\\backup.xml");
	       xmlUpdater.setElementText("/JobScriptXMLDescription/tagASQJDATATRANSFER/pszComments", "MyJob");
	       xmlUpdater = new XMLUpdatter("C:\\javaexamples\\Tungsten_Automation\\Automationxml\\compreesagent.xml");
	       xmlUpdater.setElementText("/JobScriptXMLDescription/tagASQJDATATRANSFER/pszComments", "MyJob");
	       xmlUpdater = new XMLUpdatter("C:\\javaexamples\\Tungsten_Automation\\Automationxml\\encrpt.xml");
	       xmlUpdater.setElementText("/JobScriptXMLDescription/tagASQJDATATRANSFER/pszComments", "MyJob");
	       xmlUpdater = new XMLUpdatter("C:\\javaexamples\\Tungsten_Automation\\Automationxml\\encrptagent.xml");
	       xmlUpdater.setElementText("/JobScriptXMLDescription/tagASQJDATATRANSFER/pszComments", "MyJob");
	       
	       xmlUpdater = new XMLUpdatter("C:\\javaexamples\\Tungsten_Automation\\Automationxml\\encrptatservermigcompressagent.xml");
	       xmlUpdater.setElementText("/JobScriptXMLDescription/tagASQJDATATRANSFER/pszComments", "MyJob");
	       xmlUpdater = new XMLUpdatter("C:\\javaexamples\\Tungsten_Automation\\Automationxml\\encrptsercompressagent.xml");
	       xmlUpdater.setElementText("/JobScriptXMLDescription/tagASQJDATATRANSFER/pszComments", "MyJob");
	       xmlUpdater = new XMLUpdatter("C:\\javaexamples\\Tungsten_Automation\\Automationxml\\encrptserver.xml");
	       xmlUpdater.setElementText("/JobScriptXMLDescription/tagASQJDATATRANSFER/pszComments", "MyJob");
	       /*xmlUpdater = new XMLUpdatter("C:\\javaexamples\\Tungsten_Automation\\Automationxml\\encryptcompressserver.xml");
	       xmlUpdater.setElementText("/JobScriptXMLDescription/tagASQJDATATRANSFER/pszComments", "MyJob");
	       xmlUpdater = new XMLUpdatter("C:\\javaexamples\\Tungsten_Automation\\Automationxml\\encryptcompressservermigrat.xml");
	       xmlUpdater.setElementText("/JobScriptXMLDescription/tagASQJDATATRANSFER/pszComments", "MyJob");
	       xmlUpdater = new XMLUpdatter("C:\\javaexamples\\Tungsten_Automation\\Automationxml\\encryptservermigrat.xml");
	       xmlUpdater.setElementText("/JobScriptXMLDescription/tagASQJDATATRANSFER/pszComments", "MyJob");
	       xmlUpdater = new XMLUpdatter("C:\\javaexamples\\Tungsten_Automation\\Automationxml\\encycompragent.xml");
	       xmlUpdater.setElementText("/JobScriptXMLDescription/tagASQJDATATRANSFER/pszComments", "MyJob");
	       xmlUpdater = new XMLUpdatter("C:\\javaexamples\\Tungsten_Automation\\Automationxml\\staging.xml");
	       xmlUpdater.setElementText("/JobScriptXMLDescription/tagASQJDATATRANSFER/pszComments", "MyJob");*/
			
		   xmlUpdater = new XMLUpdatter("C:\\javaexamples\\Tungsten_Automation\\ASXjobs\\scan.xml");
		   xmlUpdater.setElementText("/JobScriptXMLDescription/tagASQJDATATRANSFER/pszComments", "MyJob");
		   xmlUpdater = new XMLUpdatter("C:\\javaexamples\\Tungsten_Automation\\ASXjobs\\compare.xml");
		   xmlUpdater.setElementText("/JobScriptXMLDescription/tagASQJDATATRANSFER/pszComments", "MyJob");
		   /*xmlUpdater = new XMLUpdatter("C:\\javaexamples\\Tungsten_Automation\\Automationxml\\new\\encryptmigcomprserv.xml");
		   xmlUpdater.setElementText("/JobScriptXMLDescription/tagASQJDATATRANSFER/pszComments", "MyJob");
		   xmlUpdater = new XMLUpdatter("C:\\javaexamples\\Tungsten_Automation\\Automationxml\\new\\encryptmigcompragen.xml");
		   xmlUpdater.setElementText("/JobScriptXMLDescription/tagASQJDATATRANSFER/pszComments", "MyJob");*/
	       System.out.println("Done");
		} catch (JDOMException e) {
	       // TODO Auto-generated catch block
	       e.printStackTrace();
		} catch (IOException e) {
	       // TODO Auto-generated catch block
	       e.printStackTrace();
		}

	}
	public static void main1(String args[])
	{
		try		
		{		
			if((args.length < 4) || (args == null))
				System.out.println("java -updateEleAttribute <xmlName> <xPathToAttribEle> <attribName> <attribValue>");
			else if(args[0].equalsIgnoreCase("-updateEleAttribute"))
			{
				//args[1] : xmlName
				//args[2] : XPath
				//args[3] : attributeName
				//args[4] : attributeValue
				XMLUpdatter updater = new XMLUpdatter(args[1]);
				updater.setAttribute(args[2],args[3],args[4]);
				System.out.println("1");
			}
			else if(args[0].equalsIgnoreCase("-updateEleText"))
			{
				//args[1] : xmlName
				//args[2] : XPath
				//args[3] : Element value				
				XMLUpdatter updater = new XMLUpdatter(args[1]);
				updater.setElementText(args[2],args[3]);
				System.out.println("1");
			}
				
		}
		catch(Exception e)
		{
			System.out.println("0");
			e.printStackTrace();
		}
	}

}
