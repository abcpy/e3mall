package com.e3.page.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 响应的json数据格式EasyUIResult
 * @author liujian
 *
 */
public class EasyUIDataGridResult implements Serializable{
	private Long total;
	private List  rows;
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	public List getRows() {
		return rows;
	}
	public void setRows(List rows) {
		this.rows = rows;
	}
	
	
	
	
	
	
}
