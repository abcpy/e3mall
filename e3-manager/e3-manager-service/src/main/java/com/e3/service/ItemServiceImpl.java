package com.e3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e3.mapper.TbItemMapper;
import com.e3.pojo.TbItem;
import com.e3.test.ItemService;

@Service
public class ItemServiceImpl implements ItemService{

	@Autowired
	private TbItemMapper itemmapper;
	
	@Override
	public TbItem getItemById(long id) {
		TbItem item=itemmapper.selectByPrimaryKey(id);
		return item;
	}

}
