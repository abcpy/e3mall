package com.e3.page;

import java.util.List;

import com.e3.page.pojo.EasyUIDataGridResult;
import com.e3.page.pojo.EasyUITreeNode;

public interface ItemService {
	EasyUIDataGridResult getItemList(int page,int rows);
}
