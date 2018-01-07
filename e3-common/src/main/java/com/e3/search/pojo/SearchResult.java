package com.e3.search.pojo;

import java.io.Serializable;
import java.util.List;

public class SearchResult implements Serializable{
	//1.商品列表
	private List<SearchItem> list;
	//2. 总页数
	private int totalPages;
	//3. 总记录数
	private int recourdCount;
	public List<SearchItem> getList() {
		return list;
	}
	public void setList(List<SearchItem> list) {
		this.list = list;
	}
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public int getRecourdCount() {
		return recourdCount;
	}
	public void setRecourdCount(int recourdCount) {
		this.recourdCount = recourdCount;
	}
	
	
	
	
}
