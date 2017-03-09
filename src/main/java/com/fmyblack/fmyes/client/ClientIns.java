package com.fmyblack.fmyes.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.fmyblack.util.config.ConfigHelper;

public enum ClientIns {

	CLIENT {
	};

	protected TransportClient client = null;

	private ClientIns() {
		String cluster_name = ConfigHelper.getConf("es", "cluster.name");
		String transports = ConfigHelper.getConf("es", "transport");

		Settings settings = Settings.settingsBuilder().put("cluster.name", cluster_name).build();
		client = TransportClient.builder().settings(settings).build();
		try {
			for (String transport : transports.split(";")) {
				String[] cols = transport.split(":");
				String host = cols[0];
				int port = Integer.parseInt(cols[1]);
				client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("your " + getClusterStat().getClusterName().toString() + " exists");
	}

	public Client getClient() {
		return client;
	}

	public ClusterStateResponse getClusterStat() {
		return client.admin().cluster().prepareState().execute().actionGet();
	}

	public boolean checkClient() {
		ClusterStateResponse resp = getClusterStat();
		if (resp.getClusterName() == null) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		QueryBuilder qb = QueryBuilders.queryStringQuery("body:\"北京\"");
		CLIENT.getClient();
		SearchResponse sr = CLIENT.client.prepareSearch("caiyun").setTypes("spider_result").setQuery(qb)
				// .setHighlighterQuery(qb)
				.addHighlightedField("body")
				// .addHighlightedField("title")
				.execute().actionGet();
		System.out.println(sr.toString());
	}
}
