package com.whishworks.migrator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.whishworks.report.generator.Report;

public class SecurePropertiesMigrator {

	public static List<String> secPropFileNames = new ArrayList<>();
	
	public static void replaceAllSecPropAccessors(String m4ProjectPath) {
		File muleDir = new File(m4ProjectPath+"/src/main/mule");
		File[] muleXMLs = muleDir.listFiles();
		Set secKeys = getAllSecKeys(m4ProjectPath);
		for (File file : muleXMLs) {
			BufferedReader reader = null;
			BufferedWriter writer = null;
			String originalFileContent = "";
			try {
				FileReader fReader = new FileReader(file.getAbsolutePath());
				if (null != fReader) {
					reader = new BufferedReader(fReader);
					String currentReadingLine = reader.readLine();
					while (currentReadingLine != null) {
						originalFileContent += currentReadingLine + System.lineSeparator();
						currentReadingLine = reader.readLine();
					}
					for (Object secKey : secKeys) {
						String stringKey = (String)secKey;
						if(originalFileContent.contains(stringKey))
							originalFileContent = originalFileContent.replace("${"+stringKey+"}", "${secure::"+stringKey+"}");
							originalFileContent = originalFileContent.replace("p('"+stringKey+"')", "p('secure::"+stringKey+"')");
					}
					writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
					writer.write(originalFileContent);
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
		
	}
	
	static Set getAllSecKeys(String m4ProjectPath) {
		Set secKeySet = new HashSet<String>();
		for (String propFileName : secPropFileNames) {
			if(propFileName.endsWith(".properties")) {
				if(propFileName.contains("${")) {
					if(propFileName.indexOf("${") != propFileName.lastIndexOf("${")) {
						System.out.println("#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#");
						System.out.println("      Input required       ");
						System.out.println("#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#");
						System.out.println("Please enter the exact file name, replacing '${...}' with corresponding value ( example: properties/api-dev.properties for properties/api-${mule.env}.properties) for "+propFileName+" :");
						Scanner sc = new Scanner(System.in);
						String exactFileName = sc.next();
						if(!exactFileName.startsWith(propFileName.substring(0,propFileName.indexOf("${")))) {
							System.out.println("The prfix is not matching the file name should contain same path as of "+propFileName+" ,enter again :");
							exactFileName = sc.next();
						}
						propFileName = exactFileName; 
					}else {
						String subDir = "";
						if(propFileName.contains("/") && propFileName.lastIndexOf("/") > -1) {
						    subDir = propFileName.substring(0, propFileName.lastIndexOf("/") );	
						}
						File dir = new File(m4ProjectPath+"/src/main/resources/"+subDir);
						FileFilter fileFilter = new WildcardFileFilter(propFileName.substring(propFileName.lastIndexOf("/")+1).replaceAll("\\$\\{.*?\\}", "*"));
						File[] files = dir.listFiles(fileFilter);
						for (int i = 0; i < files.length; i++) {
							if(files[i].getName().equals(propFileName.substring(propFileName.lastIndexOf("/")+1).replaceAll("\\$\\{.*?\\}", "common"))) {
								continue;
							}else {
								propFileName = subDir+"/"+files[i].getName();
								System.out.println("Assuming "+propFileName+ " as secure properties file");
								break;
							}
						   //System.out.println(files[i]);
						}
					}
				}
				Properties props = new Properties();
				try {
					props.load(new FileInputStream(new File(m4ProjectPath+"/src/main/resources/"+propFileName)));
					secKeySet.addAll(props.keySet());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Couldn't load properties file with name :"+propFileName);
					Report.addSecurePropertiesIssue(propFileName);
					//e.printStackTrace();
				}
			}
		}
		return secKeySet;
	}
	
}
