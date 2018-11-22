/******************************************************************************
Copyright © 2016 Capgemini Group of companies. All rights reserved
(Subject to Limited Distribution and Restricted Disclosure Only.)
THIS SOURCE FILE MAY CONTAIN INFORMATION WHICH IS THE PROPRIETARY
INFORMATION OF Capgemini GROUP OF COMPANIES AND IS INTENDED FOR USE
ONLY BY THE ENTITY WHO IS ENTITLED TO AND MAY CONTAIN
INFORMATION THAT IS PRIVILEGED, CONFIDENTIAL, OR EXEMPT FROM
DISCLOSURE UNDER APPLICABLE LAW.
YOUR ACCESS TO THIS SOURCE FILE IS GOVERNED BY THE TERMS AND
CONDITIONS OF AN AGREEMENT BETWEEN YOU AND Capgemini GROUP OF COMPANIES.
The USE, DISCLOSURE REPRODUCTION OR TRANSFER OF THIS PROGRAM IS
RESTRICTED AS SET FORTH THEREIN.
******************************************************************************/

package com.cg.digi.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cg.digi.logger.DigiLoggerUtils;
import com.cg.digi.logger.DigiLoggerUtils.LEVEL;
import com.cg.digi.model.Project;
import com.cg.digi.model.User;
import com.cg.digi.service.ILoginService;
import com.cg.digi.service.SendMail;
import com.cg.digi.utilities.FileUtils;

/**
 * @author hapemmas
 *
 */
@Scope("session")
@Controller
public class DigiAssure {

	@Value("${hpalm_access}")
	String hpalm_access;

	@Value("${mobileLabUserManual}")
	String mobileLabUserManual;
	
	@Value("${macroFile}")
	String macroFile;

	@Value("${BDDCucumberAppium}")
	String bDDCucumberAppium;
	
	@Value("${OPTIKUserManual}")
	String oPTIKUserManual;
	
	@Value("${FunctionalSeetestManual}")
	String functionalSeetestManual;
	
	

	@Autowired
	ILoginService loginService;

	@RequestMapping(value = "login")
	public String login(Model model, @RequestParam("username") String username,
			@RequestParam("password") String password, HttpSession session) {
		DigiLoggerUtils.log("Form Details :: User Name :: " + username
				+ " :: Password ::" + password + "", LEVEL.info,
				DigiAssure.class);
		String role=null;
		User user = loginService.loginValidation(username, password);

		// Checking user name and password initially before DB validation
		if (username == "" || password == "") {
			model.addAttribute("error", "UserName and Password is Mandatory");
			return "login";
		}

		// Post DB validation; user name and password
		if (user == null) {
			//TexttoSpeech.dospeak("Hello User !!  Invalid  Credentials", "kevin16");
			model.addAttribute("error", "Invalid Login Credentials");
			return "login";
		} else {
			session.setAttribute("userName",
					user.getFirstName() + "  " + user.getLastName());
			session.setAttribute("userNameNoSpace", user.getFirstName() + "_"
					+ user.getLastName());
			session.setAttribute("userid", user.getUserid());
			session.setAttribute("projectid", user.getProjectid());
			session.setAttribute("rolename", user.getRolename());
			System.out.println("Rolename " + user.getRolename());
			 role=user.getRolename();
		}
		model.addAttribute("username", username);
		
		//TexttoSpeech.dospeak("Hello "+user.getFirstName()+"  Welcome to Digi Assure", "kevin16");
		return "landing_new";
		
	}
	
	@RequestMapping(value = "/adminProject")
	public String admin(Model model, HttpSession session) {
		List<Project> Projects = new ArrayList<Project>();
		Projects = loginService.getProjects();
		
		//System.out.println("========="+Projects);
		model.addAttribute("project",Projects);
		
		return "/admin/adminProject";
	}
	

	@RequestMapping(value = "redirect")
	public String redirect(Model model, @RequestParam("username") String username,
			@RequestParam("password") String password, HttpSession session) {
		
		DigiLoggerUtils.log("Form Details :: User Name :: " + username
				+ " :: Password ::" + password + "", LEVEL.info,
				DigiAssure.class);
		
		User user = loginService.loginValidation(username, password);

		

		// Post DB validation; user name and password
		if (user.getUserName()==null || user.getUserid()==null) {
			//TexttoSpeech.dospeak("Hello User !!  Invalid  Credentials", "kevin16");
			model.addAttribute("error", "Invalid Login Credentials");
			return "login";
		} else {
			session.setAttribute("userName",user.getUserName());
			session.setAttribute("userNameNoSpace", user.getFirstName() + "_"
					+ user.getLastName());
			session.setAttribute("userid", user.getUserid());
			session.setAttribute("projectid", user.getProjectid());
			session.setAttribute("rolename", user.getRolename());
			System.out.println("Rolename " + user.getRolename());

		}

		model.addAttribute("username", username);
		//TexttoSpeech.dospeak("Hello "+user.getFirstName()+"  Welcome to Digi Assure", "kevin16");
		return "landing_new";
	}

	
	
	@RequestMapping(value = "integratedQALabs/mobileLab/downloadExcelUtility")
	public String downloadExcelUtility(Model model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response) {
		DigiLoggerUtils.log("HP ALM UTILITY FILE PATH : " + hpalm_access,
				LEVEL.info);
		FileUtils.downloadFile(hpalm_access, session, response, request);
		return "integratedQALabs/mobileLab/mobileLab";
	}

