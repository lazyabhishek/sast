package com.whishworks.migrator.util;

import java.util.HashMap;
import java.util.Map;

public class ConnectorDeatils {

	public static Map<String, String> nodeToConfigMap = new HashMap<String, String>();
	
	
	
	{
		nodeToConfigMap.put("anypoint-mq", "anypoint-mq:config");
		nodeToConfigMap.put("sftp", "sftp:connector");
		nodeToConfigMap.put("smtp", "smtp:connector");
	}
	
}
