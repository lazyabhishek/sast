package com.whishworks.migrator.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.whishworks.migrator.GlobalConfig;
import com.whishworks.transformer.Transformer;

public class Utils {
	public static StringBuffer elementToString(Node n) {

		String name = n.getNodeName();

		short type = n.getNodeType();

		if (Node.CDATA_SECTION_NODE == type) {
			return new StringBuffer("<![CDATA[" + n.getNodeValue() + "]]>");
		}

		if (name.startsWith("#")) {
			return new StringBuffer();
		}

		StringBuffer sb = new StringBuffer();
		sb.append('<').append(name);

		NamedNodeMap attrs = n.getAttributes();
		if (attrs != null) {
			for (int i = 0; i < attrs.getLength(); i++) {
				Node attr = attrs.item(i);
				// if(attr.getNodeName().equals("outputPattern") ||
				// attr.getNodeName().equals("expectedValue")) {
				// System.out.println("quot exisits");
				// }
				String attrValue = attr.getNodeValue();
				if (attrValue.contains("&")) {
					attrValue = attrValue.replace("&", "&amp;");
				} 
				if (attrValue.contains("\"")) {
					attrValue = attrValue.replace("\"", "&quot;");
				} 
				
				if (attrValue.contains("<")) {
					attrValue = attrValue.replace("<", "&lt;");
				} 
				if (attr.getNodeValue().contains(">")) {
					attrValue = attrValue.replace("<", "&gt;");
				}
				sb.append(' ').append(attr.getNodeName()).append("=\"").append(attrValue).append("\"");
			}
		}

		String textContent = null;
		NodeList children = n.getChildNodes();

//		if (XSLTMapper.scopesList.contains(name)) {
//			sb.append("/>").append('\n');
//
//			return sb;
//		}

		if (children.getLength() == 0) {
			if ((textContent = n.getTextContent()) != null && !"".equals(textContent)) {
				sb.append(textContent).append("</").append(name).append('>');
				;
			} else {
				sb.append("/>").append('\n');
			}
		} else {
			sb.append('>').append('\n');
			boolean hasValidChildren = false;
			for (int i = 0; i < children.getLength(); i++) {
				StringBuffer childToString = elementToString(children.item(i));
				if (!"".equals(childToString.toString())) {
					sb.append(childToString);
					hasValidChildren = true;
				}
			}

			if (!hasValidChildren && ((textContent = n.getTextContent()) != null)) {
				sb.append(textContent);
			}

			sb.append("</").append(name).append('>');
		}

		return sb;
	}

