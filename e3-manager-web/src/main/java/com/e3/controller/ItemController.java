package com.e3.controller;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.e3.content.service.contentService;
import com.e3.page.ItemService;
import com.e3.page.pojo.EasyUIDataGridResult;
import com.e3.page.pojo.EasyUITreeNode;
import com.e3.pojo.TbContent;
import com.e3.pojo.TbItem;
import com.e3.utils.E3Result;

@Controller
public class ItemController {
	
	@Autowired
	private ItemService itemService;
	@Autowired
	private contentService service;
	
	@RequestMapping("/item/list")
	@ResponseBody
	public EasyUIDataGridResult getItemList(Integer page,Integer rows){
		EasyUIDataGridResult result =itemService.getItemList(page, rows);
		return result;
	}
	
	//商品添加
	@RequestMapping(value="/item/save",method=RequestMethod.POST)
	@ResponseBody
	public E3Result addItem(TbItem tbItem,String desc){
		E3Result result =itemService.addGoods(tbItem, desc);
		return result;
	}
	
	//内容管理页面展示
	@RequestMapping("/content/category/list")
	@ResponseBody
	public List<EasyUITreeNode> getContentList(@RequestParam(value="id",defaultValue="0")long parentId){
		List<EasyUITreeNode> list =itemService.getContentCatagoryList(parentId);
		return list;
	}
	
	//添加节点
	@RequestMapping("/content/category/create")
	@ResponseBody
	public E3Result addContentCategory(long parentId,String name){
		E3Result result=itemService.addContentCategory(parentId, name);
		return result;
	}
	
	
	
	//添加内容
			@RequestMapping("/content/save")
			@ResponseBody
			public E3Result addItemContent(TbContent content){
				E3Result result =service.addContent(content);
				return result;
			}
	
	@RequestMapping("/content/query/list")
	@ResponseBody
	public EasyUIDataGridResult getContentList(Integer page,Integer rows){
		EasyUIDataGridResult result =itemService.getItemContentList(page, rows);
		return result;
	}
	
		
		

	

}
