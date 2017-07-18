package com.fmyblack.fmyes.explain;

import org.apache.lucene.search.Explanation;
import org.elasticsearch.action.explain.ExplainResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.fmyblack.fmyes.client.ClientProxy;

public class QueryStringExplain {

	static Client client = ClientProxy.getClient();
	
	public static void plain(Doc doc, QueryBuilder qb ) {
		ExplainResponse resp = client.prepareExplain(doc.index, doc.type, doc.id)
										.setQuery(qb)
										.get();
		System.out.println(qb.toString());
		for( Explanation expl : resp.getExplanation().getDetails()) {
			System.out.println("\n-----------\n" + expl);
		}
	}
	
	public static void main(String[] args) {
		Doc doc = new Doc("t17", "test", "AVt6okhrdPM7XUA3CJ4V");
		QueryBuilder qb = QueryBuilders.queryStringQuery("message:\"190.195.81.94\"");
		plain(doc, qb);

		QueryBuilder qb2 = QueryBuilders.termQuery("ip", "190.195.81.94");
		plain(doc, qb2);
	}
	
	static class Doc {
		
		String index;
		String type;
		String id;
		
		public Doc(String index, String type, String id) {
			this.index = index;
			this.type = type;
			this.id = id;
		}
	}
}
