package com.e3.pojo;

import java.io.Serializable;
import java.util.List;

//可以扩展TbOrder，在子类中添加两个属性一个是商品明细列表，一个是配送信息。
//把pojo放到e3-order-interface工程中。
public class OrderInfo extends TbOrder implements Serializable{
	private List<TbOrderItem> orderItems;
	private TbOrderShipping OrderShipping;
	public List<TbOrderItem> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<TbOrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	public TbOrderShipping getOrderShipping() {
		return OrderShipping;
	}
	public void setOrderShipping(TbOrderShipping orderShipping) {
		OrderShipping = orderShipping;
	}
	
	
	
}
