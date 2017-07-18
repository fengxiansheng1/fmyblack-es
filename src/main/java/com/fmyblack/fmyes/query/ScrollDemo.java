package com.fmyblack.fmyes.query;

import com.fmyblack.fmyes.client.ClientProxy;
import com.fmyblack.util.config.ConfigHelper;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

public class ScrollDemo {

	static Client client;
	
	public static void scrollQuery(String index, String type) {
		long start = System.currentTimeMillis();
		SearchResponse scrollResp = client.prepareSearch(index)
									.setTypes(type)
									.setQuery(QueryBuilders.matchAllQuery())
									.addSort(SortBuilders.fieldSort("ip").order(SortOrder.DESC))
									.setScroll(new TimeValue(600000))
									.setSize(10)
									.get();
		int i = 0;
		while(true) {
			i++;
			for (SearchHit hit : scrollResp.getHits().getHits()) {
				// do hit thing
			}
			scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
								.setScroll(new TimeValue(600000))
								.get();
			if (scrollResp.getHits().getHits().length == 0) {
		        break;
		    }
		}
		long end = System.currentTimeMillis();
		System.out.println(i + "\t" + (end - start));
	}
	
	public static void normalQuery(String index, String type) {
		SearchResponse resp = client.prepareSearch(index).setTypes(type)
							.setQuery(QueryBuilders.matchAllQuery())
							.addSort(SortBuilders.fieldSort("ip").order(SortOrder.DESC))
							.setFrom(9000)
							.setSize(10)
							.get();
		System.out.println(resp);
	}
	
	public static void main(String[] args) {
		ConfigHelper.init();
		client = ClientProxy.getClient();
		String index = "t14";
		String type = "test";
		System.out.println("######### scroll #########");
		scrollQuery(index, type);
//		System.out.println("######### normal #########");
//		normalQuery(index, type);
	}
}
