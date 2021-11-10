package com.whishworks.migrator.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PomArtifactMapper {
   public static Map<String, String> artifactMapper = new HashMap<>();

   public static List<String> skipArtifacts = Stream.of("mule-plugin-weave", "mule-tests-functional", "mule-module-spring-config-ee", "mule-core-ee").collect(Collectors.toList());
   
   public static void init() {
	   artifactMapper.put("mule-transport-http","mule-http-connector");
	   artifactMapper.put("mule-module-http","mule-http-connector");
	   artifactMapper.put("mule-transport-sftp","mule-sftp-connector");
	   artifactMapper.put("mule-module-tls","mule-sockets-connector");
	   artifactMapper.put("mule-transport-sockets","mule-sockets-connector");
	   artifactMapper.put("mule-module-sockets","mule-sockets-connector");
	   artifactMapper.put("mule-module-objectstore","mule-objectstore-connector");
	   artifactMapper.put("mule-module-validation","mule-validation-module");
	   artifactMapper.put("mule-transport-sftp","mule-sftp-connector");
	   artifactMapper.put("mule-transport-ftp-ee","mule-ftp-connector");
	   artifactMapper.put("mule-module-ftp","mule-ftp-connector");
	   artifactMapper.put("mule-transport-file", "mule-file-connector");
	   artifactMapper.put("mule-transport-jms-ee","mule-jms-connector");
	   artifactMapper.put("mule-module-jms","mule-jms-connector");
	   artifactMapper.put("mule-module-spring-config","mule-spring-module");
	   artifactMapper.put("mule-module-spring-config-ee", "mule-spring-module");
	   artifactMapper.put("mule-transport-vm","mule-vm-connector");
	   artifactMapper.put("mule-module-xml","mule-xml-module");
	   artifactMapper.put("mule-module-sfdc","mule-salesforce-connector");
	   artifactMapper.put("mule-module-email","mule-email-connector");
	   artifactMapper.put("mule-module-oauth","mule-oauth-module");
	   artifactMapper.put("mule-module-sockets","mule-wsc-connector");
	   artifactMapper.put("mule-module-scripting","mule-scripting-module");
	   artifactMapper.put("mule-module-wsc","mule-scripting-module");
	   artifactMapper.put("mule-module-apikit","mule-apikit-module");
	   artifactMapper.put("mule-module-netsuite","mule-netsuite-connector");
	   artifactMapper.put("mule-module-file","mule-file-connector");
	   artifactMapper.put("mule-module-security-property-placeholder","mule-secure-configuration-property-module");
	   artifactMapper.put("mule-module-db","mule-db-connector");
	   artifactMapper.put("mule-transport-jdbc-ee","mule-db-connector");
	   artifactMapper.put("mule-module-s3","mule-amazon-s3-connector");
	   artifactMapper.put("mule-module-sns","mule-amazon-sns-connector");
	   artifactMapper.put("mule-module-sqs","mule-amazon-sqs-connector");
	  // artifactMapper.put("mule-module-s3","mule-amazon-s3-connector");
	   artifactMapper.put("mule-modules-as2","mule-as2-connector");
	   artifactMapper.put("mule-module-cassandradb","mule-cassandradb-connector");
	   artifactMapper.put("mule-module-cloudhub","mule-cloudhub-connector");
	   artifactMapper.put("edi-module-edifact","mule-edifact-extension");
	   artifactMapper.put("mule-transport-ftp-ee","mule-ftp-connector");
	   artifactMapper.put("mule-ftps-transport","mule-ftps-connector");
	   artifactMapper.put("mule-transport-jms","mule-jms-connector");
	   artifactMapper.put("mule-module-json","mule-json-module");
	   artifactMapper.put("mule-module-kafka","mule-kafka-connector");
	   artifactMapper.put("mule-module-ms-dynamics-crm","mule-microsoft-dynamics-crm-connector");
	   artifactMapper.put("mule-module-ms-dynamics-nav","mule-microsoft-dynamics-nav-connector");
	   artifactMapper.put("mule-module-msmq","mule-microsoft-mq-connector");
	   artifactMapper.put("mule-module-servicenow","mule-servicenow-connector");
	   artifactMapper.put("servicebus-connector","mule-microsoft-service-bus-connector");
	   artifactMapper.put("mule-module-netsuite","mule-netsuite-connector");
	   artifactMapper.put("mule-module-security-oauth2-provider","mule-oauth2-provider-module");
	   artifactMapper.put("mule-module-objectstore","mule-objectstore-connector");
	   artifactMapper.put("oracle-ebs-connector","mule-oracle-ebs-connector");
	   artifactMapper.put("oracle-ebs-122-connector","mule-oracle-ebs-122-connector");
	   artifactMapper.put("mule-transport-email","mule-email-connector");
	   artifactMapper.put("mule-module-redis","mule-redis-connector");
	   artifactMapper.put("mule-transport-sap","mule-sap-connector");
	   artifactMapper.put("mule-transport-sftp","mule-sftp-connector");
	   //artifactMapper.put("mule-transport-email","mule-email-connector");
	   artifactMapper.put("mule-module-success-factors","mule-sap-successfactors-connector");
	   artifactMapper.put("mule-transport-wmq-ee","mule-ibm-mq-connector");
	   artifactMapper.put("workday-connector","mule-workday-connector");
	   artifactMapper.put("mule-module-zuora","mule-zuora-connector");
	   artifactMapper.put("mule-module-sfdc","mule-sfdc-marketing-cloud-connector");
	   artifactMapper.put("mule-module-mongo","mule-mongodb-connector");
	   artifactMapper.put("mule-module-db","mule-db-connector");
	   artifactMapper.put("mule-module-anypoint-mq-ee","anypoint-mq-connector");
	   artifactMapper.put("mule-transport-amqp","mule-amqp-connector");
   
	   artifactMapper.put("mule-module-hdfs","mule-module-hdfs");
	   artifactMapper.put("microsoft-dynamics365-connector","mule-microsoft-dynamics365-connector");
	   artifactMapper.put("mule-module-dynamics-ax-2012","mule-microsoft-dynamics-ax-connector");
	   artifactMapper.put("ms-dynamics-gp-connector","mule-microsoft-dynamics-gp-connector");
	   artifactMapper.put("thru-transport-connector","thru-transport-connector");
	   artifactMapper.put("sharepoint-online","mule-sharepoint-connector");
	   artifactMapper.put("mule-module-twilio","mule-twilio-connector");
	   artifactMapper.put("siebel-businessobject","mule-oracle-siebel-jdb-connector");
	   artifactMapper.put("siebel-businessservice","mule-siebel-connector");
	   artifactMapper.put("siebel-integrationobject","mule-siebel-connector");
	   artifactMapper.put("mule-module-ec2","mule-amazon-ec2-connector");
	   artifactMapper.put("mule-module-custom-metrics","mule-custom-metrics-extension");
	   artifactMapper.put("as400-connector","as400-connector");
	   artifactMapper.put("remedy-connector","mule-oauth-module");
	   artifactMapper.put("mule-module-box","mule-box-connector");
	   artifactMapper.put("mule-module-hl7","mule-hl7-extension");
	   artifactMapper.put("mule-transport-mllp","mule-hl7-mllp-connector");
	   artifactMapper.put("mule-ibmctg-connector","mule-ibm-ctg-connector");
	   artifactMapper.put("mule-module-marketo","mule-marketo-connector");
	   artifactMapper.put("powershell-connector","mule-powershell-connector");
	   artifactMapper.put("mule-module-neo4j","mule-neo4j-connector");
	   artifactMapper.put("rosetta-net-connector","mule-rosettanet-connector");
	   artifactMapper.put("slack-connector","mule-slack-connector");
	   artifactMapper.put("mule-module-tradacoms","mule-tradacoms-connector");
	   artifactMapper.put("mule-transport-udp","mule-sockets-connector");
	   artifactMapper.put("mule-volante-connector","mule4-volante-connector");
	   artifactMapper.put("mule-transport-jms","mule-jms-connector");
	   artifactMapper.put("edi-module-x12","mule-x12-connector");
	   artifactMapper.put("mule-transport-tcp","mule-sockets-connector");
   }

}
