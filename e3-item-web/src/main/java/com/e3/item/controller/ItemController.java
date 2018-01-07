package com.e3.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.e3.item.pojo.Item;
import com.e3.page.ItemService;
import com.e3.pojo.TbItem;
import com.e3.pojo.TbItemDesc;

@Controller
public class ItemController {
	
	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/item/{itemId}")
	public String showItemInfo(@PathVariable Long itemId,Model model){
		TbItem tbItem =itemService.geTbItem(itemId);
		Item item = new Item(tbItem);
		
		TbItemDesc tbItemDesc =itemService.geTbItemDesc(itemId);
		model.addAttribute("item", item);
		model.addAttribute("itemDesc", tbItemDesc);
		
		return "item";
		
		
	}

}
