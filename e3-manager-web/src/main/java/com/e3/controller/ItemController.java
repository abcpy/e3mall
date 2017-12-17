package com.e3.controller;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.e3.page.ItemService;
import com.e3.page.pojo.EasyUIDataGridResult;
import com.e3.pojo.TbItem;
import com.e3.utils.E3Result;

@Controller
public class ItemController {
	
	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/item/list")
	@ResponseBody
	private EasyUIDataGridResult getItemList(Integer page,Integer rows){
		EasyUIDataGridResult result =itemService.getItemList(page, rows);
		return result;
	}
	
	//商品添加
	@RequestMapping(value="/item/save",method=RequestMethod.POST)
	@ResponseBody
	private E3Result addItem(TbItem tbItem,String desc){
		E3Result result =itemService.addGoods(tbItem, desc);
		return result;
	}
		
		

	

}
