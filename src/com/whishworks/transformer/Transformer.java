package com.whishworks.transformer;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.cryptlex.lexactivator.LexActivator;
import com.cryptlex.lexactivator.LexActivatorException;
import com.github.lalyos.jfiglet.FigletFont;
import com.whishworks.migrator.CodeMigrationUtils;
import com.whishworks.migrator.GlobalConfig;
import com.whishworks.migrator.MUnitMigrator;
import com.whishworks.migrator.Migrator;
import com.whishworks.migrator.PomMigrator;
import com.whishworks.migrator.SecurePropertiesMigrator;
import com.whishworks.migrator.SpringMigrator;
import com.whishworks.migrator.TransformedGlobalConfig;
import com.whishworks.migrator.util.XSLTMapper;
import com.whishworks.migrator.util.ConnectorDeatils;
import com.whishworks.migrator.util.NameSpaceMapper;
import com.whishworks.migrator.util.PercentageComputer;
import com.whishworks.migrator.util.Utils;
import com.whishworks.project.creator.ProjectLayoutGenerator;
import com.whishworks.report.generator.Report;
import com.whishworks.report.generator.ReportGenerator;

import freemarker.template.TemplateException;

public class Transformer {

	static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	static DocumentBuilder dBuilder;
	static List<String> globalConfigFileList = new ArrayList<String>();
	public static List<String> nonGlobalMuleXMLPathList = new ArrayList<String>();
	public static String sourcePath = "";
	
