/**
 * 
 */
package com.cg.digi.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;



import org.apache.http.HttpResponse;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.cg.digi.logger.DigiLoggerUtils;
import com.cg.digi.logger.DigiLoggerUtils.LEVEL;
import com.cg.digi.model.Run;
import com.cg.digi.service.IAdminService;
import com.cg.digi.service.IExecutionConsoleService;
import com.cg.digi.utilities.FileUtils;
import com.cg.digi.utilities.JenkinsUtilities;
import com.cg.digi.utilities.RuntimeCommand;
import com.cg.digi.utilities.XMLParser;

/**
 * @author hapemmas
 *
 */

@Controller
@RequestMapping(value = "/console/")
public class ExecutionConsole {
	@Value("${appiumGridMaster}")
	String appiumGridMaster;
	@Value("${GalenAppiumDeliveryMaster}")
	String GalenAppiumDeliveryMaster;
	
	@Value("${digitalQAServicesHome}")
	String digitalQAServicesHome;

	@Value("${cafePerfectoDeliveryMaster}")
	String cafePerfectoDeliveryMaster;
	
	@Value("${cucumberAppiumDeliveryMaster}")
	String cucumberAppiumDeliveryMaster;

	@Value("${continonusDeliveryHome}")
	String continonusDeliveryHome;

	@Value("seetestAutomationPath")
	String seetestAutomationPath;
	
	@Value("${neoloadPerformancePath}")
	String neoloadPerformancePath;

	@Value("${continonusDeliveryMaster}")
	String continonusDeliveryMaster;
	
	@Value("${cafePerformanceDeliveryMaster}")
	String cafePerformanceDeliveryMaster;

	@Value("${jenkinsURL}")
	String jenkinsURL;

	@Value("${jenkinsUserName}")
	String jenkinsUserName;

	@Value("${jenkinsPassword}")
	String jenkinsPassword;

	@Value("${cucumberappiumhtml}")
	String cucumberappiumhtml;
	
	
	@Value("${cafeseetesthtml}")
	String cafeseetesthtml;
	
	@Value("${sprintestappium}")
	String sprintestappium;
	
	@Autowired
	IExecutionConsoleService executionConsoleService;

	@Autowired
	IAdminService adminService;

	@RequestMapping(value = "runHome")
	public String runhome(Model model, HttpSession session,
			@RequestParam("toolName") String toolName) {
		String projectid = (String) session.getAttribute("projectid");
		DigiLoggerUtils.log("Tool Nmae selected by user " + toolName,
				LEVEL.info);
		List<Run> runs = executionConsoleService.getRuns(projectid,
				adminService.getTool(toolName).getToolid());
		model.addAttribute("toolName", toolName);
		List<String> runNames = new ArrayList<String>();
		for (Run run : runs) {
			runNames.add(run.getRunName());

		}
		model.addAttribute("runs", runs);
		model.addAttribute("runNames", runNames);
		DigiLoggerUtils.log("Run belonging to tool :: " + toolName
				+ "  :: Run Values " + runs, LEVEL.info);

		return "console/runHome";
	}

	@RequestMapping(value = "newRun")
	public String newRun(Model model,
			@RequestParam("toolName") String toolName, HttpSession session) {
		Run run = new Run();
		// Using tool id as toolName
		model.addAttribute("newRun", run);
		model.addAttribute(
				"projectName",
				adminService.getProject(
						(String) session.getAttribute("projectid"))
						.getProjectName());
		model.addAttribute("toolName", toolName);
		return "console/newRun";
	}

