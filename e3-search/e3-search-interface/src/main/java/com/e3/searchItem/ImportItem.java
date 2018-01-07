package com.e3.searchItem;

import com.e3.search.pojo.SearchResult;
import com.e3.utils.E3Result;

public interface ImportItem {
	public E3Result getSearchItem();
	SearchResult search(String keyWord,int page,int rows) throws Exception;
	
}
