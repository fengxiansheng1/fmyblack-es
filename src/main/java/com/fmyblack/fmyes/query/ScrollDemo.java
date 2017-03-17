package com.fmyblack.fmyes.query;

import com.fmyblack.fmyes.client.ClientProxy;
import com.fmyblack.util.config.ConfigHelper;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

public class ScrollDemo {

	static Client client;
	
	public static void scrollQuery(String index, String type) {
		SearchResponse scrollResp = client.prepareSearch(index)
									.setTypes(type)
									.setQuery(QueryBuilders.matchAllQuery())
									.setScroll(new TimeValue(60000))
									.setSize(4000)
									.get();
		
		while(true) {
			for (SearchHit hit : scrollResp.getHits().getHits()) {
				// do hit thing
			}
			scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
								.setScroll(new TimeValue(60000))
								.get();
			if (scrollResp.getHits().getHits().length == 0) {
		        break;
		    }
		}
	}
	
	public static void normalQuery(String index, String type) {
		client.prepareSearch(index).setTypes(type)
				.setQuery(QueryBuilders.matchAllQuery())
				.setSize(10001)
				.get();
	}
	
	public static void main(String[] args) {
		ConfigHelper.init();
		client = ClientProxy.getClient();
		String index = "test";
		String type = "testype";
		scrollQuery(index, type);
	}
}