	public static void main(String[] args) {

		Class<Transformer> classLoader = Transformer.class;
		InputStream poropInp = classLoader.getResourceAsStream("/resources/project.properties");
		Properties prop = new Properties();
		try {
			prop.load(poropInp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		if (args.length == 1) {
			if (args[0].startsWith("-LicenseKey=") && args[0].length() > 12) {
				InputStream licenseInpStream = classLoader.getResourceAsStream("/resources/lic.txt");
				List<String> licenseContentLines = Utils.convertInpStreamToStr(licenseInpStream);
				 boolean isAcceptLicense = acceptLicense(licenseContentLines);
				 if(isAcceptLicense) {
					 activateLicense(args[0].replace("-LicenseKey=", ""), prop);
				 } 
			} else {
				System.out.println("Invalid license key argument format.");
			}
		}

		 boolean isValidLicense = validateLicense(prop); */
		
		 boolean isValidLicense = true;
		 

		if (isValidLicense) {

			checkForAllInputs(args);

			printAsciiArt(classLoader);

			buildNamespaceTable();

			System.out.println("source : " + args[0]);
			System.out.println("target : " + args[1]);

			XSLTMapper mapper = new XSLTMapper();
			ConnectorDeatils connectorDetails = new ConnectorDeatils();
			PercentageComputer pc = new PercentageComputer();

			String source = args[0].trim();
			String destination = args[1].trim();
			if (source.contains("\\")) {
				source = source.replace("\\", "/");
			}
			if (destination.contains("\\")) {
				destination = destination.replace("\\", "/");
			}
			sourcePath = source;
			String projectName = source.substring(source.lastIndexOf("/") + 1, source.length());
			System.out.println("project name : " + projectName);
			System.out.println("generating mule 4 project layout");
			System.out.println("successfully generated mule 4 app layout");

			ProjectLayoutGenerator.generateM4ProjectLayout(destination, projectName);

			migratePOM(source, destination, projectName);

			File toResourceFolder = new File(destination + "/" + projectName + "/src/main/resources");
			migrateResources(source, toResourceFolder);

			File muleAppFolder = new File(source + "/src/main/app");

			migrateGlobalConfigs(destination, projectName, muleAppFolder);
			migrateAppXmlAndProperties(destination, projectName, muleAppFolder);

			migrateMUnits(source, destination, projectName);

			if (SpringMigrator.springXMLDoc != null && SpringMigrator.springXMLDoc.getElementsByTagName("beans").item(0)
					.getChildNodes().getLength() > 1) {
				System.out.println("saving spring-config.xml :");
				try {
					Migrator.saveFinalDocument(SpringMigrator.springXMLDoc,
							destination + "/" + projectName + "/src/main/resources/spring-config.xml", true);
					Report.addSpringMigrationIssue("Your spring configuration might contain property key placeholder referring to properties in properties file. You have to import properties file to spring or hardcode the repective values.");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// TransformedGlobalConfig.doc.getElementsByTagName("mule").item(0)
				Element springConfigElement = TransformedGlobalConfig.doc.createElement("spring:config");
				springConfigElement.setAttribute("name", "springConfig");
				springConfigElement.setAttribute("doc:name", "Spring Config");
				springConfigElement.setAttribute("files", "spring-config.xml");
				Node importedSpringElement = TransformedGlobalConfig.doc.importNode(springConfigElement, true);
				TransformedGlobalConfig.doc.getElementsByTagName("mule").item(0).appendChild(importedSpringElement);
				((Element) TransformedGlobalConfig.doc.getElementsByTagName("mule").item(0))
						.setAttribute("xmlns:spring", "http://www.mulesoft.org/schema/mule/spring");
				String xsiLocationStr = ((Element) TransformedGlobalConfig.doc.getElementsByTagName("mule").item(0))
						.getAttribute("xsi:schemaLocation");
				((Element) TransformedGlobalConfig.doc.getElementsByTagName("mule").item(0))
						.setAttribute("xsi:schemaLocation", xsiLocationStr
								+ " http://www.mulesoft.org/schema/mule/spring http://www.mulesoft.org/schema/mule/spring/current/mule-spring.xsd ");

			}

			System.out.println("saving global config file :");
			try {
				// adding config for mule 3 default mule-app.properties
				Element muleAppProps = TransformedGlobalConfig.doc.createElement("configuration-properties");
				muleAppProps.setAttribute("file", "mule-app.properties");
				TransformedGlobalConfig.doc.getElementsByTagName("mule").item(0)
						.appendChild(TransformedGlobalConfig.doc.importNode(muleAppProps, true));
				Migrator.saveFinalDocument(TransformedGlobalConfig.doc,
						destination + "/" + projectName + "/src/main/mule/global-config.xml", true);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			migrateJavaCode(source, destination, projectName);

			migrateApiFolder(source, destination, projectName);

			migrateWSDL(source, toResourceFolder);

			migrateJavaTests(source, destination, projectName);

			migrateTestResources(source, destination, projectName);

			if (SecurePropertiesMigrator.secPropFileNames.size() > 0)
				SecurePropertiesMigrator.replaceAllSecPropAccessors(destination + "/" + projectName);

			System.out.println("successfully generated mule 4 application.");

			generateReport(destination, projectName);

		}
	}

	private static void migrateResources(String source, File toResourceFolder) {
		File fromResourceFolder = new File(source + "/src/main/resources");

		System.out.println("copying resource files");
		try {

			FileUtils.copyDirectory(fromResourceFolder, toResourceFolder);
			String[] extensions = { "dwl" };
			toResourceFolder.list();
			Iterator<File> it = FileUtils.iterateFiles(toResourceFolder, extensions, true);
			while (it.hasNext()) {
				File dwlFile = it.next();
				// FileUtils.readLines(dwlFile).size();
				PercentageComputer.dwlFileNLengthMap.put(dwlFile.getName(), FileUtils.readLines(dwlFile).size());
				String content = FileUtils.readFileToString(dwlFile, StandardCharsets.UTF_8);
				String filePath = dwlFile.getPath();
				dwlFile.delete();
				String migratedCode = CodeMigrationUtils.migrateDwl(content);
				File migratedDWFile = new File(filePath);
				migratedDWFile.createNewFile();
				FileUtils.writeStringToFile(migratedDWFile, migratedCode);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("resource folder doesn't exist");
		}
	}

	private static void migrateJavaTests(String source, String destination, String projectName) {
		File fromJavaTestFolder = new File(source + "/src/test/java");
		File toJavaTestFolder = new File(destination + "/" + projectName + "/src/test/java");
		System.out.println("copying java test folder");
		try {
			FileUtils.copyDirectory(fromJavaTestFolder, toJavaTestFolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("src/test/java folder doesn't exist");
		}
	}

	private static void migrateWSDL(String source, File toResourceFolder) {
		File wsdlFolder = new File(source + "/src/main/wsdl");
		System.out.println("copying wsdl files");
		try {
			FileUtils.copyDirectory(wsdlFolder, toResourceFolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("wsdl folder doesn't exist");
		}
	}

	private static void migrateTestResources(String source, String destination, String projectName) {
		FileFilter filter = new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				if (pathname.getName().contains("log4j2-test.xml")) {
					return false;
				}
				return true;
			}
		};
		File fromTestResourceFolder = new File(source + "/src/test/resources");
		File toTestReourceFolder = new File(destination + "/" + projectName + "/src/test/resources");
		System.out.println("copying test resources");
		try {
			FileUtils.copyDirectory(fromTestResourceFolder, toTestReourceFolder, filter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("src/test/resources folder doesn't exist");
		}
	}

	private static void migrateJavaCode(String source, String destination, String projectName) {
		File fromJavaFolder = new File(source + "/src/main/java");
		File toJavaFolder = new File(destination + "/" + projectName + "/src/main/java");
		System.out.println("copying java files");
		Utils.deleteAllNonJavaFiles(fromJavaFolder);
		try {

			FileUtils.copyDirectory(fromJavaFolder, toJavaFolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("java folder doesn't exist");
		}
	}

	private static void migrateMUnits(String source, String destination, String projectName) {
		File mUnitFolder = new File(source + "/src/test/munit");
		System.out.println("trasforming MUnit tests");
		if (mUnitFolder.exists()) {
			for (File mUnitFile : mUnitFolder.listFiles()) {
				try {
					MUnitMigrator.migrateMUnit(mUnitFile,
							destination + "/" + projectName + "/src/test/munit/" + mUnitFile.getName());
				} catch (TransformerFactoryConfigurationError | Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("munit folder doesn't exist");
				}
			}
		}
	}

	private static void migratePOM(String source, String destination, String projectName) {
		System.out.println("migrating pom.xml");
		try {
			PomMigrator.migratePomXML(destination + "/" + projectName + "/pom.xml", source + "/pom.xml");
		}catch( FileNotFoundException fnfEx) {
			System.out.println("There is no pom.xml, looks like it is not a maven project.");
		}catch (Exception e2) {
			// TODO Auto-generated catch block
			System.out.println("exception occured while migrating pom.xml");
			e2.printStackTrace();
		}
	}

	private static void buildNamespaceTable() {
		NameSpaceMapper.constructNameSpaceMapper();
	}

	private static void generateReport(String destination, String projectName) {
		System.out.println("generating migration report");

		Report report = new Report();

		Report.setProjectName(projectName);
		String reportSavePath = destination + "/" + projectName + "/report/report.html";
		try {
			ReportGenerator.generateReport(reportSavePath);
			System.out.println("successfully generated migration report (Chrome/Mozilla recommended)");
		} catch (IOException | TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void migrateApiFolder(String source, String destination, String projectName) {
		File fromApiFolder = new File(source + "/src/main/api");
		File toApiFolder = new File(destination + "/" + projectName + "/src/main/resources/api");
		System.out.println("copying api(RAML) files");
		try {
			FileUtils.copyDirectory(fromApiFolder, toApiFolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("api folder doesn't exist");
		}
	}

	private static void migrateGlobalConfigs(String destination, String projectName, File muleAppFolder) {
		System.out.println("finding all global config files");
		for (File fileEntry : muleAppFolder.listFiles()) {
			if (fileEntry.getName().endsWith(".xml")) {
				constructGlobalConfigFile(fileEntry);
			}
		}
		if (null == GlobalConfig.globalConfigDoc) {
			try {

				Class<Transformer> classLoader = Transformer.class;
				DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				TransformedGlobalConfig.doc = newDocumentBuilder
						.parse(classLoader.getResourceAsStream("/resources/mule4XMLFile.xml"));
				GlobalConfig.globalConfigDoc = newDocumentBuilder
						.parse(classLoader.getResourceAsStream("/resources/mule3XMLFile.xml"));
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Migrating global config file");
		try {
			Migrator.transformXML("", destination + "/" + projectName + "/src/main/mule/global-config.xml", true);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private static void migrateAppXmlAndProperties(String destination, String projectName, File muleAppFolder) {
		for (File fileEntry : muleAppFolder.listFiles()) {
			if (fileEntry.getName().endsWith(".xml") && !globalConfigFileList.contains(fileEntry.getName())) {
				System.out.println("Migrating : " + fileEntry.getName());

				// System.out.println(fileEntry.getAbsolutePath());
				try {
					Migrator.transformXML(fileEntry.getAbsolutePath(),
							destination + "/" + projectName + "/src/main/mule/" + fileEntry.getName(), false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fileEntry.getName().endsWith(".properties")) {
				System.out.println("Copying " + fileEntry.getName() + " to rescources");
				File resourcesDir = new File(destination + "/" + projectName + "/src/main/resources/");
				try {
					FileUtils.copyFileToDirectory(fileEntry, resourcesDir);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static void printAsciiArt(Class<Transformer> classLoader) {
		String asciiArt1 = "";
		String asciiArt2 = "";

		try {
			InputStream inp = classLoader.getResourceAsStream("/resources/figletfonts/doom.flf");
			asciiArt1 = FigletFont.convertOneLine(inp, "whishworks");
			inp = classLoader.getResourceAsStream("/resources/figletfonts/doom.flf");
			asciiArt2 = FigletFont.convertOneLine(inp, "M4M - ACCELERATOR");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(asciiArt1);
		System.out.println(asciiArt2);
	}

	static void constructGlobalConfigFile(File file) {
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			Element flowTag = (Element) doc.getElementsByTagName("flow").item(0);
			Element batchTag = (Element) doc.getElementsByTagName("batch:job").item(0);
			boolean isExceptionHandlingFile = false;
			boolean isGlobalFlowsFile = false;
			if (doc.getElementsByTagName("mule").getLength() > 0) {
				if (null != doc.getElementsByTagName("mule").item(0).getFirstChild().getNextSibling()) {
					isExceptionHandlingFile = (doc.getElementsByTagName("catch-exception-strategy").getLength() > 0
							|| doc.getElementsByTagName("apikit:mapping-exception-strategy").getLength() > 0
							|| doc.getElementsByTagName("choice-exception-strategy").getLength() > 0) ? true : false;
					isGlobalFlowsFile = doc.getElementsByTagName("sub-flow").getLength() > 0 ? true : false;
				}
				if (null == flowTag && null == batchTag && !isExceptionHandlingFile && !isGlobalFlowsFile) {
					GlobalConfig gc = new GlobalConfig();
					if (null == GlobalConfig.globalConfigDoc) {
						gc = new GlobalConfig(file);
					} else {
						gc.appendConfigToGlobalFile(doc);
					}
					Transformer.globalConfigFileList.add(file.getName());

				} else {
					Transformer.nonGlobalMuleXMLPathList.add(file.getAbsolutePath());
				}
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static void saveGlobalConfigDocument(Document newDoc, String targetFile) throws Exception {
		TransformerFactory tranFactory = TransformerFactory.newInstance();
		javax.xml.transform.Transformer transformer = tranFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		Source src = new DOMSource(newDoc);

		File output = new File(targetFile);
		System.out.println(targetFile);
		output.createNewFile();
		Result dest = new StreamResult(output);

		transformer.transform(src, dest);
	}

	static void checkForAllInputs(String[] args) {
		
		// this IF condition is only for KCS project purpose only, do not uncomment
		//if (args.length != 3 || null == args[0] || args[0].equals("") || null == args[1] || args[1].equals("")) {
		
		if (args.length != 2 || null == args[0] || args[0].equals("") || null == args[1] || args[1].equals("")) {
			System.out.println("Invalid input command. Required arguments are missing");
			System.out.println("1st argument - source path to Mule 3 project (the path should include project name)");
			System.out.println(
					"2nd argument - target folder, destination for Mule 4 migrated output (shouldn't contain project name)");
			// System.out.println("3rd argument - license key");
			System.out.println();
			// System.out.println("Example : java -jar WM4M_Tool_V-1.0.0
			// \"C:\\Users\\smukala\\AnypointStudio\\workspace-6.5.0\\demo-flights-api\"
			// \"D:\\Demo M4 Output\" \"2C2502-3614BB-44B1AF-41E38F-4A5103-ED6EC9\"");
			System.out.println(
					"Example : java -jar WM4M_Tool_V-1.0.0 \"C:\\Users\\jamesbond\\AnypointStudio\\workspace-6.5.0\\demo-flights-api\" \"D:\\Demo M4 Output\"");
			System.exit(0);
		}
		
		
		/**
		 * ===================================================================================
		 * added specifically for KCS project. pls comment it the below condition further.
		 * ===================================================================================
		 * 
		 */
		/*
		if(args.length != 3 || null == args[2] || args[2].equals("") ){
			System.out.println("Invalid input command. Required arguments are missing");
			System.out.println("3rd argument - Please provide license key");
			System.exit(0);
		}
		if(!args[2].equals("ASD01-ZXFR9-JKLOP")){
			System.out.println("Invalid license key");
			System.exit(0);
		}*/
		//**************************************************************************************
		
	}

	static boolean validateLicense(Properties prop) {

		
		int status = -2;
		try {
			
			LexActivator.SetProductData(prop.getProperty("product.data"));
			LexActivator.SetProductId(prop.getProperty("product.id"), LexActivator.LA_USER);

			status = LexActivator.IsLicenseGenuine();
			if (LexActivator.LA_OK == status) {
				System.out.println("License is genuinely activated!");
				return true;
			} else if (LexActivator.LA_EXPIRED == status) {
				System.out.println("License is genuinely activated but has expired!");
			} else if (LexActivator.LA_GRACE_PERIOD_OVER == status) {
				System.out.println("License is genuinely activated but grace period is over!");
			} else if (LexActivator.LA_SUSPENDED == status) {
				System.out.println("License is genuinely activated but has been suspended!");
			} else {
				System.out.println(
						"License is not activated. Inavlid license.");
			}
		} catch (LexActivatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	static void activateLicense(String licenseKey, Properties prop) {
		try {
			LexActivator.SetProductData(prop.getProperty("product.data"));
			LexActivator.SetProductId(prop.getProperty("product.id"), LexActivator.LA_USER);
			LexActivator.SetLicenseKey(licenseKey);
			int status = LexActivator.ActivateLicense();
			if (LexActivator.LA_OK == status || LexActivator.LA_EXPIRED == status
					|| LexActivator.LA_SUSPENDED == status) {
				System.out.println("License activated successfully");
				System.out.println("You are now authorized to use the tool.");
			} else {
				System.out.println("License activation failed");
			}
			System.exit(0);
		} catch (LexActivatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static boolean acceptLicense(List<String> licenseContentLines) {
		Scanner sc = new Scanner(System.in);
		int noOfLines = licenseContentLines.size();
		//System.out.println(licenseContent);
		String enter;
		int firstStop = noOfLines/3;
		int secondStop = (noOfLines*10)/15;
		System.out.println();
		for(int i=0; i<noOfLines; i++ ) {
			if(i == firstStop || i == secondStop) {
				System.out.print("Hit enter to continue ...");
				enter = sc.nextLine();
			}
			System.out.println(licenseContentLines.get(i));
		}
		System.out.println("Enter \"Y\" if you agree the terms and contions : ");
		String answer = sc.next();
		if(answer.equalsIgnoreCase("Y")) {
			return true;
		}
		return false;
	}
}
