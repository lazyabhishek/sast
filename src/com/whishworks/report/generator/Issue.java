package com.whishworks.report.generator;

public class Issue {
	
	private String description;
	private String lineNo;
	private StringBuffer xmlSnippet;
	private String refferToHref;
	
	public Issue(String description, String lineNo, StringBuffer xmlSnippet) {
		super();
		this.description = description;
		this.lineNo = lineNo;
		this.xmlSnippet = xmlSnippet;
	}
	public Issue(String description, String lineNo, StringBuffer xmlSnippet, String refferToHref) {
		super();
		this.description = description;
		this.lineNo = lineNo;
		this.xmlSnippet = xmlSnippet;
		this.refferToHref = refferToHref;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLineNo() {
		return lineNo;
	}
	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
	}
	public StringBuffer getXmlSnippet() {
		return xmlSnippet;
	}
	public void setXmlSnippet(StringBuffer xmlSnippet) {
		this.xmlSnippet = xmlSnippet;
	}
	public String getRefferToHref() {
		return refferToHref;
	}
	public void setRefferToHref(String refferToHref) {
		this.refferToHref = refferToHref;
	}
	
	
}