	public static void removeXMLNSAttribeFromFile(String filePath) {
		BufferedReader reader = null;
		BufferedWriter writer = null;
		String originalFileContent = "";

		try {
			FileReader fReader = new FileReader(filePath);
			if (null != fReader) {
				reader = new BufferedReader(fReader);
				String currentReadingLine = reader.readLine();
				while (currentReadingLine != null) {
					originalFileContent += currentReadingLine + System.lineSeparator();
					currentReadingLine = reader.readLine();
				}
				
				String nonMuleTagContent = originalFileContent.substring( originalFileContent.indexOf(">", originalFileContent.indexOf("<mule"))+1, originalFileContent.indexOf("</mule>"));
				String modifiedFileContent = (nonMuleTagContent.replaceAll(" xmlns:(.*?)=\"(.*?)\"", ""));
				modifiedFileContent = originalFileContent.substring(0, originalFileContent.indexOf(">", originalFileContent.indexOf("<mule"))+1) + modifiedFileContent + "\r\n</mule>";
				writer = new BufferedWriter(new FileWriter(filePath));
				writer.write(modifiedFileContent);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
		
		public static int getRecursiveChildLen(Node node) {
			int childCount = 0;
			NodeList childList = node.getChildNodes();
			for (int index = 0; index < childList.getLength(); index++) {
				if (childList.item(index).getNodeType() == Node.ELEMENT_NODE) {
					childCount++;
					childCount += getRecursiveChildLen(childList.item(index));
				}
			}
		  return childCount;	
		}
	
		public static boolean hasSingleTextNodeAsChild(Element element) {
			if( element.hasChildNodes() && element.getChildNodes().getLength() == 1 ) {
				Node childNode = element.getFirstChild();
				if(childNode.getNodeType() == Node.TEXT_NODE || childNode.getNodeType() == Node.CDATA_SECTION_NODE) {
					return true;
				}else {
					return false;
				}
			}
			return false;
		}
		
		public static Element copyAllAttributes(Element sourceElement, Element targetElement) {
			
			NamedNodeMap attributeMap = sourceElement.getAttributes();
			  for(int j=0;j<attributeMap.getLength();j++) {
				  if(attributeMap.item(j).getNodeName().equals("xsi:schemaLocation") ) {
					  continue;
				  }
				  targetElement.setAttribute(attributeMap.item(j).getNodeName(), attributeMap.item(j).getNodeValue());
			  }
			
			return targetElement;
		}
		
		public static Element getMatchingElement(Document doc, String elementName, String attributeName, String attributeValue) {
			
			NodeList list = doc.getElementsByTagName(elementName);
			for(int i=0; i<list.getLength(); i++) {
				Node node = list.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					if(node.getAttributes().getNamedItem(attributeName).getNodeValue().equals(attributeValue)) {
						return (Element) node;
					}
				}
			}
			return null;
		}

		
		@SuppressWarnings("finally")
		public static int complieAllJavaFilesInFolder(String path) {
			
			boolean isWindows = System.getProperty("os.name")
					  .toLowerCase().startsWith("windows");
			ProcessBuilder builder = new ProcessBuilder();
			builder.directory(new File(path));
			if (isWindows) {
			    builder.command("cmd.exe", "/c", "dir /s /B *.java > sources.txt");
			} else {
			    builder.command("sh", "-c", "find -name \"*.java\" > sources.txt");
			}
			try {
				Process process = builder.start();
				int exitCode = process.waitFor();
				if(isWindows)
					builder.command("cmd.exe", "/c", "javac @sources.txt");
				else
					builder.command("sh", "-c", "javac @sources.txt");
				process = builder.start();
				exitCode = process.waitFor();
				return exitCode;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				return -1;
			}
			
		}
		
		
		public static void deleteAllNonJavaFiles(File javaDir) {
			
			    File[] allContents = javaDir.listFiles();
			    if (allContents != null) {
			        for (File file : allContents) {
			        	if(file.isDirectory())
			        		deleteAllNonJavaFiles(file);
			        	else if (file.getName().endsWith(".class") || file.getName().endsWith(".txt")) {
							FileUtils.deleteQuietly(file);
			        	}
			        }
			    }
			
		}
		
		public static Element getConfigElementFromOtherXMLs(String nodeType, Element node) {
			Element configElement = null;
			String nodeName = node.getNodeName();
			for(int i=0; i < Transformer.nonGlobalMuleXMLPathList.size(); i++) {
				try {
					Document muleDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(Transformer.nonGlobalMuleXMLPathList.get(i)));
					NodeList configElementList = muleDoc.getElementsByTagName(nodeType+":connector");
					if(configElementList.getLength() == 0) {
						configElementList = muleDoc.getElementsByTagName(nodeType+":config");
					}
					String attributeName = "connector-ref";
					for (int j = 0; j < configElementList.getLength(); j++) {
						Element element = (Element) configElementList.item(j);
						
						if(element.getNodeName().contains(":config")) {
							attributeName = "config-ref";
						}
						if (element.getAttribute("name").equals(node.getAttribute(attributeName))) {
							if(XSLTMapper.scopesList.contains(element.getNodeName()))
								configElement = element;
							else
								configElement = (Element) element.cloneNode(true);
//							configElement = Utils.elementToString(element).toString();
//							System.out.println("config element :" + configElement);
//							elementWithConfig.append(configElement);
//							elementWithConfig.append(nodeString);

							break;
						}
					}
					if( null != configElement ) {
						if(nodeName.equals("http:inbound-endpoint") || nodeName.equals("https:inbound-endpoint") || nodeName.equals("http:outbound-endpoint") || nodeName.equals("https:outbound-endpoint")) {
							configElement = (Element) getConfigforHttpInOutBoundElement(muleDoc, node.getAttribute("connector-ref"));
							
						}
					}
					if(null != configElement)
						break;
					
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			
			return configElement; 
		}
		
		public static Node getConfigforHttpInOutBoundElement(Document doc, String connectorRef) {
			
			NodeList configElementList = null;
			Node configNode = null;
			configElementList = doc.getElementsByTagName("http:polling-connector");
			
			for (int i = 0; i < configElementList.getLength(); i++) {
				Element element = (Element) configElementList.item(i);
				
				if (element.getAttribute("name").equals(connectorRef)) {
					if(XSLTMapper.scopesList.contains(element.getNodeName()))
						configNode = element;
					else
						configNode = element.cloneNode(true);
					return configNode;

				}
			}
			configElementList = doc.getElementsByTagName("https:connector");
			
			for (int i = 0; i < configElementList.getLength(); i++) {
				Element element = (Element) configElementList.item(i);
				
				if (element.getAttribute("name").equals(connectorRef)) {
					if(XSLTMapper.scopesList.contains(element.getNodeName()))
						configNode = element;
					else
						configNode = element.cloneNode(true);
					return configNode;

				}
			}
			return null;
		}
	public static List<String> convertInpStreamToStr(InputStream inputStream) {
			try {
				//return IOUtils.toString(inputStream);
				return IOUtils.readLines(inputStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
}
