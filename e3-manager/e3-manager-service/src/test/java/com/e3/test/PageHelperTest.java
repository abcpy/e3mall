package com.e3.test;

import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.e3.mapper.TbItemMapper;
import com.e3.pojo.TbItem;
import com.e3.pojo.TbItemExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

public class PageHelperTest {
	@Test
	public void testPageHelper(){
		//1.初始化Spring容器
		ApplicationContext applicationContext=new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		//2. 拿到mapper代理对象
		TbItemMapper tbItemMapper =applicationContext.getBean(TbItemMapper.class);
		//3. 设置分页
		PageHelper.startPage(1, 30);
		//执行查询
		TbItemExample tbItemExample =new TbItemExample();
		List<TbItem> list = tbItemMapper.selectByExample(tbItemExample);
		
		//取分页信息
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		System.out.println(pageInfo.getTotal());
		System.out.println(pageInfo.getPages());
	}
}