	@RequestMapping(value = "addNewRunCafePerformance")
	public String addNewRun1(
			Model model,
			@ModelAttribute("newRun") Run run,
			@RequestParam(value = "zippedBinFolder", required = false) MultipartFile zippedBinFolder,
			@RequestParam(value = "configZipFolder", required = false) MultipartFile configZipFolder,
			@RequestParam(value = "dataSheet", required = false) MultipartFile dataSheet,
			@RequestParam(value = "execution", required = false) MultipartFile execution,
			@RequestParam(value = "masterConfig", required = false) MultipartFile masterConfig,
			@RequestParam(value = "nlp", required = false) MultipartFile nlp,
			@RequestParam("toolName") String toolName,
			HttpSession session) {

		String projectid = (String) session.getAttribute("projectid");
		System.out.println("time :"+System.currentTimeMillis());
		try {
			
			/*try {
				// CommandLine.run(seetestAutomationPath);
				RuntimeCommand command = new RuntimeCommand();
				String output = command.commandLine(neoloadPerformancePath);
				DigiLoggerUtils.log(
						"Launching cafe performance in Digi Assure Server machine :: "
								+ output, LEVEL.info);
			} catch (Exception e) {
				DigiLoggerUtils.log(
						"In Exception handling part " + e.getMessage()
								+ "command output ", LEVEL.error);
			}*/

			String projectName = adminService.getProject(projectid)
					.getProjectName();
			String userName = (String) session.getAttribute("userNameNoSpace");

			DigiLoggerUtils.log(
					"Tool Name  in cafe performance add run first check--->>|"
							+ toolName, LEVEL.info);

			File runHome = executionConsoleService.getFinalRunPath(projectName,
					userName, toolName, digitalQAServicesHome,
					run.getRunName());
			FileUtils.uploadMultipartFile(runHome.getAbsolutePath(),
					zippedBinFolder);
			
			File neoloadNlp = FileUtils.createFolder(
					runHome.getAbsolutePath(), "Neoload");
			FileUtils.uploadMultipartFile(neoloadNlp.getAbsolutePath(),
					nlp);
			FileUtils.uploadMultipartFile(neoloadNlp.getAbsolutePath(),
					configZipFolder);
			// unzip config file
			FileUtils.unzippingFolder(neoloadNlp.getAbsolutePath() + "/"
					+ configZipFolder.getOriginalFilename(),
					neoloadNlp.getAbsolutePath());
			//edit repository file D:\digi_workspace\DigitalQAServices\DigiAssure\Deepika_Govindaiah\CGPF_Neoload\Neo1\NeoSeeTest.bat
			//XMLParser.editXMLNode(neoloadNlp.getAbsolutePath() + "/repository.xml", neoloadNlp.getAbsolutePath() + "/repository.xml", "value", ".bat", runHome.getAbsolutePath()+"/NeoSeeTest.bat");
			String batPath =runHome.getAbsolutePath()+"/NeoSeeTest.bat";
			String xmlNode="    <custom-action-parameter name=\"arg1\" type=\"TEXT\" value=\""+batPath+"\"/>";
			FileUtils.updateRepositoryLine(".bat", xmlNode , neoloadNlp.getAbsolutePath() + "/repository.xml");
			
			
			FileUtils.zipNeoLoadConfigFiles(neoloadNlp.getAbsolutePath());
			
			String scenarioName = run.getScenarioName();
			System.out.println("scenario name:"+scenarioName);
			
			File runHomeConfig = FileUtils.createFolder(
					runHome.getAbsolutePath(), "Configuration");
			FileUtils.uploadMultipartFile(runHomeConfig.getAbsolutePath(),
					masterConfig);
			FileUtils.uploadMultipartFile(runHomeConfig.getAbsolutePath(),
					execution);

			String[] data = dataSheet.getOriginalFilename().split("_");
			File runHomeData = FileUtils.createFolder(
					runHome.getAbsolutePath(), "data");
			File runHomeDataClass = FileUtils.createFolder(
					runHomeData.getAbsolutePath(), data[0]);
			// String className = data[0];
			FileUtils.uploadMultipartFile(runHomeDataClass.getAbsolutePath(),
					dataSheet);

			FileUtils.copyPasteFolderContents(cafePerformanceDeliveryMaster,
					runHome.getAbsolutePath());
			FileUtils.unzippingFolder(runHome.getAbsolutePath() + "/"
					+ zippedBinFolder.getOriginalFilename(),
					runHome.getAbsolutePath());
			
			
			//System.out.println("nlp path :"+runHome.getAbsolutePath()+"\\Neoload\\"+nlp.getOriginalFilename());
			String neoloadPath=neoloadPerformancePath;
			FileUtils.replaceFileContents(runHome.getAbsolutePath()+"\\", "build.xml", "NLP",runHome.getAbsolutePath().replace("\\", "/")+"/Neoload/"+nlp.getOriginalFilename(),"SCENARIO1",scenarioName,"EXE",neoloadPath , "REPORT1" , runHome.getAbsolutePath().replace("\\", "/")+"/AllResults/"+"neoReport.html");
			
			// boolean result = FileUtils.createNewFile(runHome, "run.bat",
			// "CALL java -cp bin;lib\\*; com.capgemini.scripts.google.Google >> output.txt \n pause \n timeout /t 30 /nobreak");
			boolean result = FileUtils.createNewFile(
					runHome,
					"run.bat",
					"D: && cd \"" + runHome + "\" && ant -f build.xml neo");
			DigiLoggerUtils.log(
					"Creating Run Batch file for triggering RunName :  "
							+ run.getRunName() + " :: Output ::" + result,
					LEVEL.info);
			DigiLoggerUtils.log(
					"Output File Data :: :: "
							+ FileUtils.getFileData(new File(runHome
									+ "/output.txt")), LEVEL.info);
			
			boolean result1 = FileUtils.createNewFile(
					runHome,
					"NeoSeeTest.bat",
					"D: && cd \"" + runHome + "\" && ant -f buildCMAF.xml Executioner");
			DigiLoggerUtils.log(
					"Creating Run Batch file for triggering RunName :  "
							+ run.getRunName() + " :: Output ::" + result1,
					LEVEL.info);
			DigiLoggerUtils.log(
					"Output File Data :: :: "
							+ FileUtils.getFileData(new File(runHome
									+ "/output.txt")), LEVEL.info);
			

			// TODO
			// review and change only buildid based on jenkins integration
			run.setBuildid("1");
			run.setToolid(adminService.getTool(toolName).getToolid());
			DigiLoggerUtils.log("Tool ID in Controller -- >"
					+ adminService.getTool(toolName).getToolid(), LEVEL.info);
			run.setRunHome(runHome.getAbsolutePath().replace("\\", "/"));
			run.setUserid((String) session.getAttribute("userid"));

			if (run.getRunName() != null) {

				System.out.println("IN run name ");
				String configXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><project>   <actions />  <description>Creating Job from DigiAssure :CAFE_SEETEST_FUNCTIONAL TESTING TEST SUITE TRIGGER BASED ON BUILD STATUS :: Run Name ::  "
						+ run.getRunName()
						+ "</description><keepDependencies>false</keepDependencies><properties><com.sonyericsson.jenkins.plugins.bfa.model.ScannerJobProperty plugin=\"build-failure-analyzer@1.13.4\"><doNotScan>false</doNotScan></com.sonyericsson.jenkins.plugins.bfa.model.ScannerJobProperty></properties><scm class=\"hudson.scm.NullSCM\" /><canRoam>true</canRoam><disabled>false</disabled><blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding><blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding><triggers /><concurrentBuild>false</concurrentBuild><builders><hudson.tasks.BatchFile><command>call "
						+ runHome
						+ "\\run.bat"
						+ "</command></hudson.tasks.BatchFile></builders><publishers><hudson.tasks.BuildTrigger><childProjects /><threshold><name>FAILURE</name><ordinal>2</ordinal><color>RED</color><completeBuild>true</completeBuild></threshold></hudson.tasks.BuildTrigger></publishers><buildWrappers/></project>";

				// Save for Later
				if (run.getScheduledStatus().equals("DonotRun")) {
					System.out.println("in do not run");
					// Jenkins Job Schedule
					int statusCode = JenkinsUtilities.newJenkinsJob(jenkinsURL,
							run.getRunName(), configXML);
					DigiLoggerUtils.log(
							"Run is Saved and Jenkuins Job is created -->"
									+ run.getRunName()
									+ " :: Jenkins Jon Status :: "
									+ run.getScheduledStatus() + ": "
									+ statusCode, LEVEL.info);
					run.setScheduledtime(null);
				}
				// Save and Run
				else if (run.getScheduledStatus().equals("RunOnSave")) {
					System.out.println("IN Run on save ");
					int statusCode = JenkinsUtilities.newJenkinsJob(jenkinsURL,
							run.getRunName(), configXML);
					DigiLoggerUtils.log("Creation of Job : " + jenkinsURL
							+ "      Job Output Status Code : " + statusCode,
							LEVEL.info);
					// Build Jenkins Job
					JenkinsUtilities.buildJob(jenkinsURL, run.getRunName());
					DigiLoggerUtils.log(
							"Build Job response : "
									+ JenkinsUtilities.buildJob(jenkinsURL,
											run.getRunName()), LEVEL.info);
					// Get build Status
					run.setScheduledStatus("Pending");
					run.setScheduledtime(null);
				}
				// Scheduled At
				else if (run.getScheduledStatus().equals("ScheduledAt")) {
					System.out.println("IN Scheduled Time "
							+ run.getScheduledtime());
					int statusCode = JenkinsUtilities.newJenkinsJob(jenkinsURL,
							run.getRunName(), configXML);
					DigiLoggerUtils.log("Creation of Job : " + jenkinsURL
							+ "      Job Output Status Code : " + statusCode,
							LEVEL.info);
					// Build Jenkins Job
					HttpResponse res = JenkinsUtilities.buildJob(jenkinsURL,
							run.getRunName(),
							Integer.parseInt(run.getScheduledtime()));
					DigiLoggerUtils
							.log("Triggering build code after job creation : "
									+ res, LEVEL.info);
					// Get build Status
					String buildStatus = null;
					try {
						buildStatus = JenkinsUtilities.latestBuildStatus(
								jenkinsURL, run.getRunName());
						if (buildStatus.contains("<html><head><title>")
								|| buildStatus
										.contains("H1 {font-family:Tahoma,Arial,san")) {
							buildStatus = "Pending";
						}
						if(buildStatus.contains("")||buildStatus.contains(" ")){
							buildStatus="Jenkins Offline";
						}
						DigiLoggerUtils.log(
								"Build Status after triggering Job  "
										+ buildStatus, LEVEL.info);
					} catch (Exception e) {
						System.out.println(buildStatus);
						buildStatus = "Pending";
						DigiLoggerUtils.log(
								"Error while reterving build status "
										+ e.getStackTrace(), LEVEL.error);
					}
					run.setScheduledStatus(buildStatus);
					run.setScheduledtime(null);

				}
				// Database add run
				// Database add run
				if(executionConsoleService.checkRun(run.getRunid())){
					if(	executionConsoleService.ModifyRun(run)){
				DigiLoggerUtils.log(
						"Run Details are modified in database:: Run Details :: "
								+ run, LEVEL.info);
				model.addAttribute("successRun", run.getRunName()
						+ " Modified Successfully ");
					}else{
						DigiLoggerUtils.log(
								"Run Details modification failed in database:: Run Details :: "
										+ run, LEVEL.error);
						model.addAttribute("successRun" + run.getRunName()
								+ " Failed to modify Run");
					}
				}else{
				run = executionConsoleService.addRun(run);
				DigiLoggerUtils.log(
						"Run Details Added to database and redirecting return Run Details :: "
								+ run, LEVEL.info);
				if (run.getRunName() == null)
					model.addAttribute("successRun" + run.getRunName()
							+ " Failed to add Run");
				else
					model.addAttribute("successRun", run.getRunName()
							+ " Added Successfully ");
				}

				System.out.println("OUt of runname after db");

			}
		} catch (Exception e) {
			e.printStackTrace();
			DigiLoggerUtils.log("Error while creating New Run :: Cafe Neoload"
					+ e.getMessage(), LEVEL.info);
		}

		// Runs Data
		List<Run> runs = executionConsoleService.getRuns((String) session
				.getAttribute("projectid"), adminService.getTool(toolName)
				.getToolid());
		List<String> runNames = new ArrayList<String>();
		for (Run run1 : runs) {
			runNames.add(run1.getRunName());

		}
		model.addAttribute("runs", runs);
		model.addAttribute("runNames", runNames);
		model.addAttribute("toolName", toolName);
		return "console/runHome";
	
		
		
	}
	
	//view report
		@RequestMapping(value = "viewReport")
		@ResponseBody
		public String viewReport(Model model, @RequestParam("runid") String runid,
				@RequestParam("toolName") String toolName, @RequestParam("runHome") String runHome, HttpSession session) {
			
			String htmlRelativePath="";
			
			System.out.println("in view report : run id " + runid);
			System.out.println("toolName :" + toolName);
			System.out.println("runHome :" + runHome);
			
			runHome=executionConsoleService.getRunHome(Integer.parseInt(runid));
			System.out.println("runHome from db:" + runHome);
			
			String allResultsPath=runHome+"\\AllResults";
			
			File sourceHTMLPath = new File(allResultsPath);
			if (sourceHTMLPath.exists() && sourceHTMLPath.isDirectory()) {
			
			String tomcatDirectory = ExecutionConsole.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath().split("WEB-INF")[0];
			String tomcatReports = tomcatDirectory+"reports/"+runid+"/AllResults";
			
			System.out.println("allResultsPath :"+allResultsPath);
			System.out.println("tomcatReports :"+tomcatReports);
			File f1 = new File(tomcatReports);
			f1.mkdirs();
			
			try{
			String source = new String(allResultsPath);
			String dest = new String(tomcatReports);
			FileUtils.copyPasteFolderContents(source, dest);
			}catch(Exception e){
				System.out.println("Error in copy paste report folder "+e.getMessage());
			}
			
			String htmlPath="";
			String filename="";
			
			try{
			if(toolName.equals("CGAF_Cucumber_Appium")){	
			//htmlPath=tomcatDirectory+"/HTML"+"/month"+"/date";
			htmlPath=tomcatDirectory+"/reports/"+runid+"/"+cucumberappiumhtml;
			File f = FileUtils.getTheNewestFile(htmlPath, "html"); 
			filename=f.getName();
			htmlRelativePath = "../reports/"+runid+"/"+cucumberappiumhtml.trim()+"/"+filename;
			
			}else if(toolName.equals("CGAF_Usability")){	
				htmlPath=tomcatDirectory+"/reports/"+runid+"/"+cafeseetesthtml;
				File f = FileUtils.getTheNewestFile(htmlPath, "html"); 
				filename=f.getName();
				htmlRelativePath = "../reports/"+runid+"/"+cafeseetesthtml.trim()+"/"+filename;
				
			}
			else if(toolName.equals("CGAF_LayoutTesting")){	
				htmlPath=tomcatDirectory+"/reports/"+runid+"/"+sprintestappium;
				File f = FileUtils.getTheNewestFile(htmlPath, "html"); 
				filename=f.getName();
				htmlRelativePath = "../reports/"+runid+"/"+sprintestappium.trim()+"/"+"report.html";
				
			}
			}catch(Exception e){
				System.out.println("Error finding html file in "+htmlPath +" "+e.getMessage());
			}
			
			}
	    	
	    	System.out.println("htmlRelativePath :"+htmlRelativePath);
	    	
	    	return htmlRelativePath;
		}

	
	
