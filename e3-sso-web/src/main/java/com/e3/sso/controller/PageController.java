package com.e3.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
  
	@RequestMapping("page/register")
	public String showReigster(){
		return "register";
	}
	
	@RequestMapping("page/login")
	public String showLogin(String redirctUrl,Model model){
		model.addAttribute("redirct", redirctUrl);
		return "login";
	}
}
