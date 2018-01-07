package com.e3.orderService;

import com.e3.pojo.OrderInfo;
import com.e3.utils.E3Result;

public interface OrderService {
	E3Result createOrder(OrderInfo orderInfo);
}
