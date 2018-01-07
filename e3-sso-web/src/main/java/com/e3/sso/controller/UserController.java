package com.e3.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.e3.pojo.TbUser;
import com.e3.sso.userService.UserService;
import com.e3.utils.CookieUtils;
import com.e3.utils.E3Result;

/**
 * 请求的url：/user/check/{param}/{type}
        参数：从url中取参数1、String param（要校验的数据）2、Integer type（校验的数据类型）
        响应的数据：json数据。e3Result，封装的数据校验的结果true：成功false：失败。
 * @author liujian
 *
 */
@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/user/check/{param}/{type}")
	@ResponseBody
	public E3Result checkData(@PathVariable String param,@PathVariable Integer type){
		E3Result result =userService.checkData(param, type);
		return result;
	}
	
	
	
	//注册用户
	/**
	 * Controller：
		请求的url：/user/register
		参数：表单的数据：username、password、phone、email
		返回值：json数据。e3Result
		接收参数：使用TbUser对象接收。
		请求的方法：post
	 */
	
	
	@RequestMapping(value="/user/register",method=RequestMethod.POST)
	@ResponseBody
	public E3Result register(TbUser user){
		E3Result result =userService.createUser(user);
		return result;
	}
	
	//登录用户
	/**
	 * 请求的url：/user/login
		请求的方法：POST
		参数：username、password，表单提交的数据。可以使用方法的形参接收。
		HttpServletRequest、HttpServletResponse
		返回值：json数据，使用e3Result包含一个token。
		业务逻辑：
		1、接收两个参数。
		2、调用Service进行登录。
		3、从返回结果中取token，写入cookie。Cookie要跨域。
		Cookie二级域名跨域需要设置:
		1）setDomain，设置一级域名：
		.itcatst.cn
		.e3.com
		.e3.com.cn
		2）setPath。设置为“/”
			 * @param username
			 * @param password
			 * @return
			 */
	
	@RequestMapping(value="/user/login", method=RequestMethod.POST)
	@ResponseBody
	public E3Result login(HttpServletRequest request,HttpServletResponse response,String username,String password){
		//参数：username、password，表单提交的数据。可以使用方法的形参接收。
		E3Result result =userService.login(username, password);
		//从返回结果中取token，写入cookie。Cookie要跨域。
		//判断登录成功
		if(result.getStatus()==200){
			String token=result.getData().toString();
			//如果登陆成功需要把token写入cookie
			CookieUtils.setCookie(request, response, "COOKIE_TOKEN_KEY", token);
		}
		
		//返回值：json数据，使用e3Result包含一个token。
		return result;
		
		
		
	}
	
}
