package com.e3.order.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.e3.car.CartService;
import com.e3.orderService.OrderService;
import com.e3.pojo.OrderInfo;
import com.e3.pojo.TbItem;
import com.e3.pojo.TbUser;
import com.e3.utils.E3Result;

@Controller
public class OrderCartController {
	
	/**
	 * 2.1.功能分析
		1、在购物车页面点击“去结算”按钮，跳转到订单确认页面
		a)必须要求用户登录
		b)使用拦截器实现。
		c)如果用户未登录跳转到登录页面。
		d)如果用户已经登录，放行。展示确认页面。
		e)判断cookie中是否有购物车数据
		f)如果有同步到服务端。
		2、订单确认页面中选择收货地址，选择支付方式，确认商品列表。
		a)根据用户id查询收货地址列表
		b)展示支付方式列表。
		c)从购物车中取商品列表，从服务端取购物车列表。
		3、订单确认页面点击“提交”，生成订单。
		4、展示订单生成完成，或者跳转到支付页面。
	 */
	
	/**
	 * 2.3.展示订单确认页面
		2.3.1.功能分析
		1、根据id查询用户的收货地址列表（使用静态数据）
		2、从购物车中取商品列表，展示到页面。调用购物车服务查询。
	 */
	
	@Autowired
	private CartService cartService;
	@Autowired
	private OrderService orderService;
	
	@RequestMapping("/order/order-cart")
	public String showOrderCart(HttpServletRequest request){
		Object object =request.getAttribute("user");
		if(object!=null){
			//取用户信息
			TbUser user = (TbUser) object;
			//取购物车列表
			List<TbItem> list =cartService.geTbItems(user.getId());
			//将商品列表返回给jsp
			request.setAttribute("cartList", list);
			//返回逻辑视图
			return "order-cart";	
		}
		
		return null;
		
	}
	
//	请求的url：/order/create
//	参数：使用OrderInfo接收
//	返回值：逻辑视图。
//	业务逻辑：
//	1、接收表单提交的数据OrderInfo。
//	2、补全用户信息。
//	3、调用Service创建订单。
//	4、返回逻辑视图展示成功页面
//	a)需要Service返回订单号
//	b)当前日期加三天。
//
//	在拦截器中添加用户处理逻辑：
	
	@RequestMapping(value="/order/create",method=RequestMethod.POST)
	public String createOrder(OrderInfo orderInfo,HttpServletRequest request){
		TbUser user =(TbUser) request.getAttribute("user");
		//把用户信息添加到orderInfo中。
		orderInfo.setUserId(user.getId());
		orderInfo.setBuyerNick(user.getUsername());
		//生成订单
		E3Result result=orderService.createOrder(orderInfo);
		//如果订单生成，删除购物车
		if(result.getStatus()==200){
			//清空购物车
			cartService.clearCartItem(user.getId());
		}
		
		//把订单号传递给页面
		request.setAttribute("orderId", result.getData());
		request.setAttribute("payment", orderInfo.getPayment());
		//返回逻辑视图
		return "success";
		
	}

}
