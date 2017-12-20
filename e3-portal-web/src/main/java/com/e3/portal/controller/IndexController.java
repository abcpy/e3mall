package com.e3.portal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.e3.content.service.contentService;
import com.e3.pojo.TbContent;
import com.e3.utils.E3Result;

/**
 * 返回值Sting逻辑视图
 * @author liujian
 *
 */
@Controller
public class IndexController {
	@Autowired
	private contentService service;
	
	
	
	@RequestMapping("/index")
	public String showIndex(Model model){
		List<TbContent> list =service.getContent(89);
		model.addAttribute("ad1List", list);
		return "index";
	}
}
