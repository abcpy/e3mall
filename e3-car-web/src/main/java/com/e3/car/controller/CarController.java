package com.e3.car.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.e3.car.CartService;
import com.e3.page.ItemService;
import com.e3.pojo.TbItem;
import com.e3.pojo.TbUser;
import com.e3.utils.CookieUtils;
import com.e3.utils.E3Result;
import com.e3.utils.JsonUtils;

@Controller
public class CarController {
	private String CART="CART";
	
	private Integer COOKIE_CART_EXPIRE=432000;
	
	@Autowired
	private ItemService itemService;
	
	@Autowired
	private CartService cartService;

	/**
	 * 未登录状态下使用购物车
	 * 添加购物车
	 * 
	 * 在不登陆的情况下也可以添加购物车。把购物车信息写入cookie。
		优点：
		1、不占用服务端存储空间
		2、用户体验好。
		3、代码实现简单。
		缺点：
		1、cookie中保存的容量有限。最大4k
		把购物车信息保存在cookie中，更换设备购物车信息不能同步。
		
		请求的url：/cart/add/{itemId}
		参数：
		1）商品id： Long itemId
		2）商品数量： int num
		业务逻辑：
		1、从cookie中查询商品列表。
		2、判断商品在商品列表中是否存在。
		3、如果存在，商品数量相加。
		4、不存在，根据商品id查询商品信息。
		5、把商品添加到购车列表。
		6、把购车商品列表写入cookie。

		返回值：逻辑视图
		
		Cookie保存购物车
		1）key：TT_CART
		2）Value：购物车列表转换成json数据。需要对数据进行编码。
		3）Cookie的有效期：保存7天。

		商品列表：
		List<TbItem>，每个商品数据使用TbItem保存。当根据商品id查询商品信息后，取第一张图片保存到image属性中即可。

		读写cookie可以使用CookieUtils工具类实现。
	 */
	
	@RequestMapping("/cart/add/{itemId}")
	public String addCartItem(@PathVariable Long itemId,Integer num,
			HttpServletRequest request,HttpServletResponse response){
		
		//判断用户是否为登陆状态
		Object object =request.getAttribute("user");
		if(object!=null){
			TbUser user =(TbUser) object;
			Long userId=user.getId();
			//将商品添加到服务端
			E3Result result =cartService.addCart(userId, itemId, num);
			return "cartSuccess";
		}
//		1、从cookie中查询商品列表。
		List<TbItem> carList =getCartList(request);
//		2、判断商品在商品列表中是否存在。
		boolean hasItem=false;
		for (TbItem tbItem : carList) {
			//对象比较的是地址，应该是值的比较
			if(tbItem.getId()==itemId.longValue()){
				//3、如果存在，商品数量相加。
				tbItem.setNum(tbItem.getNum()+num);
				hasItem=true;
				break;
			}
		}
//		4、不存在，根据商品id查询商品信息。
		if(!hasItem){
			TbItem tbItem =itemService.geTbItem(itemId);
			//取一张图片
			String image=tbItem.getImage();
			if(StringUtils.isNotBlank(image)){
				String[] strings =image.split(",");
				tbItem.setImage(strings[0]);
			}
			
			tbItem.setNum(num);
//			5、把商品添加到购车列表。
			carList.add(tbItem);
		}
//		6、把购车商品列表写入cookie。
		CookieUtils.setCookie(request, response, CART, JsonUtils.objectToJson(carList), COOKIE_CART_EXPIRE, true);
		return "cartSuccess";
		
	}
	
