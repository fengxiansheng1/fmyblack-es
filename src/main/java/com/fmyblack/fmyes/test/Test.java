package com.fmyblack.fmyes.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.fmyblack.fmyes.client.ClientProxy;
import com.fmyblack.util.config.ConfigHelper;

import net.sf.json.JSONObject;

public class Test implements Callable<String>{

	static Client client;
	
	static QueryBuilder qb;
	
	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		client = ClientProxy.getClient();
		Random ran = new Random(47);
		List<String> l = new ArrayList<String>();
		for(int i = 0; i < 10000; i++) {
			l.add("" + ran.nextInt(100000));
		}
		l.add("190.195.81.94");
		qb = QueryBuilders.boolQuery().filter(QueryBuilders.termsQuery("ip", l));
		System.out.println(client.prepareSearch("t17").setQuery(qb).get());
	}

	public Test() {
		Random rand = new Random(47);
		qb = QueryBuilders.matchQuery("message", rand.nextInt(1000000));
	}

	@Override
	public String call() throws Exception {
		// TODO Auto-generated method stub
		return client.prepareSearch("test")
				.setQuery(qb)
				.execute()
				.actionGet()
				.toString();
	}
}