	@RequestMapping(value = "integratedQALabs/mobileLab/downloadManual")
	public String downloadManual(Model model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam("toolName") String toolName) {
		if (toolName.equalsIgnoreCase("mobileLabUserManual")) {
			FileUtils.downloadFile(mobileLabUserManual, session, response,
					request);
		} else if (toolName.equalsIgnoreCase("BDDCucumberAppium")) {
			FileUtils.downloadFile(bDDCucumberAppium, session, response,
					request);
		} else if (toolName.equalsIgnoreCase("OPTIKUserManual")) {
			FileUtils.downloadFile(oPTIKUserManual, session, response,
					request);
		} else if (toolName.equalsIgnoreCase("FunctionalSeetestManual")) {
			FileUtils.downloadFile(functionalSeetestManual, session, response,
					request);
		} 
		return "integratedQALabs/mobileLab/mobileLab";
	}

	
	@RequestMapping(value = "logout")
	public String logout(Model model, HttpSession session) {
		DigiLoggerUtils.log("Session ID IN BEFORE LOGOUT : " + session.getId(),
				LEVEL.info);
		session.invalidate();
		return "login";
	}

	@RequestMapping(value = "views/login/register")
	public String register(Model model,
			@ModelAttribute("User") User userDetails, HttpSession session) {

		if (loginService.checkEmail(userDetails.getEmail())) {
			model.addAttribute("error",
					"Error: User already exists.. Please Login!!!");
		} else {
			String tempPassword = RandomStringUtils.randomAlphanumeric(10);
			userDetails.setUserPassword(tempPassword);
			userDetails.setStatus("N");
			if (loginService.addUser(userDetails)) {
				File file = new File(macroFile);
				if (file.exists()) {
					String userName = userDetails.getEmail();
					file = new File(macroFile + userName);
					file.mkdir();
					if (file.exists()) {
						// File dest = new File(macroFile + userName +
						// "/Macro.vbs");
						if (SendMail
								.sendMail(macroFile, userName, tempPassword)) {
							model.addAttribute("message",
									"User Registered successfully !! ");
							model.addAttribute("message1",
									" Activation mail has been sent to your email Id...");
						} else {
							model.addAttribute("error",
									"Error: Failed to Register ");
							model.addAttribute("error",
									"Error: Please contact admin !!!");
						}
					} else {
						model.addAttribute("error",
								"Error: Failed to Register ");
						model.addAttribute("error",
								"Error: Please contact admin !!!");
					}
				} else {
					model.addAttribute("error", "Error:Failed to Register ");
					model.addAttribute("error",
							"Error:Please contact admin !!!");
				}

			} else {
				model.addAttribute("error", "Error: Failed to Register ");
				model.addAttribute("error", "Error: Please try again later !!");
			}
		}

		return "views/login/register";

	}

	@RequestMapping(value = "activate")
	public String userActivate(Model model, HttpSession session,
			@RequestParam("userName") String userName,
			@RequestParam("userPassword") String userPassword,
			@RequestParam("repeatPassword") String repeatPassword,
			@RequestParam("password") String password) {
		User userDetails = loginService.loginValidation(userName, password);
		if (userDetails == null) {
			model.addAttribute("error",
					"Error: Invalid UserName or Password...");
			return "activation";
		} else {
			loginService.activateUser(userName);
			loginService.updatePassword(userName, userPassword);
			session.setAttribute("userName", userDetails.getFirstName() + "  "
					+ userDetails.getLastName());
			session.setAttribute("userNameNoSpace", userDetails.getFirstName()
					+ "_" + userDetails.getLastName());
			session.setAttribute("userid", userDetails.getUserid());
			session.setAttribute("projectid", userDetails.getProjectid());
			session.setAttribute("roleid", userDetails.getRoleid());
			model.addAttribute("username", userName);
			return "landing_new";
		}

	}

	@RequestMapping(value = "views/login/forgotPassword")
	public String forgotPassword(Model model, HttpSession session,
			@RequestParam(value = "forgotEmailId") String emailId,
			HttpServletRequest request) {
		if (loginService.checkEmail(emailId)) {
			String tempPassword = RandomStringUtils.randomAlphanumeric(10);
			if (loginService.updatePassword(emailId, tempPassword)) {
				File file = new File(macroFile);
				if (file.exists()) {
					file = new File(macroFile + emailId);
					file.mkdir();

					if (file.exists()) {
						if (SendMail.sendMail(macroFile, emailId, tempPassword)) {
							model.addAttribute("message",
									"Success:New password has been sent to your email Id... ");
						} else {
							model.addAttribute("error",
									"Error: Failed!!! Please contact admin!!! ");
						}

					} else {
						model.addAttribute("error",
								"Error: Failed!!! Please contact admin!!! ");
					}

				} else {
					model.addAttribute("error",
							"Error: Failed!!! Please contact admin!!! ");
				}
			} else {
				model.addAttribute("error",
						"Error: Failed!!! Please contact admin!!! ");
			}

		} else {
			model.addAttribute("error", "Error: User doesnot exists!!!");
		}
		return "views/login/forgotPassword";
	}
}
