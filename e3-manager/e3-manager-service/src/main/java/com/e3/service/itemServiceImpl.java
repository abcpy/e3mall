package com.e3.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e3.mapper.TbItemMapper;
import com.e3.page.ItemService;
import com.e3.page.pojo.EasyUIDataGridResult;
import com.e3.pojo.TbItem;
import com.e3.pojo.TbItemExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class itemServiceImpl implements ItemService{

	@Autowired
	private TbItemMapper itemmapper;

	@Override
	public EasyUIDataGridResult getItemList(int page, int rows) {
		//设置分页
		PageHelper.startPage(page, rows);
		//设置查询
		TbItemExample tbItemExample = new TbItemExample();
		List<TbItem> list = itemmapper.selectByExample(tbItemExample);
		//区分页信息
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setTotal(pageInfo.getTotal());
		result.setRows(list);
		return result;
	}
	
	

}
