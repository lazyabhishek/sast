package com.whishworks.migrator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.whishworks.migrator.util.GlobalVariables;
import com.whishworks.migrator.util.PercentageComputer;
import com.whishworks.migrator.util.Utils;
import com.whishworks.migrator.util.XSLTMapper;
import com.whishworks.report.generator.Issue;
import com.whishworks.report.generator.Report;

public class MUnitMigrator {

	static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	static DocumentBuilder dBuilder;
	static Document mUnitDoc;
	static Document finalDoc;
	static boolean removeXMLNSAttrib = false;
	static List<Issue> munitIssueList;
	public static List<String> beahaviorBlockElements = Stream.of("object-to-json-transformer", "object-to-string-transformer", "json:json-to-object-transformer").collect(Collectors.toList());
	
	public static void migrateMUnit(File mUnitFile, String targetFile) throws TransformerFactoryConfigurationError, Exception {
		XSLTMapper mapper = new XSLTMapper();
		munitIssueList = new ArrayList<>();
		dBuilder = dbFactory.newDocumentBuilder();
		mUnitDoc = dBuilder.parse(mUnitFile);
		finalDoc = dBuilder.newDocument();
		Node muleTag = mUnitDoc.getElementsByTagName("mule").item(0);
		Element element = (Element) muleTag;
		element.setAttribute("xmlns:munit-tools", "http://www.mulesoft.org/schema/mule/munit-tools");
		element.setAttribute("xmlns:ee", "http://www.mulesoft.org/schema/mule/ee/core");
		Node copiedMuleNode = finalDoc.importNode(element, false);
		finalDoc.appendChild(copiedMuleNode);
		StringBuffer muleTagAttribsAsString = new StringBuffer();
		for (int i = 0; i < muleTag.getAttributes().getLength(); i++) {
			muleTagAttribsAsString.append(muleTag.getAttributes().item(i) + "  ");
		}
		InputXMLConstructor.muleTagNameSpaces = muleTagAttribsAsString;
		XSLConstructor.muleTagNameSpaces = muleTagAttribsAsString;
		NodeList nodeList = mUnitDoc.getElementsByTagName("*");
		
		//Move Spring to spring-beans.xml
		if(muleTagAttribsAsString.toString().contains("xmlns:spring") && muleTagAttribsAsString.toString().contains("http://www.springframework.org/"))
			SpringMigrator.moveSpringStuffToBeansXML(mUnitDoc, muleTagAttribsAsString.toString(), dBuilder);
		
		
		for (int i = 1; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				GlobalVariables.totalElementCount++;
				String nodeName = node.getNodeName();
				StringBuffer nodeString = new StringBuffer();
				Element transformedElement = null;
				boolean includeNameSpace = true;
				boolean isWithConfig = XSLTMapper.transformWithConfig.contains(nodeName);
//				if(nodeName.contains("spring")) {
//					System.out.println("skipping spring beans part!");
//					munitIssueList.add(new Issue("skipping spring beans", ""+node.getUserData("lineNumber"), Utils.elementToString(node)));
//					continue;
//				}
					boolean copyChildern = true;
					if (XSLTMapper.copyAsIsList.contains(nodeName)) {
						transformedElement = (Element) node;
						copyChildern = false;
					}else if(XSLTMapper.skipElements.contains(nodeName) || SpringMigrator.isSpringBeanXMLElement((Element)node)){
						
						if(XSLTMapper.scopesList.contains(nodeName)) {
							System.out.println(nodeName+ "  skip element has got subscope, but couldn't transform it !");
						}
						continue;
						
					}else if(null != XSLTMapper.transformerOf.get(nodeName)){
						if (!nodeName.contains(":")) {
							includeNameSpace = false;
						}
						 nodeString = Utils.elementToString(node);
						if(!isWithConfig) {
							 transformedElement = Migrator.getTransformedTag(node, new StringBuffer(nodeString), includeNameSpace, false);
						}else {
							transformedElement = Migrator.getTransformedTag(node, Migrator.getElementWithConfig(nodeName, nodeString, (Element) node), includeNameSpace, true);
						}
						
						if (node.hasChildNodes() && !XSLTMapper.scopesList.contains(nodeName)) {
//							NodeList childList = node.getChildNodes();
//							for(int index =0; index < childList.getLength(); index++) {
//								if(childList.item(index).getNodeType() == Node.ELEMENT_NODE) {
//									i++;
//								}
//							}
							i += Utils.getRecursiveChildLen(node);
						}
					} else {
						if(!XSLTMapper.skipElements.contains(nodeName)) {
							nodeString = Utils.elementToString(node);
							System.out.println("no xslt mapping found for " + nodeName);
							System.out.println("uanble to transform :" + nodeString);
							GlobalVariables.issueCount++;
							System.out.println("******copying it as it is****** please make sure migrate it manually******");
							munitIssueList.add(new Issue("uanble to transform : "+nodeName, node.getUserData("lineNumber")+"", nodeString));
							transformedElement = (Element) node;
							PercentageComputer.computeMigrationVariables(transformedElement, nodeString.toString(), false);
						}
					}
					if (!includeNameSpace) {
						transformedElement.removeAttributeNS("", "xmlns");
						removeXMLNSAttrib = true;
					}
					if(null != transformedElement) {
						Element transformedNode = (Element) finalDoc.importNode(transformedElement, copyChildern);
						String transformedElementAsString = Utils.elementToString(transformedNode).toString();
						if(transformedElementAsString.contains("#[") && transformedElementAsString.contains("]")) {
							System.out.println("Element contains MEL expression so migrating");
							if(transformedElement.hasChildNodes() && !XSLTMapper.scopesList.contains(nodeName)) {
								
								transformedElement = Migrator.recursivelyMigrateMel(transformedNode, finalDoc);
							}else {
								transformedElement = Migrator.migrateElementWithMEL(transformedNode, finalDoc);
							}
							
							//transformedElement = Migrator.migrateElementWithMEL(transformedElement);
						}
						
						//Node transformedNode = finalDoc.importNode(transformedElement, copyChildern);
						finalDoc.getElementsByTagName("mule").item(0).appendChild(transformedNode);
						if (node.hasChildNodes() && XSLTMapper.scopesList.contains(nodeName)) {
							i += transformMunitTestElement(node, transformedNode);
						}
					}			
			}
			
		}
		Report.addMuintMigrationIssue(targetFile.substring(targetFile.lastIndexOf("/")+1, targetFile.length()), munitIssueList);
		Report.updateMunitIssuesCount(munitIssueList.size());
		Migrator.saveFinalDocument(finalDoc, targetFile, false);
	}
	
	private static int transformMunitTestElement(Node node, Node transformedNode) throws TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException, SAXException, IOException {
		int countOfElements = 0;
		NodeList children = node.getChildNodes();
		for (int j = 0; j < node.getChildNodes().getLength(); j++) {

			Node child = children.item(j);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				GlobalVariables.totalElementCount++;
				String nodeName = child.getNodeName();
				Element transformedChild = null;
				boolean copyChildChildern = true;
				if(XSLTMapper.skipElements.contains(nodeName) || SpringMigrator.isSpringBeanXMLElement((Element)node)) {
					countOfElements++;
					if(XSLTMapper.scopesList.contains(nodeName)) {
						countOfElements += transformMunitTestElement(child, transformedNode);
						
					}
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
					StringBuffer nodeString = Utils.elementToString(child);
					if (null != XSLTMapper.transformerOf.get(nodeName)) {
						transformedChild = Migrator.getTransformedTag(child, nodeString, includeNameSpace, false);
						
						if (child.hasChildNodes() && !XSLTMapper.scopesList.contains(nodeName)) {
							countOfElements += Utils.getRecursiveChildLen(child);
						}
					}else {
						System.out.println("No mapping found for " + nodeName);
						System.out.println("uanble to transform :" + nodeString);
						GlobalVariables.issueCount++;
						System.out.println("******copying it as it is****** please make sure migrate it manually******");
						munitIssueList.add(new Issue("uanble to transform : "+nodeName, child.getUserData("lineNumber")+"", nodeString));
						transformedChild = (Element) child;
						PercentageComputer.computeMigrationVariables(transformedChild, nodeString.toString(), false);
						countOfElements += Utils.getRecursiveChildLen(child);
					}
					
				}
				if (null != transformedChild) {
					
					Element transformedChildNode = (Element) finalDoc.importNode(transformedChild, copyChildChildern);
					String transformedElementAsString = Utils.elementToString(transformedChildNode).toString();
					if(transformedElementAsString.contains("#[") && transformedElementAsString.contains("]")) {
						System.out.println("Element contains MEL expression so migrating");
						if(transformedChild.hasChildNodes() && !XSLTMapper.scopesList.contains(nodeName)) {
							
							transformedChild = Migrator.recursivelyMigrateMel(transformedChildNode, finalDoc);
						}else {
							transformedChild = Migrator.migrateElementWithMEL(transformedChildNode, finalDoc);
						}
						//transformedChild = Migrator.migrateElementWithMEL(transformedChild);
					}
					
					//Node transformedChildNode = finalDoc.importNode(transformedChild, copyChildChildern);
					
					if(transformedChild.getNodeName().equals("ee:set-variable") && null != transformedChild.getTextContent() && !transformedChild.getTextContent().startsWith("#[")) {
						transformedChildNode.setTextContent("");
						transformedChildNode.appendChild(finalDoc.createCDATASection(CodeMigrationUtils.mel_to_dwl2(transformedChild.getTextContent())));
					}
					
					if(transformedChildNode.getNodeName().equals("ee:transform")) {
						if(transformedChildNode.hasChildNodes()) {
							NodeList childern = transformedChildNode.getChildNodes();
							for(int ind=0; ind < childern.getLength(); ind++) {
								Node n = childern.item(ind); 
								if(n.hasChildNodes()) {
									if(n.getNodeName().equals("ee:message")) {
										String content = n.getFirstChild().getTextContent();
										n.getFirstChild().setTextContent("");
										n.getFirstChild().appendChild(finalDoc.createCDATASection(content));
									}else if (n.getNodeName().equals("ee:variables")) {
										NodeList variables = n.getChildNodes();
										for(int v=0; v<variables.getLength(); v++) {
											String content = n.getFirstChild().getTextContent();
											variables.item(v).setTextContent("");
											variables.item(v).appendChild(finalDoc.createCDATASection(content));
										}
									}
								}
							}
						}
					}
					
					
					
					if(child.getParentNode().getNodeName().equals("munit:test")) {
						if(transformedChild.getNodeName().contains(":mock-") || transformedChild.getNodeName().equals("munit-tools:spy")) {
							transformedNode.getChildNodes().item(0).appendChild(transformedChildNode);
						}else if(transformedChild.getNodeName().contains(":assert-")) {
							transformedNode.getChildNodes().item(2).appendChild(transformedChildNode);
						}else {
							transformedNode.getChildNodes().item(1).appendChild(transformedChildNode);
						}
					}else if (transformedChildNode.getNodeName().equals("http:authentication")) {
						transformedNode.getFirstChild().appendChild(transformedChildNode);
					}else {
						transformedNode.appendChild(transformedChildNode);
					}
					countOfElements++;
					if (child.hasChildNodes() && XSLTMapper.scopesList.contains(nodeName)) {
						countOfElements += transformMunitTestElement(child, transformedChildNode);
					}	
				}
			}
		}	
		return countOfElements;
	}
	
	

}
