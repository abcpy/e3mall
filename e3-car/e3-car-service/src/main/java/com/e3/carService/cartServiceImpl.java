package com.e3.carService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e3.car.CartService;
import com.e3.mapper.TbItemMapper;
import com.e3.pojo.TbItem;
import com.e3.redisUtils.JedisClient;
import com.e3.utils.E3Result;
import com.e3.utils.JsonUtils;import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@Service
public class cartServiceImpl implements CartService{
	
	/*登录状态下添加购物车，直接把数据保存到redis中。需要调用购物车服务，使用redis的hash来保存数据。
	Key：用户id
	Field：商品id
	Value：商品对象转换成json

	参数：
	1、用户id
	2、商品id
	3、商品数量

	业务逻辑：
	1、根据商品id查询商品信息
	2、把商品信息保存到redis
	a)判断购物车中是否有此商品
	b)如果有，数量相加
	c)如果没有，根据商品id查询商品信息。
	d)把商品信息添加到购物车
	3、返回值。E3Result*/
	@Autowired
	private JedisClient jedisClient;
	
	@Autowired
	private TbItemMapper itemMapper;
	
	private String CART_REDIS_KEY="CART_REDIS_KEY";

	@Override
	public E3Result addCart(long userId, long itemId, int num) {
		//判断购物车中是否有此商品
		Boolean flag=jedisClient.hexists(CART_REDIS_KEY+":"+userId, itemId+"");
		//如果有，数量相加
		if(flag){
			String json=jedisClient.hget(CART_REDIS_KEY+":"+userId, itemId+"");
			//找到商品
			TbItem tbItem =JsonUtils.jsonToPojo(json, TbItem.class);
			//数量相加
			tbItem.setNum(tbItem.getNum()+num);
			//写回redis
			jedisClient.hset(CART_REDIS_KEY+":"+userId, itemId+"", JsonUtils.objectToJson(tbItem));
			//返回添加成功
			return E3Result.ok();
			
		}
		//c)如果没有，根据商品id查询商品信息。
		TbItem tbItem =itemMapper.selectByPrimaryKey(itemId);
		//设置商品数量
		tbItem.setNum(num);
		//取照片
		if(StringUtils.isNotBlank(tbItem.getImage())){
			String image=tbItem.getImage();
			String []strings=image.split(",");
			tbItem.setImage(strings[0]);
		}
		//d)把商品信息添加到购物车
		jedisClient.hset(CART_REDIS_KEY+":"+userId, itemId+"", JsonUtils.objectToJson(tbItem));
		
		return E3Result.ok();
	}
	
	
	/**
	 * 1、合并购物车
		参数：用户id
		      List<TbItem>
		返回值：E3Result
		业务逻辑：
		1）遍历商品列表
		2）如果服务端有相同商品，数量相加
		3）如果没有相同商品，添加一个新的商品
		
		2、取购物车列表
		参数：用户id
		返回值：List<TbItem>
		业务逻辑：
		1）从hash中取所有商品数据
		2）返回
			 */
	@Override
	public E3Result mergeCart(long userId,List<TbItem> list){
		//遍历商品列表
		for (TbItem tbItem : list) {
			addCart(userId, tbItem.getId(), tbItem.getNum());
		}
		
		return E3Result.ok();
	}
	
	/**
	 * 得到商品
	 * @param userId
	 * @return
	 */
	@Override
	public List<TbItem> geTbItems(long userId){
		//从redis中取得购物车列表
		List<String> strings =jedisClient.hvals(CART_REDIS_KEY+":"+userId);
		//将list转换成TbItem
		List<TbItem> list =new ArrayList<>();
		for (String item : strings) {
			TbItem tbItem =JsonUtils.jsonToPojo(item, TbItem.class);
			//添加商品到列表
			list.add(tbItem);
		}
		
		return list;
	}

	/*参数：
	1、用户id
	2、商品id
	3、数量
	返回值：
	E3Result
	业务逻辑：
	1、根据商品id从hash中取商品信息。
	2、把json转换成java对象
	3、更新商品数量
	4、把商品数据写回hash*/

	@Override
	public E3Result updateCatnum(long userId, long itemId, int num) {
		//1、根据商品id从hash中取商品信息。
		String json =jedisClient.hget(CART_REDIS_KEY+":"+userId, itemId+"");
		//2、把json转换成java对象
		if(StringUtils.isNotBlank(json)){
			TbItem tbItem =JsonUtils.jsonToPojo(json, TbItem.class);
			tbItem.setNum(num);
			jedisClient.hset(CART_REDIS_KEY+":"+userId, itemId+"", JsonUtils.objectToJson(tbItem));
		}
		//把商品数据写回hash
		return E3Result.ok();
	}


	/**
	 * 参数：用户id
	      商品id
	返回值：E3Result
	业务逻辑：
	根据商品id删除hash中对应的商品数据。
	 */
	@Override
	public E3Result delCat(long userId, long itemId) {
		jedisClient.hdel(CART_REDIS_KEY+":"+userId, itemId+"");
		return E3Result.ok();
	}


	/**
	 * 清空购物车
	 */
	@Override
	public E3Result clearCartItem(long userId) {
		long result =jedisClient.del(CART_REDIS_KEY+":"+userId);
		return E3Result.ok();
	}
	
	

}