	@RequestMapping(value = "addNewRunCafeSeetest")
	public String addNewRun(
			Model model,
			@ModelAttribute("newRun") Run run,
			@RequestParam(value = "zippedBinFolder", required = false) MultipartFile zippedBinFolder,
			@RequestParam(value = "dataSheet", required = false) MultipartFile dataSheet,
			@RequestParam(value = "execution", required = false) MultipartFile execution,
			@RequestParam(value = "masterConfig", required = false) MultipartFile masterConfig,
			@RequestParam("toolName") String toolName, HttpSession session) {
		String projectid = (String) session.getAttribute("projectid");
		session.setAttribute("projectid", projectid);

		try {
			// Start Seetest Automation TODO
			try {
				// CommandLine.run(seetestAutomationPath);
				RuntimeCommand command = new RuntimeCommand();
				String output = command.commandLine(seetestAutomationPath);
				DigiLoggerUtils.log(
						"Launching seetest Automation in Digi Assure Server machine :: "
								+ output, LEVEL.info);
			} catch (Exception e) {
				DigiLoggerUtils.log(
						"In Exception handling part " + e.getMessage()
								+ "command output ", LEVEL.error);
			}

			String projectName = adminService.getProject(projectid)
					.getProjectName();
			String userName = (String) session.getAttribute("userNameNoSpace");

			DigiLoggerUtils.log(
					"Tool Name  in cafe seetest add run first check--->>|"
							+ toolName, LEVEL.info);

			File runHome = executionConsoleService.getFinalRunPath(projectName,
					userName, toolName, continonusDeliveryHome,
					run.getRunName());
			FileUtils.uploadMultipartFile(runHome.getAbsolutePath(),
					zippedBinFolder);

			File runHomeConfig = FileUtils.createFolder(
					runHome.getAbsolutePath(), "Configuration");
			FileUtils.uploadMultipartFile(runHomeConfig.getAbsolutePath(),
					masterConfig);
			FileUtils.uploadMultipartFile(runHomeConfig.getAbsolutePath(),
					execution);

			String[] data = dataSheet.getOriginalFilename().split("_");
			File runHomeData = FileUtils.createFolder(
					runHome.getAbsolutePath(), "data");
			File runHomeDataClass = FileUtils.createFolder(
					runHomeData.getAbsolutePath(), data[0]);
			// String className = data[0];
			FileUtils.uploadMultipartFile(runHomeDataClass.getAbsolutePath(),
					dataSheet);

			FileUtils.copyPasteFolderContents(continonusDeliveryMaster,
					runHome.getAbsolutePath());
			FileUtils.unzippingFolder(runHome.getAbsolutePath() + "/"
					+ zippedBinFolder.getOriginalFilename(),
					runHome.getAbsolutePath());

			// boolean result = FileUtils.createNewFile(runHome, "run.bat",
			// "CALL java -cp bin;lib\\*; com.capgemini.scripts.google.Google >> output.txt \n pause \n timeout /t 30 /nobreak");
			boolean result = FileUtils.createNewFile(
					runHome,
					"run.bat",
					"CALL cd \"" + runHome + "\" && JAVA -cp " + runHome
							+ "\\bin;" + runHome
							+ "\\lib\\* com.capgemini.scripts."
							+ data[0].toLowerCase() + "." + data[0]);
			DigiLoggerUtils.log(
					"Creating Run Batch file for triggering RunName :  "
							+ run.getRunName() + " :: Output ::" + result,
					LEVEL.info);
			DigiLoggerUtils.log(
					"Output File Data :: :: "
							+ FileUtils.getFileData(new File(runHome
									+ "/output.txt")), LEVEL.info);

			// TODO
			// review and change only buildid based on jenkins integration
			run.setBuildid("1");
			run.setToolid(adminService.getTool(toolName).getToolid());
			DigiLoggerUtils.log("Tool ID in Controller -- >"
					+ adminService.getTool(toolName).getToolid(), LEVEL.info);
			run.setRunHome(runHome.getAbsolutePath().replace("\\", "/"));
			run.setUserid((String) session.getAttribute("userid"));

			if (run.getRunName() != null) {

				System.out.println("IN run name ");
				String configXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><project>   <actions />  <description>Creating Job from DigiAssure :CAFE_SEETEST_FUNCTIONAL TESTING TEST SUITE TRIGGER BASED ON BUILD STATUS :: Run Name ::  "
						+ run.getRunName()
						+ "</description><keepDependencies>false</keepDependencies><properties><com.sonyericsson.jenkins.plugins.bfa.model.ScannerJobProperty plugin=\"build-failure-analyzer@1.13.4\"><doNotScan>false</doNotScan></com.sonyericsson.jenkins.plugins.bfa.model.ScannerJobProperty></properties><scm class=\"hudson.scm.NullSCM\" /><canRoam>true</canRoam><disabled>false</disabled><blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding><blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding><triggers /><concurrentBuild>false</concurrentBuild><builders><hudson.tasks.BatchFile><command>call "
						+ runHome
						+ "\\run.bat"
						+ "</command></hudson.tasks.BatchFile></builders><publishers><hudson.tasks.BuildTrigger><childProjects /><threshold><name>FAILURE</name><ordinal>2</ordinal><color>RED</color><completeBuild>true</completeBuild></threshold></hudson.tasks.BuildTrigger></publishers><buildWrappers/></project>";

				// Save for Later
				if (run.getScheduledStatus().equals("DonotRun")) {
					System.out.println("in do not run");
					// Jenkins Job Schedule
					int statusCode = JenkinsUtilities.newJenkinsJob(jenkinsURL,
							run.getRunName(), configXML);
					DigiLoggerUtils.log(
							"Run is Saved and Jenkuins Job is created -->"
									+ run.getRunName()
									+ " :: Jenkins Jon Status :: "
									+ run.getScheduledStatus() + ": "
									+ statusCode, LEVEL.info);
					run.setScheduledtime(null);
				}
				// Save and Run
				else if (run.getScheduledStatus().equals("RunOnSave")) {
					System.out.println("IN Run on save ");
					int statusCode = JenkinsUtilities.newJenkinsJob(jenkinsURL,
							run.getRunName(), configXML);
					DigiLoggerUtils.log("Creation of Job : " + jenkinsURL
							+ "      Job Output Status Code : " + statusCode,
							LEVEL.info);
					// Build Jenkins Job
					JenkinsUtilities.buildJob(jenkinsURL, run.getRunName());
					DigiLoggerUtils.log(
							"Build Job response : "
									+ JenkinsUtilities.buildJob(jenkinsURL,
											run.getRunName()), LEVEL.info);
					// Get build Status
					run.setScheduledStatus("Pending");
					run.setScheduledtime(null);
				}
				// Scheduled At
				else if (run.getScheduledStatus().equals("ScheduledAt")) {
					System.out.println("IN Scheduled Time "
							+ run.getScheduledtime());
					int statusCode = JenkinsUtilities.newJenkinsJob(jenkinsURL,
							run.getRunName(), configXML);
					DigiLoggerUtils.log("Creation of Job : " + jenkinsURL
							+ "      Job Output Status Code : " + statusCode,
							LEVEL.info);
					// Build Jenkins Job
					HttpResponse res = JenkinsUtilities.buildJob(jenkinsURL,
							run.getRunName(),
							Integer.parseInt(run.getScheduledtime()));
					DigiLoggerUtils
							.log("Triggering build code after job creation : "
									+ res, LEVEL.info);
					// Get build Status
					String buildStatus = null;
					try {
						buildStatus = JenkinsUtilities.latestBuildStatus(
								jenkinsURL, run.getRunName());
						if (buildStatus.contains("<html><head><title>")
								|| buildStatus
										.contains("H1 {font-family:Tahoma,Arial,san")) {
							buildStatus = "Pending";
						}
						if(buildStatus.contains("")||buildStatus.contains(" ")){
							buildStatus="Jenkins Offline";
						}
						DigiLoggerUtils.log(
								"Build Status after triggering Job  "
										+ buildStatus, LEVEL.info);
					} catch (Exception e) {
						buildStatus = "Pending";
						DigiLoggerUtils.log(
								"Error while reterving build status "
										+ e.getStackTrace(), LEVEL.error);
					}
					run.setScheduledStatus(buildStatus);
					run.setScheduledtime(null);

				}
				// Database add run
				if(executionConsoleService.checkRun(run.getRunid())){
					if(	executionConsoleService.ModifyRun(run)){
				DigiLoggerUtils.log(
						"Run Details are modified in database:: Run Details :: "
								+ run, LEVEL.info);
				model.addAttribute("successRun", run.getRunName()
						+ " Modified Successfully ");
					}else{
						DigiLoggerUtils.log(
								"Run Details modification failed in database:: Run Details :: "
										+ run, LEVEL.error);
						model.addAttribute("successRun" + run.getRunName()
								+ " Failed to modify Run");
					}
				}else{
				run = executionConsoleService.addRun(run);
				DigiLoggerUtils.log(
						"Run Details Added to database and redirecting return Run Details :: "
								+ run, LEVEL.info);
				if (run.getRunName() == null)
					model.addAttribute("successRun" + run.getRunName()
							+ " Failed to add Run");
				else
					model.addAttribute("successRun", run.getRunName()
							+ " Added Successfully ");
				}

				System.out.println("OUt of runname after db");

			}
		} catch (Exception e) {
			e.printStackTrace();
			DigiLoggerUtils.log("Error while creating New Run :: Cafe Seetest"
					+ e.getMessage(), LEVEL.info);
		}

		// Runs Data
		List<Run> runs = executionConsoleService.getRuns((String) session
				.getAttribute("projectid"), adminService.getTool(toolName)
				.getToolid());
		List<String> runNames = new ArrayList<String>();
		for (Run run1 : runs) {
			runNames.add(run1.getRunName());

		}
		model.addAttribute("runs", runs);
		model.addAttribute("runNames", runNames);
		model.addAttribute("toolName", toolName);
		return "console/runHome";
	}

