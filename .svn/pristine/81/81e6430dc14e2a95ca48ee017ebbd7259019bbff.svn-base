package com.whishworks.migrator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeMigrationUtils {


	public static Map<String,String> migrateExpressionComponent(String inputCode) {

		String code = inputCode.trim().replace("<![CDATA[","").replace("]]>","");
		Map<String, String> result = new HashMap<String, String>();
		String lines[] = code.split("\\r?\\n");
		for (int i=0; i< lines.length; i++) {
			String c = lines[i];
			if (c.trim().split("=").length == 2 ) {
				String lhs  = c.trim().split("=")[0].trim();
				String rhs  = c.trim().split("=")[1].trim();
				if(rhs.endsWith(";")) {
					rhs = rhs.replace(";", "");
				}
				result.put(mel_to_dwl2(getVariableNameFromLHS(lhs)), mel_to_dwl2(rhs));
			}else {
				StringBuffer nonMigratedCode = new StringBuffer(c);
				for(int j=i+1; j< lines.length; j++) {
					nonMigratedCode.append("\n");
					nonMigratedCode.append(lines[j]);
				}
				result.put("nonMigartedCode", nonMigratedCode.toString());
				break;
			}
		}
		return result;
	}
	
	private static String getVariableNameFromLHS(String lhs) {
		
		if(lhs.startsWith("flowVars")) {
			if(lhs.startsWith("flowVars['") && lhs.endsWith("']")) {
				lhs = lhs.substring(10,lhs.length()-2);
			}else {
				lhs = lhs.substring(9,lhs.length());
			}
		}else if(lhs.startsWith("sessionVars")) {
			if(lhs.startsWith("sessionVars['") && lhs.endsWith("']")) {
				lhs = lhs.substring(13,lhs.length()-2);
			}else {
				lhs = lhs.substring(12,lhs.length());
			}
		}
		
		
		return lhs;
	}





	public static String mel_to_dwl2(String melCode){

		String temp = melCode;

		
		temp = temp.replace("flowVars" ,"vars");
		temp = temp.replace("sessionVars" , "vars");
		if(temp.contains("message.inboundProperties.'http.query.params'.get(")) {
			temp = migarteMessageQueryParamsGet(temp);
		}
		if(temp.contains("message.inboundProperties.'http.uri.params'.get(")) {
			temp = migarteMessageUriParamsGet(temp);
		}
		temp = temp.replace("message.inboundProperties.'http.uri.params'", "attributes.uriParams");
		temp = temp.replace("message.inboundProperties.'http.query.params'", "attributes.queryParams");
		temp = temp.replace("message.inboundProperties", "attributes.headers");
		temp = temp.replace("message.outboundProperties.'http.uri.params'", "attributes.uriParams");
		temp = temp.replace("message.outboundProperties.'http.query.params'", "attributes.queryParams");
		temp = temp.replace("message.outboundProperties", "attributes.headers");
		//temp = temp.replace("message.outboundProperties", "attributes");
		//temp = temp.replace("record", "vars['record']");
		temp = temp.replace("record.recordVars", "vars");
		//temp = temp.replace("'http.uri.params'", "uriParams");
		//temp = temp.replace("'http.query.params'", "queryParams");
		temp = temp.replace("http.status", "httpStatus");
		temp = temp.replace(".toString()", " as String");
		temp = temp.replace("exception.getCause().getMessage()", "error.cause.message");
		temp = temp.replace("exception.getCause()", "error.cause");
		temp = temp.replace("exception" , "error");
		temp = temp.replace(".error", ".exception");
		temp = temp.replace("groovy:message.getExceptionPayload().getRootException().getMessage()", "error.exception.cause.target.detailMessage");
		temp = temp.replace("message.payloadAs(java.lang.String)", "write(payload, 'application/json')");
		temp = temp.replace("message.payloadAs(String)", "write(payload, 'application/json')");
		temp = temp.replace("message.id", "correlationId");
		//temp = temp.replace("payload", "write(payload, 'application/json')");
		temp = temp.replace("||", " or ");
		temp = temp.replace("&&", " and ");
		temp = temp.replace("&amp;&amp;", " and ");
		temp = temp.replace("context:serviceName", "mule.muleContext.componentLocator.componentLocations[1].name");
		temp = temp.replace("java.util.UUID.randomUUID()", "uuid()");
		temp = temp.replace("http.remote.address", "remoteAddress");
		//temp = temp.replace("${", "'${");
		//temp = temp.replace("}", "}'");
		//&& !temp.contains("'${") && !temp.contains("&quot;${") && !temp.contains("\"${")
		//temp = temp.replace("<", "&lt;");
		//temp = temp.replace(">", "&gt;");
		if(temp.contains("'${") && temp.contains("}'") ) {
			temp = transformSingleQuotePropAcess(temp);
		}
		if(temp.contains("${") && temp.contains("}") ) {
			temp = transformPropAcess(temp);
		}
		if( temp.contains(".is(eq") ) {
			temp = replaceIsEq(temp);  
		}
		if( temp.contains(".replace(")) {
			temp = replaceReplace(temp);
		}
		if( temp.contains(".replaceAll(")) {
			temp = replaceReplaceAll(temp);
		}
		temp = temp.replace("message.dataType" , "dataType");
		temp = temp.replace("now" , "now()");
		temp = temp.replace("server.dateTime" , "now()");
		temp = temp.replace("now().withTimeZone('UTC')", "now() as DateTime");
		if(temp.contains("?") && temp.contains(":")) {
			temp = temp.replace("(", "");
			temp = temp.replace(")", "");
			temp = transformTernary(temp);
		}
		if (temp.contains(".causedBy(") ){
			//  errors.add("Cannot migrate exception.causedBy\n");
			temp = migrateExceptionCausedBy(temp);
			//System.out.println("Cannot migrate exceptioncausedBy");
		}
		if(temp.contains(".causeMatches(")) {
			temp = migrateCauseMatches(temp);
		}
		if(temp.contains("xpath3(")) {
			temp = transformXpath3(temp);
		}
		if(temp.contains("json:")) {
			temp = transformJsonExpr(temp);
		}
		if(temp.contains("#[") && temp.contains("]") ){
			temp = transformHashSquaredValue(temp);
		}
		
		if(temp.contains("-")) {
			temp = migrateHypenSeparatedName(temp);
		}
		
		if(temp.contains("getResource(")) {
			temp = migarteGetResourceAsByteArrayMunit(temp);
			temp = migarteGetResourceAsStringMunit(temp);
		}
		if(temp.contains(".format(")) {
			temp = migrateDateFormat(temp);
		}
		if(temp.contains(".?")) {
			temp = migrateDotQuestionMarkOperator(temp);
			temp = temp.replace("error.message", "error.cause.message");
		}
		
		return temp;	
	}



	private static String migarteGetResourceAsByteArrayMunit(String text) {
		Pattern jsonExpr = Pattern.compile("getResource(.*?)\\.asByteArray\\(\\)");
		Matcher matcher = jsonExpr.matcher(text);
		int index =0;
		String migratedStr = "";
		while(matcher.find()) {		
				migratedStr += text.substring(index, matcher.start())+"MunitTools::getResourceAsByteArray"+matcher.group(1);
				index = matcher.end();
		}
		if(!migratedStr.equals("")) {
		 migratedStr += text.substring(index);
		 return migratedStr;
		} 
		return text;
	}
	
	private static String migarteGetResourceAsStringMunit(String text) {
		Pattern jsonExpr = Pattern.compile("getResource(.*?)\\.asString\\(\\)");
		Matcher matcher = jsonExpr.matcher(text);
		int index =0;
		String migratedStr = "";
		while(matcher.find()) {		
				migratedStr += text.substring(index, matcher.start())+"MunitTools::getResourceAsString"+matcher.group(1);
				index = matcher.end();
		}
		if(!migratedStr.equals("")) {
		 migratedStr += text.substring(index);
		 return migratedStr;
		} 
		return text;
	}
	
	private static String migarteMessageQueryParamsGet(String text) {
		Pattern jsonExpr = Pattern.compile("message.inboundProperties.'http.query.params'.get\\((.*?)\\)");
		Matcher matcher = jsonExpr.matcher(text);
		int index =0;
		String migratedStr = "";
		while(matcher.find()) {		
				migratedStr += text.substring(index, matcher.start())+"attributes.queryParams."+matcher.group(1);
				index = matcher.end();
		}
		if(!migratedStr.equals("")) {
		 migratedStr += text.substring(index);
		 return migratedStr;
		} 
		return text;
	}
	
	private static String migarteMessageUriParamsGet(String text) {
		Pattern jsonExpr = Pattern.compile("message.inboundProperties.'http.uri.params'.get\\((.*?)\\)");
		Matcher matcher = jsonExpr.matcher(text);
		int index =0;
		String migratedStr = "";
		while(matcher.find()) {		
				migratedStr += text.substring(index, matcher.start())+"attributes.uriParams."+matcher.group(1);
				index = matcher.end();
		}
		if(!migratedStr.equals("")) {
		 migratedStr += text.substring(index);
		 return migratedStr;
		} 
		return text;
	}

	private static String transformTernary(String text) {
		// TODO Auto-generated method stub
		
		Pattern pattern = Pattern.compile("(.*?)\\?(.*?):(.*)");
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {		
			if(matcher.group(3).contains("?") && matcher.group(3).contains(":")) {
				return "if (" + matcher.group(1) + ") " + matcher.group(2) + " else " + transformTernary(matcher.group(3));
			}else {
				return "if (" + matcher.group(1) + ") " + matcher.group(2) + " else " + matcher.group(3);
			}
		}
		return text;
	}
	
	private static String transformXpath3(String text) {
		Pattern propAccess = Pattern.compile("xpath3\\((.*?),(.*?),(.*?)\\)");
		Matcher matcher = propAccess.matcher(text);
		int index =0;
		String migratedStr = "";
		while(matcher.find()) {
			if(!text.substring(0, matcher.start()).endsWith("'") && !text.substring(matcher.end()).startsWith("'")) {
				migratedStr += text.substring(index, matcher.start())+"XmlModule::xpath("+matcher.group(1)+","+matcher.group(2)+",{})";
				index = matcher.end();
			}	
		}
		if(!migratedStr.equals("")) {
			migratedStr += text.substring(index);
			return migratedStr;
		}
		return text;
	}
	
	private static String migrateExceptionCausedBy(String text) {
		Pattern propAccess = Pattern.compile(".causedBy\\((.*?)\\)");
		Matcher matcher = propAccess.matcher(text);
		int index =0;
		String migratedStr = "";
		while(matcher.find()) {
			migratedStr += text.substring(index, matcher.start())+".exception.class == '" + matcher.group(1) + "'";
			index = matcher.end();
		}
		if(!migratedStr.equals("")) {
		 migratedStr += text.substring(index);
		 return migratedStr;
		} 
		return text;
	}
	
	private static String migrateDateFormat(String text) {
		Pattern propAccess = Pattern.compile(".format\\((.*?)\\)");
		Matcher matcher = propAccess.matcher(text);
		int index =0;
		String migratedStr = "";
		while(matcher.find()) {
			migratedStr += text.substring(index, matcher.start())+" {format: " + matcher.group(1) + "}";
			index = matcher.end();
		}
		if(!migratedStr.equals("")) {
		 migratedStr += text.substring(index);
		 return migratedStr;
		} 
		return text;
	}
	private static String migrateDotQuestionMarkOperator(String text) {
		Pattern propAccess = Pattern.compile("\\s?(.*?)\\.\\?(.*)[\\s\\n\\r\\t]?");
		Matcher matcher = propAccess.matcher(text);
		int index =0;
		String migratedStr = "";
		while(matcher.find()) {
			migratedStr += text.substring(index, matcher.start())+" if(" + matcher.group(1)+"."+matcher.group(2) + " != null) "+matcher.group(1)+"."+matcher.group(2)+ " else null ";
			index = matcher.end();
		}
		if(!migratedStr.equals("")) {
		 migratedStr += text.substring(index);
		 return migratedStr;
		} 
		return text;
	}
	
	private static String migrateCauseMatches(String text) {
		Pattern propAccess = Pattern.compile(".causeMatches\\((.*?)\\)");
		Matcher matcher = propAccess.matcher(text);
		int index =0;
		String migratedStr = "";
		while(matcher.find()) {
			migratedStr += text.substring(index, matcher.start())+".exception.class contains " + matcher.group(1).replace(".*", "") ;
			index = matcher.end();
		}
		if(!migratedStr.equals("")) {
		 migratedStr += text.substring(index);
		 return migratedStr;
		} 
		return text;
	}
	
	private static String transformHashSquaredValue(String text) {
		
		Pattern pattern = Pattern.compile("\\#\\[(.*?)\\]");
		Matcher matcher = pattern.matcher(text);
		int index =0;
		String migratedStr = "";
		while(matcher.find()) {		
			migratedStr += text.substring(index, matcher.start())+"'${" + matcher.group(1) + "}'";
			index = matcher.end();
		}
		if(!migratedStr.equals("")) {
		 migratedStr += text.substring(index);
		 return migratedStr;
		} 
		return text;
	}
	
	private static String transformJsonExpr(String text) {
		Pattern jsonExpr = Pattern.compile("(json:)([\\w\\.\\\\\\/]*)(\\s|\\]|\\\"\\')?");
		Matcher matcher = jsonExpr.matcher(text);
		int index =0;
		String migratedStr = "";
		while(matcher.find()) {		
			if(!text.substring(index, matcher.start()).endsWith("'") && !text.substring(matcher.end()).startsWith("'")) {
				String updatedQuery = matcher.group(2).replaceAll("//", ".").replaceAll("/", ".");
				if(updatedQuery.startsWith("."))
					migratedStr += text.substring(index, matcher.start())+"payload"+updatedQuery;
				else
					migratedStr += text.substring(index, matcher.start())+"payload."+updatedQuery;
				index = matcher.end();
			}	
		}
		if(!migratedStr.equals("")) {
		 migratedStr += text.substring(index);
		 return migratedStr;
		} 
		
		return text;
	}

	private static String transformSingleQuotePropAcess(String text) {
		Pattern propAccess = Pattern.compile("'\\$\\{(.*?)\\}'");
		Matcher matcher = propAccess.matcher(text);
		int index =0;
		String migratedStr = "";
		while(matcher.find()) {	
				migratedStr += text.substring(index, matcher.start())+"p('"+matcher.group(1)+"')";
			    index = matcher.end();
		}
		if(!migratedStr.equals("")) {
		 migratedStr += text.substring(index);
		 return migratedStr;
		} 
		return text;
	}
	
	private static String transformPropAcess(String text) {
		Pattern propAccess = Pattern.compile("\\$\\{(.*?)\\}");
		Matcher matcher = propAccess.matcher(text);
		int index =0;
		String migratedStr = "";
		while(matcher.find()) {	
				migratedStr += text.substring(index, matcher.start())+"p('"+matcher.group(1)+"')";
			    index = matcher.end();
		}
		if(!migratedStr.equals("")) {
		 migratedStr += text.substring(index);
		 return migratedStr;
		} 
		return text;
	}
	
	private static String migrateDWPropAcess(String text) {
		Pattern propAccess = Pattern.compile("\\\"\\$\\{(.*?)\\}\\\"");
		Matcher matcher = propAccess.matcher(text);
		int index =0;
		String migratedStr = "";
		while(matcher.find()) {
			migratedStr += text.substring(index, matcher.start())+"p('"+matcher.group(1)+"')";
			index = matcher.end();
		}
		if(!migratedStr.equals("")) {
			 migratedStr += text.substring(index);
			 return migratedStr;
		} 
		return text;
		
	}




	public static String migrateDwl(String dwl) {


		String code = dwl;


		if ( code.split("---").length ==2 ) {


			code = code.replace("%dw 1.0","%dw 2.0");

			String header = code.split("---")[0];
			String body = code.split("---")[1];
			header = header.replace("%input","input");
			header = header.replace("%output","output");
			header = header.replace("%var","var");
			//header = header.replace("%function", "fun");
			header = migrateFunctionDef(header);
			header = header.replace("%namespace", "ns");
			header = header.replaceAll("%type", "type");
			body =	header +"---" + body;
			body = body.replace(":string", "String");
			body = body.replace(":number", "Number");
			body = body.replace(":object", "Object");
			body = body.replace(":boolean", "Boolean");
			body = body.replace(":cdata", "CData");
			body = body.replace("flowVars" ,"vars");
			body = body.replace("sessionVars" , "vars");
			body = body.replace("message.inboundProperties", "attributes");
			body = body.replace("message.outboundProperties", "attributes");
			body = body.replace("inboundProperties", "attributes");
			body = body.replace("outboundProperties", "attributes");
			body = body.replace("http.status", "httpStatus");
			body = body.replace("-&gt;", "->");
			body = body.replace("exception" , "error");
			body = body.replace("\"error\"", "\"exception\"");
			body = body.replace("java.util.UUID.randomUUID()", "uuid()");
			body = body.replace("message.dataType" , "dataType");
			body = body.replace("now", "now()");
			body = body.replace(":date", "Date");
			body = body.replace("server.dateTime" , "now()");
			body = body.replace("now().withTimeZone('UTC')", "now() as DateTime");
			body = body.replace("payload is :empty", "isEmpty(payload)");		
			body = body.replace("&lt;", "<");
			body = body.replace("&gt;", ">");
			if(body.split("\\[\\d*(..)\\d*\\]").length > 0){
				body = migrateSubString(body);
			}
			
			if(body.contains("-")) {
				body = migrateHypenSeparatedName(body);
			}
			//if dw code is accessing properties value with "${prop_name}"
			if(body.contains("\"${")) {
				body = migrateDWPropAcess(body);
			}
			if(body.contains(".format(")) {
				body = migrateDateFormat(body);
			}
			
			//body = replaceMatches(body,"([\\'|\\\"|\\.|\\w|\\[|\\]]+)\\s+when(.*?)otherwise\\s+([\\\\'|\\\\\\\"|\\\\.|\\\\w|\\\\[|\\\\]]+)");

			code = body;

		}
		else {
			System.out.println("Error migrating Dwl");
		}


		if( code.contains("when") && code.contains("otherwise")) {

				code = replaceMatches(code,"(.*?)\\swhen\\s(.*?)\\sotherwise");
		}
		if( code.contains("when") ) {
				code = transformOnlyWhen(code);
		}


		return code;
	}

	public static String replaceMessagePropertiesAccess(String script) {
		
		script = script.replace("flowVars" ,"vars");
		script = script.replace("sessionVars" , "vars");
		script = script.replace("message.inboundProperties", "attributes");
		script = script.replace("&amp;&amp;", "&&");
		script = script.replaceAll("-&gt;", "->");
		
		return script;
	}

	static String migrateSubString(String text) {
		Pattern propAccess = Pattern.compile("(\\[\\d*)(..)(\\d*\\])");
		Matcher matcher = propAccess.matcher(text);
		int index =0;
		String migratedStr = "";
		while(matcher.find()) {
			migratedStr += text.substring(index, matcher.start())+matcher.group(1)+" to "+matcher.group(3);
			index = matcher.end();
		}
		if(!migratedStr.equals("")) {
			 migratedStr += text.substring(index);
			 return migratedStr;
		} 
		return text;
	}
	 static String migrateHypenSeparatedName(String dwBody) {
		
		Pattern pattern = Pattern.compile("(^[^\"|^']\\w*\\d*-{1}\\w*\\d*[-\\d*\\w*]*[^\"|^']$)");
		Matcher matcher = pattern.matcher(dwBody);
		String migratedDwlBody = "";
		int index=0;
		while(matcher.find()) {
			if(!matcher.group(1).equals("---") && !matcher.group(1).equals("-")) 
				migratedDwlBody += dwBody.substring(index, matcher.start()) + "'" +matcher.group(1)+ "'";
			else
				migratedDwlBody += dwBody.substring(index, matcher.start()) + matcher.group(1);
			
			index = matcher.end();
		}
		migratedDwlBody += dwBody.substring(index); 
		
		return migratedDwlBody;
	}
	
	
	static String migrateFunctionDef(String dwHeader) {
		
		Pattern pattern = Pattern.compile("%function(.*?\\(.*?\\))");
		Matcher matcher = pattern.matcher(dwHeader);
		String migratedDwlHeader = "";
		int index=0;
		while(matcher.find()) {
			migratedDwlHeader += dwHeader.substring(index, matcher.start()) + "fun" +matcher.group(1)+ " = ";
			index = matcher.end();
		}
		migratedDwlHeader += dwHeader.substring(index); 
		
		return migratedDwlHeader;
	}
	
//
//	public static void main(String[] args) {
//
//		printMatches("\n\tqueue_message_id: flowVars.vQueueMsgId['bbbb'] when flowVars.vQueueMsgId['aaaa'] !=null otherwise ''\r\n}",	
//				"([\\'|\\\"|\\.|\\w|\\[|\\]]+)\\s+when(.*?)otherwise\\s+([\\\\'|\\\\\\\"|\\\\.|\\\\w|\\\\[|\\\\]]+)");
//
//
//
//	}
//
////
//	public static void printMatches(String text, String regex) {
//		Pattern pattern = Pattern.compile(regex);
//		Matcher matcher = pattern.matcher(text);
//		while (matcher.find()) {
//			System.out.print("Start index: " + matcher.start());
//			System.out.print(" End index: " + matcher.end());
//			System.out.println("\nBefore when : " + matcher.group(1));
//			System.out.println("\nAfter when : " + matcher.group(2));
//			System.out.println("\nAfter otherwise : " + matcher.group(3));
//
//			System.out.println(text.substring(0, matcher.start()) + "if (" + matcher.group(2) + ") " +
//			matcher.group(1) + " else " + matcher.group(3)+ text.substring(matcher.end()));
//
//		}
//	}



	public static String replaceMatches(String text, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		int index =0;
		String migratedStr = "";
		while(matcher.find()) {
			if(matcher.group(1).contains(":")) {
				String[] splits = matcher.group(1).split(":");
				migratedStr += text.substring(index, matcher.start()) + splits[0] + "if (" + matcher.group(2) + ") "+ splits[1] + " else ";
			}else if((matcher.group(1).contains("var ") || matcher.group(1).contains("fun ") ) && matcher.group(1).contains("=")){
				String[] splits = matcher.group(1).split("=");
				migratedStr += text.substring(index, matcher.start()) + splits[0]+" = if ("+matcher.group(2)+") " + splits[1] + " else ";
			}else {
				migratedStr += text.substring(index, matcher.start()) + "if (" + matcher.group(2) + ") " + matcher.group(1) + " else ";
			}
			index = matcher.end();
		}
		if(!migratedStr.equals("")) {
			 migratedStr += text.substring(index);
			 return migratedStr;
		} 
		return text;
	}
	
	public static String transformOnlyWhen(String text) {
		Pattern patten = Pattern.compile("\\swhen\\s");
		Matcher matcher = patten.matcher(text);
		int index =0;
		String migratedStr = "";
		while(matcher.find()) {
			migratedStr += text.substring(index, matcher.start())+ " if ";
			index = matcher.end();
		}
		if(!migratedStr.equals("")) {
			 migratedStr += text.substring(index);
			 return migratedStr;
		} 
		return text;
	}
	
	public static String replaceIsEq(String text) {
		Pattern patten = Pattern.compile("(.*?)\\.is(\\(*)eq(.*)");
		Matcher matcher = patten.matcher(text);
		int index =0;
		String migratedStr = "";
		while(matcher.find()) {
			migratedStr += text.substring(index, matcher.start()) + matcher.group(1) + " == " + matcher.group(3).substring(0, matcher.group(3).length()-1) + text.substring(matcher.end());
			index = matcher.end();
		}
		if(!migratedStr.equals("")) {
			 migratedStr += text.substring(index);
			 return migratedStr;
		} 
		return text;
	}
	
	static String replaceReplace(String text) {
		//
		Pattern patten = Pattern.compile(".replace\\((.*?),(.*)\\)");
		Matcher matcher = patten.matcher(text);
		int index =0;
		String migratedStr = "";
		while(matcher.find()) {
			migratedStr +=  text.substring(index, matcher.start()) +" replcae "+ matcher.group(1) + " with " + matcher.group(2) + " " + text.substring(matcher.end());
			index = matcher.end();
		}
		if(!migratedStr.equals("")) {
			 migratedStr += text.substring(index);
			 return migratedStr;
		}
		
		return text;
	}
	
	static String replaceReplaceAll(String text) {
		//
		Pattern patten = Pattern.compile(".replaceAll\\((.*?),(.*)\\)");
		Matcher matcher = patten.matcher(text);
		int index =0;
		String migratedStr = "";
		while(matcher.find()) {
			migratedStr +=  text.substring(index, matcher.start()) +" replcae "+ matcher.group(1) + " with " + matcher.group(2) + " " + text.substring(matcher.end());
			index = matcher.end();
		}
		if(!migratedStr.equals("")) {
			 migratedStr += text.substring(index);
			 return migratedStr;
		}
		
		return text;
	}
	







}
