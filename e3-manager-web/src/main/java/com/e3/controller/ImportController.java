package com.e3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.e3.searchItem.ImportItem;
import com.e3.utils.E3Result;

@Controller
public class ImportController {
	@Autowired
	private ImportItem item;
	
	@RequestMapping("/index/item/import")
	@ResponseBody
	public E3Result ImportItemIndex(){
		E3Result result=item.getSearchItem();
		return result;
	}
}
