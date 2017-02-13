package com.fmyblack.fmyes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.client.Client;

import com.fmyblack.fmyes.Indices.IndexIns;
import com.fmyblack.fmyes.client.ClientProxy;
import com.fmyblack.util.io.FileReaderUtil;

import net.sf.json.JSONObject;

public class EsServer extends EsAdapter{

	private static EsServer ins = null;
	
	private Client client;
	
	private EsServer(){
		client = ClientProxy.getClient();
	}
	
	public static synchronized EsServer getInstance() {
		if(ins == null) {
			ins = new EsServer();
		}
		return ins;
	}

	@Override
	public Client getClient() {
		// TODO Auto-generated method stub
		return client;
	}
	
	public static void main(String[] args) {
		EsServer es = EsServer.getInstance();
		es.createIndex("mytest");
//		es.createMapping("mytest", "type1");
	}
}
