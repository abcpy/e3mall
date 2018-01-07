package com.e3.search.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.e3.search.pojo.SearchItem;
import com.e3.search.pojo.SearchResult;


/**
 * 跟据查询条件查询索引库，返回对应的结果。
         参数：SolrQuery
         返回结果：SearchResult
 * @author liujian
 *
 */
@Repository
public class SearchDao {
	@Autowired
	private SolrServer server;
	
	public SearchResult search(SolrQuery query) throws SolrServerException{
		//1.根据查询条件查询索引库
		
			QueryResponse response =server.query(query);
			//2. 查询结果
			SolrDocumentList list =response.getResults();
			//3. 得到总记录数
			long numFound=list.getNumFound();
			SearchResult searchResult  = new SearchResult();
			searchResult.setRecourdCount((int) numFound);
			//4. 创建一个商品列表对象
			List<SearchItem> seItems =new ArrayList<>();
			//去高亮结果
			Map<String, Map<String, List<String>>> highlighting=response.getHighlighting();
			
			for (SolrDocument solrDocument : list) {
				//取商品信息
				SearchItem item = new SearchItem();
				item.setCategoty_name((String) solrDocument.get("item_category_name"));
				item.setId((String) solrDocument.get("id"));
				item.setImage((String) solrDocument.get("item_image"));
				item.setPrice((long) solrDocument.get("item_price"));
				item.setSell_point((String) solrDocument.get("item_sell_point"));
				
				//取高亮结果
				List<String> hightList =  highlighting.get(solrDocument.get("id")).get("item_title");
				String itemTitle="";
				if(hightList!=null&&hightList.size()>0){
					itemTitle=hightList.get(0);
				}else{
					itemTitle=(String) solrDocument.get("item_title");
				}
				
				item.setTitle(itemTitle);
				seItems.add(item);
				
			}
			
			searchResult.setList(seItems);
			return searchResult;
		} 
	}
	

