package com.fmyblack.fmyes.explain;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse.AnalyzeToken;
import org.elasticsearch.client.Client;

import com.fmyblack.fmyes.client.ClientProxy;

public class AnalyzerExplain {

	public static void analyzer(String index, String text, String analyzer) {
		Client client = ClientProxy.getClient();

		AnalyzeResponse resp = client.admin().indices()
				.prepareAnalyze(index, text).setAnalyzer(analyzer).get();
		
		System.out.println("\nanalyzer [" + analyzer + "] work in text [" + text + "]");
		for(AnalyzeToken token : resp.getTokens()) {
			System.out.println(token.getTerm());
		}
	}

	public static void main(String[] args) {

		analyzer("jz_query", "http://www.huanqiu.com/abc/def?q=req&r=resp", "standard");
		
		analyzer("jz_query", "UAR-d;dfDC", "semispace");
		
		analyzer("jz_query", "奔腾如虎风烟举", "standard");
		
		analyzer("jz_query", "奔腾如虎风烟举", "ik_max_word");
		
		analyzer("jz_query", "奔腾如虎风烟举", "ik_smart");
	}
}
