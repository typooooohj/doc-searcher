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
 * ElasticSearch������
 * 
 * Mapping,���Ƕ����������������ֶ��������������ͽ��ж���,
 * �����ڹ�ϵ���ݿ��б���ʱҪ�����ֶ�������������������,
 * ����es��mapping�����ݿ����ܶ࣬�����Զ�̬����ֶΡ�
 * һ�㲻��ҪҪָ��mapping�����ԣ���Ϊes���Զ��������ݸ�ʽ�����������ͣ�
 * �������Ҫ��ĳЩ�ֶ�����������ԣ��磺����ʹ�������ִ������Ƿ�ִʡ��Ƿ�洢�ȣ���
 * �ͱ����ֶ����mapping
 * 
 * @author ����
 *
 */
public class ElasticSearchManager {
	
	// jackson,������תjson�ַ���
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
	 * ��������,��������ʱ����
	 * @param indexName ��������
	 * @param indexType ��������
	 * @throws Throwable
	 */
	public void createIndex(String indexName,String indexType) {
		Client client = getClient();
		try {
			// ������ھ���ɾ������
			if (client.admin().indices().prepareExists(indexName).get().isExists()) {
				client.admin().indices().prepareDelete(indexName).get();
			}
			
			// �ȴ�����������
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
	 * ɾ��������
	 * @param indexName
	 */
	public void deleteIndex(String indexName) {
		Client client = getClient();
		client.admin().indices().prepareDelete(indexName).execute().actionGet();
		client.close();
	}
	
	/**
	 * ����һ���ĵ�,�������е�һ��
	 * @param item �ļ�
	 */
	public void addDoc(FileItem item) {
		Client client = getClient();		
		try {
			String json = mapper.writeValueAsString(item); // ת��json
			IndexResponse response = client.prepareIndex(Setting.INDEX_NAME, Setting.INDEX_TYPE)  
					.setId("1")     // ����ָ��ID 
					.setSource(json)  // ָ������ 
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
	 * ���������ļ���ES
	 * @param items �ļ��б�
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
			// ��������
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
         *  match query ����ƥ�� 
	 * @param name �ֶ�
	 * @param text �����ı�
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
	 *  �����matchQuery��multiMatchQuery��Ե��Ƕ��field��Ҳ����˵,
	 *  ��multiMatchQuery�У�fieldNames����ֻ��һ��ʱ����������matchQuery�൱;
	 *  ����fieldNames�ж������ʱ����field1��field2���ǲ�ѯ�Ľ���У�
	 *  Ҫôfield1�а���text��Ҫôfield2�а���text
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
     * ���ES_INDEX�µ��������� 
     */
    public static void clearAllDatas() {
    	
    }
	
	
}
