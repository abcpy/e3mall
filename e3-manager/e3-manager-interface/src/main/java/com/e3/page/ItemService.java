package com.e3.page;

import com.e3.page.pojo.EasyUIDataGridResult;

public interface ItemService {
	EasyUIDataGridResult getItemList(int page,int rows);
}
