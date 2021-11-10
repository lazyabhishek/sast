package com.whishworks.migrator.util;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.whishworks.report.generator.Issue;

public class UnhandledScenariosHelper {
	
	public static List<String> elements = Stream.of("until-successful", "http:request", "microsoftservicebus:queue-receive", "microsoftservicebus:rule-create", "microsoftservicebus:rule-update", "microsoftservicebus:subscription-create", "microsoftservicebus:subscription-update", "microsoftservicebus:topic-create", "microsoftservicebus:topic-update","component", "dw:transform-message", "quartz:inbound-endpoint", "quartz:outbound-endpoint").collect(Collectors.toList());

	public static void addUnhandledScenariosToReport(Element m3element, List<Issue> flowIssueList, StringBuffer nodeString) {
		//StringBuffer nodeString = Migrator.getNodeAsString(m3element);
		switch (m3element.getNodeName()) {
		case "until-successful":
			if(m3element.getAttribute("failureExpression") != null) {
				Issue issue = new Issue("The attribute <b>failureExpression</b> doesn't exist in mule 4 until-successful component, handle the migration manually. Refer to the content in the below URI for futher information", "lineNum", nodeString);
				issue.setRefferToHref("https://docs.mulesoft.com/mule-runtime/4.1/migration-core-until-successful");
				flowIssueList.add(issue);
			}
			if(m3element.getAttribute("deadLetterQueue-ref") != null) {
				Issue issue = new Issue("The attribute <b>deadLetterQueue-ref</b> doesn't exist in mule 4 until-successful component, handle the migration manually. Refer to the content in the below URI for futher information", "lineNum", nodeString);
				issue.setRefferToHref("https://docs.mulesoft.com/mule-runtime/4.1/migration-core-until-successful");
				flowIssueList.add(issue);
			}
			if(m3element.getAttribute("ackExpression") != null) {
				Issue issue = new Issue("The attribute <b>ackExpression</b> doesn't exist in mule 4 until-successful component, handle the migration manually. Refer to the content in the below URI for futher information", "lineNum", nodeString);
				issue.setRefferToHref("https://docs.mulesoft.com/mule-runtime/4.1/migration-core-until-successful");
				flowIssueList.add(issue);
			}
			break;
		
		case "message-properties-transformer":
			if(m3element.getAttribute("returnClass") != null) {
				Issue issue = new Issue("Couldn't migrate attribute <b>returnClass</b> , handle the migration manually. Refer to the content in the below URI for futher information", "lineNum", nodeString);
				issue.setRefferToHref("https://docs.mulesoft.com/mule-runtime/4.1/migration-message-properties");
				flowIssueList.add(issue);
			}
			if(m3element.getAttribute("ignoreBadInput") != null) {
				Issue issue = new Issue("Couldn't migrate attribute <b>ignoreBadInput</b> , handle the migration manually. Refer to the  content in the below URI for futher information", "lineNum", nodeString);
				issue.setRefferToHref("https://docs.mulesoft.com/mule-runtime/4.1/migration-message-properties");
				flowIssueList.add(issue);
			}
			if(m3element.getAttribute("overwrite") != null) {
				Issue issue = new Issue("Couldn't migrate attribute <b>overwrite</b> , handle the migration manually. Refer to the content in the below URI for futher information", "lineNum", nodeString);
				issue.setRefferToHref("https://docs.mulesoft.com/mule-runtime/4.1/migration-message-properties");
				flowIssueList.add(issue);
			}
			if(m3element.getElementsByTagName("rename-message-property").getLength() > 0) {
				Issue issue = new Issue("The child element <b>rename-message-property</b> couldn't be migrated, handle it manually. Refer to the content in the below URI for futher information", "lineNum", nodeString);
				issue.setRefferToHref("https://docs.mulesoft.com/mule-runtime/4.1/migration-message-properties");
				flowIssueList.add(issue);
			}
			if(m3element.getElementsByTagName("delete-message-property").getLength() > 0) {
				Issue issue = new Issue("The child element <b>delete-message-property</b> which is used to pass bulk parameters at once using MEL expression cannot be achieved in Mule 4. Figure out all the key-value pairs which are part of bulk parameters, add them as key value paris to new Mule 4 http:requester.", "lineNum", nodeString);
				issue.setRefferToHref("https://docs.mulesoft.com/mule-runtime/4.1/migration-message-properties");
				flowIssueList.add(issue);
			}
		case "http:request":
			if(m3element.getElementsByTagName("http:query-params").getLength() > 0) {
				Issue issue = new Issue("The child element <b>http:query-params</b> which is used to pass bulk parameters at once using MEL expression cannot be achieved in Mule 4. Figure out all the key-value pairs which are part of bulk parameters, add them as key value paris to new Mule 4 http:requester.", "lineNum", nodeString);
				issue.setRefferToHref("https://whishworks.atlassian.net/wiki/spaces/MC/pages/1137410062/HTTP+requester+bulk-parameters+migration");
				flowIssueList.add(issue);
			}
			if(m3element.getElementsByTagName("http:uri-params").getLength() > 0) {
				Issue issue = new Issue("The child element <b>http:uri-params</b> which is used to pass bulk parameters at once using MEL expression cannot be achieved in Mule 4. Figure out all the key-value pairs which are part of bulk parameters, add them as key value paris to new Mule 4 http:requester.", "lineNum", nodeString);
				issue.setRefferToHref("https://whishworks.atlassian.net/wiki/spaces/MC/pages/1137410062/HTTP+requester+bulk-parameters+migration");
				flowIssueList.add(issue);
			}
			if(m3element.getElementsByTagName("http:headers").getLength() > 0) {
				Issue issue = new Issue("The child element <b>http:headers</b> which is used to pass bulk parameters at once using MEL expression cannot be achieved in Mule 4. Figure out all the key-value pairs which are part of bulk parameters, add them as key value paris to new Mule 4 http:requester.", "lineNum", nodeString);
				issue.setRefferToHref("https://whishworks.atlassian.net/wiki/spaces/MC/pages/1137410062/HTTP+requester+bulk-parameters+migration");
				flowIssueList.add(issue);
			}
			break;
		case "component":
			if(true) {
				Issue issue = new Issue("Couldn't migrate <b>component</b>, which is used to invoked Java implementing callable interface.", "lineNum", nodeString);
				issue.setRefferToHref("https://whishworks.atlassian.net/wiki/spaces/MC/pages/1198817328/Migration+cheat+sheet#Migrationcheatsheet-migrating-java-component");
				flowIssueList.add(issue);
			}
			break;
		case "invoke":
			if(true) {
				Issue issue = new Issue("Couldn't migrate <b>invoke</b>, which is used to invoked Java method.", "lineNum", nodeString);
				issue.setRefferToHref("https://whishworks.atlassian.net/wiki/spaces/MC/pages/1198817328/Migration+cheat+sheet#Migrationcheatsheet-migrating-java-component");
				flowIssueList.add(issue);
			}
			break;
		case "custom-transformer":
			if(true) {
				Issue issue = new Issue("Couldn't migrate <b>custom-transformer</b>, which is used to invoked Java class inorder to transform message. Hint: Use alternatives like dataweave to achive the same transformation", "lineNum", nodeString);
				//issue.setRefferToHref("https://whishworks.atlassian.net/wiki/spaces/MC/pages/1137410062/HTTP+requester+bulk-parameters+migration");
				flowIssueList.add(issue);
			}
			break;
		case "scripting:component":
			if(true) {
				Issue issue = new Issue("We can no longer update/set values of variable in scripting component(Groovy, JavaScript or Pyhthon) as they are immutable. Please find an alternative to achive the same.", "lineNo", nodeString);
				issue.setRefferToHref("https://whishworks.atlassian.net/wiki/spaces/MC/pages/1198817328/Migration+cheat+sheet#Migrationcheatsheet-migrating-groovy-component");
				flowIssueList.add(issue);
			}
			break;
		case "scripting:transformer":
			if(true) {
				Issue issue = new Issue("We can no longer update/set values of variable in scripting transformer(Groovy, JavaScript or Pyhthon) as they are immutable. Please find an alternative to achive the same.", "lineNo", nodeString);
				issue.setRefferToHref("https://whishworks.atlassian.net/wiki/spaces/MC/pages/1198817328/Migration+cheat+sheet#Migrationcheatsheet-migrating-groovy-component");
				flowIssueList.add(issue);
			}
			break;
		case "microsoftservicebus:queue-receive":
			if(m3element.getAttribute("sourceQueue") != null) {
				Issue issue = new Issue("The attribute <b>sourceQueue</b> migration failed. Kindly handle it manually.", "lineNum", nodeString);
				flowIssueList.add(issue);
			}
			break;
		case "microsoftservicebus:rule-create":
			if(m3element.getElementsByTagName("microsoftservicebus:rule-description").getLength() > 0) {
				Issue issue = new Issue("The child element <b>microsoftservicebus:rule-description</b> migration failed. Kindly handle it and its child elements manually.", "lineNum", nodeString);
				flowIssueList.add(issue);
			}
			break;
		case "microsoftservicebus:rule-update":
			if(m3element.getElementsByTagName("microsoftservicebus:rule-update").getLength() > 0) {
				Issue issue = new Issue("The child element <b>microsoftservicebus:rule-update</b> migration failed. Kindly handle it and its child elements manually.", "lineNum", nodeString);
				flowIssueList.add(issue);
			}
			break;
		case "microsoftservicebus:subscription-create":
			if(m3element.getElementsByTagName("microsoftservicebus:subscription-description").getLength() > 0) {
				Issue issue = new Issue("The child element <b>microsoftservicebus:subscription-description</b> migration failed. Kindly handle it and its child elements manually.", "lineNum", nodeString);
				flowIssueList.add(issue);
			}
			break;
		case "microsoftservicebus:subscription-update":
			if(m3element.getElementsByTagName("microsoftservicebus:subscription-description").getLength() > 0) {
				Issue issue = new Issue("The child element <b>microsoftservicebus:subscription-description</b> migration failed. Kindly handle it and its child elements manually.", "lineNum", nodeString);
				flowIssueList.add(issue);
			}
			break;
		case "microsoftservicebus:topic-create":
			if(m3element.getElementsByTagName("microsoftservicebus:topic-description").getLength() > 0) {
				Issue issue = new Issue("The child element <b>microsoftservicebus:topic-description</b> migration failed. Kindly handle it and its child elements manually.", "lineNum", nodeString);
				flowIssueList.add(issue);
			}
			break;
		case "microsoftservicebus:topic-update":
			if(m3element.getElementsByTagName("microsoftservicebus:topic-description").getLength() > 0) {
				Issue issue = new Issue("The child element <b>microsoftservicebus:topic-description</b> migration failed. Kindly handle it and its child elements manually.", "lineNum", nodeString);
				flowIssueList.add(issue);
			}
			break;
		case "global-functions":
			Issue issue = new Issue("Global functions are removed from Mule 4, kindly handle the same manually. Refer to the below hyper link for futher assistance on migration.", "lineNo", nodeString);
			issue.setRefferToHref("https://whishworks.atlassian.net/wiki/spaces/MC/pages/1198817328/Migration+cheat+sheet#Migrationcheatsheet-migrating-global-function");
			flowIssueList.add(issue);
			break;
		case "dw:transform-message":
			Issue dwInfo = new Issue("Dataweave present in this component might need manual intervention as tool can migrate only 60-70% of Dataweave 1.0 to Dataweave 2.0", "lineNo", nodeString);
			flowIssueList.add(dwInfo);
			break;
		case "quartz:inbound-endpoint":
			if(m3element.hasChildNodes()) {
				Issue quartzissue = new Issue("The child element <b>"+m3element.getChildNodes().item(0).getNodeName()+"</b> migration failed. Kindly handle it and it's child elements manually.", "lineNum", nodeString);
				flowIssueList.add(quartzissue);
			}
			break;
		case "quartz:outbound-endpoint":
			Issue quartzOutbound = new Issue("The component <b>quartz:outbound-endpoint</b> migration failed. Kindly handle it and its child elements manually.", "lineNum", nodeString, "https://docs.mulesoft.com/mule-runtime/4.2/migration-core-poll#qz_outbound_endpoint");
			flowIssueList.add(quartzOutbound);
			break;
		case "jdbc-ee:connector":
			Issue jdbcConnectorIssue = new Issue("Couldn't migrate jdbc-ee:connector, kindly handle manually", "lineNo", nodeString);
			flowIssueList.add(jdbcConnectorIssue);
			break;
		case "jdbc-ee:outbound-endpoint":
			if(m3element.hasChildNodes()) {
				NodeList children = m3element.getChildNodes();
				String[] startStr = {"S", "s", "U", "u", "D", "d", "I", "i", "C", "c"};
				boolean handled = false;
				for(int i=0; i<children.getLength(); i++) {
					if(children.item(i).getNodeName().equals("jdbc-ee:query")) {
						for (String string : startStr) {
							handled |= children.item(i).getNodeName().trim().startsWith(string);
						}
					}
				}
				if(!handled) {
					Issue jdbcIssue = new Issue("Couldn't migrate to appropriate Mule database operation, kindly handle it manually.", "lineNo", nodeString);
					flowIssueList.add(jdbcIssue);
				}
			}
			break;
		case "jdbc-ee:inbound-endpoint":
			if(m3element.hasChildNodes()) {
				NodeList children = m3element.getChildNodes();
				String[] startStr = {"S", "s", "U", "u", "D", "d", "I", "i", "C", "c"};
				boolean handled = false;
				for(int i=0; i<children.getLength(); i++) {
					if(children.item(i).getNodeName().equals("jdbc-ee:query")) {
						for (String string : startStr) {
							handled |= children.item(i).getNodeName().trim().startsWith(string);
						}
					}
				}
				if(!handled) {
					Issue jdbcIssue = new Issue("Couldn't migrate to appropriate Mule database operation, kindly handle it manually.", "lineNo", nodeString);
					flowIssueList.add(jdbcIssue);
				}
			}
			break;
		default:
			break;
		}
	}
	
}
