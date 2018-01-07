package com.e3.search.service;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.support.logging.Log;
import com.e3.search.dao.SearchDao;
import com.e3.search.mapper.ItemMapper;
import com.e3.search.pojo.SearchItem;
import com.e3.search.pojo.SearchResult;
import com.e3.searchItem.ImportItem;
import com.e3.utils.E3Result;

@Service
public class searchItemImpl implements ImportItem{
	
	@Autowired
	private ItemMapper itemMapper;
	@Autowired
	private SolrServer server;
	@Autowired
	private SearchDao searchDao;

	@Override
	public E3Result getSearchItem() {
		//查询商品
		List<SearchItem> itemList=itemMapper.getItemList();
		//将商品导入索引库
		try {
				for (SearchItem searchItem : itemList) {
				SolrInputDocument document=new SolrInputDocument();
				document.addField("id", searchItem.getId());
				document.addField("item_title", searchItem.getTitle());
				document.addField("item_sell_point", searchItem.getSell_point());
				document.addField("item_price", searchItem.getPrice());
				document.addField("item_image", searchItem.getImage());
				document.addField("item_category_name", searchItem.getCategory_name());
				//加入索引库
				server.add(document);
			}
			
			//提交
			server.commit();
			return E3Result.ok();
			
		} catch (Exception e) {
			e.printStackTrace();
			return E3Result.build(500, "商品导入失败");
		}
	}

	/**
	 * 参数：String keyWord
      int page
      int rows
                        返回值：SearchResult
	 */
	@Override
	public SearchResult search(String keyWord, int page, int rows) throws Exception{
		//设置查询条件
		SolrQuery query = new SolrQuery();
		//设置查询条件
		query.setQuery(keyWord);
		//设置分页条件
		if(page<=0) page=1;
		query.setStart((page-1)*rows);
		query.setRows(rows);
		//设置默认收索域
		query.set("df", "item_title");
		//设置高亮显示
		query.setHighlight(true);
		query.addHighlightField("item_title");
		query.setHighlightSimplePre("<em style=\"color:red\">");
		query.setHighlightSimplePost("</em>");
				
		//执行查询
				SearchResult searchResult = searchDao.search(query);
				//计算总页数
				int recourdCount = searchResult.getRecourdCount();
				int pages = recourdCount / rows;
				if (recourdCount % rows > 0) pages++;
				//设置到返回结果
				searchResult.setTotalPages(pages);
		return searchResult;
	}
	
	/**
	 * 添加商品
	 * @param itemId
	 * @return
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public E3Result addDocument(long itemId) throws Exception{
//		1、根据商品id查询商品信息。
		SearchItem  searchItem =itemMapper.getItemById(itemId);
//		2、创建一SolrInputDocument对象。
		SolrInputDocument document =new SolrInputDocument();
//		3、使用SolrServer对象写入索引库。
		document.addField("id", searchItem.getId());
		document.addField("item_title", searchItem.getTitle());
		document.addField("item_sell_point", searchItem.getSell_point());
		document.addField("item_price", searchItem.getPrice());
		document.addField("item_image", searchItem.getImage());
		document.addField("item_category_name", searchItem.getCategory_name());
		//加入索引库
		server.add(document);
		server.commit();
//		4、返回成功，返回e3Result。
		return E3Result.ok();
		
	}

}
