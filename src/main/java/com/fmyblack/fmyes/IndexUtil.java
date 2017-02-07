package com.fmyblack.fmyes;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequestBuilder;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentBuilderString;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.indices.IndexAlreadyExistsException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class IndexUtil {

	/**
	 * 创建索引，并返回创建成功还是失败
	 * 
	 * @param index
	 * @param shardsNum
	 * @param refreshInterval
	 * @param replicator
	 * @return
	 */
	public boolean createIndex(Client client, String index, int shardsNum, String refreshInterval, String replicator) {
		try {
			Settings settings = Settings.settingsBuilder().put("number_of_shards", shardsNum)
					.put("refresh_interval", refreshInterval).put("number_of_replicas", replicator).build();

			CreateIndexResponse indexresponse = client.admin().indices().prepareCreate(index).setSettings(settings)
					.execute().actionGet();
			System.out.println("The Index Response is " + indexresponse.toString());
			return true;
		} catch (IndexAlreadyExistsException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 创建Mapping
	 * 
	 * @param index
	 * @param type
	 * @param fieldList
	 */
	public boolean createMapping(Client client, String index, String type, JSONArray fieldList) {
		try {
			XContentBuilder mapping = XContentFactory.jsonBuilder().startObject().startObject(type);
			mapping.startObject("properties");

			for (int i = 0; i < fieldList.size(); i++) {
				JSONObject fieldJo = fieldList.getJSONObject(i);
				String fieldName = fieldJo.getString("name");
				String fieldType = fieldJo.getString("type");
				if (fieldType.equals("string")) {
					mapping.startObject(fieldName).field("type", "string").field("index", "not_analyzed").endObject();
				} else if (fieldType.equals("date")) {
					String dateFormat = fieldJo.getString("format");
					mapping.startObject(fieldName).field("type", "date").endObject();
					// .field("format", dateFormat).endObject();
				} else {
					mapping.startObject(fieldName).field("type", fieldType).endObject();
				}
			}
			mapping.endObject();

			mapping.startObject("_all").field("enabled", true).startObject("norms").field("enabled", false).endObject()
					.endObject();

			mapping.endObject().endObject();

			PutMappingRequest mappingRequest = Requests.putMappingRequest(index).type(type).source(mapping);
			client.admin().indices().putMapping(mappingRequest).actionGet();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 创建模板
	 * 
	 * @param index
	 * @param type
	 * @param fieldList
	 * @param shardsNum
	 * @param refreshInterval
	 * @param replicator
	 * @throws IOException
	 */
	public void createTemplate(Client client, String index, String type, JSONObject properties, int shardsNum, String refreshInterval,
			String replicator) {

		// mapping
		XContentBuilder mapping = null;
		try {
			mapping = XContentFactory.jsonBuilder().startObject().startObject(type);
			mapping.startObject("properties");
			
//			for (Object property : properties.entrySet()) {
//				Entry<String, JSONObject> entry = (Entry<String, JSONObject>) property;
//				for()
//				JSONObject fieldJo = fieldList.getJSONObject(i);
//				String fieldName = fieldJo.getString("name");
//				String fieldType = fieldJo.getString("type");
//				if (fieldType.equals("string")) {
//					mapping.startObject(fieldName).field("type", "string").field("index", "not_analyzed").endObject();
//				} else if (fieldType.equals("date")) {
//					String dateFormat = fieldJo.getString("format");
//					mapping.startObject(fieldName).field("type", "date").endObject();
//					// .field("format", dateFormat).endObject();
//				} else {
//					mapping.startObject(fieldName).field("type", fieldType).endObject();
//				}
//			}
			
			mapping.
			mapping.endObject();
			
			mapping.startObject("_all").field("enabled", true).startObject("norms").field("enabled", false).endObject()
			.endObject();
			mapping.endObject().endObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// setting
		Settings settings = Settings.settingsBuilder().put("number_of_shards", shardsNum)
				.put("refresh_interval", refreshInterval).put("number_of_replicas", replicator).build();

		// template
		PutIndexTemplateRequestBuilder tmpBuilder = client.admin().indices().preparePutTemplate(index)
				.setTemplate(index + "_*").setSettings(settings).addMapping(type, mapping);
		// for(Entry<String, List<JSONObject>> entry :
		// typeFieldsMap.entrySet()){
		// XContentBuilder mappings = getMapping(entry.getKey(),
		// entry.getValue());
		// tmpBuilder.addMapping(entry.getKey(), mappings);
		// }
		Alias alias = new Alias(index);
		tmpBuilder.addAlias(alias);
		tmpBuilder.execute().actionGet();
	}

	/**
	 * 判断是否存在template
	 * 
	 * @param template
	 * @return
	 */
	public boolean existTemplate(Client client, String template) {
		return !client.admin().indices().prepareGetTemplates(template).execute().actionGet().isContextEmpty();
	}

	/**
	 * 删除template
	 * 
	 * @param template
	 */
	public void deleteTemplate(Client client, String template) {
		client.admin().indices().prepareDeleteTemplate(template).execute().actionGet();
	}

	/**
	 * bulk索引文档
	 * 
	 * @param index
	 * @param type
	 * @param docList
	 */
	public void bulk(Client client, String index, String type, List<XContentBuilder> docList) {
		BulkRequestBuilder requestBuilder = client.prepareBulk();
		for (XContentBuilder doc : docList) {
			requestBuilder.add(client.prepareIndex(index, type).setSource(doc));
		}
		BulkResponse response = requestBuilder.execute().actionGet();
		if (response.hasFailures()) {
			System.out.println("Response is Error");
		}
	}
	
	/**
	 * bulk索引文档
	 * @param docs
	 */
	public static void bulk(Client client, List<IndexRequestBuilder> docs){
		if( docs != null && docs.size() > 0 ){
			BulkRequestBuilder requestBuilder = client.prepareBulk();
			for (int i = 0; i < docs.size(); i++) {
				requestBuilder.add(docs.get(i));
			}
			BulkResponse response = requestBuilder.get();
			if (response.hasFailures()) {
	            System.out.println(response.buildFailureMessage());
			}
		}
	}
}
