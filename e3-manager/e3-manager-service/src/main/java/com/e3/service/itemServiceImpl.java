package com.e3.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e3.mapper.TbItemDescMapper;
import com.e3.mapper.TbItemMapper;
import com.e3.page.ItemService;
import com.e3.page.pojo.EasyUIDataGridResult;
import com.e3.pojo.TbItem;
import com.e3.pojo.TbItemDesc;
import com.e3.pojo.TbItemExample;
import com.e3.utils.E3Result;
import com.e3.utils.IDUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class itemServiceImpl implements ItemService{

	@Autowired
	private TbItemMapper itemmapper;
	@Autowired
	private TbItemDescMapper descmapper;

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

	//添加商品
	@Override
	public E3Result addGoods(TbItem tbItem, String desc) {
		// private Long id;
		//1、生成商品id
		long id =IDUtils.genItemId();
		tbItem.setId(id);
		//2.补全TbItem对象的属性
		//'商品状态，1-正常，2-下架，3-删除',
		tbItem.setStatus((byte)1);
		tbItem.setCreated(new Date());
		tbItem.setUpdated(new Date());
		//3. 向商品表插入数据
		itemmapper.insert(tbItem);
		
		//4.创建一个TbItemDesc对象
		TbItemDesc tbItemDesc = new TbItemDesc();
		//5、补全TbItemDesc的属性
		tbItemDesc.setItemId(id);
		tbItemDesc.setItemDesc(desc);
		tbItemDesc.setCreated(new Date());
		tbItemDesc.setUpdated(new Date());
		// 6、向商品描述表插入数据
		descmapper.insert(tbItemDesc);
		//7、E3Result.ok()
		return E3Result.ok();
		
		
		







		

	  
	}
	
	

}
