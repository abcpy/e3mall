package com.e3.page;

import java.util.List;

import com.e3.page.pojo.EasyUIDataGridResult;
import com.e3.page.pojo.EasyUITreeNode;
import com.e3.pojo.TbItem;
import com.e3.utils.E3Result;

public interface ItemService {
	EasyUIDataGridResult getItemList(int page,int rows);
	E3Result addGoods(TbItem tbItem,String desc);
}
