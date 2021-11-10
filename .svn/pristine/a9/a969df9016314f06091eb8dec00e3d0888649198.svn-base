package com.whishworks.migrator;

public class InputXMLConstructor {

	StringBuffer buf = new StringBuffer();
	public static StringBuffer muleTagNameSpaces = new StringBuffer();
	
	public StringBuffer constructInputXML(StringBuffer element, boolean includeNameSpaces) {
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
				"\r\n" + 
				"<mule ");
		if(includeNameSpaces) {
			buf.append(XSLConstructor.muleTagNameSpaces);
		}else {
			buf.append(" xmlns:doc=\"http://www.mulesoft.org/schema/mule/documentation\" ");
		}
		buf.append(">");
		buf.append("\r\n" + element + "\r\n" + "</mule>");
		return buf;
	}
	
	public void clearBuffer() {
		this.buf = new StringBuffer();
	}
}
