package com.whishworks.report.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report {

	private static int flowIssueCount = 0;
	
	private static int munitIssueCount = 0;
	
	private static String projectName = "";
	
	private static int percentageMigrated = 0;
	
	private static Map<String, List<Issue>> flowMigrationIssues = new HashMap<>();
	
	private static Map<String, List<Issue>> munitMigrationIssues = new HashMap<>();

	private static List<String> securePropertiesIssues = new ArrayList<>();
	
	private static List<String> springMigrationIssues = new ArrayList<>();
	//private static Map<String, List<Issue>> pomMigrationIssues = new HashMap<>();
	private static List<String> missingDependencies = new ArrayList<>();
	
	public static void addFlowMigrationIssue(String flowName, List<Issue> flowIssueList) {
		Report.flowMigrationIssues.put(flowName, flowIssueList);
	}
	
	public static void addMuintMigrationIssue(String munitName, List<Issue> issueList) {
		Report.munitMigrationIssues.put(munitName, issueList);
	}
	
	public static void addPomMigrationIssue(String missingDependency) {
		Report.missingDependencies.add(missingDependency);
	}
	public static void addSpringMigrationIssue(String springIssue) {
		Report.springMigrationIssues.add(springIssue);
	}
	
	public static void updateFlowIssuesCount(int number) {
		Report.setFlowIssueCount(Report.getFlowIssueCount() + number);
	}
	
	public static void updateMunitIssuesCount(int number) {
		Report.setMunitIssueCount(Report.getMunitIssueCount() + number);
	}
	
	public static void addSecurePropertiesIssue(String fileName) {
		securePropertiesIssues.add(fileName);
	}
	

	public static int getFlowIssueCount() {
		return flowIssueCount;
	}

	public static void setFlowIssueCount(int flowIssueCount) {
		Report.flowIssueCount = flowIssueCount;
	}

	public static int getMunitIssueCount() {
		return munitIssueCount;
	}

	public static void setMunitIssueCount(int munitIssueCount) {
		Report.munitIssueCount = munitIssueCount;
	}

	public static Map<String, List<Issue>> getFlowMigrationIssues() {
		return Report.flowMigrationIssues;
	}

	public static Map<String, List<Issue>> getMunitMigrationIssues() {
		return Report.munitMigrationIssues;
	}

	public static List<String> getMissingDependencies() {
		return Report.missingDependencies;
	}

	public static String getProjectName() {
		return projectName;
	}

	public static void setProjectName(String projectName) {
		Report.projectName = projectName;
	}

	public static int getPercentageMigrated() {
		return percentageMigrated;
	}

	public static void setPercentageMigrated(int percentageMigrated) {
		Report.percentageMigrated = percentageMigrated;
	}
	
	public static List<String> getSecurePorpIssues(){
		return securePropertiesIssues;
	}
	
	public static List<String> getSpringIssues(){
		return springMigrationIssues;
	}
	
	
}
