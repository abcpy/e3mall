package com.e3.page;

import java.util.List;

import com.e3.page.pojo.EasyUIDataGridResult;
import com.e3.page.pojo.EasyUITreeNode;
import com.e3.pojo.TbContent;
import com.e3.pojo.TbItem;
import com.e3.pojo.TbItemDesc;
import com.e3.utils.E3Result;

public interface ItemService {
	EasyUIDataGridResult getItemList(int page,int rows);
	E3Result addGoods(TbItem tbItem,String desc);
	List<EasyUITreeNode> getContentCatagoryList(long parentId);
	E3Result addContentCategory(long parentId,String name);
	E3Result addContent(TbContent tbContent);
	EasyUIDataGridResult getItemContentList(int page, int rows);
	TbItem geTbItem(long itemId);
	TbItemDesc geTbItemDesc(long itemId);
	
}
