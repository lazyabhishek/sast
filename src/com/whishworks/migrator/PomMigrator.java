package com.whishworks.migrator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.whishworks.migrator.util.PomArtifactMapper;
import com.whishworks.migrator.util.Utils;
import com.whishworks.report.generator.Report;

public class PomMigrator {

	// Create XPathFactory object
	static XPathFactory xpathFactory = XPathFactory.newInstance();
	// Create XPath object
	static XPath xpath = xpathFactory.newXPath();
	static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

	static String m4PomLocation;

	public static void migratePomXML(String m4PomLocation, String m3PomLocation) throws Exception {
		PomArtifactMapper.init();
		PomMigrator.m4PomLocation = m4PomLocation;
		File m4Pom = new File(m4PomLocation);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document m4PomDoc = dBuilder.parse(m4Pom);

		Class<PomMigrator> classLoader = PomMigrator.class;

		InputStream dependenciesXML = classLoader.getResourceAsStream("/resources/dependencies.xml");
		InputStream sharedLibrariesXML = classLoader.getResourceAsStream("/resources/sharedLibraries.xml");
		Document dependenciesXMLDoc = dBuilder.parse(dependenciesXML);
		Document sharedLibrariesXMLDoc = dBuilder.parse(sharedLibrariesXML);
		try {
			File m3Pom = new File(m3PomLocation);
			Document m3PomDoc = dBuilder.parse(m3Pom);
			m4PomDoc.getElementsByTagName("groupId").item(0)
					.setTextContent(m3PomDoc.getElementsByTagName("groupId").item(0).getTextContent());
			Document updatedPom = createDependencies(m4PomDoc, dependenciesXMLDoc, sharedLibrariesXMLDoc, m3PomDoc);
			saveDoc(updatedPom, m4Pom);
		} catch (FileNotFoundException exception) {
			System.out.println("there is no pom.xml in mule 3.x project to migrate");
		}

	}

	private static Document createDependencies(Document m4PomDoc, Document dependenciesXMLDoc,
			Document sharedLibrariesXMLDoc, Document m3PomDoc) {
		// List<String> removeStrings = Stream.of("mule", "core", "connector",
		// "transport", "module").collect(Collectors.toList());
		boolean hasSpringDependency = false;
		Node m3DependenciesNode = m3PomDoc.getElementsByTagName("dependencies").item(0);
		// Node
		// m3SharedLibrariesNode=m3PomDoc.getElementsByTagName("sharedLibraries").item(0);
		NodeList dependencyList = m3DependenciesNode.getChildNodes();
		// NodeList sharedLibraryList=m3SharedLibrariesNode.getChildNodes();
		for (int i = 0; i < dependencyList.getLength(); i++) {
			Node dependency = dependencyList.item(i);
			if (dependency.getNodeType() == Node.ELEMENT_NODE && dependency.getNodeName().equals("dependency")) {
				String artifactId = getArtifactId(dependency).trim();
				String targetArtifactId = PomArtifactMapper.artifactMapper.get(artifactId);
				if (artifactId.equals("mule-transport-jdbc-ee")) {
					System.out.println();
				}
				NodeList matchingNodes = null;
				if (!checkIsMuleDependency(dependency)) {
					if (!checkIfDependencyExists(m4PomDoc, dependency)) {
						System.out.println(
								"the dependency doesn't belong to mule/mulesoft might be our custom dependency or thirdpart so copying it as is.");
						Node importedDepedency = m4PomDoc.importNode(dependency, true);
						// Node importedSharedLibrary=m4PomDoc.importNode(sharedLibrary, deep)
						m4PomDoc.getElementsByTagName("dependencies").item(0).appendChild(importedDepedency);
					}
				} else {
					if (!PomArtifactMapper.skipArtifacts.contains(artifactId)) {
						if (null != targetArtifactId) {
							matchingNodes = getMule4DependencyElements(targetArtifactId, dependenciesXMLDoc);
						} else {
							if (!artifactId.contains("spring") && !artifactId.contains("munit")) {
								System.out
										.println("no mapping found for " + artifactId + " in the pom artifact mapper");
								Report.addPomMigrationIssue(Utils.elementToString(dependency).toString());
							}
						}
						if (artifactId.contains("spring") && !artifactId.equals("mule-module-spring-config-ee")) {
							matchingNodes = getMule4DependencyElements("spring", dependenciesXMLDoc);
							hasSpringDependency = true;
						}
						if (null != matchingNodes && matchingNodes.getLength() > 0) {
							int countOfMatchingNodes = matchingNodes.getLength();
							for (int count = 0; count < countOfMatchingNodes; count++) {
								if (!checkIfDependencyExists(m4PomDoc, matchingNodes.item(count))) {
									Node importedDepedency = m4PomDoc.importNode(matchingNodes.item(count), true);
									m4PomDoc.getElementsByTagName("dependencies").item(0)
											.appendChild(importedDepedency);
								}
							}
						} else {
							if (!artifactId.contains("munit")) {
								System.out.println("no matching depenedency found for " + artifactId);
								Report.addPomMigrationIssue(Utils.elementToString(dependency).toString());
							}
						}

					}
				}
			}
		}
		if (hasSpringDependency) {
			XPathExpression expr;
			Node sharedLibrary = sharedLibrariesXMLDoc.getElementsByTagName("sharedLibraries").item(0);
			try {
				expr = xpath.compile("project/build/plugins/plugin/configuration");
				NodeList nodes = (NodeList) expr.evaluate(m4PomDoc, XPathConstants.NODESET);
				Node importedSharedLib = m4PomDoc.importNode(sharedLibrary, true);
				nodes.item(0).appendChild(importedSharedLib);
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				hasSpringDependency = false;
			}
		}
		return m4PomDoc;
	}

