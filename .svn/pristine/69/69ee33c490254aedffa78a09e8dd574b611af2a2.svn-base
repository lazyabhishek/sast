package com.whishworks.unit.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.SAXException;


import com.whishworks.migrator.Migrator;
import com.whishworks.migrator.util.NameSpaceMapper;
import com.whishworks.migrator.util.XSLTMapper;

public class UnitTester {

	//String fileName = "http.xml";
	static HashMap<String,String> testResult = new HashMap<String,String>();
	
	
	String workingDir = System.getProperty("user.dir");
	
	// String workingDir = "C:/Users/smukala/Desktop/unit-testing";
	 
	String mule3XMLPath = workingDir + "/src/resources/UnitTest/Mule3XMLInputs/" ;

	String mule4TargetPath = workingDir + "/src/resources/UnitTest/Mule4XMLToolOutput/";

	String assertFilePath = workingDir + "/src/resources/UnitTest/Mule4ExpectedOutput/";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NameSpaceMapper.constructNameSpaceMapper();
		XSLTMapper mapper = new XSLTMapper();

		UnitTester tester = new UnitTester();
		List failedList = new ArrayList<>();
		
		
		File folder = new File(tester.mule3XMLPath);
		File[] listOfFiles = folder.listFiles();
		//for (File file : listOfFiles) {
		//	String fileName = file.getName();
		String fileName = "db_new.xml";

		try {
			Migrator.transformXML(tester.mule3XMLPath + fileName, tester.mule4TargetPath + fileName, false);
			assertExpectedOutput(tester.mule4TargetPath + fileName, tester.assertFilePath + fileName,fileName);
			
	            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			failedList.add(fileName);
			
		}

	//} 
		System.out.println("Unit test results : ");
		for (String name : testResult.keySet()) {
			  if(testResult.get(name).equals("failed"))
				System.out.println(name  + "   "+ testResult.get(name));
		}
		System.out.println(testResult.keySet().size());
		System.out.println(listOfFiles.length);
		//System.out.println(failedList);
		for (File failed : listOfFiles) {
			  if(!testResult.keySet().contains(failed.getName()))
				System.out.println(failed.getName());
		}
	}

	private static boolean assertExpectedOutput(String sourceXMLPath, String targetXMLPath,String fileName)
			throws FileNotFoundException {

		FileInputStream fis1 = new FileInputStream(sourceXMLPath);
		FileInputStream fis2 = new FileInputStream(targetXMLPath);

		BufferedReader source = new BufferedReader(new InputStreamReader(fis1));
		BufferedReader target = new BufferedReader(new InputStreamReader(fis2));

		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreAttributeOrder(true);
		XMLUnit.setNormalize(true);
		XMLUnit.setIgnoreComments(true);
		//XMLUnit.setNormalize(false);
		//XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
		XMLUnit.setNormalizeWhitespace(true);
		List differences;
		try {
			differences = compareXML(source, target, sourceXMLPath, targetXMLPath);
			printDifferences(differences,fileName);
			return true;
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public static List<Difference> compareXML(Reader source, Reader target, String sourceXMLPath,String targetXMLPath) throws SAXException, IOException {

		Diff xmlDiff = new Diff(source, target);
		//xmlDiff.overrideElementQualifier(new RecursiveElementNameAndTextQualifier());
		DetailedDiff detailXmlDiff = new DetailedDiff(xmlDiff);
		return detailXmlDiff.getAllDifferences();

	}

	public static void printDifferences(List<Difference> differences,String fileName) {
		int totalDifferences = differences.size();
		if ((totalDifferences == 1 &&  differences.get(0).getDescription().contains("xsi:schemaLocation")) || totalDifferences == 0) {
			System.out.println("=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=");
			System.out.println("Test passed");
			System.out.println("=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=");
			testResult.put(fileName, "passed");
		} else {
			System.out.println("===============================");
			System.out.println("Total differences : " + totalDifferences);
			System.out.println("================================");
			testResult.put(fileName, "failed");
			for (Difference difference : differences) {
				System.out.println(difference);
				System.out.println();
			}
		}
	} 

}

