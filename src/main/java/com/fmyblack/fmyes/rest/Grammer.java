package com.fmyblack.fmyes.rest;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;

import com.fmyblack.fmyes.client.ClientProxy;
import com.fmyblack.util.config.ConfigHelper;

import net.sf.json.JSONObject;

public class Grammer {

	static Client client;
	
	public static void showIndex() {
		JSONObject doc = new JSONObject();
		doc.put("f1", "f2");
		IndexRequestBuilder req = client.prepareIndex("myindex", "mytype")
				.setSource(doc.toString())
				.setRouting("tt");
		System.out.println(req.toString());
	}
	
	public static void main(String[] args) {
		String dir = "/Users/fmyblack/javaproject/fmyblack-es/src/main/resources/conf";
		ConfigHelper.init(dir);
		client = ClientProxy.getClient();
		showIndex();
	}
}