	private static String getArtifactId(Node node) {
		String artifactValue = "";
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node element = childNodes.item(i);
			if (element.getNodeName().equals("artifactId")) {
				return element.getTextContent();
			}
		}
		return artifactValue;
	}

	private static String getGroupId(Node node) {
		String groupId = "";
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node element = childNodes.item(i);
			if (element.getNodeName().equals("groupId")) {
				return element.getTextContent();
			}
		}
		return groupId;
	}

	private static NodeList getMule4DependencyElements(String artifactId, Document dependenciesXMLDoc) {

		try {
			XPathExpression expr = xpath.compile("/dependencies/dependency[artifactId = '" + artifactId + "']");
			if (artifactId.contains("spring")) {
				expr = xpath.compile("/dependencies/dependency[contains(artifactId, 'spring')]");
			}
			NodeList nodes = (NodeList) expr.evaluate(dependenciesXMLDoc, XPathConstants.NODESET);
			return nodes;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static boolean checkIfDependencyExists(Document doc, Node node) {
		String artifactId = getArtifactId(node);
		try {
			XPathExpression expr = xpath
					.compile("/project/dependencies/dependency[contains(artifactId,'" + artifactId + "')]");
			NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			if (nodes.getLength() > 0)
				return true;
			else
				return false;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static boolean checkIsMuleDependency(Node dependency) {
		String groupId = getGroupId(dependency);
		if (groupId.contains("mule") || groupId.contains("mulesoft")) {
			return true;
		}
		return false;
	}

	private static void saveDoc(Document newDoc, File m4Pom) throws Exception {
		TransformerFactory tranFactory = TransformerFactory.newInstance();
		Transformer aTransformer = tranFactory.newTransformer();
		Source src = new DOMSource(newDoc);
		aTransformer.transform(src, new StreamResult(m4Pom));
	}

	public static void addDependencyToPom(String artifactID)
			throws ParserConfigurationException, SAXException, IOException {
		File m4Pom = new File(m4PomLocation);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document m4PomDoc = dBuilder.parse(m4Pom);
		Class<PomMigrator> classLoader = PomMigrator.class;
		InputStream dependenciesXML = classLoader.getResourceAsStream("/resources/dependencies.xml");
		Document dependenciesXMLDoc = dBuilder.parse(dependenciesXML);
		NodeList dependecyList = getMule4DependencyElements(artifactID, dependenciesXMLDoc);
		if (dependecyList.getLength() > 0) {
			Node importedDepedency = m4PomDoc.importNode(dependecyList.item(0), true);
			m4PomDoc.getElementsByTagName("dependencies").item(0).appendChild(importedDepedency);
			try {
				saveDoc(m4PomDoc, m4Pom);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Couldn't save pom.xml, while adding dependency with artifactId " + artifactID);
			}
		}

	}
}
