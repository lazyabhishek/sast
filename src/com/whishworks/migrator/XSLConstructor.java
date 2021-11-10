package com.whishworks.migrator;

import com.whishworks.migrator.util.XSLTMapper;

public class XSLConstructor {
	public static StringBuffer muleTagNameSpaces = new StringBuffer();
	StringBuffer buf = new StringBuffer();
	XSLTMapper map = new XSLTMapper();

	public StringBuffer constructXSL(String elementName, boolean includeNameSpaces, boolean isComplex) {
		clearBuffer();
		if(elementName.equals("apikit:router")) {
			System.out.println();
		}
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + "\r\n" + "<xsl:stylesheet version=\"1.0\"\r\n"
				+ "xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" ");
		if(includeNameSpaces) {
			buf.append(XSLConstructor.muleTagNameSpaces);
			buf.append(" ");
			//buf.append("xmlns:wsc=\"http://www.mulesoft.org/schema/mule/wsc\" ");
			//buf.append("xmlns:os=\"http://www.mulesoft.org/schema/mule/os\" ");
			//buf.append("xmlns:oauth=\"http://www.mulesoft.org/schema/mule/oauth\" ");
			if(buf.indexOf("xmlns:ee=\"http://www.mulesoft.org/schema/mule/ee/core\"") < 0)
				buf.append("xmlns:ee=\"http://www.mulesoft.org/schema/mule/ee/core\" ");
			//buf.append("xmlns:tls=\"http://www.mulesoft.org/schema/mule/tls\" ");
			//buf.append("xmlns:ee=\"http://www.mulesoft.org/schema/mule/ee/core\" ");
			//buf.append("xmlns:secure-properties=\"http://www.mulesoft.org/schema/mule/secure-properties\" ");
			//buf.append("xmlns:api-gateway=\"http://www.mulesoft.org/schema/mule/api-gateway\" ");
			//buf.append("xmlns:json=\"http://www.mulesoft.org/schema/mule/json\" ");
			//buf.append("xmlns:dw=\"http://www.mulesoft.org/schema/mule/ee/dw\"");
			if(elementName.equals("http:request-config") && buf.indexOf("xmlns:sockets=") < 0) {
				buf.append("xmlns:sockets=\"http://www.mulesoft.org/schema/mule/sockets\" ");
				buf.append("xmlns:tcp=\"http://www.mulesoft.org/schema/mule/tcp\"");
			}
			if(elementName.equals("ee:object-store-caching-strategy") && buf.indexOf("xmlns:os") < 0) {
				buf.append("xmlns:os=\"http://www.mulesoft.org/schema/mule/os\"");
			}
		}else {
			buf.append(" xmlns:doc=\"http://www.mulesoft.org/schema/mule/documentation\" ");
			buf.append("xmlns:ee=\"http://www.mulesoft.org/schema/mule/ee/core\" ");
			buf.append("xmlns=\"http://www.mulesoft.org/schema/mule/core\"");
			//buf.append("xmlns:json=\"http://www.mulesoft.org/schema/mule/json\" ");
			//buf.append("xmlns:dw=\"http://www.mulesoft.org/schema/mule/ee/dw\"");
			//buf.append(" xmlns:ee=\"http://www.mulesoft.org/schema/mule/ee/core ");
		}
		//buf.append(" xmlns:ee=\"http://www.mulesoft.org/schema/mule/ee/core ");
		buf.append(">");
		if(isComplex) {
			buf.append("\r\n<xsl:template match=\"" + "*" + "\">\r\n" + "  \r\n" + "<output>");
		}else {
			buf.append("\r\n<xsl:template match=\"" + elementName + "\">\r\n" + "  \r\n" + "<output>");
		}
		
		buf.append(XSLTMapper.transformerOf.get(elementName));
		//\r\n</mule>
		buf.append("</output>\r\n " + "</xsl:template>\r\n " + "\r\n" + "</xsl:stylesheet>");
		return buf;
	}
	
	public void clearBuffer() {
		this.buf = new StringBuffer();
	}
}
