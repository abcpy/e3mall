package com.e3.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.e3.mapper.TbContentCategoryMapper;
import com.e3.mapper.TbContentMapper;
import com.e3.mapper.TbItemDescMapper;
import com.e3.mapper.TbItemMapper;
import com.e3.page.ItemService;
import com.e3.page.pojo.EasyUIDataGridResult;
import com.e3.page.pojo.EasyUITreeNode;
import com.e3.pojo.TbContent;
import com.e3.pojo.TbContentCategory;
import com.e3.pojo.TbContentCategoryExample;
import com.e3.pojo.TbContentCategoryExample.Criteria;
import com.e3.pojo.TbContentExample;
import com.e3.pojo.TbItem;
import com.e3.pojo.TbItemDesc;
import com.e3.pojo.TbItemExample;
import com.e3.redisUtils.JedisClient;
import com.e3.utils.E3Result;
import com.e3.utils.IDUtils;
import com.e3.utils.JsonUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class itemServiceImpl implements ItemService{

	@Autowired
	private TbItemMapper itemmapper;
	@Autowired
	private TbItemDescMapper descmapper;
	@Autowired
	private TbContentCategoryMapper tbContentCategorymapper;
	@Autowired
	private TbContentMapper tbContentMapper;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Resource
	private Destination destination;
	@Autowired
	private JedisClient jedisClient;
	@Value("${REDIS_ITEM_PRE}")
	private String REDIS_ITEM_PRE;
	@Value("${ITEM_CACHE_EXPIRE}")
	private Integer ITEM_CACHE_EXPIRE;
	
	
	

	@Override
	public EasyUIDataGridResult getItemList(int page, int rows) {
		//设置分页
		PageHelper.startPage(page, rows);
		//设置查询
		TbItemExample tbItemExample = new TbItemExample();
		List<TbItem> list = itemmapper.selectByExample(tbItemExample);
		//区分页信息
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setTotal(pageInfo.getTotal());
		result.setRows(list);
		return result;
	}

	//添加商品
	@Override
	public E3Result addGoods(TbItem tbItem, String desc) {
		// private Long id;
		//1、生成商品id
		final long id =IDUtils.genItemId();
		tbItem.setId(id);
		//2.补全TbItem对象的属性
		//'商品状态，1-正常，2-下架，3-删除',
		tbItem.setStatus((byte)1);
		tbItem.setCreated(new Date());
		tbItem.setUpdated(new Date());
		//3. 向商品表插入数据
		itemmapper.insert(tbItem);
		
		//4.创建一个TbItemDesc对象
		TbItemDesc tbItemDesc = new TbItemDesc();
		//5、补全TbItemDesc的属性
		tbItemDesc.setItemId(id);
		tbItemDesc.setItemDesc(desc);
		tbItemDesc.setCreated(new Date());
		tbItemDesc.setUpdated(new Date());
		// 6、向商品描述表插入数据
		descmapper.insert(tbItemDesc);
		
		//发送一个商品添加的消息
		jmsTemplate.send(destination, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage =session.createTextMessage(id+"");
				return textMessage;
			}
		});
		//7、E3Result.ok()
		return E3Result.ok();
	  
	}

	@Override
	public List<EasyUITreeNode> getContentCatagoryList(long parentId) {
		//1. 1、取查询参数id，parentId
		//2、根据parentId查询tb_content_category，查询子节点列表。
	     TbContentCategoryExample tbContentCategoryExample = new TbContentCategoryExample();
	     Criteria caCriteria =tbContentCategoryExample.createCriteria();
	     caCriteria.andParentIdEqualTo(parentId);
		//3、得到List<TbContentCategory>
		List<TbContentCategory> list =tbContentCategorymapper.selectByExample(tbContentCategoryExample);
		//4、把列表转换成List<EasyUITreeNode>
		List<EasyUITreeNode> resultList=new ArrayList<>();
		for (TbContentCategory tbContentCategory : list) {
			EasyUITreeNode easyUITreeNode=new EasyUITreeNode();
			easyUITreeNode.setId(tbContentCategory.getId());
			easyUITreeNode.setText(tbContentCategory.getName());
			easyUITreeNode.setState(tbContentCategory.getIsParent()?"closed":"open");
			resultList.add(easyUITreeNode);
		}
		return resultList;
	}

	//添加分类节点
	@Override
	public E3Result addContentCategory(long parentId, String name) {
//		2、向tb_content_category表中插入数据。
//		a)创建一个TbContentCategory对象
		TbContentCategory contentCategory = new TbContentCategory();
//		b)补全TbContentCategory对象的属性
		contentCategory.setIsParent(false);
		contentCategory.setName(name);
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		contentCategory.setName(name);
		contentCategory.setParentId(parentId);
		//排列序号，表示同级类目的展现次序，如数值相等则按名称次序排列。取值范围:大于零的整数
		////状态。可选值:1(正常),2(删除)
		contentCategory.setStatus(1);
//		c)向tb_content_category表中插入数据
		tbContentCategorymapper.insert(contentCategory);
		// 3、判断父节点的isparent是否为true，不是true需要改为true。
		TbContentCategory parentNode =tbContentCategorymapper.selectByPrimaryKey(parentId);
		if(!parentNode.getIsParent()){
			parentNode.setIsParent(true);
			//更新父节点
			tbContentCategorymapper.updateByPrimaryKey(parentNode);
		}
		
		//4. 主键返回
		//5. 返回E3Result，其中包装TbContentCategory对象
		return E3Result.ok(contentCategory);
	}

	//添加内容
	@Override
	public E3Result addContent(TbContent tbContent) {
		//把TbContent对象属性补全。
		Date data = new Date();
		tbContent.setCreated(data);
		tbContent.setUpdated(data);
		//插入数据
		tbContentMapper.insert(tbContent);
		
		return E3Result.ok();
	}

	@Override
	public EasyUIDataGridResult getItemContentList(int page, int rows) {
		//设置分页
		PageHelper.startPage(page, rows);
		//查询
		TbContentExample example = new TbContentExample();
		List<TbContent> list =tbContentMapper.selectByExample(example);
		//分页信息
		PageInfo<TbContent> pageInfo=new PageInfo<>(list);
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setTotal(pageInfo.getTotal());
		result.setRows(pageInfo.getList());
		return result;
	}

	@Override
	public TbItem geTbItem(long itemId) {
		//查询缓存
		try{
		String json=jedisClient.get(REDIS_ITEM_PRE+":"+itemId+"BASE:");
		if(StringUtils.isNoneBlank(json)){
			TbItem tbItem =JsonUtils.jsonToPojo(json, TbItem.class);
			return tbItem;
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		TbItemExample tbItemExample=new TbItemExample();
		com.e3.pojo.TbItemExample.Criteria criteria=tbItemExample.createCriteria();
		criteria.andIdEqualTo(itemId);
		List<TbItem> list =itemmapper.selectByExample(tbItemExample);
		if(list!=null&&list.size()>0){
			try{
			//加入缓存
			jedisClient.set(REDIS_ITEM_PRE+":"+itemId+"BASE:", JsonUtils.objectToJson(list.get(0)));
			//设置缓存过期时间
			jedisClient.expire(REDIS_ITEM_PRE+":"+itemId+"BASE:", ITEM_CACHE_EXPIRE);
			}catch(Exception e){
				e.printStackTrace();
			}
			return list.get(0);
		}
		return null;
	}

	@Override
	public TbItemDesc geTbItemDesc(long itemId) {
		//查询缓存
		try{
			String json =jedisClient.get(REDIS_ITEM_PRE+":"+itemId+"DESC:");
			TbItemDesc tbItemDesc =JsonUtils.jsonToPojo(json, TbItemDesc.class);
			return tbItemDesc;
		}catch(Exception e){
			e.printStackTrace();
		}
		TbItemDesc tbItemDesc =descmapper.selectByPrimaryKey(itemId);
		try{
			//加入缓存
			jedisClient.set(REDIS_ITEM_PRE+":"+itemId+"DESC:", JsonUtils.objectToJson(tbItemDesc));
			jedisClient.expire(REDIS_ITEM_PRE+":"+itemId+"DESC:", ITEM_CACHE_EXPIRE);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return tbItemDesc;
	}
	
	

}
