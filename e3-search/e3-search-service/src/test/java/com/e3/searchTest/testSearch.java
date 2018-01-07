package com.e3.searchTest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class testSearch {
	/**
	 * 添加文档
	 * @throws SolrServerException
	 * @throws IOException
	 */
	@Test
	public void addDocument() throws SolrServerException, IOException{
		//1. 创建一个SolrServer，使用HttpSolrServer创建对象。
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8081/solr");
		//2. 创建一个文档对象SolrInputDocument对象。
		SolrInputDocument document = new SolrInputDocument();
		//3. 向文档中添加域。必须有id域，域的名称必须在schema.xml中定义。
		document.addField("id", "test001");
		document.addField("item_title", "商品测试");
		document.addField("item_sell_point", "测试卖点");
		document.addField("item_price", "199");
		document.addField("item_category_name", "手机");
		//4. 把文档添加到索引库中。
		solrServer.add(document);
		//5. 提交
		solrServer.commit();	
	}
	
	/**
	 * 根据id删除文档
	 * @throws SolrServerException
	 * @throws IOException
	 */
	@Test
	public void deleteDocument() throws SolrServerException, IOException{
		//第一步：创建一个SolrServer对象。
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8081/solr");
		//第二步：调用SolrServer对象的根据id删除的方法。
		solrServer.deleteById("test001");
		//第三步：提交。
		solrServer.commit();
	}
	
	/**
	 * 根据查询删除文档
	 * @throws SolrServerException
	 * @throws IOException
	 */
	@Test
	public void deleteDocumentByQuery() throws SolrServerException, IOException{
		SolrServer solrServer=new HttpSolrServer("http://192.168.25.128:8081/solr");
		solrServer.deleteByQuery("item_title:商品测试");
		solrServer.commit();
	}
	
	/**
	 * 简单查询
	 * @throws SolrServerException
	 */
	@Test
	public void queryDocument() throws SolrServerException{
		//第一步：创建一个SolrServer对象
		SolrServer solrServer= new HttpSolrServer("http://192.168.25.128:8081/solr");
		//第二步：创建一个SolrQuery对象。
		SolrQuery solrQuery=new SolrQuery();
		//第三步：向SolrQuery中添加查询条件、过滤条件。。。
		solrQuery.setQuery("*:*");
		//第四步：执行查询。得到一个Response对象。
		QueryResponse response =solrServer.query(solrQuery);
		//第五步：取查询结果。
		SolrDocumentList list = response.getResults();
		System.out.println("查询结果的总记录数:"+list.getNumFound());
		//第六步：遍历结果并打印。
		for (SolrDocument solrDocument : list) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("item_title"));
			System.out.println(solrDocument.get("item_sell_point"));

		}
	}
	
	@Test
	public void queryDocumentWithHighLighting() throws SolrServerException{
		//第一步：创建一个SolrServer对象
		SolrServer solrServer= new HttpSolrServer("http://192.168.25.128:8081/solr");
		//第二步：创建一个SolrQuery对象。
		SolrQuery solrQuery=new SolrQuery();
		//第三步：向SolrQuery中添加查询条件、过滤条件。。。
		solrQuery.setQuery("测试");
		//第四步：指定默认搜索域
		solrQuery.set("df", "item_keywords");
		//第五步：开启高亮显示
		solrQuery.setHighlight(true);
		//第六步：高亮显示的域
		solrQuery.addHighlightField("item_title");
		solrQuery.setHighlightSimplePre("<em>");
		solrQuery.setHighlightSimplePost("</em>");
		//第七部：执行查询
		QueryResponse response =solrServer.query(solrQuery);
		//第八步：得到查询结果
		SolrDocumentList documentList=response.getResults();
		for (SolrDocument solrDocument : documentList) {
			System.out.println(solrDocument.get("id"));
			//取高亮显示
			Map<String,Map<String,List<String>>> hightLighting=response.getHighlighting();
			List<String> list =hightLighting.get(solrDocument.get("id")).get("item_title");
			String itemTitle=null;
			if(list!=null&&list.size()>0){
				itemTitle=list.get(0);
			}else {
				itemTitle=(String) solrDocument.get("item_title");
			}
			
			System.out.println(itemTitle);
			System.out.println(solrDocument.get("item_price"));
		}

		
	}
	
}
