package com.e3.sso.user;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.e3.mapper.TbUserMapper;
import com.e3.pojo.TbUser;
import com.e3.pojo.TbUserExample;
import com.e3.pojo.TbUserExample.Criteria;
import com.e3.redisUtils.JedisClient;
import com.e3.sso.userService.UserService;
import com.e3.utils.E3Result;
import com.e3.utils.JsonUtils;

/**
 * 1、要校验的数据：String param
   2、数据类型：int type（1、2、3分别代表username、phone、email）
 * @author liujian
 *
 */
@Service
public class userServiceImpl implements UserService{
	@Autowired
	private TbUserMapper userMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("$USER_INFO")
	private String USER_INFO;
	private int SESSION_EXPIRE=1800;
	
	@Override
	public E3Result checkData(String param, int type) {
		
		//1. 从tbUser表中查询数据
		TbUserExample tbUserExample = new TbUserExample();
		Criteria criteria =tbUserExample.createCriteria();
		//2. 查询条件根据参数动态生成
		//1、2、3分别代表username、phone、email
		if (type == 1){
			criteria.andUsernameEqualTo(param);
		}else if (type==2) {
			criteria.andPhoneEqualTo(param);
		}else if (type==3) {
			criteria.andEmailEqualTo(param);
		}else{
			return E3Result.build(400, "非法参数");
		}
		
		//3. 执行查询
		List<TbUser> list =userMapper.selectByExample(tbUserExample);
		//4. 判断查询结果。如果查询到数据则返回false
		if(list ==null || list.size()==0){
			//5. 如果没有则返回true
			return E3Result.ok(true);
		}
		return E3Result.ok(false);
	}

	/**
	 * 用户注册功能
	 * 
	 * 请求的url：/user/register
		参数：表单的数据：username、password、phone、email
		返回值：json数据。e3Result
		接收参数：使用TbUser对象接收。
		请求的方法：post
		业务逻辑：
		1、使用TbUser接收提交的请求。
		2、补全TbUser其他属性。
		3、密码要进行MD5加密。
		4、把用户信息插入到数据库中。
		5、返回e3Result。
	 */
	@Override
	public E3Result createUser(TbUser user) {
		//1. 判断数据的可用性
		if(StringUtils.isBlank(user.getUsername())){
			return E3Result.build(400, "用户名不能为空");
		}
		
		if(StringUtils.isBlank(user.getPassword())){
			return E3Result.build(400, "密码不能为空");
		}
		
		//2.检查用户名的可用性
		E3Result result =checkData(user.getUsername(), 1);
		if(!(boolean) result.getData()){
			return E3Result.build(400, "此用户名已被使用");
		}
		
		//3. 检查电话的可用性
		if(StringUtils.isNotBlank(user.getPhone())){
			result=checkData(user.getPhone(), 2);
			if(!(boolean) result.getData()){
				return E3Result.build(400, "此电话已被使用");
			}
		}
		
		//3.检查email的可用性
		if(StringUtils.isNotBlank(user.getEmail())){
			result =checkData(user.getEmail(), 3);
			if(!(boolean) result.getData()){
				return E3Result.build(400, "此邮箱已被使用");
			}
			
		}
		
		//2、补全TbUser其他属性
		Date date = new Date();
		user.setCreated(date);
		user.setUpdated(date);
		
		//3、密码要进行MD5加密。
		String md5Pass =DigestUtils.md5Hex(user.getPassword().getBytes());
		user.setPassword(md5Pass);
		//4、把用户信息插入到数据库中。
		userMapper.insert(user);
		//5、返回e3Result。
		return E3Result.ok();
	}

	/**
	 * 登录功能
	 * 参数：
		1、用户名：String username
		2、密码：String password
		返回值：e3Result，包装token。
		业务逻辑：
		1、判断用户名密码是否正确。
		2、登录成功后生成token。Token相当于原来的jsessionid，字符串，可以使用uuid。
		3、把用户信息保存到redis。Key就是token，value就是TbUser对象转换成json。
		4、使用String类型保存Session信息。可以使用“前缀:token”为key
		5、设置key的过期时间。模拟Session的过期时间。一般半个小时。
		6、返回e3Result包装token。
	 */
	@Override
	public E3Result login(String username, String password) {
		//1.判断用户名密码是否正确。
		TbUserExample tbUserExample = new TbUserExample();
		Criteria criteria =tbUserExample.createCriteria();
		criteria.andUsernameEqualTo(username);
		//2. 查询用户信息
		List<TbUser> list =userMapper.selectByExample(tbUserExample);
		
		if(list==null||list.size()==0){
			return E3Result.build(400, "用户名或密码错误");
		}
		
		TbUser user = list.get(0);
		//3. 校验密码
		if(!user.getPassword().equals(DigestUtils.md5Hex(password.getBytes()))){
			return E3Result.build(400, "用户名或密码错误");
		}
		
		//2、登录成功后生成token。Token相当于原来的jsessionid，字符串，可以使用uuid
		String token=UUID.randomUUID().toString();
		user.setPassword(null);
		//3、把用户信息保存到redis。Key就是token，value就是TbUser对象转换成json。
		//4、使用String类型保存Session信息。可以使用“前缀:token”为key
		jedisClient.set(USER_INFO+":"+token, JsonUtils.objectToJson(user));
		//5、设置key的过期时间。模拟Session的过期时间。一般半个小时。
		jedisClient.expire(USER_INFO+":"+token, SESSION_EXPIRE);
		//6、返回e3Result包装token。
		return E3Result.ok(token);	
	}

	/**
	 * 请求的url：/user/token/{token}
		参数：String token需要从url中取。
		返回值：json数据。使用e3Result包装Tbuser对象。
		业务逻辑：
		1、从url中取参数。
		2、根据token查询redis。
		3、如果查询不到数据。返回用户已经过期。
		4、如果查询到数据，说明用户已经登录。
		5、需要重置key的过期时间。
		6、把json数据转换成TbUser对象，然后使用e3Result包装并返回。
	 */
	@Override
	public E3Result getUserByToken(String token) {
		//1. 根据token查询redis。
		String json =jedisClient.get(USER_INFO+":"+token);
		if(StringUtils.isBlank(json)){
			//2. 如果查询不到数据。返回用户已经过期
			return E3Result.build(400, "用户已经过期，请重新登录");
		}
		
		//需要重置key的过期时间。
		jedisClient.expire(USER_INFO+":"+token, SESSION_EXPIRE);
		//3. 如果查询到数据，说明用户已经登录。
		TbUser tbUser =JsonUtils.jsonToPojo(json, TbUser.class);
		return E3Result.ok(tbUser);
	}
	
	

}