	@RequestMapping(value = "getFramework")
	public String getFramework(Model model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam("toolName") String toolName) {

		DigiLoggerUtils.log(
				"Framework Download Request for Tool: "
						+ executionConsoleService
								.getToolNameToDownloadFramework(toolName),
				LEVEL.info);
		FileUtils.downloadFile(executionConsoleService
				.getToolNameToDownloadFramework(toolName), session, response,
				request);

		// Runs Data
		List<Run> runs = executionConsoleService.getRuns(
				(String) session.getAttribute("userid"),
				adminService.getTool(toolName).getToolid());
		List<String> runNames = new ArrayList<String>();
		for (Run run : runs) {
			runNames.add(run.getRunName());

		}
		model.addAttribute("runs", runs);
		model.addAttribute("runNames", runNames);
		model.addAttribute("toolName", toolName);

		return "console/runHome";
	}

	@RequestMapping(value = "getReport")
	public void getReport(Model model, @RequestParam("runHome") String runHome,
			HttpSession session, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			FileUtils.zipFolder(runHome + "/AllResults", runHome
					+ "/AllResults.zip");
			DigiLoggerUtils.log(
					"Successfully downloaded the results for RunHome Path :: "
							+ runHome, LEVEL.info);
		} catch (Exception e) {
			DigiLoggerUtils.log(
					"Error while downloading Frameworkfrom RunHome path :: "
							+ runHome + ": Error is :" + e.getStackTrace(),
					LEVEL.error);
		}
		FileUtils.downloadFile(runHome + "/AllResults.zip", session, response,
				request);
	}

	@RequestMapping(value = "editRun")
	public String editRun(Model model, @RequestParam("runId") String runId,@RequestParam("runName") String runName, @RequestParam("toolName") String toolName, HttpSession session) {
System.out.println(runId);
		Run run = new Run();
		model.addAttribute("runName", runName);
		model.addAttribute("runId", runId);
		model.addAttribute("newRun", run);
		model.addAttribute(
				"projectName",
				adminService.getProject(
						(String) session.getAttribute("projectid"))
						.getProjectName());
		model.addAttribute("toolName", toolName);
	
		return "console/newRun";
	}
	
