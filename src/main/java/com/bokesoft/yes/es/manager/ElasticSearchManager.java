package com.bokesoft.yes.es.manager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Field.Index;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.bokesoft.yes.es.common.Setting;
import com.bokesoft.yes.es.file.FileItem;

/**
 * ElasticSearch处理类
 * 
 * Mapping,就是对索引库中索引的字段名及其数据类型进行定义,
 * 类似于关系数据库中表建立时要定义字段名及其数据类型那样,
 * 不过es的mapping比数据库灵活很多，它可以动态添加字段。
 * 一般不需要要指定mapping都可以，因为es会自动根据数据格式定义它的类型，
 * 如果你需要对某些字段添加特殊属性（如：定义使用其它分词器、是否分词、是否存储等），
 * 就必须手动添加mapping
 * 
 * @author 陈瑞
 *
 */
public class ElasticSearchManager {
	
	// jackson,将对象转json字符串
	private ObjectMapper mapper = new ObjectMapper();
	
	public static Client getClient() {
		Client client = null;
		try {
			client = TransportClient.builder().build()
			        .addTransportAddress(new InetSocketTransportAddress(
			        		InetAddress.getByName(Setting.getInstance().getHost()), Setting.getInstance().getPort()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}	
		return client;
	}
	
	/**
	 * 创建索引,程序启动时调用
	 * @param indexName 索引名称
	 * @param indexType 索引类型
	 * @throws Throwable
	 */
	public void createIndex(String indexName,String indexType) {
		Client client = getClient();
		try {
			// 如果存在就先删除索引
			if (client.admin().indices().prepareExists(indexName).get().isExists()) {
				client.admin().indices().prepareDelete(indexName).get();
			}
			
			// 先创建空索引库
			client.admin().indices().prepareCreate(indexName).execute().actionGet();
			
			XContentBuilder builder=XContentFactory.jsonBuilder()
					.startObject()
//					.startObject(indexName)
					.startObject("properties")
					.startObject("id").field("type", "string").field("store", "yes")/*.field("indexAnalyzer", "ik").field("searchAnalyzer", "ik")*/.endObject()
					.startObject("content").field("type", "string").field("store", "yes")/*.field("indexAnalyzer", "ik").field("searchAnalyzer", "ik")*/.endObject()
					.startObject("path").field("type", "string").field("store", "no").endObject()
					.endObject()
//					.endObject()
					.endObject();
			PutMappingRequest mapping = Requests.putMappingRequest(indexName).type(indexType).source(builder);
			client.admin().indices().putMapping(mapping).actionGet();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.close();
		}
	}
	
	/**
	 * 删除索引库
	 * @param indexName
	 */
	public void deleteIndex(String indexName) {
		Client client = getClient();
		client.admin().indices().prepareDelete(indexName).execute().actionGet();
		client.close();
	}
	
	/**
	 * 新增一条文档,即索引中的一行
	 * @param item 文件
	 */
	public void addDoc(FileItem item) {
		Client client = getClient();		
		try {
			String json = mapper.writeValueAsString(item); // 转成json
			IndexResponse response = client.prepareIndex(Setting.INDEX_NAME, Setting.INDEX_TYPE)  
					.setId("1")     // 单独指定ID 
					.setSource(json)  // 指定数据 
					.execute()  
					.actionGet();
			System.out.println("Doc add success,id:" + response.getId());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.close();
		}
	}
	
	/**
	 * 批量新增文件到ES
	 * @param items 文件列表
	 */
	public void addDocs(List<FileItem> items) {
		Client client = getClient();		
		try {
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			for( FileItem item : items ) {
				String json = mapper.writeValueAsString(item);
				IndexRequestBuilder indexRequest = client.prepareIndex(Setting.INDEX_NAME, Setting.INDEX_TYPE)
						.setId(item.getId())
						.setSource(json);
				bulkRequest.add(indexRequest);
			}
			// 批量插入
			BulkResponse bulkResponse = bulkRequest.execute().actionGet();
			if( !bulkResponse.hasFailures() ) {
				System.out.println("docs add success!");				
			} else {
				System.out.println(bulkResponse.buildFailureMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.close();
		}
	}
	
	/**
         *  match query 单个匹配 
	 * @param name 字段
	 * @param text 搜索文本
	 * @return
	 */
	public List<FileItem> matchQuery(String name, Object text) {
		Client client = getClient();		
		List<FileItem> items = new ArrayList<FileItem>();		
		try {
			QueryBuilder qb = QueryBuilders.matchQuery(name,text);
			SearchResponse response = client.prepareSearch(Setting.INDEX_NAME)  
					.setTypes(Setting.INDEX_TYPE)  
					.setSearchType(SearchType.SCAN)  
					.setQuery(qb)  
					.execute()  
					.actionGet();
			for (SearchHit hit : response.getHits()) {  
				FileItem item = mapper.readValue(hit.getSourceAsString(), FileItem.class);
				items.add(item);
			}  			
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			client.close();
		}
		return items;
	}
	
	/**
	 *  相对于matchQuery，multiMatchQuery针对的是多个field，也就是说,
	 *  当multiMatchQuery中，fieldNames参数只有一个时，其作用与matchQuery相当;
	 *  而当fieldNames有多个参数时，如field1和field2，那查询的结果中，
	 *  要么field1中包含text，要么field2中包含text
	 * @param text
	 * @param fieldNames
	 * @return
	 */
	public List<FileItem> multiMatchQuery(Object text, String...fieldNames) {
		Client client = getClient();		
		List<FileItem> items = new ArrayList<FileItem>();		
		try {
			QueryBuilder qb = QueryBuilders.multiMatchQuery(text,fieldNames);
			
			SearchResponse response = client.prepareSearch(Setting.INDEX_NAME)  
					.setTypes(Setting.INDEX_TYPE)  
					.setSearchType(SearchType.SCAN)  
					.setQuery(qb)  
					.execute()  
					.actionGet();
			for (SearchHit hit : response.getHits()) {  
				FileItem item = mapper.readValue(hit.getSourceAsString(), FileItem.class);
				items.add(item);
			}  			
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			client.close();
		}
		return items;
	}
	
	  /**
     * 清除ES_INDEX下的所有数据 
     */
    public static void clearAllDatas() {
    	
    }
	
	
}
