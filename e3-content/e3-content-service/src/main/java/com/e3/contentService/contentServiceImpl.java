package com.e3.contentService;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.e3.content.service.contentService;
import com.e3.mapper.TbContentMapper;
import com.e3.pojo.TbContent;
import com.e3.pojo.TbContentExample;
import com.e3.pojo.TbContentExample.Criteria;
import com.e3.redisUtils.JedisClient;
import com.e3.utils.E3Result;
import com.e3.utils.JsonUtils;

@Service
public class contentServiceImpl implements contentService{

	@Autowired
	private TbContentMapper tbContentMapper;
	@Autowired
	private JedisClient jedisClient;
	
	@Value("$CONTENT_KEY")
	private String CONTENT_KEY;
	
	//添加内容
			@Override
			public E3Result addContent(TbContent tbContent) {
				//把TbContent对象属性补全。
				Date data = new Date();
				tbContent.setCreated(data);
				tbContent.setUpdated(data);
				//插入数据
				tbContentMapper.insert(tbContent);
				//缓存同步
				jedisClient.hdel(CONTENT_KEY, tbContent.getCategoryId().toString());
				
				return E3Result.ok();
			}
	

	/**
	 * 获取首页轮播图的信息
	 */
	@Override
	public List<TbContent> getContent(long cid) {
		//向缓存中取数据
		try {
			String result=jedisClient.hget(CONTENT_KEY, cid+"");
			if(StringUtils.isNotBlank(result))
			{
				List<TbContent> list =JsonUtils.jsonToList(result, TbContent.class);
				return list;
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		TbContentExample example = new TbContentExample();
		Criteria criteria =example.createCriteria();
		criteria.andCategoryIdEqualTo(cid);
		List<TbContent> list =tbContentMapper.selectByExampleWithBLOBs(example);
		//向缓存中添加数据
		try {
			jedisClient.hset(CONTENT_KEY, cid+"", JsonUtils.objectToJson(list));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

}
