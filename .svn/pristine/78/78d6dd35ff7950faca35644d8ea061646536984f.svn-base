package com.whishworks.migrator;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class MigratorUtils {
	
	public String convertXMLDocToStringBuffer(Document document) {
		
		try {
		      TransformerFactory tf = TransformerFactory.newInstance();
		      Transformer trans = tf.newTransformer();
		      StringWriter sw = new StringWriter();
		      trans.transform(new DOMSource(document), new StreamResult(sw));
		      return sw.toString();
		    } catch (TransformerException tEx) {
		      tEx.printStackTrace();
		    }
		    return null;
		
	}
	
	public static Document constructMule4Document(String updatedMuleTagAttributes){
		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
						"<mule "+updatedMuleTagAttributes+" />";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder;  
        try  
        {  
            builder = factory.newDocumentBuilder();  
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) ); 
            return doc;
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
        return null;
    }
	
	
	public static Node getCompositeSourceNodeToMigrate(Node node) {
		int compositeSourcesCount = 0;
		Element compositeSource = Migrator.doc.createElement("custom-composite-source");
		Element subFlow = null;
		Element flowRefToSubFlow = null;
		if(null != Migrator.doc) {
			 subFlow = Migrator.doc.createElement("sub-flow");
			 subFlow.setAttribute("name", node.getAttributes().getNamedItem("name").getNodeValue()+"-subFlow");
			 
			 flowRefToSubFlow = Migrator.doc.createElement("flow-ref");
			 flowRefToSubFlow.setAttribute("name", node.getAttributes().getNamedItem("name").getNodeValue()+"-subFlow");
			 flowRefToSubFlow.setAttribute("doc:name", "Call "+node.getAttributes().getNamedItem("name").getNodeValue()+"-subFlow");
		}
		 
		NodeList childNodes = node.getChildNodes();
		for(int i=0; i <childNodes.getLength(); i++) {
			Node child = childNodes.item(i);
			if(child.getNodeType() == Node.ELEMENT_NODE ) {
				if(child.getNodeName().equals("composite-source")) {
					NodeList compositeSources = child.getChildNodes();
					for(int j=0; j<compositeSources.getLength(); j++) {
						if(compositeSources.item(j).getNodeType() == Node.ELEMENT_NODE) {
							Element flow = (Element) node.cloneNode(false);
							flow.setAttribute("name", node.getAttributes().getNamedItem("name").getNodeValue()+""+compositeSources.item(j).getAttributes().getNamedItem("doc:name").getNodeValue());
							flow.appendChild(compositeSources.item(j).cloneNode(true));
							flow.appendChild(flowRefToSubFlow.cloneNode(true));
							compositeSource.appendChild(flow);
							compositeSourcesCount++;
						}
					}
				}else {
					subFlow.appendChild(child);
				}
			}
			compositeSource.appendChild(subFlow);
		}
		compositeSource.setAttribute("count", compositeSourcesCount+"");
		return compositeSource;
	}
}
