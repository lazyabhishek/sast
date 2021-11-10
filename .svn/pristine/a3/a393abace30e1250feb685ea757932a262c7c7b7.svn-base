package com.whishworks.project.creator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class ProjectLayoutGenerator {

	
	public static void generateM4ProjectLayout(String destination, String projectName) {
		
		clearDirIfExists(destination+"/"+projectName);
		unzip(destination);
		File extarcted = new File(destination+"/mule_4");
		String newName = destination+"/"+projectName;
		System.out.println(newName);
		File rename = new File(newName);
		extarcted.renameTo(rename);
		try {
			FileUtils.deleteDirectory(extarcted);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		modifyFile(newName+"/.project", "mule_4", projectName);
		modifyFile(newName+"/pom.xml", "mule_4", projectName);
	}

	public static void unzip(String destination) {
		 
		try {
			
			
			Class<ProjectLayoutGenerator> classLoader = ProjectLayoutGenerator.class;
			InputStream zipStream = classLoader.getResourceAsStream("/resources/mule_4.zip");
			File zip = new File("mule_4.zip");
			FileUtils.copyInputStreamToFile(zipStream, zip);
			ZipFile zipFile = new ZipFile(zip);
			zipFile.extractAll(destination);
			zip.deleteOnExit();
		} catch (ZipException | IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void clearDirIfExists(String destination) {
		File destinationDir = new File(destination);
		if(destinationDir.exists()) {
			try {
				FileUtils.deleteDirectory(destinationDir);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
 
	static void modifyFile(String filePath, String oldString, String newString) {
		File fileToBeModified = new File(filePath);
		String oldContent = "";
		BufferedReader reader = null;
		FileWriter writer = null;
		try {
			reader = new BufferedReader(new FileReader(fileToBeModified));
			// Reading all the lines of input text file into oldContent
			String line = reader.readLine();
			while (line != null) {
				oldContent = oldContent + line + System.lineSeparator();
				line = reader.readLine();
			}
			// Replacing oldString with newString in the oldContent
			String newContent = oldContent.replaceAll(oldString, newString);
			// Rewriting the input text file with newContent
			writer = new FileWriter(fileToBeModified);
			writer.write(newContent);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// Closing the resources
				reader.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
