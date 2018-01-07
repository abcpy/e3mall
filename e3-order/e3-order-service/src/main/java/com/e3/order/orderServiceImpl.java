package com.e3.order;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.e3.mapper.TbOrderItemMapper;
import com.e3.mapper.TbOrderMapper;
import com.e3.mapper.TbOrderShippingMapper;
import com.e3.orderService.OrderService;
import com.e3.pojo.OrderInfo;
import com.e3.pojo.TbItem;
import com.e3.pojo.TbOrderItem;
import com.e3.pojo.TbOrderShipping;
import com.e3.redisUtils.JedisClient;
import com.e3.utils.E3Result;
import com.mysql.fabric.xmlrpc.base.Data;

@Service
public class orderServiceImpl implements OrderService{
	@Value("${ORDER_ID_GEN_KEY}")
	private String ORDER_ID_GEN_KEY;
	@Value("${ORDER_ID_START}")
	private String ORDER_ID_START;
	@Value("${ORDER_DETAIL_ID_GEN_KEY}")
	private String ORDER_DETAIL_ID_GEN_KEY;
	
	@Autowired
	private JedisClient jedisClient;
	@Autowired
	private TbOrderMapper tbOrderMapper;
	@Autowired
	private TbOrderItemMapper tbOrderItemMapper;
	@Autowired
	private TbOrderShippingMapper tborderShipper;
	@Override
	public E3Result createOrder(OrderInfo orderInfo) {
		//接收表单数据
		//生成订单id
		if(!jedisClient.exists(ORDER_ID_GEN_KEY)){
			//设置初始值
			jedisClient.set(ORDER_ID_GEN_KEY, ORDER_ID_START);
		}
		
		String orderId=jedisClient.incr(ORDER_DETAIL_ID_GEN_KEY).toString();
		//补全orderId
		orderInfo.setOrderId(orderId);
		//1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
		orderInfo.setStatus(1);
		Date data = new Date();
		orderInfo.setUpdateTime(data);
		orderInfo.setCreateTime(data);
		//插入订单表
		tbOrderMapper.insert(orderInfo);
		
		//向订单明细表中插入数据
		List<TbOrderItem> tbOrderItems =orderInfo.getOrderItems();
		for (TbOrderItem tbOrderItem : tbOrderItems) {
			String odId=jedisClient.incr(ORDER_DETAIL_ID_GEN_KEY).toString();
			//补全属性
			tbOrderItem.setId(odId);
			tbOrderItem.setOrderId(orderId);
			//向明细表中插入数据
			tbOrderItemMapper.insert(tbOrderItem);
			
		}
		
		//向订单物流表中插入数据
		TbOrderShipping tbOrderShipping =orderInfo.getOrderShipping();
		tbOrderShipping.setOrderId(orderId);
		tbOrderShipping.setCreated(data);
		tbOrderShipping.setUpdated(data);
		tborderShipper.insert(tbOrderShipping);
		
		//返回数据
		return E3Result.ok(orderId);
		
		
		

		
	}

}
