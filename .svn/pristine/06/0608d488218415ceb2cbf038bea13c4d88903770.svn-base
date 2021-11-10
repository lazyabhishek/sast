package com.whishworks.migrator.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.whishworks.migrator.CodeMigrationUtils;
import com.whishworks.migrator.GlobalConfig;
import com.whishworks.migrator.Migrator;
import com.whishworks.report.generator.Issue;

public class CommonUtils {

	public static void transformExpressionComponent(Node child, Node transformedNode, Document finalDoc) {
		Node firstChild = child.getFirstChild();
		String content = "";
		if(null != firstChild ) {
			if(firstChild.getNodeType() == Node.CDATA_SECTION_NODE) {
				content = firstChild.getNodeValue();
			}else if(firstChild.getNodeType() == Node.TEXT_NODE){
				content = firstChild.getNodeValue();
			}
			else {
				content = child.getNodeValue();
			}
		}
		if(content != null) {
			Map<String, String> result = CodeMigrationUtils.migrateExpressionComponent(content);
			Set<String> keys = result.keySet();
			if(keys.size() > 0) {
				Element eeTransform = finalDoc.createElement("ee:transform");
				Element eeVariable = finalDoc.createElement("ee:variables");
				if(null != child.getAttributes().getNamedItem("doc:name"))
					eeTransform.setAttribute("doc:name", child.getAttributes().getNamedItem("doc:name").getNodeValue());
				else
					eeTransform.setAttribute("doc:name", "expression component to dataweave");
				boolean hasNonMigratedCode = false;
				for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
					
					String keyName = (String) iterator.next();
					if(!keyName.equals("nonMigartedCode")) {
						String value = (String) result.get(keyName);
						Element eeSetVariable = finalDoc.createElement("ee:set-variable");
						Node cDataSection = finalDoc.createCDATASection(value);
						eeSetVariable.setAttribute("variableName", keyName);
						eeSetVariable.appendChild(cDataSection);
						eeVariable.appendChild(eeSetVariable);
					}else {
						hasNonMigratedCode = true;
					}
				}
				eeTransform.appendChild(eeVariable);
				if(!hasNonMigratedCode) {
					Node importedTM = finalDoc.importNode(eeTransform, true);
					transformedNode.appendChild(importedTM);
				}else{
					Element expressionComponent = (Element) child.cloneNode(false);
					expressionComponent = (Element) finalDoc.importNode(expressionComponent, false);
					if(child.getAttributes().getNamedItem("doc:name") != null)
						expressionComponent.setAttribute("doc:name", child.getAttributes().getNamedItem("doc:name").getNodeValue());
					else
						expressionComponent.setAttribute("doc:name", "expression-component");
					Node cDataSection = finalDoc.createCDATASection(result.get("nonMigartedCode"));
					expressionComponent.appendChild(cDataSection);
					transformedNode.appendChild(finalDoc.importNode(expressionComponent, true));
					GlobalVariables.issueCount++;
					Migrator.flowIssueList.add(new Issue("uanble to transform :"+expressionComponent.getNodeName(), ""+child.getUserData("lineNumber"), Utils.elementToString(child)));
				}
		    }
		}else {
			Migrator.flowIssueList.add(new Issue("uanble to transform : expression component", ""+child.getUserData("lineNumber"), Utils.elementToString(child)));
		}
	}

public static Element migrateInvokeComponenet(Element child, Document doc, Document finalDoc) {
		
		Element springBean = Utils.getMatchingElement(GlobalConfig.globalConfigDoc, "spring:bean", "id", child.getAttributeNode("object-ref").getValue());
		if(null == springBean) {
			springBean = Utils.getMatchingElement(doc, "spring:bean", "id", child.getAttributeNode("object-ref").getValue());
		}
		
		if(null != springBean) {
			Element invokeComponent = finalDoc.createElementNS("http://www.mulesoft.org/schema/mule/java","java:invoke");
			
			if(null != child.getAttributeNode("object-ref"))
				invokeComponent.setAttribute("instance", child.getAttributeNode("object-ref").getValue());
			if(null != springBean.getAttributeNode("class"))
				invokeComponent.setAttribute("class", springBean.getAttributeNode("class").getValue());
			
			String methodName = "";
			if(null != child.getAttributeNode("method"))
				methodName = child.getAttributeNode("method").getValue();
			String argTypes = "";
			if(methodName != "") {
				File file = new File(com.whishworks.transformer.Transformer.sourcePath+"/src/main/java");
				
				try {
					URL url = file.toURI().toURL();
					URL[] urls = new URL[]{url};
					ClassLoader cl = new URLClassLoader(urls); 
					
					//springBean.getAttributeNode("class").getValue()
					Class c;
					try {
						c = cl.loadClass(springBean.getAttributeNode("class").getValue());
					}catch(ClassNotFoundException e) {
						String packageName = springBean.getAttributeNode("class").getValue().substring(0, springBean.getAttributeNode("class").getValue().lastIndexOf(".")).replace(".", "/");
						Utils.complieAllJavaFilesInFolder(file.getPath()+"/"+packageName);
						c = cl.loadClass(springBean.getAttributeNode("class").getValue());
					}
					Method[] methods = c.getDeclaredMethods();
					for(int m=0;m < methods.length; m++) {
						Method method = methods[m];
						if(method.getName().equals(methodName)) {
							Class[] parameters = method.getParameterTypes();
							for (int i = 0; i < parameters.length; i++) {
								Class class1 = parameters[i];
								argTypes = argTypes+class1.getSimpleName();
								if(i < parameters.length-1) {
									argTypes = argTypes+",";
								}
							}
						}
					}
					
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("couldn't load class for the invoke component");
					return child;
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
				invokeComponent.setAttribute("method", methodName+"("+argTypes+")");
			}
			if(null != child.getAttributeNode("methodArguments")) {
				String[] methodArgs = child.getAttributeNode("methodArguments").getValue().split(",");
				String m4MethodArgs = "#[{";
				for (int i = 0; i < methodArgs.length; i++) {
					String arg = methodArgs[i];
					if(arg.startsWith("#[") && arg.endsWith("]"))
						arg = CodeMigrationUtils.mel_to_dwl2(arg.substring(2,arg.length()-1));
					else
						arg = CodeMigrationUtils.mel_to_dwl2(arg);
					m4MethodArgs +=  "arg"+i +": " +arg;
					if(i < methodArgs.length-1) 
						m4MethodArgs += ","; 
				}
				m4MethodArgs += "}]";
				Element javaArg = finalDoc.createElementNS("http://www.mulesoft.org/schema/mule/java","java:args");
				CDATASection cData = finalDoc.createCDATASection(m4MethodArgs);
				javaArg.appendChild(cData);
				invokeComponent.appendChild(finalDoc.importNode(javaArg, true));
			}
			return invokeComponent;
		}
		return child;
	}

	public static String dwStringExpression(String str) {
		
		if(str.length() > 0) {
			return "'"+str+"' ++"; 
		}
		
		return str;
	}
	
	public static String migrateSQLQuery(String query) {
		query = query.trim();
		Pattern propAccess = Pattern.compile("#\\[(.*?)\\]");
		Matcher matcher = propAccess.matcher(query);
		int index =0;
		String migratedStr = "";
		while(matcher.find()) {
			migratedStr += query.substring(index, matcher.start())+"'$("+matcher.group(1)+")'";
			index = matcher.end();
		}
		if(!migratedStr.equals("")) {
			 migratedStr += query.substring(index);
			 return "#[\""+migratedStr+"\"]";
		} 
		return query;
	}

}
