package com.e3.search.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.e3.search.pojo.SearchResult;
import com.e3.searchItem.ImportItem;

@Controller
public class SearchController {
	@Autowired
	private ImportItem item;
	
	@RequestMapping("/search")
	public String search(String keyword,@RequestParam(defaultValue="1") Integer page,Model model) throws Exception{
		
		keyword=new String(keyword.getBytes("iso-8859-1"), "utf-8");
		SearchResult result=item.search(keyword, page, 10);
		//把结果传递给jsp页面
				model.addAttribute("query", keyword);
				model.addAttribute("totalPages", result.getTotalPages());
				model.addAttribute("recourdCount", result.getRecourdCount());
				model.addAttribute("page", page);
				model.addAttribute("itemList", result.getList());
//				int a =10/0;
		return "search";
		
	}
}
