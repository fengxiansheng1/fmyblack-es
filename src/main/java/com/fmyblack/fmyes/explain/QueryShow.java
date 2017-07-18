package com.fmyblack.fmyes.explain;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;

import com.fmyblack.fmyes.client.ClientProxy;

public class QueryShow {

	public static QueryBuilder[] qbs = {
		QueryBuilders.termQuery("at", "UAR-00139_124"),
		QueryBuilders.termQuery("at", "uar-00139_124"),
		QueryBuilders.termQuery("ip", "192"),
		QueryBuilders.termQuery("ip", "192.168.1.2"),
		QueryBuilders.wildcardQuery("ip", "192*"),
		QueryBuilders.wildcardQuery("ip", "*168*"),
		QueryBuilders.queryStringQuery("虎风"),
		QueryBuilders.queryStringQuery("title:虎风"),
		QueryBuilders.queryStringQuery("title:虎风").defaultOperator(Operator.AND),
		QueryBuilders.queryStringQuery("title:奔腾"),
		QueryBuilders.queryStringQuery("title:奔腾如虎"),
		QueryBuilders.queryStringQuery("title:\"奔腾如虎\""),
		QueryBuilders.queryStringQuery("title:\"奔腾如虎风\""),
	};
	
	public static void show(QueryBuilder qb, Scanner scan) {
		Client client = ClientProxy.getClient();
		
		SearchResponse resp = client.prepareSearch("jz_query")
								.setTypes("test")
								.setQuery(qb)
								.get();
		
		scan.nextLine();
		System.out.println("-------query------");
		System.out.println(qb.toString() + "\n");
		scan.nextLine();
		System.out.println("-------response---");
		System.out.println(resp.toString());
	}
	
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		
		for(QueryBuilder qb : qbs) {
			show(qb, scan);
		}
		
		while(true) {
			System.out.println("scan your query:");
			String line = scan.nextLine();
			if(line.equals("quit")) {
				break;
			}
			QueryBuilder qb = QueryBuilders.queryStringQuery(line);
			show(qb, scan);
		}
	}
}
