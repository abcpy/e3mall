package com.e3.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.e3.car.CartService;
import com.e3.pojo.TbItem;
import com.e3.pojo.TbUser;
import com.e3.sso.userService.UserService;
import com.e3.utils.CookieUtils;
import com.e3.utils.E3Result;
import com.e3.utils.JsonUtils;

public class LoginInterceptor implements HandlerInterceptor{

	/**
	 * 1、从cookie中取token
		2、如果没有取到，没有登录，跳转到sso系统的登录页面。拦截
		3、如果取到token。判断登录是否过期，需要调用sso系统的服务，根据token取用户信息
		4、如果没有取到用户信息，登录已经过期，重新登录。跳转到登录页面。拦截
		5、如果取到用户信息，用户已经是登录状态，把用户信息保存到request中。放行
		6、判断cookie中是否有购物车信息，如果有合并购物车
	 */
	
	private String SSO_URL="http://localhost:8094";
	private String CART="CART";

	@Autowired
	private UserService userService;
	
	@Autowired
	private CartService cartService;
	
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		//1. 从cookie中取token
		String token =CookieUtils.getCookieValue(request, "COOKIE_TOKEN_KEY");
		//2、如果没有取到，没有登录，跳转到sso系统的登录页面。拦截
		if(StringUtils.isBlank(token)){
			//跳转登录页面
			response.sendRedirect(SSO_URL + "/page/login?redirect=" + request.getRequestURL());
			return false;
			
		}
		
		//3、如果取到token。判断登录是否过期，需要调用sso系统的服务，根据token取用户信息
		E3Result result =userService.getUserByToken(token);
		//4、如果没有取到用户信息，登录已经过期，重新登录。跳转到登录页面。拦截
		if(result.getStatus()!=200){
			//跳转登录页面
			response.sendRedirect(SSO_URL + "/page/login?redirect=" + request.getRequestURL());
			return false;
		}
		//5、如果取到用户信息，用户已经是登录状态，把用户信息保存到request中。放行
		TbUser user =(TbUser) result.getData();
		request.setAttribute("user", user);
		//6、判断cookie中是否有购物车信息，如果有合并购物车
		String json =CookieUtils.getCookieValue(request, CART, true);
		if(StringUtils.isNotBlank(json)){
			cartService.mergeCart(user.getId(), JsonUtils.jsonToList(json, TbItem.class));
			//删除cookie购物车
			CookieUtils.setCookie(request, response, CART, "");
			
		}

		
		
		return true;
	}

}
