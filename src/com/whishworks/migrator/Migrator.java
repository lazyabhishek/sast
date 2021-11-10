package com.whishworks.migrator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.whishworks.migrator.util.CommonUtils;
import com.whishworks.migrator.util.ConnectorDeatils;
import com.whishworks.migrator.util.GlobalVariables;
import com.whishworks.migrator.util.NameSpaceMapper;
import com.whishworks.migrator.util.PercentageComputer;
import com.whishworks.migrator.util.UnhandledScenariosHelper;
import com.whishworks.migrator.util.Utils;
import com.whishworks.migrator.util.XSLTMapper;
import com.whishworks.report.generator.Issue;
import com.whishworks.report.generator.Report;


public class Migrator {

	static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	static DocumentBuilder dBuilder;
	static Document finalDoc;
	static Document doc;
	static boolean isBatchScope = false;
	static boolean isBatchInputScope = false;
	static boolean removeXMLNSAttrib = false;
	static Element batchJobElement = null;
	static boolean isEnricherScope =false;
	static String enricherTarget = "";
	static String configElementName = "";
	public static List<Issue> flowIssueList;
	static String srcFile = "";
	static String m4DocNamespaces;

	/* this function is invoked to transform xml */
	public static void transformXML(String sourceFile, String targetFile, boolean isGlobalConfFile) throws Exception {
		try {
			srcFile = sourceFile;
			MigratorUtils mUtils = new MigratorUtils();
			flowIssueList = new ArrayList<>();
			dbFactory.setNamespaceAware(true);
			dBuilder = dbFactory.newDocumentBuilder();
			Node muleTag = null;
			if (isGlobalConfFile) {
				doc = GlobalConfig.globalConfigDoc;
			} else {
				File fXmlFile = new File(sourceFile);
				doc = dBuilder.parse(fXmlFile);
			}
			
			//finalDoc = dBuilder.newDocument();
			if(null != doc)
				muleTag = doc.getElementsByTagName("mule").item(0);
			if (null != muleTag) {
				Element element = (Element) muleTag;
				//element.setAttribute("xmlns:ee", "http://www.mulesoft.org/schema/mule/ee/core");
				element.setAttribute("xmlns:tls", "http://www.mulesoft.org/schema/mule/tls");
				//Node copiedMuleNode = finalDoc.importNode(element, false);
				//finalDoc.appendChild(copiedMuleNode);
				StringBuffer muleTagAttribsAsString = new StringBuffer();

				for (int i = 0; i < muleTag.getAttributes().getLength(); i++) {
					if(!muleTag.getAttributes().item(i).getNodeName().equals("version"))
						muleTagAttribsAsString.append(muleTag.getAttributes().item(i) + "  ");

				}

				InputXMLConstructor.muleTagNameSpaces = muleTagAttribsAsString;
				XSLConstructor.muleTagNameSpaces = muleTagAttribsAsString;
				m4DocNamespaces = muleTagAttribsAsString.toString();
				//function to replace mule3 with mule 4
				updateNamespace(); 
				finalDoc = MigratorUtils.constructMule4Document(m4DocNamespaces);
				InputXML.INPUT_XML_BUFFER.append(mUtils.convertXMLDocToStringBuffer(doc));
				NodeList nodeList = doc.getElementsByTagName("*");
				
				//Move Spring to spring-beans.xml
				if(muleTagAttribsAsString.toString().contains("xmlns:spring") && muleTagAttribsAsString.toString().contains("http://www.springframework.org/"))
					SpringMigrator.moveSpringStuffToBeansXML(doc, muleTagAttribsAsString.toString(), dBuilder);

				for (int i = 1; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {

						GlobalVariables.totalElementCount++;
						String nodeName = node.getNodeName();
						StringBuffer nodeString = new StringBuffer();
						if(XSLTMapper.scopesList.contains(nodeName))
							nodeString = Utils.elementToString(node.cloneNode(false));
						else
							nodeString = Utils.elementToString(node);
						if(nodeName.equals("flow") && node.hasChildNodes() && nodeString.toString().contains("<composite-source")) {
							System.out.println("composite source is the source for the flow");
							node = MigratorUtils.getCompositeSourceNodeToMigrate(node);
							nodeName = node.getNodeName();
							i += transformSubscopeElements(node, finalDoc.getElementsByTagName("mule").item(0), finalDoc);
							int countOfCompositeSources = Integer.parseInt(node.getAttributes().getNamedItem("count").getNodeValue());
							i = i-((countOfCompositeSources*2)+3);
						}
						
						Element transformedElement = null;
						boolean includeNameSpace = true;
						boolean isWithConfig = XSLTMapper.transformWithConfig.contains(nodeName);
						if (XSLTMapper.skipGlobalConfigElements.contains(nodeName)) {
							System.out.println("found a global config element so skipping it as of now");
							if(!XSLTMapper.scopesList.contains(nodeName)) {
								i += Utils.getRecursiveChildLen(node);
							}
							continue;
						}
						if (XSLTMapper.skipElements.contains(nodeName) || SpringMigrator.isSpringBeanXMLElement((Element)node)) {
							if (XSLTMapper.scopesList.contains(nodeName)) {
								System.out.println(
										nodeName + " skip element has got subscope, but couldn't transform it !");
							}
							continue;

						}
						
						boolean copyChildern = true;
						if (null != XSLTMapper.transformerOf.get(nodeName)
								|| XSLTMapper.copyAsIsList.contains(nodeName)) {
							if (!nodeName.contains(":")) {
								includeNameSpace = false;
							}

							if (XSLTMapper.copyAsIsList.contains(nodeName)) {
								if(XSLTMapper.scopesList.contains(nodeName)) {
									transformedElement = (Element) node.cloneNode(false);
								}else {
									transformedElement = (Element) node;
								}
								copyChildern = false;
							}else if(nodeName.equals("invoke")){
								transformedElement = CommonUtils.migrateInvokeComponenet((Element)node, doc, finalDoc);
							} else if(nodeName.equals("expression-component")){
								CommonUtils.transformExpressionComponent(node, finalDoc.getElementsByTagName("mule").item(0), finalDoc);
								i++;
								continue;
							}else {
								if (!isWithConfig) {
									transformedElement = getTransformedTag(node, nodeString, includeNameSpace, false);
								} else {
									transformedElement = getTransformedTag(node,
											getElementWithConfig(nodeName, nodeString, (Element) node),
											includeNameSpace, true);
								}
								if (node.hasChildNodes() && !XSLTMapper.scopesList.contains(nodeName)) {
									i += Utils.getRecursiveChildLen(node);
								}
							}
						} else {
							System.out.println("no xslt mapping found for " + nodeName);
							System.out.println("uanble to transform :" + nodeString);
							GlobalVariables.issueCount++;
							if(UnhandledScenariosHelper.elements.contains(node.getNodeName())) {
								if(XSLTMapper.scopesList.contains(node.getNodeName()))
									UnhandledScenariosHelper.addUnhandledScenariosToReport((Element) node.cloneNode(false),flowIssueList, nodeString);
								else
									UnhandledScenariosHelper.addUnhandledScenariosToReport((Element) node.cloneNode(true),flowIssueList, nodeString);
							}else {
								flowIssueList.add(new Issue("uanble to transform :"+node.getNodeName(), ""+node.getUserData("lineNumber"), nodeString));
							}
							System.out.println("******copying it as it is****** please make sure migrate it manually******");
							transformedElement = (Element) node;
							if (node.hasChildNodes() && !XSLTMapper.scopesList.contains(nodeName)) {
								i += Utils.getRecursiveChildLen(node);
							}
							PercentageComputer.computeMigrationVariables(transformedElement, nodeString.toString(), false);
						}
						if (!includeNameSpace) {
							transformedElement.removeAttributeNS("", "xmlns");
							removeXMLNSAttrib = true;
						}
						if (null != transformedElement) {
							
							Node transformedNode = finalDoc.importNode(transformedElement, copyChildern);
							String transformedElementAsString = Utils.elementToString(transformedNode).toString();
								if(transformedElementAsString.contains("#[") && transformedElementAsString.contains("]")) {
									if(transformedNode.hasChildNodes() && !XSLTMapper.scopesList.contains(nodeName)) {
										
										transformedNode = recursivelyMigrateMel((Element) transformedNode, finalDoc);
									}else {
										System.out.println("Element contains MEL expression so migrating");
										transformedNode = migrateElementWithMEL((Element) transformedNode, finalDoc);
									}
								}
							if(transformedElement.getNodeName().equals("scripting:component")) {
								String script = transformedElement.getNodeValue();
								if(!script.equals(""))
									transformedElement.setNodeValue(CodeMigrationUtils.replaceMessagePropertiesAccess(script));
							}
							if(transformedElement.getNodeName().equals("secure-properties:config") || transformedElement.getNodeName().equals("configuration-properties")) {
								List<String> propFileNames = Arrays.asList(transformedElement.getAttribute("file").split(","));
								if(transformedElement.getNodeName().equals("secure-properties:config"))	
									SecurePropertiesMigrator.secPropFileNames.addAll(propFileNames);
								
								transformedElement.setAttribute("file", propFileNames.get(0));
								transformedNode = finalDoc.importNode(transformedElement, copyChildern);
								int propFileIndex=0;
								for (String propFileName : propFileNames) {
									Element newSecPropElement = (Element) transformedElement.cloneNode(true);
									newSecPropElement.setAttribute("file", propFileName);
									newSecPropElement.setAttribute("name", "Secure_Property_Placeholder_"+propFileIndex++);
									finalDoc.getElementsByTagName("mule").item(0).appendChild(finalDoc.importNode(newSecPropElement, true));
								}
							}
							
							finalDoc.getElementsByTagName("mule").item(0).appendChild(transformedNode);
							if (node.hasChildNodes() && XSLTMapper.scopesList.contains(nodeName)) {
								i += transformSubscopeElements(node, transformedNode, finalDoc);
								if (nodeName.equals("batch:job")) {
									isBatchScope = false;
									batchJobElement = null;
								}
							}
							if(transformedElement.getNodeName().equals("error-handler")) {
								TransformedGlobalConfig.defaulErrorConfigName = transformedElement.getAttributeNode("name").getValue();
								if (!TransformedGlobalConfig.isDefaultErrorHandlerSet) {
										System.out.println("creating default error handler in global config file");
										Element defaultErrorHandler = TransformedGlobalConfig.doc.createElement("configuration");
										defaultErrorHandler.setAttribute("doc:name", "Configuration");
										
										defaultErrorHandler.setAttribute("defaultErrorHandler-ref",
												TransformedGlobalConfig.defaulErrorConfigName);
										TransformedGlobalConfig.doc.getElementsByTagName("mule").item(0)
										.appendChild(defaultErrorHandler);
										TransformedGlobalConfig.isDefaultErrorHandlerSet = true;
								}
								
							}
						}
					}
				}
				
				//Start - Code added for template query conversion to Mule 4
				NodeList templateNodeRemoveList = finalDoc.getElementsByTagName("db:template-query");
				templateNodeRemoveList = finalDoc.getElementsByTagName("db:template-query");
				
				while (templateNodeRemoveList.getLength() > 0) {
				    Node node = templateNodeRemoveList.item(0);
				    
				    while (node.hasChildNodes())
				    	node.removeChild(node.getFirstChild());
				    
				    node.getParentNode().removeChild(node);
				}
				//End
		        
				Report.addFlowMigrationIssue(targetFile.substring(targetFile.lastIndexOf("/")+1, targetFile.length()), flowIssueList);
				Report.updateFlowIssuesCount(flowIssueList.size());
				if (!isGlobalConfFile) {
					Migrator.saveFinalDocument(finalDoc, targetFile, isGlobalConfFile);
				} else {
					TransformedGlobalConfig.doc = finalDoc;
				}
				if (removeXMLNSAttrib && !isGlobalConfFile) {
					Utils.removeXMLNSAttribeFromFile(targetFile);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void updateNamespace() {
		// TODO Auto-generated method stub
		Set<String> keySet=NameSpaceMapper.nameSpaceMap.keySet();
		for (String key : keySet) {
			if(XSLConstructor.muleTagNameSpaces.indexOf(key) >= 0) {
				if(key.equals("dw") && m4DocNamespaces.contains("xmlns:ee=")) {
					continue;
				}else if(NameSpaceMapper.emailFamily.contains(key) && m4DocNamespaces.contains("xmlns:email")){
					continue;
				}else {
					Map<String, String> replaceMap = NameSpaceMapper.nameSpaceMap.get(key);
					Set<String> replaceKeys = replaceMap.keySet();
					String muleTagAttribAsString = XSLConstructor.muleTagNameSpaces.toString();
					String m3NameSpaceKey = "";
					String m3NameSpaceVal = "";
					for (String replaceKey : replaceKeys) {
						//if(!replaceKey.equals("dw") && !m4DocNamespaces.contains("xmlns:ee=")) {
							muleTagAttribAsString = muleTagAttribAsString.replace(replaceKey, replaceMap.get(replaceKey));
							m4DocNamespaces = m4DocNamespaces.replace(replaceKey, replaceMap.get(replaceKey));
							if(replaceKeys.size() == 3) {
								if(replaceKey.contains("xmlns:")) {
									m3NameSpaceKey = replaceKey;
								}else if(!replaceKey.contains(".xsd")) {
									m3NameSpaceVal = replaceKey;
								}
							}
						
					}
					if(replaceKeys.size() == 3 && !muleTagAttribAsString.contains(m3NameSpaceKey + "=\"" + m3NameSpaceVal +"\"")) {
						muleTagAttribAsString = " "+muleTagAttribAsString + " " + m3NameSpaceKey + "=\"" + m3NameSpaceVal +"\""; 
					}
					XSLConstructor.muleTagNameSpaces = new StringBuffer(muleTagAttribAsString);
				}
			
			}
		}
		if(!m4DocNamespaces.contains("xmlns:ee=")) {
			m4DocNamespaces = m4DocNamespaces.replace("xmlns=\"http://www.mulesoft.org/schema/mule/core\"", "xmlns=\"http://www.mulesoft.org/schema/mule/core\" xmlns:ee=\"http://www.mulesoft.org/schema/mule/ee/core\" ");
			m4DocNamespaces = m4DocNamespaces.replaceAll("http://www.mulesoft.org/schema/mule/core http://www.mulesoft.org/schema/mule/core/current/mule.xsd", "http://www.mulesoft.org/schema/mule/core http://www.mulesoft.org/schema/mule/core/current/mule.xsd http://www.mulesoft.org/schema/mule/ee/core http://www.mulesoft.org/schema/mule/ee/core/current/mule-ee.xsd");
		}
	}


	protected static Element getTransformedTag(Node node, StringBuffer nodeString, boolean includeNameSpace, boolean isComplex)
			throws TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException,
			SAXException, IOException {
		XSLConstructor xslConstructor = new XSLConstructor();
		InputXMLConstructor xmlConstructor = new InputXMLConstructor();
		xslConstructor.clearBuffer();
		xmlConstructor.clearBuffer();
		if(node.getNodeName().equals("jms:inbound-endpoint") || node.getNodeName().equals("jms:outbound-endpoint") || node.getNodeName().equals("wmq:connector")) {
			Element jmsElement = (Element) node;
			if(node.getNodeName().equals("wmq:connector") && (jmsElement.getAttribute("name").contains("WMQ") || jmsElement.getAttribute("name").contains("wmq"))) {
				if(XSLConstructor.muleTagNameSpaces.indexOf("xmlns:ibm-mq=\"http://www.mulesoft.org/schema/mule/ibm-mq\"") < 0)
					XSLConstructor.muleTagNameSpaces.append(" xmlns:ibm-mq=\"http://www.mulesoft.org/schema/mue/ibm-mq\" ");
				if(!m4DocNamespaces.contains("xmlns:ibm-mq=\"http://www.mulesoft.org/schema/mule/ibm-mq\"")) 
					m4DocNamespaces = m4DocNamespaces + " xmlns:ibm-mq=\"http://www.mulesoft.org/schema/mue/ibm-mq\" ";
			}else if(jmsElement.getAttribute("connector-ref").contains("WMQ") || jmsElement.getAttribute("connector-ref").contains("wmq")) {
				if(XSLConstructor.muleTagNameSpaces.indexOf("xmlns:ibm-mq=\"http://www.mulesoft.org/schema/mule/ibm-mq\"") < 0)
					XSLConstructor.muleTagNameSpaces.append(" xmlns:ibm-mq=\"http://www.mulesoft.org/schema/mule/ibm-mq\" ");
				if(!m4DocNamespaces.contains("xmlns:ibm-mq=\"http://www.mulesoft.org/schema/mule/ibm-mq\"")) 
					m4DocNamespaces = m4DocNamespaces + " xmlns:ibm-mq=\"http://www.mulesoft.org/schema/mue/ibm-mq\" ";
			}
		}
		StreamSource xslSource = new StreamSource(new StringReader(
				xslConstructor.constructXSL(node.getNodeName(), includeNameSpace, isComplex).toString()));

		StreamSource xmlInSource = new StreamSource(
				new StringReader(xmlConstructor.constructInputXML(nodeString, includeNameSpace).toString()));
		Transformer tf = null;
		try {
			tf = TransformerFactory.newInstance().newTransformer(xslSource);
		}catch(TransformerConfigurationException ex) {
			System.out.println("XSLT issue "+node.getNodeName());
			ex.printStackTrace();
			flowIssueList.add(new Issue("Couldn't migrate element : "+node.getNodeName()+" , kindly migrate manually.", "", nodeString));
			return (Element) node;
		}
		
		StringWriter xmlOutWriter = new StringWriter();
		if(null != tf)
			tf.transform(xmlInSource, new StreamResult(xmlOutWriter));

		DocumentBuilder dB = dbFactory.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xmlOutWriter.toString()));
		Document transformedDoc = dB.parse(is);
		Element outputElement= (Element) transformedDoc.getElementsByTagName("output").item(0).getFirstChild();
		if(node.getNodeName().equals("scripting:component") && outputElement.getElementsByTagName("scripting:code").item(0).getTextContent().contains("###"))
		{
			String inputContent= outputElement.getElementsByTagName("scripting:code").item(0).getTextContent();
			String fileName= inputContent.substring(3, inputContent.length());
			String outputContent = "Couldn't copy script from the reference file, copy it manually.";
			try {
			File scriptFile = new File(com.whishworks.transformer.Transformer.sourcePath+"/src/main/resources/"+fileName);
			 outputContent = FileUtils.readFileToString(scriptFile);
			}catch(FileNotFoundException fex) {
				System.out.println("Couldn't find script file with name : "+fileName);
				flowIssueList.add(new Issue("Couldn't copy the content of script file "+fileName+" for the component :"+node.getNodeName()+". Hint: copy content of script from the file to inline as it can't be externalised.", ""+node.getUserData("lineNumber"), nodeString));
			}
			outputElement.getElementsByTagName("scripting:code").item(0).setTextContent(outputContent);
		}
		
		if(UnhandledScenariosHelper.elements.contains(node.getNodeName())) {
			if(XSLTMapper.scopesList.contains(node.getNodeName()))
				UnhandledScenariosHelper.addUnhandledScenariosToReport((Element) node.cloneNode(false),flowIssueList, nodeString);
			else
				UnhandledScenariosHelper.addUnhandledScenariosToReport((Element) node.cloneNode(true),flowIssueList, nodeString);
		}
		if(node.getNodeName().equals("scripting:component")) {
			PercentageComputer.computeMigrationVariables(outputElement, nodeString.toString(), true);
		}else {
			PercentageComputer.computeMigrationVariables((Element)node, nodeString.toString(), true);
		}	
		
		if(node.getNodeName().equals("ee:object-store-caching-strategy")) {
			((Element) finalDoc.getElementsByTagName("mule").item(0)).setAttribute("xmlns:os", "http://www.mulesoft.org/schema/mule/os");
			String xsiLocationValue = ((Element) finalDoc.getElementsByTagName("mule").item(0)).getAttribute("xsi:schemaLocation");
			((Element) finalDoc.getElementsByTagName("mule").item(0)).setAttribute("xsi:schemaLocation", xsiLocationValue+" http://www.mulesoft.org/schema/mule/os http://www.mulesoft.org/schema/mule/os/current/mule-os.xsd");
			PomMigrator.addDependencyToPom("mule-objectstore-connector");
		}
		if(outputElement.getNodeName().startsWith("db:")) {
			NodeList sqlNodeList = outputElement.getElementsByTagName("db:sql");
			if(sqlNodeList.getLength() > 0) {
				sqlNodeList.item(0).setTextContent(CommonUtils.migrateSQLQuery(sqlNodeList.item(0).getTextContent()));
			}
			
			// Start - code added for template query conversion
			NodeList templateRefNodeList = outputElement.getElementsByTagName("db:template-query-ref");
			if(templateRefNodeList.getLength()>0){
				
				NodeList templateNodeList = finalDoc.getElementsByTagName("db:template-query");
				
				for(int i=0;i<templateNodeList.getLength();i++){
					Node templateNode = templateNodeList.item(i);
					if(templateNode.getAttributes().getNamedItem("name").getNodeValue().equals(templateRefNodeList.item(0).getAttributes().getNamedItem("name").getNodeValue())){
						NodeList children = templateNode.getChildNodes();
						for (int j = 0; j < children.getLength(); j++) {

							Node child = children.item(j);
							Node transformedChildNode = transformedDoc.importNode(child, true);

							outputElement.appendChild(transformedChildNode)	;	
						}
					}
				}
				
				outputElement.removeChild(templateRefNodeList.item(0));
			} // End
		}
			
		return outputElement;
	}

	public String getElementAsStringFromDocument(Node node) {
		try {
			DocumentBuilderFactory dbFactoryNew = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilderNew = dbFactoryNew.newDocumentBuilder();
			Document docNew = dBuilderNew.newDocument();
			Node newMuleNode = docNew.importNode(node, true);
			docNew.appendChild(newMuleNode);
			DOMSource domSource = new DOMSource(docNew);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (TransformerException ex) {
			ex.printStackTrace();
			return null;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			getClass();
			return null;
		}
	}

	public static StringBuffer getNodeAsString(Node node) {
		if(XSLTMapper.scopesList.contains(node.getNodeName()))
			return Utils.elementToString(node.cloneNode(false));
		else
			return Utils.elementToString(node.cloneNode(true));
	}

	public static String getConfigElementAsString(Node n) {
		//String name = n.getNodeName();
		StringBuffer sb = new StringBuffer();
//		sb.append('<').append(name);
//		NamedNodeMap attrs = n.getAttributes();
//		if (attrs != null) {
//			for (int i = 0; i < attrs.getLength(); i++) {
//				Node attr = attrs.item(i);
//				//if (attr.getNodeName() != "name") {
//					sb.append(' ').append(attr.getNodeName()).append("=\"").append(attr.getNodeValue()).append("\"");
//				//}
//			}
//		}
//		sb.append("/>").append('\n');
		sb = Utils.elementToString(n);
		int startIndex = sb.indexOf("name=\"");
		int endIndex = sb.indexOf("\"", startIndex);
		sb = sb.replace(startIndex, endIndex, "");

		return sb.toString();
	}

	protected static int transformSubscopeElements(Node node, Node transformedNode, Document finalDoc)
			throws TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException,
			SAXException, IOException {
		int countOfChildElements = 0;
		NodeList children = node.getChildNodes();
		for (int j = 0; j < node.getChildNodes().getLength(); j++) {

			Node child = children.item(j);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				GlobalVariables.totalElementCount++;
				String nodeName = child.getNodeName();
				Element transformedChild = null;
				boolean copyChildChildern = true;
				if (XSLTMapper.skipElements.contains(nodeName) || SpringMigrator.isSpringBeanXMLElement((Element)child) ) {
					countOfChildElements++;
					if (nodeName.equals("batch:input")) {
						batchJobElement = (Element) transformedNode.getFirstChild();
						isBatchInputScope = true;
					}
					if (XSLTMapper.scopesList.contains(nodeName)) {
						countOfChildElements += transformSubscopeElements(child, transformedNode, finalDoc);
					}
					if (!TransformedGlobalConfig.isDefaultErrorHandlerSet) {
						if (nodeName.equals("exception-strategy")) {
							System.out.println("creating default error handler in global config file");
							Element defaultErrorHandler = TransformedGlobalConfig.doc.createElement("configuration");
							defaultErrorHandler.setAttribute("doc:name", "Configuration");
							
							defaultErrorHandler.setAttribute("defaultErrorHandler-ref",
									TransformedGlobalConfig.defaulErrorConfigName);
							TransformedGlobalConfig.doc.getElementsByTagName("mule").item(0)
							.appendChild(defaultErrorHandler);
							TransformedGlobalConfig.isDefaultErrorHandlerSet = true;
						}
					}
					continue;
				}
		
				if(nodeName.contains("batch:") && !nodeName.equals("batch:input")) {
					isBatchInputScope = false;
				}
				if(transformedNode.getNodeName().equals("flow")) {
					Node firstChild = transformedNode.getFirstChild();
					if(null != firstChild && firstChild.getNodeName().equals("batch:job")) {
						batchJobElement = (Element) transformedNode.getFirstChild();
					}
				}
				if (nodeName.equals("enricher")) {
					isEnricherScope = true;
					Element enricher = (Element) child;
					enricherTarget = enricher.getAttribute("target");
					if(enricherTarget.startsWith("#[") && enricherTarget.endsWith("]")) {
						enricherTarget = enricherTarget.substring(2, enricherTarget.length()-1);
					}
					child = enricher.getFirstChild().getNextSibling();
					nodeName = child.getNodeName();
					j = j + 1;
					countOfChildElements++;
				}
				if (XSLTMapper.copyAsIsList.contains(nodeName)) {
					if(XSLTMapper.scopesList.contains(nodeName))
						transformedChild = (Element) child.cloneNode(false);
					else
						transformedChild = (Element) child;
					copyChildChildern = false;

				}else if(nodeName.equals("invoke")){
				    transformedChild = CommonUtils.migrateInvokeComponenet((Element)child, doc, finalDoc);
				}else if(nodeName.equals("expression-component")){
						CommonUtils.transformExpressionComponent(child, transformedNode, finalDoc);
						countOfChildElements++;
						continue;
				}else {
					boolean includeNameSpace = true;
					if (!nodeName.contains(":")) {
						includeNameSpace = false;
					}
					
					StringBuffer nodeString = getNodeAsString(child);
					boolean isWithConfig = XSLTMapper.transformWithConfig.contains(nodeName);
					if (null != XSLTMapper.transformerOf.get(nodeName)) {
						if (!isWithConfig) {
							transformedChild = getTransformedTag(child, new StringBuffer(nodeString), includeNameSpace,
									false);
						} else {
							transformedChild = getTransformedTag(child,
									getElementWithConfig(nodeName, nodeString, (Element) child), includeNameSpace,
									true);
						}
						if (child.hasChildNodes() && !XSLTMapper.scopesList.contains(nodeName)) {
							countOfChildElements += Utils.getRecursiveChildLen(child);
						}
						if (!includeNameSpace) {
							transformedChild.removeAttribute("xmlns");
						}

					} else {
						System.out.println("No mapping found for " + nodeName);
						System.out.println("uanble to transform :" + nodeString);
						GlobalVariables.issueCount++;
						if(UnhandledScenariosHelper.elements.contains(nodeName)) {
							if(XSLTMapper.scopesList.contains(nodeName))
								UnhandledScenariosHelper.addUnhandledScenariosToReport((Element) child.cloneNode(false),flowIssueList, nodeString);
							else
								UnhandledScenariosHelper.addUnhandledScenariosToReport((Element) child.cloneNode(true),flowIssueList, nodeString);
						}else {
							flowIssueList.add(new Issue("uanble to transform :"+nodeName, ""+child.getUserData("lineNumber"), nodeString));
						}
						System.out.println("******copying it as it is****** please make sure migrate it manually******");
						transformedChild = (Element) child;
						PercentageComputer.computeMigrationVariables(transformedChild, nodeString.toString(), false);
						if (child.hasChildNodes() && !XSLTMapper.scopesList.contains(nodeName)) {
							countOfChildElements += Utils.getRecursiveChildLen(child);
						}

					}

				}
				if (null != transformedChild) {
					if (isEnricherScope) {
						transformedChild.setAttribute("target", enricherTarget);
						isEnricherScope = false;
					}
					
						
					if(transformedChild.getNodeName().equals("scripting:component")) {
						String script = transformedChild.getNodeValue();
						if(!script.equals(""))
							transformedChild.setNodeValue(CodeMigrationUtils.replaceMessagePropertiesAccess(script));
					}
					
					Node transformedChildNode = finalDoc.importNode(transformedChild, copyChildChildern);
					
					String transformedElementAsString = Utils.elementToString(transformedChildNode).toString();
					if(transformedElementAsString.contains("#[") && transformedElementAsString.contains("]")) {
						if(transformedChildNode.hasChildNodes() && !XSLTMapper.scopesList.contains(nodeName)) {
							
							transformedChildNode = recursivelyMigrateMel((Element) transformedChildNode, finalDoc);
						}else {
							System.out.println("Element contains MEL expression so migrating");
							transformedChildNode = migrateElementWithMEL((Element) transformedChildNode, finalDoc);
						}
					}
					
					
					if(transformedChild.getNodeName().equals("ee:set-variable") && null != transformedChild.getTextContent() && !transformedChild.getTextContent().startsWith("#[")) {
						String content = transformedChild.getTextContent();
						if(content.contains("%dw") && content.contains("---")) 
							content = CodeMigrationUtils.migrateDwl(content);
						transformedChildNode.setTextContent("");
						transformedChildNode.appendChild(finalDoc.createCDATASection(content));
					}
					if(transformedChildNode.getNodeName().equals("ee:transform")) {
						if(transformedChildNode.hasChildNodes()) {
							NodeList childern = transformedChildNode.getChildNodes();
							for(int ind=0; ind < childern.getLength(); ind++) {
								Node n = childern.item(ind); 
								if(n.hasChildNodes()) {
									if(n.getNodeName().equals("ee:message")) {
										String content = CodeMigrationUtils.migrateDwl(n.getFirstChild().getTextContent());
										n.getFirstChild().setTextContent("");
										n.getFirstChild().appendChild(finalDoc.createCDATASection(content));
									}else if (n.getNodeName().equals("ee:variables")) {
										NodeList variables = n.getChildNodes();
										for(int v=0; v<variables.getLength(); v++) {
											String content = CodeMigrationUtils.migrateDwl(n.getFirstChild().getTextContent());
											variables.item(v).setTextContent("");
											variables.item(v).appendChild(finalDoc.createCDATASection(content));
											if(variables.item(v).getAttributes().getNamedItem("variableName").equals("http.status")) {
												((Element)variables.item(v)).setAttribute("variableName", "httpStatus");
											}
										}
									}
								}
							}
						}
					}
					
					if (transformedNode.getNodeName().equals("flow") && null != batchJobElement) {
						if(isBatchInputScope) {
							transformedNode.insertBefore(transformedChildNode, batchJobElement);
						}else {
							batchJobElement.appendChild(transformedChildNode);
						}
					} 
//					else if (transformedNode.getNodeName().equals("scatter-gather")) {
//						Element routeElement = finalDoc.createElement("route");
//						transformedNode.appendChild(routeElement);
//						routeElement.appendChild(transformedChildNode);
//					}
					else if(transformedNode.getNodeName().equals("wsc:config")){
						transformedNode.getFirstChild().appendChild(transformedChildNode);
					}else if (transformedChildNode.getNodeName().equals("http:authentication")) {
						transformedNode.getFirstChild().appendChild(transformedChildNode);
					} else if(node.getNodeName().equals("message-properties-transformer")){
						if(transformedNode.getFirstChild() != null && transformedNode.getFirstChild().getNodeName().equals("ee:variables")) {
							System.out.println("Element contains MEL expression so migrating");
							transformedNode.getFirstChild().appendChild(transformedChildNode);
						}
					} else if(child.getNodeName().equals("objectstore:retrieve-and-store") && transformedChild.getNodeName().equals("multiple")) {
						NodeList multipleChildNodes = transformedChild.getChildNodes();
						int len = multipleChildNodes.getLength();
						for(int in=0; in < len; in++) {
								Node importedNode = finalDoc.importNode(multipleChildNodes.item(in), true);
								transformedNode.appendChild(recursivelyMigrateMel((Element) importedNode, finalDoc));
						}	
						
					}else {
					
						transformedNode.appendChild(transformedChildNode);
					}
					countOfChildElements++;
					// finalDoc.getLastChild().appendChild(transformedChildNode);
					if (child.hasChildNodes() && XSLTMapper.scopesList.contains(nodeName)) {
						countOfChildElements += transformSubscopeElements(child, transformedChildNode, finalDoc);
					}
				}

			}
		}

		return countOfChildElements;
	}

	

	protected static StringBuffer getElementWithConfig(String nodeName, StringBuffer nodeString, Element node) {

		StringBuffer elementWithConfig = new StringBuffer();

		

			String nodeType = nodeName.substring(0, nodeName.indexOf(":"));
			String tagName = ConnectorDeatils.nodeToConfigMap.get(nodeType);
			
			NodeList configElementList = null;
			boolean isGlobalConfigElement = true;
			String configElement = "";
			Node configNode = null;
			String attributeName = "connector-ref";
		
			if (null != GlobalConfig.globalConfigDoc) {
				if(tagName != null) {
					configElementList = GlobalConfig.globalConfigDoc.getElementsByTagName(tagName);
				}else {
					configElementList = GlobalConfig.globalConfigDoc.getElementsByTagName(nodeType+":connector");
					if(configElementList.getLength() == 0) {
						configElementList = GlobalConfig.globalConfigDoc.getElementsByTagName(nodeType+":config");
					}
				}
				for (int i = 0; i < configElementList.getLength(); i++) {
					Element element = (Element) configElementList.item(i);
					if(element.getNodeName().contains(":config")) {
						attributeName = "config-ref";
					}
					if (element.getAttribute("name").equals(node.getAttribute(attributeName))) {
						if(XSLTMapper.scopesList.contains(element.getNodeName()))
							configNode = element;
						else
							configNode = element.cloneNode(true);
						
						configElement = Utils.elementToString(element).toString();
						System.out.println("config element :" + configElement);
						elementWithConfig.append(configElement);
						elementWithConfig.append(nodeString);

						break;
					}
				}
				if( null == configNode ) {
					if(nodeName.equals("http:inbound-endpoint") || nodeName.equals("https:inbound-endpoint") || nodeName.equals("http:outbound-endpoint") || nodeName.equals("https:outbound-endpoint")) {
						 configNode = Utils.getConfigforHttpInOutBoundElement(GlobalConfig.globalConfigDoc, node.getAttribute("connector-ref"));
						 if(null != configNode) {
						 	configElement = Utils.elementToString(configNode).toString();
							System.out.println("config element :" + configElement);
							elementWithConfig.append(configElement);
							elementWithConfig.append(nodeString);
						 }	
					}
				}
			}
			
			

			if (null == configElementList || configElementList.getLength() == 0 || null == configNode) {
				System.out.println("Didn't find configuration in global config file, searching in the same file");
				if(tagName != null) {
					configElementList = doc.getElementsByTagName(tagName);
				}else {
					configElementList = doc.getElementsByTagName(nodeType+":connector");
					if(configElementList.getLength() == 0) {
						configElementList = doc.getElementsByTagName(nodeType+":config");
					}
				}
				isGlobalConfigElement = false;
			}
			attributeName = "connector-ref";
			for (int i = 0; i < configElementList.getLength(); i++) {
				Element element = (Element) configElementList.item(i);
				
				if(element.getNodeName().contains(":config")) {
					attributeName = "config-ref";
				}
				if (element.getAttribute("name").equals(node.getAttribute(attributeName))) {
					if(XSLTMapper.scopesList.contains(element.getNodeName()))
						configNode = element;
					else
						configNode = element.cloneNode(true);
					configElement = Utils.elementToString(element).toString();
					System.out.println("config element :" + configElement);
					elementWithConfig.append(configElement);
					elementWithConfig.append(nodeString);

					break;
				}
			}
			if( null == configNode ) {
				if(nodeName.equals("http:inbound-endpoint") || nodeName.equals("https:inbound-endpoint") || nodeName.equals("http:outbound-endpoint") || nodeName.equals("https:outbound-endpoint")) {
					 configNode = Utils.getConfigforHttpInOutBoundElement(doc, node.getAttribute("connector-ref"));
					 if(null != configNode) {
					 	configElement = Utils.elementToString(configNode).toString();
						System.out.println("config element :" + configElement);
						elementWithConfig.append(configElement);
						elementWithConfig.append(nodeString);
					 }	
				}
			}
			
			if(null == configElementList ||  null == configNode) {
				System.out.println("Config element was neither in globalConfig nor in same document, searching it in all the available documents");
				Element confElement = Utils.getConfigElementFromOtherXMLs(nodeType, node);
				if(null != confElement) {
					configElement = Utils.elementToString(confElement).toString();
					System.out.println("config element :" + configElement);
					elementWithConfig.append(configElement);
					elementWithConfig.append(nodeString);
				}
			}
			
			if (!configElement.equals("") && null != configNode) {
				try {
					configElementName = "";
					Element transformedConfigElement = getTransformedTag(configNode, elementWithConfig, true, true);
					if (!checkIfConfigExists(isGlobalConfigElement, transformedConfigElement)) {
						if (isGlobalConfigElement) {
							int conuntOfConfigElements = TransformedGlobalConfig.doc
									.getElementsByTagName(transformedConfigElement.getNodeName()).getLength();
							if (conuntOfConfigElements != 0) {
								configElementName = transformedConfigElement.getAttribute("name") + "_"
										+ conuntOfConfigElements;
								transformedConfigElement.setAttribute("name", configElementName);
							}
							Node newConfigElement = TransformedGlobalConfig.doc.importNode(transformedConfigElement,
									true);
							TransformedGlobalConfig.doc.getElementsByTagName("mule").item(0)
							.appendChild(newConfigElement);
							System.out.println("successfully appened config element to config file.");
						} else {
							int conuntOfConfigElements = finalDoc
									.getElementsByTagName(transformedConfigElement.getNodeName()).getLength();
							if (conuntOfConfigElements != 0) {
								configElementName = transformedConfigElement.getAttribute("name") + "_"
										+ conuntOfConfigElements;
								transformedConfigElement.setAttribute("name", configElementName);
								
							}
							if(!configElementName.equals("")) {
								String elementWithConfAsStr = elementWithConfig.toString();
								Pattern pattern = Pattern.compile(attributeName+"=\"(.*?)\"");
								Matcher matcher = pattern.matcher(elementWithConfAsStr);
								String finalStr = matcher.replaceAll(attributeName+"=\""+configElementName+"\"");
								elementWithConfig = new StringBuffer(finalStr);
							}
							Node importedElement = finalDoc.importNode(transformedConfigElement, true);
							finalDoc.getElementsByTagName("mule").item(0).appendChild(importedElement);
						}
					} else {
						System.out.println(
								"there is already a config element avalible with the same config! config name : "
										+ configElementName);
						String elementWithConfigAsString = elementWithConfig.toString();
						if (elementWithConfigAsString.contains(attributeName) && configElementName != "") {
							int startIndex = elementWithConfigAsString.indexOf(attributeName+"=")
									+ (attributeName+"=\"").length();
							int endIndex = elementWithConfigAsString.indexOf("\"", startIndex);
							StringBuffer elementWithConf = new StringBuffer();
							elementWithConf.append(elementWithConfigAsString.substring(0, startIndex));
							elementWithConf.append(configElementName);
							elementWithConf.append(
									elementWithConfigAsString.substring(endIndex, elementWithConfigAsString.length()));
							return elementWithConf;
						}
					}

				} catch (TransformerFactoryConfigurationError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		

		return elementWithConfig;
	}

	static boolean checkIfConfigExists(boolean isGlobalConfigElement, Node transformedConfigElement) {
		NodeList nodes = null;
		String nodeName = transformedConfigElement.getNodeName();
		if (isGlobalConfigElement) {
			nodes = TransformedGlobalConfig.doc.getElementsByTagName(nodeName);
		} else {
			nodes = finalDoc.getElementsByTagName(nodeName);
		}
		if (nodes.getLength() == 0) {
			return false;
		}
		if (null != nodes) {
			String configElementAsString = getConfigElementAsString(transformedConfigElement).replaceAll(" name=\"(.*?)\"", " ");
			for (int i = 0; i < nodes.getLength(); i++) {
				String configElementAsStrWithoutName = getConfigElementAsString(nodes.item(i)).replaceAll(" name=\"(.*?)\"", " ");
				
				if (configElementAsStrWithoutName.equals(configElementAsString)) {
					configElementName = nodes.item(i).getAttributes().getNamedItem("name").getNodeValue();
					return true;
				}
			}
		}
		return false;
	}

/*	static int migrateBatchInputScopeElements(Node node, Node transformedNode)
			throws TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException,
			SAXException, IOException {
		int countOfElements = 0;
		System.out.println("working on batch input scope");
		NodeList batchInputChildren = node.getChildNodes();
		for (int y = 0; y < batchInputChildren.getLength(); y++) {
			Node child = batchInputChildren.item(y);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				String nodeName = child.getNodeName();
				Element transformedChild = null;
				boolean copyChildChildern = true;
				if (XSLTMapper.skipElements.contains(nodeName)) {
					countOfElements++;
					continue;
				}
				if (XSLTMapper.copyAsIsList.contains(nodeName)) {
					transformedChild = (Element) child;
					copyChildChildern = false;
				} else {
					boolean includeNameSpace = true;
					if (!nodeName.contains(":")) {
						includeNameSpace = false;
					}
					StringBuffer nodeString = getNodeAsString(child);
					boolean isWithConfig = XSLTMapper.transformWithConfig.contains(nodeName);
					if (null != XSLTMapper.transformerOf.get(nodeName)) {
						if (!isWithConfig) {
							transformedChild = getTransformedTag(child, new StringBuffer(nodeString), includeNameSpace,
									false);
						} else {
							transformedChild = getTransformedTag(child,
									getElementWithConfig(nodeName, nodeString, (Element) child), includeNameSpace,
									true);
						}
						if (child.hasChildNodes() && !XSLTMapper.scopesList.contains(nodeName)) {
							countOfElements += Utils.getRecursiveChildLen(child);
						}
						if (!includeNameSpace) {
							transformedChild.removeAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns");
							removeXMLNSAttrib = true;
						}
					} else {
						System.out.println("No mapping found for " + nodeName);
						System.out.println("uanble to transform :" + nodeString);
						System.out.println("******copying it as it is****** please make sure migrate it manually******");
						flowIssueList.add(new Issue("uanble to transform :"+nodeName, ""+child.getUserData("lineNumber"), nodeString));
						countOfElements++;
						countOfElements += Utils.getRecursiveChildLen(child);
					}
				}
				if (null != transformedChild) {
					Element transformedChildNode = (Element) finalDoc.importNode(transformedChild, copyChildChildern);
					transformedChildNode.removeAttribute("xmlns");

					if (null != batchJobElement) {
						transformedNode.insertBefore(transformedChildNode, batchJobElement);
					} else {
						transformedNode.insertBefore(transformedChildNode, transformedNode.getFirstChild());
					}
					countOfElements++;
					if (child.hasChildNodes() && XSLTMapper.scopesList.contains(nodeName)) {
						countOfElements += migrateBatchInputScopeElements(child, transformedNode);
					}
				}
			}
		}
		return countOfElements;
	} */

	static Element removeXMLNSAttrib(Element node) {
		node.removeAttribute("xmlns");
		return node;
	}

	public static Element migrateElementWithMEL(Element element, Document doc) {
		
		// Migratig MEL if it is present in attribute values ex: <logger message="#[payload.text]"/>
		
		 NamedNodeMap map = element.getAttributes();
		 for(int i=0;i<map.getLength();i++) {
			Node attribute = map.item(i);
			String attributeValue = attribute.getNodeValue();
			if(attributeValue.contains("#[") && attributeValue.contains("]")) {
				String migratedMel = "";
				attributeValue = attributeValue.trim();
			  if(attributeValue.indexOf("#[") == attributeValue.lastIndexOf("#[") && attributeValue.startsWith("#[") && attributeValue.endsWith("]")) {
				System.out.println("-------------migrating MEL-------------");
				 migratedMel = CodeMigrationUtils.mel_to_dwl2(attributeValue.substring(attributeValue.indexOf("#[")+2, attributeValue.lastIndexOf("]")));
				 element.setAttribute(attribute.getNodeName(), "#["+migratedMel+"]");
			  }else {
				   Pattern patten = Pattern.compile("#\\[(.*?)\\]");
		  			 Matcher matcher = patten.matcher(attributeValue);
		  			 int index =0;
		  			 while(matcher.find()) {
		  				String migratedMelExpr = CodeMigrationUtils.mel_to_dwl2(matcher.group(1));
		  				if(element.getNodeName().equals("logger")) {
			  				migratedMelExpr.replace("payload", "write(payload,'application/json')");
			  			} 
		  				if(index == 0)
		  					migratedMel += CommonUtils.dwStringExpression(attributeValue.substring(index, matcher.start())) + " "+ migratedMelExpr + " ";
		  				else
		  					migratedMel += " ++ "+CommonUtils.dwStringExpression(attributeValue.substring(index, matcher.start())) + " "+ migratedMelExpr +" ";
		  				
		  				index = matcher.end();
					 }
		  			migratedMel += " ++ '"+ attributeValue.substring(index) +"' ";
		  			element.setAttribute(attribute.getNodeName(), "#["+migratedMel+"]");
			  }
			  
			}else if(attribute.getNodeName().equals("expression")) {
				element.setAttribute(attribute.getNodeName(), "#["+CodeMigrationUtils.mel_to_dwl2(attributeValue)+"]");
			}
		 }
		 // Migrating MEL if it is used in text content of xml element ex: <a>#[payload.test]</a>
		 String elementContent = element.getTextContent();		 
		 if(null != elementContent && elementContent.contains("#[") && elementContent.contains("]")) {
			 String migratedMel = "";
			 if(elementContent.indexOf("#[") == elementContent.lastIndexOf("#[") && elementContent.startsWith("#[") && elementContent.endsWith("]")) {
				 if(elementContent.contains("@[")) {
					 Pattern patten = Pattern.compile("@\\[(.*?)\\]");
		  			 Matcher matcher = patten.matcher(elementContent);
		  			 String newElementContent = "";
		  			 int index =0;
		  			 Pattern hashSqrPattern = Pattern.compile("#\\[(.*?)\\]");
		  			 while(matcher.find()) {
		  				String newSubContent = "";
		  				String subContent = matcher.group(1);
		  				Matcher hashSqrMatcher = hashSqrPattern.matcher(subContent);
		  				int j=0;
		  				while(hashSqrMatcher.find()) {
		  					if(j == 0)
		  						newSubContent += "'"+subContent.substring(j, hashSqrMatcher.start())+"' ++ "+hashSqrMatcher.group(1);
		  					else
		  						newSubContent += " ++ '"+subContent.substring(j, hashSqrMatcher.start())+"' ++ "+hashSqrMatcher.group(1);
		  					j = hashSqrMatcher.end(); 
		  				}
		  				if(subContent.substring(j).length() > 0)
		  					newSubContent += " ++ '"+subContent.substring(j)+"'";
		  				
		  				newElementContent += elementContent.substring(index, matcher.start())+" "+newSubContent+" ";
		  				index = matcher.end();
					 }
		  			newElementContent += elementContent.substring(index);
		  			elementContent = newElementContent;
				 }
				 migratedMel = CodeMigrationUtils.mel_to_dwl2(elementContent.substring(2, elementContent.length()-1));
			 }else {
				 Pattern patten = Pattern.compile("#\\[(.*?)\\]");
	  			 Matcher matcher = patten.matcher(elementContent);
	  			 int index =0;
	  			 
	  			 while(matcher.find()) {
	  					migratedMel += elementContent.substring(index, matcher.start()) + "#["+ CodeMigrationUtils.mel_to_dwl2(matcher.group(1)) +"]"; 
	  					index = matcher.end();
				 }
	  			migratedMel += elementContent.substring(index);
			 }
			if(XSLTMapper.hasContentInHashSqaureBrackerts.contains(element.getNodeName())) 
				 element.setTextContent("#["+migratedMel+"]");
			else
				 element.setTextContent(migratedMel);
			
		 }
		 if(XSLTMapper.hasContentInCDATASection.contains(element.getNodeName())) {
			 Node cDataSection = doc.createCDATASection(element.getTextContent());
			 element.setTextContent("");
			 element.appendChild(cDataSection);
		 }
		return element;
	}
	
	public static Element recursivelyMigrateMel(Element element, Document doc) {
		
		if(Utils.hasSingleTextNodeAsChild(element) || !element.hasChildNodes()) {
			String content = Utils.elementToString(element).toString();
			if(content.contains("#[") && content.contains("]")) {
				  return migrateElementWithMEL(element, doc);
			}else {
				return element;
			}
		}else if(element.hasChildNodes()) {
			Element rootNode = migrateElementWithMEL((Element) element.cloneNode(false), doc);
			NodeList childern = element.getChildNodes(); 
			for(int i=0; i< childern.getLength(); i++) {
				if(childern.item(i).getNodeType() == Node.ELEMENT_NODE ) {
					//Node childRootNode = childern.item(i).cloneNode(false);
					 Node migratedChild = recursivelyMigrateMel((Element)childern.item(i), doc);
					 rootNode.appendChild(migratedChild.cloneNode(true));
					 //i += Utils.getRecursiveChildLen(migratedChild);
				}
			}
			return rootNode;
		}
		return element;
	}
	
	public static void saveFinalDocument(Document newDoc, String targetFile, boolean isGlobalConfFile)
			throws Exception {

		TransformerFactory tranFactory = TransformerFactory.newInstance();
		Transformer aTransformer = tranFactory.newTransformer();
		aTransformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");

		Source src = new DOMSource(newDoc);

		File output = new File(targetFile);
		System.out.println(targetFile);
		output.createNewFile();
		Result dest = new StreamResult(output);

		aTransformer.transform(src, dest);
	}
}	