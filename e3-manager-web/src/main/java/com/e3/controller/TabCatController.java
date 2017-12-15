package com.e3.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.e3.page.ItemCatService;
import com.e3.page.pojo.EasyUITreeNode;

@Controller
public class TabCatController {
	
	@Autowired
	private ItemCatService itemCatService;
	
	@RequestMapping("/item/cat/list")
	@ResponseBody
	public List<EasyUITreeNode> getItemCatList(@RequestParam(value="id",defaultValue="0")long parendId){
		List<EasyUITreeNode> list = itemCatService.geTreeNodes(parendId);
		return list;
		
	}
}
