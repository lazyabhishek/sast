package com.whishworks.migrator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.whishworks.migrator.util.Utils;
import com.whishworks.migrator.util.XSLTMapper;
import com.whishworks.transformer.Transformer;

public class SpringMigrator {
	
  public static Document springXMLDoc;
  static List<String> springNameSpaces = new ArrayList<String>();
  
  public static void moveSpringStuffToBeansXML(Document doc, String nameSpacesOfDoc, DocumentBuilder dBuilder) throws SAXException, IOException {

	  //springXMLDoc = dBuilder.parse("resources/spring-config.xml");
	  if(null == springXMLDoc) {
		  Class<Transformer> classLoader = Transformer.class;
		  springXMLDoc = dBuilder.parse(classLoader.getResourceAsStream("/resources/spring-config.xml"));
	  }
	  springNameSpaces = new ArrayList<String>();
	  Element springConfigRoot = (Element) springXMLDoc.getElementsByTagName("beans").item(0); 
	  
	  
	  NamedNodeMap muleTagNameSpaces = doc.getElementsByTagName("mule").item(0).getAttributes();
	  
	  for(int ns=0; ns<muleTagNameSpaces.getLength(); ns++) {
		  String attributeName = muleTagNameSpaces.item(ns).getNodeName();
		  String attributeVal = muleTagNameSpaces.item(ns).getNodeValue();
		  if(attributeVal.contains("http://www.springframework.org/") && attributeVal != "http://www.springframework.org/schema/beans" && attributeVal != "http://www.springframework.org/schema/spring" ) {
			  springNameSpaces.add(attributeVal);
			  if(springConfigRoot.getAttribute(attributeName) ==  "") {
				  springConfigRoot.setAttribute(attributeName, attributeVal);
			  }
		  }
		 
	  }
	  
	  
	  
	  //Copying spring beans to Spring-beans.xml
	  if(nameSpacesOfDoc.contains("xmlns:spring")) {

		  NodeList springBeans = doc.getElementsByTagName("spring:beans");
		  for(int b =0; b< springBeans.getLength(); b++) {
			  NodeList springBeanChildern = springBeans.item(b).getChildNodes();
			  for(int i=0; i<springBeanChildern.getLength(); i++) {
				  Node child = springBeanChildern.item(i); 
				  if(child.getNodeType() == Node.ELEMENT_NODE) {
					  if(child.getNodeName().equals("spring:import")) {
						  continue;
					  }
//						  else if(child.getNodeName().equals("spring:bean")) {
//						  Element bean = springXMLDoc.createElement("bean");
//						  bean = Utils.copyAllAttributes((Element) child, bean);
//						  if(child.hasChildNodes()) {
//							  NodeList beanChildern = child.getChildNodes();
//							  for(int c=0; c<beanChildern.getLength(); c++) {
//								  Node beanChild = beanChildern.item(c);
//								  if(beanChild.getNodeName().equals("spring:property")) {
//									  Element property = springXMLDoc.createElement("property");
//									  property = Utils.copyAllAttributes((Element)beanChild, property);
//									  bean.appendChild(property);
//								  }
//							  }
//						  }
//						  Node importedBean = springXMLDoc.importNode(bean, true);
//						  springConfigRoot.appendChild(importedBean);
//					  }
					  else {
						  Node importedElement = springXMLDoc.importNode(child, true);
						  i += Utils.getRecursiveChildLen(child);
						  springConfigRoot.appendChild(importedElement);
					  }
				  }
			  }
		  }
	  }
	  
	  
	  //Moving all spring related elements to spring-config.xml for above extracted namespaces in springNameSpaces
	  for (String string : springNameSpaces) {
		  if(nameSpacesOfDoc.contains(string)) {
			  NodeList contextElements = doc.getElementsByTagNameNS(string, "*");
			  for(int i=0;i<contextElements.getLength();i++) {
				 springConfigRoot.appendChild(springXMLDoc.importNode(contextElements.item(i), true));
				 i += Utils.getRecursiveChildLen(contextElements.item(i));
			  }
		  }
	  }
	  
//	  //copy context:property-placeholder and other spring context elements to Spring-beans.xml
//	  if(nameSpacesOfDoc.contains("http://www.springframework.org/schema/context")) {
//		  NodeList contextElements = doc.getElementsByTagNameNS("http://www.springframework.org/schema/context", "*");
//		  for(int i=0;i<contextElements.getLength();i++) {
//			 springConfigRoot.appendChild(springXMLDoc.importNode(contextElements.item(i), true));
//		  }
//	  }
//	  
//	  //copy spring security elements to spring-beans.xml
//	  if(nameSpacesOfDoc.contains("http://www.springframework.org/schema/security")) {
//		  NodeList springSecurityElements = doc.getElementsByTagNameNS("http://www.springframework.org/schema/security", "*");
//		  for(int i=0;i<springSecurityElements.getLength();i++) {
//			 springConfigRoot.appendChild(springXMLDoc.importNode(springSecurityElements.item(i), true));
//		  }
//	  }
//	  try {
//		Migrator.saveFinalDocument(springXMLDoc, "D:/outputSpringBeans.xml", false);
//	} catch (Exception e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
  }
  
  public static boolean isSpringBeanXMLElement(Element node) {
	  
	  if(XSLTMapper.moveComponentsToBeansXML.contains(node.getNodeName())) {
		  return true;
	  }else if(springNameSpaces.contains(node.getNamespaceURI())) {
		  return true;
	  }
	return false;
  }
}
