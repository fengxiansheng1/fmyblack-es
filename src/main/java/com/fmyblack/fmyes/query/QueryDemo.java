package com.fmyblack.fmyes.query;

import org.elasticsearch.client.Client;

import com.fmyblack.fmyes.client.ClientProxy;

public class QueryDemo {

	public static void main(String[] args) {
		Client client = ClientProxy.getClient();
		System.out.println(client
				.prepareSearch("")
				.setTypes("")
				.addFields("f1","f2")
				.toString());
	}
}
