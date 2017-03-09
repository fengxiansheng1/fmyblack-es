package com.fmyblack.fmyes.bulk;

import java.util.List;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;

import com.fmyblack.fmyes.client.ClientProxy;

public class BulkUtil {
	
	static Client client = ClientProxy.getClient();

	public static void bulk(String index, String type, List<String> docs){
		int doc_num = docs.size();
		if(docs!=null && docs.size()>0){
			BulkRequestBuilder requestBuilder = client.prepareBulk();
			for (int i = 0; i < docs.size(); i++) {
				requestBuilder.add(client.prepareIndex(index, type).setSource(docs.get(i)));
			}
			long start = System.currentTimeMillis();
			BulkResponse response = requestBuilder.get();
			long end = System.currentTimeMillis();
			System.out.println("sudu:" + doc_num/(end-start));
			if (response.hasFailures()) {
	            System.out.println(response.buildFailureMessage());
			}
		}
	}
}
