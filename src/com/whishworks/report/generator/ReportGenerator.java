package com.whishworks.report.generator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import com.whishworks.migrator.util.PercentageComputer;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class ReportGenerator {

	//	public static void main(String args[]) {
	//		try {
	//			generateReport();
	//		} catch (IOException | TemplateException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//	}

	public static void generateReport(String reportSavePath) throws IOException, TemplateException {
		//nfiguration cfg = new Configuration();
		//setDirectoryForTemplateLoading(new File("resources"));
		Map<String, Object> repoObj = new HashMap<>(); 
		//        Report.setFlowIssueCount(30);
		//        StringBuffer buf = new StringBuffer();
		//        List<Issue> issues = new ArrayList<>();
		//        issues.add(new Issue("test exception", "30", buf.append("<http:listener>scsdvdvdsv</http:listener>")));
		//        Report.addFlowMigrationIssue("test.xml", issues);
		repoObj.put("flowIssueCount", Report.getFlowIssueCount());
		repoObj.put("munitIssueCount", Report.getMunitIssueCount());
		repoObj.put("dependencyIssueCount", Report.getMissingDependencies().size());
		repoObj.put("flowIssuesMap", Report.getFlowMigrationIssues());
		repoObj.put("munitIssuesMap", Report.getMunitMigrationIssues());
		repoObj.put("dependencyIssues", Report.getMissingDependencies());
		repoObj.put("projectName", Report.getProjectName());
		repoObj.put("securePropIssues", Report.getSecurePorpIssues());
		repoObj.put("securePropIssueCount", Report.getSecurePorpIssues().size());
		repoObj.put("springIssueCount", Report.getSpringIssues().size());
		repoObj.put("springIssues", Report.getSpringIssues());
		//repoObj.put("percentageMigrated", (1-(float) GlobalVariables.issueCount/GlobalVariables.totalElementCount) * 100);
		repoObj.put("percentageMigrated", PercentageComputer.getSuccessPercentage());
		/* Get the template (uses cache internally) */

		try {
			Logger.selectLoggerLibrary(Logger.LIBRARY_NONE);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Configuration cfg = new Configuration();
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		//cfg.setClassForTemplateLoading(ReportGenerator.class, "/");
		//cfg.setClassLoaderForTemplateLoading(ReportGenerator.class.getClassLoader(), "/");
		//Template temp = cfg.getTemplate("resources/report.ftlh");
		InputStream reportIns = ReportGenerator.class.getResourceAsStream("/resources/report.ftlh");
		String source = IOUtils.toString(reportIns);
		Template temp = new Template("report",  source, cfg);


		OutputStream outputStream = new FileOutputStream(reportSavePath);
		/* Merge data-model with template */
		Writer out = new OutputStreamWriter(outputStream);
		temp.process(repoObj, out);
	}
}
