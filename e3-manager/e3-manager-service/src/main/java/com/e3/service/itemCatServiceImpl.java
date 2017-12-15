package com.e3.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import com.e3.mapper.TbItemCatMapper;
import com.e3.page.ItemCatService;
import com.e3.page.pojo.EasyUITreeNode;
import com.e3.pojo.TbItemCat;
import com.e3.pojo.TbItemCatExample;
import com.e3.pojo.TbItemCatExample.Criteria;

@Service
public class itemCatServiceImpl implements ItemCatService{
	@Autowired
	private TbItemCatMapper tbItemCatMapper;
	
	@Override
	public List<EasyUITreeNode> geTreeNodes(long parentId) {
		//根据parentID查询节点列表
		TbItemCatExample example = new TbItemCatExample();
		//设置查询条件
		Criteria caCriteria = example.createCriteria();
		caCriteria.andParentIdEqualTo(parentId);
		List<TbItemCat> list =tbItemCatMapper.selectByExample(example);
		//转换成EasyUITreeNode列表
		List<EasyUITreeNode> resultList = new ArrayList<>();
		for (TbItemCat tbItemCat : list) {
			EasyUITreeNode easyUITreeNode = new EasyUITreeNode();
			easyUITreeNode.setId(tbItemCat.getId());
			easyUITreeNode.setText(tbItemCat.getName());
			easyUITreeNode.setState(tbItemCat.getIsParent()?"closed":"open");
			resultList.add(easyUITreeNode);
		}
		
		return resultList;
	}

}
