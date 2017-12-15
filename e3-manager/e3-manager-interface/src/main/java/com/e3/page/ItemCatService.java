package com.e3.page;

import java.util.List;

import com.e3.page.pojo.EasyUITreeNode;

public interface ItemCatService {
	List<EasyUITreeNode> geTreeNodes(long parentId);
}
