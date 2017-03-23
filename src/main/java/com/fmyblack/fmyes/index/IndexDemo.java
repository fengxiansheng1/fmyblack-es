package com.fmyblack.fmyes.index;

import org.elasticsearch.client.Client;

import com.fmyblack.fmyes.client.ClientProxy;
import com.fmyblack.util.config.ConfigHelper;

import net.sf.json.JSONObject;

public class IndexDemo {

	static Client client;
	
	public static void index(String index, String type, String id, String source) {
		client.prepareIndex(index, type, id)
			.setSource(source)
			.get();
	}
	
	/**
	 * can not update but throw DocumentAlreadyExistsException
	 * @param index
	 * @param type
	 * @param id
	 * @param source
	 */
	public static void create(String index, String type, String id, String source) {
		client.prepareIndex(index, type, id)
			.setSource(source)
			.setCreate(true)
			.get();
	}
	
	public static void main(String[] args) {
		ConfigHelper.init();
		client = ClientProxy.getClient();
		JSONObject source = new JSONObject();
		source.put("my", "my2");
		create("a", "v", "id1", source.toString());
	}
}
