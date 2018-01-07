package com.e3.car.inerceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.e3.pojo.TbUser;
import com.e3.sso.userService.UserService;
import com.e3.utils.CookieUtils;
import com.e3.utils.E3Result;

/**
 * 判断用户是否登录的拦截器
 * @author liujian
 *
 */
public class LoginInterceptor implements HandlerInterceptor{
	
	@Autowired
	private UserService userService;
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse arg1, Object arg2) throws Exception {
		// 执行handler方法之前执行此方法
		// 1、实现一个HandlerInterceptor接口。
		// 2、在执行handler方法之前做业务处理
		// 3、从cookie中取token。使用CookieUtils工具类实现。
			String token=CookieUtils.getCookieValue(request, "COOKIE_TOKEN_KEY");
		// 4、没有取到token，用户未登录。放行
		if(StringUtils.isBlank(token)){
			return true;
		}
		// 5、取到token，调用sso系统的服务，根据token查询用户信息。
		E3Result result =userService.getUserByToken(token);
		// 6、没有返回用户信息。登录已经过期，未登录，放行。
		if(result.getStatus()!=200){
			return true;
		}
		// 7、返回用户信息。用户是登录状态。可以把用户对象保存到request中，在Controller中可以通过判断request中是否包含用户对象，确定是否为登录状态。
		TbUser tbUser =(TbUser)result.getData();
		request.setAttribute("user", tbUser);
		//返回true放行
		//返回false拦截
		
		return true;
	}

}