/*	@RequestMapping(value = "editRunCafeSeetest")
	public String editNewRun(Model model,
			@ModelAttribute("newRun") Run run,
			@RequestParam(value = "zippedBinFolder", required = false) MultipartFile zippedBinFolder,
			@RequestParam(value = "dataSheet", required = false) MultipartFile dataSheet,
			@RequestParam(value = "execution", required = false) MultipartFile execution,
			@RequestParam(value = "masterConfig", required = false) MultipartFile masterConfig,
			@RequestParam("toolName") String toolName, HttpSession session){
		
		System.out.println(run);
		
		model.addAttribute("runs", runs);
		model.addAttribute("runNames", runNames);
		model.addAttribute("toolName", toolName);
		return "console/editRun";
	}*/

	@RequestMapping(value = "deleteRun")
	public String deleteRun(Model model, @RequestParam("runid") String runid,
			@RequestParam("toolName") String toolName, HttpSession session) {
		model.addAttribute("deleteMessage",
				executionConsoleService.deleteRun(runid).getRunName()
						+ " Deleted  Successfully");
		List<Run> runs = executionConsoleService.getRuns((String) session
				.getAttribute("projectid"), adminService.getTool(toolName)
				.getToolid());
		List<String> runNames = new ArrayList<String>();
		for (Run run : runs) {
			runNames.add(run.getRunName());

		}
		model.addAttribute("runs", runs);
		model.addAttribute("runNames", runNames);
		model.addAttribute("toolName", toolName);
		return "console/runHome";
	}

	@RequestMapping(value = "triggerRun")
	public String triggerRun(Model model,
			@RequestParam("runName") String runName,
			@RequestParam("toolName") String toolName, HttpSession session) {
		JenkinsUtilities.buildJob(jenkinsURL, runName);
		List<Run> runs = executionConsoleService.getRuns((String) session
				.getAttribute("projectid"), adminService.getTool(toolName)
				.getToolid());
		List<String> runNames = new ArrayList<String>();
		for (Run run : runs) {
			runNames.add(run.getRunName());

		}
		model.addAttribute("runs", runs);
		model.addAttribute("runNames", runNames);
		model.addAttribute("toolName", toolName);
		model.addAttribute("triggerMessage", "Triggered Run : " + runName);
		return "console/runHome";
	}

	@SuppressWarnings({ "unchecked" })
	@RequestMapping(value = "jenkinsrunStatus")
	@ResponseBody
	public String jenkinsrunStatus(Model model,
			@RequestParam("runNames") List<String> runNames, HttpSession session) {
		JSONObject runObject = new JSONObject();
		for (String runName : runNames) {
			runName = runName.replace("[", "");
			runName = runName.replace("]", "");
			String buildStatus = " ";
			try {
				buildStatus = JenkinsUtilities.latestBuildStatus(jenkinsURL,
						runName);
				if (buildStatus.contains("<html><head><title>")
						|| buildStatus
								.contains("H1 {font-family:Tahoma,Arial,san")) {
					buildStatus = "Pending";
				}
				DigiLoggerUtils.log("Build Status after triggering Job  "
						+ buildStatus, LEVEL.info);
			} catch (Exception e) {
				
				buildStatus = "Pending";
				DigiLoggerUtils.log(
						"Error while reterving build status "
								+ e.getStackTrace(), LEVEL.error);
			}
			runObject.put(runName, buildStatus);
			executionConsoleService.modifyRunStatus(runName, buildStatus);
		}
		return runObject.toJSONString();
	}

	// Cucumber Mobile Web
	@RequestMapping(value = "addNewRunCucumber")
	public String addNewRunCucumber(
			Model model,
			@ModelAttribute("newRun") Run run,
			@RequestParam(value = "zippedBinFolder", required = false) MultipartFile zippedBinFolder,
			@RequestParam(value = "configFile", required = false) MultipartFile configFile,
			@RequestParam("toolName") String toolName, HttpSession session) {

        String projectid = (String) session.getAttribute("projectid");
        session.setAttribute("projectid", projectid);

        try {
               String projectName = adminService.getProject(projectid)
                            .getProjectName();
               String userName = (String) session.getAttribute("userNameNoSpace");
System.out.println("tool name:"+toolName);
               DigiLoggerUtils.log(
                            "Tool Name  in Cucumber Appium add run first check--->>|"
                                          + toolName, LEVEL.info);
               File runHome = executionConsoleService.getFinalRunPath(projectName,
                            userName, toolName, continonusDeliveryHome,
                            run.getRunName());
               FileUtils.uploadMultipartFile(runHome.getAbsolutePath(),
                            zippedBinFolder);
               System.out.println("RunHome:"+runHome.getAbsolutePath());
               /*File runHomeConfig = FileUtils.createFolder(
                            runHome.getAbsolutePath(), "configuration");
               FileUtils.uploadMultipartFile(runHomeConfig.getAbsolutePath(),
                            configFile);*/
               
               FileUtils.uploadMultipartFile(runHome.getAbsolutePath(),
                            configFile);

               FileUtils.copyPasteFolderContents(cucumberAppiumDeliveryMaster,
                            runHome.getAbsolutePath());

               FileUtils.unzippingFolder(runHome.getAbsolutePath() + "/"
                            + zippedBinFolder.getOriginalFilename(),
                            runHome.getAbsolutePath());
               
               FileUtils.unzippingFolder(runHome.getAbsolutePath() + "/"
                            + configFile.getOriginalFilename(),
                            runHome.getAbsolutePath());
               
               boolean result = FileUtils
                            .createNewFile(
                                          runHome,
                                          "run.bat",
                                          "CALL cd \""
                                                        + runHome                                                        
                                                        + "\" && CALL d: && JAVA -cp "
                                                        + runHome
                                                        + "\\bin;"
                                                        +runHome
                                                        +"\\lib\\*;"
                                                        + runHome
                                                        + "\\bin\\*  com.cukes.TestNGMain.TestNGMainRunner");
               
               /*boolean result = FileUtils
                       .createNewFile(
                                     runHome,
                                     "run.bat",
                                     "set classpath="+runHome+"\\bin;"+runHome+"\\lib\\*\n"
                                     +"java org.testng.TestNG "+runHome+"\\Config\\suite.xml");*/
               
               
               DigiLoggerUtils.log(
                            "Creating Run Batch file for triggering RunName :  "
                                          + run.getRunName() + " :: Output ::" + result,
                            LEVEL.info);
               DigiLoggerUtils.log(
                            "Output File Data :: :: "
                                          + FileUtils.getFileData(new File(runHome
                                                        + "/output.txt")), LEVEL.info);

               // TODO
               // review and change only buildid based on jenkins integration
               run.setBuildid("1");
               run.setToolid(adminService.getTool(toolName).getToolid());
               DigiLoggerUtils.log("Tool ID in Controller -- >"
                            + adminService.getTool(toolName).getToolid(), LEVEL.info);
               run.setRunHome(runHome.getAbsolutePath().replace("\\", "/"));
               run.setUserid((String) session.getAttribute("userid"));

               if (run.getRunName() != null) {

                     String configXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><project>   <actions />  <description>Creating Job from DigiAssure :CUCUMBER_APPIUM_BDD TESTING TEST SUITE TRIGGER BASED ON BUILD STATUS :: Run Name ::  "
                                   + run.getRunName()
                                   + "</description><keepDependencies>false</keepDependencies><properties><com.sonyericsson.jenkins.plugins.bfa.model.ScannerJobProperty plugin=\"build-failure-analyzer@1.13.4\"><doNotScan>false</doNotScan></com.sonyericsson.jenkins.plugins.bfa.model.ScannerJobProperty></properties><scm class=\"hudson.scm.NullSCM\" /><canRoam>true</canRoam><disabled>false</disabled><blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding><blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding><triggers /><concurrentBuild>false</concurrentBuild><builders><hudson.tasks.BatchFile><command>call "
                                   + runHome
                                   + "\\run.bat"
                                   + "</command></hudson.tasks.BatchFile></builders><publishers><hudson.tasks.BuildTrigger><childProjects /><threshold><name>FAILURE</name><ordinal>2</ordinal><color>RED</color><completeBuild>true</completeBuild></threshold></hudson.tasks.BuildTrigger></publishers><buildWrappers/></project>";

                     // Save for Later
                     if (run.getScheduledStatus().equals("DonotRun")) {
                            // Jenkins Job Schedule
                            int statusCode = JenkinsUtilities.newJenkinsJob(jenkinsURL,run.getRunName(), configXML);
                            DigiLoggerUtils.log(
                                          "Run is Saved and Jenkuins Job is created -->"
                                                        + run.getRunName()
                                                        + " :: Jenkins Jon Status :: "
                                                        + run.getScheduledStatus() + ": "
                                                        + statusCode, LEVEL.info);
                            run.setScheduledtime(null);
                     }
                     // Save and Run
                     else if (run.getScheduledStatus().equals("RunOnSave")) {
                            int statusCode = JenkinsUtilities.newJenkinsJob(jenkinsURL,
                                          run.getRunName(), configXML);
                            DigiLoggerUtils.log("Creation of Job : " + jenkinsURL
                                          + "      Job Output Status Code : " + statusCode,
                                          LEVEL.info);
                            // Build Jenkins Job
                            JenkinsUtilities.buildJob(jenkinsURL, run.getRunName());
                            DigiLoggerUtils.log(
                                          "Build Job response : "
                                                        + JenkinsUtilities.buildJob(jenkinsURL,
                                                                     run.getRunName()), LEVEL.info);
                            // Get build Status
                            run.setScheduledStatus("Pending");
                            run.setScheduledtime(null);
                     }
                     // Scheduled At
                     else if (run.getScheduledStatus().equals("ScheduledAt")) {
                            int statusCode = JenkinsUtilities.newJenkinsJob(jenkinsURL,
                                          run.getRunName(), configXML);
                            DigiLoggerUtils.log("Creation of Job : " + jenkinsURL
                                          + "      Job Output Status Code : " + statusCode,
                                          LEVEL.info);
                            // Build Jenkins Job
                            HttpResponse res = JenkinsUtilities.buildJob(jenkinsURL,
                                          run.getRunName(),
                                          Integer.parseInt(run.getScheduledtime()));
                            DigiLoggerUtils
                                          .log("Triggering build code after job creation : "
                                                        + res, LEVEL.info);
                            // Get build Status
                            String buildStatus = null;
                            try {
                                   buildStatus = JenkinsUtilities.latestBuildStatus(
                                                 jenkinsURL, run.getRunName());
                                   if (buildStatus.contains("<html><head><title>")
                                                 || buildStatus
                                                               .contains("H1 {font-family:Tahoma,Arial,san")) {
                                          buildStatus = "Pending";
                                   }
                                   DigiLoggerUtils.log(
                                                 "Build Status after triggering Job  "
                                                               + buildStatus, LEVEL.info);
                            } catch (Exception e) {
                                   System.out.println(buildStatus);
                                   buildStatus = "Pending";
                                   DigiLoggerUtils.log(
                                                 "Error while reterving build status "
                                                               + e.getStackTrace(), LEVEL.error);
                            }
                            run.setScheduledStatus(buildStatus);
                            run.setScheduledtime(null);
                     }
                     // Database add run
                     if(executionConsoleService.checkRun(run.getRunid())){
                            if(    executionConsoleService.ModifyRun(run)){
                     DigiLoggerUtils.log(
                                   "Run Details are modified in database:: Run Details :: "
                                                 + run, LEVEL.info);
                     model.addAttribute("successRun", run.getRunName()
                                   + " Modified Successfully ");
                            }else{
                                   DigiLoggerUtils.log(
                                                 "Run Details modification failed in database:: Run Details :: "
                                                               + run, LEVEL.error);
                                   model.addAttribute("successRun" + run.getRunName()
                                                 + " Failed to modify Run");
                            }
                     }else{
                     run = executionConsoleService.addRun(run);
                     DigiLoggerUtils.log(
                                   "Run Details Added to database and redirecting return Run Details :: "
                                                 + run, LEVEL.info);
                     if (run.getRunName() == null)
                            model.addAttribute("successRun" + run.getRunName()
                                          + " Failed to add Run");
                     else
                            model.addAttribute("successRun", run.getRunName()
                                          + " Added Successfully ");
                     }
               }
        } catch (Exception e) {
               e.printStackTrace();
               DigiLoggerUtils.log("Error while creating New Run :: Cafe Seetest"
                            + e.getMessage(), LEVEL.info);
        }

        // Runs Data
        List<Run> runs = executionConsoleService.getRuns((String) session
                     .getAttribute("projectid"), adminService.getTool(toolName)
                     .getToolid());
        List<String> runNames = new ArrayList<String>();
        for (Run run1 : runs) {
               runNames.add(run1.getRunName());

        }
        model.addAttribute("runs", runs);
        model.addAttribute("runNames", runNames);
        model.addAttribute("toolName", toolName);
        return "console/runHome";
}
	@RequestMapping(value = "addNewRunCafePerfecto")
	public String addNewRunCafePerfecto(
			Model model,
			@ModelAttribute("newRun") Run run,
			@RequestParam(value = "zippedBinFolder", required = false) MultipartFile zippedBinFolder,
			@RequestParam(value = "dataSheet", required = false) MultipartFile dataSheet,
			@RequestParam(value = "execution", required = false) MultipartFile execution,
			@RequestParam(value = "masterConfig", required = false) MultipartFile masterConfig,
			@RequestParam("toolName") String toolName, HttpSession session) {
		

		try {
			String projectid = (String) session.getAttribute("projectid");
			session.setAttribute("projectid", projectid);
		String projectName = adminService.getProject(projectid)
				.getProjectName();
		String userName = (String) session.getAttribute("userNameNoSpace");

		DigiLoggerUtils.log(
				"Tool Name  in cafe Perfecto add run first check--->>|"
						+ toolName, LEVEL.info);
		
		File runHome = executionConsoleService.getFinalRunPath(projectName,
				userName, toolName, digitalQAServicesHome,
				run.getRunName());
		FileUtils.uploadMultipartFile(runHome.getAbsolutePath(),
				zippedBinFolder);
		File runHomeConfig = FileUtils.createFolder(
				runHome.getAbsolutePath(), "Configuration");
		FileUtils.uploadMultipartFile(runHomeConfig.getAbsolutePath(),
				masterConfig);
		FileUtils.uploadMultipartFile(runHomeConfig.getAbsolutePath(),
				execution);
		
		String[] data = dataSheet.getOriginalFilename().split("_");
		File runHomeData = FileUtils.createFolder(
				runHome.getAbsolutePath(), "data");
		File runHomeDataClass = FileUtils.createFolder(
				runHomeData.getAbsolutePath(), data[0]);
		// String className = data[0];
		FileUtils.uploadMultipartFile(runHomeDataClass.getAbsolutePath(),
				dataSheet);
		
		FileUtils.copyPasteFolderContents(cafePerfectoDeliveryMaster,
				runHome.getAbsolutePath());
		FileUtils.unzippingFolder(runHome.getAbsolutePath() + "/"
				+ zippedBinFolder.getOriginalFilename(),
				runHome.getAbsolutePath());
		
		boolean result = FileUtils.createNewFile(
				runHome,
				"run.bat",
				"CALL JAVA -cp " + runHome
						+ "\\bin;" + runHome
						+ "\\lib\\* com.capgemini.executor.Executioner");
		DigiLoggerUtils.log(
				"Creating Run Batch file for triggering RunName :  "
						+ run.getRunName() + " :: Output ::" + result,
				LEVEL.info);
		DigiLoggerUtils.log(
				"Output File Data :: :: "
						+ FileUtils.getFileData(new File(runHome
								+ "/output.txt")), LEVEL.info);
		
		// TODO
					// review and change only buildid based on jenkins integration
					run.setBuildid("1");
					run.setToolid(adminService.getTool(toolName).getToolid());
					DigiLoggerUtils.log("Tool ID in Controller -- >"
							+ adminService.getTool(toolName).getToolid(), LEVEL.info);
					run.setRunHome(runHome.getAbsolutePath().replace("\\", "/"));
					run.setUserid((String) session.getAttribute("userid"));
					if (run.getRunName() != null) {
						String configXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><project>   <actions />  <description>Creating Job from DigiAssure :CAFE_SEETEST_FUNCTIONAL TESTING TEST SUITE TRIGGER BASED ON BUILD STATUS :: Run Name ::  "
								+ run.getRunName()
								+ "</description><keepDependencies>false</keepDependencies><properties><com.sonyericsson.jenkins.plugins.bfa.model.ScannerJobProperty plugin=\"build-failure-analyzer@1.13.4\"><doNotScan>false</doNotScan></com.sonyericsson.jenkins.plugins.bfa.model.ScannerJobProperty></properties><scm class=\"hudson.scm.NullSCM\" /><canRoam>true</canRoam><disabled>false</disabled><blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding><blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding><triggers /><concurrentBuild>false</concurrentBuild><builders><hudson.tasks.BatchFile><command>call "
								+ runHome
								+ "\\run.bat"
								+ "</command></hudson.tasks.BatchFile></builders><publishers><hudson.tasks.BuildTrigger><childProjects /><threshold><name>FAILURE</name><ordinal>2</ordinal><color>RED</color><completeBuild>true</completeBuild></threshold></hudson.tasks.BuildTrigger></publishers><buildWrappers/></project>";

						// Save for Later
						if (run.getScheduledStatus().equals("DonotRun")) {
							System.out.println("in do not run");
							// Jenkins Job Schedule
							int statusCode = JenkinsUtilities.newJenkinsJob(jenkinsURL,
									run.getRunName(), configXML);
							DigiLoggerUtils.log(
									"Run is Saved and Jenkuins Job is created -->"
											+ run.getRunName()
											+ " :: Jenkins Jon Status :: "
											+ run.getScheduledStatus() + ": "
											+ statusCode, LEVEL.info);
							run.setScheduledtime(null);
						}
						// Save and Run
						else if (run.getScheduledStatus().equals("RunOnSave")) {
							System.out.println("IN Run on save ");
							int statusCode = JenkinsUtilities.newJenkinsJob(jenkinsURL,
									run.getRunName(), configXML);
							DigiLoggerUtils.log("Creation of Job : " + jenkinsURL
									+ "      Job Output Status Code : " + statusCode,
									LEVEL.info);
							// Build Jenkins Job
							JenkinsUtilities.buildJob(jenkinsURL, run.getRunName());
							DigiLoggerUtils.log(
									"Build Job response : "
											+ JenkinsUtilities.buildJob(jenkinsURL,
													run.getRunName()), LEVEL.info);
							// Get build Status
							run.setScheduledStatus("Pending");
							run.setScheduledtime(null);
						}
						// Scheduled At
						else if (run.getScheduledStatus().equals("ScheduledAt")) {
							System.out.println("IN Scheduled Time "
									+ run.getScheduledtime());
							int statusCode = JenkinsUtilities.newJenkinsJob(jenkinsURL,
									run.getRunName(), configXML);
							DigiLoggerUtils.log("Creation of Job : " + jenkinsURL
									+ "      Job Output Status Code : " + statusCode,
									LEVEL.info);
							// Build Jenkins Job
							HttpResponse res = JenkinsUtilities.buildJob(jenkinsURL,
									run.getRunName(),
									Integer.parseInt(run.getScheduledtime()));
							DigiLoggerUtils
									.log("Triggering build code after job creation : "
											+ res, LEVEL.info);
							// Get build Status
							String buildStatus = null;
							try {
								buildStatus = JenkinsUtilities.latestBuildStatus(
										jenkinsURL, run.getRunName());
								if (buildStatus.contains("<html><head><title>")
										|| buildStatus
												.contains("H1 {font-family:Tahoma,Arial,san")) {
									buildStatus = "Pending";
								}
								if(buildStatus.contains("")||buildStatus.contains(" ")){
									buildStatus="Jenkins Offline";
								}
								DigiLoggerUtils.log(
										"Build Status after triggering Job  "
												+ buildStatus, LEVEL.info);
							} catch (Exception e) {
								System.out.println(buildStatus);
								buildStatus = "Pending";
								DigiLoggerUtils.log(
										"Error while reterving build status "
												+ e.getStackTrace(), LEVEL.error);
							}
							run.setScheduledStatus(buildStatus);
							run.setScheduledtime(null);
						}
						// Database add run
						if(executionConsoleService.checkRun(run.getRunid())){
							if(	executionConsoleService.ModifyRun(run)){
						DigiLoggerUtils.log(
								"Run Details are modified in database:: Run Details :: "
										+ run, LEVEL.info);
						model.addAttribute("successRun", run.getRunName()
								+ " Modified Successfully ");
							}else{
								DigiLoggerUtils.log(
										"Run Details modification failed in database:: Run Details :: "
												+ run, LEVEL.error);
								model.addAttribute("successRun" + run.getRunName()
										+ " Failed to modify Run");
							}
						}else{
						run = executionConsoleService.addRun(run);
						DigiLoggerUtils.log(
								"Run Details Added to database and redirecting return Run Details :: "
										+ run, LEVEL.info);
						if (run.getRunName() == null)
							model.addAttribute("successRun" + run.getRunName()
									+ " Failed to add Run");
						else
							model.addAttribute("successRun", run.getRunName()
									+ " Added Successfully ");
						}
						System.out.println("OUt of runname after db");
						
					}
		
	} catch (Exception e) {
		e.printStackTrace();
		DigiLoggerUtils.log("Error while creating New Run :: Cafe Perfecto"
				+ e.getMessage(), LEVEL.info);
	}

	// Runs Data
	List<Run> runs = executionConsoleService.getRuns((String) session
			.getAttribute("projectid"), adminService.getTool(toolName)
			.getToolid());
	List<String> runNames = new ArrayList<String>();
	for (Run run1 : runs) {
		runNames.add(run1.getRunName());

	}
	model.addAttribute("runs", runs);
	model.addAttribute("runNames", runNames);
	model.addAttribute("toolName", toolName);
	return "console/runHome";
		
	}
	@RequestMapping(value = "addNewRunLayoutTesting")
	public String addNewRun_LayoutTesting(Model model, @ModelAttribute("newRun") Run run,
			@RequestParam(value = "Layout_zippedBinFolder", required = false) MultipartFile Layout_zippedBinFolder,
			@RequestParam(value = "Layout_zippedExcelFolder", required = false) MultipartFile Layout_zippedExcelFolder,
			@RequestParam(value = "Layout_gspecs", required = false) MultipartFile Layout_gspecs,
			@RequestParam("toolName") String toolName, HttpSession session) {
		String projectid = (String) session.getAttribute("projectid");
		session.setAttribute("projectid", projectid);
		try {

			String projectName = adminService.getProject(projectid).getProjectName();
			String userName = (String) session.getAttribute("userNameNoSpace");

			DigiLoggerUtils.log("Tool Name  in Layout Testing add run first check--->>|" + toolName, LEVEL.info);
			File runHome = executionConsoleService.getFinalRunPath(projectName, userName, toolName,
					continonusDeliveryHome, run.getRunName());
			// Copying all Folders from Utility folder
			FileUtils.copyPasteFolderContents(GalenAppiumDeliveryMaster, runHome.getAbsolutePath());
			// Start Appium Server
			//Runtime.getRuntime().exec("cmd /c start " + runHome + "/Start_Appium.bat");
			// Uploading Bin Folder
			FileUtils.uploadMultipartFile(runHome.getAbsolutePath(), Layout_zippedBinFolder);
			FileUtils.unzippingFolder(runHome.getAbsolutePath() + "/" + Layout_zippedBinFolder.getOriginalFilename(),
					runHome.getAbsolutePath());
			// Uploading EXCEL Folder
			FileUtils.uploadMultipartFile(runHome.getAbsolutePath(), Layout_zippedExcelFolder);
			FileUtils.unzippingFolder(runHome.getAbsolutePath() + "/" + Layout_zippedExcelFolder.getOriginalFilename(),
					runHome.getAbsolutePath());
			// Uploading gspecs Folder
			FileUtils.uploadMultipartFile(runHome.getAbsolutePath(), Layout_gspecs);
			FileUtils.unzippingFolder(runHome.getAbsolutePath() + "/" + Layout_gspecs.getOriginalFilename(),
					runHome.getAbsolutePath());

			// Creating testNg.xml file
			try {
				Properties property = new Properties();
				FileInputStream input = new FileInputStream(
						runHome.getAbsolutePath() + "/Excel/configuration.properties");
				property.load(input);
				String ExecutableClass = property.getProperty("ExecutableClass");
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

				// root elements
				Document doc = docBuilder.newDocument();
				Element suite = doc.createElement("suite");
				doc.appendChild(suite);
				// set attribute to suite element
				Attr name = doc.createAttribute("name");
				name.setValue("1");
				suite.setAttributeNode(name);
				Attr verbose = doc.createAttribute("verbose");
				verbose.setValue("1");
				suite.setAttributeNode(verbose);

				// test elements
				Element test = doc.createElement("test");
				suite.appendChild(test);
				// set attribute to test element
				Attr test_name = doc.createAttribute("name");
				test_name.setValue(ExecutableClass + "Test case");
				test.setAttributeNode(test_name);
				// classes elements
				Element classes = doc.createElement("classes");
				test.appendChild(classes);
				// Class element
				Element Class = doc.createElement("class");
				classes.appendChild(Class);
				// set attribute to test element
				Attr Class_name = doc.createAttribute("name");
				Class_name.setValue("com.igate.test." + ExecutableClass);
				Class.setAttributeNode(Class_name);
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(runHome.getAbsolutePath() + "\\testNg.xml"));
				transformer.transform(source, result);
			} catch (Exception e) {
				e.printStackTrace();
			}

			boolean Layout_result = FileUtils.createNewFile(runHome, "run.bat",
					"CALL cd \"" + runHome + "\" && JAVA -cp " + runHome + "\\bin;" + runHome
							+ "\\lib\\httpclient-4.3.jar;" + runHome
							+ "\\lib\\org.apache.httpcomponents.httpcore_4.3.3.v201411290715.jar;" + runHome
							+ "\\lib\\*;" + " org.testng.TestNG " + runHome + "\\testNg.xml");
			DigiLoggerUtils.log("Creating Run Batch file for triggering RunName :  " + run.getRunName()
					+ " :: Output ::" + Layout_result, LEVEL.info);
			DigiLoggerUtils.log("Output File Data :: :: " + FileUtils.getFileData(new File(runHome + "/output.txt")),
					LEVEL.info);

			// TODO
			// review and change only buildid based on jenkins integration
			run.setBuildid("1");
			run.setToolid(adminService.getTool(toolName).getToolid());
			DigiLoggerUtils.log("Tool ID in Controller -- >" + adminService.getTool(toolName).getToolid(), LEVEL.info);
			run.setRunHome(runHome.getAbsolutePath().replace("\\", "/"));
			run.setUserid((String) session.getAttribute("userid"));

			if (run.getRunName() != null) {

				System.out.println("IN run name ");
				String configXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><project>   <actions />  <description>Creating Job from DigiAssure :CAFE_SEETEST_FUNCTIONAL TESTING TEST SUITE TRIGGER BASED ON BUILD STATUS :: Run Name ::  "
						+ run.getRunName()
						+ "</description><keepDependencies>false</keepDependencies><properties><com.sonyericsson.jenkins.plugins.bfa.model.ScannerJobProperty plugin=\"build-failure-analyzer@1.13.4\"><doNotScan>false</doNotScan></com.sonyericsson.jenkins.plugins.bfa.model.ScannerJobProperty></properties><scm class=\"hudson.scm.NullSCM\" /><canRoam>true</canRoam><disabled>false</disabled><blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding><blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding><triggers /><concurrentBuild>false</concurrentBuild><builders><hudson.tasks.BatchFile><command>call "
						+ runHome + "\\run.bat"
						+ "</command></hudson.tasks.BatchFile></builders><publishers><hudson.tasks.BuildTrigger><childProjects /><threshold><name>FAILURE</name><ordinal>2</ordinal><color>RED</color><completeBuild>true</completeBuild></threshold></hudson.tasks.BuildTrigger></publishers><buildWrappers/></project>";

				// Save for Later
				if (run.getScheduledStatus().equals("DonotRun")) {
					System.out.println("in do not run");
					// Jenkins Job Schedule
					int statusCode = JenkinsUtilities.newJenkinsJob(jenkinsURL, run.getRunName(), configXML);
					DigiLoggerUtils.log(
							"Run is Saved and Jenkuins Job is created -->" + run.getRunName()
									+ " :: Jenkins Jon Status :: " + run.getScheduledStatus() + ": " + statusCode,
							LEVEL.info);
					run.setScheduledtime(null);
				}
				// Save and Run
				else if (run.getScheduledStatus().equals("RunOnSave")) {
					System.out.println("IN Run on save ");
					int statusCode = JenkinsUtilities.newJenkinsJob(jenkinsURL, run.getRunName(), configXML);
					DigiLoggerUtils.log(
							"Creation of Job : " + jenkinsURL + "      Job Output Status Code : " + statusCode,
							LEVEL.info);
					// Build Jenkins Job
					JenkinsUtilities.buildJob(jenkinsURL, run.getRunName());
					DigiLoggerUtils.log(
							"Build Job response : " + JenkinsUtilities.buildJob(jenkinsURL, run.getRunName()),
							LEVEL.info);
					// Get build Status
					run.setScheduledStatus("Pending");
					run.setScheduledtime(null);
				}
				// Scheduled At
				else if (run.getScheduledStatus().equals("ScheduledAt")) {
					System.out.println("IN Scheduled Time " + run.getScheduledtime());
					int statusCode = JenkinsUtilities.newJenkinsJob(jenkinsURL, run.getRunName(), configXML);
					DigiLoggerUtils.log(
							"Creation of Job : " + jenkinsURL + "      Job Output Status Code : " + statusCode,
							LEVEL.info);
					// Build Jenkins Job
					HttpResponse res = JenkinsUtilities.buildJob(jenkinsURL, run.getRunName(),
							Integer.parseInt(run.getScheduledtime()));
					DigiLoggerUtils.log("Triggering build code after job creation : " + res, LEVEL.info);
					// Get build Status
					String buildStatus = null;
					try {
						buildStatus = JenkinsUtilities.latestBuildStatus(jenkinsURL, run.getRunName());
						if (buildStatus.contains("<html><head><title>")
								|| buildStatus.contains("H1 {font-family:Tahoma,Arial,san")) {
							buildStatus = "Pending";
						}
						DigiLoggerUtils.log("Build Status after triggering Job  " + buildStatus, LEVEL.info);
					} catch (Exception e) {
						System.out.println(buildStatus);
						buildStatus = "Pending";
						DigiLoggerUtils.log("Error while reterving build status " + e.getStackTrace(), LEVEL.error);
					}
					run.setScheduledStatus(buildStatus);
					run.setScheduledtime(null);

				}
				// Database add run
				if(executionConsoleService.checkRun(run.getRunid())){
					if(	executionConsoleService.ModifyRun(run)){
				DigiLoggerUtils.log(
						"Run Details are modified in database:: Run Details :: "
								+ run, LEVEL.info);
				model.addAttribute("successRun", run.getRunName()
						+ " Modified Successfully ");
					}else{
						DigiLoggerUtils.log(
								"Run Details modification failed in database:: Run Details :: "
										+ run, LEVEL.error);
						model.addAttribute("successRun" + run.getRunName()
								+ " Failed to modify Run");
					}
				}else{
				run = executionConsoleService.addRun(run);
				DigiLoggerUtils.log(
						"Run Details Added to database and redirecting return Run Details :: "
								+ run, LEVEL.info);
				if (run.getRunName() == null)
					model.addAttribute("successRun" + run.getRunName()
							+ " Failed to add Run");
				else
					model.addAttribute("successRun", run.getRunName()
							+ " Added Successfully ");
				}

				System.out.println("OUt of runname after db");

			}
		} catch (Exception e) {
			e.printStackTrace();
			DigiLoggerUtils.log("Error while creating New Run :: Layout Testing" + e.getMessage(), LEVEL.info);
		}

		// Runs Data
		List<Run> runs = executionConsoleService.getRuns((String) session.getAttribute("projectid"),
				adminService.getTool(toolName).getToolid());
		List<String> runNames = new ArrayList<String>();
		for (Run run1 : runs) {
			runNames.add(run1.getRunName());

		}
		model.addAttribute("runs", runs);
		model.addAttribute("runNames", runNames);
		model.addAttribute("toolName", toolName);
		return "console/runHome";
	}
	@RequestMapping(value = "addNewRun")
	public String addNewRun(Model model, @ModelAttribute("newRun") Run run,
			@RequestParam(value = "Layout_zippedBinFolder", required = false) MultipartFile Layout_zippedBinFolder,
			@RequestParam(value = "config_zip", required = false) MultipartFile config_zip,
			@RequestParam(value = "testNGFile", required = false) MultipartFile testNGFile,
			@RequestParam("toolName") String toolName, HttpSession session) {
		String projectid = (String) session.getAttribute("projectid");
		session.setAttribute("projectid", projectid);
		try {

			String projectName = adminService.getProject(projectid).getProjectName();
			String userName = (String) session.getAttribute("userNameNoSpace");

			DigiLoggerUtils.log("Tool Name  in Layout Testing add run first check--->>|" + toolName, LEVEL.info);
			File runHome = executionConsoleService.getFinalRunPath(projectName, userName, toolName,
					continonusDeliveryHome, run.getRunName());
			// Copying all Folders from Utility folder
			FileUtils.copyPasteFolderContents(appiumGridMaster, runHome.getAbsolutePath());
		
			FileUtils.uploadMultipartFile(runHome.getAbsolutePath(), Layout_zippedBinFolder);
			FileUtils.unzippingFolder(runHome.getAbsolutePath() + "/" + Layout_zippedBinFolder.getOriginalFilename(),
					runHome.getAbsolutePath());
			// Uploading EXCEL Folder
			FileUtils.uploadMultipartFile(runHome.getAbsolutePath(), config_zip);
			FileUtils.unzippingFolder(runHome.getAbsolutePath() + "/" + config_zip.getOriginalFilename(),
					runHome.getAbsolutePath());
			// Uploading gspecs Folder
			FileUtils.uploadMultipartFile(runHome.getAbsolutePath(), testNGFile);
			


			boolean Layout_result = FileUtils.createNewFile(runHome, "run.bat",
					"CALL JAVA -cp " + runHome + "\\bin;" + runHome
							+ "\\sprintestlibs\\*;" + runHome
							+ "\\jenkinsLib\\*;" + runHome
							+ "\\libs\\*;" + " org.testng.TestNG " + runHome + "\\"+testNGFile.getOriginalFilename());
			DigiLoggerUtils.log("Creating Run Batch file for triggering RunName :  " + run.getRunName()
					+ " :: Output ::" + Layout_result, LEVEL.info);
			DigiLoggerUtils.log("Output File Data :: :: " + FileUtils.getFileData(new File(runHome + "/output.txt")),
					LEVEL.info);

			// TODO
			// review and change only buildid based on jenkins integration
			run.setBuildid("1");
			run.setToolid(adminService.getTool(toolName).getToolid());
			DigiLoggerUtils.log("Tool ID in Controller -- >" + adminService.getTool(toolName).getToolid(), LEVEL.info);
			run.setRunHome(runHome.getAbsolutePath().replace("\\", "/"));
			run.setUserid((String) session.getAttribute("userid"));

			if (run.getRunName() != null) {

				System.out.println("IN run name ");
				String configXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><project>   <actions />  <description>Creating Job from DigiAssure :CAFE_SEETEST_FUNCTIONAL TESTING TEST SUITE TRIGGER BASED ON BUILD STATUS :: Run Name ::  "
						+ run.getRunName()
						+ "</description><keepDependencies>false</keepDependencies><properties><com.sonyericsson.jenkins.plugins.bfa.model.ScannerJobProperty plugin=\"build-failure-analyzer@1.13.4\"><doNotScan>false</doNotScan></com.sonyericsson.jenkins.plugins.bfa.model.ScannerJobProperty></properties><scm class=\"hudson.scm.NullSCM\" /><canRoam>true</canRoam><disabled>false</disabled><blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding><blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding><triggers /><concurrentBuild>false</concurrentBuild><builders><hudson.tasks.BatchFile><command>call "
						+ runHome + "\\run.bat"
						+ "</command></hudson.tasks.BatchFile></builders><publishers><hudson.tasks.BuildTrigger><childProjects /><threshold><name>FAILURE</name><ordinal>2</ordinal><color>RED</color><completeBuild>true</completeBuild></threshold></hudson.tasks.BuildTrigger></publishers><buildWrappers/></project>";

				// Save for Later
				if (run.getScheduledStatus().equals("DonotRun")) {
					System.out.println("in do not run");
					// Jenkins Job Schedule
					int statusCode = JenkinsUtilities.newJenkinsJob(jenkinsURL, run.getRunName(), configXML);
					DigiLoggerUtils.log(
							"Run is Saved and Jenkuins Job is created -->" + run.getRunName()
									+ " :: Jenkins Jon Status :: " + run.getScheduledStatus() + ": " + statusCode,
							LEVEL.info);
					run.setScheduledtime(null);
				}
				// Save and Run
				else if (run.getScheduledStatus().equals("RunOnSave")) {
					System.out.println("IN Run on save ");
					int statusCode = JenkinsUtilities.newJenkinsJob(jenkinsURL, run.getRunName(), configXML);
					DigiLoggerUtils.log(
							"Creation of Job : " + jenkinsURL + "      Job Output Status Code : " + statusCode,
							LEVEL.info);
					// Build Jenkins Job
					JenkinsUtilities.buildJob(jenkinsURL, run.getRunName());
					DigiLoggerUtils.log(
							"Build Job response : " + JenkinsUtilities.buildJob(jenkinsURL, run.getRunName()),
							LEVEL.info);
					// Get build Status
					run.setScheduledStatus("Pending");
					run.setScheduledtime(null);
				}
				// Scheduled At
				else if (run.getScheduledStatus().equals("ScheduledAt")) {
					System.out.println("IN Scheduled Time " + run.getScheduledtime());
					int statusCode = JenkinsUtilities.newJenkinsJob(jenkinsURL, run.getRunName(), configXML);
					DigiLoggerUtils.log(
							"Creation of Job : " + jenkinsURL + "      Job Output Status Code : " + statusCode,
							LEVEL.info);
					// Build Jenkins Job
					HttpResponse res = JenkinsUtilities.buildJob(jenkinsURL, run.getRunName(),
							Integer.parseInt(run.getScheduledtime()));
					DigiLoggerUtils.log("Triggering build code after job creation : " + res, LEVEL.info);
					// Get build Status
					String buildStatus = null;
					try {
						buildStatus = JenkinsUtilities.latestBuildStatus(jenkinsURL, run.getRunName());
						if (buildStatus.contains("<html><head><title>")
								|| buildStatus.contains("H1 {font-family:Tahoma,Arial,san")) {
							buildStatus = "Pending";
						}
						DigiLoggerUtils.log("Build Status after triggering Job  " + buildStatus, LEVEL.info);
					} catch (Exception e) {
						System.out.println(buildStatus);
						buildStatus = "Pending";
						DigiLoggerUtils.log("Error while reterving build status " + e.getStackTrace(), LEVEL.error);
					}
					run.setScheduledStatus(buildStatus);
					run.setScheduledtime(null);

				}
				// Database add run
				if(executionConsoleService.checkRun(run.getRunid())){
					if(	executionConsoleService.ModifyRun(run)){
				DigiLoggerUtils.log(
						"Run Details are modified in database:: Run Details :: "
								+ run, LEVEL.info);
				model.addAttribute("successRun", run.getRunName()
						+ " Modified Successfully ");
					}else{
						DigiLoggerUtils.log(
								"Run Details modification failed in database:: Run Details :: "
										+ run, LEVEL.error);
						model.addAttribute("successRun" + run.getRunName()
								+ " Failed to modify Run");
					}
				}else{
				run = executionConsoleService.addRun(run);
				DigiLoggerUtils.log(
						"Run Details Added to database and redirecting return Run Details :: "
								+ run, LEVEL.info);
				if (run.getRunName() == null)
					model.addAttribute("successRun" + run.getRunName()
							+ " Failed to add Run");
				else
					model.addAttribute("successRun", run.getRunName()
							+ " Added Successfully ");
				}

				System.out.println("OUt of runname after db");

			}
		} catch (Exception e) {
			e.printStackTrace();
			DigiLoggerUtils.log("Error while creating New Run :: Layout Testing" + e.getMessage(), LEVEL.info);
		}

		// Runs Data
		List<Run> runs = executionConsoleService.getRuns((String) session.getAttribute("projectid"),
				adminService.getTool(toolName).getToolid());
		List<String> runNames = new ArrayList<String>();
		for (Run run1 : runs) {
			runNames.add(run1.getRunName());

		}
		model.addAttribute("runs", runs);
		model.addAttribute("runNames", runNames);
		model.addAttribute("toolName", toolName);
		return "console/runHome";
	}

}
