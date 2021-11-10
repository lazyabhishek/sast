package com.whishworks.migrator;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GlobalConfig {

	public static Document globalConfigDoc;
	
	//protected static StringBuffer
	
	public GlobalConfig(File globalConfFile) throws SAXException, IOException, ParserConfigurationException {

		GlobalConfig.globalConfigDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(globalConfFile);
	}
	
	public GlobalConfig() {
	}

	public void appendConfigToGlobalFile(Document configDoc) {
		if(null != globalConfigDoc) {
			Node muleNode =  globalConfigDoc.getElementsByTagName("mule").item(0);
			if(null != muleNode) {
				NodeList nodeList = configDoc.getElementsByTagName("mule").item(0).getChildNodes();
				for(int i=0;i<nodeList.getLength();i++) {
					Node importNode = globalConfigDoc.importNode(nodeList.item(i), true);
					muleNode.appendChild(importNode);
				}
			}
		}
	}
	
}
