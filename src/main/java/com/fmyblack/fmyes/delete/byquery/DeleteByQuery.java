package com.fmyblack.fmyes.delete.byquery;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.fmyblack.fmyes.client.ClientProxy;

public class DeleteByQuery {

	String index = null;
	String type = null;
	QueryBuilder qb = null;
	
	int defaultBatchSize = 1000;
	int defaultTimeValue = 60000;
	
	public DeleteByQuery(String index, String type, QueryBuilder qb) {
		this.index = index;
		this.type = type;
		this.qb = qb;
	}
	
	public void setBatchSize(int batchSize) {
		this.defaultBatchSize = batchSize;
	}
	
	public void setTimeValue(int mills) {
		this.defaultTimeValue = mills;
	}
	
	public void action() {
		Client client = ClientProxy.getClient();
		
		SearchResponse scrollResp = client.prepareSearch(this.index)
											.setTypes(this.type)
											.setQuery(this.qb)
//											.setRouting("1")
											.setScroll(new TimeValue(this.defaultTimeValue))
											.setSize(this.defaultBatchSize)
											.get();
		
		while(true) {
			BulkRequestBuilder requestBuilder = client.prepareBulk().setRefresh(true);
			
			for (SearchHit hit : scrollResp.getHits().getHits()) {
				// do hit thing
				requestBuilder.add(client.prepareDelete()
										.setIndex(this.index)
										.setType(this.type)
										.setId(hit.getId())
										.setRouting("1"));
			}
			
			BulkResponse deleteResp = requestBuilder.execute().actionGet();
			System.out.println(deleteResp.getHeaders().toString());
			int fail = 0;
			if(deleteResp.hasFailures()) {
				for (BulkItemResponse item : deleteResp) {
					if (item.isFailed()) {
						fail++;
						System.out.println(item.getFailureMessage());
					}
				}
			}
			int deleted = deleteResp.getItems().length - fail;
			System.out.println("deleted " + deleted + " docs");
//			System.out.println(deleteResp.getContext().size());
//			System.out.println(deleteResp.toString());
			
			scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute()
					.actionGet();
			if (scrollResp.getHits().getHits().length == 0){
				break;
			}
		}
		
		System.out.println("delete over");
	}
	
	public static void main(String[] args) {
		String index = "test";
		String type = "testype";
		QueryBuilder qb = QueryBuilders.termQuery("ee", "1");
		DeleteByQuery dbq = new DeleteByQuery(index, type, qb);
		dbq.action();
	}
}
