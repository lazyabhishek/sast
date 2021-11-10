package com.whishworks.migrator.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NameSpaceMapper {

	public static Map<String, Map<String, String>> nameSpaceMap = new LinkedHashMap<>();
	
	public static List<String> emailFamily = Stream.of("smtps","smtp","pop3s","pop3").collect(Collectors.toList());
	
	public static void constructNameSpaceMapper() {
		
		Map<String, String> temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/apikit/current/mule-apikit.xsd", "http://www.mulesoft.org/schema/mule/mule-apikit/current/mule-apikit.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/apikit", "http://www.mulesoft.org/schema/mule/mule-apikit");
		nameSpaceMap.put("apikit/", temp);
		
//		temp = new HashMap<>();
//		temp.put("", "");
//		temp.put("", "");
//		nameSpaceMap.put("", temp);
	
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/ee/jdbc/current/mule-jdbc-ee.xsd","http://www.mulesoft.org/schema/mule/db/current/mule-db.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/ee/jdbc", "http://www.mulesoft.org/schema/mule/db");
		temp.put("xmlns:jdbc-ee", "xmlns:db");
		nameSpaceMap.put("jdbc-ee", temp);
	
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/sharepoint2010/current/mule-sharepoint2010.xsd","http://www.mulesoft.org/schema/mule/sharepoint/current/mule-sharepoint.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/sharepoint2010", "http://www.mulesoft.org/schema/mule/sharepoint");
		temp.put("xmlns:sharepoint2010", "xmlns:sharepoint");
		nameSpaceMap.put("sharepoint2010", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/sharepoint-online/current/mule-sharepoint-online.xsd","http://www.mulesoft.org/schema/mule/sharepoint/current/mule-sharepoint.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/sharepoint-online", "http://www.mulesoft.org/schema/mule/sharepoint");
		temp.put("xmlns:sharepoint-online", "xmlns:sharepoint");
		nameSpaceMap.put("sharepoint-online", temp);
//	
//		temp = new LinkedHashMap<>();
//		temp.put("http://www.mulesoft.org/schema/mule/sfdc/current/mule-sfdc.xsd", "http://www.mulesoft.org/schema/mule/salesforce/current/mule-salesforce.xsd");
//		temp.put("http://www.mulesoft.org/schema/mule/sfdc", "http://www.mulesoft.org/schema/mule/salesforce");
//		nameSpaceMap.put("sfdc", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/ee/ftp/current/mule-ftp-ee.xsd", "http://www.mulesoft.org/schema/mule/ftp/current/mule-ftp.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/ee/ftp", "http://www.mulesoft.org/schema/mule/ftp");
		nameSpaceMap.put("ftp", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/oauth2/current/mule-oauth2.xsd", "http://www.mulesoft.org/schema/mule/oauth/current/mule-oauth.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/oauth2", "http://www.mulesoft.org/schema/mule/oauth");
		nameSpaceMap.put("oauth", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("", "http://www.mulesoft.org/schema/mule/java");
		temp.put("", "http://www.mulesoft.org/schema/mule/java/current/mule-java.xsd");
		nameSpaceMap.put("java", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/ws/current/mule-ws.xsd", "http://www.mulesoft.org/schema/mule/wsc/current/mule-wsc.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/ws", "http://www.mulesoft.org/schema/mule/wsc");
		temp.put("xmlns:ws", "xmlns:wsc");
		nameSpaceMap.put("ws", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.springframework.org/schema/beans/spring-beans-current.xsd", "http://www.mulesoft.org/schema/mule/spring/current/mule-spring.xsd");
		temp.put("http://www.springframework.org/schema/beans", "http://www.mulesoft.org/schema/mule/spring");
		nameSpaceMap.put("spring", temp);
		
				
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/smtps/current/mule-smtps.xsd", "http://www.mulesoft.org/schema/mule/email/current/mule-email.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/smtps", "http://www.mulesoft.org/schema/mule/email");
		temp.put("xmlns:smtps", "xmlns:email");
		nameSpaceMap.put("smtps", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/smtp/current/mule-smtp.xsd", "http://www.mulesoft.org/schema/mule/email/current/mule-email.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/smtp", "http://www.mulesoft.org/schema/mule/email");
		temp.put("xmlns:smtp", "xmlns:email");
		nameSpaceMap.put("smtp", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/pop3s/current/mule-pop3s.xsd", "http://www.mulesoft.org/schema/mule/email/current/mule-email.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/pop3s", "http://www.mulesoft.org/schema/mule/email");
		temp.put("xmlns:pop3s", "xmlns:email");
		nameSpaceMap.put("pop3s", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/pop3/current/mule-pop3.xsd", "http://www.mulesoft.org/schema/mule/email/current/mule-email.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/pop3", "http://www.mulesoft.org/schema/mule/email");
		temp.put("xmlns:pop3", "xmlns:email");
		nameSpaceMap.put("pop3", temp);

		
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/ee/dw/current/dw.xsd", "http://www.mulesoft.org/schema/mule/ee/core/current/mule-ee.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/ee/dw", "http://www.mulesoft.org/schema/mule/ee/core");
		temp.put("xmlns:dw", "xmlns:ee");
		nameSpaceMap.put("dw", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/secure-property-placeholder/current/mule-secure-property-placeholder.xsd", "http://www.mulesoft.org/schema/mule/secure-properties/current/mule-secure-properties.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/secure-property-placeholder", "http://www.mulesoft.org/schema/mule/secure-properties");
		temp.put("xmlns:secure-property-placeholder", "xmlns:secure-properties");
		nameSpaceMap.put("secure-property-placeholder", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/xml/current/mule-xml.xsd","http://www.mulesoft.org/schema/mule/xml-module/current/mule-xml-module.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/xml", "http://www.mulesoft.org/schema/mule/xml-module");
		temp.put("xmlns:mulexml", "xmlns:xml-module");
		temp.put("xml-module-module", "xml-module");
		nameSpaceMap.put("mulexml", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/api-platform-gw/current/mule-api-platform-gw.xsd","http://www.mulesoft.org/schema/mule/api-gateway/current/mule-api-gateway.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/api-platform-gw", "http://www.mulesoft.org/schema/mule/api-gateway");
		temp.put("xmlns:api-platform-gw", "xmlns:api-gateway");
		nameSpaceMap.put("api-platform-gw", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/objectstore/current/mule-objectstore.xsd","http://www.mulesoft.org/schema/mule/os/current/mule-os.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/objectstore", "http://www.mulesoft.org/schema/mule/os");
		temp.put("xmlns:objectstore", "xmlns:os");
		nameSpaceMap.put("objectstore", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/microsoftservicebus/current/mule-microsoftservicebus.xsd","http://www.mulesoft.org/schema/mule/servicebus/current/mule-servicebus.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/microsoftservicebus", "http://www.mulesoft.org/schema/mule/servicebus");
		temp.put("xmlns:microsoftservicebus", "xmlns:servicebus");
		nameSpaceMap.put("microsoftservicebus", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/oauth-provider/current/mule-oauth2-provider.xsd","http://www.mulesoft.org/schema/mule/oauth2-provider/current/mule-oauth2-provider.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/oauth-provider", "http://www.mulesoft.org/schema/mule/oauth2-provider");
		//temp.put("xmlns:oauth2-provider", "xmlns:oauth2-provider");
		nameSpaceMap.put("oauth2-provider", temp);
		
//		temp = new LinkedHashMap<>();
//		temp.put("http://www.mulesoft.org/schema/mule/oauth2/current/mule-oauth2.xsd", "http://www.mulesoft.org/schema/mule/oauth/current/mule-oauth.xsd");
//		temp.put("http://www.mulesoft.org/schema/mule/oauth2", "http://www.mulesoft.org/schema/mule/oauth");
//		nameSpaceMap.put("oauth", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/wd-connector/current/mule-wd-connector.xsd","http://www.mulesoft.org/schema/mule/workday/current/mule-workday.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/wd-connector", "http://www.mulesoft.org/schema/mule/workday");
		temp.put("xmlns:wd-connector", "xmlns:workday");
		nameSpaceMap.put("wd-connector", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/ee/wmq/current/mule-wmq-ee.xsd","http://www.mulesoft.org/schema/mule/ibm-mq/current/mule-ibm-mq.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/ee/wmq", "http://www.mulesoft.org/schema/mule/ibm-mq");
		temp.put("xmlns:wmq", "xmlns:ibm-mq");
		nameSpaceMap.put("wmq", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/apachekafka/current/mule-apachekafka.xsd","http://www.mulesoft.org/schema/mule/kafka/current/mule-kafka.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/apachekafka", "http://www.mulesoft.org/schema/mule/kafka");
		temp.put("xmlns:apachekafka", "xmlns:kafka");
		nameSpaceMap.put("apachekafka", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/cassandradb/current/mule-cassandradb.xsd","http://www.mulesoft.org/schema/mule/cassandra-db/current/mule-cassandra-db.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/cassandradb", "http://www.mulesoft.org/schema/mule/cassandra-db");
		temp.put("xmlns:cassandradb", "xmlns:cassandra-db");
		nameSpaceMap.put("cassandradb", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/edifact-edi/current/mule-edifact-edi.xsd","http://www.mulesoft.org/schema/mule/edifact/current/mule-edifact.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/edifact-edi", "http://www.mulesoft.org/schema/mule/edifact");
		temp.put("xmlns:edifact-edi", "xmlns:edifact");
		nameSpaceMap.put("edifact-edi", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/dynamicscrm/current/mule-dynamicscrm.xsd","http://www.mulesoft.org/schema/mule/microsoft-dynamics-crm/current/mule-microsoft-dynamics-crm.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/dynamicscrm", "http://www.mulesoft.org/schema/mule/microsoft-dynamics-crm");
		temp.put("xmlns:dynamicscrm", "xmlns:microsoft-dynamics-crm");
		nameSpaceMap.put("dynamicscrm", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/dynamics-nav/current/mule-dynamics-nav.xsd","http://www.mulesoft.org/schema/mule/nav/current/mule-nav.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/dynamics-nav", "http://www.mulesoft.org/schema/mule/nav");
		temp.put("xmlns:dynamics-nav", "xmlns:nav");
		nameSpaceMap.put("dynamics-nav", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/concur/current/mule-concur.xsd","http://www.mulesoft.org/schema/mule/mule-sap-concur-connector/current/mule-mule-sap-concur-connector.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/concur", "http://www.mulesoft.org/schema/mule/mule-sap-concur-connector");
		temp.put("xmlns:concur", "xmlns:mule-sap-concur-connector");
		nameSpaceMap.put("concur", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/dynamicsax/current/mule-dynamicsax.xsd","http://www.mulesoft.org/schema/mule/microsoft-dynamics-ax/current/mule-microsoft-dynamics-ax.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/dynamicsax", "http://www.mulesoft.org/schema/mule/microsoft-dynamics-ax");
		temp.put("xmlns:dynamicsax", "xmlns:microsoft-dynamics-ax");
		nameSpaceMap.put("dynamicsax", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/thru-transport/current/mule-thru-transport.xsd","http://www.mulesoft.org/schema/mule/mft/current/mule-mft.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/thru-transport", "http://www.mulesoft.org/schema/mule/mft");
		temp.put("xmlns:thru-transport", "xmlns:mft");
		nameSpaceMap.put("thru-transport", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/dynamics365/current/mule-dynamics365.xsd","http://www.mulesoft.org/schema/mule/dynamics/current/mule-dynamics.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/dynamics365", "http://www.mulesoft.org/schema/mule/dynamics");
		temp.put("xmlns:dynamics365", "xmlns:dynamics");
		nameSpaceMap.put("dynamics365", temp);
		
		
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/sfdc/current/mule-sfdc.xsd", "http://www.mulesoft.org/schema/mule/sfdc-marketing-cloud/current/mule-sfdc-marketing-cloud.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/sfdc", "http://www.mulesoft.org/schema/mule/sfdc-marketing-cloud");
		temp.put("xmlns:sfdc", "xmlns:sfdc-marketing-cloud");
		nameSpaceMap.put("mule/sfdc/current", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/sfdc-composite/current/mule-sfdc-composite.xsd","http://www.mulesoft.org/schema/mule/salesforce-composite/current/mule-salesforce-composite.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/sfdc-composite", "http://www.mulesoft.org/schema/mule/salesforce-composite");
		temp.put("xmlns:sfdc-composite", "xmlns:salesforce-composite");
		nameSpaceMap.put("sfdc-composite", temp);
	
//		temp = new LinkedHashMap<>();
//		temp.put("http://www.mulesoft.org/schema/mule/sfdc/current/mule-sfdc.xsd", "http://www.mulesoft.org/schema/mule/salesforce/current/mule-salesforce.xsd");
//		temp.put("http://www.mulesoft.org/schema/mule/sfdc", "http://www.mulesoft.org/schema/mule/salesforce");
//		nameSpaceMap.put("sfdc", temp);
		
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/twilio/current/mule-twilio.xsd","http://www.mulesoft.org/schema/mule/twilio-connector/current/mule-twilio-connector.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/twilio", "http://www.mulesoft.org/schema/mule/twilio-connector");
		temp.put("xmlns:twilio", "xmlns:twilio-connector");
		nameSpaceMap.put("twilio", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/hl7-edi/current/mule-hl7-edi.xsd","http://www.mulesoft.org/schema/mule/hl7/current/mule-hl7.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/hl7-edi", "http://www.mulesoft.org/schema/mule/hl7");
		temp.put("xmlns:hl7-edi", "xmlns:hl7");
		nameSpaceMap.put("hl7-edi", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/ibm-ctg/current/mule-ibm-ctg.xsd","http://www.mulesoft.org/schema/mule/ibmctg/current/mule-ibmctg.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/ibm-ctg", "http://www.mulesoft.org/schema/mule/ibmctg");
		temp.put("xmlns:ibm-ctg", "xmlns:ibmctg");
		nameSpaceMap.put("ibm-ctg", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/rosetta-net/current/mule-rosetta-net.xsd","http://www.mulesoft.org/schema/mule/rosetta/current/mule-rosetta.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/rosetta-net", "http://www.mulesoft.org/schema/mule/rosetta");
		temp.put("xmlns:rosetta-net", "xmlns:rosetta");
		nameSpaceMap.put("rosetta-net", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/tcp/current/mule-tcp.xsd","http://www.mulesoft.org/schema/mule/sockets/current/mule-sockets.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/tcp", "http://www.mulesoft.org/schema/mule/sockets");
		temp.put("xmlns:tcp", "xmlns:sockets");
		nameSpaceMap.put("tcp", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/x12-edi/current/mule-x12-edi.xsd","http://www.mulesoft.org/schema/mule/x12/current/mule-x12.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/x12-edi", "http://www.mulesoft.org/schema/mule/x12");
		temp.put("xmlns:x12-edi", "xmlns:x12");
		nameSpaceMap.put("x12-edi", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/udp/current/mule-udp.xsd","http://www.mulesoft.org/schema/mule/sockets/current/mule-sockets.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/udp", "http://www.mulesoft.org/schema/mule/sockets");
		temp.put("xmlns:udp", "xmlns:sockets");
		nameSpaceMap.put("udp", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/box/current/mule-box.xsd","http://www.mulesoft.org/schema/mule/mule-box-connector/current/mule-mule-box-connector.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/box", "http://www.mulesoft.org/schema/mule/mule-box-connector");
		temp.put("xmlns:box", "xmlns:mule-box-connector");
		nameSpaceMap.put("box", temp);
		
		temp = new LinkedHashMap<>();
		temp.put("http://www.mulesoft.org/schema/mule/https/current/mule-https.xsd","http://www.mulesoft.org/schema/mule/sockets/current/mule-sockets.xsd");
		temp.put("http://www.mulesoft.org/schema/mule/https", "http://www.mulesoft.org/schema/mule/sockets");
		temp.put("xmlns:https", "xmlns:sockets");
		nameSpaceMap.put("https", temp);
	}
}