	/**
	 * 显示购物车列表
	 * @param request
	 * @param model
	 * @return
	 * 
	 * 1、判断用户是否登录。
		2、如果已经登录，判断cookie中是否有购物车信息
		3、如果有合并购物车，并删除cookie中的购物车。
		4、如果是登录状态，应从服务端取购物车列表。
		5、如果是未登录状态，从cookie中取购物车列表
	 */
	@RequestMapping("/cart/cart")
	private String showCartList(HttpServletRequest request,HttpServletResponse response,Model model){
		
		//取购物车商品列表
		List<TbItem> list =getCartList(request);
		
		//判断用户是否登录。
		Object object =request.getAttribute("user");
		if(object!=null){
			//用户已经登录
			TbUser user =(TbUser) object;
			System.out.println("用户已经登录，用户名为：" + user.getUsername());
			//判断cookie中是否有购物车信息
			if(!list.isEmpty()){
				//如果有合并购物车，
				cartService.mergeCart(user.getId(), list);
				//并删除cookie中的购物车。
				CookieUtils.setCookie(request, response, CART, "");	
			}
			//如果是登录状态，应从服务端取购物车列表。
			List<TbItem> cartList = cartService.geTbItems(user.getId());
			//传递给商品页面
			model.addAttribute("cartList", cartList);
			return "cart";
			
			
		}else {
			System.out.println("用户未登录");

		}
		
		//传递给商品页面
		model.addAttribute("cartList", list);
		return "cart";
		
	}
	
	
	/**
	 * 请求的url：/cart/update/num/{itemId}/{num}
		参数：long itemId、int num
		业务逻辑：
		1、接收两个参数
		2、从cookie中取商品列表
		3、遍历商品列表找到对应商品
		4、更新商品数量
		5、把商品列表写入cookie。
		6、响应e3Result。Json数据。
			 * @param request
			 * @return
	 */
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	@ResponseBody
	public E3Result updateNum(@PathVariable Long itemId,
			@PathVariable Integer num,HttpServletRequest request,
			HttpServletResponse response){
		//判断是否为登录状态
		Object object =request.getAttribute("user");
		if(object!=null){
			TbUser user=(TbUser) object;
			//更新服务端的购物车
			cartService.updateCatnum(user.getId(), itemId, num);
			return E3Result.ok();
		}
		List<TbItem> list =getCartList(request);
		for (TbItem tbItem : list) {
			if(tbItem.getId()==itemId.longValue()){
				//4、更新商品数量
				tbItem.setNum(num);
			}
		}
		
		//5、把商品列表写入cookie。
		CookieUtils.setCookie(request, response, CART, JsonUtils.objectToJson(list), COOKIE_CART_EXPIRE, true);
		//6、响应e3Result。Json数据。
		return E3Result.ok();
		
		
	}
	
	
	/**
	 * 删除购物车商品
	 * 请求的url：/cart/delete/{itemId}
	参数：商品id
	返回值：展示购物车列表页面。Url需要做redirect跳转。
	业务逻辑：
	1、从url中取商品id
	2、从cookie中取购物车商品列表
	3、遍历列表找到对应的商品
	4、删除商品。
	5、把商品列表写入cookie。
	返回逻辑视图：在逻辑视图中做redirect跳转。
	 * @param request
	 * @return
	 */
	
	@RequestMapping("/cart/delete/{itemId}")
	public String deleteCar(@PathVariable Long itemId,HttpServletRequest request,HttpServletResponse response){
		Object object =request.getAttribute("user");
		if(object!=null){
			TbUser tbUser = (TbUser) object;
			//删除服务端的购物车商品
			cartService.delCat(tbUser.getId(),itemId);
			return "redirect:/cart/cart.html";

		}
		//1. 从cookie中取购物车商品列表
		List<TbItem> list =getCartList(request);
		//3、遍历列表找到对应的商品
		for (TbItem tbItem : list) {
			if(tbItem.getId()==itemId.longValue()){
				//删除商品
				list.remove(tbItem);
				break;
			}
		}
		
		CookieUtils.setCookie(request, response, CART, JsonUtils.objectToJson(list), COOKIE_CART_EXPIRE, true);
		// 6、返回逻辑视图：在逻辑视图中做redirect跳转。
		return "redirect:/cart/cart.html";

	}
	
	
	//1、从cookie中查询商品列表。
	private List<TbItem> getCartList(HttpServletRequest request){
		//从Cookie中取购物车列表
		String json =CookieUtils.getCookieValue(request, CART, true);
		//判断json是否为null
		if(StringUtils.isNotBlank(json)){
			List<TbItem> list =JsonUtils.jsonToList(json, TbItem.class);
			return list;
		}
		
		return new ArrayList<>();
		
	}
	
	
	
	
}
