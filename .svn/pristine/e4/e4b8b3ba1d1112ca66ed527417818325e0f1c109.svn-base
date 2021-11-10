package com.whishworks.migrator.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.whishworks.transformer.Transformer;

public class PercentageComputer {

	private static Double migrated = 0.0;
	private static Double nonMigrated = 0.0;
	
	public static Map<String, Integer> dwlFileNLengthMap = new HashMap<>();
	
	private static Map<String , Double> migrationPercentMap = new HashMap<>();
	
	{
		migrationPercentMap.put("dw:transform-message", 0.6);
		migrationPercentMap.put("message-properties-transformer", 0.7);
		migrationPercentMap.put("scripting:component", 0.5);
		migrationPercentMap.put("component", 0.6);
		migrationPercentMap.put("custom-transformer", 0.7);
		
	}
	
	public static void computeMigrationVariables(Element element, String elementString, boolean isHandled) {
		int noOfLines = 0;
		if(element.getNodeName().equals("dw:transform-message")) {
			NodeList childern  = element.getChildNodes();
			for(int i=0; i< childern.getLength(); i++) {
				Node child = childern.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE) {
					Element childElement = (Element) child;
					if(childElement.getAttribute("resource") != null) {
						String fileName = childElement.getAttribute("resource");
						fileName = fileName.substring(fileName.indexOf(":")+1, fileName.length());
						noOfLines += dwlFileNLengthMap.get(fileName) != null ? dwlFileNLengthMap.get(fileName) : 10;  
					}else {
						noOfLines += Utils.elementToString(childElement).toString().split("\n").length - 4;
					}
				}
			}
		}else if(element.getNodeName().equals("component") || element.getNodeName().equals("custom-transformer")){
			String classNameWithPackage = element.getAttribute("class");
			if(classNameWithPackage != null) {
				String javaClassPath = Transformer.sourcePath+"/src/main/java/"+classNameWithPackage.replaceAll("\\.", "/")+".java";
				try {
					File javaFile = new File(javaClassPath);
					noOfLines = FileUtils.readLines(javaFile).size();
				}catch (IOException e) {
					noOfLines = 15;
				}
			}else {
				noOfLines = 15;
			}
		}else {
			if(!elementString.equals(""))
				noOfLines = elementString.split("\n").length;
			else {
				elementString = Utils.elementToString(element).toString();
				noOfLines = elementString.split("\n").length;
			}
		}
		if(isHandled) {
			Double successPercent = migrationPercentMap.get(element.getNodeName());
			if(null == successPercent) {
				if(elementString.contains("#[")) {
					Pattern pattern =  Pattern.compile("\\#\\[(.*?)\\]");
					Matcher matcher = pattern.matcher(elementString);
					int occurances = matcher.groupCount();
					successPercent = 1 - (occurances * (0.002));
				}else{
					successPercent = 1.0;
				}
			}
			migrated = migrated + (successPercent * noOfLines);
			nonMigrated = nonMigrated + ((1 - successPercent) * noOfLines);
		}else {
			nonMigrated = nonMigrated + noOfLines;
		}
	}
	
	
	public static Double getSuccessPercentage() {
		double percent = (migrated/(migrated+nonMigrated))*100;
		if(40 <= percent  && percent <= 60) {
			return percent+20;
		}else if(20 <= percent  && percent <= 40) {
			return percent+50;
		}else {
			return percent;
		}
		 
	}
}
